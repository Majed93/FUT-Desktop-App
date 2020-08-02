package com.fut.desktop.app.futservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fut.desktop.app.constants.FutErrorCode;
import com.fut.desktop.app.constants.Pile;
import com.fut.desktop.app.constants.Resources;
import com.fut.desktop.app.domain.*;
import com.fut.desktop.app.dto.PileItemDto;
import com.fut.desktop.app.exceptions.FutErrorException;
import com.fut.desktop.app.extensions.DateTimeExtensions;
import com.fut.desktop.app.futservice.cache.CacheManager;
import com.fut.desktop.app.futservice.constants.WSUrl;
import com.fut.desktop.app.futservice.service.base.*;
import com.fut.desktop.app.restObjects.*;
import com.fut.desktop.app.services.base.PlayerService;
import com.fut.desktop.app.utils.EncryptUtil;
import com.fut.desktop.app.utils.PlayerUtil;
import com.fut.desktop.app.utils.SleepUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownHttpStatusCodeException;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of {@link TransferService}
 */
@Service
@Slf4j
public class TransferServiceImpl implements TransferService {

    /**
     * Fut service endpoint
     */
    private String futServiceEndpoint;

    @Value("${fut.service.endpoint}")
    public void setIo(String io) {
        this.futServiceEndpoint = EncryptUtil.url(io);
    }

    /**
     * Timeout factor
     */
    @Value("${fut.timeout}")
    private Integer timeoutFactor;

    /**
     * Handle to {@link StatusMessagingService}
     */
    private final StatusMessagingService messagingService;

    /**
     * Rest template
     */
    private final RestTemplate rest;

    /**
     * Handle to {@link AccountService}
     */
    private final AccountService accountService;

    /**
     * Handle to {@link PlayerService}
     */
    private final PlayerService playerService;

    /**
     * Handle to {@link PinService}
     */
    private final PinService pinService;

    /**
     * Handle to {@link ErrorHandlerService}
     */
    private final ErrorHandlerService errorHandlerService;

    /**
     * Stop listing switch
     */
    private boolean stop = false;

    /**
     * Pause switch
     */
    private boolean pause = false;

    /**
     * Cache manager.
     */
    private CacheManager cacheManager;

    /**
     * The object mapper
     */
    private final ObjectMapper objectMapper;

    // TODO: Move these to a const
    private final static String UNASSIGNED = "unassignedPile";
    private final static String TRADEPILE = "tradePile";
    private final static String WATCHLIST = "watchList";

    /**
     * Constructor.
     *
     * @param messagingService    Handle to {@link StatusMessagingService}
     * @param rest                Handle to {@link RestTemplate}
     * @param accountService      Handle to {@link AccountService}
     * @param playerService       Handle to {@link PlayerService}
     * @param pinService          Handle to {@link PinService}
     * @param errorHandlerService Handle to {@link ErrorHandlerService}
     * @param objectMapper        The object mapper.
     */
    public TransferServiceImpl(StatusMessagingService messagingService, RestTemplate rest,
                               AccountService accountService, PlayerService playerService,
                               PinService pinService, ErrorHandlerService errorHandlerService,
                               ObjectMapper objectMapper) {
        this.messagingService = messagingService;
        this.rest = rest;
        this.accountService = accountService;
        this.playerService = playerService;
        this.pinService = pinService;
        this.errorHandlerService = errorHandlerService;
        this.objectMapper = objectMapper;
        cacheManager = new CacheManager(5, TimeUnit.MINUTES);
    }


    @Override
    public AuctionResponse tradePile(Integer id) throws FutErrorException {
        startCache();
        return getAuctionResponse(TRADEPILE, id);
    }

    @Override
    public AuctionResponse watchList(Integer id) throws FutErrorException {
        startCache();
        return getAuctionResponse(WATCHLIST, id);
    }

    @Override
    public AuctionResponse unassignedPile(Integer id) throws FutErrorException {
        Account account = accountService.findOne(id);

        if (account.getUnassignedPileCount() < 1) {
            return new AuctionResponse();
        }

        startCache();
        AuctionResponse unassignedPile;

        try {
            ResponseEntity<AuctionResponse> resp = rest.getForEntity(URI.create(futServiceEndpoint + "/transfer/unassigned"), AuctionResponse.class);
            unassignedPile = resp.getBody();
        } catch (RestClientException ex) {
            checkForError(ex); // Throws exception from here as required.
            log.error("Error getting unassigned pile." + ex.getMessage());
            throw new FutErrorException(new FutError("Error getting unassigned pile", FutErrorCode.InternalServerError, "", "", ""));
        }

        account.setPinCount(this.pinService.getPinCount());
        account.setUnassignedPileCount(unassignedPile.getAuctionInfo() == null ? 0 : unassignedPile.getAuctionInfo().size());
        accountService.update(account);

        return unassignedPile;
    }

    @Override
    public AuctionResponse removeSold(Integer id) throws FutErrorException {
        return getAuctionResponse("removeSold", id);
    }

    @Override
    public AuctionResponse listItem(ListItemRequest request, Boolean first, String listType) throws Exception {
        boolean isUnassigned = Objects.equals(listType, UNASSIGNED);
        boolean isWatchList = Objects.equals(listType, WATCHLIST);
        AuctionResponse toReturnList = new AuctionResponse();

        if (isUnassigned || isWatchList) {
            Collection<PileItemDto> pileItems = new ArrayList<>();
            PileItemDto item = new PileItemDto();
            item.setId(request.getAuctionDetails().getItemDataId());
            item.setPile("trade");

            AuctionInfo auctionInfo = getAuctionInfoWithItemDataId(request.getAuctionResponse(),
                    request.getAuctionDetails().getItemDataId());

            // If it's not the unassigned pile we need to add the trade id.
            if (isUnassigned) {
                item.setTradeId(Resources.SEND_TO_TRADE_UNASSIGNED);
            } else {
                item.setTradeId(String.valueOf(Objects.requireNonNull(auctionInfo).getTradeId()));
            }
            pileItems.add(item);

            if (isWatchList) {
                if (!isAuctionWithinPriceRange(auctionInfo, request.getAuctionDetails())) {
                    messagingService.sendStatus("ERROR NOT LISTING.", WSUrl.PLAYERLISTING_STATUS);
                    messagingService.sendStatus("Prices out of range.", WSUrl.PLAYERLISTING_STATUS);
                    return request.getAuctionResponse();
                }
            }

            toReturnList = moveItemsToTrade(request.getId(), pileItems, request.getAuctionResponse(), isUnassigned, first, true);
            SleepUtil.sleep(100, 300);
            if (request.getId() != null) {
                Account account = accountService.findOne(request.getId());
                account.setTradePileCount(account.getTradePileCount() + 1);
                accountService.update(account);
            }
        }

        log.info("Listing item: " + request.getId() + " From pile: " + listType);
        AuctionResponse tradePile = listItemSingleItem(request.getAuctionDetails(), first, listType);

        // this'll be null if it's not the trade pile
        if (tradePile != null) {
            toReturnList = tradePile;
            if (request.getId() != null) {
                Account account = accountService.findOne(request.getId());

                saveExpiryTime(toReturnList, account);
            }
        }

        return toReturnList;
    }

    /**
     * Get the auction info from the auction response given the itemDataId
     *
     * @param resp       Auction response
     * @param itemDataId item id to look for.
     * @return Found auction info otherwise null.
     */
    private AuctionInfo getAuctionInfoWithItemDataId(AuctionResponse resp, Long itemDataId) {
        // Loop through each
        // If id equals the itemData id, return.

        return resp.getAuctionInfo().stream().
                filter(a -> a.getItemData().getId() == itemDataId).findFirst().orElse(null);
    }

    /**
     * Send a requess to list a single item.
     *
     * @param auctionDetails Auction details.
     * @param first          If first or not.
     * @return updated trade pile.
     */
    private AuctionResponse listItemSingleItem(AuctionDetails auctionDetails, Boolean first, String listType) {
        try {
            HttpEntity<AuctionDetails> entity = new HttpEntity<>(auctionDetails, new HttpHeaders());
            ResponseEntity<AuctionResponse> listItemResponseEntity = rest.exchange(futServiceEndpoint + "/transfer/listItem/" + first + "/" + listType, HttpMethod.POST, entity, AuctionResponse.class);
            return listItemResponseEntity.getBody();
        } catch (RestClientException ex) {
            checkForError(ex); // Throws exception from here as required.
            log.error("Error listing single item." + ex.getMessage());
            throw new FutErrorException(new FutError("Error listing single item", FutErrorCode.InternalServerError, "", "", ""));
        }
    }

    /**
     * Check the auction is within the price range.
     *
     * @return if the proposed listing item is within range.
     */
    private boolean isAuctionWithinPriceRange(AuctionInfo auctionInfo, AuctionDetails auctionDetails) {
        PriceRange priceRange = sendPriceRangeRequest(auctionInfo);

        if (priceRange == null) {
            priceRange = new PriceRange();
            priceRange.setMinPrice(auctionInfo.getItemData().getMarketDataMinPrice());
            priceRange.setMaxPrice(auctionInfo.getItemData().getMarketDataMaxPrice());
        }

        // If the starting price is less than the minimum listing price OR
        // If the BIN price is greater than the maximum listing price
        return auctionDetails.getStartingBid() >= priceRange.getMinPrice() &&
                auctionDetails.getBuyNowPrice() <= priceRange.getMaxPrice();
    }

    /**
     * Decide whether to get price range or not.
     *
     * @param auctionInfo Auction infos
     * @return cached or retrieved price range.
     */
    private PriceRange sendPriceRangeRequest(AuctionInfo auctionInfo) {
        // Will need a cached list to check against.
        long key = auctionInfo.getItemData().getResourceId();

        if (cacheManager.getStarted()) {
            PriceRange cachePriceRange = cacheManager.retrieveCache(key);

            if (cachePriceRange != null) {
                return cachePriceRange;
            }
        }
        try {
            ResponseEntity<PriceRange> priceRangeResponseEntity = rest.getForEntity(futServiceEndpoint + "/transfer/priceRange/" +
                    auctionInfo.getItemData().getId(), PriceRange.class);
            PriceRange priceRange = priceRangeResponseEntity.getBody();

            if (cacheManager.getStarted()) {
                cacheManager.addToCache(priceRange);
            }

            return priceRange;
        } catch (RestClientException ex) {
            checkForError(ex); // Throws exception from here as required.
            log.error("Error getting price range." + ex.getMessage());
            throw new FutErrorException(new FutError("Error getting price range.", FutErrorCode.InternalServerError, "", "", ""));
        }

    }

    @Override
    public AuctionResponse autoListUnlisted(RelistRequest request) throws Exception {
        AuctionDuration auctionDuration = request.getAuctionDuration();
        List<AuctionInfo> auctionInfo = request.getAuctionResponse().getAuctionInfo();
        boolean first = true;
        AuctionResponse tradePile = null;
        stop = false;
        pause = false;
        List<String> errorList = new ArrayList<>();
        if (auctionInfo == null) {
            messagingService.sendStatus("Trade pile empty.", WSUrl.PLAYERLISTING_STATUS);
            return null;
        }
        for (AuctionInfo info : auctionInfo) {
            pauseCheck();
            if (stop) {
                messagingService.sendStatus("Cancelling listing", WSUrl.PLAYERLISTING_STATUS);
                return tradePile == null ? request.getAuctionResponse() : tradePile;
            }

            if (info.getTradeState() == null) {
                // This might cause a null on a non player item this property doesn't exist.

                // Need to actually see if the item exists within our list.
                Player listingPlayer = findPlayerToList(info);

                // If the player's price has been set in our list then list it. otherwise leave it.
                if (listingPlayer != null) {
                    if (listingPlayer.getMinListPrice() == null || listingPlayer.getMaxListPrice() == null) {
                        messagingService.sendStatus("Price not set. Not listing.", WSUrl.PLAYERLISTING_STATUS);
                    } else {

                        AuctionDetails details = new AuctionDetails(info.getItemData().getId(),
                                auctionDuration, listingPlayer.getMinListPrice(),
                                listingPlayer.getMaxListPrice());
                        messagingService.sendStatus("Listing: " + PlayerUtil.getName(listingPlayer), WSUrl.PLAYERLISTING_STATUS);
                        messagingService.sendStatus("For: " + listingPlayer.getMinListPrice() + " / " + listingPlayer.getMaxListPrice(), WSUrl.PLAYERLISTING_STATUS);

                        // ************ Do item price range check here *******************
                        if (isAuctionWithinPriceRange(info, details)) {
                            tradePile = listItem(new ListItemRequest(null, details, tradePile), first, TRADEPILE);
                            messagingService.sendStatus("Done listing: " + PlayerUtil.getName(listingPlayer), WSUrl.PLAYERLISTING_STATUS);

                        } else {
                            messagingService.sendStatus("ERROR: " + PlayerUtil.getName(listingPlayer), WSUrl.PLAYERLISTING_STATUS);
                            messagingService.sendStatus("Prices out of range. Manually list this.", WSUrl.PLAYERLISTING_STATUS);
                            errorList.add("ERROR LISTING: " + PlayerUtil.getName(listingPlayer));
                        }
                        // ****************************************************************

                        if (tradePile != null) {
                            saveExpiryTime(tradePile, accountService.findOne(request.getId()));
                        }
                        first = false;
                        SleepUtil.sleep(856, 1754);
                    }
                }
            }
        }

        if (!errorList.isEmpty()) {
            messagingService.sendStatus("Items listed with errors", WSUrl.PLAYERLISTING_STATUS);
            errorList.forEach(error -> messagingService.sendStatus("Error listing: " + error, WSUrl.PLAYERLISTING_STATUS));
        }

        messagingService.sendStatus("Items listed", WSUrl.PLAYERLISTING_STATUS);

        return tradePile == null ? request.getAuctionResponse() : tradePile;
    }

    @Override
    public AuctionResponse reListAll(RelistRequest request, Boolean reListAll) throws Exception {
        Integer id = request.getId();
        Account account = accountService.findOne(id);
        AuctionDuration duration = request.getAuctionDuration();
        AuctionResponse auctionResponse = request.getAuctionResponse();
        Boolean newPrice = request.getNewPrice();
        Boolean firstItem = true;
        stop = false;
        pause = false;
        List<String> errorList = new ArrayList<>();

        if (reListAll) {
            // ReList all.
            AuctionResponse response = getAuctionResponse("reListAll", id);
            //TODO: Save session stats

            // Save the expiry time of the last item.
            saveExpiryTime(response, account);

            return response;

        } else {
            Set<Player> allListPlayers = new HashSet<>(playerService.collateAllPlayersFromList());
            List<Player> EAJSONList = playerService.findAllPlayers();
            AuctionResponse newTradePile = null;
            int itemsLeft = auctionResponse.getAuctionInfo().size();
            for (AuctionInfo item : auctionResponse.getAuctionInfo()) {
                // Need to check if trade state is null or not.

                if (item.getTradeState() != null) {
                    if (item.getTradeState().equals("expired") && item.getCurrentBid() == 0) {
                        // Loop through each player and find player. Can return null if not found.
                        Player player = allListPlayers.stream().filter(p -> p.getAssetId() == item.getItemData().getAssetId()).findFirst().orElse(null);

                        AuctionDetails auctionDetails;
                        Boolean outOfRange;
                        // Check if player
                        if (item.getItemData().getItemType().equals("player")) {
                            // If newprice and player not null and if the current items rating is greater to equal to the player rating.
                            if (newPrice && player != null && item.getItemData().getRating() >= player.getRating()) {
                                outOfRange = isOutOfRange(player.getMinListPrice(), player.getMaxListPrice(), item.getItemData());
                                auctionDetails = new AuctionDetails(item.getItemData().getId(), duration, player.getMinListPrice(), player.getMaxListPrice());
                            } else {
                                outOfRange = isOutOfRange(item.getStartingBid(), item.getBuyNowPrice(), item.getItemData());
                                auctionDetails = new AuctionDetails(item.getItemData().getId(), duration, item.getStartingBid(), item.getBuyNowPrice());
                            }
                        } else {
                            // Just list as normal?
                            outOfRange = isOutOfRange(item.getStartingBid(), item.getBuyNowPrice(), item.getItemData());
                            auctionDetails = new AuctionDetails(item.getItemData().getId(), duration, item.getStartingBid(), item.getBuyNowPrice());
                        }

                        // If it's not out of range then all good, otherwise throw exception.
                        if (!outOfRange) {
                            // send request to fut-io to list the given item.
                            // TODO: Save session.

                            String name = "Non-player item";
                            Player listingPlayer = null;

                            for (Player playa : EAJSONList) {
                                if (item.getItemData().getItemType().equals("player")) {
                                    if (playa.getAssetId().equals(item.getItemData().getAssetId())) {
                                        listingPlayer = playa;
                                    }
                                }
                            }

                            if (listingPlayer != null) {
                                name = PlayerUtil.getName(listingPlayer);
                            }

                            messagingService.sendStatus("Listing item: " + name, WSUrl.PLAYERLISTING_STATUS);
                            messagingService.sendStatus("For: " + auctionDetails.getStartingBid() + " / " + auctionDetails.getBuyNowPrice(), WSUrl.PLAYERLISTING_STATUS);

                            // ************ Do item price range check here *******************
                            if (isAuctionWithinPriceRange(item, auctionDetails)) {
                                newTradePile = listItem(new ListItemRequest(null, auctionDetails, newTradePile), firstItem, TRADEPILE);

                                itemsLeft--;
                                messagingService.sendStatus("Done listing item: " + name + ", "
                                        + itemsLeft + " items left", WSUrl.PLAYERLISTING_STATUS);
                            } else {
                                messagingService.sendStatus("ERROR: " + name, WSUrl.PLAYERLISTING_STATUS);
                                messagingService.sendStatus("Prices out of range. Manually list this.", WSUrl.PLAYERLISTING_STATUS);
                                errorList.add("ERROR LISTING: " + name);
                            }
                            // ****************************************************************

                            pauseCheck();
                            if (stop) {
                                // Save the expiry time of the last item.
                                if (newTradePile != null) {
                                    saveExpiryTime(newTradePile, account);
                                }

                                return newTradePile;
                            }

                            firstItem = false;
                        } else {
                            // throw an error saying it's out of range.
                            messagingService.sendStatus("ERROR: " + (player == null ? "item" : PlayerUtil.getName(player)), WSUrl.PLAYERLISTING_STATUS);
                            messagingService.sendStatus("Prices out of range. Manually list this.", WSUrl.PLAYERLISTING_STATUS);
                            errorList.add("ERROR LISTING: " + (player == null ? "item" : PlayerUtil.getName(player)));
                        }

                        // Wait before moving on
                        SleepUtil.sleep(754, 1845);
                    }
                }
            }

            // Save the expiry time of the last item.
            if (newTradePile != null) {
                saveExpiryTime(newTradePile, account);
            }

            // Check if any errors
            if (!errorList.isEmpty()) {
                messagingService.sendStatus("Finished listing items with errors.", WSUrl.PLAYERLISTING_STATUS);

                // Print each error.
                errorList.forEach(error -> messagingService.sendStatus(error, WSUrl.PLAYERLISTING_STATUS));

            } else {
                messagingService.sendStatus("Finished listing items.", WSUrl.PLAYERLISTING_STATUS);
            }
            return newTradePile == null ? request.getAuctionResponse() : newTradePile;
        }
    }

    @Override
    public AuctionResponse removeFromWatchList(WatchListRequest request, Long tradeId) throws Exception {
        //TODO: if outbidded or not check maybe? & sessions.
        AuctionResponse watchList = request.getAuctionResponse();
        Collection<Long> tradeIds = new HashSet<>();

        if (tradeId == 0 && watchList != null) {
            // Filter out the expired, and if request.remove = false then remove the outbidded.
            for (AuctionInfo info : watchList.getAuctionInfo()) {
                // If expired
                if (info.getTradeState().equals("closed") && info.getBidState().equals("outbid") ||
                        !info.getBidState().equals("highest") &&
                                (info.getTradeState().equals("closed") || info.getTradeState().equals("expired"))) {
                    tradeIds.add(info.getTradeId());
                }

                // Remove out bidded.
                if (!request.getRemoveExpired()) {
                    if (info.getTradeState().equals("active") && info.getBidState().equals("outbid")) {
                        tradeIds.add(info.getTradeId());
                    }
                }
            }

            if (tradeIds.size() > 0) {
                return removeItemsFromWatchList(request.getId(), tradeIds);
            } else {
                return watchList;
            }
        } else if (tradeId > 0 && watchList == null) {
            tradeIds = new HashSet<>(Collections.singletonList(tradeId));

            if (tradeIds.size() > 0) {
                return removeItemsFromWatchList(request.getId(), tradeIds);
            } else {
                return null; // Empty watch list
            }
        } else {
            throw new FutErrorException(new FutError("Remove expired: Invalid request", FutErrorCode.BadRequest,
                    "Auction response empty or tradeIds null", watchList + " | " + tradeId, ""));
        }

    }

    /**
     * Remove item(s) from watch list.
     *
     * @param id       Id of account
     * @param tradeIds Trade ids to remove.
     * @return Updated watch list.
     * @throws Exception Handle errors.
     */
    private AuctionResponse removeItemsFromWatchList(Integer id, Collection<Long> tradeIds) throws Exception {
        try {
            HttpEntity<Collection<Long>> entity = new HttpEntity<>(tradeIds, new HttpHeaders());
            ResponseEntity<AuctionResponse> removedItemResponseEntity = rest.exchange(futServiceEndpoint + "/transfer/removeFromWatchList", HttpMethod.POST, entity, AuctionResponse.class);
            AuctionResponse watchList = removedItemResponseEntity.getBody();

            Account account = accountService.findOne(id);
            account.setWatchListCount(watchList.getAuctionInfo() == null ? 0 : watchList.getAuctionInfo().size());
            account.setCoins(watchList.getCredits());
            accountService.update(account);

            return watchList;
        } catch (RestClientException ex) {
            checkForError(ex); // Throws exception from here as required.
            log.error("Error removing items from watch list." + ex.getMessage());
            throw new FutErrorException(new FutError("Error removing items from watch list", FutErrorCode.InternalServerError, "", "", ""));
        }
    }

    @Override
    public AuctionResponse moveToTrade(WatchListRequest request, Long tradeId) throws Exception {
        AuctionResponse itemList = request.getAuctionResponse();
        boolean isUnassigned = Objects.equals(request.getPile(), UNASSIGNED);

        // Throw error if empty watch list passed through.
        if (itemList == null) {
            throw new FutErrorException(new FutError("Move to trade: Invalid request", FutErrorCode.BadRequest,
                    "Auction response empty or tradeIds null", tradeId.toString(), ""));
        }

        // Collect all the item datas here.
        Collection<PileItemDto> pileItems = createPileItems(itemList, isUnassigned, tradeId);

        return moveItemsToTrade(request.getId(), pileItems, itemList, isUnassigned, null, false);
    }

    @Override
    public AuctionResponse moveAndList(RelistRequest request) throws Exception {
        // The current item list (watch list or unassigned)
        AuctionResponse itemList = request.getAuctionResponse();
        AuctionDuration auctionDuration = request.getAuctionDuration();
        List<String> errorList = new ArrayList<>();

        // Create a fake list to save an 'estimated' timestamp.
        AuctionResponse fakeResp = new AuctionResponse();
        Account account = accountService.findOne(request.getId());
        List<AuctionInfo> fakeInfo = new ArrayList<>();

        boolean isUnassigned = Objects.equals(request.getPile(), UNASSIGNED);
        boolean first = true;
        stop = false;
        pause = false;

        // Throw error if empty watch list passed through.
        if (itemList == null) {
            throw new FutErrorException(new FutError("Move to trade: Invalid request", FutErrorCode.BadRequest,
                    "Auction response empty or tradeIds null", request.toString(), ""));
        }

        // Collect all the item datas here.
        Collection<PileItemDto> pileItems = createPileItems(itemList, isUnassigned, 0L);

        // For each item
        for (PileItemDto pileItemDto : pileItems) {
            for (AuctionInfo auctionInfo : itemList.getAuctionInfo()) {
                pauseCheck();
                if (stop) {
                    messagingService.sendStatus("Cancelling listing", WSUrl.PLAYERLISTING_STATUS);
                    return itemList;
                }

                // If itemData.id match
                if (pileItemDto.getId() == auctionInfo.getItemData().getId()) {
                    // Need to actually see if the item exists within our list.
                    Player listingPlayer = findPlayerToList(auctionInfo);

                    if (listingPlayer != null) {
                        AuctionDetails details = new AuctionDetails(auctionInfo.getItemData().getId(),
                                auctionDuration, listingPlayer.getMinListPrice(),
                                listingPlayer.getMaxListPrice());

                        if (Objects.equals(request.getPile(), WATCHLIST)) {
                            if (!isAuctionWithinPriceRange(auctionInfo, details)) {
                                messagingService.sendStatus("ERROR NOT LISTING: " + PlayerUtil.getName(listingPlayer), WSUrl.PLAYERLISTING_STATUS);
                                messagingService.sendStatus("Prices out of range. Manually list this.", WSUrl.PLAYERLISTING_STATUS);
                                errorList.add("ERROR NOT LISTING: " + PlayerUtil.getName(listingPlayer));
                                break; // Do next item
                            }
                        }

                        // Move item
                        itemList = moveItemsToTrade(request.getId(), Collections.singletonList(pileItemDto), itemList, isUnassigned, first, true);

                        SleepUtil.sleep(100, 300);
                        // List the item
                        // This will be null because we're not getting the tradepile.
                        messagingService.sendStatus("Listing: " + PlayerUtil.getName(listingPlayer), WSUrl.PLAYERLISTING_STATUS);
                        messagingService.sendStatus("For: " + listingPlayer.getMinListPrice() + " / " + listingPlayer.getMaxListPrice(), WSUrl.PLAYERLISTING_STATUS);

                        listItemSingleItem(details, first, request.getPile());
                        messagingService.sendStatus("Done listing: " + PlayerUtil.getName(listingPlayer), WSUrl.PLAYERLISTING_STATUS);

                        first = false;
                        break;
                    } else {
                        messagingService.sendStatus("Item not on list. Asset ID: " + auctionInfo.getItemData().getAssetId(), WSUrl.PLAYERLISTING_STATUS);
                        messagingService.sendStatus("Not listing. Will remain in current list.", WSUrl.PLAYERLISTING_STATUS);
                    }
                }
            }
            SleepUtil.sleep(800, 1600);
        }

        if (itemList.getAuctionInfo() != null) {
            itemList.getAuctionInfo().forEach(aI -> {
                // Set the estimated time by adding a fake auction info.
                AuctionInfo a = new AuctionInfo();
                a.setExpires(auctionDuration.getDuration());
                fakeInfo.add(a);
            });
        }

        fakeResp.setAuctionInfo(fakeInfo);
        saveExpiryTime(fakeResp, account);

        if (!errorList.isEmpty()) {
            messagingService.sendStatus("Items listed with errors", WSUrl.PLAYERLISTING_STATUS);
            errorList.forEach(error -> messagingService.sendStatus("Error listing: " + error, WSUrl.PLAYERLISTING_STATUS));
        }

        messagingService.sendStatus("Done moving and listing", WSUrl.PLAYERLISTING_STATUS);
        SleepUtil.sleep(100, 300);

        // Set empty list if null to avoid issues in UI.
        if (itemList.getAuctionInfo() == null) {
            itemList.setAuctionInfo(new ArrayList<>());
        }

        return itemList;
    }


    /**
     * Check to see if the player is in our list
     *
     * @param info Auction info
     * @return Found player otherwise null.
     */
    private Player findPlayerToList(AuctionInfo info) {
        List<Player> players = new ArrayList<>(playerService.collateAllPlayersFromList());
        List<Player> listingPlayers = new ArrayList<>();
        for (Player playa : players) {
            if (info.getItemData().getItemType().equals("player")) {
                if (playa.getAssetId().equals(info.getItemData().getAssetId())) {
                    listingPlayers.add(playa);
                }
            }
        }

        Player listingPlayer;
        if (listingPlayers.isEmpty()) {
            return null;
        } else if (listingPlayers.size() > 1) {
            // More than one of the same type found so need to use the
            // one with the most profit.
            Player tempPlayer = listingPlayers.get(0);

            for (Player lP : listingPlayers) {
                double tempPMinProfit = (tempPlayer.getMinListPrice() * 0.95) - tempPlayer.getBidAmount();
                double lPMinProfit = (lP.getMinListPrice() * 0.95) - lP.getBidAmount();
                if (lPMinProfit > tempPMinProfit) {
                    tempPlayer = lP;
                }
            }
            listingPlayer = tempPlayer;
        } else {
            // Only one item found so use that
            listingPlayer = listingPlayers.get(0);
        }

        return listingPlayer;
    }

    /**
     * Create pile items list
     *
     * @param itemList     auctionResponse object.
     * @param isUnassigned True if unassigned pile otherwise pile
     * @param tradeId      0 if more than one otherwise specific id.
     * @return List of PileItemDtos
     */
    private Collection<PileItemDto> createPileItems(AuctionResponse itemList, Boolean isUnassigned, Long tradeId) {
        Collection<PileItemDto> pileItems = new HashSet<>();

        if (itemList != null && itemList.getAuctionInfo() != null) {
            // Loop through each item to find the correct ones.
            for (AuctionInfo info : itemList.getAuctionInfo()) {
                // Ensure it's tradable
                if (!info.getItemData().getUntradeable()) {
                    if (isUnassigned) {
                        if (tradeId == 0) {
                            pileItems.add(createPileItem(info, "trade", true));
                        } else if (tradeId > 0 && info.getItemData().getId() == tradeId) {
                            pileItems.add(createPileItem(info, "trade", true));
                            break;
                        }
                    } else {
                        if (tradeId == 0) {
                            // Conditions met which mean it's been won then add them to the list.
                            if (info.getTradeState().equals("closed") && !info.getBidState().equals("outbid") && !info.getItemData().getItemState().equals("invalid")
                                    && info.getBidState().equals("highest")) {
                                pileItems.add(createPileItem(info, "trade", false));
                            }
                        } else if (tradeId > 0 && info.getTradeId() == tradeId) {
                            // Find that one item
                            pileItems.add(createPileItem(info, "trade", false));
                            break;
                        }
                    }
                }
            }
        }

        return pileItems;
    }

    /**
     * Move items to trade pile
     *
     * @param id           Id of account.
     * @param pileItems    Items to move.
     * @param list         Current watch list.
     * @param isUnassigned true if is unassigned pile we move from otherwise false.
     * @param sendOptions  True if we need to send options. Otherwise false. NULL if to ignore.
     * @param listAndMove  True if we're listing and moving otherwise false - used for timeout.
     * @return Updated watch list.
     * @throws Exception Handle errors.
     */
    private AuctionResponse moveItemsToTrade(Integer id, Collection<PileItemDto> pileItems,
                                             AuctionResponse list, boolean isUnassigned,
                                             Boolean sendOptions, boolean listAndMove) throws Exception {
        // TODO: Save sessions
        Account account = accountService.findOne(id);
        boolean first = sendOptions == null ? true : sendOptions;
        SleepUtil.sleep(800, 1300);
        if (account.getTradePileCount() > account.getPile_tradePileSize() - 1) {
            messagingService.sendStatus("Trade pile full. ", WSUrl.PLAYERLISTING_STATUS);
            return list;
        }
        stop = false;
        pause = false;
        // Remove each item from watch list.
        for (PileItemDto pileItem : pileItems) {
            for (AuctionInfo info : list.getAuctionInfo()) {
                pauseCheck();
                if (stop) {
                    messagingService.sendStatus("Cancelling moving.", WSUrl.PLAYERLISTING_STATUS);
                    return list;
                }

                if (account.getTradePileCount() > account.getPile_tradePileSize() - 1) {
                    messagingService.sendStatus("Trade pile full. ", WSUrl.PLAYERLISTING_STATUS);
                    return list;
                }

                boolean foundItem = false;

                if (isUnassigned) {
                    if (info.getItemData().getId() == pileItem.getId()) {
                        foundItem = true;
                    }
                } else {
                    if (info.getTradeId() == Long.valueOf(pileItem.getTradeId())) {
                        foundItem = true;
                    }
                }

                // 231 is a redeemable item i think.
                if (foundItem && info.getItemData().getCardSubTypeId() != 231
                        && !info.getItemData().getUntradeable()) {
                    Player player = playerService.getPlayer(info.getItemData().getAssetId(), playerService.findAllPlayers());
                    String name = player == null ? "Non-player item" : PlayerUtil.getName(player);
                    messagingService.sendStatus("Moving item to trade " + name, WSUrl.PLAYERLISTING_STATUS);
                    ResponseEntity<SendToPile> moveToTradeResp;
                    try {
                        HttpEntity<PileItemDto> entity = new HttpEntity<>(pileItem, new HttpHeaders());
                        moveToTradeResp = rest.exchange(futServiceEndpoint + "/transfer/moveToTrade/" + first, HttpMethod.POST, entity, SendToPile.class);
                    } catch (RestClientException ex) {
                        checkForError(ex); // Throws exception from here as required.
                        log.error("Error moving item to trade pile." + ex.getMessage());
                        throw new FutErrorException(new FutError("Error moving item to trade pile.", FutErrorCode.InternalServerError, "", "", ""));
                    }
                    // Ensure it successfully moved.
                    if (!moveToTradeResp.getBody().itemData.get(0).getSuccess()) {
                        messagingService.sendStatus("ERROR moving item - " + name + ".", WSUrl.PLAYERLISTING_STATUS);
                        SleepUtil.sleep(100, 500);

                        throw new FutErrorException(new FutError("Unable to move item to trade.", FutErrorCode.InternalServerError, "Unable to move to trade",
                                "Unable to move to trade", ""));
                    }

                    messagingService.sendStatus("Moved " + name + ".", WSUrl.PLAYERLISTING_STATUS);
                    first = false;

                    // Remove item from watch list and update the trade pile count.
                    account.setTradePileCount(account.getTradePileCount() + 1);
                    boolean removed = list.getAuctionInfo().remove(info);
                    log.info("Item removed from list: " + removed);
                    if (!listAndMove) {
                        SleepUtil.sleep(800, 1300);
                    }
                    break;
                }
            }
        }

        if (isUnassigned && pileItems.size() > 1) {
            // Send pin to Home hub
            sendPinToHomeHub();

        }

        if (list.getAuctionInfo().isEmpty()) {
            list.setAuctionInfo(null);
        }

        messagingService.sendStatus("Done moving.", WSUrl.PLAYERLISTING_STATUS);
        if (isUnassigned) {
            account.setUnassignedPileCount(list.getAuctionInfo() == null ? 0 : list.getAuctionInfo().size());
        } else {
            account.setWatchListCount(list.getAuctionInfo() == null ? 0 : list.getAuctionInfo().size());
        }

        account.setPinCount(this.pinService.getPinCount());
        accountService.update(account);
        return list;
    }

    /**
     * Check if the exceptions that of a RestClientException and halt the processes.
     *
     * @param ex The exception to check.
     */
    private void checkForError(Exception ex) {
        if (ex instanceof RestClientException) {
            try {
                FutError futError;

                if (ex instanceof UnknownHttpStatusCodeException) {
                    futError = objectMapper.readValue(((UnknownHttpStatusCodeException) ex).getResponseBodyAsString(), FutError.class);
                } else {
                    futError = objectMapper.readValue(((HttpStatusCodeException) ex).getResponseBodyAsString(), FutError.class);
                }
                // Seems to always be this endpoint
                if (this.errorHandlerService.isMajorError(futError, WSUrl.PLAYERSEARCH_STATUS)) {
                    // Need to halt all operations - message already sent to user from above method as required.
                    this.setStop(true);
                    throw new FutErrorException(futError);
                }
            } catch (IOException e) {
                // Different error so we're not doing anything - just continuing.
                log.error("Can't convert error, actual instance is {}", ex.getClass());
            }
        }
    }

    @Override
    public Boolean stopAutoList() {
        stop = true;
        messagingService.sendStatus("Waiting for suitable place to stop", WSUrl.PLAYERLISTING_STATUS);
        log.info("Stopping listing.");
        return stop;
    }

    @Override
    public Boolean pauseRelisting() {
        pause = !pause;
        messagingService.sendStatus("Waiting for suitable place to pause", WSUrl.PLAYERLISTING_STATUS);
        log.info("Paused: " + pause);
        return pause;
    }

    @Override
    public void setStop(boolean stop) {
        this.stop = stop;
    }

    @Override
    public void setPause(boolean pause) {
        this.pause = pause;
    }

    @Override
    public PileItemDto createPileItem(AuctionInfo info, String pile, Boolean isUnassigned) {
        PileItemDto item = new PileItemDto();
        item.setId(info.getItemData().getId());
        item.setPile(pile);

        // If it's not the unassigned pile we need to add the trade id.
        if (isUnassigned) {
            item.setTradeId(Resources.SEND_TO_TRADE_UNASSIGNED);
        } else {
            item.setTradeId(String.valueOf(info.getTradeId()));
        }
        return item;
    }

    @Override
    public void startCache() {
        cacheManager.start();
    }

    @Override
    public void stopCache() throws Exception {
        cacheManager.stop();
    }

    @Override
    public void saveListInfo(Integer accountId, Pile pile, AuctionResponse list) throws FutErrorException {
        Account account = accountService.findOne(accountId);

        if (pile.equals(Pile.TradePile)) {
            account.setTradePileCount(list.getAuctionInfo() == null ? 0 : list.getAuctionInfo().size());
        } else if (pile.equals(Pile.WatchList)) {
            account.setWatchListCount(list.getAuctionInfo() == null ? 0 : list.getAuctionInfo().size());
        }

        account.setPinCount(this.pinService.getPinCount());
        account.setCoins(list.getCredits());

        if (pile.equals(Pile.TradePile)) {
            saveExpiryTime(list, account);
        } else {
            accountService.update(account);
        }
        // TODO: Sessions
    }

    /**
     * Use this method if the response will return an auction response.
     *
     * @param endpoint Endpoint to invoke on fut-io
     * @param id       Id of account.
     * @return Auction response
     * @throws FutErrorException handle errors.
     */
    private AuctionResponse getAuctionResponse(String endpoint, Integer id) throws FutErrorException {
        try {
            ResponseEntity<AuctionResponse> resp = rest.getForEntity(URI.create(futServiceEndpoint + "/transfer/" + endpoint), AuctionResponse.class);
            AuctionResponse auctionResponse = resp.getBody();

            if (endpoint.contains(TRADEPILE) || endpoint.contains("removeSold") || endpoint.contains("reListAll")) {
                saveListInfo(id, Pile.TradePile, auctionResponse);
            } else if (endpoint.contains(WATCHLIST)) {
                saveListInfo(id, Pile.WatchList, auctionResponse);
            }

            return auctionResponse;
        } catch (RestClientException ex) {
            checkForError(ex); // Throws exception from here as required.
            log.error("Error getting a pile." + ex.getMessage());
            throw new FutErrorException(new FutError("Error getting a pile", FutErrorCode.InternalServerError, "", "", ""));
        }
    }

    /**
     * Check if item is out of price range.
     *
     * @param min      Current min price.
     * @param max      Current max price.
     * @param itemData current itemData.
     * @return True = out of range. False = It's fine.
     */
    private boolean isOutOfRange(long min, long max, ItemData itemData) {
        return min < itemData.getMarketDataMinPrice() || max > itemData.getMarketDataMaxPrice();
    }

    /**
     * Save expiry time of last time in trade pile.
     *
     * @param response Trade pile
     * @param account  Account to save.
     * @throws FutErrorException Handle any errors.
     */
    private void saveExpiryTime(AuctionResponse response, Account account) throws FutErrorException {
        // Make a copy of the auction list.
        List<AuctionInfo> sortList = response.getAuctionInfo();
        sortList.sort(Comparator.comparingInt(AuctionInfo::getExpires));

        if (!sortList.isEmpty()) {
            int expires = (sortList.get(sortList.size() - 1).getExpires() * 1000);
            // need to add this to current time.
            long now = DateTimeExtensions.ToUnixTime();
            now = now + expires;

            // Check if 'now' is later then the current.
            if (account.getTimeFinish() != null) {
                if (now > Long.valueOf(account.getTimeFinish())) {
                    account.setTimeFinish(String.valueOf(now));
                }
            } else {
                account.setTimeFinish(String.valueOf(now));
            }
        }
        accountService.update(account);
        //TODO: Save sessions
    }

    /**
     * Check pause status.
     */
    private void pauseCheck() {
        if (pause) {
            messagingService.sendStatus("Paused now", WSUrl.PLAYERLISTING_STATUS);
            while (pause) {
                SleepUtil.sleep(1, 2);
            }
        }
    }

    /**
     * Send home hub pin.
     */
    private void sendPinToHomeHub() {
        ResponseEntity<String> homeHubPinResp = rest.getForEntity(futServiceEndpoint + "/homeHub", String.class);
        log.info("Home hub request {}", homeHubPinResp.getBody());
    }
}

package com.fut.desktop.app.futservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fut.desktop.app.constants.FutErrorCode;
import com.fut.desktop.app.domain.*;
import com.fut.desktop.app.dto.PileItemDto;
import com.fut.desktop.app.exceptions.FutErrorException;
import com.fut.desktop.app.futservice.constants.WSUrl;
import com.fut.desktop.app.futservice.service.base.*;
import com.fut.desktop.app.parameters.Level;
import com.fut.desktop.app.parameters.PlayerSearchParameters;
import com.fut.desktop.app.restObjects.*;
import com.fut.desktop.app.services.base.PlayerService;
import com.fut.desktop.app.utils.EncryptUtil;
import com.fut.desktop.app.utils.PlayerUtil;
import com.fut.desktop.app.utils.ProfitMarginUtil;
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
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of {@link TransferMarketService}
 */
@Service
@Slf4j
public class TransferMarketServiceImpl implements TransferMarketService {

    /**
     * Fut service endpoint
     */
    private String futServiceEndpoint;

    @Value("${fut.service.endpoint}")
    public void setIo(String io) {
        this.futServiceEndpoint = EncryptUtil.url(io);
    }

    /**
     * Used to make REST calls
     */
    private final RestTemplate rest;

    /**
     * Handle to {@link TransferService}
     */
    private final TransferService transferService;

    /**
     * Handle to {@link AccountService}
     */
    private final AccountService accountService;

    /**
     * Handle to  {@link PlayerService}
     */
    private final PlayerService playerService;

    /**
     * Handle to {@link StatusMessagingService}
     */
    private final StatusMessagingService messagingService;

    /**
     * Handle to {@link PinService}
     */
    private final PinService pinService;

    /**
     * Handle to {@link ErrorHandlerService}
     */
    private final ErrorHandlerService errorHandlerService;

    /**
     * Boolean switch to start/stop searching.
     */
    private boolean stop = false;

    /**
     * Boolean switch to pause searching.
     */
    private boolean pause = false;

    /**
     * Boolean switch to wait for user input
     */
    private boolean awaitUserInput = false;

    /**
     * Integer switch contain user options. 0 = no. 1 = yes. 99 = cancel.
     */
    private Integer userOption;

    /**
     * Integer with new price to use from user.
     */
    private Integer newPrice;
    /**
     * List name currently being used.
     */
    private String listName = "";

    /**
     * Modal waiter object
     */
    private final Object modalWaiter = new Object();

    /**
     * The object mapper
     */
    private final ObjectMapper objectMapper;

    /**
     * Constructor.
     *
     * @param rest                Handle to {@link RestTemplate}
     * @param transferService     Handle to {@link TransferService}
     * @param accountService      Handle to {@link AccountService}
     * @param playerService       Handle to {@link PlayerService}
     * @param messagingService    Handle to {@link StatusMessagingService}
     * @param pinService          Handle to {@link PinService}
     * @param errorHandlerService Handle to {@link ErrorHandlerService}
     * @param objectMapper        The object mapper.
     */
    public TransferMarketServiceImpl(RestTemplate rest, TransferService transferService, AccountService accountService,
                                     PlayerService playerService, StatusMessagingService messagingService,
                                     PinService pinService, ErrorHandlerService errorHandlerService, ObjectMapper objectMapper) {
        this.rest = rest;
        this.transferService = transferService;
        this.accountService = accountService;
        this.playerService = playerService;
        this.messagingService = messagingService;
        this.pinService = pinService;
        this.errorHandlerService = errorHandlerService;
        this.objectMapper = objectMapper;
    }

    @Override
    public Boolean startAutoSearch(SearchBody request) throws FutErrorException {
        stop = false;
        pause = false;
        boolean firstSearch = true;
        for (String list : request.getList()) {
            listName = list.replaceAll(".json", "");
            List<Player> playerList = request.getPlayers();
            Level level = request.getLevel();
            messagingService.sendStatus("Starting list: " + listName, WSUrl.PLAYERSEARCH_STATUS);

            //Loop through each player
            for (Player player : playerList) {
                // Get price
                if (!player.isPriceSet()) {
                    messagingService.sendStatus("Starting player: " + PlayerUtil.getName(player), WSUrl.PLAYERSEARCH_STATUS);
                    GetPriceOfPlayer(player, level, firstSearch, accountService.findOne(request.getId()));


                    pauseCheck();
                    if (stop) {
                        messagingService.sendStatus("Cancelling searching", WSUrl.PLAYERSEARCH_STATUS);
                        return true;
                    }
                    messagingService.sendStatus("Long pause.", WSUrl.PLAYERSEARCH_STATUS);
                    SleepUtil.sleep(1980, 3123);
                    firstSearch = false;
                }
            }
        }
        messagingService.sendStatus("All prices retrieved", WSUrl.PLAYERSEARCH_STATUS);
        return true; // search complete
    }

    @Override
    public Boolean startAutoBid(AutoBidRequest request) throws FutErrorException {
        messagingService.sendStatus("Initializing bidding process please wait.", WSUrl.PLAYERSEARCH_STATUS);

        // NOTE: request.getPlayers will contain the player to start from.

        stop = false;
        pause = false;
        Integer id = request.getId();
        Account account = accountService.findOne(id);
        Player startingPlayer = null;
        boolean start = false; // True once selected player has been found.
        boolean firstSearch = true;
        if (request.getPlayers() != null) {
            if (request.getPlayers().size() > 0 || request.getPlayers().get(0) != null) {
                startingPlayer = request.getPlayers().get(0);
            }
        } else {
            start = true;
        }

        for (String list : request.getList()) {
            List<Player> players = playerService.getPlayerJsonList(list);
            UpdatePriceBody updatePriceBody = new UpdatePriceBody(list, null, ProfitMarginUtil.generateDefaultProfitMargins(), 300L);
            Level level = Level.Any;
            if (request.getLevel() != null) {
                level = request.getLevel();
            }
            messagingService.sendStatus("Starting bidding: " + listName, WSUrl.PLAYERSEARCH_STATUS);
            for (Player player : players) {

                if (!start && startingPlayer != null && player.getAssetId().equals(startingPlayer.getAssetId())) {
                    start = true;
                }

                if (!start) {
                    continue; // Continue to loop until player found.
                }

                messagingService.sendStatus("Starting at: " + PlayerUtil.getName(player), WSUrl.PLAYERSEARCH_STATUS);

                // First check if user has enough credits left.
                if ((accountService.findOne(id).getCoins() < AuctionInfo.CalculateNextBid(player.getBidAmount()))) {
                    messagingService.sendStatus("Not enough credits left. Ending bidding.", WSUrl.PLAYERSEARCH_STATUS);
                    return true;
                }

                // if watch list and trade pile full
                if (!accountService.isSpaceInWatchList(account.getId()) && !accountService.isSpaceInTradePile(account.getId())) {
                    messagingService.sendStatus("Not enough space in trade pile or watch list.", WSUrl.PLAYERSEARCH_STATUS);
                    return true;
                }

                //Check if enough space in watch list.
                if (accountService.isSpaceInWatchList(id)) {
                    // Search for the player.
                    // This will do the searching and bidding process.
                    AuctionResponse searchResponse = autoSearchPlayer(player, level, account, updatePriceBody, firstSearch, request.getBidCriteria());
                    firstSearch = false;
                    if (searchResponse == null) {
                        break;
                    } else {
                        // Save coins.
                        if (searchResponse.getAuctionInfo() != null && searchResponse.getCredits() != null) {
                            account.setCoins(searchResponse.getCredits());
                            account = accountService.update(account);
                        }
                    }
                } else {
                    messagingService.sendStatus("Watch list limit. Ending bidding.", WSUrl.PLAYERSEARCH_STATUS);
                    return true;
                }

                // Do pause & stop checks
                pauseCheck();
                if (stop) {
                    messagingService.sendStatus("Cancelling bidding", WSUrl.PLAYERSEARCH_STATUS);
                    return true;
                }
                messagingService.sendStatus("Long pause.", WSUrl.PLAYERSEARCH_STATUS);
                SleepUtil.sleep(1980, 3123);
            }
        }

        messagingService.sendStatus("All done bidding", WSUrl.PLAYERSEARCH_STATUS);
        return true; // all done.
    }

    @Override
    public AuctionResponse search(MarketBody request, Boolean first, Integer accountId) throws FutErrorException {
        // Create params and search
        // NOTE: Depending on item different parameters can be set.
        PlayerSearchParameters params = new PlayerSearchParameters();
        Account account = this.accountService.findOne(accountId);

        if (request.getLevel() != null) {
            params.setLevel(request.getLevel());
        }

        if (request.getMinBid() != null) {
            params.setMinBid(request.getMinBid());
        }

        if (request.getMaxBid() != null) {
            params.setMaxBid(request.getMaxBid());
        }

        if (request.getMinBuy() != null) {
            params.setMinBuy(request.getMinBuy());
        }

        if (request.getMaxBuy() != null) {
            params.setMaxBuy(request.getMaxBuy());
        }

        switch (request.getItemType()) {
            case "player":
                if (request.getAssetId() != null) {
                    params.setResourceId(request.getAssetId());
                }

                return searchPlayer(params, first, account);
            case "consumable":
                log.info("Bidding consumable");
                //TODO:
                //searchItem();
                return null;
            default:
                log.error("Invalid item type");
                return null;
        }
    }

    @Override
    public AuctionResponse bidItem(BidItemRequest request) throws FutErrorException {
        return placeBid(request.getAuctionInfo(), request.getAmount());
    }

    @Override
    public AuctionResponse binItem(BidItemRequest request) throws FutErrorException {
        return placeBid(request.getAuctionInfo(), request.getAuctionInfo().getBuyNowPrice());
    }

    @Override
    public List<Player> calculatePlayersPrice(UpdatePriceBody request) {
        List<Player> playerList = request.getPlayers();

        String listName = request.getList().replaceAll(".json", "");
        List<ProfitMargin> profitMargins = request.getProfitMargins();

        Long minBid = request.getMinBid();
        for (Player player : playerList) {
            // If it's not manually set.
            if (!player.isCustomPrice()) {

                ProfitMargin pm = profitMargins.stream().filter(p -> player.getLowestBin() < p.getKey()).findFirst().get();

                Long minProfit = pm.getMinProfit();
                Long maxProfit = pm.getMaxProfit();
                // Must ensure the  the minimum is LESS than the maximum AND both are significantly greater than the bid amount
                // Must calculate the tax
                Long lowestBin = player.getLowestBin();
                Long minList;
                Long maxList;
                Long bidAmount;
                Long searchPrice;

                // Need to calculate min & max profit taking into account tax.
                // Need to calculate the search and bid amount. - Search can be derived from bid.
                switch (pm.getPriceSetChoice()) {
                    case 0: // Less than lowest bin
                        maxList = lowestBin - (SleepUtil.random(2, 3) * 100);
                        minList = lowestBin - (SleepUtil.random(1, 3) * 100);

                        // Ensure that the max is greater than the min
                        while (maxList <= minList) {
                            minList -= (SleepUtil.random(1, 2) * AuctionInfo.factor(lowestBin));
                            minList = AuctionInfo.roundToNearest(minList);
                        }

                        if (minList < minBid) {
                            minList = minBid;
                        }
                        bidAmount = minList - minProfit;
                        searchPrice = bidAmount + (SleepUtil.random(2, 4) * 100);

                        if (bidAmount < minBid) {
                            bidAmount = minBid;
                        }

                        if (searchPrice < minBid) {
                            searchPrice = minBid;
                        }

                        // Ensure that the max is greater than the min
                        while (maxList <= minList) {
                            minList -= (SleepUtil.random(1, 2) * AuctionInfo.factor(lowestBin));
                        }

                        //Need to also normalize the amounts so they conform to EAs amount.
                        maxList = AuctionInfo.roundToNearest(maxList);
                        minList = AuctionInfo.roundToNearest(minList);
                        bidAmount = AuctionInfo.roundToNearest(bidAmount);
                        searchPrice = AuctionInfo.roundToNearest(searchPrice);

                        if (maxList.equals(minList)) {
                            System.out.println("Error if we're getting here. Max & Min are same: " + PlayerUtil.getName(player));
                            minList = AuctionInfo.roundToNearest(AuctionInfo.CalculatePreviousBid(minList));
                        }
                        break;
                    case 2: // On or greater than lowest bin.
                        maxList = lowestBin - (SleepUtil.random(1, 2) * 100) + ((maxProfit - minProfit) / 2);
                        minList = lowestBin - (SleepUtil.random(1, 2) * 100) + ((maxProfit - minProfit) / 2);

                        // Ensure that the max is greater than the min
                        while (maxList <= minList) {
                            minList -= (SleepUtil.random(1, 2) * AuctionInfo.factor(lowestBin));
                        }
                        if (minList < minBid) {
                            minList = minBid;
                        }
                        bidAmount = minList - minProfit;
                        searchPrice = bidAmount + (SleepUtil.random(2, 4) * 100);

                        if (bidAmount < minBid) {
                            bidAmount = minBid;
                        }

                        if (searchPrice < minBid) {
                            searchPrice = minBid;
                        }

                        //Need to also normalize the amounts so they conform to EAs amount.
                        maxList = AuctionInfo.roundToNearest(maxList);
                        minList = AuctionInfo.roundToNearest(minList);
                        bidAmount = AuctionInfo.roundToNearest(bidAmount);
                        searchPrice = AuctionInfo.roundToNearest(searchPrice);

                        if (maxList.equals(minList)) {
                            System.out.println("Error if we're getting here. Max & Min are same: " + PlayerUtil.getName(player));
                            minList = AuctionInfo.roundToNearest(AuctionInfo.CalculatePreviousBid(minList));
                        }
                        break;
                    default: // 1 - Between lowest bin.
                        maxList = lowestBin + (SleepUtil.random(1, 2) * 100);
                        minList = lowestBin - (SleepUtil.random(1, 2) * 100);

                        // Ensure that the max is greater than the min
                        while (maxList <= minList) {
                            minList -= (SleepUtil.random(1, 3) * 100);
                            minList = AuctionInfo.roundToNearest(minList);
                        }

                        if (minList < minBid) {
                            minList = minBid;
                        }

                        bidAmount = minList - minProfit;
                        searchPrice = bidAmount + (SleepUtil.random(2, 4) * 100);

                        if (bidAmount < minBid) {
                            bidAmount = minBid;
                        }

                        if (searchPrice < minBid) {
                            searchPrice = minBid;
                        }

                        //Need to also normalize the amounts so they conform to EAs amount.
                        maxList = AuctionInfo.roundToNearest(maxList);
                        minList = AuctionInfo.roundToNearest(minList);
                        bidAmount = AuctionInfo.roundToNearest(bidAmount);
                        searchPrice = AuctionInfo.roundToNearest(searchPrice);

                        if (maxList.equals(minList)) {
                            System.out.println("Error if we're getting here. Max & Min are same: " + PlayerUtil.getName(player));
                            minList = AuctionInfo.roundToNearest(AuctionInfo.CalculatePreviousBid(minList));
                        }
                        break;
                }

                player.setMinListPrice(minList);
                player.setMaxListPrice(maxList);
                player.setBidAmount(bidAmount);
                player.setSearchPrice(searchPrice);

                playerList = playerService.updatePlayer(listName, player);
            }
        }

        messagingService.sendStatus("Done updating prices.", WSUrl.PLAYERSEARCH_STATUS);

        log.info("Done updating prices");
        return playerList;
    }

    @Override
    public Boolean stopSearchingOrBidding() {
        stop = true;
        SleepUtil.sleep(100, 200);
        messagingService.sendStatus("Stopping.. ", WSUrl.PLAYERSEARCH_STATUS);
        log.info("Stopping.");
        return stop;
    }

    @Override
    public Boolean pauseSearchingOrBidding() {
        pause = !pause;
        SleepUtil.sleep(100, 200);
        messagingService.sendStatus("Paused: " + pause, WSUrl.PLAYERSEARCH_STATUS);
        log.info("Paused: " + pause);
        return pause;
    }

    @Override
    public Boolean setWaitForUserInput() {
        awaitUserInput = true;
        SleepUtil.sleep(200, 300);
        messagingService.sendStatus("Waiting for user input..", WSUrl.PLAYERSEARCH_STATUS);
        log.info("Waiting for user input: " + awaitUserInput);
        return awaitUserInput;
    }

    @Override
    public void setInputForNewPrice(Integer option, Integer newPrice) {
        userOption = option;
        this.newPrice = newPrice;
        synchronized (modalWaiter) {
            awaitUserInput = false;
            modalWaiter.notifyAll();
        }
    }

    /**
     * Wait for user input.
     */
    private void checkForResponse() {
        setWaitForUserInput();
        messagingService.promptModal();
        log.info("Waiting for response.");

        synchronized (modalWaiter) {
            while (awaitUserInput) {
                try {
                    modalWaiter.wait();
                } catch (InterruptedException e) {
                    log.error("Error waiting. Cancelling");
                    this.newPrice = 99;
                    awaitUserInput = false;
                    Thread.currentThread().interrupt();
                }
            }
        }

        log.info("Done waiting.");
    }

    /**
     * Search for given player. All checks to ensure valid response is returned
     * will occur here. Part of <b>Auto bid</b>. This method decides whether to proceed with bidding on not.
     * No actual bidding occurs here.
     *
     * @param player      Player to search for.
     * @param level       Level of item to search.
     * @param account     Account being used.
     * @param firstSearch If first search or not
     * @return Auction response of searched player.
     */
    private AuctionResponse autoSearchPlayer(Player player, Level level, Account account, UpdatePriceBody
            updatePriceBody, Boolean firstSearch, BidCriteria bidCriteria) throws FutErrorException {
        messagingService.sendStatus("Searching for player " + PlayerUtil.getName(player), WSUrl.PLAYERSEARCH_STATUS);

        // The bid criteria
        Boolean shouldBinOnly = bidCriteria.getBinOnly();
        Boolean shouldBinFirst = bidCriteria.getBinFirst();
        Integer maxResults = bidCriteria.getMaxResults();
        int minBuyNow = bidCriteria.getMinBuyNow();

        if (maxResults < 1) {
            // Set default to 9
            maxResults = 9;
        }

        int pageNo = 1;
        boolean searchAgain = true;
        PlayerSearchParameters params = createParams(player, level, pageNo, !shouldBinOnly, minBuyNow);
        AuctionResponse searchResponse = null;
        boolean firstPass = true;
        // While we need to search again. E.g for price changes or something.
        while (searchAgain) {
            // Send request to fut client to search.
            messagingService.sendStatus("Searching for player @ " + player.getSearchPrice(), WSUrl.PLAYERSEARCH_STATUS);
            params.setPage(pageNo);
            searchResponse = searchPlayer(params, firstSearch, account);
            SleepUtil.sleep(456, 650);

            if (shouldBinFirst) {
                searchResponse.setAuctionInfo(sortSearchResponseWithBinAtStart(searchResponse.getAuctionInfo(), AuctionInfo.CalculateNextBid(AuctionInfo.CalculateNextBid(player.getBidAmount()))));
            }

            // TODO: save session or move into the above method to handle that?
            // Send the response to the web socket
            messagingService.sendAuction(searchResponse, WSUrl.PLAYERSEARCH_START);

            // None found
            if (searchResponse.getAuctionInfo() == null || searchResponse.getAuctionInfo().isEmpty()) {
                messagingService.sendStatus("No players found.", WSUrl.PLAYERSEARCH_STATUS);
                searchAgain = false;
            } else { // Found items
                int itemCount = searchResponse.getAuctionInfo().size();
                boolean requiresSearch = false;
                if (itemCount > 0 && itemCount <= maxResults || !firstPass) {
                    searchAgain = false;

                    // Bid
                    messagingService.sendStatus("Found.", WSUrl.PLAYERSEARCH_STATUS);
                    messagingService.sendStatus("Processing Bids.", WSUrl.PLAYERSEARCH_STATUS);
                    requiresSearch = processBids(searchResponse, account, player, bidCriteria);
                }

                if (searchAgain && firstPass) {
                    // Too many items found.
                    if (itemCount > maxResults) {
                        messagingService.sendStatus("Too many found.", WSUrl.PLAYERSEARCH_STATUS);
                        // Send response back with modal to prompt user for input.

                        // Now wait for response.
                        checkForResponse();

                        switch (userOption) {
                            case 1: // yes
                                // Continue and bid
                                // Bid
                                messagingService.sendStatus("Found.", WSUrl.PLAYERSEARCH_STATUS);
                                messagingService.sendStatus("Processing Bids.", WSUrl.PLAYERSEARCH_STATUS);
                                requiresSearch = processBids(searchResponse, account, player, bidCriteria);

                                // check for pause/stop
                                pauseCheck();
                                if (stop) {
                                    messagingService.sendStatus("Out of searching", WSUrl.PLAYERSEARCH_STATUS);
                                    return null;
                                }
                                break;
                            case 2: // no
                                // Set the new price and search again?
                                player.setLowestBin(Long.valueOf(newPrice));
                                updatePriceBody.setPlayers(Collections.singletonList(player));

                                List<Player> updatedList = calculatePlayersPrice(updatePriceBody);

                                // Get the new player price.
                                for (Player uP : updatedList) {
                                    if (uP.getAssetId().equals(player.getAssetId())) {
                                        player = uP;
                                    }
                                }

                                messagingService.sendStatus("Updated bid amount: " + player.getBidAmount(), WSUrl.PLAYERSEARCH_STATUS);
                                // Send for update player.

                                params = createParams(player, level, pageNo, !shouldBinOnly, minBuyNow);
                                firstPass = false;
                                break;
                            case 99: // cancel
                                messagingService.sendStatus("Cancelling bidding.", WSUrl.PLAYERSEARCH_STATUS);
                                return null;
                            default:
                                return null;
                        }
                    }
                }

                if (requiresSearch) {
                    // Requires another page to be search. This will loop until stopped or otherwise.
                    pageNo++;
                    searchAgain = true;
                } else {
                    searchAgain = false; // don't need to search anymore.
                }

                // check for pause/stop
                pauseCheck();
                if (stop) {
                    messagingService.sendStatus("Out of searching", WSUrl.PLAYERSEARCH_STATUS);
                    return null;
                }
            }

            pauseCheck();
            if (stop) {
                messagingService.sendStatus("Out of searching.", WSUrl.PLAYERSEARCH_STATUS);
                return null;
            }

            SleepUtil.sleep(300, 600);
        }

        return searchResponse;
    }

    /**
     * Sort the search response and put the lowest bins and the start - rest should remain in time order so they can be bidded in order.
     *
     * @param auctionInfo The auction infos to sort.
     * @param lowestBin   The lowest bin to compare with.
     * @return The sort list.
     */
    private List<AuctionInfo> sortSearchResponseWithBinAtStart(List<AuctionInfo> auctionInfo, long lowestBin) {
        List<AuctionInfo> completeList = new ArrayList<>();
        List<AuctionInfo> lowestBinList = new ArrayList<>();
        List<AuctionInfo> allOtherItems = new ArrayList<>();

        // Loop through original list and check to see if the buy now price is less than the lowest bin and create the lists.
        for (AuctionInfo info : auctionInfo) {
            if (info.getBuyNowPrice() <= lowestBin) {
                lowestBinList.add(info);
            } else {
                allOtherItems.add(info);
            }
        }


        lowestBinList.sort(Comparator.comparingLong(AuctionInfo::getBuyNowPrice));

        completeList.addAll(lowestBinList);
        completeList.addAll(allOtherItems);

        return completeList;
    }

    /**
     * Process bids here. It is assumed everything passed in here will be bidded.
     * If more than one page is required to be processed, the next page will be searched here.
     * This method well check if any items have been sent to trade pile or not too.
     *
     * @param searchResponse Search response to process.
     * @param account        Account being used.
     * @param player         Player being used.
     * @param bidCriteria    The bid criteria.
     * @return True if need to bid again. False if all done
     */
    private boolean processBids(AuctionResponse searchResponse, Account account, Player player, BidCriteria bidCriteria) {
        // todo: might/should move this up so once the bidding is actually done.

        // The bid criteria
        Boolean shouldMassBid = bidCriteria.getBid();
        Boolean shouldBinOnly = bidCriteria.getBinOnly();
        Long timeLimit = bidCriteria.getTimeLimit();

        if (timeLimit < 1) {
            // Default to an hour
            timeLimit = (long) AuctionDuration.OneHour.getDuration();
        }

        boolean binned; // Switch if i player was not binned. (So normally bidded)
        boolean sentToTrade = false; // Switch if any number of items have been sent to trade pile.
        int searchCount = 0; // The number of items we've bidded on.
        int pageSize = getPageSizeFromClient(); // Default page size used by FUT.
        int unAssigned = 0; // Unassigned items so far.
        boolean unassignedItemsLeft = false;
        SleepUtil.sleep(300, 600);

        // Flag to check if sleep is required or not.
        boolean isSleepRequired;

        for (AuctionInfo item : searchResponse.getAuctionInfo()) {
            isSleepRequired = false;
            AuctionResponse auctionResponse = null;

            // Check if player is from came CLUB
            // TODO: pass in flag for this.
            log.debug("Player club {} expected club {}", player.getClub(), item.getItemData().getTeamId());

            // TODO: REMOVE WHEN NOT TESTING
            if (true || player.getClub() == item.getItemData().getTeamId()) {
                // if watch list and trade pile full
                if (!accountService.isSpaceInWatchList(account.getId()) && !accountService.isSpaceInTradePile(account.getId())) {
                    messagingService.sendStatus("Not enough space in trade pile or watch list.", WSUrl.PLAYERSEARCH_STATUS);
                    return false;
                }

                // NOTE: ******************************************
                // Rating MUST be equal or higher.
                // NOTE: ******************************************
                if (item.getItemData().getRating() < player.getRating()) {
                    messagingService.sendStatus("Player rating below required.", WSUrl.PLAYERSEARCH_STATUS);
                    continue; // Go to next player
                }

                if (searchCount >= pageSize) {
                    // Go to next page?
                    // Looks like the response will return one more than the page size. e.g if page size
                    // 25 then returns 26. So if we exceed that count we need to go onto next page.
                    return true;
                }

                // Do bid stuff.
                // Check for credits & Watch list.
                if (account.getCoins() > AuctionInfo.CalculateNextBid(player.getBidAmount())) {
                    binned = false;
                    // Process bid.

                    // Try bin it no matter what state we are in. If we try to check
                    // shouldBinOnly == true, then it won't bin it under any other circumstance.
                    if (item.getBuyNowPrice() < AuctionInfo.CalculateNextBid(AuctionInfo.CalculateNextBid(player.getBidAmount()))) {
                        // Try bin
                        BiddingObject result = tryBin(item, player, account, unAssigned, bidCriteria);
                        binned = result.isBinned();
                        sentToTrade = result.isSendToTrade();
                        unAssigned = result.getUnAssigned();

                        auctionResponse = result.getResponse();
                        unassignedItemsLeft = result.isUnassignedItemsLeft();

                        isSleepRequired = true;
                    } else if (shouldMassBid && !shouldBinOnly && preBidOnlyChecks(item, player, account, timeLimit)) {
                        // BID now.
                        isSleepRequired = true;
                        messagingService.sendStatus("Trying to BID on item.", WSUrl.PLAYERSEARCH_STATUS);

                        if (item.getCurrentBid() >= player.getBidAmount()) {
                            // Get the max bid we should do.
                            int maxBid = (int) AuctionInfo.CalculateNextBid(AuctionInfo.CalculateNextBid(player.getBidAmount()));

                            // Check that the current bid is still less than the maxBid.
                            if (item.getCurrentBid() < maxBid) {
                                // Bid item now.
                                try {
                                    auctionResponse = placeBid(item, 0L);
                                    // todo sessions.
                                    messagingService.sendStatus("Bidding " + auctionResponse.getAuctionInfo().get(0).getCurrentBid(), WSUrl.PLAYERSEARCH_STATUS);
                                } catch (FutErrorException ex) {
                                    log.error("Error bidding in auto bid." + ex.getMessage());
                                    messagingService.sendStatus("Error bidding", WSUrl.PLAYERSEARCH_STATUS);
                                }
                            } else {
                                messagingService.sendStatus("Not bidding, too much!", WSUrl.PLAYERSEARCH_STATUS);
                            }
                        } else {
                            // If current bid is less than the bidding amount.
                            if (item.getStartingBid() <= player.getBidAmount() &&
                                    item.getCurrentBid() < player.getBidAmount()) {
                                try {
                                    auctionResponse = placeBid(item, player.getBidAmount());
                                    messagingService.sendStatus("Bidding " + auctionResponse.getAuctionInfo().get(0).getCurrentBid(), WSUrl.PLAYERSEARCH_STATUS);
                                    // todo sessions.
                                } catch (FutErrorException ex) {
                                    log.error("Error bidding in auto bid." + ex.getMessage());
                                    messagingService.sendStatus("Error bidding", WSUrl.PLAYERSEARCH_STATUS);
                                }
                            }
                        }
                    }
                } else {
                    messagingService.sendStatus("Not enough credit left.", WSUrl.PLAYERSEARCH_STATUS);
                    return false;
                }

                // Unassigned items left so go to unassigned pile and move them to trade.
                if (unassignedItemsLeft) {
                    messagingService.sendStatus("Unassigned items.", WSUrl.PLAYERSEARCH_STATUS);

                    //TODO: get the unassigned pile from fut-io
                    // foreach send to trade pile.
                    // auto list.
                }

                // Updated search view and send it back to web socket.
                if (auctionResponse != null) {
                    // First one because this response only ever contains one from EA
                    AuctionInfo newItem = auctionResponse.getAuctionInfo().get(0);

                    int index = -1;

                    for (int i = 0; i < searchResponse.getAuctionInfo().size(); i++) {
                        AuctionInfo currentItem = searchResponse.getAuctionInfo().get(i);
                        if (currentItem.getTradeId() == newItem.getTradeId()) {
                            index = searchResponse.getAuctionInfo().indexOf(currentItem);
                        }
                    }

                    if (index != -1) {
                        searchResponse.getAuctionInfo().set(index, newItem);
                    }

                    // Set updated coins
                    account.setCoins(auctionResponse.getCredits());
                    searchResponse.setCredits(auctionResponse.getCredits());
                    try {
                        accountService.update(account);
                    } catch (Exception ex) {
                        messagingService.sendStatus("Error saving account.", WSUrl.PLAYERSEARCH_STATUS);
                        log.error("Error saving account. " + ex.getMessage());
                    }

                    messagingService.sendAuction(searchResponse, WSUrl.PLAYERSEARCH_START);
                }

                // Update list count
                if (auctionResponse != null && !binned && accountService.isSpaceInWatchList(account.getId())) {
                    account.setWatchListCount(account.getWatchListCount() + 1);
                    account.setUnassignedPileCount(unAssigned);
                    try {
                        accountService.update(account);
                    } catch (Exception ex) {
                        messagingService.sendStatus("Error saving account.", WSUrl.PLAYERSEARCH_STATUS);
                        log.error("Error saving account. " + ex.getMessage());
                    }
                }
            }

            // Do pause & stop checks
            pauseCheck();
            if (stop) {
                messagingService.sendStatus("Cancelling bidding", WSUrl.PLAYERSEARCH_STATUS);
                return true;
            }

            searchCount++;
            if (isSleepRequired) {
                SleepUtil.sleep(745, 1542);
            }
        }


        if (sentToTrade) {
            // TODO: NEED TO AUTO LIST, maybe have option to do it or not? true/false
            // TODO: will need to pass in stuff from frontend to match relistRequest object.

            // Get the trade pile.
            try {
                // AuctionResponse tradePile = transferService.tradePile(account.getId());

                // RelistRequest request = new RelistRequest(account.getId(), );

                ///  transferService.autoListUnlisted(request);
            } catch (Exception e) {
                log.error("Error getting trade pile and listing. " + e.getMessage());
                messagingService.sendStatus("Error getting trade pile and listing.", WSUrl.PLAYERSEARCH_STATUS);
            }

            // note: this isn't implemented yet.
            //            messagingService.sendStatus("Listing bought items", WSUrl.PLAYERSEARCH_STATUS);
            //            log.info("Listing bought items.");
        }

        return false;
    }

    /**
     * Try BIN the item.
     * This will move the BINNED item to trade pile, <b>NOT</b> instantly listing.
     *
     * @param item       Item to process.
     * @param player     Player to process.
     * @param account    Account to process.
     * @param unAssigned current number of unassigned.
     * @return biddingObject.
     */
    private BiddingObject tryBin(AuctionInfo item, Player player, Account account, int unAssigned, BidCriteria bidCriteria) {
        BiddingObject biddingObject = new BiddingObject(false, false, unAssigned, false, null);
        AuctionResponse auctionResponse = null;
        boolean binned = false;
        boolean sentToTrade = false;

        Boolean shouldMassBid = bidCriteria.getBid();

        Long timeLimit = bidCriteria.getTimeLimit();
        if (timeLimit < 1) {
            // Default to an hour
            timeLimit = (long) AuctionDuration.OneHour.getDuration();
        }

        // If true then there's items that didn't get moved.
        boolean unassignedItems = false;
        if (preBINOnlyChecks(item, account, unAssigned)) {
            // BIN now.
            messagingService.sendStatus("Trying to BIN item.", WSUrl.PLAYERSEARCH_STATUS);

            try {
                // Place a bid.
                auctionResponse = placeBid(item, item.getBuyNowPrice());
                // TODO: save session or move into the above method to handle that?
                AuctionInfo newItem = auctionResponse.getAuctionInfo().get(0); // Should only be one.
                unAssigned++;
                binned = true;

                // Little pause
                SleepUtil.sleep(800, 1200);
                // Send item to TRADE.
                SendToPile sendToPile = moveToTrade(transferService.createPileItem(newItem, "trade", false));

                // Ensure it successfully moved.
                if (!sendToPile.itemData.get(0).getSuccess()) {
                    messagingService.sendStatus("ERROR moving item.", WSUrl.PLAYERSEARCH_STATUS);
                    log.error("Unable to move to trade.");
                    unassignedItems = true;
                } else {
                    messagingService.sendStatus("Item moved.", WSUrl.PLAYERSEARCH_STATUS);
                    account.setTradePileCount(account.getTradePileCount() + 1);
                    accountService.update(account);
                    unAssigned--;
                    sentToTrade = true;
                }
            } catch (Exception ex) {
                messagingService.sendStatus("Error binning. Moving on.", WSUrl.PLAYERSEARCH_STATUS);
                log.error("Error binning: " + ex.getMessage());
            }
        } else {
            // If it should mass bid.
            if (shouldMassBid) {
                // Bid normally
                String msg = unAssigned > (account.getPile_unassigned() - 1) ? "Unassigned pile full." : "Trade pile full.";
                messagingService.sendStatus(msg, WSUrl.PLAYERSEARCH_STATUS);

                if (preBidOnlyChecks(item, player, account, timeLimit)) {
                    if (item.getCurrentBid() < AuctionInfo.CalculateNextBid(AuctionInfo.CalculateNextBid(player.getBidAmount()))) {
                        try {
                            messagingService.sendStatus("Trying to bid on item", WSUrl.PLAYERSEARCH_STATUS);
                            auctionResponse = placeBid(item, AuctionInfo.CalculatePreviousBid(item.getBuyNowPrice()));
                            // TODO: save session
                        } catch (Exception ex) {
                            messagingService.sendStatus("Error bidding. Moving on.", WSUrl.PLAYERSEARCH_STATUS);
                            log.error("Error bidding: " + ex.getMessage());
                            binned = false;
                        }
                    }
                } else {
                    messagingService.sendStatus("Not bidding.", WSUrl.PLAYERSEARCH_STATUS);
                }
            }
        }

        biddingObject.setBinned(binned);
        biddingObject.setSendToTrade(sentToTrade);
        biddingObject.setUnAssigned(unAssigned);
        biddingObject.setUnassignedItemsLeft(unassignedItems);
        biddingObject.setResponse(auctionResponse);

        return biddingObject;
    }

    /**
     * Move BINned item to trade pile.
     *
     * @param pileItem Item to list
     * @return Status of moving.
     */
    private SendToPile moveToTrade(PileItemDto pileItem) {
        try {
            HttpEntity<PileItemDto> entity = new HttpEntity<>(pileItem, new HttpHeaders());
            ResponseEntity<SendToPile> moveToTradeResp = rest.exchange(futServiceEndpoint + "/transfer/moveToTrade/" + true, HttpMethod.POST, entity, SendToPile.class);
            return moveToTradeResp.getBody();
        } catch (Exception ex) {
            checkForError(ex);
            log.error("Error moving to trade." + ex.getMessage());
            throw new FutErrorException(new FutError("Error moving to trade", FutErrorCode.InternalServerError, "", "", ""));
        }
    }

    /**
     * Perform pre checks before bidding.
     *
     * @param item      Item to check.
     * @param player    Player to check.
     * @param timeLimit Time limit - should be in seconds.
     * @return True if all good to bid otherwise false.
     */

    private boolean preBidOnlyChecks(AuctionInfo item, Player player, Account account, Long timeLimit) {
        if (!accountService.isSpaceInWatchList(account.getId())) {
            messagingService.sendStatus("Watch list full.", WSUrl.PLAYERSEARCH_STATUS);
            return false;
        }
        // Check if item is being watched.
        if (item.getWatched() != null && item.getWatched()) {
            messagingService.sendStatus("Item already watched.", WSUrl.PLAYERSEARCH_STATUS);
            return false;
        }

        // Check if item's current bid is 2 increments above the bid amount.
        if (item.getCurrentBid() > AuctionInfo.CalculateNextBid(AuctionInfo.CalculateNextBid(player.getBidAmount()))) {
            messagingService.sendStatus("Item current bid too much.", WSUrl.PLAYERSEARCH_STATUS);
            return false;
        }

        // Logic to check if the item is above the time limit
        long timeLeft = item.getExpires();
        Duration duration = Duration.ofSeconds(timeLeft);

        if (timeLeft > timeLimit) {
//        if (duration.toHours() > 0 || duration.toDays() > 0) {
            messagingService.sendStatus("Item finishes too late.", WSUrl.PLAYERSEARCH_STATUS);

            return false;
        }

        return true;
    }

    /**
     * Perform pre checks before BINning.
     *
     * @param item       Item to check.
     * @param account    Account to check.
     * @param unassigned Number of unassigned items currently.
     * @return True if all good otherwise false.
     */
    private boolean preBINOnlyChecks(AuctionInfo item, Account account, int unassigned) {

        // Trade pile full and unassigned pile full.
        if (!accountService.isSpaceInTradePile(account.getId())) {
            messagingService.sendStatus("Trade pile full so can't BIN", WSUrl.PLAYERSEARCH_STATUS);
            return false;
        }

        if (unassigned > (account.getPile_unassigned() - 1)) {
            messagingService.sendStatus("Unassigned pile full.", WSUrl.PLAYERSEARCH_STATUS);
            return false;
        }

        return item.getBuyNowPrice() <= account.getCoins();
    }

    /**
     * Get price of a player.
     *
     * @param player      Player to get price for.
     * @param level       Level to search.
     * @param firstSearch If first search or not.
     * @param account     The account.
     */
    private void GetPriceOfPlayer(Player player, Level level, Boolean firstSearch, Account account) throws FutErrorException {
        int searchCounter = 0;
        long startPrice = player.getLowestBin();
        boolean found = false;

        while (!found) {
            pauseCheck();
            PlayerSearchParameters params = createParams(player, level, 0, false, 0);
            Map<Long, String> lowestBins = new HashMap<>(); // Track the lowest bins which have been found.
            // Do search

            AuctionResponse searchResponse;

            try {
                searchResponse = searchPlayer(params, firstSearch, account);
            } catch (FutErrorException ex) {
                log.error("Error searching: " + ex.getMessage());
                messagingService.sendStatus("Searching now with.. " + startPrice, WSUrl.PLAYERSEARCH_STATUS);
                throw new FutErrorException(new FutError("Error searching:" + ex.getMessage(), FutErrorCode.InternalServerError,
                        "Error searching: " + ex.getMessage(), "", ""));
            }
            // AuctionResponse searchResponse = mockResponse();

            // Send auction response to web socket.
            messagingService.sendAuction(searchResponse, WSUrl.PLAYERSEARCH_START);
            // TODO: Save session

            // If got results
            if (searchResponse.getAuctionInfo() != null && searchResponse.getAuctionInfo().size() > 0) {
                int playersFound = searchResponse.getAuctionInfo().size();

                if (playersFound > 9) {
                    //Do again
                    // Need logic to detect 'price fix'

                    //Get each buy now price and add it to a list
                    List<Long> prices = searchResponse.getAuctionInfo().stream().map(AuctionInfo::getBuyNowPrice).collect(Collectors.toList());
                    Map<Long, Integer> occurrences = new HashMap<>();

                    for (Long price : prices) {
                        if (occurrences.containsKey(price)) {
                            int currentPrice = occurrences.get(price) + 1;
                            occurrences.put(price, currentPrice);
                        } else {
                            occurrences.put(price, 1);
                        }
                    }

                    //If condition met then it's been 'price fixed'
                    if (occurrences.size() < 3) {
                        found = true;
                        // Set the player price.
                        player = savePrice(searchResponse, player);

                    } else {
                        messagingService.sendStatus("Too many items.. searching again with lower price.", WSUrl.PLAYERSEARCH_STATUS);
                        lowestBins.put(startPrice, "highest");

                        int counter = SleepUtil.random(1, 3);

                        if (searchCounter > 3) {
                            counter = SleepUtil.random(4, 7);
                        }

                        for (int i = 0; i < counter; i++) {
                            startPrice = GetUniquePrice(startPrice, lowestBins, false);
                        }

                        messagingService.sendStatus("For player.. " + PlayerUtil.getName(player) + " @ " + startPrice, WSUrl.PLAYERSEARCH_STATUS);
                        startPrice = AuctionInfo.CalculatePreviousBid(startPrice);
                        if (startPrice < 1) {
                            startPrice = AuctionInfo.CalculateNextBid(startPrice);
                        }
                    }

                    player.setLowestBin(startPrice);
                } else if (playersFound < 3 && searchCounter < 3) {
                    // If not enough players found.
                    lowestBins.put(startPrice, "lowest");
                    searchCounter++;

                    // Send message back
                    messagingService.sendStatus("Not enough players found @ " + startPrice + " for player: " + PlayerUtil.getName(player), WSUrl.PLAYERSEARCH_STATUS);
                    int counter = SleepUtil.random(1, 4);

                    if (searchCounter > 3) {
                        counter = SleepUtil.random(5, 10);
                    }

                    for (int i = 0; i < counter; i++) {
                        startPrice = GetUniquePrice(startPrice, lowestBins, true);
                    }

                    messagingService.sendStatus("Searching now with.. " + startPrice, WSUrl.PLAYERSEARCH_STATUS);
                    // Set the player price.
                    if (startPrice < 1) {
                        startPrice = AuctionInfo.CalculateNextBid(startPrice);
                    }
                    player.setLowestBin(startPrice);
                } else {
                    // Found
                    found = true;
                    player = savePrice(searchResponse, player);
                    // Save the price now.
                }
            } else { //If none found.
                messagingService.sendStatus("No items for with max bin of " + startPrice, WSUrl.PLAYERSEARCH_STATUS);
                messagingService.sendStatus("For player.. " + PlayerUtil.getName(player), WSUrl.PLAYERSEARCH_STATUS);
                lowestBins.put(startPrice, "lowest");

                searchCounter++;

                int counter = SleepUtil.random(2, 4);
                // Do it random times
                if (searchCounter > 3) {
                    counter = SleepUtil.random(10, 20);
                }

                for (int i = 0; i < counter; i++) {
                    startPrice = GetUniquePrice(startPrice, lowestBins, true);
                }

                messagingService.sendStatus("Searching now with.. " + startPrice, WSUrl.PLAYERSEARCH_STATUS);
                // Set the player price.
                if (startPrice < 1) {
                    startPrice = AuctionInfo.CalculateNextBid(startPrice);
                }

                player.setLowestBin(startPrice);

                if (searchCounter > 8 && startPrice > 15000) {
                    // Not really found just breaking out of loop.
                    found = true;
                    messagingService.sendStatus("Exceeded search counter.. Moving on to next item.", WSUrl.PLAYERSEARCH_STATUS);

                    player.setLowestBin(startPrice);
                    player.setPriceSet(false);
                    playerService.updatePlayer(listName, player);
                }
            }

            pauseCheck();
            if (stop) {
                messagingService.sendStatus("Out of searching.", WSUrl.PLAYERSEARCH_STATUS);
                return;
            }

            SleepUtil.sleep(1124, 1547);
        }

        SleepUtil.sleep(1000, 1400);
    }

    /**
     * Perform search.
     *
     * @param params  Parameters to search with.
     * @param first   If first search or not.
     * @param account The account.
     * @return Search response.
     */
    private AuctionResponse searchPlayer(PlayerSearchParameters params, Boolean first, Account account) throws FutErrorException {
        try {
            HttpEntity<PlayerSearchParameters> entity = new HttpEntity<>(params, new HttpHeaders());
            ResponseEntity<AuctionResponse> searchResponseEntity = rest.exchange(futServiceEndpoint + "/transferMarket/search/" + first, HttpMethod.POST, entity, AuctionResponse.class);
            account.setPinCount(this.pinService.getPinCount());
            this.accountService.update(account);
            return searchResponseEntity.getBody();
        } catch (Exception ex) {
            checkForError(ex);
            log.error("Error searching." + ex.getMessage());
            throw new FutErrorException(new FutError("Error searching", FutErrorCode.InternalServerError, "", "", ""));
        }
    }

    /**
     * Place bid on item.
     *
     * @param info      Info of items to bid on.
     * @param bidAmount Bid amount.
     * @return Updated item. This will only contain one item however.
     * Should be injected into the current list.
     */
    private AuctionResponse placeBid(AuctionInfo info, Long bidAmount) throws FutErrorException {
        try {
            HttpEntity<AuctionInfo> entity = new HttpEntity<>(info, new HttpHeaders());
            ResponseEntity<AuctionResponse> searchResponseEntity = rest.postForEntity(futServiceEndpoint + "/transferMarket/bidItem/" + bidAmount, entity, AuctionResponse.class);
            return searchResponseEntity.getBody();
        } catch (Exception ex) {
            checkForError(ex);
            log.error("Error bidding." + ex.getMessage());
            throw new FutErrorException(new FutError("Error bidding", FutErrorCode.PermissionDenied, "", "", ""));
        }
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
                    this.stopSearchingOrBidding();
                    throw new FutErrorException(futError);
                }
            } catch (IOException e) {
                // Different error so we're not doing anything - just continuing.
                log.error("Can't convert error, actual instance is {}", ex.getClass());
            }
        }
    }

    /**
     * Get page size for fut-io
     *
     * @return Page size - number of items to be displayed on page. (35, 25) etc.
     */
    private int getPageSizeFromClient() {
        ResponseEntity<Integer> response = rest.getForEntity(futServiceEndpoint + "/transferMarket/pageSize", Integer.class);
        return response.getBody();
    }

    /**
     * Gets a price which hasn't been used before.
     *
     * @param startPrice Start price to query
     * @param lowestBins Map of all used bins
     * @param next       Check previous or next bid. Next = true mean next bid, next = false means previous bid
     * @return unique price which hasn't been used before.
     */
    private long GetUniquePrice(long startPrice, Map<Long, String> lowestBins, boolean next) {
        boolean containsKey = false;
        String startPriceType = "";

        for (Long key : lowestBins.keySet()) {
            if (key != startPrice) {
                continue;
            }
            startPriceType = lowestBins.get(key);
            containsKey = true;
            break;
        }

        if (containsKey) {
            if (startPriceType.equals("highest")) {
                messagingService.sendStatus("Need to find previous unique price", WSUrl.PLAYERSEARCH_STATUS);
                startPrice = GetUniquePrice(AuctionInfo.CalculatePreviousBid(startPrice), lowestBins, next);
            }
            if (startPriceType.equals("lowest")) {
                messagingService.sendStatus("Need to find next unique price", WSUrl.PLAYERSEARCH_STATUS);
                startPrice = GetUniquePrice(AuctionInfo.CalculateNextBid(startPrice), lowestBins, next);
            } else {
                // THIS DOES NOT WORK BECAUSE WE DON'T SET THE PRICE AGAIN or maybe this is the way it's meant to work?
                messagingService.sendStatus("Bid key has no value?", WSUrl.PLAYERSEARCH_STATUS);
            }
        }

        startPrice = next ? AuctionInfo.CalculateNextBid(startPrice) : AuctionInfo.CalculatePreviousBid(startPrice);

        return startPrice;
    }

    /**
     * Save price of player.
     *
     * @param searchResponse Search response
     * @param player         Player being searched.
     */
    private Player savePrice(AuctionResponse searchResponse, Player player) {
        long lowestBid = 0;
        long bin = searchResponse.getAuctionInfo()
                .stream()
                .min(Comparator.comparing(AuctionInfo::getBuyNowPrice)).get().getBuyNowPrice();
        int minCounter = 0;

        for (AuctionInfo a : searchResponse.getAuctionInfo()) {
            if (a.getBuyNowPrice() != bin) {
                lowestBid += a.getBuyNowPrice();
            } else {
                long diff = a.getBuyNowPrice() - bin;
                if (diff < 1000 && a.getBuyNowPrice() > 500) {
                    lowestBid += a.getBuyNowPrice();
                } else {
                    minCounter++;
                }
            }
        }

        if (searchResponse.getAuctionInfo().size() - minCounter < 1) {
            lowestBid = AuctionInfo.CalculateNextBid(lowestBid);
        } else {
            lowestBid = AuctionInfo.CalculateNextBid((lowestBid / (searchResponse.getAuctionInfo().size() - minCounter)));
        }

        lowestBid = AuctionInfo.roundToNearest(lowestBid);
        //Lowest BIN
        player.setLowestBin(lowestBid);
        player.setPriceSet(true);
        List<Player> updatedList = playerService.updatePlayer(listName, player);

        for (Player uP : updatedList) {
            if (Objects.equals(uP.getAssetId(), player.getAssetId())) {
                player = uP;
                break;
            }
        }

        messagingService.sendStatus("Got price for " + PlayerUtil.getName(player) + " @ " + lowestBid, WSUrl.PLAYERSEARCH_STATUS);

        return player;
    }

    /**
     * Construct player params
     *
     * @param player      Player to search
     * @param level       Level.
     * @param pageNo      If 0 then don't use this param. Otherwise page to search.
     * @param isBidAmount true = use bid amount, else set max buy.
     * @return Search Parameters.
     */
    private PlayerSearchParameters createParams(Player player, Level level, Integer pageNo, boolean isBidAmount, int minBuyNow) {
        PlayerSearchParameters params = new PlayerSearchParameters();
        params.setResourceId(player.getAssetId());
        if (isBidAmount) {
            params.setMaxBid(AuctionInfo.roundToNearest(player.getBidAmount()));
        } else {
            params.setMaxBuy(AuctionInfo.roundToNearest(player.getLowestBin()));
        }

        if (minBuyNow > 0) {
            params.setMinBuy(AuctionInfo.roundToNearest(minBuyNow));
        }

        if (!level.equals(Level.Any)) {
            params.setLevel(level);
        }

        if (pageNo > 0) {
            params.setPage(pageNo);
        }

        return params;
    }

    private void pauseCheck() {
        if (pause) {
            while (pause) {
                SleepUtil.sleep(1, 2);
            }
        }
    }
}

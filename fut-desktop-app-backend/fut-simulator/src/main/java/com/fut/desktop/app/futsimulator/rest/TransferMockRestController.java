package com.fut.desktop.app.futsimulator.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fut.desktop.app.constants.FutErrorCode;
import com.fut.desktop.app.domain.*;
import com.fut.desktop.app.dto.PileItemDto;
import com.fut.desktop.app.futsimulator.constants.RestConstants;
import com.fut.desktop.app.futsimulator.service.TransferSimService;
import com.fut.desktop.app.futsimulator.utils.GlobalPlayerUtil;
import com.fut.desktop.app.futsimulator.utils.HttpHeaderCheckerUtil;
import com.fut.desktop.app.futsimulator.utils.RandomStringUtilsExtension;
import com.fut.desktop.app.utils.SleepUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Handle transfer list & watch list operations
 */
@RequestMapping(RestConstants.MOCK_IO)
@RestController
@Slf4j
public class TransferMockRestController {

    private final TransferSimService transferSimService;

    private boolean listingTrainedToFail;

    @Autowired
    public TransferMockRestController(TransferSimService transferSimService) {
        this.transferSimService = transferSimService;
        this.listingTrainedToFail = false;
    }

    /**
     * Get trade pile
     *
     * @return Trade pile response.
     */
    @GetMapping(RestConstants.UTAS + "tradepile")
    @ResponseBody
    public AuctionResponse getTradePile(@RequestHeader HttpHeaders requestHeaders) {
        HttpHeaderCheckerUtil.checkHeaders(requestHeaders);
        HttpHeaderCheckerUtil.checkAuthHeaders(requestHeaders, false);

        return transferSimService.getTradePile();
    }

    /**
     * Get watch list.
     *
     * @return Watch list response.
     */
    @GetMapping(RestConstants.UTAS + "watchlist")
    @ResponseBody
    public AuctionResponse getWatchList(@RequestHeader HttpHeaders requestHeaders) {
        HttpHeaderCheckerUtil.checkHeaders(requestHeaders);
        HttpHeaderCheckerUtil.checkAuthHeaders(requestHeaders, false);

        return transferSimService.getWatchList();
    }

    /**
     * Get unassigned pile
     *
     * @return Unassigned pile response.
     */
    @GetMapping(RestConstants.UTAS + "purchased/items")
    @ResponseBody
    public PurchasedItemsResponse getUnassignedPile(@RequestHeader HttpHeaders requestHeaders) {
        HttpHeaderCheckerUtil.checkHeaders(requestHeaders);
        HttpHeaderCheckerUtil.checkAuthHeaders(requestHeaders, false);

        return transferSimService.getUnassignedPile();
    }

    /**
     * Re list all items.
     */
    @PutMapping(RestConstants.UTAS + "auctionhouse/relist")
    @ResponseBody
    public String reListAll(@RequestHeader HttpHeaders requestHeaders) throws JsonProcessingException {
        HttpHeaderCheckerUtil.checkHeaders(requestHeaders);
        HttpHeaderCheckerUtil.checkAuthHeaders(requestHeaders, false);

        ObjectMapper objectMapper = new ObjectMapper();

        RelistResponse relist = transferSimService.relist();
        return objectMapper.writeValueAsString(relist);
    }

    /**
     * Remove item(s) from watch list.
     *
     * @param tradeIds Ids to remove.
     * @return Updated watch list.
     */
    @DeleteMapping(RestConstants.UTAS + "watchlist{tradeId}")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public void removeItemFromWatchList(@RequestHeader HttpHeaders requestHeaders,
                                        @RequestParam("tradeId") Collection<Long> tradeIds) {
        HttpHeaderCheckerUtil.checkHeaders(requestHeaders);
        HttpHeaderCheckerUtil.checkAuthHeaders(requestHeaders, false);

        this.transferSimService.removeFromWatchList(tradeIds);
    }

    /**
     * Move item to trade pile.
     *
     * @param pileItem Item to move.
     * @return status of moving item.
     */
    @PutMapping(RestConstants.UTAS + "item")
    @ResponseBody
    public SendToPile moveToTrade(@RequestHeader HttpHeaders requestHeaders, @RequestBody PileItemDto pileItem) {
        HttpHeaderCheckerUtil.checkHeaders(requestHeaders);
        HttpHeaderCheckerUtil.checkAuthHeaders(requestHeaders, true);

        SendToPile sendToPile = new SendToPile();
        List<PileItem> pileItemList = new ArrayList<>();
        pileItemList.add(new PileItem(pileItem.getId(), "trade", true, 0, null));
        sendToPile.setItemData(pileItemList);


        return sendToPile;
    }

    /**
     * Price ranges
     */
    @GetMapping(RestConstants.UTAS + "marketdata/item/pricelimits{itemIdList}")
    public String priceRange(@RequestHeader HttpHeaders requestHeaders,
                             @RequestParam("itemIdList") String itemIdList) {
        HttpHeaderCheckerUtil.checkHeaders(requestHeaders);
        HttpHeaderCheckerUtil.checkAuthHeaders(requestHeaders, false);

        Assert.assertNotNull("Item id list is null", itemIdList);

        // TODO: if there are a list of items..? Don't think this ever happens though
        if (itemIdList.contains(",")) {
            // Loop through if we ever handle more than one item list.
            throw new UnsupportedOperationException("List of items not supported");
        }

        return "[{\"source\":\"ITEM_DEFINITION\",\"defId\":" +
                GlobalPlayerUtil.getPlayersList().
                        get(SleepUtil.random(0, GlobalPlayerUtil.getPlayersList().size())).getAssetId()
                + ",\"itemId\":" +
                itemIdList +
                ",\"minPrice\":" + transferSimService.getDefaultMin() +
                ",\"maxPrice\":" + transferSimService.getDefaultMax() + "}]";
    }

    /**
     * List an item
     */
    @PostMapping(RestConstants.UTAS + "auctionhouse")
    public String listItem(@RequestHeader HttpHeaders requestHeaders,
                           HttpServletResponse response,
                           @RequestBody Object listBody) {
        HttpHeaderCheckerUtil.checkHeaders(requestHeaders);
        HttpHeaderCheckerUtil.checkAuthHeaders(requestHeaders, false);

        if (listingTrainedToFail) {
            response.setStatus(FutErrorCode.CaptchaTriggered.getFutErrorCode());
            return null;
        }

        Object itemData = ((LinkedHashMap) listBody).get("itemData");

        String id = ((LinkedHashMap) itemData).get("id").toString();
        String startingBid = ((LinkedHashMap) listBody).get("startingBid").toString();
        String duration = ((LinkedHashMap) listBody).get("duration").toString();
        String buyNowPrice = ((LinkedHashMap) listBody).get("buyNowPrice").toString();

        Assert.assertNotNull("Id on list body is null", id);
        Assert.assertNotNull("startingBid on list body is null", startingBid);
        Assert.assertNotNull("duration on list body is null", duration);
        Assert.assertNotNull("buyNowPrice on list body is buyNowPrice", buyNowPrice);

        Integer startingPrice = Integer.parseInt(startingBid);
        Integer buyItNowPrice = Integer.parseInt(buyNowPrice);

        if (startingPrice >= buyItNowPrice) {
            log.error("Starting bid is greater than buy now price");
            response.setStatus(465);
            return null;
        }

        if (AuctionDuration.fromValue(Integer.parseInt(duration)) == null) {
            log.error("Incorrect listing time");
            response.setStatus(465);
            return null;
        }

        String randomId = RandomStringUtilsExtension.ensureDoesNotStartWithZero(RandomStringUtils.random(11, false, true));

        transferSimService.listItem(Long.parseLong(id), Long.parseLong(randomId), startingPrice, buyItNowPrice, Long.parseLong(duration));
        return "{\"id\":" + randomId + ",\"idStr\":\"" + randomId + "\"}";
    }

    /**
     * Get the trade status
     */
    @GetMapping(RestConstants.UTAS + "trade/status{tradeIds}{client}")
    public String tradeStatus(@RequestHeader HttpHeaders requestHeaders,
                              @RequestParam("tradeIds") String tradeIds,
                              @RequestParam("client") String client) throws JsonProcessingException {
        HttpHeaderCheckerUtil.checkHeaders(requestHeaders);
        HttpHeaderCheckerUtil.checkAuthHeaders(requestHeaders, false);

        Assert.assertNotNull("client is null", client);
        Assert.assertNotNull("tradeIds is null", tradeIds);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        AuctionResponse tradeStatus = transferSimService.generateTradeStatus(Long.parseLong(tradeIds));
        return mapper.writeValueAsString(tradeStatus);
    }

    /**
     * Remove sold items
     */
    @DeleteMapping(RestConstants.UTAS + "trade/sold")
    @ResponseStatus(HttpStatus.OK)
    public void removeSoldItems(@RequestHeader HttpHeaders requestHeaders) {
        HttpHeaderCheckerUtil.checkHeaders(requestHeaders);
        HttpHeaderCheckerUtil.checkAuthHeaders(requestHeaders, true);

        transferSimService.removeSold();
    }

    /******************************************************************************************************************
     *********************************************** Trainer REST Calls ***********************************************
     ******************************************************************************************************************
     */

    /**
     * Train simulator to return coins
     *
     * @param amount Amount of coins to add to train simulator with.
     */
    @GetMapping(RestConstants.TRAIN + RestConstants.COINS + "/{amount}")
    public void trainCoins(@PathVariable("amount") Long amount) {
        transferSimService.setCredits(amount);
        log.info("Coin total is now: {}", transferSimService.getTradePile().getCredits());
    }

    /**
     * Set the min price range
     *
     * @param amount amount to set
     */
    @GetMapping(RestConstants.TRAIN + RestConstants.MIN_PRICE_RANGE + "/{amount}")
    public void trainMinPriceRange(@PathVariable("amount") Integer amount) {
        transferSimService.setDefaultMin(amount);
    }

    @GetMapping(RestConstants.TRAIN + RestConstants.MAX_PRICE_RANGE + "/{amount}")
    public void trainMaxPriceRange(@PathVariable("amount") Integer amount) {
        transferSimService.setDefaultMax(amount);
    }

    /**
     * Train a pile to have X amount of items
     *
     * @param pile   Pile to set
     * @param number number of items
     */
    @GetMapping(RestConstants.TRAIN + RestConstants.PILE + "/{pile}/{number}")
    @ResponseStatus(HttpStatus.OK)
    public void trainPile(@PathVariable("pile") String pile,
                          @PathVariable("number") Integer number) {
        switch (pile) {
            case RestConstants.TRADEPILE:
                transferSimService.generateTradePilePileWithItems(number);
                break;
            case RestConstants.WATCHLIST:
                transferSimService.generateWatchListPileWithItems(number);
                break;
            case RestConstants.UNASSIGNEDPILE:
                transferSimService.generateUnassignedPileWithItems(number);
                break;
            default:
                throw new UnsupportedOperationException("Incorrect pile type. Check the request");
        }
    }

    /**
     * Train trade pile with X amount of items which are of given type.
     *
     * @param number number of items
     */
    @GetMapping(RestConstants.TRAIN + RestConstants.TRADEPILE + "/{type}/{number}/{listName}")
    @ResponseStatus(HttpStatus.OK)
    public void trainTradePileWithItems(@PathVariable("number") Integer number,
                                        @PathVariable("type") String type,
                                        @PathVariable("listName") String listName) {
        if (!listName.contains(".players")) {
            listName += ".players";
        }

        switch (type) {
            case RestConstants.UNLISTED:
                transferSimService.generateTradePileWithUnlistedItems(number, listName);
                break;
            case RestConstants.SOLD:
                transferSimService.generateTradePileWithSoldItems(number, listName);
                break;
            case RestConstants.EXPIRED:
                transferSimService.generateTradePileWithExpiredItems(number, listName);
                break;
            default:
                throw new UnsupportedOperationException(type + " not supported!");
        }
    }

    /**
     * Train watch list with X of amount of items which are of given type.
     */
    @GetMapping(RestConstants.TRAIN + RestConstants.WATCHLIST + "/{type}/{number}/{listName}")
    @ResponseStatus(HttpStatus.CREATED)
    public void trainWatchListWithItems(@PathVariable String type, @PathVariable Integer number, @PathVariable String listName) {
        switch (type) {
            case RestConstants.WON:
                transferSimService.generateWatchListWithWon(number, listName);
                break;
            case RestConstants.WINNING:
                transferSimService.generateWatchListWithWinning(number, listName);
                break;
            case RestConstants.OUTBIDDED:
                transferSimService.generateWatchListWithOutbiddedItems(number, listName);
                break;
            case RestConstants.EXPIRED:
                transferSimService.generateWatchListPileWithExpiredItems(number, listName);
                break;
            default:
                throw new UnsupportedOperationException(type + " not supported!");
        }
    }

    /**
     * Train the listing to fail or succeed.
     *
     * @param listingTrainedToFail True or false
     */
    @GetMapping(RestConstants.TRAIN + "/listing/trainToFail/{listingTrainedToFail}")
    public void setListingTrainToFail(@PathVariable Boolean listingTrainedToFail) {
        this.listingTrainedToFail = listingTrainedToFail;
        log.info("Train listing for fail is now {}", this.listingTrainedToFail);
    }
}

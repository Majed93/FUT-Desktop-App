package com.fut.api.fut.rest;

import com.fut.api.fut.client.IFutClient;
import com.fut.desktop.app.domain.*;
import com.fut.desktop.app.dto.PileItemDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * Handle transfer list & watch list operations
 */
@RequestMapping("/io/transfer")
@RestController
@Slf4j
public class TransferIORestController {

    private final IFutClient futClient;

    @Autowired
    public TransferIORestController(IFutClient futClient) {
        this.futClient = futClient;
    }

    /**
     * Get trade pile
     *
     * @return Trade pile response.
     * @throws Exception Handle errors.
     */
    @GetMapping(value = "/tradePile")
    @ResponseBody
    public AuctionResponse getTradePile() throws Exception {
        return futClient.getTradePile(true).get();
    }

    /**
     * Get watch list.
     *
     * @return Watch list response.
     * @throws Exception Handle errors.
     */
    @GetMapping(value = "/watchList")
    @ResponseBody
    public AuctionResponse getWatchList() throws Exception {
        return futClient.getWatchList(true).get();
    }

    /**
     * Get unassigned pile
     *
     * @return Unassigned pile response.
     * @throws Exception Handle errors.
     */
    @GetMapping(value = "/unassigned")
    @ResponseBody
    public AuctionResponse getUnassignedPile() throws Exception {
        return futClient.getUnassignedPile().get();
    }

    /**
     * Remove all sold items
     *
     * @return Updated trade pile.
     * @throws Exception Handle errors.
     */
    @GetMapping(value = "/removeSold")
    @ResponseBody
    public AuctionResponse getRemoveSold() throws Exception {
        futClient.removeSold().get();

        // Trade pile request is sent twice without any pin events.
        futClient.getTradePile(false).get();
        return futClient.getTradePile(false).get();
    }

    /**
     * Re list all items.
     *
     * @return Update trade pile.
     * @throws Exception Handle errors.
     */
    @GetMapping(value = "/reListAll")
    @ResponseBody
    public AuctionResponse getReListAll() throws Exception {
        futClient.getReListAll().get();

        //Trade pile request is sent without any pin events.
        return futClient.getTradePile(false).get();
    }

    /**
     * List single item.
     *
     * @param auctionDetails item details.
     * @param sendOptions    First item to be listed or not.
     * @param tradePile      If trade pile or not to see if extra requests need to be made.
     * @return Updated trade pile.
     * @throws Exception Handle errors.
     */
    @PostMapping(value = "/listItem/{sendOptions}/{tradePile}")
    @ResponseBody
    public AuctionResponse postListItem(@RequestBody AuctionDetails auctionDetails,
                                        @PathVariable("sendOptions") Boolean sendOptions,
                                        @PathVariable("tradePile") String tradePile) throws Exception {
        // List the item
        ListAuctionResponse listAuctionResponse = futClient.listItem(auctionDetails, sendOptions).get();

        // Get the trade status
        futClient.tradeStatus(Collections.singletonList(listAuctionResponse.getId())).get();

        if (Objects.equals(tradePile, "tradePile")) {
            // Get & return refreshed trade pile.
            return futClient.getTradePile(false).get();
        } else {
            return null;
        }
    }

    /**
     * Remove item(s) from watch list.
     *
     * @param tradeIds Ids to remove.
     * @return Updated watch list.
     * @throws Exception Handle any errors.
     */
    @PostMapping(value = "/removeFromWatchList")
    @ResponseBody
    public AuctionResponse postRemoveItemFromWatchList(@RequestBody Collection<Long> tradeIds) throws Exception {
        Boolean removed = futClient.removeFromWatchList(tradeIds).get();
        if (!removed) {
            log.error("Unable to remove item");
        }
        return futClient.getWatchList(false).get();
    }

    /**
     * Move item to trade pile
     *
     * @param pileItem    Item to move.
     * @param sendOptions First item to be listed or not.
     * @return status of moving item.
     * @throws Exception Handle errors.
     */
    @PostMapping(value = "/moveToTrade/{sendOptions}")
    @ResponseBody
    public SendToPile moveToTrade(@RequestBody PileItemDto pileItem, @PathVariable("sendOptions") Boolean sendOptions) throws Exception {
        return futClient.moveItemToPile(pileItem, "trade", sendOptions).get();
    }

    @GetMapping(value = "/priceRange/{itemId}")
    @ResponseBody
    public PriceRange priceRange(@PathVariable("itemId") Long itemId) throws Exception {
        return futClient.priceRange(itemId).get();
    }
}

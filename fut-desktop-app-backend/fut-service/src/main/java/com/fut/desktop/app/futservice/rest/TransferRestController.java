package com.fut.desktop.app.futservice.rest;

import com.fut.desktop.app.domain.AuctionResponse;
import com.fut.desktop.app.exceptions.FutErrorException;
import com.fut.desktop.app.futservice.service.base.TransferService;
import com.fut.desktop.app.restObjects.ListItemRequest;
import com.fut.desktop.app.restObjects.RelistRequest;
import com.fut.desktop.app.restObjects.WatchListRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Handle transfer list & watch list operations
 */
@RequestMapping("/transfer")
@RestController
@Slf4j
public class TransferRestController {

    /**
     * Handle to {@link TransferService}
     */
    private final TransferService transferService;

    /**
     * Constructor.
     *
     * @param transferService Handle to {@link TransferService}
     */
    @Autowired
    public TransferRestController(TransferService transferService) {
        this.transferService = transferService;
    }

    /**
     * Get trade pile
     *
     * @param id Id of account.
     * @return Trade pile response
     * @throws FutErrorException handle any exception thrown
     */
    @PostMapping("/tradePile")
    @ResponseBody
    public AuctionResponse tradePile(@RequestBody Integer id) throws FutErrorException {
        return transferService.tradePile(id);
    }

    /**
     * Get watch list.
     *
     * @param id Id of account
     * @return Watch list response
     * @throws FutErrorException Handle any errors.
     */
    @PostMapping("/watchList")
    @ResponseBody
    public AuctionResponse watchList(@RequestBody Integer id) throws FutErrorException {
        return transferService.watchList(id);
    }

    /**
     * Get unassigned pile.
     *
     * @param id Id of account
     * @return Unassigned pile
     * @throws FutErrorException Handle any exceptions
     */
    @PostMapping("/unassigned")
    @ResponseBody
    public AuctionResponse unassignedPile(@RequestBody Integer id) throws FutErrorException {
        return transferService.unassignedPile(id);
    }

    /**
     * Remove sold
     *
     * @param id Id of account.
     * @return Auction response
     * @throws FutErrorException handle errors.
     */
    @PostMapping("/removeSold")
    @ResponseBody
    public AuctionResponse removeSold(@RequestBody Integer id) throws FutErrorException {
        return transferService.removeSold(id);
    }

    /**
     * List a single item.
     *
     * @param request Request containing info
     * @param first   If first item to be listed or not.
     * @return updated trade pile.
     * @throws Exception Handle errors.
     */
    @PostMapping("/listItem/{first}/{listType}")
    @ResponseBody
    public AuctionResponse listItem(@RequestBody ListItemRequest request,
                                    @PathVariable(name = "first") Boolean first,
                                    @PathVariable("listType") String listType) throws Exception {
        return transferService.listItem(request, first, listType);
    }

    /**
     * Auto list unlisted items.
     *
     * @param request listing info.
     * @return updated trade pile.
     * @throws Exception Handle errors.
     */
    @PostMapping("/autoListUnlisted")
    @ResponseBody
    public AuctionResponse autoListUnlisted(@RequestBody RelistRequest request) throws Exception {
        return transferService.autoListUnlisted(request);
    }

    /**
     * Relist all items.
     *
     * @param request   Relist request object.
     * @param reListAll If relist all or not.
     * @return Auction response.
     */
    @PostMapping("/reList/{all}")
    @ResponseBody
    public AuctionResponse reListAll(@RequestBody RelistRequest request, @PathVariable(name = "all") Boolean
            reListAll) throws Exception {
        return transferService.reListAll(request, reListAll);
    }

    /**
     * Remove item(s) from watch list.
     *
     * @param request Request body containing data.
     * @param tradeId Trade id to remove.
     * @return Updated watch list.
     * @throws Exception handle exceptions.
     */
    @PostMapping("/removeFromWatchList/{tradeId}")
    @ResponseBody
    public AuctionResponse removeItemFromWatchList(@RequestBody WatchListRequest request, @PathVariable(name = "tradeId") Long tradeId) throws Exception {
        return transferService.removeFromWatchList(request, tradeId);
    }

    /**
     * Move won items to trade.
     *
     * @param request Request body containing data.
     * @param tradeId Trade id to remove.
     * @return Updated watch list.
     * @throws Exception handle exception.
     */
    @PostMapping("/moveToTrade/{tradeId}")
    @ResponseBody
    public AuctionResponse moveToTrade(@RequestBody WatchListRequest request, @PathVariable(name = "tradeId") Long tradeId) throws Exception {
        return transferService.moveToTrade(request, tradeId);
    }

    /**
     * Move items to trade AND list them.
     *
     * @param request listing info.
     * @return updated trade pile.
     * @throws Exception Handle errors.
     */
    @PostMapping("/moveAndList")
    @ResponseBody
    public AuctionResponse moveAndList(@RequestBody RelistRequest request) throws Exception {
        return transferService.moveAndList(request);
    }

    /**
     * TODO:
     * Send item to club from watch list? (can also be trade pile i ASSUME.)
     *
     * @return
     */
    @PostMapping("/moveToClub")
    @ResponseBody
    public AuctionResponse moveToClub() {
        return null;
    }

    /**
     * Stop the listing
     *
     * @return stop value
     */
    @GetMapping("/stopListing")
    @ResponseBody
    public boolean stopAutoList() {
        return transferService. stopAutoList();
    }

    /**
     * Pause the relisting
     *
     * @return pause value
     */
    @GetMapping("/pauseListing")
    @ResponseBody
    public boolean pauseRelisting() {
        return transferService.pauseRelisting();
    }

    @GetMapping("/stopCache")
    @ResponseBody
    public boolean stopCache() throws Exception {
        transferService.stopCache();
        return true;
    }

}

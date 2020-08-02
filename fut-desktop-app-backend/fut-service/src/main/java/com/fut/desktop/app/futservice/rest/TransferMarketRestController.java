package com.fut.desktop.app.futservice.rest;

import com.fut.desktop.app.domain.AuctionResponse;
import com.fut.desktop.app.exceptions.FutErrorException;
import com.fut.desktop.app.futservice.service.base.TransferMarketService;
import com.fut.desktop.app.restObjects.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Handle transfer market operations
 */
@RequestMapping("/transferMarket")
@RestController
@Slf4j
public class TransferMarketRestController {

    /**
     * Handle to {@link TransferMarketService}
     */
    private final TransferMarketService transferMarketService;

    /**
     * Constructor
     *
     * @param transferMarketService Handle to {@link TransferMarketService}
     */
    @Autowired
    public TransferMarketRestController(TransferMarketService transferMarketService) {
        this.transferMarketService = transferMarketService;
    }

    /**
     * Start auto search. Used to retrieve player prices.
     *
     * @param request Request body.
     * @return true once done.
     * @throws FutErrorException Handle errors.
     */
    @PostMapping("/autoSearch")
    @ResponseBody
    public boolean startAutoSearch(@RequestBody SearchBody request) throws FutErrorException {
        return transferMarketService.startAutoSearch(request);
    }

    /**
     * Start the auto bid process.
     *
     * @param request SearchBody request DTO.
     * @return true once finished
     * @throws FutErrorException Handle any errors.
     */
    @PostMapping("/autoBid")
    @ResponseBody
    public boolean startAutoBid(@RequestBody AutoBidRequest request) throws FutErrorException {
        return transferMarketService.startAutoBid(request);
    }

    @PostMapping("/search/{firstSearch}/{accountId}")
    @ResponseBody
    public AuctionResponse search(@RequestBody MarketBody request, @PathVariable("firstSearch") Boolean firstSearch, @PathVariable Integer accountId) throws FutErrorException {
        return transferMarketService.search(request, firstSearch, accountId);
    }

    /**
     * Bid on given item
     *
     * @param request Request body
     * @return Auction info with bid item result
     * @throws FutErrorException Handle errors
     */
    @PostMapping("/bidItem")
    @ResponseBody
    public AuctionResponse bidItem(@RequestBody BidItemRequest request) throws FutErrorException {
        return transferMarketService.bidItem(request);
    }

    @PostMapping("/binItem")
    @ResponseBody
    public AuctionResponse binItem(@RequestBody BidItemRequest request) throws FutErrorException {
        return transferMarketService.binItem(request);
    }

    /**
     * Calculate each player listing & search price.
     *
     * @param request Request body.
     */
    @PostMapping("/updatePrices")
    @ResponseBody
    public List<Player> calculatePlayersPrice(@RequestBody UpdatePriceBody request) throws Exception {
        return transferMarketService.calculatePlayersPrice(request);
    }

    @GetMapping("/pauseSearch")
    @ResponseBody
    public boolean pauseAutoSearch() {
        return transferMarketService.pauseSearchingOrBidding();
    }

    @GetMapping("/stopSearch")
    @ResponseBody
    public boolean stopAutoSearch() {
        return transferMarketService.stopSearchingOrBidding();
    }

    /**
     * This must return something otherwise Angular cannot process it since it needs a JSON response
     *
     * @param userOption modal option
     * @param newPrice   If new price.
     * @return
     */
    @PostMapping("/priceInput/{newPrice}")
    @ResponseStatus(HttpStatus.OK)
    public boolean setUserInput(@RequestBody Integer userOption, @PathVariable("newPrice") Integer newPrice) {
        transferMarketService.setInputForNewPrice(userOption, newPrice);
        return true;
    }
}

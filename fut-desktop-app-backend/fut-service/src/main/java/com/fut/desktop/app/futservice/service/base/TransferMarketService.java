package com.fut.desktop.app.futservice.service.base;

import com.fut.desktop.app.domain.AuctionResponse;
import com.fut.desktop.app.exceptions.FutErrorException;
import com.fut.desktop.app.restObjects.*;

import java.util.List;

/**
 * Contract for transfer market service.
 */
public interface TransferMarketService {

    /**
     * Start auto search for players prices.
     *
     * @param request Search body with parameters.
     * @return true once complete.
     */
    Boolean startAutoSearch(SearchBody request) throws FutErrorException;

    /**
     * Start auto bidding.
     *
     * @param request Search body with parameters.
     * @return true once complete.
     */
    Boolean startAutoBid(AutoBidRequest request) throws FutErrorException;

    /**
     * Search for given item.
     *
     * @param request   request with search details
     * @param first     If first search or not.
     * @param accountId The account Id.
     * @return Search results.
     */
    AuctionResponse search(MarketBody request, Boolean first, Integer accountId) throws FutErrorException;

    /**
     * Bid on item.
     *
     * @param request Bid item request.
     * @return Updated auction
     * @throws FutErrorException handle errors.
     */
    AuctionResponse bidItem(BidItemRequest request) throws FutErrorException;

    /**
     * BIN an item.
     *
     * @param request Bid item request.
     * @return Updated auction.
     * @throws FutErrorException handle errors.
     */
    AuctionResponse binItem(BidItemRequest request) throws FutErrorException;

    /**
     * Calculate each player listing & search price.
     *
     * @param request Request body.
     */
    List<Player> calculatePlayersPrice(UpdatePriceBody request) throws Exception;

    /**
     * Stop searching/bidding
     *
     * @return stop value.
     */
    Boolean stopSearchingOrBidding();

    /**
     * Pause searching/bidding.
     *
     * @return pause value.
     */
    Boolean pauseSearchingOrBidding();

    /**
     * Wait for user input switch for too many search results while auto bidding.
     *
     * @return awaitForUser value.
     */
    Boolean setWaitForUserInput();

    /**
     * Set new price which comes from waiting for user if
     * too many search results found while auto bidding.
     *
     * @param option   1 = yes. 2 = no.  99 = cancel.
     * @param newPrice newPrice to set.
     */
    void setInputForNewPrice(Integer option, Integer newPrice);
}

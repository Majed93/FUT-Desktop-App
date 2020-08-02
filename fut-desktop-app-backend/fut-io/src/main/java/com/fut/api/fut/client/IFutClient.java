package com.fut.api.fut.client;

import com.fut.api.fut.factories.FutRequestFactories;
import com.fut.desktop.app.domain.*;
import com.fut.desktop.app.dto.PileItemDto;
import com.fut.desktop.app.parameters.SearchParameters;
import com.fut.desktop.app.services.base.ITwoFactorCodeProvider;
import org.apache.http.impl.client.BasicCookieStore;

import java.util.Collection;
import java.util.concurrent.Future;

public interface IFutClient {

    /**
     * Get timeout factor
     *
     * @return timeout factor
     */
    Integer getTimeoutFactor();

    /**
     * Set the timeout factor.
     *
     * @param timeoutFactor new timeout factor.
     */
    void setTimeoutFactor(Integer timeoutFactor);

    /**
     * Create new instance with cookies.
     *
     * @param basicCookieStore Cookies.
     */
    void newInstance(BasicCookieStore basicCookieStore);

    /**
     * Get request factory.
     *
     * @return request factory.
     */
    FutRequestFactories getFutRequestFactories();

    /**
     * Get remote config.
     */
    void remoteConfig() throws Exception;

    /**
     * Go to home hub
     *
     * @param sendTrade True if required to fetch trade pile. Usually only on initial login
     * @return Home wrapper with objects
     * @throws Exception handle errors
     */
    Future<HomeWrapper> homeHub(Boolean sendTrade) throws Exception;

    /**
     * Login
     *
     * @param loginDetails           .
     * @param iTwoFactorCodeProvider .
     * @return login response.
     * @throws Exception .
     */
    Future<LoginAuctionWrapper> login(LoginDetails loginDetails, ITwoFactorCodeProvider iTwoFactorCodeProvider) throws Exception;

    /**
     * Get credits.
     *
     * @return credits.
     * @throws Exception Exception thrown while processing request.
     */
    Future<CreditsResponse> getCredits() throws Exception;

    /**
     * Get trade pile.
     *
     * @param sendPin If pin events need to be sent or not.
     * @return auction response with trade pile and coins.
     * @throws Exception Exception thrown while processing request.
     */
    Future<AuctionResponse> getTradePile(boolean sendPin) throws Exception;

    /**
     * Get watch list
     *
     * @return Watch list response.
     * @throws Exception Handle exceptions.
     */
    Future<AuctionResponse> getWatchList(boolean sendPin) throws Exception;

    /**
     * Search transfer market.
     *
     * @param params  Search parameters.
     * @param isFirst Is first search. True if we've searched before and essentially just want to go 'back'
     * @return Search results
     * @throws Exception Exception thrown while processing request.
     */
    Future<AuctionResponse> search(SearchParameters params, Boolean isFirst) throws Exception;

    /**
     * Remove sold.
     *
     * @return .
     * @throws Exception Handle exceptions.
     */
    Future<Boolean> removeSold() throws Exception;

    /**
     * Re-list all items.
     *
     * @return Relist response.
     * @throws Exception Thrown while processing request.
     */
    Future<RelistResponse> getReListAll() throws Exception;

    /**
     * List an item.
     *
     * @param auctionDetails Details to list with.
     * @param sendOptions    if true then send options otherwise don't. When massing listing, options is only sent after the FIRST item.
     * @return list auction response.
     * @throws Exception Handle exception.
     */
    Future<ListAuctionResponse> listItem(AuctionDetails auctionDetails, Boolean sendOptions) throws Exception;

    /**
     * Get trade status of an item.
     *
     * @param tradeIds Id of trade to check
     * @return Auction response of items.
     * @throws Exception Handle exception.
     */
    Future<AuctionResponse> tradeStatus(Collection<Long> tradeIds) throws Exception;

    /**
     * Remove item from watch list.
     *
     * @param tradeIds Ids of items to remove
     * @return true if successful otherwise false.
     * @throws Exception Handle exception.
     */
    Future<Boolean> removeFromWatchList(Collection<Long> tradeIds) throws Exception;

    /**
     * Move item to trade pile
     *
     * @param pileItem Item to move
     * @param pile     Pile to move to. e.g 'trade'.
     * @param sendOptions    if true then send options otherwise don't. When massing listing, options is only sent after the FIRST item.
     * @return Moved status.
     * @throws Exception Handle exceptions.
     */
    Future<SendToPile> moveItemToPile(PileItemDto pileItem, String pile, Boolean sendOptions) throws Exception;

    /**
     * Bid on an item. Including BINs.
     * <p>
     * If bidAmount = buy now price then BIN.
     * If bidAmount anything else then bid the specified amount.
     *
     * @param tradeId   Trade Id for item.
     * @param bidAmount Amount to bid.
     * @return Updated auction info for item.
     * @throws Exception Handle exceptions.
     */
    Future<AuctionResponse> bidItem(long tradeId, Long bidAmount) throws Exception;

    /**
     * Retrieve price ranges for given price range.
     *
     * @param itemId Id of item
     * @return Price range data
     * @throws Exception Handle any errors.
     */
    Future<PriceRange> priceRange(Long itemId) throws Exception;

    /**
     * Get unassigned pile.
     *
     * @return Unassigned pile.
     * @throws Exception Handle any errors.
     */
    Future<AuctionResponse> getUnassignedPile() throws Exception;
}

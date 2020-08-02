package com.fut.desktop.app.futservice.service.base;

import com.fut.desktop.app.constants.Pile;
import com.fut.desktop.app.domain.AuctionInfo;
import com.fut.desktop.app.domain.AuctionResponse;
import com.fut.desktop.app.dto.PileItemDto;
import com.fut.desktop.app.exceptions.FutErrorException;
import com.fut.desktop.app.restObjects.ListItemRequest;
import com.fut.desktop.app.restObjects.RelistRequest;
import com.fut.desktop.app.restObjects.WatchListRequest;

/**
 * Contract for transfer service.
 */
public interface TransferService {

    /**
     * Get trade pile
     *
     * @param id Id of account.
     * @return Trade pile response
     * @throws FutErrorException handle any exception thrown
     */
    AuctionResponse tradePile(Integer id) throws FutErrorException;

    /**
     * Get watch list
     *
     * @param id Id of account.
     * @return Watch List response.
     * @throws FutErrorException Handle any exceptions thrown.
     */
    AuctionResponse watchList(Integer id) throws FutErrorException;

    /**
     * Get unassigned pile
     *
     * @param id Id of account.
     * @return Unassigned Pile
     * @throws FutErrorException Handle any exceptions.
     */
    AuctionResponse unassignedPile(Integer id) throws FutErrorException;

    /**
     * Remove sold
     *
     * @param id Id of account.
     * @return Auction response
     * @throws FutErrorException handle errors.
     */
    AuctionResponse removeSold(Integer id) throws FutErrorException;

    /**
     * List a single item.
     *
     * @param request  Request containing info
     * @param first    If first item to be listed or not.
     * @param listType Type of list, tradePile, watchList, unassignedPile
     * @return updated trade pile.
     * @throws Exception Handle errors.
     */
    AuctionResponse listItem(ListItemRequest request, Boolean first, String listType) throws Exception;

    /**
     * Auto list unlisted items.
     *
     * @param request listing info.
     * @return updated trade pile.
     * @throws Exception Handle errors.
     */
    AuctionResponse autoListUnlisted(RelistRequest request) throws Exception;

    /**
     * Relist all accounts.
     *
     * @param request   Relist request object.
     * @param reListAll If relist all or not.
     * @return Auction response.
     * @throws Exception Handle errors.
     */
    AuctionResponse reListAll(RelistRequest request, Boolean reListAll) throws Exception;

    /**
     * Remove item(s) from watch list.
     *
     * @param request Request info
     * @param tradeId Id which to process, if 0, then more than one. If > 0 then only one item.
     * @return Updated watch list.
     * @throws Exception Handle errors.
     */
    AuctionResponse removeFromWatchList(WatchListRequest request, Long tradeId) throws Exception;

    /**
     * Move items to trade pile
     *
     * @param request Request with info.
     * @param tradeId trade id.
     * @return Updated watch list.
     * @throws Exception Handle errors.
     */
    AuctionResponse moveToTrade(WatchListRequest request, Long tradeId) throws Exception;

    /**
     * List items directly from given location
     *
     * @param request Request with info.
     * @return AuctionResponse from listed items.
     * @throws Exception Handle errors.
     */
    AuctionResponse moveAndList(RelistRequest request) throws Exception;

    /**
     * Stop the listing
     *
     * @return stop value
     */
    Boolean stopAutoList();

    /**
     * Pause the relisting
     *
     * @return pause value
     */
    Boolean pauseRelisting();

    /**
     * Set stop.
     *
     * @param stop stop value.
     */
    void setStop(boolean stop);

    /**
     * Set pause.
     *
     * @param pause pause value.
     */
    void setPause(boolean pause);


    /**
     * Create a pile item.
     *
     * @param info         Auction info.
     * @param pile         Which pile to send it to, trade or club, etc.
     * @param isUnassigned True if we're sending to unassigned pile otherwise false.
     * @return Created pile item
     */
    PileItemDto createPileItem(AuctionInfo info, String pile, Boolean isUnassigned);

    /**
     * Start caching.
     */
    void startCache();

    /**
     * Stop caching.
     */
    void stopCache() throws Exception;

    /**
     * Save pile sizes and times to file.
     *
     * @param accountId Id of account
     * @param pile      Type of pile
     * @param list      List of the pile
     */
    void saveListInfo(Integer accountId, Pile pile, AuctionResponse list) throws FutErrorException;
}

package com.fut.desktop.app.futsimulator.service;

import com.fut.desktop.app.domain.*;
import com.fut.desktop.app.extensions.DateTimeExtensions;
import com.fut.desktop.app.futsimulator.dto.MarketSearchRequest;
import com.fut.desktop.app.futsimulator.utils.AuctionResponseGenerator;
import com.fut.desktop.app.futsimulator.utils.GlobalPlayerUtil;
import com.fut.desktop.app.futsimulator.utils.UnassignedPileGenerator;
import com.fut.desktop.app.utils.SleepUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Slf4j
@Getter
@Setter
@Component
@Scope("singleton")
public class TransferSimService {

    /**
     * Piles which can be trained
     */
    private AuctionResponse tradePile;
    private AuctionResponse watchList;
    private PurchasedItemsResponse unassignedPile;
    private Long credits;
    private AuctionResponse searchResults;

    /**
     * Price ranges
     */
    public Integer defaultMin = 150;
    public Integer defaultMax = 100000;

    private final AuctionResponseGenerator auctionResponseGenerator;
    private final UnassignedPileGenerator unassignedPileGenerator;

    public TransferSimService(AuctionResponseGenerator auctionResponseGenerator,
                              UnassignedPileGenerator unassignedPileGenerator) {
        credits = 0L;
        this.auctionResponseGenerator = auctionResponseGenerator;
        this.unassignedPileGenerator = unassignedPileGenerator;
        tradePile = new AuctionResponse();
        watchList = new AuctionResponse();
        unassignedPile = new PurchasedItemsResponse();
        searchResults = new AuctionResponse();
        searchResults.setAuctionInfo(new ArrayList<>());
        tradePile.setAuctionInfo(new ArrayList<>());
        watchList.setAuctionInfo(new ArrayList<>());
        unassignedPile.setItemData(new ArrayList<>());
        tradePile.setCredits(credits);
        watchList.setCredits(credits);
    }

    /**
     * Generate a NEW trade pile with random number of players items
     *
     * @param numOfItems Number of items to generate.
     */
    public void generateTradePilePileWithItems(Integer numOfItems) {
        tradePile = auctionResponseGenerator.generateTradePile(numOfItems, credits);
    }

    /**
     * Generate a NEW watch pile with random number of players items
     *
     * @param numOfItems Number of items to generate.
     */
    public void generateWatchListPileWithItems(Integer numOfItems) {
        watchList = auctionResponseGenerator.generateWatchList(numOfItems, credits);
    }

    /**
     * Generate a NEW unassigned pile
     *
     * @param numOfItems Number of items to generate
     */
    public void generateUnassignedPileWithItems(Integer numOfItems) {
        unassignedPile = unassignedPileGenerator.generateUnassignedPile(numOfItems);
    }

    /**
     * Generate a number of unlisted items for the trade pile.
     */
    public void generateTradePileWithUnlistedItems(Integer number, String listName) {
        tradePile.getAuctionInfo().addAll(auctionResponseGenerator.makeUnlistedItems(number, listName));
    }

    /**
     * Generate sold items for the trade pile
     */
    public void generateTradePileWithSoldItems(Integer number, String listName) {
        tradePile.getAuctionInfo().addAll(auctionResponseGenerator.makeSoldItems(number, listName));
    }

    /**
     * Generate expired items for the trade pile
     */
    public void generateTradePileWithExpiredItems(Integer number, String listName) {
        tradePile.getAuctionInfo().addAll(auctionResponseGenerator.makeExpiredItems(number, listName));
    }

    /**
     * Generate watch list with won items
     */
    public void generateWatchListWithWon(Integer number, String listName) {
        watchList.getAuctionInfo().addAll(auctionResponseGenerator.makeWonItems(number, listName));
    }

    /**
     * Generate watch list with winning items.
     */
    public void generateWatchListWithWinning(Integer number, String listName) {
        watchList.getAuctionInfo().addAll(auctionResponseGenerator.makeWinningItems(number, listName));
    }

    /**
     * Generate watch list with outbidded items.
     */
    public void generateWatchListWithOutbiddedItems(Integer number, String listName) {
        watchList.getAuctionInfo().addAll(auctionResponseGenerator.makeOutbiddedItems(number, listName));
    }

    /**
     * Generate watch list with expired items.
     */
    public void generateWatchListPileWithExpiredItems(Integer number, String listName) {
        watchList.getAuctionInfo().addAll(auctionResponseGenerator.makeExpiredWatchListItems(number, listName));
    }

    /**
     * List a single item. Set the according values
     */
    public void listItem(Long itemId, Long tradeId, Integer startingPrice, Integer buyItNowPrice, long duration) {
        tradePile.getAuctionInfo().forEach(auctionInfo -> {
            if (auctionInfo.getItemData().getId() == itemId) {
                auctionInfo.setTradeId(tradeId);
                auctionInfo.setStartingBid(startingPrice);
                auctionInfo.setBuyNowPrice(buyItNowPrice);
                auctionInfo.setExpires((int) duration);
                auctionInfo.getItemData().setItemState("forSale");
                auctionInfo.setTradeState("active");
                auctionInfo.setBidState("none");
            }
        });

    }

    /**
     * Generate a trade status response
     */
    public AuctionResponse generateTradeStatus(long tradeIds) {
        AuctionResponse response = new AuctionResponse();

        response.setCurrencies(tradePile.getCurrencies());
        response.setCredits(tradePile.getCredits());

        // Find one
        AuctionInfo auctionInfo = tradePile.getAuctionInfo().stream().filter(a -> a.getTradeId() == tradeIds).findFirst().orElse(null);
        response.setAuctionInfo(Collections.singletonList(auctionInfo));

        return response;
    }

    /**
     * Set the coins
     */
    public void setCredits(Long coins) {
        credits = coins;
        this.tradePile.setCredits(credits);
        this.watchList.setCredits(credits);
        this.tradePile.setCurrencies(currencyList());
        this.watchList.setCurrencies(currencyList());
    }

    /**
     * Remove sold items.
     */
    public void removeSold() {
        tradePile.getAuctionInfo().
                removeIf(aI -> aI.getExpires() < 0 &&
                        aI.getTradeState().equals("closed") &&
                        aI.getCurrentBid() > aI.getStartingBid());

    }

    /**
     * Relist items. NOTE: Only doing for an hour because don't know how it actually gets set.
     */
    public RelistResponse relist() {
        List<ListAuctionResponse> tradeIds = new ArrayList<>();

        tradePile.getAuctionInfo().forEach(aI -> {
            if (aI.getExpires() < 0 && aI.getCurrentBid() == 0) {
                tradeIds.add(new ListAuctionResponse(aI.getTradeId(), aI.getTradeIdStr()));
                aI.setExpires(AuctionDuration.OneHour.getDuration());
                aI.setTradeState("active");
            }
        });

        return new RelistResponse(tradeIds);
    }

    /**
     * Generate search results.
     */
    public AuctionResponse generateSearchResults(Long maskedDefId) {

        if (maskedDefId != null) {
            this.searchResults = auctionResponseGenerator.generateSearchResults(maskedDefId);
        } else {
            this.searchResults = auctionResponseGenerator.generateSearchResults(GlobalPlayerUtil.randomAssetId());
        }

        this.searchResults.setCredits(this.credits);

        this.searchResults.setCurrencies(currencyList());
        return this.searchResults;
    }

    /**
     * Generate search results.
     */
    public void generateSearchResults(List<MarketSearchRequest> request) {

        this.searchResults.setAuctionInfo(new ArrayList<>());

        int count = 0;
        for (MarketSearchRequest r : request) {
            // Ensure the starting bid is less than the bin
            Assert.assertTrue("Starting price for item of index: " + count + " is greater than the bin", r.getStartingBid() < r.getBuyNowPrice());

            if (r.getAssetId() < 1) {
                r.setAssetId(GlobalPlayerUtil.getPlayersList().
                        get(SleepUtil.random(0, GlobalPlayerUtil.getPlayersList().size())).getAssetId());
            }

            AuctionInfo info = this.auctionResponseGenerator.generateAuctionInfo(
                    r.getAssetId(), // AssetId - this will be overriden when returned by the mock search endpoint
                    r.getBidState(),// Bid state
                    r.getTradeState(),// trade state
                    r.getCurrentBid(),// current bid
                    r.getStartingBid(),// starting bid
                    r.getBuyNowPrice(),// buy now price
                    String.valueOf(DateTimeExtensions.ToUnixTime()),// timestamp
                    r.getExpires(),// expires
                    false// isWatched
            );
            this.searchResults.getAuctionInfo().add(info);
            count++;
        }

        this.searchResults.setCredits(this.credits);

        this.searchResults.setCurrencies(currencyList());
    }

    /**
     * Generate bidded item.
     */
    public AuctionResponse generateBiddedItem(Long tradeId, Long bidAmount) {
        AuctionResponse response = new AuctionResponse();

        AuctionInfo info = this.searchResults.getAuctionInfo().stream().filter(x -> x.getTradeId() == tradeId).findFirst().orElse(null);

        assert info != null;

        if (info.getBuyNowPrice() <= bidAmount) {
            // Binning
            info.setCurrentBid(info.getBuyNowPrice());
            info.setExpires(-1);
            info.setTradeState("closed");
            info.setBidState("buyNow");
            this.credits -= bidAmount;
        } else {
            // Normal bidding
            this.credits -= bidAmount;
            info.setWatched(true);
            info.setCurrentBid(bidAmount);
            info.setBidState("highest");

//            this.watchList.getAuctionInfo().add(info);
        }

        setCredits(this.credits);
        response.setCredits(this.credits);
        response.setCurrencies(currencyList());
        response.setAuctionInfo(Collections.singletonList(info));
        return response;
    }

    /**
     * Create a currency list.
     */
    private List<Currency> currencyList() {
        List<Currency> currencyList = new ArrayList<>();
        currencyList.add(new Currency(credits, credits, "COINS"));
        currencyList.add(new Currency(0, 0, "POINTS"));
        currencyList.add(new Currency(0, 0, "DRAFT_TOKEN"));
        return currencyList;
    }

    /**
     * Remove items from the watch list.
     *
     * @param tradeIds The trade ids to remove from the watch list.
     */
    public void removeFromWatchList(Collection<Long> tradeIds) {
        tradeIds.forEach(t -> this.watchList.getAuctionInfo().removeIf(a -> a.getTradeId() == t));
    }

    public AuctionResponse setAssetIdAndReturnSearchResults(Integer maskedDefId) {
        AuctionResponse aR = this.searchResults;

        aR.getAuctionInfo().forEach(a -> {
            a.getItemData().setAssetId(maskedDefId);
            a.getItemData().setResourceId(maskedDefId);
        });

        return aR;
    }
}

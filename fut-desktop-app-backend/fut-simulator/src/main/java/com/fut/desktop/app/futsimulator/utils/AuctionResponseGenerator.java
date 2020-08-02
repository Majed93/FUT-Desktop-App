package com.fut.desktop.app.futsimulator.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fut.desktop.app.domain.*;
import com.fut.desktop.app.extensions.DateTimeExtensions;
import com.fut.desktop.app.parameters.ChemistryStyle;
import com.fut.desktop.app.parameters.Nation;
import com.fut.desktop.app.restObjects.Player;
import com.fut.desktop.app.services.base.PlayerService;
import com.fut.desktop.app.utils.FileUtils;
import com.fut.desktop.app.utils.SleepUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Slf4j
@Component
public class AuctionResponseGenerator {

    private final PlayerService playerService;

    // This corresponds to the maximum number of items displayed on EAs web app
    private final Integer MAX_PAGE_SIZE = 20;

    @Autowired
    public AuctionResponseGenerator(PlayerService playerService) {
        this.playerService = playerService;
        GlobalPlayerUtil.setPlayersList(playerService.findAllPlayers());
    }

    public AuctionResponse readTradePile() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);

        try {
            InputStream inputStream = this.getClass().getResourceAsStream("/tradePile.json");
            return mapper.readValue(inputStream, AuctionResponse.class);
        } catch (Exception e) {
            log.error("Unable to parse JSON: " + e.getMessage());
            throw new Exception("Unable to parse JSON: " + e.getMessage());
        }
    }

    /**
     * Generate response for one bidded item.
     *
     * @param info      Auction info
     * @param bidAmount bid amount.
     */
    public AuctionResponse generateBiddedItem(AuctionInfo info, Long bidAmount) {
        AuctionResponse auctionResponse = new AuctionResponse();

        if (bidAmount == info.getBuyNowPrice()) {
            info.setExpires(-1);
            info.setTradeState("closed");
            info.setBidState("buyNow");
        } else {
            info.setBidState("highest");
        }

        info.setCurrentBid(bidAmount);
        auctionResponse.setAuctionInfo(Collections.singletonList(info));

        return auctionResponse;
    }

    /**
     * Generate search results.
     *
     * @param resourceId Id of item
     */
    public AuctionResponse generateSearchResults(Long resourceId) {
        AuctionResponse auctionResponse = new AuctionResponse();

        List<AuctionInfo> auctionInfos = new ArrayList<>();
        for (int i = 1; i < MAX_PAGE_SIZE; i++) {
            if (resourceId == null || resourceId < 1) {
                resourceId = GlobalPlayerUtil.getPlayersList().
                        get(SleepUtil.random(0, GlobalPlayerUtil.getPlayersList().size())).getAssetId();
            }

            auctionInfos.add(generateAuctionInfo(resourceId,
                    "none", "active", 300, 150,
                    Math.toIntExact(AuctionInfo.roundToNearest(SleepUtil.random(i * 200, i * 900))),
                    String.valueOf(DateTimeExtensions.ToUnixTime()),
                    SleepUtil.random(100, 4000), false));
        }

        auctionResponse.setBidTokens(new BidTokens());
        auctionResponse.setAuctionInfo(auctionInfos);

        return auctionResponse;
    }

    /**
     * Generate watch list.
     */
    public AuctionResponse generateWatchList(Integer numberOfItems, Long credits) {
        AuctionResponse auctionResponse = new AuctionResponse();
        auctionResponse.setCredits(credits);

        List<AuctionInfo> auctionInfos = new ArrayList<>();

        for (int i = 0; i < numberOfItems; i++) {
            auctionInfos.add(generateAuctionInfo(GlobalPlayerUtil.randomAssetId(),
                    "outbid", "expired", 500, 150, 10000,
                    String.valueOf(DateTimeExtensions.ToUnixTime()), -1, true));
        }
        // Generate won items
     /*   for (int i = 1; i < 3; i++) {
            auctionInfos.add(generateAuctionInfo(200759L,
                    "highest", "closed", 500, 150,
                    10000,
                    String.valueOf(DateTimeExtensions.ToUnixTime()),
                    -1, false));
        }*/


        auctionResponse.setAuctionInfo(auctionInfos);

        return auctionResponse;
    }

    /**
     * Generate trade pile.
     */
    public AuctionResponse generateTradePile(Integer numberOfItems, Long credits) {
        AuctionResponse auctionResponse = new AuctionResponse();
        auctionResponse.setCredits(credits);

        List<AuctionInfo> auctionInfos = new ArrayList<>();

        for (int i = 0; i < numberOfItems; i++) {
            auctionInfos.add(generateAuctionInfo(GlobalPlayerUtil.randomAssetId(),
                    "none", "expired", 0, 150,
                    10000,
                    String.valueOf(DateTimeExtensions.ToUnixTime()),
                    -1, false));
        }

        auctionResponse.setAuctionInfo(auctionInfos);

        return auctionResponse;
    }

    /**
     * Create unlisted items
     *
     * @param number   Number of items to create
     * @param listName
     * @return Unlisted items
     */
    public List<AuctionInfo> makeUnlistedItems(Integer number, String listName) {
        List<AuctionInfo> auctionInfos = new ArrayList<>();
        List<Long> assetIds = generateIds(number, listName);

        for (int i = 0; i < number; i++) {
            auctionInfos.add(generateAuctionInfo(assetIds.get(i),
                    "none", null, 0, 150,
                    10000,
                    String.valueOf(DateTimeExtensions.ToUnixTime()),
                    -1, false));
        }

        return auctionInfos;
    }


    /**
     * Create expired items
     */
    public List<AuctionInfo> makeExpiredItems(Integer number, String listName) {
        List<AuctionInfo> auctionInfos = new ArrayList<>();
        List<Long> assetIds = generateIds(number, listName);

        for (int i = 0; i < number; i++) {
            auctionInfos.add(generateAuctionInfo(assetIds.get(i),
                    "none", "expired", 0, 150,
                    10000,
                    String.valueOf(DateTimeExtensions.ToUnixTime()),
                    -1, false));
        }

        return auctionInfos;
    }

    /**
     * Create list of sold items. Only mocking BINNED items
     */
    public List<AuctionInfo> makeSoldItems(Integer number, String listName) {
        List<AuctionInfo> auctionInfos = new ArrayList<>();
        List<Long> assetIds = generateIds(number, listName);

        for (int i = 0; i < number; i++) {
            auctionInfos.add(generateAuctionInfo(assetIds.get(i),
                    "none", "closed", 10000, 300,
                    10000,
                    String.valueOf(DateTimeExtensions.ToUnixTime()),
                    -1, false));
        }

        return auctionInfos;
    }

    /**
     * Create a list of won items. Used for watch list.
     */
    public List<AuctionInfo> makeWonItems(Integer number, String listName) {
        List<AuctionInfo> auctionInfos = new ArrayList<>();
        List<Long> assetIds = generateIds(number, listName);

        for (int i = 0; i < number; i++) {
            auctionInfos.add(generateAuctionInfo(assetIds.get(i),
                    "highest", "closed", 10000, 300,
                    10000,
                    String.valueOf(DateTimeExtensions.ToUnixTime()),
                    -1, true));
        }

        return auctionInfos;
    }

    /**
     * Create list of winning items - used for watch list.
     */
    public List<AuctionInfo> makeWinningItems(Integer number, String listName) {
        List<AuctionInfo> auctionInfos = new ArrayList<>();
        List<Long> assetIds = generateIds(number, listName);

        for (int i = 0; i < number; i++) {
            auctionInfos.add(generateAuctionInfo(assetIds.get(i),
                    "highest", "active", 10000, 300,
                    10000,
                    String.valueOf(DateTimeExtensions.ToUnixTime()),
                    SleepUtil.random(60, 86400), true));
        }

        return auctionInfos;
    }

    /**
     * Create list of outbidded items - used for watch list.
     */
    public List<AuctionInfo> makeOutbiddedItems(Integer number, String listName) {
        List<AuctionInfo> auctionInfos = new ArrayList<>();
        List<Long> assetIds = generateIds(number, listName);

        for (int i = 0; i < number; i++) {
            auctionInfos.add(generateAuctionInfo(assetIds.get(i),
                    "outbid", "active", 10000, 300,
                    10000,
                    String.valueOf(DateTimeExtensions.ToUnixTime()),
                    SleepUtil.random(60, 86400), true));
        }

        return auctionInfos;
    }


    /**
     * Create list of expired items - used for watch list.
     */
    public List<AuctionInfo> makeExpiredWatchListItems(Integer number, String listName) {
        List<AuctionInfo> auctionInfos = new ArrayList<>();
        List<Long> assetIds = generateIds(number, listName);

        for (int i = 0; i < number; i++) {
            auctionInfos.add(generateAuctionInfo(assetIds.get(i),
                    "outbid", "closed", 10000, 300,
                    10000,
                    String.valueOf(DateTimeExtensions.ToUnixTime()),
                    -1, true));
        }

        return auctionInfos;
    }

    /**
     * Generate a list of longs either randomly or from a player list.
     */
    private List<Long> generateIds(Integer number, String listName) {
        List<Long> assetIds = new ArrayList<>();
        List<Player> playerJsonList = listName == null || listName.contains("null") || listName.isEmpty()
                ? new ArrayList<>() : playerService.getPlayerJsonList(listName + FileUtils.jsonExt);
        for (int i = 0; i < number; i++) {
            if (playerJsonList != null && !playerJsonList.isEmpty()) {
                int index = SleepUtil.random(0, playerJsonList.size() - 1);
                assetIds.add(playerJsonList.get(index).getAssetId());
            } else {
                assetIds.add(GlobalPlayerUtil.randomAssetId());
            }
        }
        return assetIds;
    }

    /**
     * Generate auction infos
     *
     * @param assetId     Asset id of item.
     * @param tradeState  trade state.
     * @param currentBid  current bid.
     * @param startingBid starting bid.
     * @param buyNowPrice buy now price.
     * @return auction info.
     */
    public AuctionInfo generateAuctionInfo(Long assetId, String bidState, String tradeState,
                                            Integer currentBid, Integer startingBid, Integer buyNowPrice,
                                            String timestamp, Integer expires, boolean watched) {
        Player player = GlobalPlayerUtil.findOne(assetId);
        AuctionInfo auctionInfo = new AuctionInfo();
        Random rand = new Random();
        long tradeId = rand.nextInt(Integer.MAX_VALUE - 1);
        auctionInfo.setTradeId(tradeId);

        ItemData itemData = new ItemData();
        itemData.setId(rand.nextInt(Integer.MAX_VALUE - 1));
        itemData.setTimestamp(timestamp);
        itemData.setFormation("f4321");
        itemData.setUntradeable(false);
        itemData.setAssetId(assetId);
        itemData.setRating(player.getRating().byteValue());
        itemData.setItemType("player");
        itemData.setResourceId(assetId);
        itemData.setOwners((byte) 1);
        itemData.setDiscardValue(17);
        itemData.setItemState("free");
        itemData.setCardSubTypeId(2);
        itemData.setLastSalePrice(0);
        itemData.setMorale((byte) 50);
        itemData.setFitness((byte) 99);
        itemData.setInjuryType("none");
        itemData.setInjuryGames((byte) 0);
        itemData.setPreferredPosition(player.getPosition());

        // Create attributes.
        List<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute(0, 0));
        attributes.add(new Attribute(0, 1));
        attributes.add(new Attribute(0, 2));
        attributes.add(new Attribute(0, 3));
        attributes.add(new Attribute(0, 4));

        itemData.setStatsList(attributes);
        itemData.setLifeTimeStats(attributes);

        itemData.setTraining(0);
        itemData.setContract((byte) 7);
        itemData.setSuspension((byte) 0);

        // TODO: Can make these random?
        List<Attribute> baseStats = new ArrayList<>();
        attributes.add(new Attribute(65, 0));
        attributes.add(new Attribute(51, 1));
        attributes.add(new Attribute(52, 2));
        attributes.add(new Attribute(58, 3));
        attributes.add(new Attribute(18, 4));
        attributes.add(new Attribute(47, 4));
        itemData.setAttributeList(baseStats);

        if (player.getClub() != null) {
            itemData.setTeamId(player.getClub());
        }
        itemData.setRareFlag((byte) 0);
        itemData.setPlayStyle(ChemistryStyle.Basic);
        if (player.getLeague() != null) {
            itemData.setLeagueId(player.getLeague());
        }
        itemData.setAssists(0);
        itemData.setLifeTimeAssists(0);
        itemData.setLoyaltyBonus((byte) 1);
        itemData.setPile(5);
        itemData.setNation(player.getNation() == null ? Nation.getRandom() : player.getNation());
        itemData.setMarketDataMinPrice(150);
        itemData.setMarketDataMaxPrice(10000);
        itemData.setResourceGameYear(2019);
        auctionInfo.setItemData(itemData);

        auctionInfo.setTradeState(tradeState);
        auctionInfo.setBuyNowPrice(buyNowPrice);
        auctionInfo.setCurrentBid(currentBid);
        auctionInfo.setOffers(0);
        auctionInfo.setWatched(watched);
        auctionInfo.setBidState(bidState);
        auctionInfo.setStartingBid(startingBid);
        auctionInfo.setConfidenceValue((byte) 100);
        auctionInfo.setExpires(expires);
        auctionInfo.setSellerName(null);
        auctionInfo.setSellerId(0);
        auctionInfo.setTradeOwner(true);
        auctionInfo.setTradeIdStr(String.valueOf(tradeId));
        return auctionInfo;
    }
}

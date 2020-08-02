package com.fut.desktop.app.futsimulator.utils;

import com.fut.desktop.app.domain.Attribute;
import com.fut.desktop.app.domain.ItemData;
import com.fut.desktop.app.domain.PurchasedItemsResponse;
import com.fut.desktop.app.extensions.DateTimeExtensions;
import com.fut.desktop.app.parameters.ChemistryStyle;
import com.fut.desktop.app.restObjects.Player;
import com.fut.desktop.app.services.base.PlayerService;
import com.fut.desktop.app.utils.SleepUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Component
public class UnassignedPileGenerator {
    private final PlayerService playerService;

    public UnassignedPileGenerator(PlayerService playerService) {
        this.playerService = playerService;
        GlobalPlayerUtil.setPlayersList(playerService.findAllPlayers());
    }

    public PurchasedItemsResponse generateUnassignedPile(int numberOfItems) {
        PurchasedItemsResponse purchasedItemsResponse = new PurchasedItemsResponse();
        List<ItemData> itemDatas = new ArrayList<>();

        for (int i = 0; i < numberOfItems; i++) {
            Long assetId = GlobalPlayerUtil.getPlayersList().get(SleepUtil.random(0, GlobalPlayerUtil.getPlayersList().size())).getAssetId();
            itemDatas.add(generateItemData(assetId,
                    String.valueOf(DateTimeExtensions.ToUnixTime())));
        }
        purchasedItemsResponse.setItemData(itemDatas);

        return purchasedItemsResponse;
    }

    private ItemData generateItemData(Long assetId, String timestamp) {
        Player player = GlobalPlayerUtil.findOne(assetId);
        Random rand = new Random();

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
        itemData.setNation(player.getNation());
        itemData.setMarketDataMinPrice(150);
        itemData.setMarketDataMaxPrice(10000);
        itemData.setResourceGameYear(2019);

        return itemData;
    }
}

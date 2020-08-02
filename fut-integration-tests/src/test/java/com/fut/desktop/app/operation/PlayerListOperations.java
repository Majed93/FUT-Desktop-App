package com.fut.desktop.app.operation;

import com.fut.desktop.app.domain.AuctionInfo;
import com.fut.desktop.app.exceptions.FutErrorException;
import com.fut.desktop.app.exceptions.FutException;
import com.fut.desktop.app.futsimulator.utils.GlobalPlayerUtil;
import com.fut.desktop.app.page.PlayerListPage;
import com.fut.desktop.app.parameters.League;
import com.fut.desktop.app.parameters.Nation;
import com.fut.desktop.app.parameters.Position;
import com.fut.desktop.app.parameters.Team;
import com.fut.desktop.app.restObjects.Player;
import com.fut.desktop.app.services.base.PlayerService;
import com.fut.desktop.app.utils.FileUtils;
import com.fut.desktop.app.utils.SleepUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Player list operations
 */
@Slf4j
@Component
public class PlayerListOperations {

    private final FileUtils fileUtils;

    private final PlayerService playerService;

    private final PlayerListPage playerListPage;

    @Autowired
    PlayerListOperations(FileUtils fileUtils,
                         PlayerService playerService, PlayerListPage playerListPage) {
        this.fileUtils = fileUtils;
        this.playerService = playerService;
        this.playerListPage = playerListPage;
    }

    /**
     * Create player list
     *
     * @param fileName File to create
     */
    public void createList(String fileName) {
        Boolean created = fileUtils.createPlayerListFile(fileName);
        Assert.assertNotNull("File creation is null.", created);
        Assert.assertTrue("File creation is false.", created);

        // Write an empty array to it.
        try {
            playerService.saveList(fileName + FileUtils.jsonExt, new ArrayList<>());
        } catch (FutException fEx) {
            Assert.fail("Unable to save list, " + fEx);
        }
    }

    /**
     * Add random players
     *
     * @param numberOfPlayers Number of players to add
     * @param listName        List to add to
     */
    public void addRandomPlayers(Integer numberOfPlayers, String listName) {
        String newListName = listName + FileUtils.jsonExt;
        List<Player> playerJsonList = playerService.getPlayerJsonList(newListName);
        if (playerJsonList == null) {
            playerJsonList = new ArrayList<>();
        }

        for (int i = 0; i < numberOfPlayers; i++) {
            long assetId = GlobalPlayerUtil.randomAssetId();
            Player player = GlobalPlayerUtil.findOne(assetId);

            player.setPosition(Position.random());
            player.setLowestBin(AuctionInfo.roundToNearest(SleepUtil.random(150, 50000)));
            player.setSearchPrice(AuctionInfo.CalculateNextBid(player.getLowestBin()));
            player.setMinListPrice(AuctionInfo.CalculateNextBid(
                    AuctionInfo.CalculateNextBid(
                            AuctionInfo.CalculateNextBid(player.getSearchPrice()))));
            player.setMaxListPrice(AuctionInfo.CalculateNextBid(
                    AuctionInfo.CalculateNextBid
                            (AuctionInfo.CalculateNextBid(player.getMinListPrice()))));
            player.setNation(Nation.getRandom());
            player.setClub(Team.getRandom());
            player.setLeague(League.getRandom());
            player.setPriceSet(true);
            player.setBidAmount(AuctionInfo.CalculatePreviousBid(player.getSearchPrice()));

            try {
                playerService.addPlayer(listName, player);
            } catch (FutErrorException feEx) {
                log.error("Error adding: {}", feEx.getMessage());
                i--;
            }
        }

        List<Player> updatedList = playerService.getPlayerJsonList(newListName);

        Assert.assertEquals("Player lists don't add up.. items might not have been added.",
                numberOfPlayers + playerJsonList.size(), updatedList.size());
    }

    public void verifyVisibilityOfPlayerListTable() {
        Assert.assertNotNull(playerListPage.getPlayerTable());
        Assert.assertNotNull(playerListPage.getPlayerRows());
    }

    /**
     * Delete given list
     *
     * @param listName List to delete
     */
    public void deleteList(String listName) {
        Boolean result = playerService.deleteList(listName);

        if (result != null) {
            Assert.assertTrue("Unable to delete file! " + listName, result);
        }
    }
}

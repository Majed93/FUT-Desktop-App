package com.fut.desktop.app.services.base;

import com.fut.desktop.app.exceptions.FutErrorException;
import com.fut.desktop.app.exceptions.FutException;
import com.fut.desktop.app.restObjects.Player;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Contract for player service.
 */
public interface PlayerService {

    /**
     * Find all lists with players.
     *
     * @return all lists
     */
    Set<String> getAllLists();

    /**
     * Get a specific list
     *
     * @param name List to get.
     * @return list.
     */
    List<Player> getPlayerJsonList(String name);

    /**
     * Combine all loaded lists to get all players in one list.
     *
     * @return One list with all players.
     */
    Collection<Player> collateAllPlayersFromList();

    /**
     * Find one player by asset id.
     *
     * @param assetId Id of player.
     * @return Found player otherwise null.
     */
    Player findOne(Long assetId);

    /**
     * Save the list
     *
     * @param name       list to save
     * @param listToSave List of players to save.
     * @throws FutException Handle any exceptions.
     */
    void saveList(String name, List<Player> listToSave) throws FutException;

    /**
     * Add a player
     *
     * @param list   List to add player to.
     * @param player Player to add.
     * @return List of updated players.
     */
    List<Player> addPlayer(String list, Player player) throws FutErrorException;

    /**
     * Update a player
     *
     * @param player player to update.
     * @return List of updated players.
     */
    List<Player> updatePlayer(String list, Player player);

    /**
     * Delete a player from a list.
     *
     * @param list List to delete form
     * @param id   Player to delete.
     * @return List of updated players.
     */
    List<Player> deletePlayer(String list, Long id);

    /**
     * Return EAs JSON with all players including legends.
     *
     * @return All Players in EA Database
     */
    List<Player> findAllPlayers();

    /**
     * Get player from list.
     *
     * @param assetId Asset id of player.
     * @param list    List to search
     * @return Player or null.
     */
    Player getPlayer(Long assetId, List<Player> list);

    /**
     * Set all PriceSet values to false for given list.
     *
     * @param list List to change.
     * @return true if successful, false if failed.
     */
    boolean resetPriceSet(String list);

    /**
     * Delete given list
     *
     * @param listName Delete given list
     */
    Boolean deleteList(String listName);
}

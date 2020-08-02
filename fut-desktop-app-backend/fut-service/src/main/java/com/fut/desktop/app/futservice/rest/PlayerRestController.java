package com.fut.desktop.app.futservice.rest;

import com.fut.desktop.app.exceptions.FutErrorException;
import com.fut.desktop.app.services.base.PlayerService;
import com.fut.desktop.app.parameters.League;
import com.fut.desktop.app.parameters.Nation;
import com.fut.desktop.app.parameters.Position;
import com.fut.desktop.app.parameters.Team;
import com.fut.desktop.app.restObjects.Player;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Handles player list operations
 */
@RequestMapping("/players")
@RestController
@Slf4j
public class PlayerRestController {

    private final PlayerService playerService;

    @Autowired
    public PlayerRestController(PlayerService playerService) {
        this.playerService = playerService;
    }

    /**
     * Get all players
     *
     * @return all players
     */
    @GetMapping("/allPlayers")
    @ResponseBody
    public List<Player> getAllPlayers() {
        return playerService.findAllPlayers();
    }

    /**
     * Get all list names.
     *
     * @return Found list names
     */
    @GetMapping("/lists")
    @ResponseBody
    public Set<String> lists() {
        return playerService.getAllLists();
    }

    /**
     * Return a list
     *
     * @param list list to return.
     * @return found list.
     */
    @PostMapping("/list")
    @ResponseBody
    public List<Player> players(@RequestBody String list) {
        return playerService.getPlayerJsonList(list);
    }

    /**
     * Add player to given list
     *
     * @param list   list to add to
     * @param player player to add
     * @return list with added player.
     */
    @PostMapping("/add/{list}")
    @ResponseBody
    public List<Player> addPlayer(@PathVariable("list") String list, @RequestBody Player player) throws FutErrorException {
        return playerService.addPlayer(list, player);
    }

    /**
     * Update given player.
     *
     * @param list   list to update
     * @param player player to update
     * @return list with updated player
     */
    @PostMapping("/update/{list}")
    @ResponseBody
    public List<Player> updatePlayerList(@PathVariable(value = "list") String list, @RequestBody Player player) {
        return playerService.updatePlayer(list, player);
    }

    /**
     * Delete given player
     *
     * @param id   asset id of player to delete
     * @param list list to delete from
     * @return Updated list with deleted player
     */
    @DeleteMapping("/list/{id}/{list}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public List<Player> deletePlayer(@PathVariable(value = "id") String id, @PathVariable(value = "list") String list) {
        return playerService.deletePlayer(list, Long.valueOf(id));
    }

    /**
     * Reset price set property for all players on given list
     *
     * @param list List to change
     * @return true if successful
     */
    @PostMapping("/resetPriceSet")
    @ResponseBody
    public boolean resetPriceSet(@RequestBody String list) {
        return playerService.resetPriceSet(list);
    }

    @GetMapping("/teams")
    @ResponseBody
    public Collection<Team> getAllTeams() {
        return Team.getAll();
    }

    @GetMapping("/nations")
    @ResponseBody
    public Collection<Nation> getAllNations() {
        return Nation.getAll();
    }

    @GetMapping("/leagues")
    @ResponseBody
    public Collection<League> getAllLeagues() {
        return League.getAll();
    }

    @GetMapping("/positions")
    @ResponseBody
    public Collection<Position> getAllPositions() {
        return Position.getAllWithoutOverall();
    }
}

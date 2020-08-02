package com.fut.desktop.app.services.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fut.desktop.app.constants.FutErrorCode;
import com.fut.desktop.app.domain.FutError;
import com.fut.desktop.app.exceptions.FutErrorException;
import com.fut.desktop.app.exceptions.FutException;
import com.fut.desktop.app.extensions.LongExtensions;
import com.fut.desktop.app.restObjects.Player;
import com.fut.desktop.app.services.base.PlayerService;
import com.fut.desktop.app.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Implementation of {@link PlayerService}
 */
@Service
@Slf4j
@SuppressWarnings("unchecked")
// ^ Doing this because java doesn't think it's safe to cast the list to list.. but it is safe!
public class PlayerServiceImpl implements PlayerService {

    private static String workingDir;

    private static String DIR = "data" + File.separator + "lists";

    private static final String SUFFIX = ".players.json";

    private final FileUtils fileUtils;

    @Autowired
    public PlayerServiceImpl(FileUtils fileUtils) {
        this.fileUtils = fileUtils;
    }

    @Value("${working.dir}")
    public void setWorkingDir(String dir) {
        workingDir = dir == null ? "" : dir;
        DIR = workingDir + File.separator + DIR;
    }

    @Override
    public Set<String> getAllLists() {
        // Get all files from the data dir
        Set<String> files = new HashSet<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(DIR), path ->
                path.toString().endsWith(SUFFIX))) {
            stream.forEach(f -> files.add(f.getFileName().toString()));
            return files;
        } catch (IOException e) {
            log.error("Error reading directory: " + e.getMessage());
            return null;
        }
    }

    /**
     * Retrieve player list from given file.
     *
     * @param fileName File to get.
     * @return List of players
     */
    private List<Player> getPlayerList(String fileName) {
        ObjectMapper mapper = new ObjectMapper();

        //Read the file
        try {
            File file = new File(DIR + File.separator + fileName);
            if (!file.exists()) {
                return null;
            }
            return mapper.readValue(file, new TypeReference<List<Player>>() {
            });

        } catch (IOException e) {
            log.error("Error reading file: " + fileName + " | " + e.getMessage());
            log.error("It might be empty");
            return new ArrayList<>();
        }
    }


    @Override
    public List<Player> getPlayerJsonList(String name) {
        return getPlayerList(name);
    }

    @Override
    public Collection<Player> collateAllPlayersFromList() {
        Collection<Player> allPlayers = new HashSet<>();

        // Add all the players to the above collection.
        getAllLists().forEach(file -> allPlayers.addAll(getPlayerJsonList(file)));

        return allPlayers;
    }

    @Override
    public Player findOne(Long assetId) {
        for (Player p : findAllPlayers()) {
            if (p.getAssetId().equals(assetId)) {
                return p;
            }
        }
        return null;
    }

    @Override
    public void saveList(String name, List<Player> listToSave) throws FutException {

        if (getPlayerList(name) == null) {
            throw new FutException("No list with name: " + name);
        }

        if (!fileUtils.writePlayerListToFile(listToSave, name)) {
            log.error("Unable to save list");
        }
    }

    @Override
    public List<Player> addPlayer(String list, Player player) throws FutErrorException {
        // Get player list
        list = list + FileUtils.jsonExt;
        List<Player> playerList = getPlayerList(list);

        if (playerList != null) {
            // check if player exists in the list
            for (Player p : playerList) {
                if (p.getAssetId().equals(player.getAssetId())) {
                    String msg = "Player already exists";
                    throw new FutErrorException(new FutError(msg, FutErrorCode.InternalServerError, msg, "", ""));
                }
            }

            playerList.add(player);
            // Save list now
            try {
                saveList(list, playerList);
            } catch (FutException e) {
                log.error("unable to save new list: " + e.getMessage());
            }

        } else {
            log.error("List {} not found", list);
            return null;
        }

        return playerList;
    }

    @Override
    public List<Player> updatePlayer(String list, Player player) {
        list = list + ".json";
        List<Player> listContainingPlayer = getPlayerList(list);

        if (listContainingPlayer != null) {
            listContainingPlayer.listIterator().forEachRemaining(p -> {
                if (Objects.equals(p.getAssetId(), player.getAssetId())) {
                    p.setFirstName(player.getFirstName());
                    p.setLastName(player.getLastName());
                    p.setCommonName(player.getCommonName());
                    p.setRating(player.getRating());
                    p.setAssetId(player.getAssetId());
                    p.setPosition(player.getPosition());
                    p.setLowestBin(player.getLowestBin());
                    p.setSearchPrice(player.getSearchPrice());
                    p.setMaxListPrice(player.getMaxListPrice());
                    p.setMinListPrice(player.getMinListPrice());
                    p.setNation(player.getNation());
                    p.setClub(player.getClub());
                    p.setLeague(player.getLeague());
                    p.setPriceSet(player.isPriceSet());
                    p.setCustomPrice(player.isCustomPrice());
                    p.setBidAmount(player.getBidAmount());
                }
            });
        } else {
            log.error("Cannot find file {}", list);
            return null;
        }
        try {
            saveList(list, listContainingPlayer);
            log.info("Updated player list.");
        } catch (FutException e) {
            log.error("unable to save new list: " + e.getMessage());
        }

        return listContainingPlayer;
    }

    @Override
    public List<Player> deletePlayer(String list, Long id) {
        list = list + ".json";
        List<Player> listContainingPlayer = getPlayerList(list);

        if (listContainingPlayer != null) {

            listContainingPlayer.removeIf(p -> p.getAssetId().equals(id));

            try {
                saveList(list, listContainingPlayer);
            } catch (FutException e) {
                log.error("unable to save new list: " + e.getMessage());
            }

            return listContainingPlayer;
        } else {
            log.error("Cannot find file {}", list);
            return null;
        }
    }

    @Override
    public List<Player> findAllPlayers() {
        ObjectMapper mapper = new ObjectMapper();

        try {
            File EAplayersJSONfile = new File(DIR + "/EAplayers.json");

            if (!EAplayersJSONfile.exists()) {
                copyEAJSON(EAplayersJSONfile);
            }
            Object obj = mapper.readValue(EAplayersJSONfile, Object.class);

            // Objects because jackson cannot map to Player object.
            List<Object> players = (List<Object>) ((LinkedHashMap) obj).get("Players");
            players.addAll((List<Object>) ((LinkedHashMap) obj).get("LegendsPlayers"));

            List<Player> fullPlayers = new ArrayList<>();

            // Iterate and get each json object and map it to the player object.
            for (Object p : players) {
                Player player = new Player();
                Object c = ((LinkedHashMap) p).get("c");

                player.setCommonName(c == null ? "" : c.toString());
                player.setFirstName(((LinkedHashMap) p).get("f").toString());
                player.setLastName(((LinkedHashMap) p).get("l").toString());
                // NOTE: This has been now removed from the player.json
                // player.setNation(Integer.valueOf(((LinkedHashMap) p).get("n").toString()));
                player.setRating(Integer.valueOf(((LinkedHashMap) p).get("r").toString()));
                player.setAssetId(Long.valueOf(((LinkedHashMap) p).get("id").toString()));

                player.setAssetId(LongExtensions.CalculateBaseId(player.getAssetId()));

                if (!player.getFirstName().equals("Alex") && !player.getLastName().equals("Hunter") && !notAlreadyAdded(player, fullPlayers)) {
                    fullPlayers.add(player);
                }
            }

            return fullPlayers;
        } catch (IOException e) {
            log.error("Error getting all players from JSON: " + e.getMessage());
            return null;
        }
    }

    /**
     * Copy EAplayers.json
     *
     * @param EAplayersJSONfile location of the file
     */
    private void copyEAJSON(File EAplayersJSONfile) {
        // Copy of EAPlayer.json
        try {
            // File to copy
            File decodedPath = new File(Objects.requireNonNull(this.getClass().getClassLoader().getResource("EAplayers.json")).getFile());
            File eaJson = new File(URLDecoder.decode(decodedPath.getPath(), "UTF-8"));

            if (eaJson.exists()) {

                if (EAplayersJSONfile.exists()) {
                    log.info("EAplayers.json already exists");
                } else {
                    org.apache.commons.io.FileUtils.copyFile(eaJson, EAplayersJSONfile);

                    log.info("Copied EAPlayers.json: {}", EAplayersJSONfile.exists());
                }
            } else {
                log.error("Unable to find EAplayers.json @ ", eaJson.getAbsolutePath());
            }
        } catch (Exception ex) {
            log.error("Error moving EAPlayers.json: {}", ex);
        }
    }

    /**
     * Checks if player already added to list
     *
     * @param player      Player to check
     * @param fullPlayers list of player to check against.
     * @return false if not found otherwise true.
     */
    private boolean notAlreadyAdded(Player player, List<Player> fullPlayers) {
        for (Player p : fullPlayers) {
            if (Objects.equals(p.getAssetId(), player.getAssetId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Player getPlayer(Long assetId, List<Player> list) {
        for (Player player : list) {
            if (Objects.equals(player.getAssetId(), assetId)) {
                return player;
            }
        }
        return null;
    }

    @Override
    public boolean resetPriceSet(String list) {
        List<Player> listContainingPlayer = getPlayerList(list);

        if (listContainingPlayer != null) {
            listContainingPlayer.listIterator().forEachRemaining(p -> p.setPriceSet(false));

            try {
                saveList(list, listContainingPlayer);
                return true;
            } catch (FutException e) {
                log.error("unable to save new list: " + e.getMessage());
                return false;
            }
        } else {
            log.error("Cannot find list {}", list);
            return false;
        }
    }

    @Override
    public Boolean deleteList(String listName) {
        Boolean result = FileUtils.deleteFile(FileUtils.player_path + File.separator + listName + FileUtils.jsonExt);

        if (result == null) {
            log.info("No file to delete.");
            return null;
        } else if (result) {
            log.info("Delete list {}  successfully", listName);
        } else {
            log.error("Unable to delete file.");
        }

        return result;
    }
}

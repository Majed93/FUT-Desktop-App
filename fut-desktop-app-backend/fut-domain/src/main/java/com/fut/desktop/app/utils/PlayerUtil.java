package com.fut.desktop.app.utils;

import com.fut.desktop.app.restObjects.Player;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class PlayerUtil {


    /**
     * Helper method to get name.
     *
     * @return player name.
     */
    public static String getName(Player player) {
        return player.getCommonName().isEmpty() ?
                player.getFirstName() + " " + player.getLastName()
                : player.getCommonName();
    }
}

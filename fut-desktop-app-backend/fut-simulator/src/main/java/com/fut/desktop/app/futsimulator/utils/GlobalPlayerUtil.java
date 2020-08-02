package com.fut.desktop.app.futsimulator.utils;

import com.fut.desktop.app.restObjects.Player;
import com.fut.desktop.app.utils.SleepUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public final class GlobalPlayerUtil {

    private static List<Player> players = new ArrayList<>();

    public static List<Player> getPlayersList() {
        return players;
    }

    static void setPlayersList(List<Player> playersList) {
        players = playersList;
    }

    public static Player findOne(Long assetId) {
        return players.stream().filter(p -> p.getAssetId().equals(assetId)).
                findFirst().orElseThrow(() -> new NullPointerException("Couldn't find player with id: " + assetId));
    }

    public static long randomAssetId() {
        return GlobalPlayerUtil.getPlayersList().
                get(SleepUtil.random(0, GlobalPlayerUtil.getPlayersList().size() - 1)).getAssetId();
    }


}

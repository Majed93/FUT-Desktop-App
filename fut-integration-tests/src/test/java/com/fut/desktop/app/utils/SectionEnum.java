package com.fut.desktop.app.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enums to be used by the tests to identify each section. These will only be the part without the 'app-' on it.
 */
@Getter
@AllArgsConstructor
public enum SectionEnum {
    Root("root"),
    Alert("alert"),
    InputFilter("input-filters"),
    Loader("loader"),
    Login("login"),
    Main("main"),
    Accounts("accounts"),
    AutoBid("auto-bid"),
    PlayerCard("player-card"),
    PlayerMgmt("players-mgmt"),
    PlayerList("players-list"),
    PlayerPrice("player-price"),
    TradePile("trade-pile"),
    TransferSearch("transfer-search"),
    Unassigned("unassigned"),
    WatchList("watch-list"),
    PlayerFilter("player-filter"),
    Sidebar("sidebar"),
    StatusModal("status-modal");

    private String section;
}

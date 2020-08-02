package com.fut.desktop.app.futsimulator.constants;

import com.fut.desktop.app.constants.Resources;

/**
 * Reusable rest constants
 */
public final class RestConstants {

    public final static String MOCK_IO = "/mock/io";
    public final static String TRAIN = "/train/";

    public final static String UTAS = "/utas.external.*fut.ea.com/ut/game/fifa" + Resources.FUT_YEAR + "/";
    public static final String MARKET = "/market";
    public static final String SEARCH = "/search";

    private static final String PRICE_RANGE = "priceRange";

    public static final String MIN_PRICE_RANGE = PRICE_RANGE + "/min";
    public static final String MAX_PRICE_RANGE = PRICE_RANGE + "/max";

    public static final String COINS = "coins";

    public static final String PILE = "pile";

    public static final String TRADEPILE = "tradePile";
    public static final String WATCHLIST = "watchList";
    public static final String UNASSIGNEDPILE = "unassigned";

    public static final String UNLISTED = "unlisted";
    public static final String SOLD = "sold";
    public static final String EXPIRED = "expired";
    public static final String WON = "won";
    public static final String OUTBIDDED = "outbidded";
    public static final String WINNING = "winning";
}

package com.fut.desktop.app.futservice.constants;

/**
 * Constants for Web socket endpoints
 */
public final class WSUrl {
    public final static String PLAYERSEARCH_START = "/playerSearch/start";
    public final static String PLAYERSEARCH_STATUS = "/playerSearch/status";
    public final static String PLAYERLISTING_STATUS = "/playerListing/status";
    public final static String AUTOBID_TOOMANY_MODAL = "/autoBid/modal";
    public final static String ACTION_NEEDED = "/manual/actionNeeded";

    /**
     * Constructor.
     */
    private WSUrl() {
        // NOOP
    }
}

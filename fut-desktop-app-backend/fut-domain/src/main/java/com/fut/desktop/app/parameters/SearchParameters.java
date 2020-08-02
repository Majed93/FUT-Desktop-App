package com.fut.desktop.app.parameters;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class SearchParameters {
    private final static byte DefaultPageSize = 35; // This is read in from remoteConfig.

    private String type;

    private long page;

    private long league;

    private String position;

    private long nation;

    private ChemistryStyle chemistryStyle;

    private long team;

    private long minBuy;

    private long maxBuy;

    private long minBid;

    private long maxBid;

    private Level level;

    private String developmentType;

    private String trainingType;

    private String clubInfoType;

    private String staffType;

    private long resourceId;

    private byte pageSize;

    public SearchParameters(ResourceType resourceType) {
        type = resourceType.toString().toLowerCase();
        page = 1;
        pageSize = DefaultPageSize;
    }

    public abstract String BuildUriString(String uriString);

    String setUriLevel(String uriString) {
        if (getLevel() != null) {
            switch (getLevel()) {
                case Any:
                    return uriString;
                case Bronze:
                case Silver:
                case Gold:
                    uriString += "&lev=" + getLevel().toString().toLowerCase();
                    return uriString;
                case Special:
                    uriString += "&rare=SP";
                    return uriString;
                default:
                    throw new IllegalArgumentException("Level");
            }
        }
        return uriString;
    }
}

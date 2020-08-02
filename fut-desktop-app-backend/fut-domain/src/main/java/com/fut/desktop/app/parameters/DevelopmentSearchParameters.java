package com.fut.desktop.app.parameters;

public class DevelopmentSearchParameters extends SearchParameters {
    public DevelopmentSearchParameters() {
        super(ResourceType.Development);
    }

    @Override
    public String BuildUriString(String uriString) {

        uriString = setUriLevel(uriString);

        uriString += "&cat=" + getDevelopmentType().toLowerCase();

        uriString += "&type=" + getType().toLowerCase();

        if (getMinBuy() > 0) {
            uriString += "&minb=" + getMinBuy();
        }
        if (getMaxBuy() > 0) {
            uriString += "&maxb=" + getMaxBuy();
        }
        if (getMinBid() > 0) {
            uriString += "&micr=" + getMinBid();
        }
        if (getMaxBid() > 0) {
            uriString += "&macr=" + getMaxBid();
        }

        return uriString;
    }
}

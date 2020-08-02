package com.fut.desktop.app.parameters;

public class ClubInfoSearchParameters extends SearchParameters {
    protected ClubInfoSearchParameters() {
        super(ResourceType.ClubInfo);
    }

    @Override
    public String BuildUriString(String uriString) {

        uriString = setUriLevel(uriString);

        if (getLeague() != 0) {
            uriString += "&leag=" + getLeague();
        }
        if (getTeam() != 0) {
            uriString += "&team=" + getTeam();
        }
        uriString += "&cat=" + getClubInfoType().toLowerCase();

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

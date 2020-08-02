package com.fut.desktop.app.parameters;

import java.util.Objects;

public class StaffSearchParameters extends SearchParameters {
    public StaffSearchParameters() {
        super(ResourceType.Staff);
    }

    @Override
    public String BuildUriString(String uriString) {

        uriString = setUriLevel(uriString);

        // only the manager type has Nation category
        if (getNation() != 0 && Objects.equals(getStaffType(), "manager")) {
            uriString += "&nat=" + getNation();
        }
        // only the manager type has League category
        if (getLeague() != 0 && Objects.equals(getStaffType(), "manager")) {
            uriString += "&leag=" + getLeague();
        }

        uriString += "&cat=" + getStaffType();

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

package com.fut.desktop.app.parameters;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrainingSearchParameters extends SearchParameters {


    public TrainingSearchParameters() {
        super(ResourceType.Training);
    }

    @Override
    public String BuildUriString(String uriString) {
        uriString = setUriLevel(uriString);

        uriString += "&cat=" + getTrainingType();

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
        if (getChemistryStyle() != null && getChemistryStyle().getChemistryStyle() > 0) {
            uriString += "&playStyle=" + getChemistryStyle().getChemistryStyle();
        }

        return uriString;
    }
}

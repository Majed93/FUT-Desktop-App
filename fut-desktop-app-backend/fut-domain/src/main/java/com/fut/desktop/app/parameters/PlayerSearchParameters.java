package com.fut.desktop.app.parameters;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fut.desktop.app.extensions.LongExtensions;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;


@Getter
@Setter
@JsonFormat
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerSearchParameters extends SearchParameters {
    public PlayerSearchParameters() {
        super(ResourceType.Player);
    }

    @Override
    public String BuildUriString(String uriString) {

        if (getType() != null) {
            uriString += "&type=" + getType().toLowerCase();
        }

        if (getPosition() != null) {
            uriString += SetPlayerPosition(uriString);
        }

        // Set level
        uriString = setUriLevel(uriString);

        if (getResourceId() != 0) {
            if (getResourceId() <= 16777216) {
                uriString += "&maskedDefId=" + LongExtensions.CalculateBaseId(getResourceId());
            } else {
                uriString += "&definitionId=" + getResourceId();
            }
        } else {
            if (getNation() > 0) {
                uriString += "&nat=" + getNation();
            }

            if (getLeague() > 0) {
                uriString += "&leag=" + getLeague();
            }

            if (getTeam() > 0) {
                uriString += "&team=" + getTeam();
            }
        }

        if (getChemistryStyle() != null && getChemistryStyle() != ChemistryStyle.All) {
            uriString += "&playStyle=" + getChemistryStyle().getChemistryStyle();
        }

        if (getMinBid() > 0) {
            uriString += "&micr=" + getMinBid();
        }

        if (getMaxBid() > 0) {
            uriString += "&macr=" + getMaxBid();
        }

        if (getMinBuy() > 0) {
            uriString += "&minb=" + getMinBuy();
        }

        if (getMaxBuy() > 0) {
            uriString += "&maxb=" + getMaxBuy();
        }

        return uriString;
    }

    private String SetPlayerPosition(String uriString) {
        if (!getPosition().isEmpty())
            uriString += (
                    Objects.equals(getPosition(), Position.Defenders) ||
                            Objects.equals(getPosition(), Position.Midfielders) ||
                            Objects.equals(getPosition(), Position.Attackers)
                            ? "&zone="
                            : "&pos=")
                    + getPosition();

        return uriString;
    }
}

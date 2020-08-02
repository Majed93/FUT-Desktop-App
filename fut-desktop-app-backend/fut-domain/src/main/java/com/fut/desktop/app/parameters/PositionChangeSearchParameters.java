package com.fut.desktop.app.parameters;

import com.fut.desktop.app.constants.PosChangeEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class PositionChangeSearchParameters extends SearchParameters {
    public PositionChangeSearchParameters() {
        super(ResourceType.Training);
    }

    @Override
    public String BuildUriString(String uriString) {

        // Sample string:
        // Request	POST /ut/game/fifa14/transfermarket?start=0&pos=LWB%2DLB&type=training&cat=position&num=16&lev=gold
        // Quality level doesn't make sense since all position change cards are gold

        uriString = setUriLevel(uriString);

        uriString += "&cat=position";

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
        if (!Objects.equals(getPosition(), "")) {
            uriString += "&pos=" + EncodePosString(getPosition());
        }

        return uriString;
    }

    /**
     * Url encoding the position change string
     *
     * @param poschange >a valid position change string
     * @return position
     */
    private static String EncodePosString(String poschange) {
        PosChangeEnum pc = Enum.valueOf(PosChangeEnum.class, poschange);
        if (pc.toString().isEmpty()) {
            throw new IllegalArgumentException("Position Change Parameter is invalid");
        }

        return poschange.replaceAll("_", "%2D");
    }
}

package com.fut.desktop.app.futsimulator.dto;

import lombok.*;

/**
 * The DTO used to trained the simulator for market search
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MarketSearchRequest {

    /**
     * Nullable
     */
    Long assetId;

    /**
     * Examples: highest, buyNow, outbid, none
     */
    String bidState;

    /**
     * Example: closed, active
     */
    String tradeState;

    Integer currentBid;

    Integer startingBid;

    Integer buyNowPrice;

    Integer expires;
}

package com.fut.desktop.app.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Wrapper containing all objects from home hub page
 */
@Getter
@Setter
@Slf4j
@AllArgsConstructor
public class HomeWrapper {

    /**
     * Trade pile response
     */
    private AuctionResponse tradePile;

    /**
     * Unassigned pile response
     */
    private AuctionResponse unassignedItems;
}

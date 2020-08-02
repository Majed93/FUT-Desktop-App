package com.fut.desktop.app.restObjects;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@JsonFormat
@Getter
@Setter
@AllArgsConstructor
public class ProfitMargin {

    /**
     * Key of the max amount for this filter to apply on.
     */
    Integer key;

    /**
     * Minimum profit margin
     */
    Long minProfit;

    /**
     * Maximum profit margin
     */
    Long maxProfit;

    /**
     * Choice of how to set the prices
     * <p>
     * 0 - Less than lowest bin.
     * 1 - Between lowest bin.
     * 2 - On and greater than lowest bin.
     * </p>
    */
    Integer priceSetChoice;
}

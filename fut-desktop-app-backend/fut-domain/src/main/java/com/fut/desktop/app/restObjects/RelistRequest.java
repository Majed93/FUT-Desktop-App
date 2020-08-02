package com.fut.desktop.app.restObjects;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fut.desktop.app.domain.AuctionDuration;
import com.fut.desktop.app.domain.AuctionResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@JsonFormat
@Getter
@Setter
@AllArgsConstructor
public class RelistRequest {

    /**
     * Id of account
     */
    private Integer id;

    /**
     * Auction duration
     */
    private AuctionDuration auctionDuration;

    /**
     * Auction response.
     */
    private AuctionResponse auctionResponse;

    /**
     * New price. True = list using new price from lists where applicable. False = keep the same.
     */
    private Boolean newPrice;

    /**
     * If check which pile.
     */
    private String pile;
}

package com.fut.desktop.app.restObjects;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fut.desktop.app.domain.AuctionResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Used by unassigned pile AND watch list
 */
@JsonFormat
@Getter
@Setter
@AllArgsConstructor
@ToString
public class WatchListRequest {

    /**
     * Id of account
     */
    private Integer id;

    /**
     * Watch list response
     */
    private AuctionResponse auctionResponse;

    /**
     * true = remove only expired, false = remove expired and outbidded.
     */
    private Boolean removeExpired;

    /**
     * Which pile are we moving FROM.
     * watchlist
     * unassigned
     */
    private String pile;
}

package com.fut.desktop.app.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JsonFormat
@NoArgsConstructor
public class AuctionDetails {

    private long itemDataId;

    private AuctionDuration auctionDuration;

    private long startingBid;

    private long buyNowPrice;

    public AuctionDetails(long itemDataId, AuctionDuration auctionDuration, long startingBid, long buyNowPrice) throws Exception {
        if(itemDataId < 1) {
            throw new IllegalArgumentException("AuctionDetails: Invalid itemDataId.");
        }

        if(startingBid < 150) {
            throw new IllegalArgumentException("AuctionDetails: Invalid startingBid.");
        }

        if(buyNowPrice != 0 && buyNowPrice < startingBid) {
            throw new IllegalArgumentException("AuctionDetails: Invalid buyNowPrice.");
        }

        this.itemDataId = itemDataId;
        this.auctionDuration = auctionDuration;
        this.startingBid = startingBid;
        this.buyNowPrice = buyNowPrice;

    }
}

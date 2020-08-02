package com.fut.desktop.app.restObjects;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fut.desktop.app.domain.AuctionInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonFormat
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BidItemRequest {

    /**
     * Id of account
     */
    Integer id;

    /**
     * Auction to process.
     */
    AuctionInfo auctionInfo;

    /**
     * Amount to bid.
     */
    Long amount;

    /**
     * True = BIN the item. False = Normally bid.
     */
    Boolean bin;
}

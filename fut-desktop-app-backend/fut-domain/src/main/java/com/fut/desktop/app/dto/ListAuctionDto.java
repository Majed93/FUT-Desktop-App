package com.fut.desktop.app.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Object to send when listing an item.
 */
@Getter
@Setter
@JsonFormat
@AllArgsConstructor
@NoArgsConstructor
public class ListAuctionDto {

    /**
     * Item data.
     */
    private ListAuctionItemDataDto itemData;

    /**
     * Starting bid.
     */
    private long startingBid;

    /**
     * Duration.
     */
    private long duration;

    /**
     * Buy now price.
     */
    private long buyNowPrice;
}

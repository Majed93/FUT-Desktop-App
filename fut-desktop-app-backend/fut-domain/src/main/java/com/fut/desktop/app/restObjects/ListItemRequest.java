package com.fut.desktop.app.restObjects;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fut.desktop.app.domain.AuctionDetails;
import com.fut.desktop.app.domain.AuctionResponse;
import lombok.*;

@JsonFormat
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ListItemRequest {

    /**
     * Id of account
     */
    Integer id;

    /**
     * Auction details.
     */
    AuctionDetails auctionDetails;

    /**
     * List object if watchlist or unassigned
     */
    AuctionResponse auctionResponse;
}

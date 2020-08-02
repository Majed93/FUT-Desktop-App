package com.fut.desktop.app.restObjects;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

/**
 * Bid criteria request.
 */
@JsonFormat
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BidCriteria {

    /**
     * The time limit of the item
     */
    private Long timeLimit;

    /**
     * The minimum buy now to search with
     */
    private Integer minBuyNow;

    /**
     * The maximum number of items before displaying a warning.
     */
    private Integer maxResults;

    /**
     * If true then bidding will only BIN and no BID
     */
    private Boolean binOnly;

    /**
     * If true then will BIN first and then BID
     */
    private Boolean binFirst;

    /**
     * If true then will only bid.
     */
    private Boolean bid;
}

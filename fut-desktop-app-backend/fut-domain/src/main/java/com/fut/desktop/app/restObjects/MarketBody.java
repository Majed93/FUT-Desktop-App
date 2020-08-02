package com.fut.desktop.app.restObjects;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fut.desktop.app.parameters.Level;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonFormat
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
// NOTE: Not finished. More parameters can be added to this.
// NOTE: Update frontend models if this updates
public class MarketBody {

    /**
     * Account id.
     */
    private Integer accountId;

    /**
     * Type of item.
     * <p>
     * 'player'
     * 'consumable'
     * </p>
     */
    private String itemType;

    /**
     * Asset id of item.
     */
    private Long assetId;

    /**
     * Min bid of item.
     */
    private Integer minBid;

    /**
     * Max bid of item.
     */
    private Integer maxBid;

    /**
     * Min buy of item.
     */
    private Integer minBuy;

    /**
     * Max buy of item
     */
    private Integer maxBuy;

    /**
     * Level of searching.
     */
    private Level level;

}

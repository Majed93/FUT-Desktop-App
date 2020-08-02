package com.fut.desktop.app.restObjects;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonFormat
@Getter
@Setter
@AllArgsConstructor
public class UpdatePriceBody {

    /**
     * The list used
     */
    String list;

    /**
     * List of players
     */
    List<Player> players;

    /**
     * Filers for profit margins
     */
    List<ProfitMargin> profitMargins;

    /**
     * Minimum amount to bid.
     */
    Long minBid;
}

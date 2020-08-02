package com.fut.desktop.app.restObjects;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

/**
 * Domain object for Player.
 * <p>
 * Note: When adding fields the PlayerService implementation(s) <b>MUST</b> be updated.
 */

@Getter
@Setter
@JsonFormat
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Player {

    /**
     * First name of player
     */
    String firstName;

    /**
     * Last name of player
     */
    String lastName;

    /**
     * Common name used to refer to player
     */
    String commonName;

    /**
     * Player rating
     */
    Integer rating;

    /**
     * Unique ID determined by EA.
     */
    Long assetId;

    /**
     * Player position
     */
    String position;

    /**
     * Lowest BIN price of the player
     */
    Long lowestBin;

    /**
     * Used when searching to mass bid. MUST BE LOWER THAN LOWEST BIN IN MOST CASES
     */
    Long searchPrice;

    /**
     * Max price to list for
     */
    Long maxListPrice;

    /**
     * Min price to list for
     */
    Long minListPrice;

    /**
     * Player's nation
     */
    Integer nation;

    /**
     * Player's club
     */
    Integer club;

    /**
     * League
     */
    Integer league;

    /**
     * If custom price has been set or not. This will ensure that when searching this player's price is not overridden
     */
    boolean customPrice;

    /**
     * If while searching for lowest bin player's price was set. False if not found. True if found.
     */
    boolean priceSet;

    /**
     * Amount to bid on
     */
    Long bidAmount;
}

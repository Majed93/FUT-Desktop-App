package com.fut.desktop.app.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fut.desktop.app.extensions.LongExtensions;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@JsonFormat
@ToString
public class AuctionInfo {

    private final static long MINBID = 150;

    private String bidState;

    private long buyNowPrice;

    private long currentBid;

    private int expires;

    private ItemData itemData;

    private long offers;

    private String sellerEstablished;

    private long sellerId;

    private Boolean tradeOwner;

    private String sellerName;

    private long startingBid;

    private byte confidenceValue;

    private long tradeId;

    private String tradeIdStr;

    private String tradeState;

    private Boolean watched;

    private long coinsProcessed;

    private String itemType;

    public long CalculateBid() {
        return currentBid == 0 ? startingBid : CalculateNextBid(currentBid);
    }

    public static long CalculateNextBid(long currentBid) {
        if (currentBid == 0)
            return MINBID;

        if (currentBid < 1000)
            return currentBid + 50;

        if (currentBid < 10000)
            return currentBid + 100;

        if (currentBid < 50000)
            return currentBid + 250;

        if (currentBid < 100000)
            return currentBid + 500;

        return currentBid + 1000;
    }

    public static long CalculatePreviousBid(long currentBid) {
        if (currentBid <= MINBID)
            return 0;

        if (currentBid <= 1000)
            return currentBid - 50;

        if (currentBid <= 10000)
            return currentBid - 100;

        if (currentBid <= 50000)
            return currentBid - 250;

        if (currentBid <= 100000)
            return currentBid - 500;

        return currentBid - 1000;
    }

    /**
     * Round (up) to the nearest acceptable value.
     *
     * NOTE: In future will need to cast {amount} otherwise it'll always round down..
     *
     * @param amount Amount to round.
     * @return rounded amount.
     */
    public static long roundToNearest(long amount) {
        if (amount == 0) {
            return amount;
        } else if (amount < 150) {
            return 150;
        } else if (amount < 1000) {
            return (long) Math.ceil((amount / 50) * 50);
        } else if (amount < 10000) {
            return (long) Math.ceil((amount / 100) * 100);
        } else if (amount < 50000) {
            return (long) Math.ceil((amount / 250) * 250);
        } else if (amount < 100000) {
            return (long) Math.ceil((amount / 500) * 500);
        }
        return (long) Math.ceil((amount / 1000) * 1000);
    }

    /**
     * Return factor of which the bid amounts increase in.
     *
     * @param amount Amount to check.
     * @return Increment of bids.
     */
    public static long factor(long amount) {
        if (amount == 0) {
            return 0;
        } else if (amount < 150) {
            return 0;
        } else if (amount < 1000) {
            return 50;
        } else if (amount < 10000) {
            return 100;
        } else if (amount < 50000) {
            return 250;
        } else if (amount < 100000) {
            return 500;
        }
        return 1000;
    }

    //Long extension
    public long CalculateBaseId() {
        return LongExtensions.CalculateBaseId(itemData.getResourceId());
    }
}

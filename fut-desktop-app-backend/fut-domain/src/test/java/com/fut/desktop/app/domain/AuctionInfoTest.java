package com.fut.desktop.app.domain;

import org.junit.Assert;
import org.junit.Test;

public class AuctionInfoTest {
    @Test
    public void testRoundToNearest() {
        long amount = 123;
        Assert.assertEquals(150, AuctionInfo.roundToNearest(amount));

        amount = 840;
        Assert.assertEquals(800, AuctionInfo.roundToNearest(amount));

        amount = 2340;
        Assert.assertEquals(2300, AuctionInfo.roundToNearest(amount));

        amount = 12450;
        Assert.assertEquals(12250, AuctionInfo.roundToNearest(amount));

        amount = 46875;
        Assert.assertEquals(46750, AuctionInfo.roundToNearest(amount));

        amount = 86400;
        Assert.assertEquals(86000, AuctionInfo.roundToNearest(amount));

        amount = 101100;
        Assert.assertEquals(101000, AuctionInfo.roundToNearest(amount));

        amount = 150;
        Assert.assertEquals(150, AuctionInfo.roundToNearest(amount));

        amount = 950;
        Assert.assertEquals(950, AuctionInfo.roundToNearest(amount));

        amount = 2200;
        Assert.assertEquals(2200, AuctionInfo.roundToNearest(amount));

        amount = 16750;
        Assert.assertEquals(16750, AuctionInfo.roundToNearest(amount));

        amount = 47750;
        Assert.assertEquals(47750, AuctionInfo.roundToNearest(amount));

        amount = 79000;
        Assert.assertEquals(79000, AuctionInfo.roundToNearest(amount));

        amount = 151000;
        Assert.assertEquals(151000, AuctionInfo.roundToNearest(amount));
    }

    @Test
    public void testFactor() {
        long amount = 0;
        Assert.assertEquals(0, AuctionInfo.factor(amount));

        amount = 100;
        Assert.assertEquals(0, AuctionInfo.factor(amount));

        amount = 850;
        Assert.assertEquals(50, AuctionInfo.factor(amount));

        amount = 6700;
        Assert.assertEquals(100, AuctionInfo.factor(amount));

        amount = 35000;
        Assert.assertEquals(250, AuctionInfo.factor(amount));

        amount = 65000;
        Assert.assertEquals(500, AuctionInfo.factor(amount));

        amount = 110000;
        Assert.assertEquals(1000, AuctionInfo.factor(amount));
    }

}
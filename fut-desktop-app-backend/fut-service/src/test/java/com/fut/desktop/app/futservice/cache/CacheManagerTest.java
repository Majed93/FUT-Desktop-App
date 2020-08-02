package com.fut.desktop.app.futservice.cache;

import com.fut.desktop.app.domain.PriceRange;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.LinkedHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.Silent.class)
@SpringBootTest
public class CacheManagerTest {

    @InjectMocks
    private CacheManager cacheManager = new CacheManager(2, TimeUnit.SECONDS);

    @Before
    public void setUp() {
        cacheManager.start();
    }

    @After
    public void tearDown() throws Exception {
        cacheManager.stop();
    }

    @Test
    public void testStart() {
        cacheManager.start();

        Assert.assertTrue(cacheManager.getStarted());
    }

    @Test
    public void testStop() throws Exception {
        cacheManager.stop();
        Assert.assertFalse(cacheManager.getStarted());
    }

    @Test
    public void testRetrieveCache() {
        PriceRange priceRange = cacheManager.retrieveCache(1234L);

        Assert.assertNull(priceRange);
    }

    @Test
    public void testAddToCache() {
        PriceRange priceRange = new PriceRange();
        priceRange.setDefId(123456789);
        priceRange.setSource("ITEM_DEFINITION");
        priceRange.setItemId(1234);
        priceRange.setMinPrice(150);
        priceRange.setMaxPrice(10000);


        PriceRange addedPriceRange = cacheManager.addToCache(priceRange);

        Assert.assertNotNull(addedPriceRange);
        Assert.assertEquals(priceRange, addedPriceRange);
    }

    @Test
    public void testRetrieveCacheAlreadyAdded() {
        PriceRange priceRange = new PriceRange();
        priceRange.setDefId(123456789);
        priceRange.setSource("ITEM_DEFINITION");
        priceRange.setItemId(1234);
        priceRange.setMinPrice(150);
        priceRange.setMaxPrice(10000);


        PriceRange addedPriceRange = cacheManager.addToCache(priceRange);
        PriceRange retrievedPriceRange = cacheManager.retrieveCache(priceRange.getDefId());

        Assert.assertNotNull(addedPriceRange);
        Assert.assertEquals(priceRange, addedPriceRange);

        Assert.assertNotNull(retrievedPriceRange);
        Assert.assertEquals(priceRange, retrievedPriceRange);
    }

    @Test
    public void testAddedCacheAlreadyExists() {
        PriceRange priceRange = new PriceRange();
        priceRange.setDefId(123456789);
        priceRange.setSource("ITEM_DEFINITION");
        priceRange.setItemId(1234);
        priceRange.setMinPrice(150);
        priceRange.setMaxPrice(10000);


        PriceRange addedPriceRange = cacheManager.addToCache(priceRange);
        PriceRange retrievedpriceRange = cacheManager.retrieveCache(priceRange.getDefId());
        PriceRange addAgainPriceRange = cacheManager.addToCache(priceRange);

        Assert.assertNotNull(addedPriceRange);
        Assert.assertEquals(priceRange, addedPriceRange);

        Assert.assertNotNull(retrievedpriceRange);
        Assert.assertEquals(priceRange, retrievedpriceRange);

        Assert.assertNotNull(addAgainPriceRange);
        Assert.assertEquals(priceRange, addAgainPriceRange);
    }

    @Test
    public void testCleanUp() throws Exception {
        PriceRange priceRange = new PriceRange();
        priceRange.setDefId(123456789);
        priceRange.setSource("ITEM_DEFINITION");
        priceRange.setItemId(1234);
        priceRange.setMinPrice(150);
        priceRange.setMaxPrice(10000);


        cacheManager.addToCache(priceRange);

        Thread.sleep(2500);

        Assert.assertTrue(cacheManager.getPriceRangeMap().isEmpty());
    }

    @Test(expected = Exception.class)
    public void testCleanUpThrowsException() throws Exception {
        PriceRange priceRange = new PriceRange();
        priceRange.setDefId(123456789);
        priceRange.setSource("ITEM_DEFINITION");
        priceRange.setItemId(1234);
        priceRange.setMinPrice(150);
        priceRange.setMaxPrice(10000);

        cacheManager.addToCache(priceRange);
        // Mocking a null pointer exception because hard to mock any other expection for this.
        cacheManager.setPriceRangeMap(null);
        Thread.sleep(2500);

        Assert.assertTrue(cacheManager.getPriceRangeMap().isEmpty());
    }

    @Test
    public void testGetSetExecutorService() {
        cacheManager.setExecutorService(mock(ScheduledExecutorService.class));
        Assert.assertNotNull(cacheManager.getExecutorService());
    }

    @Test
    public void testGetSetRunnable() {
        cacheManager.setRunnable(mock(Runnable.class));
        Assert.assertNotNull(cacheManager.getRunnable());
    }

    @Test
    public void testGetSetPriceRangeMap() {
        cacheManager.setPriceRangeMap(new LinkedHashMap<>());
        Assert.assertNotNull(cacheManager.getPriceRangeMap());
    }

    @Test
    public void testGetSetTimeout() {
        Assert.assertNotNull(cacheManager.getTimeout());
    }

    @Test
    public void testGetSetTimeUnit() {
        Assert.assertNotNull(cacheManager.getTimeUnit());
    }

    @Test
    public void testGetSetStarted() {
        cacheManager.setStarted(false);
        Assert.assertFalse(cacheManager.getStarted());
    }
}
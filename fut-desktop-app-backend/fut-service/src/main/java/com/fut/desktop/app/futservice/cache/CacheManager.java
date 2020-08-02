package com.fut.desktop.app.futservice.cache;

import com.fut.desktop.app.domain.PriceRange;
import com.fut.desktop.app.extensions.DateTimeExtensions;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Instance of a cache manager to cache requests.
 */
@Slf4j
@Getter
@Setter
public class CacheManager {

    /**
     * Task executor.
     */
    private ScheduledExecutorService executorService;

    /**
     * Runnable task.
     */
    private Runnable runnable;

    /**
     * Map of the cache price ranges.
     */
    private Map<Long, Map<Long, PriceRange>> priceRangeMap;

    /**
     * TTL of a single item in the map.
     */
    private final Integer timeout;

    /**
     * Time unit.
     */
    private final TimeUnit timeUnit;

    /**
     * If cache manager start or not.
     */
    private Boolean started;

    /**
     * Constructor
     *
     * @param timeout  Timeout period.
     * @param timeUnit Time unit.
     */
    public CacheManager(Integer timeout, TimeUnit timeUnit) {
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        started = false;
        priceRangeMap = new LinkedHashMap<>();
    }

    /**
     * Start cache process.
     * Will start listening to cache and cleaning up after timeout.
     */
    public void start() {
        log.info("Starting cache manager.");
        priceRangeMap = new LinkedHashMap<>();
        executorService = Executors.newSingleThreadScheduledExecutor();
        runnable = () -> {
            try {
                cleanUp();
                log.info("Done task.");
            } catch (Exception ex) {
                log.error("Error executing task. " + ex.getMessage());
            }
        };
        executorService.scheduleWithFixedDelay(runnable, timeout, timeout, timeUnit);
        started = true;
    }

    /**
     * Stop cache process
     *
     * @throws Exception Handle any errors.
     */
    public void stop() throws Exception {
        if (executorService != null) {
            if (started || !executorService.isShutdown() && !executorService.isTerminated()) {
                log.info("Stopping cache manager.");
                executorService.shutdownNow();
                executorService.awaitTermination(10, TimeUnit.SECONDS);
                started = false;
            }
        }
    }

    /**
     * Retrieve cached price range if exists.
     */
    public PriceRange retrieveCache(Long defId) {
        Map<Long, PriceRange> prMap = priceRangeMap.get(defId);

        // New items to map
        if (prMap == null || prMap.isEmpty()) {
            log.info("Cache empty.");
            return null;
        } else {
            // Return the cache one.
            Long firstValue = prMap.keySet().iterator().next();
            PriceRange cachePr = prMap.get(firstValue);

            // Create a new map with timeStamp
            Map<Long, PriceRange> longPriceRangeMap = new LinkedHashMap<>();
            Long newTime = DateTimeExtensions.ToUnixTime();
            longPriceRangeMap.put(newTime, cachePr);

            //Replace the current one.
            priceRangeMap.remove(defId);
            priceRangeMap.put(defId, longPriceRangeMap);
            log.info("Cache not empty. Updating with timestamp.", longPriceRangeMap);
            return priceRangeMap.get(defId).get(newTime);
        }
    }

    /**
     * Add to cache.
     */
    public PriceRange addToCache(PriceRange priceRange) {
        PriceRange cachePriceRange = retrieveCache(priceRange.getDefId());

        if (cachePriceRange != null) {
            return cachePriceRange;
        }

        Map<Long, PriceRange> newMap = new LinkedHashMap<>();
        newMap.put(DateTimeExtensions.ToUnixTime(), priceRange);
        priceRangeMap.put(priceRange.getDefId(), newMap);
        log.info("Adding to cache.");
        return priceRangeMap.get(priceRange.getDefId()).values().iterator().next();
    }

    /**
     * Clean up cache max TTL of cache.
     */
    private void cleanUp() {
        log.info("Cleaning cache manager.");

        Iterator<Long> iterator = priceRangeMap.keySet().iterator();

        while (iterator.hasNext()) {
            Long defId = iterator.next();
            Map<Long, PriceRange> longPriceRangeMap = priceRangeMap.get(defId);

            Long timeStamp = longPriceRangeMap.keySet().iterator().next();
            LocalDateTime lDTTimeStamp = DateTimeExtensions.FromUnixTime(String.valueOf(timeStamp));

            boolean lessThanTimeOut = DateTimeExtensions.isTimeWithAddedDurationBeforeNow(timeout, lDTTimeStamp, timeUnit);

            if (lessThanTimeOut) {
                // Clean up
                iterator.remove();
                log.info("Removing cache with key: " + defId);
            }
        }
    }
}

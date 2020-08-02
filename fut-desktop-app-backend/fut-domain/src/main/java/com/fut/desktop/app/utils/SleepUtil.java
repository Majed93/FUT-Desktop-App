package com.fut.desktop.app.utils;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@Slf4j
@Getter
@Setter
public final class SleepUtil {

    /**
     * Value from property file
     */
    private static Integer timeoutFactor;

    /**
     * Random generator
     */
    private static Random random = new Random();

    /**
     * Invoke random sleep
     *
     * @param min Min time to sleep in ms.
     * @param max Max time to sleep in ms.
     */
    public static void sleep(int min, int max) {
        try {
            Thread.sleep(random(min, max) * timeoutFactor);
        } catch (InterruptedException e) {
            log.error("Error sleeping.. " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Returns random number between two values.
     *
     * @param min Minimum number to return
     * @param max Maximum number to return.
     * @return Random value.
     */
    public static int random(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }

    public static void setTimeoutFactor(Integer timeoutFactor) {
        SleepUtil.timeoutFactor = timeoutFactor;
    }

    public static Integer getTimeoutFactor() {
        return SleepUtil.timeoutFactor;
    }
}

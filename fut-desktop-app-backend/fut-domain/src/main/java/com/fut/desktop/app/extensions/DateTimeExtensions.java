package com.fut.desktop.app.extensions;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * ALL TIME USED HERE MUST BE UTC TIME ZONE
 */
public final class DateTimeExtensions {

    private final static ZoneOffset ZONE = OffsetDateTime.now().getOffset();
    private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);

    public static void ThrowIfInvalidArgument(LocalDateTime input) {
        if (input == null) {
            throw new IllegalArgumentException("Invalid dateTime.");
        }
    }

    /**
     * Convert local date time to unix timestamp.[Time] -> 1501432596
     *
     * @return unix timestamp
     */
    public static long ToUnixTime() {
        return Clock.systemUTC().millis();
    }

    /**
     * LocalDateTime to formatted time ISO8601
     *
     * @param input localDateTime.
     * @return formatted LocalDateTime.
     */
    public static String ToISO8601Time(LocalDateTime input) {
        return FORMATTER.format(input) + "Z";
    }

    /**
     * 1501432596 to localDateTime format
     *
     * @param unixTime e.g 1508695126131
     * @return localDateTime.
     */
    public static LocalDateTime FromUnixTime(String unixTime) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(unixTime)), ZONE);
    }

    public static long FromLocalDateTimeString(String dateTime) {
        Instant instant = Instant.parse(dateTime);
        return instant.toEpochMilli();
    }

    /**
     * Check if provided time is less than X minutes
     *
     * @return true if the given time is before the current with the timeDuration added to it.
     */
    public static boolean isTimeWithAddedDurationBeforeNow(int timeDuration, LocalDateTime localDateTime, TimeUnit timeUnit) {
        LocalDateTime futureTime;

        switch (timeUnit) {
            case SECONDS:
                futureTime = localDateTime.plusSeconds(timeDuration);
                break;
            default:
                futureTime = localDateTime.plusMinutes(timeDuration);
                break;

        }

        LocalDateTime currentTime = LocalDateTime.now();
        return futureTime.isBefore(currentTime);
    }
}

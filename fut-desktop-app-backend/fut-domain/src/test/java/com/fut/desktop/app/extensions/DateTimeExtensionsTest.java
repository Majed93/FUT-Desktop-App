package com.fut.desktop.app.extensions;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;

@RunWith(MockitoJUnitRunner.Silent.class)
public class DateTimeExtensionsTest {

    @Test
    public void testToISO8601Time() throws Exception {
        String isoTime = "1970-01-01T00:00:00Z";
        LocalDateTime localDateTime = LocalDateTime.of(1970, 1, 1, 0, 0, 0);
        String converted = DateTimeExtensions.ToISO8601Time(localDateTime);

        Assert.assertEquals(isoTime, converted);
    }

    @Test
    public void testFromUnixTime() throws Exception {
        LocalDateTime result = LocalDateTime.of(1993, 4, 19, 5, 0, 5);
        LocalDateTime converted = DateTimeExtensions.FromUnixTime("735192005000");

        Assert.assertEquals(result, converted);
    }

    @Test
    public void testFromLocalDateTimeStringToLong() throws Exception {
        String dateTime = "1993-04-19T04:00:00Z";
        long expected = 735192000000L;
        long result = DateTimeExtensions.FromLocalDateTimeString(dateTime);
        Assert.assertEquals("Timestamps do not match. ", expected, result);
    }

}

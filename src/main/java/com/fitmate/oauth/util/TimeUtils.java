package com.fitmate.oauth.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtils {
    public static String formatTimeToCustomString(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSXXX");
        ZonedDateTime zonedDateTime = dateTime.atZone(ZoneId.of("UTC+09"));
        String formattedDateTime = zonedDateTime.format(formatter);
        return formattedDateTime;
    }

    public static String formatInstantToTimestamp(Instant instant) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSXXX")
                .withZone(ZoneId.systemDefault());
        return formatter.format(instant);
    }
}

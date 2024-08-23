package com.fitmate.oauth.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TimeUtils {

    // LocalDateTime을 특정 형식으로 변환하는 메서드
    public static String formatTimeToCustomString(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSXXX");
        ZonedDateTime zonedDateTime = dateTime.atZone(ZoneId.of("Asia/Seoul")); // 한국의 표준 시간대
        String formattedDateTime = zonedDateTime.format(formatter);
        return formattedDateTime;
    }

    // Instant를 특정 형식으로 변환하는 메서드
    public static String formatInstantToTimestamp(Instant instant) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSXXX")
                .withZone(ZoneId.of("Asia/Seoul")); // 한국 시간대로 변환
        return formatter.format(instant);
    }
}


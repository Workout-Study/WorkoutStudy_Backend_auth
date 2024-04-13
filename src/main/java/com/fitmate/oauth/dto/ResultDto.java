package com.fitmate.oauth.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ResultDto {
    private final int code;
    private final String message;

    public static ResultDto success() {
        return new ResultDto(200, null);
    }

    public static ResultDto fail(String message) {
        return new ResultDto(500, message);
    }

    public static ResultDto fail() {
        return new ResultDto(500, null);
    }
}


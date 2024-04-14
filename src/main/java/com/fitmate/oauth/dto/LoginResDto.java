package com.fitmate.oauth.dto;

import com.fitmate.oauth.code.ResultCode;
import lombok.Getter;

@Getter
public class LoginResDto {
    private final ResultCode resultCode;
    private final String accessToken;
    private final String refreshToken;
    private final long userId;
    private final int isNewUser; // -1 : 실패, 0 : 기존 사용자, 1 : 새로운 사용자

    private LoginResDto(ResultCode resultCode, String accessToken, String refreshToken, long userId, int isNewUser) {
        this.resultCode = resultCode;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userId = userId;
        this.isNewUser = isNewUser;
    }

    public static LoginResDto newUser(String accessToken, String refreshToken, long userId) {
        return new LoginResDto(ResultCode.SUCCESS, accessToken, refreshToken, userId, 1);
    }

    public static LoginResDto existingUser(String accessToken, String refreshToken, long userId) {
        return new LoginResDto(ResultCode.SUCCESS, accessToken, refreshToken, userId, 0);
    }

    public static LoginResDto fail() {
        return new LoginResDto(ResultCode.FAIL ,null, null, -1, -1);
    }

    public String getResultCode() {
        return resultCode.getCode();
    }
}

package com.fitmate.oauth.dto.authLogin;

import com.fitmate.oauth.code.ResultCode;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResDto {
    private final ResultCode resultCode;
    private final String accessToken;
    private final String refreshToken;
    private final long userId;
    private final int isNewUser; // -1 : 실패, 0 : 기존 사용자, 1 : 새로운 사용자
    private final String fcmToken;

    private LoginResDto(ResultCode resultCode, String accessToken, String refreshToken, long userId, int isNewUser, String fcmToken) {
        this.resultCode = resultCode;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userId = userId;
        this.isNewUser = isNewUser;
        this.fcmToken = fcmToken;
    }

    public static LoginResDto newUser(String accessToken, String refreshToken, long userId, String fcmToken) {
        return new LoginResDto(ResultCode.SUCCESS, accessToken, refreshToken, userId, 1, fcmToken);
    }

    public static LoginResDto existingUser(String accessToken, String refreshToken, long userId, String fcmToken) {
        return new LoginResDto(ResultCode.SUCCESS, accessToken, refreshToken, userId, 0, fcmToken);
    }

    public static LoginResDto fail() {
        return new LoginResDto(ResultCode.FAIL ,null, null, -1, -1, null);
    }

    public String getResultCode() {
        return resultCode.getCode();
    }
}

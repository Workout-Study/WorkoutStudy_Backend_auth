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
    private final String imageUrl;

    private LoginResDto(ResultCode resultCode, String accessToken, String refreshToken, long userId, int isNewUser, String fcmToken, String imageUrl) {
        this.resultCode = resultCode;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userId = userId;
        this.isNewUser = isNewUser;
        this.fcmToken = fcmToken;
        this.imageUrl = imageUrl;
    }
}

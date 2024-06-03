package com.fitmate.oauth.dto.authApi;

import com.fitmate.oauth.dto.authLogin.AuthLoginParams;
import com.fitmate.oauth.dto.authLogin.AuthVerifyTokenVo;
import com.fitmate.oauth.vo.AuthProvider;

public interface AuthApiClient {
    AuthProvider AuthProvider();

    String requestAccessToken(AuthLoginParams params);

    AuthVerifyTokenVo verifyAccessToken(String accessToken);

    String logout(String accessToken);
}

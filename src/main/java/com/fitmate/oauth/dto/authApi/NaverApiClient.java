package com.fitmate.oauth.dto.authApi;

import com.fitmate.oauth.dto.authLogin.AuthLoginParams;
import com.fitmate.oauth.dto.authLogout.AuthLogoutParams;
import com.fitmate.oauth.dto.authLogin.AuthVerifyTokenVo;
import com.fitmate.oauth.dto.authLogin.NaverTokens;
import com.fitmate.oauth.vo.AuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class NaverApiClient implements AuthApiClient {

    private static final String GRANT_TYPE = "authorization_code";

    @Value("${auth.naver.url.auth}")
    private String authUrl;

    @Value("${auth.naver.client-id}")
    private String clientId;

    @Value("${auth.naver.secret}")
    private String clientSecret;

    private final RestTemplate restTemplate;

    @Override
    public AuthProvider AuthProvider() {
        return AuthProvider.NAVER;
    }

    @Override
    public String requestAccessToken(AuthLoginParams params) {
        String url = authUrl + "/oauth2.0/token";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = params.makeBody();
        body.add("grant_type", GRANT_TYPE);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);

        HttpEntity<?> request = new HttpEntity<>(body, httpHeaders);

        NaverTokens response = restTemplate.postForObject(url, request, NaverTokens.class);

        assert response != null;
        return response.getAccessToken();
    }

    @Override
    public AuthVerifyTokenVo verifyAccessToken(String accessToken) {
        return null;
    }

    @Override
    public boolean logout(String accessToken) {
        return false;
    }
}
package com.fitmate.oauth.dto.authApi;

import com.fitmate.oauth.dto.authLogin.AuthLoginParams;
import com.fitmate.oauth.dto.authLogout.AuthLogoutParams;
import com.fitmate.oauth.dto.authLogin.AuthVerifyTokenVo;
import com.fitmate.oauth.dto.authLogin.NaverTokens;
import com.fitmate.oauth.properties.OauthProperties;
import com.fitmate.oauth.vo.AuthProvider;
import com.fitmate.oauth.vo.naver.NaverGetProfileVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
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

    public NaverGetProfileVo verifyAccessToken(String accessToken) {
        String url = OauthProperties.NAVER_VERIFY_TOKEN_URL;

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        HttpEntity<?> request = new HttpEntity<>(httpHeaders);

        ResponseEntity<NaverGetProfileVo> responseEntity = restTemplate.exchange(url, HttpMethod.GET, request, NaverGetProfileVo.class);
        return responseEntity.getBody();
    }

    public String logout(String authUserId) {
        // 네이버는 로그아웃 기능이 없음 -> token 삭제로 대체
        return authUserId;
    }

}
package com.fitmate.oauth.dto.authApi;

import com.fitmate.oauth.dto.authLogin.AuthLoginParams;
import com.fitmate.oauth.dto.authLogout.AuthLogoutParams;
import com.fitmate.oauth.dto.authLogin.AuthVerifyTokenVo;
import com.fitmate.oauth.dto.authLogin.KakaoTokens;
import com.fitmate.oauth.properties.OauthProperties;
import com.fitmate.oauth.vo.AuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class KakaoApiClient implements AuthApiClient {

    private static final String GRANT_TYPE = "authorization_code";

    @Value("${auth.kakao.url.auth}")
    private String authUrl;

    @Value("${auth.kakao.client-id}")
    private String clientId;

    private final RestTemplate restTemplate;

    @Override
    public AuthProvider AuthProvider() {
        return AuthProvider.KAKAO;
    }

    @Override
    public String requestAccessToken(AuthLoginParams params) {
        String url = authUrl + "/oauth/token";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = params.makeBody();
        body.add("grant_type", GRANT_TYPE);
        body.add("client_id", clientId);

        HttpEntity<?> request = new HttpEntity<>(body, httpHeaders);

        KakaoTokens response = restTemplate.postForObject(url, request, KakaoTokens.class);

        assert response != null;
        return response.getAccessToken();
    }

    @Override
    public AuthVerifyTokenVo verifyAccessToken(String accessToken) {
        String url = OauthProperties.KAKAO_VERIFY_TOKEN_URL;

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer "+ accessToken);

        HttpEntity<?> request = new HttpEntity<>(httpHeaders);

        ResponseEntity<AuthVerifyTokenVo> responseEntity = restTemplate.exchange(url, HttpMethod.GET, request, AuthVerifyTokenVo.class);

        return responseEntity.getBody();
    }

    @Override
    public void logout(AuthLogoutParams params) {
        String url = OauthProperties.KAKAO_LOGOUT_URL;

        // TODO : Logout Redirect URL 프론트에서 생성 필요
        String uri = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("client_id", clientId)
                .queryParam("logout_redirect_uri", "https://fitmate.com/oauth")
                .toUriString();
        restTemplate.getForObject(uri, String.class);
    }
}
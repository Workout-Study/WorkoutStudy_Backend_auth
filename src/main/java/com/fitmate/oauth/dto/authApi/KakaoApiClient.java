package com.fitmate.oauth.dto.authApi;

import com.fitmate.oauth.dto.authLogin.AuthLoginParams;
import com.fitmate.oauth.dto.authLogin.AuthVerifyTokenVo;
import com.fitmate.oauth.dto.authLogin.KakaoTokens;
import com.fitmate.oauth.dto.authLogout.KakaoLogoutResDto;
import com.fitmate.oauth.properties.OauthProperties;
import com.fitmate.oauth.vo.AuthProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
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
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        HttpEntity<?> request = new HttpEntity<>(httpHeaders);

        ResponseEntity<AuthVerifyTokenVo> responseEntity = restTemplate.exchange(url, HttpMethod.GET, request, AuthVerifyTokenVo.class);

        return responseEntity.getBody();
    }

    @Override
    public boolean logout(String authUserId) {

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
        headers.add("Authorization", "KakaoAK c1de961d08e41eb160c907ded02df374");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("target_id_type", "user_id");
        body.add("target_id", authUserId);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<KakaoLogoutResDto> response = restTemplate.exchange(
                    "https://kapi.kakao.com/v1/user/logout", HttpMethod.POST, requestEntity, KakaoLogoutResDto.class);
            log.info("response = {}", response);

            KakaoLogoutResDto responseBody = response.getBody();
            if (responseBody == null) {
                log.error("Response body is null");
                return false;
            }

            return authUserId.equals(responseBody.getId());
        } catch (HttpClientErrorException e) {
            log.error("HttpClientErrorException occurred: {}", e.getMessage());
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                log.error("Bad request: {}", e.getResponseBodyAsString());
            }
        } catch (Exception e) {
            log.error("An error occurred: {}", e.getMessage());
        }
        return false;
    }

}

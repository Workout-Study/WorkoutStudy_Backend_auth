package com.fitmate.oauth.service;

import com.fitmate.oauth.properties.OauthProperties;
import com.fitmate.oauth.vo.kakao.KakaoDeleteTokenVo;
import com.fitmate.oauth.vo.kakao.KakaoGetTokenVo;
import com.fitmate.oauth.vo.kakao.KakaoRefreshTokenVo;
import com.fitmate.oauth.vo.kakao.KakaoVerifyTokenVo;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class KakaoOAuthService {

    /**
     * 접근 토큰 발급
     * @param code 인가 코드
     * @return KakaoGetTokenVo
     */
    public KakaoGetTokenVo getToken(String code) {
        WebClient webClient = WebClient.create();

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", OauthProperties.KAKAO_REST_API_KEY);
        formData.add("redirect_uri", OauthProperties.KAKAO_REDIRECT_URL);
        formData.add("code", code);

        return webClient.post()
                .uri(OauthProperties.KAKAO_TOKEN_URL)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(KakaoGetTokenVo.class)
                .block();
    }

    /**
     * 접근 토큰 갱신
     * @param refreshToken 갱신 토큰
     * @return KakaoRefreshTokenVo
     */
    public KakaoRefreshTokenVo refreshToken(String refreshToken) {
        WebClient webClient = WebClient.create();

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "refresh_token");
        formData.add("client_id", OauthProperties.KAKAO_REST_API_KEY);
        formData.add("refresh_token", refreshToken);

        return webClient.post()
                .uri(OauthProperties.KAKAO_TOKEN_URL)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(KakaoRefreshTokenVo.class)
                .block();
    }

    /**
     * 접근 토큰 삭제
     * @param accessToken 접근 토큰
     * @return KakaoDeleteTokenVo
     */
    public KakaoDeleteTokenVo deleteToken(String accessToken) {
        WebClient webClient = WebClient.create();

        return webClient.post()
                .uri(OauthProperties.KAKAO_DELETE_TOKEN_URL)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(KakaoDeleteTokenVo.class)
                .block();
    }

    /**
     * 접근 토근 검증
     * @param accessToken
     * @return
     */
    public KakaoVerifyTokenVo verifyToken(String accessToken) {
        WebClient webClient = WebClient.create();

        return webClient.get()
                .uri(OauthProperties.KAKAO_VERIFY_TOKEN_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(KakaoVerifyTokenVo.class)
                .block();
    }
}

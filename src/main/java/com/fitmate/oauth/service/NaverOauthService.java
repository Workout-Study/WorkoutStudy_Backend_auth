package com.fitmate.oauth.service;

import com.fitmate.oauth.properties.OauthProperties;
import com.fitmate.oauth.vo.naver.NaverDeleteTokenVo;
import com.fitmate.oauth.vo.naver.NaverGetProfileVo;
import com.fitmate.oauth.vo.naver.NaverRefreshTokenVo;
import com.fitmate.oauth.vo.naver.NaverGetTokenVo;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.yaml.snakeyaml.util.UriEncoder;

@Service
public class NaverOauthService {

    /**
     * 토큰 발급
     * @param code 인가 코드
     * @return LoginResDto
     */
    public NaverGetTokenVo getToken(String code) {
        /* 접근 토큰 발급 */
        String TOKEN_URL = OauthProperties.NAVER_ACCESS_TOKEN_URL;
        String CLIENT_ID = OauthProperties.NAVER_CLIENT_ID;
        String CLIENT_SECRET = OauthProperties.NAVER_CLIENT_SECRET;
        String STATE = OauthProperties.SERVER_STATE;

        WebClient client = WebClient.create();
        String url = String.format("%s?grant_type=authorization_code&client_id=%s&client_secret=%s&code=%s&state=%s",
                TOKEN_URL, CLIENT_ID, CLIENT_SECRET, code, STATE);

        return client.get()
                .uri(url)
                .retrieve()
                .bodyToMono(NaverGetTokenVo.class)
                .block();
    }

    /**
     * 접근 토큰 갱신
     * @param refreshToken 갱신 토큰
     */
    public NaverRefreshTokenVo refreshToken(String refreshToken) {
        String TOKEN_URL = OauthProperties.NAVER_ACCESS_TOKEN_URL;
        String grant_type = "refresh_token";
        String client_id = OauthProperties.NAVER_CLIENT_ID;
        String client_secret = OauthProperties.NAVER_CLIENT_SECRET;
        String refresh_token = UriEncoder.encode(refreshToken);

        String url = TOKEN_URL +
                "?grant_type=" + grant_type +
                "&client_id=" + client_id +
                "&client_secret=" + client_secret +
                "&refresh_token=" + refresh_token;

        WebClient webClient = WebClient.create();

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(NaverRefreshTokenVo.class)
                .block();
    }

    public NaverDeleteTokenVo deleteToken(String accessToken) {
        String TOKEN_URL = OauthProperties.NAVER_ACCESS_TOKEN_URL;
        String grant_type = "delete";
        String client_id = OauthProperties.NAVER_CLIENT_ID;
        String client_secret = OauthProperties.NAVER_CLIENT_SECRET;
        String access_token = UriEncoder.encode(accessToken);
        String service_provider = "NAVER";

        String url = TOKEN_URL +
                "?grant_type=" + grant_type +
                "&client_id=" + client_id +
                "&client_secret=" + client_secret +
                "&access_token=" + access_token +
                "&service_provider=" + service_provider;

        WebClient webClient = WebClient.create();

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(NaverDeleteTokenVo.class)
                .block();

    }

    /**
     * 사용자 조회
     * @param accessToken 접근 토큰
     * @return String
     */
    public NaverGetProfileVo getUserProfile(String accessToken) {
        WebClient client = WebClient.create();

        return client.get()
                .uri(OauthProperties.NAVER_PROFILE_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(NaverGetProfileVo.class)
                .block();
    }
}

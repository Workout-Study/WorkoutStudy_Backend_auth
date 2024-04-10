package com.fitmate.oauth.controller;

import com.fitmate.oauth.service.KakaoOauthService;
import com.fitmate.oauth.service.NaverOauthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class LoginController {
    private final NaverOauthService naverOauthService;
    private final KakaoOauthService kakaoOauthService;

    @Autowired
    public LoginController(NaverOauthService naverOauthService, KakaoOauthService kakaoOauthService) {
        this.naverOauthService = naverOauthService;
        this.kakaoOauthService = kakaoOauthService;
    }

    /**
     * 네이버로그인
     * @param code 인가 코드
     * @return ResponseEntity
     */
//    @GetMapping("/auth/login/naver")
//    public ResponseEntity<LoginResDto> naverLogin(@RequestParam String code) {
//
//        return ResponseEntity.ok(result);
//    }

    /**
     * 카카오 로그인
     * @param code 인가 코드
     * @return ResponseEntity
     */
//    @GetMapping("/auth/login/kakao")
//    public ResponseEntity<LoginResDto> kakaoLogin(@RequestParam String code) {
//        LoginResDto result = kakaoOauthService.getKakaoToken(code);
//
//        return ResponseEntity.ok(result);
//    }
}

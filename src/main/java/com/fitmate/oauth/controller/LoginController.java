package com.fitmate.oauth.controller;

import com.fitmate.oauth.dto.naver.LoginResDto;
import com.fitmate.oauth.service.KakaoLoginService;
import com.fitmate.oauth.service.NaverLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class LoginController {
    private final NaverLoginService naverLoginService;
    private final KakaoLoginService kakaoLoginService;

    @Autowired
    public LoginController(NaverLoginService naverLoginService, KakaoLoginService kakaoLoginService) {
        this.naverLoginService = naverLoginService;
        this.kakaoLoginService = kakaoLoginService;
    }

    /**
     * 네이버로그인
     * @param code 인가 코드
     * @return ResponseEntity
     */
    @GetMapping("/auth/login/naver")
    public ResponseEntity<LoginResDto> naverLogin(@RequestParam String code) {
        LoginResDto result = naverLoginService.login(code);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/auth/logout/naver")
    public ResponseEntity<?> naverLogout(@RequestParam String AccessToken) {
        boolean result = naverLoginService.logout(AccessToken);

        return ResponseEntity.ok().build();
    }

    /**
     * 카카오 로그인
     * @param code 인가 코드
     * @return ResponseEntity
     */
    @GetMapping("/auth/login/kakao")
    public ResponseEntity<LoginResDto> kakaoLogin(@RequestParam String code) {
        LoginResDto result = kakaoLoginService.login(code);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/auth/logout/naver")
    public ResponseEntity<?> kakaoLogout(@RequestParam String AccessToken) {
        boolean result = kakaoLoginService.logout(AccessToken);

        return ResponseEntity.ok().build();
    }
}

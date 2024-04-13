package com.fitmate.oauth.controller;

import com.fitmate.oauth.dto.ResultDto;
import com.fitmate.oauth.dto.naver.LoginResDto;
import com.fitmate.oauth.service.KakaoLoginService;
import com.fitmate.oauth.service.NaverLoginService;
import com.fitmate.oauth.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class LoginController {
    private final NaverLoginService naverLoginService;
    private final KakaoLoginService kakaoLoginService;
    private final TokenService tokenService;

    @Autowired
    public LoginController(NaverLoginService naverLoginService, KakaoLoginService kakaoLoginService, TokenService tokenService) {
        this.naverLoginService = naverLoginService;
        this.kakaoLoginService = kakaoLoginService;
        this.tokenService = tokenService;
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
    public ResponseEntity<ResultDto> naverLogout(@RequestParam String accessToken) {
        boolean result = naverLoginService.logout(accessToken);
        if(result) {
            return ResponseEntity.ok(ResultDto.success());
        }

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResultDto.fail());
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

    @GetMapping("/auth/logout/kakao")
    public ResponseEntity<ResultDto> kakaoLogout(@RequestParam String accessToken) {
        boolean result = kakaoLoginService.logout(accessToken);
        if(result) {
            return ResponseEntity.ok(ResultDto.success());
        }

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResultDto.fail());
    }

    @GetMapping("/auth/token/valid")
    public ResponseEntity<ResultDto> validToken(String accessToken) {
        if(tokenService.isTokenValid(accessToken)) {
            return ResponseEntity.ok(ResultDto.success());
        }
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(ResultDto.fail("there is no Token"));
    }
}

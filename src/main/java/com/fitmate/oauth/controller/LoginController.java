package com.fitmate.oauth.controller;

import com.fitmate.oauth.dto.authLogin.KakaoLoginReqDto;
import com.fitmate.oauth.dto.ResultDto;
import com.fitmate.oauth.dto.authLogin.LoginResDto;
import com.fitmate.oauth.dto.authLogin.NaverLoginReqDto;
import com.fitmate.oauth.dto.authLogout.KakaoLogoutReqDto;
import com.fitmate.oauth.dto.authLogout.NaverLogoutReqDto;
import com.fitmate.oauth.service.OAuthLoginService;
import com.fitmate.oauth.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
public class LoginController {
    private final OAuthLoginService oAuthLoginService;
    private final TokenService tokenService;

    /**
     * 네이버로그인
     * @param code 인가 코드
     * @return ResponseEntity
     */
    @GetMapping("/auth/login/naver")
    public ResponseEntity<LoginResDto> naverLogin(@RequestParam String code, @RequestParam String fcmToken, @RequestParam String imageUrl) {
        NaverLoginReqDto params = new NaverLoginReqDto(code);
        LoginResDto result = oAuthLoginService.naverAuthLogin(params, fcmToken, imageUrl);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/auth/logout/naver")
    public ResponseEntity<ResultDto> naverLogout(@RequestParam String accessToken) {
        String jwtToken = accessToken;
        if (accessToken.contains(" ")) {
            jwtToken = accessToken.split(" ")[1];
        }
        NaverLogoutReqDto params = new NaverLogoutReqDto(jwtToken);
        String result = oAuthLoginService.naverAuthLogout(params);
        if(result == null) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResultDto.fail());
        }
        return ResponseEntity.ok(ResultDto.of(200, result));
    }

    /**
     * 카카오 로그인
     * @param code 인가 코드
     * @return ResponseEntity
     */
    @GetMapping("/auth/login/kakao")
    public ResponseEntity<LoginResDto> kakaoLogin(@RequestParam String code, @RequestParam String fcmToken, @RequestParam String imageUrl) {
        KakaoLoginReqDto params = new KakaoLoginReqDto(code);
        LoginResDto result = oAuthLoginService.kakaoAuthLogin(params, fcmToken, imageUrl);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/auth/logout/kakao")
    public ResponseEntity<ResultDto> kakaoLogout(@RequestParam String accessToken) {
        String jwtToken = accessToken;
        if (accessToken.contains(" ")) {
            jwtToken = accessToken.split(" ")[1];
        }
        KakaoLogoutReqDto params = new KakaoLogoutReqDto(jwtToken);
        String result = oAuthLoginService.kakaoAuthLogout(params);
        if(result == null) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResultDto.fail());
        }
        return ResponseEntity.ok(ResultDto.of(200, result));
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

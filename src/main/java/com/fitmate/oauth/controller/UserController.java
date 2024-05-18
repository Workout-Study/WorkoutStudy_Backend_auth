package com.fitmate.oauth.controller;

import com.fitmate.oauth.controller.responses.GetUserInfoResponse;
import com.fitmate.oauth.dto.ResultDto;
import com.fitmate.oauth.service.TokenService;
import com.fitmate.oauth.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserService userService;
    private final TokenService tokenService;

    public UserController(UserService userService, TokenService tokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
    }

    @PostMapping("/user/nickname")
    public ResponseEntity<ResultDto> updateNickname(@RequestParam long userId, @RequestParam String nickName, HttpServletRequest httpServletRequest) {
        String accessToken = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if(!tokenService.isTokenValid(accessToken)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ResultDto.of(401, "UNAUTHORIZED. check accessToken"));
        }

        if(userService.updateUserNickname(userId, nickName)) {
            return ResponseEntity.ok(ResultDto.success());
        } else {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResultDto.fail());
        }
    }

    @DeleteMapping("/user")
    public ResponseEntity<ResultDto> deleteUser(@RequestParam long userId, HttpServletRequest httpServletRequest) {
        String accessToken = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if(!tokenService.isTokenValid(accessToken)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ResultDto.of(401, "UNAUTHORIZED. check accessToken"));
        }

        if(userService.deleteUser(userId)) {
            return ResponseEntity.ok(ResultDto.success());
        } else {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResultDto.fail());
        }
    }

    @GetMapping
    public ResponseEntity<GetUserInfoResponse> getUserInfo(@RequestParam long userId) {
        GetUserInfoResponse getUserInfoResponse = userService.getUserInfo(userId);
        return ResponseEntity.ok(getUserInfoResponse);
    }
}

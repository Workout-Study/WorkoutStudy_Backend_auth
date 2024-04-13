package com.fitmate.oauth.controller;

import com.fitmate.oauth.dto.ResultDto;
import com.fitmate.oauth.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user/nickname")
    public ResponseEntity<ResultDto> updateNickname(@RequestParam long userId, @RequestParam String nickName, HttpServletRequest httpServletRequest) {
        String accessToken = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);

        if(userService.updateUserNickname(userId, nickName)) {
            return ResponseEntity.ok(ResultDto.success());
        } else {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResultDto.fail());
        }
    }

    @DeleteMapping("/user")
    public ResponseEntity<ResultDto> deleteUser(@RequestParam long userId) {
        if(userService.deleteUser(userId)) {
            return ResponseEntity.ok(ResultDto.success());
        } else {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResultDto.fail());
        }
    }
}

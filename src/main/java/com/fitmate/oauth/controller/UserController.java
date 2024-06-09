package com.fitmate.oauth.controller;

import com.fitmate.oauth.controller.requests.UpdateNicknameRequest;
import com.fitmate.oauth.controller.responses.GetUserInfoResponse;
import com.fitmate.oauth.dto.ResultDto;
import com.fitmate.oauth.service.TokenService;
import com.fitmate.oauth.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final TokenService tokenService;

    @PutMapping("/update/nickname")
    @Operation(summary = "사용자 nickname 생성 및 업데이트", description = "nickname 이 null 이면 에러 발생")
    public ResponseEntity<ResultDto> createNickname(@RequestBody UpdateNicknameRequest request, HttpServletRequest httpServletRequest) {
        String accessToken = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if(!tokenService.isTokenValid(accessToken)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ResultDto.of(401, "UNAUTHORIZED. check accessToken"));
        }

        if(userService.updateUserNickname(request, accessToken)) {
            return ResponseEntity.ok(ResultDto.success());
        } else {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResultDto.fail());
        }
    }

    // @PutMapping("/update/nickname")
    // public ResponseEntity<ResultDto> updateNickname(@Re)

//    @DeleteMapping("/delete")
//    public ResponseEntity<ResultDto> deleteUser(@RequestParam long userId, HttpServletRequest httpServletRequest) {
//        String accessToken = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
//        if(!tokenService.isTokenValid(accessToken)) {
//            return ResponseEntity
//                    .status(HttpStatus.UNAUTHORIZED)
//                    .body(ResultDto.of(401, "UNAUTHORIZED. check accessToken"));
//        }
//
//        if(userService.deleteUser(userId)) {
//            return ResponseEntity.ok(ResultDto.success());
//        } else {
//            return ResponseEntity
//                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(ResultDto.fail());
//        }
//    }

    @GetMapping("/user-info")
    public ResponseEntity<GetUserInfoResponse> getUserInfo(@RequestParam long userId) {
        GetUserInfoResponse getUserInfoResponse = userService.getUserInfo(userId);
        return ResponseEntity.ok(getUserInfoResponse);
    }
}

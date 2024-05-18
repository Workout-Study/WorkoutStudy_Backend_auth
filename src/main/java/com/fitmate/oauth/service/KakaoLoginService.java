package com.fitmate.oauth.service;

import com.fitmate.oauth.dto.LoginResDto;
import com.fitmate.oauth.jpa.entity.UserToken;
import com.fitmate.oauth.jpa.entity.Users;
import com.fitmate.oauth.jpa.repository.UserTokenRepository;
import com.fitmate.oauth.jpa.repository.UsersRepository;
import com.fitmate.oauth.kafka.message.UserCreateEvent;
import com.fitmate.oauth.kafka.producer.UserCreateKafkaProducer;
import com.fitmate.oauth.vo.kakao.KakaoDeleteTokenVo;
import com.fitmate.oauth.vo.kakao.KakaoGetTokenVo;
import com.fitmate.oauth.vo.kakao.KakaoVerifyTokenVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KakaoLoginService {
    private final KakaoOauthService kakaoOauthService;
    private final UsersRepository usersRepository;
    private final UserTokenRepository userTokenRepository;
    private final UserCreateKafkaProducer userCreateKafkaProducer;

    public LoginResDto login(String code) {
        /* 1. 접근 토큰 발급 */
        KakaoGetTokenVo kakaoGetTokenVo = kakaoOauthService.getToken(code);
        String accessToken = kakaoGetTokenVo.getAccess_token();
        String refreshToken = kakaoGetTokenVo.getRefresh_token();

        /* 사용자 프로필 조회 */
        KakaoVerifyTokenVo kakaoVerifyTokenVo = kakaoOauthService.verifyToken(accessToken);
        String kakaoUserId = Long.toString(kakaoVerifyTokenVo.getId());

        Users findUser = usersRepository.findByOauthIdAndOauthType(kakaoUserId, "KAKAO");

        if(findUser == null) { // 신규 사용자
            Users newUser = Users.builder()
                    .oauthId(kakaoUserId)
                    .oauthType("KAKAO")
                    .build();

            Users savedUser = usersRepository.save(newUser);
            long userId = savedUser.getUserId();

            UserToken token = UserToken.builder()
                    .userId(userId)
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();

            userTokenRepository.save(token);
            /* kafka */
            // new user(UserId, nickName, createDate)
            userCreateKafkaProducer.handleEvent(UserCreateEvent.of(userId));

            return LoginResDto.newUser(accessToken, refreshToken, userId);
        } else {
            long userId = findUser.getUserId();
            UserToken findToken = userTokenRepository.findByUserId(userId);
            findToken.setAccessToken(accessToken);
            findToken.setRefreshToken(refreshToken);

            userTokenRepository.save(findToken);

            return LoginResDto.existingUser(accessToken, refreshToken, userId);
        }
    }

    public boolean logout(String accessToken) {
        KakaoDeleteTokenVo kakaoDeleteTokenVo = kakaoOauthService.deleteToken(accessToken);
        if(kakaoDeleteTokenVo == null || kakaoDeleteTokenVo.getId() == 0) {
            return false;
        }

        Optional<UserToken> userToken = userTokenRepository.findByAccessToken(accessToken);
        userToken.ifPresent(userTokenRepository::delete);

        return true;
    }
}

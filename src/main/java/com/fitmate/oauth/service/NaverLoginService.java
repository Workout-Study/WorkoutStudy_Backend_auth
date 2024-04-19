package com.fitmate.oauth.service;

import com.fitmate.oauth.dto.LoginResDto;
import com.fitmate.oauth.jpa.entity.UserToken;
import com.fitmate.oauth.jpa.entity.Users;
import com.fitmate.oauth.jpa.repository.UserTokenRepository;
import com.fitmate.oauth.jpa.repository.UsersRepository;
import com.fitmate.oauth.kafka.message.UserCreateEvent;
import com.fitmate.oauth.kafka.producer.UserCreateKafkaProducer;
import com.fitmate.oauth.vo.naver.NaverDeleteTokenVo;
import com.fitmate.oauth.vo.naver.NaverGetProfileVo;
import com.fitmate.oauth.vo.naver.NaverGetTokenVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NaverLoginService {
    private final NaverOauthService naverOauthService;
    private final UsersRepository usersRepository;
    private final UserTokenRepository userTokenRepository;
    private final UserCreateKafkaProducer userCreateKafkaProducer;

    @Transactional
    public LoginResDto login(String code) {
        /* 1. 접근 토큰 발급 */
        NaverGetTokenVo naverGetTokenVo = naverOauthService.getToken(code);
        String accessToken = naverGetTokenVo.getAccess_token();
        String refreshToken = naverGetTokenVo.getRefresh_token();

        /* 사용자 프로필 조회 */
        NaverGetProfileVo naverGetProfileVo = naverOauthService.getUserProfile(accessToken);
        String naverId = naverGetProfileVo.getResponse().getId();

        Users findUser = usersRepository.findByOauthIdAndOauthType(naverId, "NAVER");

        if(findUser == null) { // 신규 사용자
            Users newUser = Users.builder()
                    .oauthId(naverId)
                    .oauthType("NAVER")
                    .createDate(new Date())
                    .updateDate(new Date())
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
            userCreateKafkaProducer.handleEvent(UserCreateEvent.of(userId, ""));

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

    @Transactional
    public boolean logout(String accessToken) {
        NaverDeleteTokenVo naverDeleteTokenVo = naverOauthService.deleteToken(accessToken);

        if(naverDeleteTokenVo.getError_description() == null || "".equals(naverDeleteTokenVo.getError_description())) {
            return false;
        }
        Optional<UserToken> userToken = userTokenRepository.findByAccessToken(accessToken);
        userToken.ifPresent(userTokenRepository::delete);

        return true;
    }
}

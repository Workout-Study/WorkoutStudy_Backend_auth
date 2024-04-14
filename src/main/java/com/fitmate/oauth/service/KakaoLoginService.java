package com.fitmate.oauth.service;

import com.fitmate.oauth.dto.LoginResDto;
import com.fitmate.oauth.jpa.entity.UserToken;
import com.fitmate.oauth.jpa.entity.Users;
import com.fitmate.oauth.jpa.repository.UserTokenRepository;
import com.fitmate.oauth.jpa.repository.UsersRepository;
import com.fitmate.oauth.vo.kakao.KakaoDeleteTokenVo;
import com.fitmate.oauth.vo.kakao.KakaoGetTokenVo;
import com.fitmate.oauth.vo.kakao.KakaoVerifyTokenVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class KakaoLoginService {
    private final KakaoOauthService kakaoOauthService;
    private final UsersRepository usersRepository;
    private final UserTokenRepository userTokenRepository;

    @Autowired
    public KakaoLoginService(KakaoOauthService kakaoOauthService, UsersRepository usersRepository, UserTokenRepository userTokenRepository) {
        this.kakaoOauthService = kakaoOauthService;
        this.usersRepository = usersRepository;
        this.userTokenRepository = userTokenRepository;
    }

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
            // login(userID)

            return LoginResDto.newUser(accessToken, refreshToken, userId);
        } else {
            long userId = findUser.getUserId();
            UserToken findToken = userTokenRepository.findByUserId(userId);
            findToken.setAccessToken(accessToken);
            findToken.setRefreshToken(refreshToken);

            userTokenRepository.save(findToken);

            /* kafka */
            // login(userID)

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

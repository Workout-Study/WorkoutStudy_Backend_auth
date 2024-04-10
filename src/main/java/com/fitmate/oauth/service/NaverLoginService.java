package com.fitmate.oauth.service;

import com.fitmate.oauth.dto.naver.LoginResDto;
import com.fitmate.oauth.jpa.entity.UserToken;
import com.fitmate.oauth.jpa.entity.Users;
import com.fitmate.oauth.jpa.repository.UserTokenRepository;
import com.fitmate.oauth.jpa.repository.UsersRepository;
import com.fitmate.oauth.vo.naver.NaverDeleteTokenVo;
import com.fitmate.oauth.vo.naver.NaverGetProfileVo;
import com.fitmate.oauth.vo.naver.NaverGetTokenVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class NaverLoginService {
    private final NaverOauthService naverOauthService;
    private final UsersRepository usersRepository;
    private final UserTokenRepository userTokenRepository;

    @Autowired
    public NaverLoginService(NaverOauthService naverOauthService, UsersRepository usersRepository, UserTokenRepository userTokenRepository) {
        this.naverOauthService = naverOauthService;
        this.usersRepository = usersRepository;
        this.userTokenRepository = userTokenRepository;
    }

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
        NaverDeleteTokenVo naverDeleteTokenVo = naverOauthService.deleteToken(accessToken);

        if(naverDeleteTokenVo.getError_description() == null || "".equals(naverDeleteTokenVo.getError_description())) {
            return false;
        }
        return true;
    }
}

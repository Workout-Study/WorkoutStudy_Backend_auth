package com.fitmate.oauth.service;

import com.fitmate.oauth.dto.authLogin.LoginResDto;
import com.fitmate.oauth.jpa.entity.UserToken;
import com.fitmate.oauth.jpa.entity.Users;
import com.fitmate.oauth.jpa.repository.UserTokenRepository;
import com.fitmate.oauth.jpa.repository.UsersRepository;
import com.fitmate.oauth.vo.kakao.KakaoDeleteTokenVo;
import com.fitmate.oauth.vo.kakao.KakaoGetTokenVo;
import com.fitmate.oauth.vo.kakao.KakaoVerifyTokenVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KakaoLoginService {
    private final KakaoOauthService kakaoOAuthService;
    private final UsersRepository usersRepository;
    private final UserTokenRepository userTokenRepository;

}

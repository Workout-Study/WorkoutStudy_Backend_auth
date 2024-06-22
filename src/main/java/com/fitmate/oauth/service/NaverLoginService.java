package com.fitmate.oauth.service;

import com.fitmate.oauth.dto.authLogin.LoginResDto;
import com.fitmate.oauth.jpa.entity.UserToken;
import com.fitmate.oauth.jpa.entity.Users;
import com.fitmate.oauth.jpa.repository.UserTokenRepository;
import com.fitmate.oauth.jpa.repository.UsersRepository;
import com.fitmate.oauth.vo.naver.NaverDeleteTokenVo;
import com.fitmate.oauth.vo.naver.NaverGetProfileVo;
import com.fitmate.oauth.vo.naver.NaverGetTokenVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NaverLoginService {
    private final NaverOauthService naverOauthService;
    private final UsersRepository usersRepository;
    private final UserTokenRepository userTokenRepository;

}

package com.fitmate.oauth.service;

import com.fitmate.oauth.jpa.entity.UserToken;
import com.fitmate.oauth.jpa.repository.UserTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class TokenService {
    private final UserTokenRepository userTokenRepository;

    public TokenService(UserTokenRepository userTokenRepository) {
        this.userTokenRepository = userTokenRepository;
    }

    @Transactional
    public boolean isTokenValid(String accessToken) {
        String splitedAccessToken = accessToken.split(" ")[1];
        Optional<UserToken> token = userTokenRepository.findByAccessToken(splitedAccessToken);
        return token.isPresent();
    }
}

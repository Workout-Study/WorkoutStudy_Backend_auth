package com.fitmate.oauth.jpa.repository;

import com.fitmate.oauth.jpa.entity.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, Long> {
    void deleteUserTokenByAccessToken(String accessToken);
    Optional<UserToken> findByAccessToken(String accessToken);
}

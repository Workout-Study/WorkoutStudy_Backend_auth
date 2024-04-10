package com.fitmate.oauth.jpa.repository;

import com.fitmate.oauth.jpa.entity.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserTokenRepository extends JpaRepository<UserToken, Long> {
    UserToken findByUserId(long userId);
}

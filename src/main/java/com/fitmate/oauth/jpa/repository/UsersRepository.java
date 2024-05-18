package com.fitmate.oauth.jpa.repository;

import com.fitmate.oauth.jpa.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, Long> {

    Users findByUserId(Long userId);
    Users findByOauthIdAndOauthType(String oauthId, String oauthType);
}

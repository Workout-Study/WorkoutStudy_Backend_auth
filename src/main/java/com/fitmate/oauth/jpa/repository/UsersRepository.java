package com.fitmate.oauth.jpa.repository;

import com.fitmate.oauth.jpa.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {

    Users findByUserId(Long userId);
    Optional<Users> findByOauthIdAndOauthType(String oauthId, String oauthType);
}

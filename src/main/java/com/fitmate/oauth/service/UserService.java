package com.fitmate.oauth.service;

import com.fitmate.oauth.controller.requests.UpdateRequest;
import com.fitmate.oauth.controller.responses.GetUserInfoResponse;
import com.fitmate.oauth.jpa.entity.UserToken;
import com.fitmate.oauth.jpa.entity.Users;
import com.fitmate.oauth.jpa.repository.UserTokenRepository;
import com.fitmate.oauth.jpa.repository.UsersRepository;
import com.fitmate.oauth.kafka.message.UserCreateEvent;
import com.fitmate.oauth.kafka.producer.UserCreateKafkaProducer;
import com.fitmate.oauth.kafka.producer.UserInfoKafkaProducer;
import com.fitmate.oauth.service.mapper.UserMapper;

import com.fitmate.oauth.util.TimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZoneOffset;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UsersRepository usersRepository;
    private final UserTokenRepository tokenRepository;
    private final UserCreateKafkaProducer userCreateKafkaProducer;
    private final UserInfoKafkaProducer userInfoKafkaProducer;

    @Transactional
    public Long deleteUser(String accessToken) {
        if (accessToken.contains(" ")) {
            accessToken = accessToken.split(" ")[1];
        }
        Optional<UserToken> byAccessToken = tokenRepository.findByAccessToken(accessToken);
        Users users = byAccessToken.get().getUsers();
        // USER DB State 변경
        users.setUserDelete();
        usersRepository.save(users);
        //kafka deleteUser(userId)
        userInfoKafkaProducer.handleEvent(users.getUserId());
        return users.getUserId();
    }

    @Transactional
    public boolean updateUser(UpdateRequest request, String accessToken) {
        if (accessToken.contains(" ")) {
            accessToken = accessToken.split(" ")[1];
        }
        Optional<UserToken> byAccessToken = tokenRepository.findByAccessToken(accessToken);
        Users users = byAccessToken.get().getUsers();
        validateUserNotDeleted(users.getUserId());
        users.setNickname(request.getNickname());

        if(request.getImageUrl() != null){
            log.info("request ImageUrl : {}", request.getImageUrl());
            users.setImageUrl(request.getImageUrl());
            log.info("ImageUrl = {}", users.getImageUrl());
        }
        usersRepository.save(users);
        if (users.getFirstCreate()) {
            // kafka User-create-message produce
            String createdAt = TimeUtils.formatTimeToCustomString(users.getCreatedAt());
            String updatedAt = TimeUtils.formatTimeToCustomString(users.getUpdatedAt());
            userCreateKafkaProducer.handleEvent(UserCreateEvent.of(
                    users.getUserId(), request.getNickname(), users.getState(), request.getImageUrl(), createdAt, updatedAt));
            users.setFirstCreate(false);
        }
        //kafka updateUserNickName (userId, userNickname)
        log.info("UPDATE NICKNAME = {} USERID : {}, ImageURL : {}", users.getNickName(), users.getUserId(), users.getImageUrl());
        userInfoKafkaProducer.handleEvent(users.getUserId());
        return true;
    }

    @Transactional(readOnly = true)
    public GetUserInfoResponse getUserInfo(long userId) {
        validateUserNotDeleted(userId);
        Users users = usersRepository.findByUserId(userId);
        return UserMapper.toGetUserInfoResponse(users);
    }

    public void validateUserNotDeleted(long userId) {
        if(usersRepository.findByUserId(userId).getState()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제된 사용자입니다.");
        }
    }
}

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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public boolean deleteUser(long userId) {
        Optional<Users> usersOptional = usersRepository.findById(userId);
        // USER DB State 변경
        usersOptional.ifPresent(users -> {
            users.setUserDelete();
            //kafka deleteUser(userId)
            userInfoKafkaProducer.handleEvent(users.getUserId());
        });
        return true;
    }

    @Transactional
    public boolean updateUser(UpdateRequest request, String accessToken) {
        if (accessToken.contains(" ")) {
            accessToken = accessToken.split(" ")[1];
        }
        Optional<UserToken> byAccessToken = tokenRepository.findByAccessToken(accessToken);
        Users users = byAccessToken.get().getUsers();
        users.setNickname(request.getNickname());
        if(!request.getImageUrl().isEmpty()){
            users.setImageUrl(request.getImageUrl());
        }
        usersRepository.save(users);
        if (users.getFirstCreate()) {
            // kafka User-create-message produce
            String createdAtEpoch = String.valueOf(users.getCreatedAt().toInstant(ZoneOffset.UTC).toEpochMilli());
            String updatedAtEpoch = String.valueOf(users.getUpdatedAt().toInstant(ZoneOffset.UTC).toEpochMilli());
            userCreateKafkaProducer.handleEvent(UserCreateEvent.of(users.getUserId(), request.getNickname(), users.getState(), request.getImageUrl(), createdAtEpoch, updatedAtEpoch));
            users.setFirstCreate(false);
        }
        //kafka updateUserNickName (userId, userNickname)
        log.info("UPDATE NICKNAME = {} USERID : {}", users.getNickName(), users.getUserId());
        userInfoKafkaProducer.handleEvent(users.getUserId());
        return true;
    }

    @Transactional(readOnly = true)
    public GetUserInfoResponse getUserInfo(long userId) {
        Users users = usersRepository.findByUserId(userId);
        return UserMapper.toGetUserInfoResponse(users);
    }
}

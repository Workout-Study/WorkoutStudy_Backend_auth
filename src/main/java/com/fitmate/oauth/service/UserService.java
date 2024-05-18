package com.fitmate.oauth.service;

import com.fitmate.oauth.controller.responses.GetUserInfoResponse;
import com.fitmate.oauth.jpa.entity.Users;
import com.fitmate.oauth.jpa.repository.UsersRepository;
import com.fitmate.oauth.kafka.message.UserDeleteEvent;
import com.fitmate.oauth.kafka.message.UserUpdateEvent;
import com.fitmate.oauth.kafka.producer.UserDeleteKafkaProducer;
import com.fitmate.oauth.kafka.producer.UserUpdateKafkaProducer;
import com.fitmate.oauth.service.mapper.UserMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UsersRepository usersRepository;
    private final UserUpdateKafkaProducer userUpdateKafkaProducer;
    private final UserDeleteKafkaProducer userDeleteKafkaProducer;

    @Transactional
    public boolean deleteUser(long userId) {
        usersRepository.deleteById(userId);

        //kafka deleteUser(userId)
        userDeleteKafkaProducer.handleEvent(UserDeleteEvent.of(userId));

        return true;
    }

    @Transactional
    public boolean updateUserNickname(long  userId, String nickname) {
        Optional<Users> usersOptional = usersRepository.findById(userId);

        usersOptional.ifPresent(users -> {
            users.setNickName(nickname);
            usersRepository.save(users);
        });

        //kafka updateUserNickName (userId, userNickname)
        userUpdateKafkaProducer.handleEvent(UserUpdateEvent.of(userId, nickname));

        return true;
    }

    @Transactional(readOnly = true)
    public GetUserInfoResponse getUserInfo(long userId) {
        Users users = usersRepository.findByUserId(userId);
        return UserMapper.toGetUserInfoResponse(users);
    }
}

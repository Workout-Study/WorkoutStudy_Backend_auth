package com.fitmate.oauth.service;

import com.fitmate.oauth.jpa.entity.Users;
import com.fitmate.oauth.jpa.repository.UsersRepository;
import com.fitmate.oauth.kafka.message.UserDelMsg;
import com.fitmate.oauth.kafka.message.UserUdtMsg;
import com.fitmate.oauth.kafka.producer.UserDelKafkaProducer;
import com.fitmate.oauth.kafka.producer.UserUdtKafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UsersRepository usersRepository;
    private final UserUdtKafkaProducer userUdtKafkaProducer;
    private final UserDelKafkaProducer userDelKafkaProducer;

    @Transactional
    public boolean deleteUser(long userId) {
        usersRepository.deleteById(userId);

        //kafka deleteUser(userId)
        userDelKafkaProducer.send(UserDelMsg.of(userId));

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
        userUdtKafkaProducer.send(UserUdtMsg.of(userId, nickname));

        return true;
    }
}

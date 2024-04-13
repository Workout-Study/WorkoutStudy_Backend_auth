package com.fitmate.oauth.service;

import com.fitmate.oauth.jpa.entity.Users;
import com.fitmate.oauth.jpa.repository.UsersRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UsersRepository usersRepository;

    public UserService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Transactional
    public boolean deleteUser(long userId) {
        usersRepository.deleteById(userId);

        return true;
        //kafka deleteUser(userId)
    }

    @Transactional
    public boolean updateUserNickname(long  userId, String nickname) {
        Optional<Users> usersOptional = usersRepository.findById(userId);

        usersOptional.ifPresent(users -> {
            users.setNickName(nickname);
            usersRepository.save(users);
        });

        //kafka updateUserNickName (userId, userNickname)

        return true;
    }
}

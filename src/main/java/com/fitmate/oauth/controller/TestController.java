package com.fitmate.oauth.controller;

import com.fitmate.oauth.jpa.entity.Users;
import com.fitmate.oauth.jpa.repository.UsersRepository;
import com.fitmate.oauth.jpa.repository.UserTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private final UsersRepository usersRepository;
    private final UserTokenRepository userTokenRepository;

    @Autowired
    public TestController(UsersRepository usersRepository, UserTokenRepository userTokenRepository) {
        this.usersRepository = usersRepository;
        this.userTokenRepository = userTokenRepository;
    }

    @GetMapping("test")
    public void test() {

        Users user = usersRepository.findByOauthIdAndOauthType("naverid", "NAVER");

        System.out.println(user);
    }

}

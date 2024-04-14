package com.fitmate.oauth.kafka.message;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.Date;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserAddMsg {
    private final long userId;
    private final String nickname;
    private final Date createDate;

    public static UserAddMsg of(long userId, String nickname) {
        return new UserAddMsg(userId, nickname, new Date());
    }
}

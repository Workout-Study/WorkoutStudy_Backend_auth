package com.fitmate.oauth.kafka.message;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.Date;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserUdtMsg {
    private final long userId;
    private final String nickname;
    private final Date updateDate;

    public static UserUdtMsg of(long userId, String nickname) {
        return new UserUdtMsg(userId, nickname, new Date());
    }
}

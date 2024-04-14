package com.fitmate.oauth.kafka.message;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.Date;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDelMsg {
    private final long userId;
    private final Date deleteDate;

    public static UserDelMsg of(long userId) {
        return new UserDelMsg(userId, new Date());
    }
}

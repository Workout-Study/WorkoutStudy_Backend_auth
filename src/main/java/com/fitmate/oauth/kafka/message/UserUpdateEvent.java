package com.fitmate.oauth.kafka.message;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.Date;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class UserUpdateEvent {
    private final long userId;
    private final String nickname;
    private final Instant updateDate;

    public static UserUpdateEvent of(long userId, String nickname) {
        return new UserUpdateEvent(userId, nickname, Instant.now());
    }
}

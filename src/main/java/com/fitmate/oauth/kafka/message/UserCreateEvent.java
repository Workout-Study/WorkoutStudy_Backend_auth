package com.fitmate.oauth.kafka.message;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class UserCreateEvent {
    private final long userId;
    private final String nickname;
    private final Instant createDate;

    public static UserCreateEvent of(long userId, String nickname) {
        return new UserCreateEvent(userId, nickname, Instant.now());
    }
}

package com.fitmate.oauth.kafka.message;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class UserCreateEvent {
    private final Long userId;
    private final String nickname;
    private final Boolean state;
    private final String createdAt;
    private final String updatedAt;

    public static UserCreateEvent of(Long userId, String nickname, Boolean state, String createdAt, String updatedAt) {
        return new UserCreateEvent(userId, nickname, state, createdAt, updatedAt);
    }
}

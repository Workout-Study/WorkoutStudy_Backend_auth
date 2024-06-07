package com.fitmate.oauth.kafka.message;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class UserInfoEvent {
    private final Long userId;
    private final String nickname;
    private final String state;
    private final String createdAt;
    private final String updatedAt;

    public static UserInfoEvent of(Long userId, String nickname, String state, String createdAt, String updatedAt) {
        return new UserInfoEvent(userId, nickname, state, createdAt, updatedAt);
    }
}

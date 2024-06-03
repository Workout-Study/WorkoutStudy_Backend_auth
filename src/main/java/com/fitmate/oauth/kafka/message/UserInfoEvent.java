package com.fitmate.oauth.kafka.message;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class UserInfoEvent {
    private final long userId;
    private final String nickname;
    private final Instant createDate;

    public static UserInfoEvent of(long userId, String nickname) {
        return new UserInfoEvent(userId, nickname, Instant.now());
    }
}

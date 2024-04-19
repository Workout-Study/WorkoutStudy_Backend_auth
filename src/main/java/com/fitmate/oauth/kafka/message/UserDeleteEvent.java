package com.fitmate.oauth.kafka.message;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class UserDeleteEvent {
    private final long userId;
    private final Instant deleteDate;

    public static UserDeleteEvent of(long userId) {
        return new UserDeleteEvent(userId, Instant.now());
    }
}

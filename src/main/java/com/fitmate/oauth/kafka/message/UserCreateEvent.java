package com.fitmate.oauth.kafka.message;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserCreateEvent {
    private Long userId;
    private String nickname;
    private Boolean state;
    private String createdAt;
    private String updatedAt;

    public static UserCreateEvent of(Long userId, String nickname, Boolean state, String createdAt, String updatedAt) {
        return new UserCreateEvent(userId, nickname, state, createdAt, updatedAt);
    }
}

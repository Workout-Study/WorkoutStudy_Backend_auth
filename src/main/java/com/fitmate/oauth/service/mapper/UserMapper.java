package com.fitmate.oauth.service.mapper;

import com.fitmate.oauth.controller.responses.GetUserInfoResponse;
import com.fitmate.oauth.jpa.entity.Users;

import com.fitmate.oauth.util.TimeUtils;
import lombok.experimental.UtilityClass;

import java.time.ZoneOffset;

@UtilityClass
public class UserMapper {

    public static GetUserInfoResponse toGetUserInfoResponse(Users users) {
        String createdAt = TimeUtils.formatTimeToCustomString(users.getCreatedAt());
        String updatedAt = TimeUtils.formatTimeToCustomString(users.getUpdatedAt());
        return GetUserInfoResponse.builder()
                .userId(users.getUserId())
                .nickname(users.getNickName())
                .imageUrl(users.getImageUrl())
                .state(users.getState())
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}

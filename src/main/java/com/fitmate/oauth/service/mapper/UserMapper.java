package com.fitmate.oauth.service.mapper;

import com.fitmate.oauth.controller.responses.GetUserInfoResponse;
import com.fitmate.oauth.jpa.entity.Users;

import lombok.experimental.UtilityClass;

import java.time.ZoneOffset;

@UtilityClass
public class UserMapper {

	public static GetUserInfoResponse toGetUserInfoResponse(Users users) {
		String createdAtEpoch = String.valueOf(users.getCreatedAt().toInstant(ZoneOffset.UTC).toEpochMilli());
		String updatedAtEpoch = String.valueOf(users.getUpdatedAt().toInstant(ZoneOffset.UTC).toEpochMilli());
		return GetUserInfoResponse.builder()
			.userId(users.getUserId())
			.nickname(users.getNickName())
			.state(users.getState())
			.createdAt(createdAtEpoch)
			.updatedAt(updatedAtEpoch)
			.build();
	}
}

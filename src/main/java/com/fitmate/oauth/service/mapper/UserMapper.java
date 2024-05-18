package com.fitmate.oauth.service.mapper;

import com.fitmate.oauth.controller.responses.GetUserInfoResponse;
import com.fitmate.oauth.jpa.entity.Users;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UserMapper {

	public static GetUserInfoResponse toGetUserInfoResponse(Users users) {
		return GetUserInfoResponse.builder()
			.userId(users.getUserId())
			.nickname(users.getNickName())
			.createdAt(users.getCreatedAt())
			.updatedAt(users.getUpdatedAt())
			.build();
	}
}

package com.fitmate.oauth.controller.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Getter
@Value
@RequiredArgsConstructor
public class UpdateNicknameRequest {
	Long userId;
	@NotNull
	String nickname;

}

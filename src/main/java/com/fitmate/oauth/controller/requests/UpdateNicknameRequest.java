package com.fitmate.oauth.controller.requests;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateNicknameRequest {
	@NotNull
	String nickname;
}

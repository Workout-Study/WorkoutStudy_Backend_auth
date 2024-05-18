package com.fitmate.oauth.controller.responses;

import java.time.LocalDateTime;


import lombok.Builder;
import lombok.Data;
import lombok.Value;

@Value
@Data
@Builder
public class GetUserInfoResponse {
	Long userId;
	String nickname;
	LocalDateTime createdAt;
	LocalDateTime updatedAt;
}

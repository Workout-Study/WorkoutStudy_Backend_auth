package com.fitmate.oauth.dto;

import lombok.Data;

@Data
public class UsersDto {

    private Long userId;
    private String oauthId;
    private String oauthType;
    private String nickName;
}

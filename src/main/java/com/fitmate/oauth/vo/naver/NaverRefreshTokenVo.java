package com.fitmate.oauth.vo.naver;

import lombok.Data;

@Data
public class NaverRefreshTokenVo {
    private String access_token;
    private String token_type;
    private int expires_in;
    private String error;
    private String error_description;
}

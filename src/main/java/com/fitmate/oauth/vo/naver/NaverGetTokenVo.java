package com.fitmate.oauth.vo.naver;

import lombok.Data;

@Data
public class NaverGetTokenVo {
    String access_token;
    String refresh_token;
    String token_type;
    String expires_in;
    String error;
    String error_description;
}

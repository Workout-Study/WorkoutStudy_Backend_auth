package com.fitmate.oauth.vo.naver;

import lombok.Data;

@Data
public class NaverDeleteTokenVo {
    private String access_token;
    private String result;
    private String expires_in;
    private String error;
    private String error_description;
}

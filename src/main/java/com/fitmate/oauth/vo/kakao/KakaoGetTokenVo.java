package com.fitmate.oauth.vo.kakao;

import lombok.Data;

@Data
public class KakaoGetTokenVo {
    String token_type;
    String access_token;
    long expires_in;
    String refresh_token;
    long refresh_token_expires_in;
    String scope;
}

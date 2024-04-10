package com.fitmate.oauth.vo.kakao;

import lombok.Data;

@Data
public class KakaoVerifyTokenVo {
    private long id;
    private int expires_in;
    private int app_id;
}

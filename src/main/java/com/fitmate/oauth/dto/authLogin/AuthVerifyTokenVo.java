package com.fitmate.oauth.dto.authLogin;

import lombok.Data;

@Data
public class AuthVerifyTokenVo {
    private long id;
    private int expires_in;
    private int app_id;
}

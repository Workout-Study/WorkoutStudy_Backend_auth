package com.fitmate.oauth.dto.authLogin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthVerifyTokenVo {
    private long id;
    private int expires_in;
    private int app_id;
}

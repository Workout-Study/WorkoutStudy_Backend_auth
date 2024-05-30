package com.fitmate.oauth.dto.authLogin;

import com.fitmate.oauth.vo.AuthProvider;
import org.springframework.util.MultiValueMap;

public interface AuthLoginParams {
    AuthProvider authProvider();
    MultiValueMap<String, String> makeBody();
}

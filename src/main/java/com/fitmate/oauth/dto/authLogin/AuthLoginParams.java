package com.fitmate.oauth.dto.authLogin;

import com.fitmate.oauth.dto.AuthProvider;
import org.springframework.util.MultiValueMap;

public interface AuthLoginParams {
    AuthProvider authProvider();
    MultiValueMap<String, String> makeBody();
}

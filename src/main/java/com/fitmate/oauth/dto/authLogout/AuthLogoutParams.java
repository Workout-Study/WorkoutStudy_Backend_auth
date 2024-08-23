package com.fitmate.oauth.dto.authLogout;

import com.fitmate.oauth.dto.AuthProvider;
import org.springframework.util.MultiValueMap;

public interface AuthLogoutParams {
    AuthProvider oAuthProvider();
    MultiValueMap<String, String> makeBody();
}

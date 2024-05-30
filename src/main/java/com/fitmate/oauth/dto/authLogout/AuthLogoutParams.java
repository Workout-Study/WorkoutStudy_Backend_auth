package com.fitmate.oauth.dto.authLogout;

import com.fitmate.oauth.vo.AuthProvider;
import org.springframework.util.MultiValueMap;

public interface AuthLogoutParams {
    AuthProvider oAuthProvider();
    MultiValueMap<String, String> makeBody();
}

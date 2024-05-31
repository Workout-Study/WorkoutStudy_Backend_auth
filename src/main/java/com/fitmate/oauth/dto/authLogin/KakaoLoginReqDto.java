package com.fitmate.oauth.dto.authLogin;

import com.fitmate.oauth.dto.authLogin.AuthLoginParams;
import com.fitmate.oauth.vo.AuthProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KakaoLoginReqDto implements AuthLoginParams {

    private String authorizationCode;

    @Override
    public AuthProvider authProvider() {
        return AuthProvider.KAKAO;
    }

    @Override
    public MultiValueMap<String, String> makeBody() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", authorizationCode);
        return body;
    }
}

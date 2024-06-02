package com.fitmate.oauth.dto.authLogout;

import com.fitmate.oauth.vo.AuthProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KakaoLogoutReqDto implements AuthLogoutParams {

    private String accessToken;

    @Override
    public AuthProvider oAuthProvider() {
        return AuthProvider.KAKAO;
    }

    @Override
    public MultiValueMap<String, String> makeBody() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("accessToken", accessToken);
        return body;
    }
}

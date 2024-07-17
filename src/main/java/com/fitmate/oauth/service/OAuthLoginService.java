package com.fitmate.oauth.service;

import com.fitmate.oauth.code.ResultCode;
import com.fitmate.oauth.dto.authApi.NaverApiClient;
import com.fitmate.oauth.dto.authLogin.AuthLoginParams;
import com.fitmate.oauth.dto.authLogin.AuthVerifyTokenVo;
import com.fitmate.oauth.dto.authApi.KakaoApiClient;
import com.fitmate.oauth.dto.authLogin.LoginResDto;
import com.fitmate.oauth.dto.authLogin.NaverLoginReqDto;
import com.fitmate.oauth.dto.authLogout.AuthLogoutParams;
import com.fitmate.oauth.jpa.entity.UserToken;
import com.fitmate.oauth.jpa.entity.Users;
import com.fitmate.oauth.jpa.repository.UserTokenRepository;
import com.fitmate.oauth.jpa.repository.UsersRepository;
import com.fitmate.oauth.kafka.message.UserCreateEvent;
import com.fitmate.oauth.kafka.producer.UserCreateKafkaProducer;
import com.fitmate.oauth.kafka.producer.UserInfoKafkaProducer;
import com.fitmate.oauth.util.JwtTokenUtils;
import com.fitmate.oauth.vo.AuthProvider;
import com.fitmate.oauth.vo.naver.NaverGetProfileVo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.ZoneOffset;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthLoginService {

    private final RestTemplate restTemplate;
    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.token.access-token-expired-time-ms}")
    private Long accessTokenExpiredTimeMs;

    @Value("${jwt.token.refresh-token-expired-time-ms}")
    private Long refreshTokenExpiredTimeMs;

    private final KakaoApiClient kakaoApiClient;
    private final NaverApiClient naverApiClient;
    private final UsersRepository usersRepository;
    private final UserTokenRepository userTokenRepository;
    private final UserCreateKafkaProducer userCreateKafkaProducer;

    @Transactional
    public LoginResDto kakaoAuthLogin(AuthLoginParams params, String fcmToken) {
        String accessToken = kakaoApiClient.requestAccessToken(params);
        AuthVerifyTokenVo authVerifyTokenVo = kakaoApiClient.verifyAccessToken(accessToken);
        Optional<Users> optionalUsers =
                usersRepository.findByOauthIdAndOauthType(String.valueOf(authVerifyTokenVo.getId()), params.authProvider().name()); // 회원 ID, 소셜 로그인 provider
        if (optionalUsers.isPresent()) {
            return loginExistingUser(fcmToken, optionalUsers, accessToken);
        }
        String oauthId = String.valueOf(authVerifyTokenVo.getId());
        return loginNotExistingUser(params, fcmToken, oauthId, accessToken);
    }

    @Transactional
    public LoginResDto naverAuthLogin(NaverLoginReqDto params, String fcmToken) {
        String accessToken = naverApiClient.requestAccessToken(params);
        NaverGetProfileVo authVerifyTokenVo = naverApiClient.verifyAccessToken(accessToken);
        Optional<Users> optionalUsers =
                usersRepository.findByOauthIdAndOauthType(String.valueOf(authVerifyTokenVo.getResponse().getId()), params.authProvider().name());// 회원 ID, 소셜 로그인 provider
        if (optionalUsers.isPresent()) {
            return loginExistingUser(fcmToken, optionalUsers, accessToken);
        }

        String oauthId = String.valueOf(authVerifyTokenVo.getResponse().getId());
        return loginNotExistingUser(params, fcmToken, oauthId, accessToken);
    }

    @Transactional
    public String kakaoAuthLogout(AuthLogoutParams params) {
        String authUserId = logout(params);
        if (params.oAuthProvider().equals(AuthProvider.KAKAO)) {
            return kakaoApiClient.logout(authUserId);
        }
        return kakaoApiClient.logout(authUserId);
    }

    @Transactional
    public String naverAuthLogout(AuthLogoutParams params) {
        String authUserId = logout(params);
        if (params.oAuthProvider().equals(AuthProvider.NAVER)) {
            return naverApiClient.logout(authUserId);
        }
        return naverApiClient.logout(authUserId);
    }

    private LoginResDto loginNotExistingUser(AuthLoginParams params, String fcmToken, String oauthId, String accessToken) {
        Users users = Users.builder()
                .oauthId(oauthId)
                .oauthType(params.authProvider().name())
                .nickName("")
                .state(false)
                .fcmToken(fcmToken)
                .build();
        usersRepository.save(users);
        // kafka User-create-message produce
        String createdAtEpoch = String.valueOf(users.getCreatedAt().toInstant(ZoneOffset.UTC).toEpochMilli());
        String updatedAtEpoch = String.valueOf(users.getUpdatedAt().toInstant(ZoneOffset.UTC).toEpochMilli());

        // JWT 토큰 발급
        String jwtAccessToken = JwtTokenUtils.generateToken(oauthId, secretKey, accessTokenExpiredTimeMs);
        String jwtRefreshToken = JwtTokenUtils.generateToken(oauthId, secretKey, refreshTokenExpiredTimeMs);
        // UserToken 정보 저장
        UserToken userToken = UserToken.builder()
                .users(users)
                .authAccessToken(accessToken)
                .accessToken(jwtAccessToken)
                .refreshToken(jwtRefreshToken).build();
        users.setUserToken(userToken);
        UserToken save = userTokenRepository.save(userToken);

        userCreateKafkaProducer.handleEvent(UserCreateEvent.of(users.getUserId(), users.getNickName(), users.getState(), createdAtEpoch, updatedAtEpoch));

        return LoginResDto.builder()
                .resultCode(ResultCode.SUCCESS)
                .accessToken(jwtAccessToken)
                .refreshToken(jwtRefreshToken)
                .userId(users.getUserId())
                .isNewUser(1)
                .fcmToken(fcmToken)
                .build();
    }

    private LoginResDto loginExistingUser(String fcmToken, Optional<Users> optionalUsers, String accessToken) {
        String oauthId = optionalUsers.get().getOauthId();
        Long userId = optionalUsers.get().getUserId();
        String jwtAccessToken = JwtTokenUtils.generateToken(oauthId, secretKey, accessTokenExpiredTimeMs);
        String jwtRefreshToken = JwtTokenUtils.generateToken(oauthId, secretKey, refreshTokenExpiredTimeMs);
        log.info("user가 DB에 있는 경우");
        log.info("jwtAccessToken = {}, jwtRefreshToken = {}", jwtAccessToken, jwtRefreshToken);
        // UserToken 수정
        // UserToken 정보 저장
        UserToken userToken = UserToken.builder()
                .users(optionalUsers.get())
                .authAccessToken(accessToken)
                .accessToken(jwtAccessToken)
                .refreshToken(jwtRefreshToken).build();
        userTokenRepository.save(userToken);

        optionalUsers.get().setUserToken(userToken);
        return LoginResDto.builder()
                .resultCode(ResultCode.SUCCESS)
                .accessToken(jwtAccessToken)
                .refreshToken(jwtRefreshToken)
                .userId(userId)
                .isNewUser(0)
                .fcmToken(fcmToken)
                .build();
    }

    private String logout(AuthLogoutParams params) {
        String accessToken = params.makeBody().getFirst("accessToken");
        log.info("accessToken = {}", accessToken);
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey.getBytes())
                .parseClaimsJws(accessToken)
                .getBody();
        String authUserId = claims.get("authUserId", String.class);
        log.info("authUserId = {}", authUserId);
        // UserToken 제거
        Optional<UserToken> userToken = userTokenRepository.findByAccessToken(accessToken);
        userToken.ifPresent(userTokenRepository::delete);
        return authUserId;
    }
}

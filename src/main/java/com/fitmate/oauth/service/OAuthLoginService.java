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
    private final UserService userService;
    private final UserCreateKafkaProducer userCreateKafkaProducer;
    private final UserInfoKafkaProducer userInfoKafkaProducer;

    @Transactional
    public LoginResDto kakaoAuthLogin(AuthLoginParams params, String fcmToken) {
        // authorizeToken으로 accessToken 발급받아서 accessToken이 유효한지 판단
        String accessToken = kakaoApiClient.requestAccessToken(params);
        log.info("authorizeToken으로 발급받은 kakao accessToken = {}", accessToken);
        AuthVerifyTokenVo authVerifyTokenVo = kakaoApiClient.verifyAccessToken(accessToken);
        log.info("kakao accessToken이 유효한지 확인 = {}", authVerifyTokenVo);
        // 데이터베이스에 존재하는 유저인지 확인 -> JWT 토큰만 발급
        Optional<Users> optionalUsers =
                usersRepository.findByOauthIdAndOauthType(String.valueOf(authVerifyTokenVo.getId()), params.authProvider().name());// 회원 ID, 소셜 로그인 provider
        log.info("user 정보 = {}", optionalUsers);
        if (optionalUsers.isPresent()) {
            String oauthId = optionalUsers.get().getOauthId();
            Long userId = optionalUsers.get().getUserId();
            String jwtAccessToken = JwtTokenUtils.generateToken(oauthId, secretKey, accessTokenExpiredTimeMs);
            String jwtRefreshToken = JwtTokenUtils.generateToken(oauthId, secretKey, refreshTokenExpiredTimeMs);
            log.info("user가 DB에 있는 경우");
            log.info("jwtAccessToken = {}, jwtRefreshToken = {}", jwtAccessToken, jwtRefreshToken);
            // UserToken 수정
            // UserToken 정보 저장
            UserToken userToken = UserToken.builder()
                    .userId(userId)
                    .authAccessToken(accessToken)
                    .accessToken(jwtAccessToken)
                    .refreshToken(jwtRefreshToken).build();
            userTokenRepository.save(userToken);
            return LoginResDto.builder()
                    .resultCode(ResultCode.SUCCESS)
                    .accessToken(jwtAccessToken)
                    .refreshToken(jwtRefreshToken)
                    .userId(userId)
                    .isNewUser(0)
                    .fcmToken(fcmToken)
                    .build();
        }
        // 데이터베이스에 없는 유저일 때 -> 새롭게 유저를 만들어서 DB에 저장, JWT 토큰 발급
        // DB에 저장
        String oauthId = String.valueOf(authVerifyTokenVo.getId());
        Users users = Users.builder()
                .oauthId(oauthId)
                .oauthType(params.authProvider().name())
                .nickName("")
                .state(false)
                .fcmToken(fcmToken)
                .build();
        Users savedUser = usersRepository.save(users);
        // kafka User-create-message produce
        String createdAtEpoch = String.valueOf(users.getCreatedAt().toInstant(ZoneOffset.UTC).toEpochMilli());
        String updatedAtEpoch = String.valueOf(users.getUpdatedAt().toInstant(ZoneOffset.UTC).toEpochMilli());

        // JWT 토큰 발급
        String jwtAccessToken = JwtTokenUtils.generateToken(oauthId, secretKey, accessTokenExpiredTimeMs);
        String jwtRefreshToken = JwtTokenUtils.generateToken(oauthId, secretKey, refreshTokenExpiredTimeMs);
        // UserToken 정보 저장
        UserToken userToken = UserToken.builder()
                .userId(savedUser.getUserId())
                .authAccessToken(accessToken)
                .accessToken(jwtAccessToken)
                .refreshToken(jwtRefreshToken).build();
        userTokenRepository.save(userToken);

        log.info("user가 DB에 없는 경우");
        log.info("jwtAccessToken = {}, jwtRefreshToken = {}", jwtAccessToken, jwtRefreshToken);

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

    @Transactional
    public LoginResDto naverAuthLogin(NaverLoginReqDto params, String fcmToken) {
        // authorizeToken으로 accessToken 발급받아서 accessToken이 유효한지 판단
        String accessToken = naverApiClient.requestAccessToken(params);
        log.info("authorizeToken으로 발급받은 naver accessToken = {}", accessToken);
        NaverGetProfileVo authVerifyTokenVo = naverApiClient.verifyAccessToken(accessToken);
        log.info("naver accessToken이 유효한지 확인 = {}", authVerifyTokenVo);
        // 데이터베이스에 존재하는 유저인지 확인 -> JWT 토큰만 발급
        Optional<Users> optionalUsers =
                usersRepository.findByOauthIdAndOauthType(String.valueOf(authVerifyTokenVo.getResponse().getId()), params.authProvider().name());// 회원 ID, 소셜 로그인 provider
        log.info("user 정보 = {}", optionalUsers);
        if (optionalUsers.isPresent()) {
            String oauthId = optionalUsers.get().getOauthId(); // 소셜 로그인 ID
            Long userId = optionalUsers.get().getUserId(); // 데이터베이스에 저장된 사용자 ID
            String jwtAccessToken = JwtTokenUtils.generateToken(oauthId, secretKey, accessTokenExpiredTimeMs);
            String jwtRefreshToken = JwtTokenUtils.generateToken(oauthId, secretKey, refreshTokenExpiredTimeMs);
            log.info("user가 DB에 있는 경우");
            log.info("jwtAccessToken = {}, jwtRefreshToken = {}", jwtAccessToken, jwtRefreshToken);
            // UserToken 수정
            // UserToken 정보 저장
            UserToken userToken = UserToken.builder()
                    .userId(userId)
                    .authAccessToken(accessToken)
                    .accessToken(jwtAccessToken)
                    .refreshToken(jwtRefreshToken).build();
            userTokenRepository.save(userToken);
            return LoginResDto.builder()
                    .resultCode(ResultCode.SUCCESS)
                    .accessToken(jwtAccessToken)
                    .refreshToken(jwtRefreshToken)
                    .userId(userId)
                    .isNewUser(0) // 기존 유저
                    .fcmToken(fcmToken)
                    .build();
        }
        // 데이터베이스에 없는 유저일 때 -> 새롭게 유저를 만들어서 DB에 저장, JWT 토큰 발급
        // DB에 저장
        String oauthId = String.valueOf(authVerifyTokenVo.getResponse().getId());
        Users users = Users.builder()
                .oauthId(oauthId)
                .oauthType(params.authProvider().name())
                .nickName("")
                .state(false)
                .fcmToken(fcmToken)
                .build();
        Users savedUser = usersRepository.save(users);
        // kafka User-create-message produce
        String createdAtEpoch = String.valueOf(users.getCreatedAt().toInstant(ZoneOffset.UTC).toEpochMilli());
        String updatedAtEpoch = String.valueOf(users.getUpdatedAt().toInstant(ZoneOffset.UTC).toEpochMilli());
        // JWT 토큰 발급
        String jwtAccessToken = JwtTokenUtils.generateToken(oauthId, secretKey, accessTokenExpiredTimeMs);
        String jwtRefreshToken = JwtTokenUtils.generateToken(oauthId, secretKey, refreshTokenExpiredTimeMs);
        // UserToken 정보 저장
        UserToken userToken = UserToken.builder()
                .userId(savedUser.getUserId())
                .authAccessToken(accessToken)
                .accessToken(jwtAccessToken)
                .refreshToken(jwtRefreshToken).build();
        userTokenRepository.save(userToken);

        log.info("user가 DB에 없는 경우");
        log.info("jwtAccessToken = {}, jwtRefreshToken = {}", jwtAccessToken, jwtRefreshToken);

        userCreateKafkaProducer.handleEvent(UserCreateEvent.of(savedUser.getUserId(), savedUser.getNickName(), savedUser.getState(), createdAtEpoch, updatedAtEpoch));

        return LoginResDto.builder()
                .resultCode(ResultCode.SUCCESS)
                .accessToken(jwtAccessToken)
                .refreshToken(jwtRefreshToken)
                .userId(users.getUserId())
                .isNewUser(1) // 새로운 유저임
                .fcmToken(fcmToken)
                .build();
    }

    @Transactional
    public String kakaoAuthLogout(AuthLogoutParams params) {
        String accessToken = params.makeBody().getFirst("accessToken");
        log.info("accessToken = {}", accessToken);
        // accessToken에서 authId 가져오기
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey.getBytes())
                .parseClaimsJws(accessToken)
                .getBody();
        String authUserId = claims.get("authUserId", String.class);
        log.info("authUserId = {}", authUserId);
        // UserToken 제거
        Optional<UserToken> userToken = userTokenRepository.findByAccessToken(accessToken);
        userToken.ifPresent(userTokenRepository::delete);
        // authId를 kakao 쪽에 전송하여 로그아웃
        if(params.oAuthProvider().equals(AuthProvider.KAKAO)){
            return kakaoApiClient.logout(authUserId);
        }
        return kakaoApiClient.logout(authUserId);
    }

    @Transactional
    public String naverAuthLogout(AuthLogoutParams params) {
        String accessToken = params.makeBody().getFirst("accessToken");
        log.info("accessToken = {}", accessToken);
        // accessToken에서 authId 가져오기
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey.getBytes())
                .parseClaimsJws(accessToken)
                .getBody();
        String authUserId = claims.get("authUserId", String.class);
        log.info("authUserId = {}", authUserId);
        // UserToken 제거
        Optional<UserToken> userToken = userTokenRepository.findByAccessToken(accessToken);
        userToken.ifPresent(userTokenRepository::delete);
        // authId를 naver 쪽에 전송하여 로그아웃
        if(params.oAuthProvider().equals(AuthProvider.NAVER)){
            return naverApiClient.logout(authUserId);
        }
        return naverApiClient.logout(authUserId);
    }
}

package com.cocky.cockyserver.domain.auth.service;

import com.cocky.cockyserver.domain.auth.client.DataGsmOauthClient;
import com.cocky.cockyserver.domain.auth.client.dto.DataGsmStudentInfo;
import com.cocky.cockyserver.domain.auth.client.dto.DataGsmUserInfoResponse;
import com.cocky.cockyserver.domain.auth.dto.ReissueResponse;
import com.cocky.cockyserver.domain.auth.dto.SigninResponse;
import com.cocky.cockyserver.domain.auth.exception.RefreshTokenExpiredException;
import com.cocky.cockyserver.domain.auth.exception.RefreshTokenInvalidException;
import com.cocky.cockyserver.domain.auth.exception.SignupNotAllowedException;
import com.cocky.cockyserver.domain.user.entity.Role;
import com.cocky.cockyserver.domain.user.entity.User;
import com.cocky.cockyserver.domain.user.repository.UserRepository;
import com.cocky.cockyserver.global.security.jwt.JwtProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * DataGSM OAuth 인가 코드로 로그인/자동 가입을 처리하고 우리 JWT를 발급한다.
 * DataGSM 토큰은 여기서만 쓰고 버리며 저장하지 않는다.
 */
@Service
public class AuthService {

    private static final Set<String> INELIGIBLE_STUDENT_ROLES = Set.of("GRADUATE", "WITHDRAWN");

    private final DataGsmOauthClient dataGsmOauthClient;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final Set<String> adminEmails;

    public AuthService(DataGsmOauthClient dataGsmOauthClient,
                        UserRepository userRepository,
                        JwtProvider jwtProvider,
                        @Value("${auth.admin-emails:}") String adminEmails) {
        this.dataGsmOauthClient = dataGsmOauthClient;
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
        this.adminEmails = Arrays.stream(adminEmails.split(","))
                .map(String::trim)
                .filter(email -> !email.isEmpty())
                .collect(Collectors.toUnmodifiableSet());
    }

    @Transactional
    public SigninResponse signin(String code) {
        String dataGsmAccessToken = dataGsmOauthClient.exchangeToken(code);
        DataGsmUserInfoResponse userInfo = dataGsmOauthClient.fetchUserInfo(dataGsmAccessToken);

        validateSignupEligibility(userInfo);

        User user = userRepository.findByDatagsmId(userInfo.id())
                .map(existing -> syncProfile(existing, userInfo))
                .orElseGet(() -> registerUser(userInfo));

        String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getRole());
        String refreshToken = jwtProvider.generateRefreshToken(user.getId());
        user.updateRefreshToken(refreshToken);

        return new SigninResponse(accessToken, refreshToken);
    }

    @Transactional
    public ReissueResponse reissue(String refreshToken) {
        Claims claims = parseRefreshClaims(refreshToken);
        Long userId = jwtProvider.getUserId(claims);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RefreshTokenInvalidException("유효하지 않은 토큰입니다."));
        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new RefreshTokenInvalidException("유효하지 않은 토큰입니다.");
        }

        String newAccessToken = jwtProvider.generateAccessToken(user.getId(), user.getRole());
        String newRefreshToken = jwtProvider.generateRefreshToken(user.getId());
        user.updateRefreshToken(newRefreshToken);

        return new ReissueResponse(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void signout(Long userId) {
        userRepository.findById(userId).ifPresent(user -> user.updateRefreshToken(null));
    }

    private Claims parseRefreshClaims(String refreshToken) {
        Claims claims;
        try {
            claims = jwtProvider.parseClaims(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new RefreshTokenExpiredException("토큰이 만료되었습니다.");
        } catch (JwtException | IllegalArgumentException e) {
            throw new RefreshTokenInvalidException("유효하지 않은 토큰입니다.");
        }
        if (!jwtProvider.isRefreshToken(claims)) {
            throw new RefreshTokenInvalidException("유효하지 않은 토큰입니다.");
        }
        return claims;
    }

    private void validateSignupEligibility(DataGsmUserInfoResponse userInfo) {
        DataGsmStudentInfo student = userInfo.student();
        boolean eligible = Boolean.TRUE.equals(userInfo.isStudent())
                && student != null
                && !Boolean.TRUE.equals(student.isLeaveSchool())
                && !INELIGIBLE_STUDENT_ROLES.contains(student.role());
        if (!eligible) {
            throw new SignupNotAllowedException("가입 대상이 아닙니다.");
        }
    }

    private User syncProfile(User user, DataGsmUserInfoResponse userInfo) {
        DataGsmStudentInfo student = userInfo.student();
        user.updateProfile(student.name(), student.grade(), student.classNum(), student.number(), student.major());
        return user;
    }

    private User registerUser(DataGsmUserInfoResponse userInfo) {
        DataGsmStudentInfo student = userInfo.student();
        Role role = adminEmails.contains(userInfo.email()) ? Role.ADMIN : Role.STUDENT;
        User user = new User(
                userInfo.id(),
                userInfo.email(),
                student.name(),
                student.grade(),
                student.classNum(),
                student.number(),
                student.major(),
                role
        );
        return userRepository.save(user);
    }
}

package com.cocky.cockyserver.domain.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cocky.cockyserver.domain.auth.client.DataGsmOauthClient;
import com.cocky.cockyserver.domain.auth.client.dto.DataGsmStudentInfo;
import com.cocky.cockyserver.domain.auth.client.dto.DataGsmUserInfoResponse;
import com.cocky.cockyserver.domain.auth.dto.ReissueResponse;
import com.cocky.cockyserver.domain.auth.dto.SigninResponse;
import com.cocky.cockyserver.domain.auth.exception.OAuthCodeInvalidException;
import com.cocky.cockyserver.domain.auth.exception.OAuthServerException;
import com.cocky.cockyserver.domain.auth.exception.RefreshTokenExpiredException;
import com.cocky.cockyserver.domain.auth.exception.RefreshTokenInvalidException;
import com.cocky.cockyserver.domain.auth.exception.SignupNotAllowedException;
import com.cocky.cockyserver.domain.user.entity.Role;
import com.cocky.cockyserver.domain.user.entity.User;
import com.cocky.cockyserver.domain.user.repository.UserRepository;
import com.cocky.cockyserver.global.security.jwt.JwtProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final String CODE = "auth-code";
    private static final String DATAGSM_ACCESS_TOKEN = "dgsm-access-token";
    private static final String REFRESH_TOKEN = "refresh-token-abc";

    @Mock
    private DataGsmOauthClient dataGsmOauthClient;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private Claims claims;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(dataGsmOauthClient, userRepository, jwtProvider, "admin@gsm.hs.kr");
    }

    private DataGsmUserInfoResponse eligibleStudent(Long id, String email) {
        DataGsmStudentInfo student = new DataGsmStudentInfo(
                "홍길동", 2, 3, 15, "20250315", "SW과", false, "GENERAL_STUDENT");
        return new DataGsmUserInfoResponse(id, email, "STUDENT", true, student);
    }

    @Test
    void newUserSignsUpAsStudent() {
        DataGsmUserInfoResponse userInfo = eligibleStudent(100L, "student@gsm.hs.kr");
        when(dataGsmOauthClient.exchangeToken(CODE)).thenReturn(DATAGSM_ACCESS_TOKEN);
        when(dataGsmOauthClient.fetchUserInfo(DATAGSM_ACCESS_TOKEN)).thenReturn(userInfo);
        when(userRepository.findByDatagsmId(100L)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtProvider.generateAccessToken(any(), any())).thenReturn("access-token");
        when(jwtProvider.generateRefreshToken(any())).thenReturn("refresh-token");

        SigninResponse response = authService.signin(CODE);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertEquals("student@gsm.hs.kr", saved.getEmail());
        assertEquals("홍길동", saved.getName());
        assertEquals(2, saved.getGrade());
        assertEquals(3, saved.getClassNo());
        assertEquals(15, saved.getNumber());
        assertEquals("SW과", saved.getDepartment());
        assertEquals(Role.STUDENT, saved.getRole());
        assertEquals("access-token", response.accessToken());
        assertEquals("refresh-token", response.refreshToken());
        assertEquals("refresh-token", saved.getRefreshToken());
    }

    @Test
    void existingUserSignsInWithoutRegistering() {
        User existingUser = new User(200L, "existing@gsm.hs.kr", "김철수", 1, 2, 3, "SW과", Role.STUDENT);
        DataGsmUserInfoResponse userInfo = eligibleStudent(200L, "existing@gsm.hs.kr");
        when(dataGsmOauthClient.exchangeToken(CODE)).thenReturn(DATAGSM_ACCESS_TOKEN);
        when(dataGsmOauthClient.fetchUserInfo(DATAGSM_ACCESS_TOKEN)).thenReturn(userInfo);
        when(userRepository.findByDatagsmId(200L)).thenReturn(Optional.of(existingUser));
        when(jwtProvider.generateAccessToken(any(), any())).thenReturn("access-token");
        when(jwtProvider.generateRefreshToken(any())).thenReturn("refresh-token");

        SigninResponse response = authService.signin(CODE);

        verify(userRepository, never()).save(any());
        assertEquals("access-token", response.accessToken());
        assertEquals("refresh-token", existingUser.getRefreshToken());
    }

    @Test
    void adminEmailIsRegisteredAsAdmin() {
        DataGsmUserInfoResponse userInfo = eligibleStudent(300L, "admin@gsm.hs.kr");
        when(dataGsmOauthClient.exchangeToken(CODE)).thenReturn(DATAGSM_ACCESS_TOKEN);
        when(dataGsmOauthClient.fetchUserInfo(DATAGSM_ACCESS_TOKEN)).thenReturn(userInfo);
        when(userRepository.findByDatagsmId(300L)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtProvider.generateAccessToken(any(), any())).thenReturn("access-token");
        when(jwtProvider.generateRefreshToken(any())).thenReturn("refresh-token");

        authService.signin(CODE);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals(Role.ADMIN, captor.getValue().getRole());
    }

    @Test
    void nonStudentSignupIsRejected() {
        DataGsmStudentInfo student = new DataGsmStudentInfo(
                "홍길동", 2, 3, 15, "20250315", "SW과", true, "GENERAL_STUDENT");
        DataGsmUserInfoResponse userInfo = new DataGsmUserInfoResponse(400L, "left@gsm.hs.kr", "STUDENT", true, student);
        when(dataGsmOauthClient.exchangeToken(CODE)).thenReturn(DATAGSM_ACCESS_TOKEN);
        when(dataGsmOauthClient.fetchUserInfo(DATAGSM_ACCESS_TOKEN)).thenReturn(userInfo);

        assertThrows(SignupNotAllowedException.class, () -> authService.signin(CODE));
        verify(userRepository, never()).findByDatagsmId(anyLong());
        verify(userRepository, never()).save(any());
    }

    @Test
    void dataGsmTokenExchangeFailurePropagates() {
        when(dataGsmOauthClient.exchangeToken(CODE))
                .thenThrow(new OAuthCodeInvalidException("유효하지 않거나 만료된 인가 코드입니다."));

        assertThrows(OAuthCodeInvalidException.class, () -> authService.signin(CODE));
        verify(userRepository, never()).findByDatagsmId(anyLong());
    }

    @Test
    void dataGsmServerErrorPropagatesAsOAuthServerException() {
        when(dataGsmOauthClient.exchangeToken(CODE))
                .thenThrow(new OAuthServerException("DataGSM 인증 서버 오류입니다."));

        assertThrows(OAuthServerException.class, () -> authService.signin(CODE));
        verify(userRepository, never()).findByDatagsmId(anyLong());
    }

    @Test
    void existingUserProfileIsSyncedOnSignin() {
        User existingUser = new User(200L, "existing@gsm.hs.kr", "김철수", 1, 2, 3, "SW과", Role.STUDENT);
        DataGsmStudentInfo updatedStudent = new DataGsmStudentInfo(
                "김철수", 2, 5, 10, "20250315", "SW과", false, "GENERAL_STUDENT");
        DataGsmUserInfoResponse userInfo =
                new DataGsmUserInfoResponse(200L, "existing@gsm.hs.kr", "STUDENT", true, updatedStudent);
        when(dataGsmOauthClient.exchangeToken(CODE)).thenReturn(DATAGSM_ACCESS_TOKEN);
        when(dataGsmOauthClient.fetchUserInfo(DATAGSM_ACCESS_TOKEN)).thenReturn(userInfo);
        when(userRepository.findByDatagsmId(200L)).thenReturn(Optional.of(existingUser));
        when(jwtProvider.generateAccessToken(any(), any())).thenReturn("access-token");
        when(jwtProvider.generateRefreshToken(any())).thenReturn("refresh-token");

        authService.signin(CODE);

        verify(userRepository, never()).save(any());
        assertEquals(2, existingUser.getGrade());
        assertEquals(5, existingUser.getClassNo());
        assertEquals(10, existingUser.getNumber());
        assertEquals("김철수", existingUser.getName());
    }

    @Test
    void reissueRotatesTokensWhenRefreshTokenMatchesDb() {
        User user = new User(200L, "existing@gsm.hs.kr", "김철수", 1, 2, 3, "SW과", Role.STUDENT);
        user.updateRefreshToken(REFRESH_TOKEN);
        when(jwtProvider.parseClaims(REFRESH_TOKEN)).thenReturn(claims);
        when(jwtProvider.isRefreshToken(claims)).thenReturn(true);
        when(jwtProvider.getUserId(claims)).thenReturn(200L);
        when(userRepository.findById(200L)).thenReturn(Optional.of(user));
        when(jwtProvider.generateAccessToken(any(), any())).thenReturn("new-access-token");
        when(jwtProvider.generateRefreshToken(any())).thenReturn("new-refresh-token");

        ReissueResponse response = authService.reissue(REFRESH_TOKEN);

        assertEquals("new-access-token", response.accessToken());
        assertEquals("new-refresh-token", response.refreshToken());
        assertEquals("new-refresh-token", user.getRefreshToken());
    }

    @Test
    void reissueFailsWhenDbRefreshTokenDoesNotMatch() {
        User user = new User(200L, "existing@gsm.hs.kr", "김철수", 1, 2, 3, "SW과", Role.STUDENT);
        user.updateRefreshToken("already-rotated-or-logged-out-token");
        when(jwtProvider.parseClaims(REFRESH_TOKEN)).thenReturn(claims);
        when(jwtProvider.isRefreshToken(claims)).thenReturn(true);
        when(jwtProvider.getUserId(claims)).thenReturn(200L);
        when(userRepository.findById(200L)).thenReturn(Optional.of(user));

        assertThrows(RefreshTokenInvalidException.class, () -> authService.reissue(REFRESH_TOKEN));
    }

    @Test
    void reissueFailsWhenTokenIsAccessTokenNotRefresh() {
        when(jwtProvider.parseClaims(REFRESH_TOKEN)).thenReturn(claims);
        when(jwtProvider.isRefreshToken(claims)).thenReturn(false);

        assertThrows(RefreshTokenInvalidException.class, () -> authService.reissue(REFRESH_TOKEN));
        verify(userRepository, never()).findById(any());
    }

    @Test
    void reissueFailsWhenRefreshTokenExpired() {
        when(jwtProvider.parseClaims(REFRESH_TOKEN))
                .thenThrow(new ExpiredJwtException(null, claims, "만료된 토큰"));

        assertThrows(RefreshTokenExpiredException.class, () -> authService.reissue(REFRESH_TOKEN));
        verify(userRepository, never()).findById(any());
    }

    @Test
    void signoutClearsRefreshToken() {
        User user = new User(200L, "existing@gsm.hs.kr", "김철수", 1, 2, 3, "SW과", Role.STUDENT);
        user.updateRefreshToken(REFRESH_TOKEN);
        when(userRepository.findById(200L)).thenReturn(Optional.of(user));

        authService.signout(200L);

        assertNull(user.getRefreshToken());
    }

    @Test
    void reissueAfterSignoutIsRejected() {
        User user = new User(200L, "existing@gsm.hs.kr", "김철수", 1, 2, 3, "SW과", Role.STUDENT);
        user.updateRefreshToken(REFRESH_TOKEN);
        when(userRepository.findById(200L)).thenReturn(Optional.of(user));

        authService.signout(200L);

        when(jwtProvider.parseClaims(REFRESH_TOKEN)).thenReturn(claims);
        when(jwtProvider.isRefreshToken(claims)).thenReturn(true);
        when(jwtProvider.getUserId(claims)).thenReturn(200L);

        assertThrows(RefreshTokenInvalidException.class, () -> authService.reissue(REFRESH_TOKEN));
    }
}
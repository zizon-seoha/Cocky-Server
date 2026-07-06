package com.cocky.cockyserver.global.security;

import com.cocky.cockyserver.global.security.jwt.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * 인증 실패 시 401을 JSON으로 내려준다. code로 만료(TOKEN_EXPIRED)와 위조/누락(TOKEN_INVALID)을
 * 구분해, 프론트가 TOKEN_EXPIRED일 때만 reissue를 시도할 수 있게 한다.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                          AuthenticationException authException) throws IOException {
        AuthErrorCode code = (AuthErrorCode) request.getAttribute(JwtAuthenticationFilter.AUTH_ERROR_ATTRIBUTE);
        if (code == null) {
            code = AuthErrorCode.TOKEN_INVALID;
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(Map.of(
                "code", code.name(),
                "message", message(code)
        )));
    }

    private String message(AuthErrorCode code) {
        return switch (code) {
            case TOKEN_EXPIRED -> "토큰이 만료되었습니다.";
            case TOKEN_INVALID -> "유효하지 않은 토큰입니다.";
        };
    }
}
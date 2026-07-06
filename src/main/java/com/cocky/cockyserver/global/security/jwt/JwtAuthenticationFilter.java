package com.cocky.cockyserver.global.security.jwt;

import com.cocky.cockyserver.domain.user.entity.Role;
import com.cocky.cockyserver.global.security.AuthErrorCode;
import com.cocky.cockyserver.global.security.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Authorization: Bearer 헤더의 access 토큰을 검증해 SecurityContext에 인증을 세팅한다.
 * 토큰이 없거나 검증에 실패해도 여기서 요청을 막지 않고 그대로 통과시킨다 — 최종 401 여부와
 * 응답 body(TOKEN_EXPIRED/TOKEN_INVALID)는 {@link com.cocky.cockyserver.global.security.JwtAuthenticationEntryPoint}가
 * request attribute({@link #AUTH_ERROR_ATTRIBUTE})를 읽어 결정한다.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String AUTH_ERROR_ATTRIBUTE = "authErrorCode";

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtProvider jwtProvider;

    public JwtAuthenticationFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);
        if (token != null) {
            try {
                Claims claims = jwtProvider.parseClaims(token);
                if (jwtProvider.isAccessToken(claims)) {
                    authenticate(claims, request);
                } else {
                    request.setAttribute(AUTH_ERROR_ATTRIBUTE, AuthErrorCode.TOKEN_INVALID);
                }
            } catch (ExpiredJwtException e) {
                SecurityContextHolder.clearContext();
                request.setAttribute(AUTH_ERROR_ATTRIBUTE, AuthErrorCode.TOKEN_EXPIRED);
            } catch (JwtException | IllegalArgumentException e) {
                SecurityContextHolder.clearContext();
                request.setAttribute(AUTH_ERROR_ATTRIBUTE, AuthErrorCode.TOKEN_INVALID);
            }
        }
        filterChain.doFilter(request, response);
    }

    private void authenticate(Claims claims, HttpServletRequest request) {
        Long userId = jwtProvider.getUserId(claims);
        Role role = jwtProvider.getRole(claims);
        UserPrincipal principal = new UserPrincipal(userId, role);
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, authorities);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION_HEADER);
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
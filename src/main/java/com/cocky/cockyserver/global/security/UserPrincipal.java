package com.cocky.cockyserver.global.security;

import com.cocky.cockyserver.domain.user.entity.Role;

/**
 * 인증된 요청의 principal. 컨트롤러에서 {@code @AuthenticationPrincipal UserPrincipal}로 꺼낸다.
 */
public record UserPrincipal(Long userId, Role role) {
}
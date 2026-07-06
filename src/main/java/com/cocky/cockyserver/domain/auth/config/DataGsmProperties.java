package com.cocky.cockyserver.domain.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * DataGSM OAuth 연동 설정 바인딩. client-id/secret/redirect-uri는 기본값이 없다 — 값이
 * 비어 있어도 부팅은 실패하지 않고, 실제 signin 호출 시점에 {@link com.cocky.cockyserver.domain.auth.client.DataGsmOauthClient}가
 * 명확한 에러를 던진다.
 */
@ConfigurationProperties(prefix = "datagsm")
public record DataGsmProperties(
        String authorizationBaseUrl,
        String resourceBaseUrl,
        String clientId,
        String clientSecret,
        String redirectUri
) {
}
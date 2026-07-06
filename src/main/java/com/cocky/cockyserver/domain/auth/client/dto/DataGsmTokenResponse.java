package com.cocky.cockyserver.domain.auth.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** DataGSM 토큰 교환 응답(POST /v1/oauth/token) — snake_case 원본 그대로 매핑. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DataGsmTokenResponse(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("expires_in") Long expiresIn,
        @JsonProperty("refresh_token") String refreshToken,
        String scope
) {
}
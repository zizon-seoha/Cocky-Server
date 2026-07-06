package com.cocky.cockyserver.domain.auth.client;

import com.cocky.cockyserver.domain.auth.client.dto.DataGsmTokenResponse;
import com.cocky.cockyserver.domain.auth.client.dto.DataGsmUserInfoResponse;
import com.cocky.cockyserver.domain.auth.config.DataGsmProperties;
import com.cocky.cockyserver.domain.auth.exception.OAuthCodeInvalidException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * DataGSM OAuth 연동 전용 클라이언트. 인가 코드를 access_token으로 교환하고, 그 토큰으로
 * 사용자 정보를 조회하는 것까지만 담당한다. DataGSM 토큰은 이 클래스 밖으로 반환하지 않고
 * signin 흐름 안에서만 쓰고 버린다(저장 금지).
 */
@Component
public class DataGsmOauthClient {

    private static final Logger log = LoggerFactory.getLogger(DataGsmOauthClient.class);

    private final DataGsmProperties properties;
    private final RestClient authorizationClient;
    private final RestClient resourceClient;

    public DataGsmOauthClient(DataGsmProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(objectMapper);
        this.authorizationClient = RestClient.builder()
                .baseUrl(properties.authorizationBaseUrl())
                .messageConverters(converters -> {
                    converters.clear();
                    converters.add(converter);
                })
                .build();
        this.resourceClient = RestClient.builder()
                .baseUrl(properties.resourceBaseUrl())
                .messageConverters(converters -> {
                    converters.clear();
                    converters.add(converter);
                })
                .build();
    }

    /** 인가 코드를 DataGSM access_token으로 교환한다. 실패 시 항상 {@link OAuthCodeInvalidException}. */
    public String exchangeToken(String code) {
        validateClientConfig();

        Map<String, String> body = Map.of(
                "grant_type", "authorization_code",
                "code", code,
                "client_id", properties.clientId(),
                "client_secret", properties.clientSecret(),
                "redirect_uri", properties.redirectUri()
        );

        try {
            DataGsmTokenResponse response = authorizationClient.post()
                    .uri("/v1/oauth/token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(DataGsmTokenResponse.class);
            if (response == null || response.accessToken() == null) {
                throw new OAuthCodeInvalidException("DataGSM 토큰 응답이 비어 있습니다.");
            }
            return response.accessToken();
        } catch (HttpClientErrorException e) {
            log.warn("DataGSM 토큰 교환 실패: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new OAuthCodeInvalidException("유효하지 않거나 만료된 인가 코드입니다.");
        } catch (RestClientException e) {
            log.warn("DataGSM 토큰 교환 호출 실패: {}", e.getMessage());
            throw new OAuthCodeInvalidException("DataGSM 인증 서버 호출에 실패했습니다.");
        }
    }

    /** DataGSM access_token으로 사용자 정보를 조회한다. */
    public DataGsmUserInfoResponse fetchUserInfo(String dataGsmAccessToken) {
        try {
            DataGsmUserInfoResponse response = resourceClient.get()
                    .uri("/userinfo")
                    .header("Authorization", "Bearer " + dataGsmAccessToken)
                    .retrieve()
                    .body(DataGsmUserInfoResponse.class);
            if (response == null) {
                throw new OAuthCodeInvalidException("DataGSM 사용자 정보 응답이 비어 있습니다.");
            }
            return response;
        } catch (HttpClientErrorException e) {
            log.warn("DataGSM 사용자 정보 조회 실패: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new OAuthCodeInvalidException("DataGSM 사용자 정보 조회에 실패했습니다.");
        } catch (RestClientException e) {
            log.warn("DataGSM 사용자 정보 조회 호출 실패: {}", e.getMessage());
            throw new OAuthCodeInvalidException("DataGSM 인증 서버 호출에 실패했습니다.");
        }
    }

    private void validateClientConfig() {
        if (isBlank(properties.clientId()) || isBlank(properties.clientSecret()) || isBlank(properties.redirectUri())) {
            throw new IllegalStateException(
                    "DataGSM 연동 설정(DATAGSM_CLIENT_ID/DATAGSM_CLIENT_SECRET/DATAGSM_REDIRECT_URI)이 비어 있습니다. .env를 확인하세요.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
package com.cocky.cockyserver.infra.judge0;

import com.cocky.cockyserver.domain.submission.judge.JudgeExecutionException;
import com.cocky.cockyserver.infra.judge0.dto.Judge0SubmissionRequest;
import com.cocky.cockyserver.infra.judge0.dto.Judge0SubmissionResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/** Judge0 REST API 얇은 래퍼. Judge0 응답/에러 처리는 이 클래스 안에서만 다룬다. */
public class Judge0Client {

    private static final Logger log = LoggerFactory.getLogger(Judge0Client.class);

    private final RestClient restClient;

    public Judge0Client(Judge0Properties properties, ObjectMapper objectMapper) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(objectMapper);
        RestClient.Builder builder = RestClient.builder()
                .baseUrl(properties.url())
                .messageConverters(converters -> {
                    converters.clear();
                    converters.add(converter);
                });
        if (properties.token() != null && !properties.token().isBlank()) {
            builder.defaultHeader("X-Auth-Token", properties.token());
        }
        this.restClient = builder.build();
    }

    /** wait=true, base64_encoded=true로 동기 제출한다. */
    public Judge0SubmissionResult submit(Judge0SubmissionRequest request) {
        try {
            Judge0SubmissionResult result = restClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/submissions")
                            .queryParam("base64_encoded", "true")
                            .queryParam("wait", "true")
                            .build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(Judge0SubmissionResult.class);
            if (result == null || result.status() == null) {
                throw new JudgeExecutionException("Judge0 응답이 비어 있습니다.");
            }
            return result;
        } catch (HttpServerErrorException | ResourceAccessException e) {
            log.warn("Judge0 호출 중 서버 오류: {}", e.getMessage());
            throw new JudgeExecutionException("Judge0 서버 오류입니다.", e);
        } catch (RestClientException e) {
            log.warn("Judge0 호출 실패: {}", e.getMessage());
            throw new JudgeExecutionException("Judge0 호출에 실패했습니다.", e);
        }
    }
}

package com.cocky.cockyserver.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 프로젝트 전역에서 쓰는 classic Jackson2 {@link ObjectMapper} 빈.
 *
 * <p>Spring Boot의 jackson 스타터는 Jackson 3({@code tools.jackson}) 기반 ObjectMapper를
 * 자동 등록하는데, 이 코드베이스(OpenAiClient, DataGsmOauthClient 등)는 classic Jackson2
 * ({@code com.fasterxml.jackson}) 애노테이션/타입을 그대로 쓰고 있어 별도로 등록한다.
 */
@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
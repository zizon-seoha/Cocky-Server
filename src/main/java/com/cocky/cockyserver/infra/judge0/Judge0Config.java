package com.cocky.cockyserver.infra.judge0;

import com.cocky.cockyserver.domain.submission.judge.JudgeService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(Judge0Properties.class)
public class Judge0Config {

    /**
     * 이 프로젝트는 spring-boot-starter-web이 아니라 spring-boot-starter-webmvc만 쓰는데,
     * Boot 4.1은 RestClient.Builder 자동구성을 별도 모듈(spring-boot-starter-restclient)로
     * 분리해놔서 webmvc 스타터만으로는 이 빈이 생기지 않는다. 의존성을 늘리는 대신 여기서
     * 직접 등록한다. 매 주입마다 새 Builder를 받도록 프로토타입 스코프로 둔다(빌더는
     * baseUrl/헤더 설정으로 상태를 바꾸는 1회용 객체라 싱글톤으로 공유하면 안 됨).
     */
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    public LanguageMapper languageMapper() {
        return new LanguageMapper();
    }

    @Bean
    public Judge0Client judge0Client(RestClient.Builder restClientBuilder, Judge0Properties properties) {
        return new Judge0Client(restClientBuilder, properties);
    }

    @Bean
    public JudgeService judgeService(Judge0Client judge0Client, LanguageMapper languageMapper,
                                     Judge0Properties properties) {
        return new Judge0Adapter(judge0Client, languageMapper, properties);
    }
}

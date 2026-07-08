package com.cocky.cockyserver.infra.judge0;

import com.cocky.cockyserver.domain.submission.judge.JudgeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(Judge0Properties.class)
public class Judge0Config {

    @Bean
    public LanguageMapper languageMapper() {
        return new LanguageMapper();
    }

    @Bean
    public Judge0Client judge0Client(Judge0Properties properties, ObjectMapper objectMapper) {
        return new Judge0Client(properties, objectMapper);
    }

    @Bean
    public JudgeService judgeService(Judge0Client judge0Client, LanguageMapper languageMapper,
                                     Judge0Properties properties) {
        return new Judge0Adapter(judge0Client, languageMapper, properties);
    }
}

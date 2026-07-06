package com.cocky.cockyserver.domain.auth.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(DataGsmProperties.class)
public class AuthConfig {
}
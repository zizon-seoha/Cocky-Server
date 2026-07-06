package com.cocky.cockyserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;

/**
 * UserDetailsServiceAutoConfiguration을 제외한다: JWT 인증만 쓰고
 * UserDetailsService/AuthenticationManager를 정의하지 않아, 이걸 끄지 않으면
 * 부팅 시 쓰이지도 않는 in-memory 계정과 generated password 로그가 매번 남는다.
 */
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class CockyServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CockyServerApplication.class, args);
    }

}

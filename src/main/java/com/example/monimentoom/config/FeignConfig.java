package com.example.monimentoom.config;

import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 카카오 토큰 교환 시 application/x-www-form-urlencoded 전송을 위한 Feign 인코더 설정
 */
@Configuration
public class FeignConfig {

    @Bean
    public Encoder feignFormEncoder() {
        return new SpringFormEncoder();
    }

}
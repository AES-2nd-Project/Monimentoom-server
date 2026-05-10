package com.example.monimentoom.global.oauth.client;

import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.context.annotation.Bean;

public class KakaoFeignConfig {
    @Bean
    public Encoder feignFormEncoder() {
        return new SpringFormEncoder();
    }

}

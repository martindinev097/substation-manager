package com.buildingenergy.substation_manager.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfiguration {

    @Bean
    public RequestInterceptor apiKeyInterceptor(FormulaConfiguration config) {
        return requestTemplate -> requestTemplate.header("SM-API-KEY", config.getKey());
    }
}

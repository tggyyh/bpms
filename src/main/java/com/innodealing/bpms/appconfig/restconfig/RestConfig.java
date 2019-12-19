package com.innodealing.bpms.appconfig.restconfig;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestConfig{
    @Bean
    @ConfigurationProperties(prefix = "rest.connection")
    public HttpComponentsClientHttpRequestFactory customHttpRequestFactory() {
        return new HttpComponentsClientHttpRequestFactory();
    }
    @Bean
    public RestTemplate customRestTemplate(){
        return new RestTemplate(customHttpRequestFactory());
    }
}
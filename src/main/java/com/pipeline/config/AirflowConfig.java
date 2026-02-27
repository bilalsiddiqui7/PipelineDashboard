package com.pipeline.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AirflowConfig {

    @Value("${airflow.base.url}")
    private String airflowBaseUrl;

    @Value("${airflow.username:}")
    private String airflowUsername;

    @Value("${airflow.password:}")
    private String airflowPassword;

    @Value("${airflow.auth.type:basic}")
    private String authType;

    @Bean(name = "airflowRestTemplate")
    public RestTemplate airflowRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        // Don't add interceptors here - we'll handle JWT token in the service
        // because JWT tokens need to be fetched first and may expire

        return restTemplate;
    }

    public String getAirflowBaseUrl() {
        return airflowBaseUrl;
    }

    public String getAirflowUsername() {
        return airflowUsername;
    }

    public String getAirflowPassword() {
        return airflowPassword;
    }

    public String getAuthType() {
        return authType;
    }
}

package com.pipeline.service;

import com.pipeline.config.AirflowConfig;
import com.pipeline.dto.airflow.AirflowAuthRequestDto;
import com.pipeline.dto.airflow.AirflowTokenResponseDto;
import com.pipeline.exception.AirflowConnectionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Collections;

/**
 * Service to handle JWT token authentication for Airflow
 */
@Service
@Slf4j
public class AirflowAuthService {

    private final RestTemplate restTemplate;
    private final AirflowConfig airflowConfig;

    private String cachedToken;
    private Instant tokenExpiryTime;

    public AirflowAuthService(@Qualifier("airflowRestTemplate") RestTemplate restTemplate,
                              AirflowConfig airflowConfig) {
        this.restTemplate = restTemplate;
        this.airflowConfig = airflowConfig;
    }

    /**
     * Get a valid JWT token, fetching a new one if necessary
     * @return JWT access token
     */
    public synchronized String getAccessToken() {
        // Check if we have a cached token that's still valid
        if (cachedToken != null && tokenExpiryTime != null &&
            Instant.now().isBefore(tokenExpiryTime)) {
            log.debug("Using cached JWT token");
            return cachedToken;
        }

        // Fetch a new token
        log.info("Fetching new JWT token from Airflow");
        return fetchNewToken();
    }

    /**
     * Force refresh the token
     * @return New JWT access token
     */
    public synchronized String refreshToken() {
        log.info("Force refreshing JWT token");
        return fetchNewToken();
    }

    /**
     * Fetch a new JWT token from Airflow
     * @return JWT access token
     */
    private String fetchNewToken() {
        try {
            String tokenUrl = airflowConfig.getAirflowBaseUrl() + "/auth/token";
            log.debug("Requesting JWT token from: {}", tokenUrl);

            // Create auth request body
            AirflowAuthRequestDto authRequest = new AirflowAuthRequestDto(
                airflowConfig.getAirflowUsername(),
                airflowConfig.getAirflowPassword()
            );

            // Create headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            HttpEntity<AirflowAuthRequestDto> request = new HttpEntity<>(authRequest, headers);

            // Make the request
            ResponseEntity<AirflowTokenResponseDto> response = restTemplate.exchange(
                tokenUrl,
                HttpMethod.POST,
                request,
                AirflowTokenResponseDto.class
            );

            if (response.getBody() == null || response.getBody().getAccessToken() == null) {
                throw new AirflowConnectionException("Failed to get access token from Airflow", null);
            }

            String token = response.getBody().getAccessToken();

            // Cache the token (JWT tokens typically expire in 24 hours, we'll refresh after 23 hours)
            cachedToken = token;
            tokenExpiryTime = Instant.now().plusSeconds(23 * 60 * 60); // 23 hours

            log.info("Successfully obtained JWT token from Airflow");
            return token;

        } catch (Exception e) {
            log.error("Failed to fetch JWT token from Airflow", e);
            throw new AirflowConnectionException(
                "Failed to authenticate with Airflow. Please check username and password. Error: " + e.getMessage(), e);
        }
    }

    /**
     * Clear the cached token (useful for testing or logout)
     */
    public synchronized void clearToken() {
        log.debug("Clearing cached JWT token");
        cachedToken = null;
        tokenExpiryTime = null;
    }
}

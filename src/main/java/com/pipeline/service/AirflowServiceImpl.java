package com.pipeline.service;

import com.pipeline.config.AirflowConfig;
import com.pipeline.dto.airflow.AirflowDagDto;
import com.pipeline.dto.airflow.AirflowDagListResponseDto;
import com.pipeline.dto.airflow.AirflowDagSummaryDto;
import com.pipeline.exception.AirflowConnectionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AirflowServiceImpl implements AirflowService {

    private static final String API_PATH = "/api/v2";
    private final RestTemplate restTemplate;
    private final AirflowConfig airflowConfig;
    private final AirflowAuthService authService;

    public AirflowServiceImpl(@Qualifier("airflowRestTemplate") RestTemplate restTemplate,
                              AirflowConfig airflowConfig, AirflowAuthService authService) {
        this.restTemplate = restTemplate;
        this.airflowConfig = airflowConfig;
        this.authService = authService;
    }

    @Override
    public List<AirflowDagSummaryDto> getAllAirflowDags() {
        String url = airflowConfig.getAirflowBaseUrl() + API_PATH + "/dags";
        log.info("Fetching DAGs from Airflow with 'silver' tag filter: {}", url);

        try {
            ResponseEntity<AirflowDagListResponseDto> response = restTemplate.exchange(url, HttpMethod.GET,
                    createHttpEntity(), AirflowDagListResponseDto.class);

            if (response.getBody() == null || response.getBody().getDags() == null) {
                return Collections.emptyList();
            }

            // Filter DAGs to only include those with "silver" tag
            return response.getBody().getDags().stream().filter(this::hasSilverTag).map(this::mapToSummary).collect(Collectors.toList());

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new AirflowConnectionException("Failed to fetch DAGs from Airflow. HTTP " + e.getStatusCode() + ": "
                    + e.getMessage(), e);
        } catch (ResourceAccessException e) {
            throw new AirflowConnectionException("Unable to connect to Airflow at " + url + ". " + "Please check: 1) "
                    + "The URL is correct, 2) Network connectivity, 3) Firewall rules", e);
        } catch (Exception e) {
            throw new AirflowConnectionException("Unexpected error while connecting to Airflow: " + e.getMessage() +
                    ". " + "URL attempted: " + url, e);
        }
    }

    @Override
    public void toggleAirflowDag(String dagId, boolean isPaused) {
        try {
            String url = airflowConfig.getAirflowBaseUrl() + API_PATH + "/dags/" + dagId;

            Map<String, Boolean> body = new HashMap<>();
            body.put("is_paused", isPaused);

            HttpEntity<Map<String, Boolean>> request = new HttpEntity<>(body, createHeaders());

            restTemplate.exchange(url, HttpMethod.PATCH, request, AirflowDagDto.class);

        } catch (HttpClientErrorException.NotFound e) {
            throw new AirflowConnectionException("DAG not found: " + dagId, e);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new AirflowConnectionException("Failed to toggle DAG in Airflow: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new AirflowConnectionException("Unexpected error: " + e.getMessage(), e);
        }
    }

    private HttpEntity<Void> createHttpEntity() {
        return new HttpEntity<>(createHeaders());
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // Add JWT Bearer token if credentials are configured
        if (airflowConfig.getAirflowUsername() != null && !airflowConfig.getAirflowUsername().isEmpty()) {
            try {
                String token = authService.getAccessToken();
                headers.setBearerAuth(token);
            } catch (Exception e) {

            }
        }

        return headers;
    }

    private AirflowDagSummaryDto mapToSummary(AirflowDagDto dag) {
        // Extract tag names from tags
        List<String> tagNames = dag.getTags() != null ?
                dag.getTags().stream().map(tag -> tag.get("name")).collect(Collectors.toList()) :
                Collections.emptyList();

        return AirflowDagSummaryDto.builder().dagId(dag.getDagId()).description(dag.getDescription()).build();
    }

    /**
     * Check if DAG has "silver" tag
     */
    private boolean hasSilverTag(AirflowDagDto dag) {
        if (dag.getTags() == null) {
            return false;
        }

        return dag.getTags().stream().anyMatch(tag -> {
            String tagName = tag.get("name");
            return tagName != null && tagName.equalsIgnoreCase("silver");
        });
    }
}

package com.pipeline.controller;

import com.pipeline.dto.airflow.AirflowDagSummaryDto;
import com.pipeline.dto.configuration.ConfigurationParameterDto;
import com.pipeline.dto.configuration.PipelineConfigurationResponseDto;
import com.pipeline.dto.configuration.SaveConfigurationValueDto;
import com.pipeline.dto.configuration.UserConfigurationValueDto;
import com.pipeline.service.AirflowService;
import com.pipeline.service.ConfigurationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for configuration parameter management
 */
@RestController
@CrossOrigin("*")
@RequestMapping("/workflow-approval/threshold-config")
@RequiredArgsConstructor
public class ConfigurationController {

    private final ConfigurationService configurationService;

    private final AirflowService airflowService;

    /**
     * Get all DAGs from Airflow
     *
     * @return List of Airflow DAGs
     */
    @GetMapping("/dags")
    public ResponseEntity<List<AirflowDagSummaryDto>> getAllDags() {
        List<AirflowDagSummaryDto> dags = airflowService.getAllAirflowDags();
        return ResponseEntity.ok(dags);
    }

    /**
     * Pause or unpause a DAG in Airflow
     *
     * @param dagId    The DAG ID
     * @param isPaused Whether to pause (true) or unpause (false)
     * @return No content on success
     */
    @PatchMapping("/dags/{dagId}/pause")
    public ResponseEntity<Void> toggleDag(@PathVariable String dagId, @RequestParam boolean isPaused) { // TODO -  remove this api this, developed this for future use case, but currently not used anywhere
        airflowService.toggleAirflowDag(dagId, isPaused);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get all active configuration parameters (for dropdown)
     *
     * @return List of active parameters
     */
    @GetMapping("/parameters")
    public ResponseEntity<List<ConfigurationParameterDto>> getAllActiveParameters() {
        List<ConfigurationParameterDto> parameters = configurationService.getAllActiveParameters();
        return ResponseEntity.ok(parameters);
    }

    /**
     * Save user configuration values for a pipeline
     *
     * @param pipelineId Pipeline ID
     * @param request    Request containing configuredBy and list of values
     * @return Success message
     */
    @PutMapping("/{pipelineId}/values")
    public ResponseEntity<Map<String, String>> saveUserConfigurationValues(
            @PathVariable String pipelineId,
            @RequestBody SaveConfigurationRequest request) {
        configurationService.saveUserConfigurationValues(
                pipelineId,
                request.getValues()
        );
        return ResponseEntity.ok(Map.of("message", "Configuration values saved successfully"));
    }

    /**
     * Get pipeline configuration (optimized - pipelineId at top level only)
     *
     * @param pipelineId Pipeline ID
     * @return Pipeline configuration with all configured parameters
     */
    @GetMapping("/{pipelineId}/values")
    public ResponseEntity<PipelineConfigurationResponseDto> getPipelineConfiguration(
            @PathVariable String pipelineId) {
        PipelineConfigurationResponseDto config = configurationService.getPipelineConfiguration(pipelineId);
        return ResponseEntity.ok(config);
    }

    /**
     * Request DTO for saving configuration values
     */
    @lombok.Data
    public static class SaveConfigurationRequest {
        private List<SaveConfigurationValueDto> values;
    }
}

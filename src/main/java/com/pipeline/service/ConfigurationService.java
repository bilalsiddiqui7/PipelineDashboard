package com.pipeline.service;

import com.pipeline.dto.configuration.ConfigurationParameterDto;
import com.pipeline.dto.configuration.PipelineConfigurationResponseDto;
import com.pipeline.dto.configuration.SaveConfigurationValueDto;
import com.pipeline.dto.configuration.UserConfigurationValueDto;

import java.util.List;

/**
 * Service for managing configuration parameters and user configuration values
 */
public interface ConfigurationService {

    /**
     * Get all active configuration parameters (for dropdown)
     */
    List<ConfigurationParameterDto> getAllActiveParameters();

    /**
     * Save user configuration values for a pipeline
     */
    void saveUserConfigurationValues(String pipelineId, List<SaveConfigurationValueDto> values);

    /**
     * Get pipeline configuration (optimized - pipelineId at top level only)
     */
    PipelineConfigurationResponseDto getPipelineConfiguration(String pipelineId);

}

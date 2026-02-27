package com.pipeline.dto.configuration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * DTO for a single configured parameter (without redundant pipelineId)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfiguredParameterDto {

    private Long id;
    private ConfigurationParameterDto parameter;
    private String configuredValue;
}

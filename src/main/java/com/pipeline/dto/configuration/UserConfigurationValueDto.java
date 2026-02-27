package com.pipeline.dto.configuration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * DTO for user configured values
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserConfigurationValueDto {

    private Long id;
    private String pipelineId;
    private ConfigurationParameterDto parameter;
    private String configuredValue;
    private String configuredBy;
    private Instant createdAt;
    private Instant updatedAt;
}

package com.pipeline.dto.configuration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for pipeline configuration response
 * Groups all configured values under one pipeline
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PipelineConfigurationResponseDto {

    private String pipelineId;
    private List<ConfiguredParameterDto> configuredParameters;
}

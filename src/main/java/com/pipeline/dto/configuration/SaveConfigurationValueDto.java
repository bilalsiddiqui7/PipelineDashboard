package com.pipeline.dto.configuration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for saving user configuration values
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveConfigurationValueDto {

    private Long parameterId;
    private String configuredValue;
}

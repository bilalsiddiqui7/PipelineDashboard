package com.pipeline.dto.configuration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for configuration parameter (dropdown options)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfigurationParameterDto {

    private Long id;
    private String parameterName;
    private String displayName;
    private String dataType;
}

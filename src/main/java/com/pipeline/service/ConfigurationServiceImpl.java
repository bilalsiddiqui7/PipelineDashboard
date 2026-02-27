package com.pipeline.service;

import com.pipeline.dto.configuration.ConfigurationParameterDto;
import com.pipeline.dto.configuration.ConfiguredParameterDto;
import com.pipeline.dto.configuration.PipelineConfigurationResponseDto;
import com.pipeline.dto.configuration.SaveConfigurationValueDto;
import com.pipeline.dto.configuration.UserConfigurationValueDto;
import com.pipeline.model.ConfigurationParameter;
import com.pipeline.model.UserConfigurationValue;
import com.pipeline.repository.ConfigurationParameterRepository;
import com.pipeline.repository.UserConfigurationValueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConfigurationServiceImpl implements ConfigurationService {

    private final ConfigurationParameterRepository parameterRepository;
    private final UserConfigurationValueRepository configValueRepository;

    @Override
    public List<ConfigurationParameterDto> getAllActiveParameters() {
        log.info("Fetching all active configuration parameters");
        return parameterRepository.findByActiveTrue().stream()
                .map(this::mapParameterToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void saveUserConfigurationValues(String pipelineId, List<SaveConfigurationValueDto> values) {
        log.info("Saving {} configuration values for pipeline: {}", values.size(), pipelineId);

        for (SaveConfigurationValueDto valueDto : values) {
            ConfigurationParameter parameter = parameterRepository.findById(valueDto.getParameterId())
                    .orElseThrow(() -> new RuntimeException("Parameter not found: " + valueDto.getParameterId()));

            // Check if value already exists
            UserConfigurationValue existingValue = configValueRepository
                    .findByPipelineIdAndParameterId(pipelineId, valueDto.getParameterId())
                    .orElse(null);

            if (existingValue != null) {
                // Update existing value
                existingValue.setConfiguredValue(valueDto.getConfiguredValue());
                existingValue.setUpdatedAt(Instant.now());
                configValueRepository.save(existingValue);
                log.debug("Updated configuration value for parameter: {}", parameter.getParameterName());
            } else {
                // Create new value
                UserConfigurationValue newValue = UserConfigurationValue.builder()
                        .pipelineId(pipelineId)
                        .parameter(parameter)
                        .configuredValue(valueDto.getConfiguredValue())
                        .build();
                configValueRepository.save(newValue);
                log.debug("Created configuration value for parameter: {}", parameter.getParameterName());
            }
        }

        log.info("Successfully saved configuration values for pipeline: {}", pipelineId);
    }

    @Override
    public PipelineConfigurationResponseDto getPipelineConfiguration(String pipelineId) {
        log.info("Fetching optimized pipeline configuration for pipeline: {}", pipelineId);

        List<ConfiguredParameterDto> configuredParams = configValueRepository.findByPipelineId(pipelineId).stream()
                .map(this::mapToConfiguredParameterDto)
                .collect(Collectors.toList());

        return PipelineConfigurationResponseDto.builder()
                .pipelineId(pipelineId)
                .configuredParameters(configuredParams)
                .build();
    }

    private ConfigurationParameterDto mapParameterToDto(ConfigurationParameter entity) {
        return ConfigurationParameterDto.builder()
                .id(entity.getId())
                .parameterName(entity.getParameterName())
                .displayName(entity.getDisplayName())
                .dataType(entity.getDataType())
                .build();
    }

    private UserConfigurationValueDto mapConfigValueToDto(UserConfigurationValue entity) {
        return UserConfigurationValueDto.builder()
                .id(entity.getId())
                .pipelineId(entity.getPipelineId())
                .parameter(mapParameterToDto(entity.getParameter()))
                .configuredValue(entity.getConfiguredValue())
                .configuredBy(entity.getConfiguredBy())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private ConfiguredParameterDto mapToConfiguredParameterDto(UserConfigurationValue entity) {
        return ConfiguredParameterDto.builder()
                .id(entity.getId())
                .parameter(mapParameterToDto(entity.getParameter()))
                .configuredValue(entity.getConfiguredValue())
                .build();
    }
}

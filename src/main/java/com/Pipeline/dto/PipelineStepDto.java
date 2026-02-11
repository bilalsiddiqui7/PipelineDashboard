package com.Pipeline.dto;

import com.Pipeline.enums.ConfigType;
import com.Pipeline.enums.StepType;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PipelineStepDto {

    private String stepName;
    private StepType stepType;
    private int stepOrder;

    private ConfigType configType;
    private Object configContent;
}

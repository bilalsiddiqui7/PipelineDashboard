package com.pipeline.dto;

import com.pipeline.enums.ConfigType;
import com.pipeline.enums.StepType;
import lombok.AllArgsConstructor;
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
    private String configContent;
}

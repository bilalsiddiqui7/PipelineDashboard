package com.pipeline.dto.pipeline;

import com.pipeline.enums.StepType;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PipelineSummaryDto {

    private Long id;
    private String name;
    private String description;
    private boolean enabled;
    private String scheduleDescription;
    private int stepCount;
    private List<StepType> stepTypes;


}




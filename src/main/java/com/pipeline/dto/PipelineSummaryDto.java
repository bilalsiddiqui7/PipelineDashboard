package com.pipeline.dto;

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
//    private ScheduleType scheduleType;
//    private DayOfWeek scheduleDay;
//    private Integer scheduleDayOfMonth;
//    private LocalTime scheduleTime;
    private String scheduleDescription;
    private int stepCount;
    private List<StepType> stepTypes;


}




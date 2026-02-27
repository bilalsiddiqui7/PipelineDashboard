package com.pipeline.dto.pipeline;

import com.pipeline.enums.ScheduleType;
import lombok.Builder;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
public class PipelineResponseDto {

    private Long id;
    private String name;
    private String description;
    private boolean enabled;

    private ScheduleType scheduleType;
    private LocalTime scheduleTime;
    private DayOfWeek scheduleDay;
    private Integer scheduleDayOfMonth;
    private String cronExpression;

    private List<PipelineStepDto> steps;
}


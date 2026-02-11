package com.Pipeline.dto;

import com.Pipeline.enums.ScheduleType;
import com.Pipeline.enums.StepType;
import lombok.Builder;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

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




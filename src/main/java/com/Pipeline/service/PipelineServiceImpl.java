package com.Pipeline.service;

import com.Pipeline.dto.*;
import com.Pipeline.enums.ScheduleType;
import com.Pipeline.model.Pipeline;
import com.Pipeline.model.PipelineStep;
import com.Pipeline.repository.PipelineRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PipelineServiceImpl implements PipelineService {

    private final PipelineRepository pipelineRepository;

    @Override
    public List<PipelineSummaryDto> getAllPipelines() {
        return pipelineRepository.findAll().stream().map(p -> PipelineSummaryDto.builder().id(p.getId()).name(p.getName()).description(p.getDescription()).enabled(p.isEnabled())
                .scheduleDescription(getScheduleDescription(p.getScheduleType(), p.getScheduleDay(), p.getScheduleDayOfMonth(), p.getScheduleTime(), p.getCronExpression())).stepCount(p.getSteps().size()).stepTypes(p.getSteps().stream().map(step -> step.getStepType()).distinct().toList()).build()).collect(Collectors.toList());
    }

    @Override
    public PipelineResponseDto getPipeline(Long id) {
        Pipeline pipeline = pipelineRepository.findById(id).orElseThrow(() -> new RuntimeException("Pipeline not found"));

        return mapToResponse(pipeline);
    }

    @Override
    public PipelineResponseDto createPipeline(PipelineRequestDto request) {

        Pipeline pipeline = new Pipeline();
        pipeline.setName(request.getName());
        pipeline.setDescription(request.getDescription());
        pipeline.setEnabled(request.isEnabled());
        pipeline.setScheduleType(request.getScheduleType());
        pipeline.setScheduleDay(request.getScheduleDay());
        pipeline.setScheduleDayOfMonth(request.getScheduleDayOfMonth());
        pipeline.setScheduleTime(request.getScheduleTime());
        pipeline.setCronExpression(request.getCronExpression());

        List<PipelineStep> steps = new ArrayList<>();

        for (PipelineStepDto stepDto : request.getSteps()) {

            PipelineStep step = new PipelineStep();
            step.setStepName(stepDto.getStepName());
            step.setStepType(stepDto.getStepType());
            step.setStepOrder(stepDto.getStepOrder());
            step.setConfigType(stepDto.getConfigType());

            // Convert Object (JSON) to String for storage
            String configContent;
            if (stepDto.getConfigContent() instanceof String) {
                configContent = (String) stepDto.getConfigContent();
            } else {
                // JSON object -> string
                try {
                    configContent = new ObjectMapper().writeValueAsString(stepDto.getConfigContent());
                } catch (JsonProcessingException e) {
                    throw new RuntimeException("Invalid config content", e);
                }
            }

            step.setConfigContent(configContent);
            step.setPipeline(pipeline);

            steps.add(step);
        }

        pipeline.setSteps(steps);
        Pipeline saved = pipelineRepository.save(pipeline);

        return mapToResponse(saved);
    }

    @Override
    public PipelineResponseDto updatePipeline(Long id, PipelineRequestDto dto) {
        Pipeline pipeline = pipelineRepository.findById(id).orElseThrow(() -> new RuntimeException("Pipeline not found"));

        pipeline.getSteps().clear();
        pipeline.setName(dto.getName());
        pipeline.setDescription(dto.getDescription());
        pipeline.setEnabled(dto.isEnabled());
        pipeline.setScheduleType(dto.getScheduleType());
        pipeline.setScheduleTime(dto.getScheduleTime());
        pipeline.setScheduleDay(dto.getScheduleDay());
        pipeline.setScheduleDayOfMonth(dto.getScheduleDayOfMonth());
        pipeline.setCronExpression(dto.getCronExpression());
        pipeline.setUpdatedAt(Instant.now());

        dto.getSteps().forEach(s -> pipeline.getSteps().add(PipelineStep.builder().stepName(s.getStepName()).stepType(s.getStepType()).stepOrder(s.getStepOrder()).configType(s.getConfigType()).configContent((String) s.getConfigContent()).pipeline(pipeline).build()));

        return mapToResponse(pipelineRepository.save(pipeline));
    }

    @Override
    public void togglePipeline(Long id, boolean enabled) {
        Pipeline pipeline = pipelineRepository.findById(id).orElseThrow(() -> new RuntimeException("Pipeline not found"));
        pipeline.setEnabled(enabled);
        pipelineRepository.save(pipeline);
    }

    @Override
    public void deletePipeline(Long id) {
        pipelineRepository.deleteById(id);
    }

    @Override
    public PipelineResponseDto copyPipeline(Long id) {
        Pipeline original = pipelineRepository.findById(id).orElseThrow(() -> new RuntimeException("Pipeline not found"));

        Pipeline copy = Pipeline.builder().name(original.getName() + " (Copy)").description(original.getDescription()).enabled(original.isEnabled()).scheduleType(original.getScheduleType()).scheduleTime(original.getScheduleTime()).scheduleDay(original.getScheduleDay()).scheduleDayOfMonth(original.getScheduleDayOfMonth()).cronExpression(original.getCronExpression()).createdAt(Instant.now()).updatedAt(Instant.now()).build();

        copy.setSteps(original.getSteps().stream().map(s -> PipelineStep.builder().stepName(s.getStepName()).stepType(s.getStepType()).stepOrder(s.getStepOrder()).configType(s.getConfigType()).configContent(s.getConfigContent()).pipeline(copy).build()).collect(Collectors.toList()));

        return mapToResponse(pipelineRepository.save(copy));
    }


    private PipelineResponseDto mapToResponse(Pipeline pipeline) {
        return PipelineResponseDto.builder().id(pipeline.getId()).name(pipeline.getName()).description(pipeline.getDescription()).enabled(pipeline.isEnabled()).scheduleType(pipeline.getScheduleType()).scheduleTime(pipeline.getScheduleTime()).scheduleDay(pipeline.getScheduleDay()).scheduleDayOfMonth(pipeline.getScheduleDayOfMonth()).cronExpression(pipeline.getCronExpression()).steps(pipeline.getSteps().stream().map(s -> {
            PipelineStepDto dto = new PipelineStepDto();
            dto.setStepName(s.getStepName());
            dto.setStepType(s.getStepType());
            dto.setStepOrder(s.getStepOrder());
            dto.setConfigType(s.getConfigType());
            dto.setConfigContent(s.getConfigContent());
            return dto;
        }).collect(Collectors.toList())).build();
    }

    public String getScheduleDescription(ScheduleType scheduleType, DayOfWeek scheduleDay, Integer scheduleDayOfMonth, LocalTime scheduleTime, String cronExpression) {
        if (scheduleType == null) {
            return "";
        }

        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");

        switch (scheduleType) {
            case MANUAL:
                return "Manual Only";
            case HOURLY:
                return "Every hour";
            case DAILY:
                return scheduleTime != null ? "Daily at " + scheduleTime.format(timeFmt) : "Daily";
            case WEEKLY:
                if (scheduleDay != null) {
                    String dayPlural = scheduleDay.getDisplayName(TextStyle.FULL, Locale.ENGLISH) + "s";
                    return scheduleTime != null ? dayPlural + " at " + scheduleTime.format(timeFmt) : dayPlural;
                }
                return "Weekly";
            case MONTHLY:
                if (scheduleDayOfMonth != null) {
                    return scheduleTime != null ? "Day " + scheduleDayOfMonth + " at " + scheduleTime.format(timeFmt) : "Day " + scheduleDayOfMonth;
                }
                return "Monthly";
            case CUSTOM:
                return cronExpression != null ? cronExpression : "Custom Schedule";
            default:
                return scheduleType.name();
        }
    }
}

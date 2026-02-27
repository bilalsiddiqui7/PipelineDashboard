package com.pipeline.service;

import com.pipeline.dto.pipeline.PipelineRequestDto;
import com.pipeline.dto.pipeline.PipelineResponseDto;
import com.pipeline.dto.pipeline.PipelineStepDto;
import com.pipeline.dto.pipeline.PipelineSummaryDto;
import com.pipeline.enums.ConfigType;
import com.pipeline.enums.ScheduleType;
import com.pipeline.exception.PipelineNotFoundException;
import com.pipeline.model.Pipeline;
import com.pipeline.model.PipelineStep;
import com.pipeline.repository.PipelineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PipelineServiceImpl implements PipelineService {

    private final PipelineRepository pipelineRepository;

    private final S3StorageService s3StorageService;

    @Override
    public List<PipelineSummaryDto> getAllPipelines() {
        return pipelineRepository.findAll().stream().map(p -> PipelineSummaryDto.builder().id(p.getId()).name(p.getName()).description(p.getDescription()).enabled(p.isEnabled()).scheduleDescription(getScheduleDescription(p.getScheduleType(), p.getScheduleDay(), p.getScheduleDayOfMonth(), p.getScheduleTime(), p.getCronExpression())).stepCount(p.getSteps().size()).stepTypes(p.getSteps().stream().map(step -> step.getStepType()).distinct().toList()).build()).collect(Collectors.toList());
    }

    @Override
    public PipelineResponseDto getPipeline(Long id) {
        Pipeline pipeline = pipelineRepository.findById(id).orElseThrow(() -> new PipelineNotFoundException(id));

        return mapToResponse(pipeline);
    }

    @Override
    @Transactional
    public PipelineResponseDto createPipeline(PipelineRequestDto request) {

        //create basic pipeline and save to get ID
        Pipeline pipeline =
                Pipeline.builder().name(request.getName()).description(request.getDescription()).enabled(request.isEnabled()).scheduleType(request.getScheduleType()).scheduleTime(request.getScheduleTime()).scheduleDay(request.getScheduleDay()).scheduleDayOfMonth(request.getScheduleDayOfMonth()).cronExpression(request.getCronExpression()).createdAt(Instant.now()).build();
        pipeline = pipelineRepository.save(pipeline);

        List<PipelineStep> steps = new ArrayList<>();
        for (PipelineStepDto stepDto : request.getSteps()) {
            PipelineStep step =
                    PipelineStep.builder().stepName(stepDto.getStepName()).stepType(stepDto.getStepType()).stepOrder(stepDto.getStepOrder()).configType(stepDto.getConfigType()).pipeline(pipeline).build();

            String content = stepDto.getConfigContent();
            if (content != null && !content.isBlank()) {
                String key = generateS3Key(pipeline.getId(), step.getStepOrder(), stepDto.getConfigType());
                String contentType = stepDto.getConfigType() == ConfigType.YAML ? "text/yaml" : "application/json";
                s3StorageService.upload(key, content, contentType);
                step.setConfigS3Key(key);
                step.setConfigContent(content);
            }
            steps.add(step);
        }
        pipeline.setSteps(steps);
        Pipeline saved = pipelineRepository.save(pipeline);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public PipelineResponseDto updatePipeline(Long id, PipelineRequestDto dto) {
        Pipeline pipeline = pipelineRepository.findById(id).orElseThrow(() -> new PipelineNotFoundException(id));

        // Collect old S3 keys for cleanup
        List<String> oldS3Keys =
                pipeline.getSteps().stream().map(PipelineStep::getConfigS3Key).filter(key -> key != null && !key.isBlank()).collect(Collectors.toList());

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

        // Process new steps and upload to S3
        List<PipelineStep> newSteps = new ArrayList<>();
        for (PipelineStepDto stepDto : dto.getSteps()) {
            PipelineStep step =
                    PipelineStep.builder().stepName(stepDto.getStepName()).stepType(stepDto.getStepType()).stepOrder(stepDto.getStepOrder()).configType(stepDto.getConfigType()).pipeline(pipeline).build();

            String content = stepDto.getConfigContent();
            if (content != null && !content.isBlank()) {
                String key = generateS3Key(pipeline.getId(), step.getStepOrder(), stepDto.getConfigType());
                String contentType = stepDto.getConfigType() == ConfigType.YAML ? "text/yaml" : "application/json";
                s3StorageService.upload(key, content, contentType);
                step.setConfigS3Key(key);
                step.setConfigContent(content);
            }
            newSteps.add(step);
        }
        pipeline.getSteps().addAll(newSteps);

        Pipeline saved = pipelineRepository.save(pipeline);

        // Delete old S3 objects after successful save
        oldS3Keys.forEach(key -> {
            try {
                s3StorageService.delete(key);
            } catch (Exception e) {
                System.err.println("Failed to delete old S3 object: " + key);
            }
        });

        return mapToResponse(saved);
    }

    @Override
    public void togglePipeline(Long id, boolean enabled) {
        Pipeline pipeline = pipelineRepository.findById(id).orElseThrow(() -> new PipelineNotFoundException(id));
        pipeline.setEnabled(enabled);
        pipelineRepository.save(pipeline);
    }

    @Override
    @Transactional
    public void deletePipeline(Long id) {
        Pipeline pipeline = pipelineRepository.findById(id).orElseThrow(() -> new PipelineNotFoundException(id));

        // Delete S3 objects for all steps
        pipeline.getSteps().stream().map(PipelineStep::getConfigS3Key).filter(key -> key != null && !key.isBlank()).forEach(key -> {
            try {
                s3StorageService.delete(key);
            } catch (Exception e) {
                // Log but don't fail the deletion
                System.err.println("Failed to delete S3 object: " + key);
            }
        });

        pipelineRepository.deleteById(id);
    }

    @Override
    @Transactional
    public PipelineResponseDto copyPipeline(Long id) {
        Pipeline original = pipelineRepository.findById(id).orElseThrow(() -> new PipelineNotFoundException(id));

        Pipeline copy =
                Pipeline.builder().name(original.getName() + " (Copy)").description(original.getDescription()).enabled(false).scheduleType(original.getScheduleType()).scheduleTime(original.getScheduleTime()).scheduleDay(original.getScheduleDay()).scheduleDayOfMonth(original.getScheduleDayOfMonth()).cronExpression(original.getCronExpression()).createdAt(Instant.now()).updatedAt(Instant.now()).build();

        // Save to get ID for S3 key generation
        copy = pipelineRepository.save(copy);

        List<PipelineStep> copiedSteps = new ArrayList<>();
        for (PipelineStep originalStep : original.getSteps()) {
            PipelineStep.PipelineStepBuilder stepBuilder =
                    PipelineStep.builder().stepName(originalStep.getStepName()).stepType(originalStep.getStepType()).stepOrder(originalStep.getStepOrder()).configType(originalStep.getConfigType()).pipeline(copy);

            String content = null;
            if (originalStep.getConfigS3Key() != null && !originalStep.getConfigS3Key().isBlank()) {
                // Config stored in S3, fetch it
                content = s3StorageService.get(originalStep.getConfigS3Key()).orElse(null);
            }

            if (content != null && !content.isBlank()) {
                // Upload to S3 with new key for the copy
                String key = generateS3Key(copy.getId(), originalStep.getStepOrder(), originalStep.getConfigType());
                String contentType = originalStep.getConfigType() == ConfigType.YAML ? "text/yaml" : "application/json";
                s3StorageService.upload(key, content, contentType);
                stepBuilder.configS3Key(key);
                stepBuilder.configContent(content);
            }

            copiedSteps.add(stepBuilder.build());
        }

        copy.setSteps(copiedSteps);
        Pipeline saved = pipelineRepository.save(copy);

        return mapToResponse(saved);
    }


    private PipelineResponseDto mapToResponse(Pipeline pipeline) {
        return PipelineResponseDto.builder().id(pipeline.getId()).name(pipeline.getName()).description(pipeline.getDescription()).enabled(pipeline.isEnabled()).scheduleType(pipeline.getScheduleType()).scheduleTime(pipeline.getScheduleTime()).scheduleDay(pipeline.getScheduleDay()).scheduleDayOfMonth(pipeline.getScheduleDayOfMonth()).cronExpression(pipeline.getCronExpression()).steps(pipeline.getSteps().stream().map(s -> {
            PipelineStepDto dto = new PipelineStepDto();
            dto.setStepName(s.getStepName());
            dto.setStepType(s.getStepType());
            dto.setStepOrder(s.getStepOrder());
            dto.setConfigType(s.getConfigType());

            String config = null;
            if (s.getConfigS3Key() != null && !s.getConfigS3Key().isBlank()) {
                config = s3StorageService.get(s.getConfigS3Key()).orElse("");
            }
            dto.setConfigContent(config);

            return dto;
        }).collect(Collectors.toList())).build();
    }

    public String getScheduleDescription(ScheduleType scheduleType, DayOfWeek scheduleDay, Integer scheduleDayOfMonth
            , LocalTime scheduleTime, String cronExpression) {
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
                    return scheduleTime != null ?
                            "Day " + scheduleDayOfMonth + " at " + scheduleTime.format(timeFmt) :
                            "Day " + scheduleDayOfMonth;
                }
                return "Monthly";
            case CUSTOM:
                return cronExpression != null ? cronExpression : "Custom Schedule";
            default:
                return scheduleType.name();
        }
    }

    private String generateS3Key(Long pipelineId, Integer stepOrder, ConfigType configType) {
        String ext = configType == ConfigType.YAML ? ".yaml" : ".json";
        return String.format("pipelines/%d/steps/%d-%s%s", pipelineId, stepOrder, UUID.randomUUID(), ext);
    }
}

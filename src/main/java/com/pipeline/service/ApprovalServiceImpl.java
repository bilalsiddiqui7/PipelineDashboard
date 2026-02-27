package com.pipeline.service;

import com.pipeline.dto.approval.ApprovalActionDto;
import com.pipeline.dto.approval.ApprovalRequestDetailDto;
import com.pipeline.dto.approval.ApprovalRequestSummaryDto;
import com.pipeline.dto.approval.ApprovalStatsDto;
import com.pipeline.dto.approval.RecentDecisionDto;
import com.pipeline.enums.ApprovalStatus;
import com.pipeline.enums.TimeFilter;
import com.pipeline.model.DEApprovalRequest;
import com.pipeline.repository.DEApprovalRequestRepository;
import com.pipeline.repository.WorkflowThresholdConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApprovalServiceImpl implements ApprovalService {

    private final DEApprovalRequestRepository approvalRepository;
    private final WorkflowThresholdConfigRepository workflowThresholdConfigRepository;

    @Override
    public List<ApprovalRequestSummaryDto> getPendingApprovals() {
        log.info("Fetching pending DE approval requests");
        return approvalRepository.findByStatus(ApprovalStatus.PENDING).stream()
                .map(this::mapToSummaryDto)
                .collect(Collectors.toList());
    }

    @Override
    public ApprovalRequestDetailDto getApprovalById(Long id) {
        log.info("Fetching DE approval request by ID: {}", id);
        DEApprovalRequest approval = approvalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Approval request not found: " + id));

        // Fetch actual threshold values from WorkflowThresholdConfig
        String actualThresholdValues = fetchActualThresholdValues(approval.getPipelineId());

        return mapToDetailDto(approval, actualThresholdValues);
    }

    @Override
    @Transactional
    public ApprovalRequestSummaryDto approveRequest(Long id, ApprovalActionDto actionDto) {
        log.info("Approving DE approval request ID: {}", id);

        DEApprovalRequest approval = approvalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Approval request not found: " + id));

        if (approval.getStatus() != ApprovalStatus.PENDING) {
            throw new RuntimeException("Approval request is not in PENDING status. Current status: " + approval.getStatus());
        }

        approval.setStatus(ApprovalStatus.APPROVED);
        approval.setReason(actionDto.getReason());
        approval.setReviewedBy(actionDto.getReviewedBy());
        approval.setReviewedAt(Instant.now());

        approvalRepository.save(approval);
        log.info("DE approval request approved: {}", id);

        return mapToSummaryDto(approval);
    }

    @Override
    @Transactional
    public ApprovalRequestSummaryDto rejectRequest(Long id, ApprovalActionDto actionDto) {
        log.info("Rejecting DE approval request ID: {}", id);

        DEApprovalRequest approval = approvalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Approval request not found: " + id));

        if (approval.getStatus() != ApprovalStatus.PENDING) {
            throw new RuntimeException("Approval request is not in PENDING status. Current status: " + approval.getStatus());
        }

        approval.setStatus(ApprovalStatus.REJECTED);
        approval.setReason(actionDto.getReason());
        approval.setReviewedBy(actionDto.getReviewedBy());
        approval.setReviewedAt(Instant.now());

        approvalRepository.save(approval);
        log.info("DE approval request rejected: {}", id);

        return mapToSummaryDto(approval);
    }

    @Override
    @Transactional
    public ApprovalRequestSummaryDto partialApproveRequest(Long id, ApprovalActionDto actionDto) {
        log.info("Partial approving DE approval request ID: {}", id);

        DEApprovalRequest approval = approvalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Approval request not found: " + id));

        if (approval.getStatus() != ApprovalStatus.PENDING) {
            throw new RuntimeException("Approval request is not in PENDING status. Current status: " + approval.getStatus());
        }

        approval.setStatus(ApprovalStatus.PARTIAL_APPROVAL);
        approval.setReason(actionDto.getReason());
        approval.setReviewedBy(actionDto.getReviewedBy());
        approval.setReviewedAt(Instant.now());

        approvalRepository.save(approval);
        log.info("DE approval request partially approved: {}", id);

        return mapToSummaryDto(approval);
    }

    @Override
    public ApprovalStatsDto getApprovalStats() {
        log.info("Fetching DE approval statistics");

        long pendingCount = approvalRepository.countByStatus(ApprovalStatus.PENDING);
        long approvedCount = approvalRepository.countByStatus(ApprovalStatus.APPROVED);
        long rejectedCount = approvalRepository.countByStatus(ApprovalStatus.REJECTED);
        long partialApprovalCount = approvalRepository.countByStatus(ApprovalStatus.PARTIAL_APPROVAL);

        return ApprovalStatsDto.builder()
                .pendingCount(pendingCount)
                .approvedCount(approvedCount)
                .rejectedCount(rejectedCount)
                .partialApprovalCount(partialApprovalCount)
                .build();
    }

    @Override
    public List<RecentDecisionDto> getRecentDecisions(int limit) {
        log.info("Fetching recent {} DE decisions", limit);

        List<ApprovalStatus> decisionStatuses = Arrays.asList(
                ApprovalStatus.APPROVED,
                ApprovalStatus.REJECTED,
                ApprovalStatus.PARTIAL_APPROVAL
        );

        List<DEApprovalRequest> recentApprovals = approvalRepository.findRecentDecisions(decisionStatuses);

        return recentApprovals.stream()
                .limit(limit)
                .map(this::mapToRecentDecisionDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RecentDecisionDto> getRecentDecisionsByFilter(String filter) {
        log.info("Fetching recent DE decisions with filter: {}", filter);

        TimeFilter timeFilter = TimeFilter.fromString(filter);
        Instant startTime = timeFilter.getStartTime();

        List<ApprovalStatus> decisionStatuses = Arrays.asList(
                ApprovalStatus.APPROVED,
                ApprovalStatus.REJECTED,
                ApprovalStatus.PARTIAL_APPROVAL
        );

        List<DEApprovalRequest> recentApprovals = approvalRepository.findRecentDecisionsByTimePeriod(
                decisionStatuses,
                startTime
        );

        log.info("Found {} decisions in the {} period", recentApprovals.size(), timeFilter.getDisplayName());

        return recentApprovals.stream()
                .map(this::mapToRecentDecisionDto)
                .collect(Collectors.toList());
    }

    private ApprovalRequestSummaryDto mapToSummaryDto(DEApprovalRequest entity) {
        return ApprovalRequestSummaryDto.builder()
                .id(entity.getId())
                .pipelineId(entity.getPipelineId())
                .stage(entity.getStage())
                .requestTitle(entity.getRequestTitle())
                .status(entity.getStatus())
                .pipelineRunId(entity.getPipelineRunId())
                .slaDeadline(entity.getSlaDeadline())
                .datasetCount(entity.getDatasetCount())
                .totalRecords(entity.getTotalRecords())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private ApprovalRequestDetailDto mapToDetailDto(DEApprovalRequest entity, String actualThresholdValues) {
        return ApprovalRequestDetailDto.builder()
                .id(entity.getId())
                .pipelineId(entity.getPipelineId())
                .stage(entity.getStage())
                .requestTitle(entity.getRequestTitle())
                .requestDescription(entity.getRequestDescription())
                .configurationDetails(entity.getConfigurationDetails())
                .actualThresholdValues(actualThresholdValues)
                .status(entity.getStatus())
                .reason(entity.getReason())
                .requestedBy(entity.getRequestedBy())
                .reviewedBy(entity.getReviewedBy())
                .pipelineRunId(entity.getPipelineRunId())
                .slaDeadline(entity.getSlaDeadline())
                .datasetCount(entity.getDatasetCount())
                .totalRecords(entity.getTotalRecords())
                .createdAt(entity.getCreatedAt())
                .reviewedAt(entity.getReviewedAt())
                .build();
    }

    /**
     * Fetch actual threshold values from WorkflowThresholdConfig for the pipeline
     * Returns JSON string with field names matching configurationDetails for easy UI comparison
     */
    private String fetchActualThresholdValues(String pipelineId) {
        log.debug("Fetching actual threshold values for pipeline: {}", pipelineId);

        return workflowThresholdConfigRepository.findByPipelineId(pipelineId)
                .map(config -> {
                    Map<String, Object> thresholds = new HashMap<>();

                    // Use field names matching configurationDetails (without "Threshold" suffix)
                    if (config.getRecordCountVariancePercent() != null) {
                        thresholds.put("recordCountVariancePercent", config.getRecordCountVariancePercent());
                    }
                    if (config.getVolumeAnomalyVsBaselinePercent() != null) {
                        thresholds.put("volumeAnomalyVsBaselinePercent", config.getVolumeAnomalyVsBaselinePercent());
                    }
                    if (config.getMandatoryColumnNullsPercent() != null) {
                        thresholds.put("mandatoryColumnNullsPercent", config.getMandatoryColumnNullsPercent());
                    }
                    if (config.getDuplicateBusinessKeysPercent() != null) {
                        thresholds.put("duplicateBusinessKeysPercent", config.getDuplicateBusinessKeysPercent());
                    }
                    if (config.getFormatViolationsPercent() != null) {
                        thresholds.put("formatViolationsPercent", config.getFormatViolationsPercent());
                    }
                    if (config.getBreakingSchemaChangesAllowed() != null) {
                        thresholds.put("breakingSchemaChangesAllowed", config.getBreakingSchemaChangesAllowed());
                    }
                    if (config.getDataFreshnessDelayMinutes() != null) {
                        thresholds.put("dataFreshnessDelayMinutes", config.getDataFreshnessDelayMinutes());
                    }

                    // Convert map to JSON string
                    try {
                        return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(thresholds);
                    } catch (Exception e) {
                        log.error("Error converting thresholds to JSON", e);
                        return "{}";
                    }
                })
                .orElse("{}"); // Return empty JSON if no config found
    }

    private RecentDecisionDto mapToRecentDecisionDto(DEApprovalRequest entity) {
        return RecentDecisionDto.builder()
                .approvalId(entity.getId())
                .pipelineId(entity.getPipelineId())
                .stage(entity.getStage())
                .status(entity.getStatus())
                .reason(entity.getReason())
                .decidedAt(entity.getReviewedAt() != null ? entity.getReviewedAt() : entity.getCreatedAt())
                .build();
    }
}

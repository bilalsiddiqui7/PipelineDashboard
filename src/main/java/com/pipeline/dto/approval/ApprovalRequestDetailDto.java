package com.pipeline.dto.approval;

import com.pipeline.enums.ApprovalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * DTO for DE team approval request details
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalRequestDetailDto {

    private Long id;
    private String pipelineId;
    private String stage;
    private String requestTitle;
    private String requestDescription;
    private String configurationDetails;
    private String actualThresholdValues;
    private ApprovalStatus status;
    private String reason;
    private String requestedBy;
    private String reviewedBy;
    private String pipelineRunId;
    private Instant slaDeadline;
    private Integer datasetCount;
    private Long totalRecords;
    private Instant createdAt;
    private Instant reviewedAt;
}

package com.pipeline.dto.approval;

import com.pipeline.enums.ApprovalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * DTO for DE team approval request summary (list view)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalRequestSummaryDto {

    private Long id;
    private String pipelineId;
    private String stage;
    private String requestTitle;
    private ApprovalStatus status;
    private String pipelineRunId;
    private Instant slaDeadline;
    private Integer datasetCount;
    private Long totalRecords;
    private Instant createdAt;
}

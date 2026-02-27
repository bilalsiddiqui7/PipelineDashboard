package com.pipeline.dto.approval;

import com.pipeline.enums.ApprovalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * DTO for recent approval decisions
 * Shows recently approved or rejected configurations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecentDecisionDto {

    private Long approvalId;
    private String pipelineId;
    private String stage;
    private ApprovalStatus status;
    private String reason;
    private Instant decidedAt;
}

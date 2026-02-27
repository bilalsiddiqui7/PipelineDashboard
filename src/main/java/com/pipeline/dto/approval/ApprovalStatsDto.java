package com.pipeline.dto.approval;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalStatsDto {

    private long pendingCount;
    private long approvedCount;
    private long rejectedCount;
    private long partialApprovalCount;
}

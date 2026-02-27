package com.pipeline.dto.approval;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for approval action (approve/reject/partial approve)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalActionDto {

    private String reviewedBy;
    private String reason; // Reason for approve/reject/partial approval
}

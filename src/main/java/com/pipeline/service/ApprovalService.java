package com.pipeline.service;

import com.pipeline.dto.approval.ApprovalActionDto;
import com.pipeline.dto.approval.ApprovalRequestDetailDto;
import com.pipeline.dto.approval.ApprovalRequestSummaryDto;
import com.pipeline.dto.approval.ApprovalStatsDto;
import com.pipeline.dto.approval.RecentDecisionDto;

import java.util.List;

/**
 * Service for managing DE team approval requests
 */
public interface ApprovalService {

    /**
     * Get all pending approval requests
     */
    List<ApprovalRequestSummaryDto> getPendingApprovals();

    /**
     * Get approval request by ID
     */
    ApprovalRequestDetailDto getApprovalById(Long id);

    /**
     * Approve a request
     */
    ApprovalRequestSummaryDto approveRequest(Long id, ApprovalActionDto actionDto);

    /**
     * Reject a request
     */
    ApprovalRequestSummaryDto rejectRequest(Long id, ApprovalActionDto actionDto);

    /**
     * Partial approve a request
     */
    ApprovalRequestSummaryDto partialApproveRequest(Long id, ApprovalActionDto actionDto);

    /**
     * Get approval statistics
     */
    ApprovalStatsDto getApprovalStats();

    /**
     * Get recent decisions (approved/rejected/partial)
     */
    List<RecentDecisionDto> getRecentDecisions(int limit);

    /**
     * Get recent decisions filtered by time period
     */
    List<RecentDecisionDto> getRecentDecisionsByFilter(String filter);
}

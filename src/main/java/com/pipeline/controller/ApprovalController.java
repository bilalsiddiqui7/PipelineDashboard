package com.pipeline.controller;

import com.pipeline.dto.approval.ApprovalActionDto;
import com.pipeline.dto.approval.ApprovalRequestDetailDto;
import com.pipeline.dto.approval.ApprovalRequestSummaryDto;
import com.pipeline.dto.approval.ApprovalStatsDto;
import com.pipeline.dto.approval.RecentDecisionDto;
import com.pipeline.service.ApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for DE team approval workflow
 */
@RestController
@CrossOrigin("*")
@RequestMapping("workflow-approval/approval-inbox")
@RequiredArgsConstructor
public class ApprovalController {

    private final ApprovalService approvalService;

    /**
     * Get all pending DE approval requests
     *
     * @return List of pending approvals
     */
    @GetMapping("/pending")
    public ResponseEntity<List<ApprovalRequestSummaryDto>> getPendingApprovals() {
        List<ApprovalRequestSummaryDto> approvals = approvalService.getPendingApprovals();
        return ResponseEntity.ok(approvals);
    }

    /**
     * Get DE approval request by ID
     *
     * @param id Approval request ID
     * @return Approval request details
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApprovalRequestDetailDto> getApprovalById(@PathVariable Long id) {
        ApprovalRequestDetailDto approval = approvalService.getApprovalById(id);
        return ResponseEntity.ok(approval);
    }

    /**
     * Approve a DE approval request
     *
     * @param id        Approval request ID
     * @param actionDto Action details
     * @return Updated approval request
     */
    @PostMapping("/{id}/approve")
    public ResponseEntity<ApprovalRequestSummaryDto> approveRequest(
            @PathVariable Long id,
            @RequestBody ApprovalActionDto actionDto) {
        ApprovalRequestSummaryDto approved = approvalService.approveRequest(id, actionDto);
        return ResponseEntity.ok(approved);
    }

    /**
     * Reject a DE approval request
     *
     * @param id        Approval request ID
     * @param actionDto Action details (must include rejectionReason)
     * @return Updated approval request
     */
    @PostMapping("/{id}/reject")
    public ResponseEntity<ApprovalRequestSummaryDto> rejectRequest(
            @PathVariable Long id,
            @RequestBody ApprovalActionDto actionDto) {
        ApprovalRequestSummaryDto rejected = approvalService.rejectRequest(id, actionDto);
        return ResponseEntity.ok(rejected);
    }

    /**
     * Partial approve a DE approval request
     *
     * @param id        Approval request ID
     * @param actionDto Action details (may include partialApprovalNotes)
     * @return Updated approval request
     */
    @PostMapping("/{id}/partial-approve")
    public ResponseEntity<ApprovalRequestSummaryDto> partialApproveRequest(
            @PathVariable Long id,
            @RequestBody ApprovalActionDto actionDto) {
        ApprovalRequestSummaryDto partialApproved = approvalService.partialApproveRequest(id, actionDto);
        return ResponseEntity.ok(partialApproved);
    }

    /**
     * Get approval statistics
     *
     * @return Counts by status (pending, approved, rejected, partial approval)
     */
    @GetMapping("/stats")
    public ResponseEntity<ApprovalStatsDto> getApprovalStats() {
        ApprovalStatsDto stats = approvalService.getApprovalStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * Get recent approval decisions filtered by time period
     *
     * @param filter Time filter (LAST_24_HOURS, LAST_7_DAYS, LAST_1_MONTH) - default: LAST_7_DAYS
     * @return List of recent decisions within the specified time period
     */
    @GetMapping("/recent-decisions")
    public ResponseEntity<List<RecentDecisionDto>> getRecentDecisions(
            @RequestParam(defaultValue = "LAST_7_DAYS") String filter) {
        List<RecentDecisionDto> recentDecisions = approvalService.getRecentDecisionsByFilter(filter);
        return ResponseEntity.ok(recentDecisions);
    }
}

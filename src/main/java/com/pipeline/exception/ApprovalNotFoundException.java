package com.pipeline.exception;

public class ApprovalNotFoundException extends RuntimeException {
    public ApprovalNotFoundException(Long approvalId) {
        super("Approval request not found with Approval ID: " + approvalId);
    }
}

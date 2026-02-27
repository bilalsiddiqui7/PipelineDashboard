package com.pipeline.exception;

public class PendingApprovalExistsException extends RuntimeException {
    public PendingApprovalExistsException(String pipelineId) {
        super("There is already a pending approval request for pipeline: " + pipelineId);
    }
}

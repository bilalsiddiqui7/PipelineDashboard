package com.pipeline.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    //    TODO - WORK IN PROGRESS
    @ExceptionHandler(PipelineNotFoundException.class)
    public ResponseEntity<String> handleNotFound(PipelineNotFoundException ex) {
        log.error("Pipeline not found", ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequest(IllegalArgumentException ex) {
        log.error("Validation error", ex);
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(AirflowConnectionException.class)
    public ResponseEntity<String> handleAirflowConnectionError(AirflowConnectionException ex) {
        log.error("Airflow connection error", ex);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(ex.getMessage());
    }

    @ExceptionHandler(ApprovalNotFoundException.class)
    public ResponseEntity<String> handleApprovalNotFound(ApprovalNotFoundException ex) {
        log.error("Approval not found", ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(PendingApprovalExistsException.class)
    public ResponseEntity<String> handlePendingApprovalExists(PendingApprovalExistsException ex) {
        log.error("Pending approval already exists", ex);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidApprovalStatusException.class)
    public ResponseEntity<String> handleInvalidApprovalStatus(InvalidApprovalStatusException ex) {
        log.error("Invalid approval status", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneral(Exception ex) {
        log.error("Unexpected error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred: " + ex.getMessage());
    }
}


package com.pipeline.model;

import com.pipeline.enums.ApprovalStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/**
 * Entity for Data Engineering team approval requests
 * DE team inserts data here with PENDING status
 */
@Entity
@Table(name = "de_approval_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DEApprovalRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pipeline_id", nullable = false)
    private String pipelineId;

    @Column(nullable = false)
    private String stage;

    @Column(name = "request_title")
    private String requestTitle;

    @Column(name = "request_description", length = 1000)
    private String requestDescription;

    @Column(name = "configuration_details", columnDefinition = "TEXT")
    private String configurationDetails; // JSON string of configuration

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus status;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason; // Reason for approval/rejection/partial approval

    @Column(name = "requested_by")
    private String requestedBy; // DE team member who created this

    @Column(name = "reviewed_by")
    private String reviewedBy; // Who approved/rejected

    @Column(name = "pipeline_run_id")
    private String pipelineRunId; // Pipeline run identifier (e.g., RUN-20260121-064530)

    @Column(name = "sla_deadline")
    private Instant slaDeadline; // SLA deadline for this approval request

    @Column(name = "dataset_count")
    private Integer datasetCount; // Number of datasets involved (e.g., 2)

    @Column(name = "total_records")
    private Long totalRecords; // Total number of records (e.g., 567890)

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "reviewed_at")
    private Instant reviewedAt;

    @PrePersist
    protected void onCreate() {
        if (status == null) {
            status = ApprovalStatus.PENDING;
        }
    }
}

package com.pipeline.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Entity representing workflow threshold configuration from external database
 * Maps to workflow_threshold_config table in wids_platform_control database
 */
@Entity
@Table(name = "workflow_threshold_config")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowThresholdConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pipeline_id", nullable = false)
    private String pipelineId;

    @Column(name = "record_count_variance_percent")
    private Double recordCountVariancePercent;

    @Column(name = "volume_anomaly_vs_baseline_percent")
    private Double volumeAnomalyVsBaselinePercent;

    @Column(name = "mandatory_column_nulls_percent")
    private Double mandatoryColumnNullsPercent;

    @Column(name = "duplicate_business_keys_percent")
    private Double duplicateBusinessKeysPercent;

    @Column(name = "format_violations_percent")
    private Double formatViolationsPercent;

    @Column(name = "breaking_schema_changes_allowed")
    private Integer breakingSchemaChangesAllowed;

    @Column(name = "data_freshness_delay_minutes")
    private Integer dataFreshnessDelayMinutes;

    @Column(name = "created_at")
    private Instant createdAt;
}

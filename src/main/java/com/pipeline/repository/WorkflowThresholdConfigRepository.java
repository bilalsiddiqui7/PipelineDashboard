package com.pipeline.repository;

import com.pipeline.model.WorkflowThresholdConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for WorkflowThresholdConfig entity
 * Connects to workflow_threshold_config table in wids_platform_control database
 */
@Repository
public interface WorkflowThresholdConfigRepository extends JpaRepository<WorkflowThresholdConfig, Long> {

    /**
     * Find workflow threshold configuration by pipeline ID
     *
     * @param pipelineId The pipeline ID
     * @return Optional containing the config if found
     */
    Optional<WorkflowThresholdConfig> findByPipelineId(String pipelineId);
}

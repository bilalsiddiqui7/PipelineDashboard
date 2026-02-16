package com.pipeline.repository;

import com.pipeline.model.PipelineStep;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PipelineStepRepository extends JpaRepository<PipelineStep, Long> {
}

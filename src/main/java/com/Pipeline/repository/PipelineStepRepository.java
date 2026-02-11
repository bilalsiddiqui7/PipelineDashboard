package com.Pipeline.repository;

import com.Pipeline.model.PipelineStep;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PipelineStepRepository extends JpaRepository<PipelineStep, Long> {
}

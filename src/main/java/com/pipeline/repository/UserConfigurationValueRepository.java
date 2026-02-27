package com.pipeline.repository;

import com.pipeline.model.UserConfigurationValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserConfigurationValueRepository extends JpaRepository<UserConfigurationValue, Long> {

    List<UserConfigurationValue> findByPipelineId(String pipelineId);

    Optional<UserConfigurationValue> findByPipelineIdAndParameterId(String pipelineId, Long parameterId);

    void deleteByPipelineIdAndParameterId(String pipelineId, Long parameterId);
}

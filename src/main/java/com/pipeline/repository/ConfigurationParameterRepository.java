package com.pipeline.repository;

import com.pipeline.model.ConfigurationParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfigurationParameterRepository extends JpaRepository<ConfigurationParameter, Long> {

    List<ConfigurationParameter> findByActiveTrue();

    Optional<ConfigurationParameter> findByParameterName(String parameterName);
}

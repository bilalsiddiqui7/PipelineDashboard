package com.pipeline.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/**
 * Entity for storing user-configured values for parameters
 * Links pipeline + parameter + configured value
 */
@Entity
@Table(name = "user_configuration_values",
       uniqueConstraints = @UniqueConstraint(columnNames = {"pipeline_id", "parameter_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserConfigurationValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pipeline_id", nullable = false)
    private String pipelineId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parameter_id", nullable = false)
    private ConfigurationParameter parameter;

    @Column(name = "configured_value", nullable = false)
    private String configuredValue;

    @Column(name = "configured_by")
    private String configuredBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}

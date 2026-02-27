package com.pipeline.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/**
 * Entity for storing configuration parameter definitions (metadata)
 * These are the available parameters that users can select and configure
 */
@Entity
@Table(name = "configuration_parameters")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfigurationParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String parameterName;

    @Column(nullable = false)
    private String displayName;

    @Column(nullable = false)
    private String dataType; // DOUBLE, INTEGER, STRING, BOOLEAN

    @Column(nullable = false)
    private boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}

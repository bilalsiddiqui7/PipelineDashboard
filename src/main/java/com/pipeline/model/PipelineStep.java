package com.pipeline.model;

import com.pipeline.enums.ConfigType;
import com.pipeline.enums.StepType;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pipeline_steps")
@Builder(toBuilder = true)
public class PipelineStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stepName;

    @Enumerated(EnumType.STRING)
    private StepType stepType;

    private int stepOrder;

    @Enumerated(EnumType.STRING)
    private ConfigType configType;

    @Column(name = "config_s3_key")
    private String configS3Key;

//    @Lob
    @Column(name = "config_content", columnDefinition = "TEXT")
    private String configContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pipeline_id")
    private Pipeline pipeline;

    // Automatically set on insert
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    // Automatically set on insert and update
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}


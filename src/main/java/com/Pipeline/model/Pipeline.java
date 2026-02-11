package com.Pipeline.model;

import com.Pipeline.enums.ScheduleType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.*;
import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pipelines")
@Builder(toBuilder = true)
public class Pipeline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    private boolean enabled;

    @Enumerated(EnumType.STRING)
    private ScheduleType scheduleType; //  Frequency - MANUAL,HOURLY,DAILY,WEEKLY,MONTHLY,CUSTOM

    private DayOfWeek scheduleDay; // for weekly (Monday-Sunday)

    @Min(1)
    @Max(31)
    private Integer scheduleDayOfMonth; // for monthly (1-31)

    // TODO - Cron Expression

    private LocalTime scheduleTime;

    // Automatically set on insert
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    // Automatically set on insert and update
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(mappedBy = "pipeline", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("stepOrder ASC")
    private List<PipelineStep> steps = new ArrayList<>();
}


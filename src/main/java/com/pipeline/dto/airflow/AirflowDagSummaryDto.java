package com.pipeline.dto.airflow;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Simplified DTO for displaying Airflow DAGs in the UI
 */
@Data
@Builder
public class AirflowDagSummaryDto {
    private String dagId;
    private String description;
}

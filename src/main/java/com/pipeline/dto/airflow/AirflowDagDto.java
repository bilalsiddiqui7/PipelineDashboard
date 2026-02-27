package com.pipeline.dto.airflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AirflowDagDto {

    @JsonProperty("dag_id")
    private String dagId;

    @JsonProperty("is_paused")
    private Boolean isPaused;

    @JsonProperty("description")
    private String description;

    @JsonProperty("tags")
    private List<Map<String, String>> tags;
}

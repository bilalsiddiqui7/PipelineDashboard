package com.pipeline.dto.airflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AirflowDagListResponseDto {

    @JsonProperty("dags")
    private List<AirflowDagDto> dags;

    @JsonProperty("total_entries")
    private Integer totalEntries;
}

package com.pipeline.dto.airflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AirflowAuthRequestDto {

    @JsonProperty("username")
    private String username;

    @JsonProperty("password")
    private String password;
}

package com.pipeline.dto.airflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AirflowTokenResponseDto {

    @JsonProperty("access_token")
    private String accessToken;
}

package com.pipeline.service;

import com.pipeline.dto.airflow.AirflowDagDto;
import com.pipeline.dto.airflow.AirflowDagSummaryDto;

import java.util.List;

public interface AirflowService {

    /**
     * Fetch all DAGs from Airflow
     * @return List of Airflow DAG summaries
     */
    List<AirflowDagSummaryDto> getAllAirflowDags();

    /**
     * Pause/Unpause a DAG in Airflow
     * @param dagId The DAG ID
     * @param isPaused Whether to pause or unpause
     */
    void toggleAirflowDag(String dagId, boolean isPaused);
}

package com.Pipeline.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum StepType {
    DATA_INGESTION("Data Ingestion"),
    DATA_TRANSFORMATION("Data Transformation"),
    DATA_VALIDATION("Data Validation"),
    MACHINE_LEARNING("Machine Learning"),
    DATA_EXPORT("Data Export"),
    NOTIFICATION("Notification"),
    API_CALL("Api Call"),
    FILE_PROCESSING("File Processing");

    private final String display;

    StepType(String display) {
        this.display = display;
    }

    @JsonValue
    @Override
    public String toString() {
        return display;
    }

}

package com.pipeline.exception;

public class PipelineNotFoundException extends RuntimeException {
    public PipelineNotFoundException(Long id) {
        super("Pipeline not found with id: " + id);
    }
}

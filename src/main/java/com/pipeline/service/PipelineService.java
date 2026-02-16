package com.pipeline.service;

import com.pipeline.dto.PipelineRequestDto;
import com.pipeline.dto.PipelineResponseDto;
import com.pipeline.dto.PipelineSummaryDto;

import java.util.List;

public interface PipelineService {

    List<PipelineSummaryDto> getAllPipelines();

    PipelineResponseDto getPipeline(Long id);

    PipelineResponseDto createPipeline(PipelineRequestDto dto);

    PipelineResponseDto updatePipeline(Long id, PipelineRequestDto dto);

    void togglePipeline(Long id, boolean enabled);

    void deletePipeline(Long id);

    PipelineResponseDto copyPipeline(Long id);
}


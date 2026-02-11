package com.Pipeline.service;

import com.Pipeline.dto.PipelineRequestDto;
import com.Pipeline.dto.PipelineResponseDto;
import com.Pipeline.dto.PipelineSummaryDto;

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


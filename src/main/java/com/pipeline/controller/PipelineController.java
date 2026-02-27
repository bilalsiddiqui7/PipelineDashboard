package com.pipeline.controller;


import com.pipeline.dto.pipeline.PipelineRequestDto;
import com.pipeline.dto.pipeline.PipelineResponseDto;
import com.pipeline.dto.pipeline.PipelineSummaryDto;
import com.pipeline.service.PipelineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/pipelines")
@RequiredArgsConstructor
public class PipelineController {

    private final PipelineService pipelineService;

    @GetMapping
    public ResponseEntity<List<PipelineSummaryDto>> getAll() {
        return ResponseEntity.ok(pipelineService.getAllPipelines());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PipelineResponseDto> get(@PathVariable Long id) { // TODO -  remove this api this was just for testing
        return ResponseEntity.ok(pipelineService.getPipeline(id));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PipelineResponseDto> create(@RequestBody PipelineRequestDto dto) {
        PipelineResponseDto created = pipelineService.createPipeline(dto);
        return ResponseEntity.created(URI.create("/pipelines/" + created.getId())).body(created);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PipelineResponseDto> update(@PathVariable Long id, @RequestBody PipelineRequestDto dto) {
        PipelineResponseDto updated = pipelineService.updatePipeline(id, dto);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> toggle(@PathVariable Long id, @RequestParam boolean enabled) {
        pipelineService.togglePipeline(id, enabled);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        pipelineService.deletePipeline(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/copy")
    public ResponseEntity<PipelineResponseDto> copy(@PathVariable Long id) {
        PipelineResponseDto created = pipelineService.copyPipeline(id);
        return ResponseEntity.created(URI.create("/pipelines/" + created.getId())).body(created);
    }
}

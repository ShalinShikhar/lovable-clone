package com.example.lovable_clone.service;

import com.example.lovable_clone.dto.chat.StreamResponse;
import io.micrometer.observation.ObservationFilter;
import reactor.core.publisher.Flux;

public interface AiGenerationService {
    Flux<StreamResponse> streamResponse(String message, Long projectId);
}

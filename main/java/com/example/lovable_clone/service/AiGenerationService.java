package com.example.lovable_clone.service;

import io.micrometer.observation.ObservationFilter;
import reactor.core.publisher.Flux;

public interface AiGenerationService {
    Flux<String> streamResponse(String message, Long projectId);
}

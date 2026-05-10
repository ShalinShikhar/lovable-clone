package com.example.lovable_clone.dto.subscription;

public record PlanResponse(

        Long id,
        String name,
        String stripePriceID,
        Integer maxProjects,
        Integer maxTokenPerDay,
        Integer maxPreviews,
        Boolean unlimitedAi,
        String price
) {
}

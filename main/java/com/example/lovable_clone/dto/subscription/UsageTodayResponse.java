package com.example.lovable_clone.dto.subscription;

public record UsageTodayResponse(
        int tokenUsed,
        int tokenLimit,
        int previewRunning,
        int previewsLimit
) {
}

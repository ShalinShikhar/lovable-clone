package com.example.lovable_clone.dto.project;

import java.time.Instant;

public record FileNode(
        String path,
        Instant modifiedAt,
        String type,
        Long size

) {
}

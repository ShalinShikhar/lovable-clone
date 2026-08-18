package com.example.lovable_clone.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.LocalDate;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Table(name = "usage_log",uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id","date"}) //one log per user per day
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class UsageLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "user_id",nullable = false)
    Long userId;

//    Project project;
//    String action;
//    Integer tokenUsed;
//    Integer durationMs;
//    String metaData;// JSON of {model_used,prompt_used}
//    Instant createdAt;

    @Column(nullable = false)
    LocalDate date;

    Integer tokenUsed;



}

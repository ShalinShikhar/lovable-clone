package com.example.lovable_clone.repository;

import com.example.lovable_clone.entity.ChatSession;
import com.example.lovable_clone.entity.ChatSessionId;
import org.hibernate.boot.models.JpaAnnotations;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatSessionRepository extends JpaRepository<ChatSession, ChatSessionId> {
}

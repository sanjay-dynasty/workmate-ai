package com.workmate.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.workmate.backend.entity.Conversation;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    List<Conversation> findByEmployeeIdOrderByCreatedAtDesc(String employeeId);

    Optional<Conversation> findByIdAndEmployeeId(
            Long id,
            String employeeId);
}
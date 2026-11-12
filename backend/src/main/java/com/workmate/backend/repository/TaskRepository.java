package com.workmate.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.workmate.backend.entity.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByEmployeeIdOrderByIdAsc(String employeeId);

    Optional<Task> findByIdAndEmployeeId(Long id, String employeeId);
}
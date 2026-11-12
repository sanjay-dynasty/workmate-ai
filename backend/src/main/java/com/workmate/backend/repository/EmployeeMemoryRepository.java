package com.workmate.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.workmate.backend.entity.EmployeeMemory;

public interface EmployeeMemoryRepository extends JpaRepository<EmployeeMemory, Long> {

    Optional<EmployeeMemory> findByEmployeeId(String employeeId);

    boolean existsByEmployeeId(String employeeId);

    void deleteByEmployeeId(String employeeId);
}

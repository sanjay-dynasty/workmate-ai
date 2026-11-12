package com.workmate.backend.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.workmate.backend.dto.request.UpdateEmployeeMemoryRequest;
import com.workmate.backend.dto.response.EmployeeMemoryResponse;
import com.workmate.backend.entity.EmployeeMemory;
import com.workmate.backend.repository.EmployeeMemoryRepository;

@Service
@Transactional
public class EmployeeMemoryService {

    private final EmployeeMemoryRepository memoryRepository;
    private final ObjectMapper objectMapper;

    public EmployeeMemoryService(
            EmployeeMemoryRepository memoryRepository,
            ObjectMapper objectMapper) {

        this.memoryRepository = memoryRepository;
        this.objectMapper = objectMapper;
    }

    // Retrieve employee memory.
    public EmployeeMemoryResponse getMemory(String employeeId) {

        EmployeeMemory memory = getOrCreateMemory(employeeId);

        return toResponse(memory);
    }

    // Create or replace employee memory.
    public EmployeeMemoryResponse updateMemory(
            String employeeId,
            UpdateEmployeeMemoryRequest request) {

        EmployeeMemory memory = getOrCreateMemory(employeeId);

        memory.setPreferencesJson(
                writeJson(request.preferences()));

        memory.setActiveGoalsJson(
                writeJson(request.activeGoals()));

        return toResponse(memoryRepository.save(memory));
    }

    // Merge new preferences with existing preferences.
    public EmployeeMemoryResponse updatePreferences(
            String employeeId,
            Map<String, Object> newPreferences) {

        EmployeeMemory memory = getOrCreateMemory(employeeId);

        Map<String, Object> preferences = readPreferences(memory.getPreferencesJson());

        preferences.putAll(newPreferences);

        memory.setPreferencesJson(writeJson(preferences));

        return toResponse(memoryRepository.save(memory));
    }

    // Add an employee goal.
    public EmployeeMemoryResponse addGoal(
            String employeeId,
            String goal) {

        EmployeeMemory memory = getOrCreateMemory(employeeId);

        List<String> goals = readGoals(memory.getActiveGoalsJson());

        if (goals.contains(goal)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Goal already exists.");
        }

        goals.add(goal);

        memory.setActiveGoalsJson(writeJson(goals));

        return toResponse(memoryRepository.save(memory));
    }

    // Remove an employee goal.
    public EmployeeMemoryResponse removeGoal(
            String employeeId,
            String goal) {

        EmployeeMemory memory = getOrCreateMemory(employeeId);

        List<String> goals = readGoals(memory.getActiveGoalsJson());

        if (!goals.remove(goal)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Goal not found.");
        }

        memory.setActiveGoalsJson(writeJson(goals));

        return toResponse(memoryRepository.save(memory));
    }

    private EmployeeMemory getOrCreateMemory(String employeeId) {

        return memoryRepository.findByEmployeeId(employeeId)
                .orElseGet(() -> {

                    EmployeeMemory memory = new EmployeeMemory();

                    memory.setEmployeeId(employeeId);
                    memory.setPreferencesJson("{}");
                    memory.setActiveGoalsJson("[]");

                    return memoryRepository.save(memory);
                });
    }

    private EmployeeMemoryResponse toResponse(EmployeeMemory memory) {

        return new EmployeeMemoryResponse(
                memory.getId(),
                memory.getEmployeeId(),
                readPreferences(memory.getPreferencesJson()),
                readGoals(memory.getActiveGoalsJson()),
                memory.getCreatedAt(),
                memory.getUpdatedAt());
    }

    private Map<String, Object> readPreferences(String json) {

        try {
            if (json == null || json.isBlank()) {
                return new LinkedHashMap<>();
            }

            return objectMapper.readValue(
                    json,
                    new TypeReference<Map<String, Object>>() {
                    });

        } catch (JsonProcessingException exception) {
            throw new IllegalStateException(
                    "Unable to read employee preferences.", exception);
        }
    }

    private List<String> readGoals(String json) {

        try {
            if (json == null || json.isBlank()) {
                return new ArrayList<>();
            }

            return objectMapper.readValue(
                    json,
                    new TypeReference<List<String>>() {
                    });

        } catch (JsonProcessingException exception) {
            throw new IllegalStateException(
                    "Unable to read employee goals.", exception);
        }
    }

    private String writeJson(Object value) {

        try {
            return objectMapper.writeValueAsString(value);

        } catch (JsonProcessingException exception) {
            throw new IllegalStateException(
                    "Unable to serialize employee memory.", exception);
        }
    }
}

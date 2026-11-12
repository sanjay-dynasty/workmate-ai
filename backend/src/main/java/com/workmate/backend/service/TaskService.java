package com.workmate.backend.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.workmate.backend.dto.request.CreateTaskRequest;
import com.workmate.backend.dto.response.TaskResponse;
import com.workmate.backend.entity.Task;
import com.workmate.backend.repository.TaskRepository;

import jakarta.annotation.PostConstruct;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // Initialize demo data only when the task table is empty.
    @PostConstruct
    public void initializeMockTasks() {

        if (taskRepository.count() > 0) {
            return;
        }

        createMockTask(
                "Complete Java preparation",
                "Revise Core Java & Java 17 concepts",
                "EMP001",
                "HIGH",
                LocalDate.now().plusDays(2));

        createMockTask(
                "Complete Spring Boot training",
                "Study Spring Boot and REST APIs",
                "EMP001",
                "MEDIUM",
                LocalDate.now().plusDays(5));

        createMockTask(
                "Review microservices architecture",
                "Understand service communication",
                "EMP001",
                "LOW",
                LocalDate.now().plusDays(7));
    }

    private void createMockTask(
            String taskName,
            String description,
            String employeeId,
            String priority,
            LocalDate dueDate) {

        Task task = new Task();

        task.setTaskName(taskName);
        task.setDescription(description);
        task.setEmployeeId(employeeId);
        task.setPriority(priority);
        task.setStatus("PENDING");
        task.setDueDate(dueDate);

        taskRepository.save(task);
    }

    // Get all tasks belonging to an employee.
    @Transactional(readOnly = true)
    public List<TaskResponse> getAllTasks(String employeeId) {

        return taskRepository.findByEmployeeIdOrderByIdAsc(employeeId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Create a new task.
    public TaskResponse createTask(CreateTaskRequest request) {

        validatePriority(request.priority());

        Task task = new Task();

        task.setTaskName(request.taskName());
        task.setDescription(request.description());
        task.setEmployeeId(request.employeeId());
        task.setPriority(normalizePriority(request.priority()));
        task.setStatus("PENDING");
        task.setDueDate(request.dueDate());

        Task savedTask = taskRepository.save(task);

        return toResponse(savedTask);
    }

    // Get task by ID.
    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long id) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Task not found: " + id));

        return toResponse(task);
    }

    // Update an existing task.
    public TaskResponse updateTask(
            Long id,
            CreateTaskRequest request) {

        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Task not found: " + id));

        validatePriority(request.priority());

        existingTask.setTaskName(request.taskName());
        existingTask.setDescription(request.description());

        if (request.priority() != null) {
            existingTask.setPriority(normalizePriority(request.priority()));
        }

        existingTask.setDueDate(request.dueDate());

        Task updatedTask = taskRepository.save(existingTask);

        return toResponse(updatedTask);
    }

    // Delete a task.
    public void deleteTask(Long id) {

        if (!taskRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Task not found: " + id);
        }

        taskRepository.deleteById(id);
    }

    private TaskResponse toResponse(Task task) {

        return new TaskResponse(
                task.getId(),
                task.getTaskName(),
                task.getDescription(),
                task.getEmployeeId(),
                task.getPriority(),
                task.getStatus(),
                task.getDueDate(),
                task.getCreatedAt());
    }

    private String normalizePriority(String priority) {

        return priority == null ? "MEDIUM" : priority.toUpperCase();
    }

    private void validatePriority(String priority) {

        if (priority == null) {
            return;
        }

        if (!List.of("HIGH", "MEDIUM", "LOW")
                .contains(priority.toUpperCase())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Priority must be HIGH, MEDIUM or LOW");
        }
    }
}
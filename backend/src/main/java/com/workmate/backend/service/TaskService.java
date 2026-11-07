package com.workmate.backend.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.workmate.backend.dto.request.CreateTaskRequest;
import com.workmate.backend.dto.response.TaskResponse;

@Service
public class TaskService {

    private final ConcurrentHashMap<Long, TaskResponse> tasks = new ConcurrentHashMap<>();

    private final AtomicLong idGenerator = new AtomicLong(0);

    public TaskService() {
        createMockTask(
                "Complete Java preparation",
                "Revise Core Java & Java 17 concepts",
                "EMP001",
                "HIGH",
                LocalDate.now().plusDays(2)

        );

        createMockTask(
                "Complete Spring Boot training",
                "Study Spring Boot and REST APIs",
                "EMP001",
                "MEDIUM",
                LocalDate.now().plusDays(5)

        );

        createMockTask(
                "Review microservices architecture",
                "Understand service communication",
                "EMP001",
                "LOW",
                LocalDate.now().plusDays(7)

        );
    }

    private void createMockTask(
            String taskName,
            String description,
            String employeeId,
            String priority,
            LocalDate dueDate) {

        Long id = idGenerator.incrementAndGet();

        tasks.put(id, new TaskResponse(
                id,
                taskName,
                description,
                employeeId,
                priority,
                "PENDING",
                dueDate,
                LocalDateTime.now()));
    }

    public List<TaskResponse> getAllTasks(String employeeId) {

        return tasks.values()
                .stream()
                .filter(task -> task.employeeId().equals(employeeId))
                .sorted(Comparator.comparing(TaskResponse::id))
                .toList();
    }

    public TaskResponse createTask(CreateTaskRequest request) {

        validatePriority(request.priority());

        Long id = idGenerator.incrementAndGet();

        TaskResponse task = new TaskResponse(id,
                request.taskName(),
                request.description(),
                request.employeeId(),
                request.priority() == null ? "MEDIUM" : request.priority(),
                "PENDING",
                request.dueDate(),
                LocalDateTime.now()

        );

        tasks.put(id, task);

        return task;
    }

    public TaskResponse getTaskById(Long id) {

        TaskResponse task = tasks.get(id);

        if (task == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Task not found: " + id);
        }

        return task;
    }

    public TaskResponse updateTask(
            Long id,
            CreateTaskRequest request) {

        TaskResponse existingTask = getTaskById(id);

        validatePriority(request.priority());

        TaskResponse updatedTask = new TaskResponse(
                id,
                request.taskName(),
                request.description(),
                existingTask.employeeId(),
                request.priority() == null
                        ? existingTask.priority()
                        : request.priority(),
                existingTask.status(),
                request.dueDate(),
                existingTask.createdAt());

        tasks.put(id, updatedTask);

        return updatedTask;
    }

    public void deleteTask(Long id) {

        TaskResponse removed = tasks.remove(id);

        if (removed == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Task not found: " + id);
        }
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

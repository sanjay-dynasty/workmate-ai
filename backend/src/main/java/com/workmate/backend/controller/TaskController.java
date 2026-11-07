package com.workmate.backend.controller;

import com.workmate.backend.dto.request.CreateTaskRequest;
import com.workmate.backend.dto.response.TaskResponse;
import com.workmate.backend.service.TaskService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<TaskResponse> getAllTasks(
            @RequestParam(defaultValue = "EMP001") String employeeId) {

        return taskService.getAllTasks(employeeId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(
            @Valid @RequestBody CreateTaskRequest request) {

        return taskService.createTask(request);
    }

    @GetMapping("/{id}")
    public TaskResponse getTaskById(@PathVariable Long id) {

        return taskService.getTaskById(id);
    }

    @PutMapping("/{id}")
    public TaskResponse updateTask(
            @PathVariable Long id,
            @Valid @RequestBody CreateTaskRequest request) {

        return taskService.updateTask(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable Long id) {

        taskService.deleteTask(id);
    }
}

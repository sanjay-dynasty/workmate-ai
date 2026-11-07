
package com.workmate.backend.controller;

import com.workmate.backend.dto.request.CreateTaskRequest;
import com.workmate.backend.dto.response.TaskResponse;
import com.workmate.backend.service.TaskService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    // Temporary development identity.
    // Later, obtain this from Spring Security authentication.
    private static final String DEMO_EMPLOYEE_ID = "EMP001";

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // --------------------------------------------------
    // 1. Get all tasks belonging to the logged-in employee
    // --------------------------------------------------

    @GetMapping
    public List<TaskResponse> getAllTasks() {

        return taskService.getAllTasks(DEMO_EMPLOYEE_ID);
    }

    // --------------------------------------------------
    // 2. Create a task for the logged-in employee
    // --------------------------------------------------

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(
            @Valid @RequestBody CreateTaskRequest request) {

        CreateTaskRequest safeRequest = new CreateTaskRequest(
                request.taskName(),
                request.description(),
                DEMO_EMPLOYEE_ID,
                request.priority(),
                request.dueDate());

        return taskService.createTask(safeRequest);
    }

    // --------------------------------------------------
    // 3. Get a task only if it belongs to the employee
    // --------------------------------------------------

    @GetMapping("/{id}")
    public TaskResponse getTaskById(
            @PathVariable Long id) {

        TaskResponse task = taskService.getTaskById(id);

        verifyOwnership(task);

        return task;
    }

    // --------------------------------------------------
    // 4. Update a task only if it belongs to the employee
    // --------------------------------------------------

    @PutMapping("/{id}")
    public TaskResponse updateTask(
            @PathVariable Long id,
            @Valid @RequestBody CreateTaskRequest request) {

        TaskResponse existingTask = taskService.getTaskById(id);

        verifyOwnership(existingTask);

        CreateTaskRequest safeRequest = new CreateTaskRequest(
                request.taskName(),
                request.description(),
                DEMO_EMPLOYEE_ID,
                request.priority(),
                request.dueDate());

        return taskService.updateTask(id, safeRequest);
    }

    // --------------------------------------------------
    // 5. Delete a task only if it belongs to the employee
    // --------------------------------------------------

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(
            @PathVariable Long id) {

        TaskResponse existingTask = taskService.getTaskById(id);

        verifyOwnership(existingTask);

        taskService.deleteTask(id);
    }

    // --------------------------------------------------
    // Ownership validation
    // --------------------------------------------------

    private void verifyOwnership(TaskResponse task) {

        if (!DEMO_EMPLOYEE_ID.equals(task.employeeId())) {

            // Return 404 rather than revealing another
            // employee's task existence.
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Task not found.");
        }
    }
}

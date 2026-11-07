package com.workmate.backend.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.workmate.backend.dto.request.CreateTaskRequest;
import com.workmate.backend.dto.response.TaskResponse;
import com.workmate.backend.dto.response.ToolExecutionResult;

@Service
public class ToolExecutionService {

    private final TaskService taskService;
    private final EmployeeService employeeService;

    public ToolExecutionService(TaskService taskService, EmployeeService employeeService) {
        this.taskService = taskService;
        this.employeeService = employeeService;
    }

    public ToolExecutionResult execute(
            String toolName,
            Map<String, Object> input) {

        try {
            if (toolName == null || toolName.isBlank()) {
                throw new IllegalArgumentException(
                        "Tool name is required.");
            }

            if (input == null) {
                throw new IllegalArgumentException(
                        "Tool input is required.");
            }

            Object result = switch (toolName) {

                case "get_pending_tasks" ->
                    getPendingTasks(input);

                case "create_task" ->
                    createTask(input);

                case "update_task" ->
                    updateTask(input);

                case "get_leave_balance" ->
                    getLeaveBalance(input);

                case "search_documents" ->
                    searchDocuments(input);

                case "get_employee_profile" ->
                    getEmployeeProfile(input);

                default -> throw new IllegalArgumentException(
                        "Unsupported tool: " + toolName);
            };

            return ToolExecutionResult.success(
                    toolName,
                    result);

        } catch (Exception exception) {

            return ToolExecutionResult.failure(
                    toolName,
                    exception.getMessage() == null
                            ? "Tool execution failed."
                            : exception.getMessage());
        }
    }

    // --------------------------------------------------
    // 1. Get pending tasks
    // --------------------------------------------------

    private List<TaskResponse> getPendingTasks(
            Map<String, Object> input) {

        validateArguments(
                input,
                Set.of("employeeId"),
                Set.of("employeeId"));

        String employeeId = requiredString(input, "employeeId");

        return taskService.getAllTasks(employeeId)
                .stream()
                .filter(task -> "PENDING".equalsIgnoreCase(task.status()))
                .toList();
    }

    // --------------------------------------------------
    // 2. Create task
    // --------------------------------------------------

    private TaskResponse createTask(
            Map<String, Object> input) {

        validateArguments(
                input,
                Set.of(
                        "employeeId",
                        "taskName",
                        "description",
                        "priority",
                        "dueDate"),
                Set.of(
                        "employeeId",
                        "taskName",
                        "priority"));

        String employeeId = requiredString(input, "employeeId");
        String taskName = requiredString(input, "taskName");
        String priority = requiredString(input, "priority");

        String description = optionalString(input, "description");
        LocalDate dueDate = optionalDate(input, "dueDate");

        CreateTaskRequest request = new CreateTaskRequest(
                taskName,
                description,
                employeeId,
                priority,
                dueDate);

        return taskService.createTask(request);
    }

    // --------------------------------------------------
    // 3. Update task
    // --------------------------------------------------

    private TaskResponse updateTask(
            Map<String, Object> input) {

        validateArguments(
                input,
                Set.of(
                        "id",
                        "employeeId",
                        "taskName",
                        "description",
                        "priority",
                        "dueDate"),
                Set.of(
                        "id",
                        "employeeId",
                        "taskName",
                        "priority"));

        long id = requiredLong(input, "id");
        String employeeId = requiredString(input, "employeeId");

        String taskName = requiredString(input, "taskName");
        String priority = requiredString(input, "priority");

        String description = optionalString(input, "description");
        LocalDate dueDate = optionalDate(input, "dueDate");

        // Verify ownership before updating.
        TaskResponse existingTask = taskService.getTaskById(id);

        if (!existingTask.employeeId().equals(employeeId)) {
            throw new IllegalArgumentException(
                    "You are not authorized to update this task.");
        }

        CreateTaskRequest request = new CreateTaskRequest(
                taskName,
                description,
                employeeId,
                priority,
                dueDate);

        return taskService.updateTask(id, request);
    }

    // --------------------------------------------------
    // 4. Get leave balance
    // --------------------------------------------------

    private Object getLeaveBalance(
            Map<String, Object> input) {

        validateArguments(
                input,
                Set.of("employeeId"),
                Set.of("employeeId"));

        String employeeId = requiredString(input, "employeeId");

        return employeeService.getLeaveBalance(employeeId);
    }

    // --------------------------------------------------
    // 5. Search documents
    // --------------------------------------------------

    private Object searchDocuments(
            Map<String, Object> input) {

        validateArguments(
                input,
                Set.of("query"),
                Set.of("query"));

        String query = requiredString(input, "query");

        return employeeService.searchDocuments(query);
    }

    // --------------------------------------------------
    // 6. Get employee profile
    // --------------------------------------------------

    private Object getEmployeeProfile(
            Map<String, Object> input) {

        validateArguments(
                input,
                Set.of("employeeId"),
                Set.of("employeeId"));

        String employeeId = requiredString(input, "employeeId");

        return employeeService.getEmployeeProfile(employeeId);
    }

    // --------------------------------------------------
    // Argument validation helpers
    // --------------------------------------------------

    private void validateArguments(
            Map<String, Object> input,
            Set<String> allowed,
            Set<String> required) {

        for (String key : required) {
            if (!input.containsKey(key) || input.get(key) == null) {
                throw new IllegalArgumentException(
                        "Missing required argument: " + key);
            }
        }

        for (String key : input.keySet()) {
            if (!allowed.contains(key)) {
                throw new IllegalArgumentException(
                        "Unexpected argument: " + key);
            }
        }
    }

    private String requiredString(
            Map<String, Object> input,
            String key) {

        Object value = input.get(key);

        if (!(value instanceof String stringValue)
                || stringValue.isBlank()) {

            throw new IllegalArgumentException(
                    "Invalid or missing string argument: " + key);
        }

        return stringValue.trim();
    }

    private String optionalString(
            Map<String, Object> input,
            String key) {

        Object value = input.get(key);

        if (value == null) {
            return null;
        }

        if (!(value instanceof String stringValue)) {
            throw new IllegalArgumentException(
                    "Invalid string argument: " + key);
        }

        return stringValue.trim();
    }

    private long requiredLong(
            Map<String, Object> input,
            String key) {

        Object value = input.get(key);

        if (!(value instanceof Number number)) {
            throw new IllegalArgumentException(
                    "Invalid numeric argument: " + key);
        }

        long id = number.longValue();

        if (id <= 0 || number.doubleValue() != id) {
            throw new IllegalArgumentException(
                    "Argument must be a positive integer: " + key);
        }

        return id;
    }

    private LocalDate optionalDate(
            Map<String, Object> input,
            String key) {

        String value = optionalString(input, key);

        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return LocalDate.parse(value);
        } catch (Exception exception) {
            throw new IllegalArgumentException(
                    "Invalid date for " + key +
                            ". Expected YYYY-MM-DD.");
        }
    }

}

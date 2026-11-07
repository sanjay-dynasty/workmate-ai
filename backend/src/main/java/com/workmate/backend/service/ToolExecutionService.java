package com.workmate.backend.service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
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

    private static final Set<String> EMPLOYEE_SCOPED_TOOLS = Set.of(
            "get_pending_tasks",
            "create_task",
            "update_task",
            "get_leave_balance",
            "get_employee_profile");

    private static final Set<String> VALID_PRIORITIES = Set.of(
            "HIGH", "MEDIUM", "LOW");

    public ToolExecutionService(
            TaskService taskService,
            EmployeeService employeeService) {

        this.taskService = taskService;
        this.employeeService = employeeService;
    }

    /**
     * Execute without trusted identity.
     *
     * Employee-scoped tools are rejected through this overload.
     * Use the trusted identity overload for employee operations.
     */
    public ToolExecutionResult execute(
            String toolName,
            Map<String, Object> input) {

        return execute(toolName, input, null);
    }

    /**
     * Execute a Claude tool using server-controlled employee identity.
     */
    public ToolExecutionResult execute(
            String toolName,
            Map<String, Object> input,
            String trustedEmployeeId) {

        String safeToolName = toolName == null ? "unknown" : toolName;

        try {
            if (toolName == null || toolName.isBlank()) {
                throw new IllegalArgumentException();
            }

            if (input == null) {
                throw new IllegalArgumentException();
            }

            // Reject unsupported tools before processing arguments.
            if (!isSupportedTool(toolName)) {
                throw new IllegalArgumentException();
            }

            // Never trust employee identity supplied by Claude or clients.
            Map<String, Object> safeInput = new HashMap<>(input);

            if (EMPLOYEE_SCOPED_TOOLS.contains(toolName)) {

                if (trustedEmployeeId == null
                        || trustedEmployeeId.isBlank()) {
                    throw new IllegalArgumentException();
                }

                safeInput.put("employeeId", trustedEmployeeId);
            }

            Object result = switch (toolName) {

                case "get_pending_tasks" ->
                    getPendingTasks(safeInput);

                case "create_task" ->
                    createTask(safeInput);

                case "update_task" ->
                    updateTask(safeInput);

                case "get_leave_balance" ->
                    getLeaveBalance(safeInput);

                case "search_documents" ->
                    searchDocuments(safeInput);

                case "get_employee_profile" ->
                    getEmployeeProfile(safeInput);

                default -> throw new IllegalArgumentException();
            };

            return ToolExecutionResult.success(toolName, result);

        } catch (IllegalArgumentException exception) {

            return ToolExecutionResult.failure(
                    safeToolName,
                    "Invalid tool request or arguments.");

        } catch (Exception exception) {

            // Do not expose internal exception messages to Claude or clients.
            return ToolExecutionResult.failure(
                    safeToolName,
                    "The requested operation could not be completed.");
        }
    }

    private boolean isSupportedTool(String toolName) {

        return Set.of(
                "get_pending_tasks",
                "create_task",
                "update_task",
                "get_leave_balance",
                "search_documents",
                "get_employee_profile").contains(toolName);
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
        String priority = requiredPriority(input, "priority");

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
        String priority = requiredPriority(input, "priority");

        String description = optionalString(input, "description");
        LocalDate dueDate = optionalDate(input, "dueDate");

        // Verify that the task belongs to the trusted employee.
        TaskResponse existingTask = taskService.getTaskById(id);

        if (!existingTask.employeeId().equals(employeeId)) {
            throw new IllegalArgumentException();
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

        if (query.length() > 200) {
            throw new IllegalArgumentException();
        }

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
    // Argument validation
    // --------------------------------------------------

    private void validateArguments(
            Map<String, Object> input,
            Set<String> allowed,
            Set<String> required) {

        for (String key : required) {

            if (!input.containsKey(key) || input.get(key) == null) {
                throw new IllegalArgumentException();
            }
        }

        for (String key : input.keySet()) {

            if (!allowed.contains(key)) {
                throw new IllegalArgumentException();
            }
        }
    }

    private String requiredString(
            Map<String, Object> input,
            String key) {

        Object value = input.get(key);

        if (!(value instanceof String stringValue)
                || stringValue.isBlank()) {

            throw new IllegalArgumentException();
        }

        String result = stringValue.trim();

        if (result.length() > 200) {
            throw new IllegalArgumentException();
        }

        return result;
    }

    private String optionalString(
            Map<String, Object> input,
            String key) {

        Object value = input.get(key);

        if (value == null) {
            return null;
        }

        if (!(value instanceof String stringValue)) {
            throw new IllegalArgumentException();
        }

        String result = stringValue.trim();

        if (result.length() > 500) {
            throw new IllegalArgumentException();
        }

        return result;
    }

    private String requiredPriority(
            Map<String, Object> input,
            String key) {

        String priority = requiredString(input, key).toUpperCase();

        if (!VALID_PRIORITIES.contains(priority)) {
            throw new IllegalArgumentException();
        }

        return priority;
    }

    private long requiredLong(
            Map<String, Object> input,
            String key) {

        Object value = input.get(key);

        if (!(value instanceof Number number)) {
            throw new IllegalArgumentException();
        }

        long id = number.longValue();

        if (id <= 0 || number.doubleValue() != id) {
            throw new IllegalArgumentException();
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

        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException();
        }
    }
}
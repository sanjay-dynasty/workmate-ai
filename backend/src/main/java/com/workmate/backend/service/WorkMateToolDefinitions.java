package com.workmate.backend.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class WorkMateToolDefinitions {

    public List<Map<String, Object>> getTools() {

        return List.of(
                // 1. Retrieve pending tasks
                tool(
                        "get_pending_tasks",
                        "Retrive all pending tasks assigned to an employee",
                        objectSchema(
                                Map.of(
                                        "employeeId", stringProperty(
                                                "Unique employee identifier")),
                                List.of("employeeId"))),
                // 2. Create a task
                tool(
                        "create_task",
                        "Create a new task for an employee.",
                        objectSchema(
                                Map.of(
                                        "employeeId", stringProperty(
                                                "Unique employee identifier."),
                                        "taskName", stringProperty(
                                                "Name of the task."),
                                        "description", stringProperty(
                                                "Detailed task description."),
                                        "priority", enumProperty(
                                                "Task priority.",
                                                List.of("HIGH", "MEDIUM", "LOW")),
                                        "dueDate", dateProperty(
                                                "Task due date in YYYY-MM-DD format.")),
                                List.of(
                                        "employeeId",
                                        "taskName",
                                        "priority"))),

                // 3. Update an existing task
                tool(
                        "update_task",
                        "Update the details of an existing employee task.",
                        objectSchema(
                                Map.of(
                                        "id", integerProperty(
                                                "Unique task identifier."
                                        ),
                                        "employeeId", stringProperty(
                                                "Employee identifier associated with the task."
                                        ),
                                        "taskName", stringProperty(
                                                "Updated task name."
                                        ),
                                        "description", stringProperty(
                                                "Updated task description."
                                        ),
                                        "priority", enumProperty(
                                                "Updated task priority.",
                                                List.of("HIGH", "MEDIUM", "LOW")
                                        ),
                                        "dueDate", dateProperty(
                                                "Updated due date in YYYY-MM-DD format."
                                        )
                                ),
                                List.of("id", "employeeId")
                        )
                ),

                // 4. Retrieve leave balance
                tool(
                        "get_leave_balance",
                        "Retrieve the leave balance of an employee.",
                        objectSchema(
                                Map.of(
                                        "employeeId", stringProperty(
                                                "Unique employee identifier.")),
                                List.of("employeeId"))),

                // 5. Search employee documents
                tool(
                        "search_documents",
                        "Search company documents using a keyword or phrase.",
                        objectSchema(
                                Map.of(
                                        "query", stringProperty(
                                                "Keyword or phrase to search for.")),
                                List.of("query"))),

                // 6. Retrieve employee profile
                tool(
                        "get_employee_profile",
                        "Retrieve an employee's profile information.",
                        objectSchema(
                                Map.of(
                                        "employeeId", stringProperty(
                                                "Unique employee identifier.")),
                                List.of("employeeId")))

        );
    }

    private Map<String, Object> tool(
            String name,
            String description,
            Map<String, Object> inputSchema) {

        return Map.of(
                "name", name,
                "description", description,
                "input_schema", inputSchema);
    }

    private Map<String, Object> objectSchema(
            Map<String, Object> properties,
            List<String> required) {

        return Map.of(
                "type", "object",
                "properties", properties,
                "required", required,
                "additionalProperties", false);
    }

    private Map<String, Object> stringProperty(String description) {

        return Map.of(
                "type", "string",
                "description", description);
    }

    private Map<String, Object> dateProperty(String description) {

        return Map.of(
                "type", "string",
                "format", "date",
                "description", description);
    }

    private Map<String, Object> integerProperty(String description) {

        return Map.of(
                "type", "integer",
                "description", description);
    }

    private Map<String, Object> enumProperty(
            String description,
            List<String> values) {

        return Map.of(
                "type", "string",
                "enum", values,
                "description", description);
    }

}

package com.workmate.backend.service;

import org.springframework.stereotype.Service;

import com.workmate.backend.dto.PromptStrategy;

@Service
public class PromptTemplateService {

    public String getPrompt(PromptStrategy strategy) {

        return switch (strategy) {
            case ZERO_SHOT -> zeroShotPrompt();
            case ONE_SHOT -> oneShotPrompt();
            case FEW_SHOT -> fewShotPrompt();
            case XML -> xmlPrompt();
        };
    }

    private String zeroShotPrompt() {
        return """
                You are an employee request classifier.

                Classify the user's request into exactly one of these categories:
                TASK_LIST
                CREATE_TASK
                LEAVE_BALANCE
                PROJECT_SUMMARY
                TASK_PLANNING
                OTHER

                Return only the category name.
                """;
    }

    private String oneShotPrompt() {
        return """
                You are an employee request classifier.

                Example:
                Request: Create a task for Java preparation.
                Intent: CREATE_TASK

                Classify the next employee request into:

                TASK_LIST
                CREATE_TASK
                LEAVE_BALANCE
                PROJECT_SUMMARY
                TASK_PLANNING
                OTHER

                Return only the category name.
                """;
    }

    private String fewShotPrompt() {

        return """
                You are an employee request classifier.

                Examples:

                Request: Show my pending tasks.
                Intent: TASK_LIST

                Request: Create a task for Java preparation.
                Intent: CREATE_TASK

                Request: How many leaves do I have?
                Intent: LEAVE_BALANCE

                Request: Summarize my project.
                Intent: PROJECT_SUMMARY

                Request: Help me prioritize today's work.
                Intent: TASK_PLANNING

                Classify the next request into the correct category.

                Return only the category name.
                """;
    }

    private String xmlPrompt() {

        return """
                <role>
                    You are WorkMate, an enterprise employee assistant.
                </role>

                <responsibilities>
                    Help employees manage tasks, leaves and projects.
                </responsibilities>

                <instructions>
                    Classify the employee request accurately.
                    Do not invent employee information.
                    Choose exactly one supported intent.
                </instructions>

                <supported_intents>
                    <intent>TASK_LIST</intent>
                    <intent>CREATE_TASK</intent>
                    <intent>LEAVE_BALANCE</intent>
                    <intent>PROJECT_SUMMARY</intent>
                    <intent>TASK_PLANNING</intent>
                    <intent>OTHER</intent>
                </supported_intents>

                <output_requirements>
                    Return only the intent name.
                </output_requirements>
                """;
    }
}

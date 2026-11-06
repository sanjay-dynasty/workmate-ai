# WorkMate AI – Backend Documentation

**Project:** WorkMate AI  
**Component:** Backend  
**Technology:** Java, Spring Boot, Anthropic Claude API  
**Documentation Scope:** Phase 0 and Phase 1

---

# 1. Project Overview

## 1.1 Introduction

WorkMate AI is an AI-powered employee productivity assistant designed to understand employee requests and provide intelligent assistance for workplace activities.

The application uses Anthropic Claude as its Large Language Model (LLM), with Java and Spring Boot providing the backend infrastructure.

The backend is responsible for receiving user requests, communicating with Claude, processing AI-generated responses, identifying user intent, extracting relevant entities and validating structured output.

## 1.2 Objectives

The primary objectives of the current backend implementation are:

- Establish a scalable Spring Boot backend.
- Integrate Anthropic Claude through its Messages API.
- Implement different prompt engineering strategies.
- Understand and classify natural-language employee requests.
- Extract structured information from user messages.
- Generate predictable JSON responses.
- Validate AI-generated responses using JSON Schema.
- Evaluate classification accuracy using predefined test cases.

---

# 2. Technology Stack

| Technology | Purpose |
|---|---|
| Java | Backend programming language |
| Spring Boot 4.1.2 | Backend application framework |
| Maven | Build and dependency management |
| Anthropic Claude | Large Language Model integration |
| Claude Messages API | AI communication interface |
| Spring RestClient | HTTP communication |
| Jackson | JSON parsing and object mapping |
| JSON Schema | Structured response definition |
| NetworkNT JSON Schema Validator | JSON validation |
| JUnit | Automated testing |
| Postman | API testing |

The application was observed running with Java 21 and configured with Java 17 compilation compatibility.

---

# 3. Backend Architecture

The backend follows a layered architecture that separates API handling, business logic, AI integration, prompt management and response validation.

## 3.1 Architectural Components

**Controller Layer**

Receives HTTP requests and exposes backend REST endpoints.

**Service Layer**

Contains application logic for Claude communication, prompt construction, intent classification and evaluation.

**AI Integration Layer**

Communicates with Anthropic Claude through the Messages API.

**Prompt Management Layer**

Maintains system instructions and different prompt engineering strategies.

**Validation Layer**

Validates generated JSON against the configured schema and performs additional Java-level validation.

**DTO Layer**

Defines the request and response structures exchanged between application components.

**Resources Layer**

Stores application configuration, JSON Schema definitions and evaluation datasets.

## 3.2 Request Processing Flow

The current structured classification workflow follows this sequence:

User Request  
↓  
Spring Boot Controller  
↓  
Claude Service  
↓  
System Prompt and Runtime Context  
↓  
Claude Messages API  
↓  
Structured JSON Response  
↓  
JSON Schema Validation  
↓  
Java DTO Deserialization  
↓  
Business Validation  
↓  
REST API Response

---

# 4. Backend Project Structure

The backend source code is organized into the following packages and resources.

## 4.1 Configuration – config/

Responsible for configuring application components and external integrations.

**ClaudeClientConfig**
- Configures the Claude HTTP client.
- Maintains Anthropic API headers.
- Configures API authentication and base URL.

**JacksonConfig**
- Provides Jackson ObjectMapper configuration.
- Supports JSON processing and Java date-time types.

## 4.2 Controllers – controller/

Responsible for exposing backend REST APIs.

**ClaudeController**
- Handles basic Claude conversation requests.

**PromptExperimentController**
- Exposes prompt experimentation functionality.

**IntentClassificationController**
- Handles employee request classification.

**IntentEvaluationController**
- Exposes the predefined classification evaluation process.

## 4.3 DTOs – dto/

Contains application request and response models.

Includes:

- Intent: Supported intent categories.
- Priority: Supported task priority values.
- PromptStrategy: Available prompt experimentation strategies.
- PromptExperimentRequest: Prompt experiment request model.
- ClaudeChatResponse: Basic Claude conversation response.
- IntentClassificationResponse: Structured classification response.
- TaskEntities: Extracted task information.
- IntentTestCase: Evaluation dataset entry.
- IntentEvaluationResult: Individual evaluation result.

## 4.4 Services – service/

Contains the primary application and AI integration logic.

**ClaudeService**

Responsible for:
- Sending requests to Claude.
- Processing Claude API responses.
- Basic conversational interaction.
- Prompt experimentation.
- Structured response generation.
- Intent classification.
- JSON deserialization.
- API rate-limit handling.

**WorkMateSystemPrompt**

Maintains WorkMate's identity, responsibilities, supported intents, classification instructions, entity extraction rules and response requirements.

**PromptTemplateService**

Provides different prompt engineering strategies for experimentation.

**SchemaValidationService**

Validates Claude-generated JSON against the configured JSON Schema.

**IntentResponseValidator**

Performs application-level validation of the structured classification response.

**IntentEvaluationService**

Loads evaluation test cases, executes classification requests and compares predicted intents with expected intents.

## 4.5 Resources – resources/

Contains configuration and external data files.

| Resource | Responsibility |
|---|---|
| application.properties | Application and Claude API configuration |
| schemas/intent-classification-schema.json | Structured response definition |
| evaluation/intent-test-cases.json | Intent classification evaluation dataset |

---

# 5. Phase 0 – Project Foundation and Claude API Integration

## 5.1 Objective

The objective of Phase 0 was to establish the backend foundation and integrate Anthropic Claude with the Spring Boot application.

The primary focus was to create a working backend capable of communicating with Claude and processing its responses.

## 5.2 Backend Initialization

The Spring Boot backend project was initialized with:

- Java.
- Spring Boot.
- Maven.
- Spring Web.
- Spring Validation.
- Spring Boot Actuator.

The application was configured to run locally on port 8080.

## 5.3 Application Configuration

Configured the application properties for:

- Application name.
- Server port.
- Anthropic API base URL.
- Claude model identifier.
- API key configuration through environment variables.
- Application logging.
- Actuator health endpoint exposure.

API credentials were externalized rather than stored directly in source code.

## 5.4 Claude API Integration

Integrated the Anthropic Messages API.

The integration established:

- HTTP client configuration.
- Anthropic authentication headers.
- API request construction.
- Message submission.
- HTTP response handling.
- Generated text extraction.

The backend communicates with Claude through Spring RestClient.

## 5.5 Basic Conversation Functionality

Implemented the initial Claude service to support basic conversational interaction.

The workflow accepts a user message, constructs a Claude API request, submits the request and processes the generated response.

The response is returned through the backend's REST interface.

## 5.6 Application Testing

The following development activities were performed:

- Started the Spring Boot application locally.
- Verified application startup.
- Checked the backend health endpoint.
- Tested communication with Claude.
- Verified response processing through REST requests.

## 5.7 Phase 0 Deliverables

At the completion of Phase 0, the backend had an initialized Spring Boot application, configured Claude API connectivity, externalized credentials, a reusable HTTP client, basic conversation functionality and application health monitoring.

**Outcome:** The backend foundation and Claude integration were established successfully.

---

# 6. Phase 1 – Prompt Engineering and Structured Output

## 6.1 Objective

The objective of Phase 1 was to transform WorkMate from a basic conversational application into an intent-aware AI assistant.

The backend needed to understand employee requests, identify their intent, extract relevant information and return predictable structured responses.

The implementation focused on prompt engineering, intent classification, entity extraction, structured JSON generation, schema validation and prompt evaluation.

---

# 7. Prompt Engineering

Different prompt engineering techniques were implemented and experimented with using Claude.

## 7.1 Zero-Shot Prompting

Implemented prompting without providing classification examples.

The model receives instructions describing the classification task and supported intent categories.

Purpose:
- Establish baseline classification behaviour.
- Understand instruction-based classification.
- Evaluate Claude's ability to classify requests without examples.

## 7.2 One-Shot Prompting

Introduced one example containing an input request and its expected classification.

Purpose:
- Demonstrate the expected classification pattern.
- Improve consistency in the response format.

## 7.3 Few-Shot Prompting

Provided multiple classification examples covering different employee requests.

Purpose:
- Establish classification patterns.
- Demonstrate distinctions between supported intents.
- Improve consistency when handling different natural-language expressions.

## 7.4 Role-Based Prompting

Defined Claude's role as WorkMate, an intelligent employee productivity assistant.

The system instructions describe:
- Assistant identity.
- Responsibilities.
- Supported request categories.
- Classification expectations.
- Entity extraction requirements.
- Output requirements.

## 7.5 XML-Structured Prompting

Organized system instructions using XML-style sections.

The prompt separates identity, responsibilities, supported intents, classification rules, entity extraction, security instructions and output requirements.

This makes the system prompt easier to organize and maintain.

## 7.6 Prompt Chaining

Established a structured sequence for processing employee requests:

1. Understand the incoming request.
2. Identify the user's intent.
3. Extract relevant entities.
4. Generate structured output.
5. Validate the generated response.

## 7.7 Instruction Hierarchy and Context Separation

The prompt design separates system-level instructions, user input and runtime context.

Runtime context currently includes:
- Current date.
- Asia/Kolkata timezone.
- Relative due-date interpretation.
- Handling of unknown or non-applicable entity values.

This context helps Claude interpret requests involving dates such as tomorrow and return normalized date values.

---

# 8. Intent Classification

## 8.1 Supported Intent Categories

Six intent categories were established for the current implementation.

| Intent | Description | Example |
|---|---|---|
| TASK_LIST | Retrieve employee tasks | Show my pending tasks |
| CREATE_TASK | Create a new task | Create a task for Java preparation |
| LEAVE_BALANCE | Retrieve leave information | How many leaves do I have? |
| PROJECT_SUMMARY | Summarize project information | Summarize my current project |
| TASK_PLANNING | Plan and prioritize work | Which task should I complete first? |
| OTHER | Requests outside supported categories | Explain Java and Python differences |

## 8.2 Classification Responsibilities

The classification process identifies:

- What the employee is requesting.
- Which supported intent matches the request.
- Whether the request requires a tool.
- Which entities are relevant.
- What response should be returned.

## 8.3 Entity Extraction

The structured response currently supports four entities.

| Entity | Description |
|---|---|
| taskName | Task name or description |
| priority | HIGH, MEDIUM or LOW |
| dueDate | Task due date |
| projectName | Associated project name |

Unknown or non-applicable entity values are represented as null.

---

# 9. Structured Output Generation

## 9.1 Objective

Conversational LLM responses are not always predictable enough for backend business processing.

Structured output was introduced to ensure Claude returns machine-readable JSON that follows a predefined response structure.

## 9.2 Implementation

The Anthropic structured-output capability was integrated into the Messages API request.

The implementation includes:

- A predefined JSON Schema.
- Required response properties.
- Supported intent values.
- Confidence field.
- Tool requirement indicator.
- Response text.
- Nested entity structure.
- Nullable entity properties.
- Restrictions on additional properties.

The schema is maintained separately under the backend resources directory.

## 9.3 Structured Response Model

The classification response contains five primary properties.

| Property | Description |
|---|---|
| intent | Identified employee request category |
| confidence | Model-provided confidence value |
| requiresTool | Indicates whether a tool is required |
| response | User-facing response |
| entities | Extracted structured information |

## 9.4 Java DTO Mapping

Java records were introduced to represent the structured response.

Claude's generated JSON is deserialized into strongly typed Java objects.

This enables the application to process classification results using Java types rather than manually handling unstructured text.

---

# 10. JSON Schema and Business Validation

Two validation levels were implemented.

## 10.1 JSON Schema Validation

The generated JSON response is validated against the predefined intent classification schema.

Validation covers:

- Required properties.
- Property data types.
- Supported enum values.
- Nested entity structure.
- Additional property restrictions.
- Nullable entity values.

The NetworkNT JSON Schema Validator is used for schema validation.

## 10.2 Java Business Validation

After schema validation and DTO deserialization, additional Java-level checks are performed.

These include:

- Intent must not be null.
- Confidence must be between 0 and 1.
- Response must not be blank.
- Entities must not be null.

This ensures that structured output is validated before being returned by the application.

---

# 11. Claude Service Enhancements

The initial Claude service was extended to support Phase 1 functionality.

Its responsibilities now include:

**Basic Conversation**

Handles normal user messages and returns Claude-generated text.

**Prompt Experimentation**

Accepts different prompt strategies and sends the corresponding system prompt to Claude.

**Structured Response Generation**

Loads the JSON Schema, constructs the structured-output request, adds runtime context and processes Claude's response.

**Intent Classification**

Coordinates structured response generation, schema validation, JSON deserialization and business validation.

**HTTP Response Processing**

Receives the Claude HTTP response as a String and parses it using the configured Jackson ObjectMapper. This avoids the Jackson 2 and Jackson 3 HTTP message converter compatibility issue encountered during development.

**Rate-Limit Handling**

Includes retry handling for HTTP 429 responses, supports the Retry-After response header and uses increasing retry intervals.

---

# 12. Prompt Experimentation

A prompt experimentation capability was introduced to compare different prompting approaches.

The supported strategies are:

| Strategy | Description |
|---|---|
| ZERO_SHOT | Classification without examples |
| ONE_SHOT | Classification using one example |
| FEW_SHOT | Classification using multiple examples |
| XML | Classification using XML-structured instructions |

The experimentation workflow accepts a prompt strategy and user message, constructs the corresponding prompt and submits it to Claude.

This provides a way to compare prompting techniques and understand their impact on model behaviour.

---

# 13. Prompt Evaluation Framework

## 13.1 Objective

The evaluation framework measures how accurately Claude classifies predefined employee requests.

A dataset of 20 test cases was created to evaluate all six supported intent categories.

## 13.2 Evaluation Dataset

The dataset is maintained at:

`src/main/resources/evaluation/intent-test-cases.json`

Each test case contains:
- Test case identifier.
- User input.
- Expected intent.

## 13.3 Evaluation Workflow

The evaluation service performs the following operations:

1. Loads the evaluation dataset.
2. Reads individual test cases.
3. Sends each input to Claude.
4. Retrieves the predicted intent.
5. Compares the predicted intent with the expected intent.
6. Determines whether the classification is correct.
7. Returns individual evaluation results.

## 13.4 Evaluation API

**Method:** POST

**Endpoint:** `/api/claude/evaluation/run`

The endpoint executes the predefined evaluation dataset and returns the results.

No request body is required.

## 13.5 Evaluation Execution

To avoid exceeding Anthropic workspace token limits, evaluation requests are executed sequentially with a configurable delay between requests.

HTTP 429 responses are handled through retry logic.

## 13.6 Evaluation Results

The initial evaluation was completed successfully.

| Intent | Test Cases | Correct Predictions |
|---|---:|---:|
| TASK_LIST | 4 | 4 |
| CREATE_TASK | 4 | 4 |
| LEAVE_BALANCE | 3 | 3 |
| PROJECT_SUMMARY | 3 | 3 |
| TASK_PLANNING | 3 | 3 |
| OTHER | 3 | 3 |
| **Total** | **20** | **20** |

**Observed classification accuracy: 100%**

The evaluation achieved 20 correct predictions out of 20 test cases.

This result represents performance on the initial evaluation dataset and does not establish production-level accuracy.

---

# 14. API Testing

The backend capabilities were tested using Postman.

The testing covered:

- Basic Claude conversation.
- Prompt experimentation.
- Employee request classification.
- Structured JSON response generation.
- Evaluation execution.
- Application health.

The backend is accessible locally through port 8080.

The classification endpoint accepts a natural-language message and returns the validated intent classification response.

---

# 15. Technical Challenges and Resolutions

Several integration issues were identified and resolved during implementation
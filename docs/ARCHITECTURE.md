# WorkMate AI Architecture

## Phase 0

WorkMate AI is an AI-powered employee assistant.

### Backend
- Java 17
- Spring Boot 3
- Maven

### AI Integration
- Anthropic Claude API
- Messages API
- Spring RestClient

### Request Flow

User
  |
  v
Spring Boot Controller
  |
  v
Claude Service
  |
  v
Anthropic Messages API
  |
  v
Claude Model
  |
  v
Generated Response
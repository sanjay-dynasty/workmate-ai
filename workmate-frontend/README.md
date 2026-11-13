# WorkMate AI Frontend (Phase 3.6)

A deliberately simple Next.js frontend for visualizing the Phase 1–3 Spring Boot/Claude capabilities. It is not intended to replace or duplicate the backend.

## Requirements
- Node.js 20.9 or newer
- npm
- WorkMate Spring Boot backend running at `http://localhost:8080`

## Run
Open a terminal in this folder:

```powershell
npm install
npm run dev
```

Open http://localhost:3000.

Build check:

```powershell
npm run build
```

## Backend proxy
`next.config.ts` proxies `/api/*` to `http://localhost:8080/api/*`. Keep Spring Boot running on port 8080. If your backend uses another port, update the destination in `next.config.ts` and restart Next.js.

## Screens
- `/` — overview: task counts, active goals and planning preference.
- `/chat` — agentic chat, conversation list and tool-call metadata.
- `/tasks` — list/create/delete tasks.
- `/memory` — edit employee preferences and add/remove goals.

## Expected backend routes
This frontend is wired to the routes used by the WorkMate backend during development:

- `GET /api/tasks`, `POST /api/tasks`, `PUT /api/tasks/{id}`, `DELETE /api/tasks/{id}`
- `POST /api/claude/agentic-chat` with `{ "message": "...", "conversationId": 123 }` (conversationId omitted for a new chat)
- `GET /api/conversations?employeeId=EMP001`
- `GET /api/conversations/{id}/messages`
- `GET /api/memory`, `PUT /api/memory`, `PATCH /api/memory/preferences`
- `POST /api/memory/goals`, `DELETE /api/memory/goals?goal=...`

If a route returns 404 or a validation error, compare it with the controller mapping/DTO in your local backend. The memory and conversation routes follow the API shapes tested in your development session; small differences can be adjusted in `lib/api.ts`.

## Important development notes
- The employee identity is hard-coded as `EMP001` for the demo, matching the current backend development identity. This is not production authentication.
- Do not put the Claude API key in frontend environment variables or browser code. Claude calls and tool execution must remain in Spring Boot.
- This frontend displays the backend's capabilities; it does not implement prompt engineering, agent loops, context selection, summarization, employee memory or caching itself.
- The frontend does not implement login or authorization. Add those only when you introduce real Spring Security authentication.

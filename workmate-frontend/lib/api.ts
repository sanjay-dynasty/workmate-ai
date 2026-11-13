import type { ChatMessage, Conversation, Memory, Task } from "./types";

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(path, {
    ...init,
    headers: { "Content-Type": "application/json", ...(init?.headers || {}) },
    cache: "no-store",
  });
  if (!response.ok) {
    const body = await response.text();
    throw new Error(body || `Request failed (${response.status})`);
  }
  if (response.status === 204) return undefined as T;
  return response.json() as Promise<T>;
}

export const api = {
  tasks: () => request<Task[]>("/api/tasks"),
  createTask: (task: Partial<Task>) => request<Task>("/api/tasks", { method: "POST", body: JSON.stringify({ ...task, employeeId: "EMP001" }) }),
  updateTask: (id: number, task: Partial<Task>) => request<Task>(`/api/tasks/${id}`, { method: "PUT", body: JSON.stringify({ ...task, employeeId: "EMP001" }) }),
  deleteTask: (id: number) => request<void>(`/api/tasks/${id}`, { method: "DELETE" }),
  memory: () => request<Memory>("/api/memory"),
  saveMemory: (memory: Pick<Memory, "preferences" | "activeGoals">) => request<Memory>("/api/memory", { method: "PUT", body: JSON.stringify(memory) }),
  patchPreferences: (preferences: Record<string, unknown>) => request<Memory>("/api/memory/preferences", { method: "PATCH", body: JSON.stringify({ preferences }) }),
  addGoal: (goal: string) => request<Memory>("/api/memory/goals", { method: "POST", body: JSON.stringify({ goal }) }),
  removeGoal: (goal: string) => request<Memory>(`/api/memory/goals?goal=${encodeURIComponent(goal)}`, { method: "DELETE" }),
  conversations: () => request<Conversation[]>("/api/conversations?employeeId=EMP001"),
  messages: (id: number) => request<ChatMessage[]>(`/api/conversations/${id}/messages`),
  sendChat: (message: string, conversationId?: number) => request<{ conversationId: number; answer: string; model: string; stopReason: string; toolCalls: number; iterations: number }>("/api/claude/agentic-chat", { method: "POST", body: JSON.stringify({ message, ...(conversationId ? { conversationId } : {}) }) }),
};

export function messageText(content: unknown): string {
  if (typeof content === "string") {
    try { return messageText(JSON.parse(content)); } catch { return content; }
  }
  if (Array.isArray(content)) {
    return content.map((block: any) => {
      if (block?.type === "text") return block.text || "";
      if (block?.type === "tool_use") return `Used tool: ${block.name || "tool"}`;
      if (block?.type === "tool_result") return typeof block.content === "string" ? block.content : "Tool completed";
      return "";
    }).filter(Boolean).join("\n");
  }
  if (content && typeof content === "object" && "text" in content) return String((content as any).text);
  return content == null ? "" : JSON.stringify(content);
}

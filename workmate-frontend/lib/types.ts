export type Task = {
  id: number; taskName: string; description?: string; employeeId: string;
  priority: "HIGH" | "MEDIUM" | "LOW"; status: string; dueDate?: string | null; createdAt?: string;
};
export type Memory = {
  id?: number; employeeId: string; preferences: Record<string, unknown>;
  activeGoals: string[]; createdAt?: string; updatedAt?: string;
};
export type Conversation = { id: number; employeeId?: string; createdAt?: string; summary?: string };
export type ChatMessage = { id?: number; role: string; content: unknown; createdAt?: string };

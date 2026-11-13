"use client";
import { useEffect, useState } from "react";
import Link from "next/link";
import { ArrowRight, Bot, CheckCircle2, Clock3, ListTodo } from "lucide-react";
import { api } from "@/lib/api";
import type { Memory, Task } from "@/lib/types";
export default function Dashboard() {
  const [tasks,setTasks]=useState<Task[]>([]); const [memory,setMemory]=useState<Memory|null>(null); const [error,setError]=useState(""); const [loading,setLoading]=useState(true);
  useEffect(()=>{Promise.all([api.tasks(),api.memory()]).then(([t,m])=>{setTasks(t);setMemory(m)}).catch(e=>setError(e.message)).finally(()=>setLoading(false))},[]);
  const pending=tasks.filter(t=>t.status?.toUpperCase()!=="COMPLETED"&&t.status?.toUpperCase()!=="DONE");
  const high=pending.filter(t=>t.priority==="HIGH").length;
  return <><div className="page-heading"><div><div className="eyebrow">EMPLOYEE WORKSPACE</div><h1>Good day, Rahul</h1><p className="subtext">A simple overview of your work, goals and AI assistant.</p></div><Link className="btn" href="/chat"><Bot size={16}/> Ask WorkMate</Link></div>
    {error&&<div className="error">Could not load backend data: {error}. Check that Spring Boot is running on port 8080.</div>}
    <div className="grid grid-4" style={{marginBottom:18}}>
      <div className="card"><div className="stat-label">Total tasks</div><div className="stat-value">{loading?"—":tasks.length}</div><div className="stat-note">Stored in H2 database</div></div>
      <div className="card"><div className="stat-label">Pending tasks</div><div className="stat-value">{loading?"—":pending.length}</div><div className="stat-note">Your current workload</div></div>
      <div className="card"><div className="stat-label">High priority</div><div className="stat-value">{loading?"—":high}</div><div className="stat-note">Tasks needing attention</div></div>
      <div className="card"><div className="stat-label">Active goals</div><div className="stat-value">{loading?"—":memory?.activeGoals?.length??0}</div><div className="stat-note">Persistent employee memory</div></div>
    </div>
    <div className="grid grid-2">
      <section className="card"><div className="card-head"><h2 className="section-title">Upcoming tasks</h2><Link className="muted" href="/tasks">View all <ArrowRight size={13} style={{display:"inline",verticalAlign:"middle"}}/></Link></div>
        {loading?<div className="empty">Loading tasks…</div>:pending.slice(0,5).length===0?<div className="empty">No pending tasks found.</div>:<div className="table-wrap"><table><thead><tr><th>Task</th><th>Priority</th><th>Due date</th></tr></thead><tbody>{pending.slice(0,5).map(t=><tr key={t.id}><td><strong>{t.taskName}</strong><div className="muted" style={{marginTop:4}}>{t.description}</div></td><td><span className={`pill ${t.priority.toLowerCase()}`}>{t.priority}</span></td><td>{t.dueDate||"—"}</td></tr>)}</tbody></table></div>}
      </section>
      <section className="card"><div className="card-head"><h2 className="section-title">Employee memory</h2><Link className="muted" href="/memory">Manage <ArrowRight size={13} style={{display:"inline",verticalAlign:"middle"}}/></Link></div>
        <div className="stat-label">Planning time</div><div style={{fontSize:22,fontWeight:750,margin:"6px 0 16px"}}>{String(memory?.preferences?.dailyPlanningTime||"Not set")}</div>
        <div className="stat-label" style={{marginBottom:9}}>Active goals</div>{memory?.activeGoals?.length?memory.activeGoals.slice(0,4).map(g=><div key={g} className="goal-row"><span>{g}</span><CheckCircle2 size={15} color="#2b9a70"/></div>):<div className="empty">No goals saved yet.</div>}
      </section>
    </div>
    <div className="card" style={{marginTop:18}}><div className="card-head"><h2 className="section-title">Learning workspace</h2><span className="pill">CCAR-F project</span></div><div className="grid grid-2"><div><div style={{fontWeight:700,marginBottom:6}}>Phase 1–3 backend capabilities</div><p className="subtext">Prompt engineering, Claude tool calling, agentic execution, persistent conversations, context summarization, employee memory and prompt caching.</p></div><div className="toolbar"><Link href="/chat" className="btn"><Bot size={15}/> Try agentic chat</Link><Link href="/tasks" className="btn btn-secondary"><ListTodo size={15}/> Manage tasks</Link></div></div></div>
  </>;
}

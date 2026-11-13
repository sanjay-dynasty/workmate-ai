"use client";
import Link from "next/link";
import { usePathname } from "next/navigation";
import { Bot, CheckSquare, LayoutDashboard, Brain, Sparkles } from "lucide-react";
const links = [
  { href: "/", label: "Overview", icon: LayoutDashboard },
  { href: "/chat", label: "AI Assistant", icon: Bot },
  { href: "/tasks", label: "Tasks", icon: CheckSquare },
  { href: "/memory", label: "Employee Memory", icon: Brain },
];
export function Sidebar() {
  const path = usePathname();
  return <aside className="sidebar">
    <Link href="/" className="brand"><span className="brand-icon"><Sparkles size={19}/></span><span>WorkMate<span className="brand-sub">AI WORKSPACE</span></span></Link>
    <div className="nav-label">WORKSPACE</div>
    <nav>{links.map(({href,label,icon:Icon}) => <Link key={href} href={href} className={`nav-link ${path === href ? "active" : ""}`}><Icon size={18}/>{label}</Link>)}</nav>
    <div className="sidebar-bottom"><div className="avatar">RS</div><div><strong>Rahul Sharma</strong><small>EMP001 · Demo employee</small></div></div>
  </aside>;
}

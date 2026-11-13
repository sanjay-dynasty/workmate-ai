import { Sidebar } from "./sidebar";
export function Shell({ children }: { children: React.ReactNode }) {
  return <div className="app-shell"><Sidebar/><main className="main"><header className="topbar"><div><span className="status-dot"/> Backend workspace</div><span className="topbar-tag">DEVELOPMENT</span></header><div className="page-content">{children}</div></main></div>;
}

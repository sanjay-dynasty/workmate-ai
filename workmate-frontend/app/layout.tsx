import type { Metadata } from "next";
import "./globals.css";
import { Shell } from "@/components/shell";
export const metadata: Metadata = { title: "WorkMate AI", description: "Employee assistant workspace" };
export default function RootLayout({ children }: Readonly<{ children: React.ReactNode }>) {
  return <html lang="en"><body><Shell>{children}</Shell></body></html>;
}

import React from "react";
import { NavLink, Outlet } from "react-router-dom";
import "./layout.css";

export default function PublicLayout() {
  return (
    <div className="app-shell">
      <header className="topbar">
        <div className="brand">
          <div className="brand-icon">🍃</div>
          <div className="brand-text">
            <div className="brand-title">Green Steps</div>
            <div className="brand-subtitle">
              Track your carbon footprint
            </div>
          </div>
        </div>

        <div className="topbar-actions">
          <NavLink to="/login" className="nav-link">
            Login
          </NavLink>
          <NavLink to="/register" className="nav-link">
            Register
          </NavLink>
        </div>
      </header>

      <main className="main-content">
        <Outlet />
      </main>
    </div>
  );
}
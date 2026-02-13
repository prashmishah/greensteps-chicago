import React from "react";
import { NavLink, Outlet } from "react-router-dom";

export default function PublicLayout() {
  return (
    <div className="public-shell">
      <header className="public-header">
        <div className="public-brand">
          <span className="brand-icon">🍃</span>
          <div>
            <div className="brand-title">Green Steps</div>
            <div className="brand-subtitle">Track your carbon footprint</div>
          </div>
        </div>

        <div className="auth-links">
          <NavLink to="/login" className="link">
            Login
          </NavLink>
          <NavLink to="/register" className="link">
            Register
          </NavLink>
        </div>
      </header>

      <main className="main">
        <Outlet />
      </main>
    </div>
  );
}
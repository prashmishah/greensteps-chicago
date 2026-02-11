import React from "react";
import { NavLink, Outlet, useLocation, useNavigate } from "react-router-dom";

export default function AppLayout() {
  const navigate = useNavigate();
  const location = useLocation();

  const isActive = (path) => location.pathname.startsWith(path);

  return (
    <div className="app-shell">
      <header className="topbar">
        <div className="brand" onClick={() => navigate("/")} role="button" tabIndex={0}>
          <div className="brand-icon">🍃</div>
          <div className="brand-text">
            <div className="brand-title">Green Steps</div>
            <div className="brand-subtitle">Track your carbon footprint</div>
          </div>
        </div>

        <nav className="topbar-actions">
          <NavLink
            to="/history"
            className={`chip ${isActive("/history") ? "chip-active" : ""}`}
          >
            History
          </NavLink>
          <button className="chip" onClick={() => navigate("/login")}>
            Logout
          </button>
        </nav>
      </header>

      <main className="main">
        <Outlet />
      </main>
    </div>
  );
}
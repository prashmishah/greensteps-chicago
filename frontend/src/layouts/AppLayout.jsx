import React from "react";
import { NavLink, Outlet, useNavigate } from "react-router-dom";
import "./layout.css";

export default function AppLayout() {
  const navigate = useNavigate();

  return (
    <div className="app-shell">
      <header className="topbar">
        <div
          className="brand"
          onClick={() => navigate("/")}
          role="button"
          tabIndex={0}
        >
          <div className="brand-icon">🍃</div>
          <div className="brand-text">
            <div className="brand-title">Green Steps</div>
            <div className="brand-subtitle">
              Track your carbon footprint
            </div>
          </div>
        </div>

        <nav className="topbar-actions">
          <NavLink
            to="/history"
            className={({ isActive }) =>
              isActive ? "nav-link active-link" : "nav-link"
            }
          >
            History
          </NavLink>

          <NavLink
            to="/add-activity"
            className={({ isActive }) =>
            isActive ? "nav-link active-link" : "nav-link"
            }
          >
            Add Activity
          </NavLink>

          <button className="logout-btn" onClick={() => navigate("/login")}>
            Logout
          </button>
        </nav>
      </header>

      <main className="main-content">
        <Outlet />
      </main>
    </div>
  );
}
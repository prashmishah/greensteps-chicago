import React from "react";
import { useNavigate } from "react-router-dom";

export default function NotFound() {
  const navigate = useNavigate();

  return (
    <div className="page">
      <h2>404 — Page not found</h2>
      <p className="muted">That route doesn’t exist.</p>
      <button className="btn" onClick={() => navigate("/")}>Go Home</button>
    </div>
  );
}
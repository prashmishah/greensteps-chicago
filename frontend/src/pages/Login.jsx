import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { setActiveUser } from "../utils/auth.js";
import "./auth.css";

export default function Login() {
  const navigate = useNavigate();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const onSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await fetch("/api/users/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username: email, password }),
      });

      if (!response.ok) {
        const errorBody = await response.json().catch(() => ({}));
        const message = errorBody?.message || "Login failed";
        throw new Error(message);
      }

      const user = await response.json();
      setActiveUser(user);
      navigate("/");
    } catch (error) {
      console.error(error);
      alert(error.message || "Unable to login right now. Please check your credentials.");
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-card">
        <h2>Login</h2>
        <p className="auth-subtitle">Welcome back.</p>

        {/* removed className="form" */}
        <form onSubmit={onSubmit}>
          <label>Email</label>
          <input
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            type="email"
            required
          />

          <label>Password</label>
          <input
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            type="password"
            required
          />

          <button type="submit">Login</button>
        </form>
      </div>
    </div>
  );
}
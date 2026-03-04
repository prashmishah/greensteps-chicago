import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { setActiveUser } from "../utils/auth.js";
import "./auth.css";

export default function Register() {
  const navigate = useNavigate();

  const [name, setName] = useState("");   // fixed broken line
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const onSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await fetch("/api/users", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username: email, password }),
      });

      if (!response.ok) {
        const errorBody = await response.json().catch(() => ({}));
        const message = errorBody?.message || "Registration failed";
        throw new Error(message);
      }

      const user = await response.json();
      setActiveUser(user);
      navigate("/");
    } catch (error) {
      console.error(error);
      alert(error.message || "Unable to register right now. Please try again.");
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-card">
        <h2>Create account</h2>
        <p className="auth-subtitle">Start tracking your footprint.</p>

        {/* removed className="form" */}
        <form onSubmit={onSubmit}>
          <label>Name</label>
          <input
            value={name}
            onChange={(e) => setName(e.target.value)}
            required
          />

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

          <button type="submit">Register</button>
        </form>
      </div>
    </div>
  );
}
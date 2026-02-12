import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { setActiveUser } from "../utils/auth.js";

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
    <div className="auth-card">
      <h2>Login</h2>
      <p className="muted">Welcome back.</p>

      <form className="form" onSubmit={onSubmit}>
        <div className="form-row">
          <label>Email</label>
          <input value={email} onChange={(e) => setEmail(e.target.value)} type="email" required />
        </div>

        <div className="form-row">
          <label>Password</label>
          <input value={password} onChange={(e) => setPassword(e.target.value)} type="password" required />
        </div>

        <button className="btn" type="submit">Login</button>
      </form>
    </div>
  );
}

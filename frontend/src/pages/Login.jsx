import React, { useState } from "react";
import { useNavigate } from "react-router-dom";

export default function Login() {
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const onSubmit = (e) => {
    e.preventDefault();
    // Placeholder auth (replace with backend later)
    navigate("/");
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
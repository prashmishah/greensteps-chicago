import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./activity.css";

const MODE_ICONS = {
  car: "🚗",
  rideshare: "🚕",
  bus: "🚌",
  train: "🚆",
  bike: "🚲",
  walk: "🚶",
};

const ALT_ICONS = {
  walk: "🚶",
  bike: "🚲",
  bus: "🚌",
  train: "🚆",
  none: "✅",
};

const MILES_TO_KM = 1.60934;

export default function Recommendations() {
  const navigate = useNavigate();

  const [form, setForm] = useState({ mode: "car", distance: "" });
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const update = (k) => (e) => {
    setForm((p) => ({ ...p, [k]: e.target.value }));
    setResult(null);
    setError(null);
  };

  const onSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setResult(null);

    const miles = Number(form.distance);
    if (!miles || miles <= 0) {
      setError("Please enter a valid distance.");
      return;
    }

    const distanceKm = +(miles * MILES_TO_KM).toFixed(2);

    setLoading(true);
    try {
      const res = await fetch(
        `/api/recommendations?mode=${form.mode}&distanceKm=${distanceKm}`
      );
      if (!res.ok) throw new Error("Failed to get recommendation");
      const data = await res.json();
      setResult({ ...data, distanceKm });
    } catch (err) {
      console.error(err);
      setError("Unable to get a recommendation right now. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  const isGoodChoice =
    result && (result.alternateMode === "none" || result.alternateMode === form.mode);

  return (
    <div className="page">
      <div className="form-wrapper">

        {/* Header */}
        <div className="form-header">
          <button
            className="btn btn-secondary"
            onClick={() => navigate("/")}
            type="button"
          >
            ← Back
          </button>
          <div className="form-title">
            <span className="form-icon">🌿</span>
            <h2>Get a Greener Recommendation</h2>
          </div>
        </div>

        {/* Form */}
        <form className="form" onSubmit={onSubmit}>
          <div className="form-row">
            <label>Travel Mode</label>
            <select value={form.mode} onChange={update("mode")}>
              <option value="car">🚗 Car</option>
              <option value="rideshare">🚕 Rideshare</option>
              <option value="bus">🚌 Bus</option>
              <option value="train">🚆 Train</option>
              <option value="bike">🚲 Bike</option>
              <option value="walk">🚶 Walk</option>
            </select>
          </div>

          <div className="form-row">
            <label>Distance (miles)</label>
            <input
              type="number"
              min="0"
              step="0.1"
              value={form.distance}
              onChange={update("distance")}
              placeholder="e.g., 3.2"
              required
            />
          </div>

          {error && (
            <div className="muted" style={{ color: "#C62828", marginBottom: "0.5rem" }}>
              {error}
            </div>
          )}

          <div className="form-actions">
            <button className="btn" type="submit" disabled={loading}>
              {loading ? "Checking…" : "Get Recommendation"}
            </button>
          </div>
        </form>

        {/* Result */}
        {result && (
          <div
            className="co2-box"
            style={{
              marginTop: "1.5rem",
              borderLeft: `4px solid ${isGoodChoice ? "#2C5F2D" : "#E65100"}`,
              textAlign: "left",
              padding: "1.2rem 1.5rem",
            }}
          >
            {/* Your current choice */}
            <div style={{ marginBottom: "0.75rem" }}>
              <span className="co2-label">Your choice</span>
              <div style={{ fontSize: "1.1rem", fontWeight: 600, marginTop: "0.2rem" }}>
                {MODE_ICONS[form.mode] || "🚗"} {form.mode.charAt(0).toUpperCase() + form.mode.slice(1)}
                <span className="muted" style={{ fontWeight: 400, marginLeft: "0.5rem" }}>
                  — {form.distance} mi ({result.distanceKm} km)
                </span>
              </div>
            </div>

            {/* Recommendation */}
            <div style={{ marginBottom: "0.75rem" }}>
              <span className="co2-label">Recommendation</span>
              <div style={{ fontSize: "1.1rem", fontWeight: 600, marginTop: "0.2rem", color: isGoodChoice ? "#2C5F2D" : "#E65100" }}>
                {ALT_ICONS[result.alternateMode] || "🌿"}{" "}
                {result.alternateMode === "none"
                  ? "You're already making a great choice!"
                  : result.alternateMode.charAt(0).toUpperCase() + result.alternateMode.slice(1)}
              </div>
            </div>

            {/* Message */}
            <div className="muted" style={{ fontSize: "0.95rem", marginTop: "0.25rem" }}>
              💬 {result.message}
            </div>
          </div>
        )}

        {/* Footer nav */}
        <div className="form-actions" style={{ marginTop: "1rem" }}>
          <button
            className="btn btn-secondary"
            type="button"
            onClick={() => navigate("/add-activity")}
          >
            Log an Activity
          </button>
          <button
            className="btn btn-secondary"
            type="button"
            onClick={() => navigate("/history")}
          >
            View History
          </button>
        </div>

      </div>
    </div>
  );
}

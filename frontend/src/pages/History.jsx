import React, { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";

const STORAGE_KEY = "greensteps_entries";

function loadEntries() {
  try {
    return JSON.parse(localStorage.getItem(STORAGE_KEY) || "[]");
  } catch {
    return [];
  }
}

function saveEntries(entries) {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(entries));
}

export default function History() {
  const navigate = useNavigate();
  const [entries, setEntries] = useState([]);

  useEffect(() => {
    setEntries(loadEntries());
  }, []);

  const total = entries.length;

  const onDelete = (id) => {
    const updated = entries.filter((e) => e.id !== id && e.clientId !== id);
    setEntries(updated);
    saveEntries(updated);
  };

  const formatted = useMemo(
    () =>
      entries.map((e) => ({
        ...e,
        when: new Date(e.startTime || Date.now()).toLocaleString(),
      })),
    [entries]
  );

  return (
    <div className="page">
      <div className="history-header">
        <div>
          <h2>History</h2>
          <p className="muted">{total} activities logged</p>
        </div>

        <div className="history-actions">
          <button className="btn btn-secondary" onClick={() => navigate("/")}>
            + Add New
          </button>
          <button
            className="btn btn-danger"
            onClick={() => {
              saveEntries([]);
              setEntries([]);
            }}
            disabled={entries.length === 0}
          >
            Clear All
          </button>
        </div>
      </div>

      {formatted.length === 0 ? (
        <div className="empty">
          <div className="empty-title">No history yet</div>
          <div className="muted">Log an activity from the dashboard.</div>
          <button className="btn" onClick={() => navigate("/")}>Go to Dashboard</button>
        </div>
      ) : (
        <div className="list">
          {formatted.map((e) => (
            <div key={e.id ?? e.clientId} className="list-item">
              <div className="li-left">
                <div className="li-icon">{e.icon || "🍃"}</div>
                <div>
                  <div className="li-title">{e.title}</div>
                  <div className="li-sub">{e.when}</div>
                </div>
              </div>

              <div className="li-right">
                <div className="co2-pill">{Number(e.carbonKg || 0).toFixed(2)} kg CO₂</div>
                <button className="btn btn-secondary" onClick={() => navigate(`/activity/${e.activityType}`)}>
                  Log Again
                </button>
                <button className="btn btn-danger" onClick={() => onDelete(e.id ?? e.clientId)}>
                  Delete
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

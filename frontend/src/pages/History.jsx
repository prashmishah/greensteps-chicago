import React, { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getActiveUser } from "../utils/auth.js";
import "./history.css";

const ACTIVITY_META = {
  commute_university: { title: "Commuting to University", icon: "🎓" },
  commute_work: { title: "Commuting to Work", icon: "💼" },
  dining: { title: "Dining at Restaurant", icon: "🍽️" },
  grocery: { title: "Grocery Shopping", icon: "🛒" },
  gym: { title: "Gym Visit", icon: "💪" },
  shopping: { title: "Shopping", icon: "🛍️" },
  leisure: { title: "Leisure Activity", icon: "🎨" },
};

export default function History() {
  const navigate = useNavigate();
  const [entries, setEntries] = useState([]);

  useEffect(() => {
    const fetchEntries = async () => {
      try {
        const user = getActiveUser();
        if (!user?.id) {
          setEntries([]);
          return;
        }

        const response = await fetch(`/api/activities?userId=${user.id}`);
        if (!response.ok) {
          throw new Error("Failed to load activities");
        }

        const data = await response.json();
        setEntries(data);
      } catch (error) {
        console.error(error);
        setEntries([]);
      }
    };

    fetchEntries();
  }, []);

  const total = entries.length;

  const onDelete = (id) => {
    const updated = entries.filter((e) => e.id !== id);
    setEntries(updated);
  };

  const formatted = useMemo(
    () =>
      entries.map((e) => ({
        ...e,
        when: new Date(e.startTime || Date.now()).toLocaleString(),
        title: ACTIVITY_META[e.activityType]?.title || e.activityType,
        icon: ACTIVITY_META[e.activityType]?.icon || "🍃",
      })),
    [entries]
  );

  return (
    <div className="history-page">
      <div className="history-container">

        <div className="history-header">
          <div>
            <h2>History</h2>
            <p className="muted">{total} activities logged</p>
          </div>

          <div className="history-actions">
            <button className="btn btn-secondary" onClick={() => navigate("/add-activity")}>
              + Add New
            </button>

            <button
              className="btn btn-danger"
              onClick={() => setEntries([])}
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
            <button className="btn" onClick={() => navigate("/")}>
              Go to Dashboard
            </button>
          </div>
        ) : (
          <div className="list">
            {formatted.map((e) => (
              <div key={e.id} className="list-item">
                <div className="li-left">
                  <div className="li-icon">{e.icon}</div>
                  <div>
                    <div className="li-title">{e.title}</div>
                    <div className="li-sub">{e.when}</div>
                  </div>
                </div>

                <div className="li-right">
                  <div className="co2-pill">
                    {Number(e.carbonKg || 0).toFixed(2)} kg CO₂
                  </div>

                  <button
                    className="btn btn-secondary"
                    onClick={() => navigate(`/activity/${e.activityType}`)}
                  >
                    Log Again
                  </button>

                  <button
                    className="btn btn-danger"
                    onClick={() => onDelete(e.id)}
                  >
                    Delete
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}

      </div>
    </div>
  );
}
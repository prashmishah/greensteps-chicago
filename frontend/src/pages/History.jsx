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

// CO2 per km by mode (kg)
const CO2_PER_KM = {
  car: 0.21, rideshare: 0.25, bus: 0.08,
  train: 0.04, bike: 0, walk: 0,
};
const CAR_BASELINE = CO2_PER_KM.car;

function calcCO2Saved(activity) {
  const mode = (activity.mode || "car").toLowerCase();
  const km = activity.distanceKm || 0;
  const factor = CO2_PER_KM[mode] ?? 0.21;
  const saved = (CAR_BASELINE - factor) * km;
  return saved > 0 ? saved.toFixed(2) : null;
}

export default function History() {
  const navigate = useNavigate();
  const [entries, setEntries] = useState([]);

  useEffect(() => {
    const fetchEntries = async () => {
      try {
        const user = getActiveUser();
        if (!user?.id) { setEntries([]); return; }

        const response = await fetch(`/api/activities?userId=${user.id}`);
        if (!response.ok) throw new Error("Failed to load activities");

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

  const onDelete = async (id) => {
    try {
      const response = await fetch(`/api/activities/${id}`, { method: "DELETE" });
      if (!response.ok) throw new Error("Delete failed");
      setEntries(prev => prev.filter(e => e.id !== id));
    } catch (error) {
      console.error(error);
      alert("Unable to delete activity right now.");
    }
  };

  const formatted = useMemo(
    () =>
      entries.map((e) => ({
        ...e,
        when: new Date(e.startTime || Date.now()).toLocaleString(),
        title: ACTIVITY_META[e.activityType]?.title || e.activityType,
        icon: ACTIVITY_META[e.activityType]?.icon || "🍃",
        co2Saved: calcCO2Saved(e),
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
              onClick={async () => {
                try {
                  await Promise.all(
                    entries.map((entry) =>
                      fetch(`/api/activities/${entry.id}`, { method: "DELETE" })
                    )
                  );
                  setEntries([]);
                } catch (error) {
                  console.error(error);
                  alert("Unable to clear activities right now.");
                }
              }}
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
                    {e.mode && (
                      <div className="li-sub">
                        {e.mode.charAt(0).toUpperCase() + e.mode.slice(1)}
                        {e.distanceKm ? ` • ${e.distanceKm} km` : ""}
                      </div>
                    )}
                  </div>
                </div>

                <div className="li-right">
                  {/* CO2 emitted */}
                  <div className="co2-pill">
                    {Number(e.carbonKg || 0).toFixed(2)} kg CO₂
                  </div>

                  {/* CO2 saved — only show if > 0 */}
                  {e.co2Saved && (
                    <div className="co2-saved-pill">
                      🌱 {e.co2Saved} kg saved
                    </div>
                  )}

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

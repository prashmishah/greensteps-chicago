import React from "react";
import { useNavigate } from "react-router-dom";
import "../components/dashboard/dashboard.css";

const ACTIVITY_META = {
  commute_university: { title: "Commuting to University", icon: "🎓" },
  commute_work: { title: "Commuting to Work", icon: "💼" },
  dining: { title: "Dining at Restaurant", icon: "🍽️" },
  grocery: { title: "Grocery Shopping", icon: "🛒" },
  gym: { title: "Gym Visit", icon: "💪" },
  shopping: { title: "Shopping", icon: "🛍️" },
  leisure: { title: "Leisure Activity", icon: "🎨" },
};

export default function ActivitySelection() {
  const navigate = useNavigate();

  return (
    <div className="dashboard-container">
      <h2 className="dashboard-title">Choose Activity Type</h2>

      <div className="summary-grid">
        {Object.entries(ACTIVITY_META).map(([key, value]) => (
          <div
            key={key}
            className="summary-card"
            style={{ cursor: "pointer", textAlign: "center" }}
            onClick={() => navigate(`/activity/${key}`)}
          >
            <div style={{ fontSize: "30px" }}>{value.icon}</div>
            <div className="card-number" style={{ fontSize: "18px", marginTop: "10px" }}>
              {value.title}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
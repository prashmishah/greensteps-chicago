import React from "react";
import { useNavigate } from "react-router-dom";

const tiles = [
  { key: "commute_university", title: "Commuting to University", subtitle: "Track your daily commute to campus", icon: "🎓" },
  { key: "commute_work", title: "Commuting to Work", subtitle: "Track your work commute", icon: "💼" },
  { key: "dining", title: "Dining at Restaurant", subtitle: "Going out to eat", icon: "🍽️" },
  { key: "grocery", title: "Grocery Shopping", subtitle: "Shopping for food and essentials", icon: "🛒" },
  { key: "gym", title: "Gym Visit", subtitle: "Going to the gym or fitness center", icon: "💪" },
  { key: "shopping", title: "Shopping", subtitle: "General shopping activities", icon: "🛍️" },
  { key: "leisure", title: "Leisure Activity", subtitle: "Entertainment and leisure", icon: "🎨" },
];

export default function Dashboard() {
  const navigate = useNavigate();

  return (
    <div className="page">
      <div className="page-hero">
        <h1>What would you like to do?</h1>
        <p>Select your activity to get started</p>
      </div>

      <div className="grid">
        {tiles.map((t) => (
          <button
            key={t.key}
            className="card"
            onClick={() => navigate(`/activity/${t.key}`)}
            type="button"
          >
            <div className="card-icon">{t.icon}</div>
            <div className="card-body">
              <div className="card-title">{t.title}</div>
              <div className="card-subtitle">{t.subtitle}</div>
            </div>
          </button>
        ))}
      </div>
    </div>
  );
}
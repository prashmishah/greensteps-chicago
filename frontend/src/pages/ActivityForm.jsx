import React, { useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { getActiveUser } from "../utils/auth.js";
import "./activity.css";

const ACTIVITY_META = {
  commute_university: { title: "Commuting to University", icon: "🎓" },
  commute_work: { title: "Commuting to Work", icon: "💼" },
  dining: { title: "Dining at Restaurant", icon: "🍽️" },
  grocery: { title: "Grocery Shopping", icon: "🛒" },
  gym: { title: "Gym Visit", icon: "💪" },
  shopping: { title: "Shopping", icon: "🛍️" },
  leisure: { title: "Leisure Activity", icon: "🎨" },
};

const MILES_TO_KM = 1.60934;

function nowLocalDateTimeValue() {
  const d = new Date();
  const pad = (n) => String(n).padStart(2, "0");
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`;
}

function milesToKm(distanceMiles) {
  const miles = Number(distanceMiles);
  if (!Number.isFinite(miles) || miles <= 0) {
    return null;
  }
  return +(miles * MILES_TO_KM).toFixed(2);
}

function addMinutes(isoString, minutes) {
  const base = new Date(isoString);
  if (Number.isFinite(minutes) && minutes > 0) {
    base.setMinutes(base.getMinutes() + minutes);
  }
  return base.toISOString();
}

// Super simple estimate just to have something working
function estimateCO2(activityType, data) {
  const distance = Number(data.distance || 0);
  const mode = data.mode || "car";

  if (activityType.startsWith("commute")) {
    // very rough multipliers
    const perMile = {
      walk: 0,
      bike: 0,
      train: 0.09,
      bus: 0.12,
      car: 0.35,
      rideshare: 0.40,
    }[mode] ?? 0.35;
    return +(distance * perMile).toFixed(2);
  }

  if (activityType === "dining") {
    const meal = data.mealType || "lunch";
    const diet = data.diet || "vegetarian";
    const base = meal === "dinner" ? 2.2 : meal === "breakfast" ? 1.1 : 1.6;
    const factor = diet === "vegan" ? 0.7 : diet === "vegetarian" ? 0.85 : 1.2;
    return +(base * factor).toFixed(2);
  }

  if (activityType === "grocery" || activityType === "shopping") {
    const basket = data.basket || "medium";
    const base = basket === "small" ? 1.0 : basket === "large" ? 3.0 : 2.0;
    return +base.toFixed(2);
  }

  if (activityType === "gym" || activityType === "leisure") {
    // mainly travel related if provided
    const perMile = { walk: 0, bike: 0, train: 0.09, bus: 0.12, car: 0.35 }[mode] ?? 0.2;
    return +(distance * perMile).toFixed(2);
  }

  return 0;
}

export default function ActivityForm() {
  const { type } = useParams();
  const navigate = useNavigate();

  const meta = ACTIVITY_META[type] || { title: "Activity", icon: "🍃" };

  const isCommute = type === "commute_university" || type === "commute_work";

  const [form, setForm] = useState(() => ({
    datetime: nowLocalDateTimeValue(),
    // shared
    notes: "",
    // travel-ish
    mode: "car",
    distance: "",
    // grocery/shopping
    basket: "medium",
    // dining
    mealType: "lunch",
    diet: "vegetarian",
    // gym/leisure
    duration: "",
    category: "general",
  }));

  const co2 = useMemo(() => estimateCO2(type || "", form), [type, form]);

  const update = (k) => (e) => setForm((p) => ({ ...p, [k]: e.target.value }));

  const onSubmit = async (e) => {
    e.preventDefault();

    const startTime = new Date(form.datetime).toISOString();
    const durationMinutes = Number(form.duration || 0);
    const endTime = addMinutes(startTime, durationMinutes);
    const distanceKm = milesToKm(form.distance);
    const user = getActiveUser();
    const userId = user?.id;
    if (!userId) {
      alert("Please register or log in first.");
      navigate("/login");
      return;
    }

    const entry = {
      id: null,
      userId,
      activityType: type,
      description: form.notes,
      startTime,
      endTime,
      distanceKm,
      mode: form.mode,
      carbonKg: co2,
    };

    try {
      const response = await fetch("/api/activities", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(entry),
      });

      if (!response.ok) {
        throw new Error("Failed to save activity");
      }

      navigate("/");
    } catch (error) {
      console.error(error);
      alert("Unable to save activity right now. Please try again.");
    }
  };

  if (!ACTIVITY_META[type]) {
    return (
      <div className="page">
        <h2>Unknown activity</h2>
        <p className="muted">This activity type does not exist.</p>
        <button className="btn" onClick={() => navigate("/")}>Go back</button>
      </div>
    );
  }

  return (
  <div className="page">
    <div className="form-wrapper">

      <div className="form-header">
        <button
          className="btn btn-secondary"
          onClick={() => navigate(-1)}
          type="button"
        >
          ← Back
        </button>

        <div className="form-title">
          <span className="form-icon">{meta.icon}</span>
          <h2>{meta.title}</h2>
        </div>
      </div>

      <form className="form" onSubmit={onSubmit}>
        <div className="form-row">
          <label>Date & time</label>
          <input type="datetime-local" value={form.datetime} onChange={update("datetime")} required />
        </div>

        {(isCommute || type === "gym" || type === "leisure") && (
          <>
            <div className="form-row">
              <label>Mode</label>
              <select value={form.mode} onChange={update("mode")}>
                <option value="walk">Walk</option>
                <option value="bike">Bike</option>
                <option value="bus">Bus</option>
                <option value="train">Train</option>
                <option value="car">Car</option>
                <option value="rideshare">Rideshare</option>
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
              />
            </div>
          </>
        )}

        {type === "dining" && (
          <>
            <div className="form-row">
              <label>Meal type</label>
              <select value={form.mealType} onChange={update("mealType")}>
                <option value="breakfast">Breakfast</option>
                <option value="lunch">Lunch</option>
                <option value="dinner">Dinner</option>
              </select>
            </div>

            <div className="form-row">
              <label>Diet</label>
              <select value={form.diet} onChange={update("diet")}>
                <option value="vegan">Vegan</option>
                <option value="vegetarian">Vegetarian</option>
                <option value="nonveg">Non-veg</option>
              </select>
            </div>
          </>
        )}

        {(type === "grocery" || type === "shopping") && (
          <div className="form-row">
            <label>Basket size</label>
            <select value={form.basket} onChange={update("basket")}>
              <option value="small">Small</option>
              <option value="medium">Medium</option>
              <option value="large">Large</option>
            </select>
          </div>
        )}

        {type === "gym" && (
          <div className="form-row">
            <label>Workout duration (minutes)</label>
            <input
              type="number"
              min="0"
              step="1"
              value={form.duration}
              onChange={update("duration")}
              placeholder="e.g., 45"
            />
          </div>
        )}

        {type === "leisure" && (
          <div className="form-row">
            <label>Leisure category</label>
            <select value={form.category} onChange={update("category")}>
              <option value="general">General</option>
              <option value="movie">Movie</option>
              <option value="park">Park</option>
              <option value="event">Event</option>
              <option value="museum">Museum</option>
            </select>
          </div>
        )}

        <div className="form-row">
          <label>Notes (optional)</label>
          <input value={form.notes} onChange={update("notes")} placeholder="Any extra details…" />
        </div>

        <div className="co2-box">
          <div className="co2-label">Estimated CO₂</div>
          <div className="co2-value">{co2} kg</div>
          <div className="muted">You can refine this later — for now it’s a working baseline.</div>
        </div>

        <div className="form-actions">
          <button className="btn" type="submit">Save Activity</button>
          <button className="btn btn-secondary" type="button" onClick={() => navigate("/history")}>
            View History
          </button>
        </div>
      </form>
    </div>
  </div>
  );
}


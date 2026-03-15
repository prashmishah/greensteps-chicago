import { getActiveUser } from "../utils/auth.js";
import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import SummaryCard from "../components/dashboard/SummaryCard";
import EmissionsLineChart from "../components/dashboard/EmissionsLineChart";
import TransportBarChart from "../components/dashboard/TransportBarChart";
import RecentActivityTable from "../components/dashboard/RecentActivityTable";
import "../components/dashboard/dashboard.css";

// CO2 per km by mode (kg) — used to estimate savings vs car baseline
const CO2_PER_KM = {
  car: 0.21, rideshare: 0.25, bus: 0.08,
  train: 0.04, bike: 0, walk: 0,
};
const CAR_BASELINE = CO2_PER_KM.car;

function calcCO2Saved(activities) {
  return activities.reduce((total, a) => {
    const mode = (a.mode || "car").toLowerCase();
    const km = a.distanceKm || 0;
    const factor = CO2_PER_KM[mode] ?? 0.21;
    const saved = (CAR_BASELINE - factor) * km;
    return total + (saved > 0 ? saved : 0);
  }, 0);
}

function Dashboard() {
  const navigate = useNavigate();

  const [summary, setSummary] = useState({
    totalActivities: 0,
    totalDistanceKm: 0,
    totalCarbonKg: 0,
    avgCarbonKg: 0,
    byTransport: [],
  });

  const [timeseries, setTimeseries] = useState([]);
  const [recentActivities, setRecentActivities] = useState([]);
  const [co2Saved, setCo2Saved] = useState(0);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchDashboard = async () => {
      try {
        const user = getActiveUser();
        if (!user?.id) { setLoading(false); return; }

        const response = await fetch(`/api/activities?userId=${user.id}`);
        const activities = await response.json();

        if (!activities || activities.length === 0) { setLoading(false); return; }

        const totalActivities = activities.length;
        const totalDistanceKm = activities.reduce((sum, a) => sum + (a.distanceKm || 0), 0);
        const totalCarbonKg = activities.reduce((sum, a) => sum + (a.carbonKg || 0), 0);
        const avgCarbonKg = totalActivities > 0 ? totalCarbonKg / totalActivities : 0;

        const transportMap = {};
        activities.forEach((a) => {
          const mode = a.mode || "unknown";
          if (!transportMap[mode]) transportMap[mode] = 0;
          transportMap[mode] += a.carbonKg || 0;
        });
        const byTransport = Object.keys(transportMap).map((mode) => ({
          mode, co2Kg: transportMap[mode],
        }));

        const dateMap = {};
        activities.forEach((a) => {
          const date = (a.startTime || a.createdAt || "").slice(0, 10);
          if (!dateMap[date]) dateMap[date] = 0;
          dateMap[date] += a.carbonKg || 0;
        });
        const timeseriesData = Object.keys(dateMap)
          .map((date) => ({ date, co2Kg: dateMap[date] }))
          .sort((a, b) => new Date(a.date) - new Date(b.date));

        const sortedActivities = [...activities].sort(
          (a, b) => new Date(b.startTime || b.createdAt) - new Date(a.startTime || a.createdAt)
        );

        // Calculate CO2 saved vs car baseline
        const saved = calcCO2Saved(activities);

        setSummary({ totalActivities, totalDistanceKm, totalCarbonKg, avgCarbonKg, byTransport });
        setTimeseries(timeseriesData);
        setRecentActivities(sortedActivities.slice(0, 5));
        setCo2Saved(saved);

      } catch (error) {
        console.error("Dashboard load failed:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchDashboard();
  }, []);

  if (loading) {
    return <div className="dashboard-loading">Loading Dashboard...</div>;
  }

  return (
    <div className="dashboard-container">
      <h2 className="dashboard-title">Carbon Overview</h2>

      {/* Summary Cards — CO2 Saved is first */}
      <div className="summary-grid">
        <SummaryCard
          title="CO₂ Saved"
          value={co2Saved.toFixed(2)}
          unit="kg"
          highlight
        />
        <SummaryCard title="Total CO₂" value={summary.totalCarbonKg.toFixed(2)} unit="kg" />
        <SummaryCard title="Total Distance" value={summary.totalDistanceKm.toFixed(2)} unit="km" />
        <SummaryCard title="Activities" value={summary.totalActivities} />
        <SummaryCard title="Avg CO₂" value={summary.avgCarbonKg.toFixed(2)} unit="kg" />
      </div>

      <div className="charts-grid">
        <EmissionsLineChart data={timeseries} />
        <TransportBarChart data={summary.byTransport} />
      </div>

      <RecentActivityTable activities={recentActivities} />

      {/* Recommendations button */}
      <div style={{ textAlign: "center", marginTop: "24px" }}>
        <button
          className="btn"
          onClick={() => navigate("/recommendations")}
          style={{ fontSize: "15px", padding: "12px 28px" }}
        >
          🌱 Get Green Recommendations
        </button>
      </div>

    </div>
  );
}

export default Dashboard;

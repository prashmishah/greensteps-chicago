import { getActiveUser } from "../utils/auth.js";
import { useState, useEffect } from "react";
import SummaryCard from "../components/dashboard/SummaryCard";
import EmissionsLineChart from "../components/dashboard/EmissionsLineChart";
import TransportBarChart from "../components/dashboard/TransportBarChart";
import RecentActivityTable from "../components/dashboard/RecentActivityTable";
import "../components/dashboard/dashboard.css";

function Dashboard() {
  const [summary, setSummary] = useState(null);
  const [timeseries, setTimeseries] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
  const fetchDashboard = async () => {
    try {
      const user = getActiveUser();
      const userId = user?.id ?? 1;

      const response = await fetch(`/api/carbon/dashboard?userId=${userId}`);
      if (!response.ok) throw new Error("API failed");

      const data = await response.json();
      setSummary({
        totalActivities: data.summary.totalActivities,
        totalDistanceKm: data.summary.totalDistanceKm,
        totalCarbonKg: data.summary.totalCarbonKg,
        avgCarbonKg: data.summary.avgCarbonKg,
        byTransport: data.byTransport,
      });
      setTimeseries(data.daily.map(d => ({
        date: d.date,
        co2Kg: d.carbonKg,
      })));
    } catch (err) {
      console.warn("Falling back to mock data:", err.message);
      setSummary(mockSummary);
      setTimeseries(mockTimeseries);
    } finally {
      setLoading(false);
    }
  };

  fetchDashboard();
}, []);

  if (loading) return <div className="dashboard-loading">Loading Dashboard...</div>;

  return (
    <div className="dashboard-container">
      <h2 className="dashboard-title">Carbon Overview</h2>

      <div className="summary-grid">
        <SummaryCard title="Total CO₂" value={summary.totalCarbonKg} unit="kg" />
        <SummaryCard title="Total Distance" value={summary.totalDistanceKm} unit="km" />
        <SummaryCard title="Activities" value={summary.totalActivities} />
        <SummaryCard title="Avg CO₂" value={summary.avgCarbonKg} unit="kg" />
      </div>

      <div className="charts-grid">
        <EmissionsLineChart data={timeseries} />
        <TransportBarChart data={summary.byTransport} />
      </div>

      <RecentActivityTable />
    </div>
  );
}

const mockSummary = {
  totalActivities: 12,
  totalDistanceKm: 48.5,
  totalCarbonKg: 16.3,
  avgCarbonKg: 1.36,
  byTransport: [
    { mode: "DRIVE", co2Kg: 8.2 },
    { mode: "CTA", co2Kg: 4.1 },
    { mode: "WALK", co2Kg: 0 }
  ]
};

const mockTimeseries = [
  { date: "2026-02-20", co2Kg: 2.1 },
  { date: "2026-02-21", co2Kg: 1.8 },
  { date: "2026-02-22", co2Kg: 3.4 },
  { date: "2026-02-23", co2Kg: 2.0 }
];

export default Dashboard;
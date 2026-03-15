const CO2_PER_KM = {
  car: 0.21, rideshare: 0.25, bus: 0.08,
  train: 0.04, bike: 0, walk: 0,
};
const CAR_BASELINE = CO2_PER_KM.car;

function calcSaved(activity) {
  const mode = (activity.mode || "car").toLowerCase();
  const km = activity.distanceKm || 0;
  const factor = CO2_PER_KM[mode] ?? 0.21;
  const saved = (CAR_BASELINE - factor) * km;
  return saved > 0 ? saved.toFixed(2) : "—";
}

function formatLabel(value) {
  if (!value) return "-";
  return String(value)
    .replaceAll("_", " ")
    .replace(/\b\w/g, (c) => c.toUpperCase());
}

export default function RecentActivityTable({ activities = [] }) {
  return (
    <div className="table-card">
      <h4 className="chart-title">Recent Activities</h4>

      {activities.length === 0 ? (
        <p className="muted">No recent activities yet.</p>
      ) : (
        <table>
          <thead>
            <tr>
              <th>Activity</th>
              <th>Mode</th>
              <th>Distance (km)</th>
              <th>CO₂ (kg)</th>
              <th>CO₂ Saved (kg)</th>
            </tr>
          </thead>
          <tbody>
            {activities.map((activity) => (
              <tr key={activity.id}>
                <td>{formatLabel(activity.activityType)}</td>
                <td>{formatLabel(activity.mode)}</td>
                <td>{activity.distanceKm ?? "—"}</td>
                <td>{Number(activity.carbonKg ?? 0).toFixed(2)}</td>
                <td style={{ color: "#2d6a4f", fontWeight: "600" }}>
                  {calcSaved(activity)}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

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
            </tr>
          </thead>
          <tbody>
            {activities.map((activity) => (
              <tr key={activity.id}>
                <td>{formatLabel(activity.activityType)}</td>
                <td>{formatLabel(activity.mode)}</td>
                <td>{activity.distanceKm ?? "-"}</td>
                <td>{Number(activity.carbonKg ?? 0).toFixed(2)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
function RecentActivityTable() {
  return (
    <div>
      <h4>Recent Activities</h4>
      <table border="1" cellPadding="10">
        <thead>
          <tr>
            <th>Mode</th>
            <th>Distance (km)</th>
            <th>CO₂ (kg)</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>Drive</td>
            <td>10</td>
            <td>2.5</td>
          </tr>
        </tbody>
      </table>
    </div>
  );
}

export default RecentActivityTable;
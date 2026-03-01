import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  CartesianGrid,
  ResponsiveContainer
} from "recharts";

function TransportBarChart({ data }) {
  return (
    <div className="chart-card">
      <h4 className="chart-title">Emissions by Transport</h4>
      <ResponsiveContainer width="100%" height={300}>
        <BarChart data={data}>
          <CartesianGrid stroke="#e9ecef" />
          <XAxis dataKey="mode" />
          <YAxis />
          <Tooltip />
          <Bar dataKey="co2Kg" fill="#40916c" radius={[8, 8, 0, 0]} />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
}

export default TransportBarChart;
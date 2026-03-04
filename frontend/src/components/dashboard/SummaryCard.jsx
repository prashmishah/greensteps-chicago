export default function SummaryCard({ title, value, unit }) {
  return (
    <div className="summary-card">
      <div className="summary-title">{title}</div>
      <div className="summary-value">
        {value} {unit}
      </div>
    </div>
  );
}
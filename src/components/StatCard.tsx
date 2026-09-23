interface StatCardProps {
  icon: string;
  value: string | number;
  label: string;
}

export default function StatCard({ icon, value, label }: StatCardProps) {
  return (
    <div className="stat-card">
      <span className="stat-card__icon" aria-hidden="true">
        {icon}
      </span>
      <span className="stat-card__value">{value}</span>
      <span className="stat-card__label">{label}</span>
    </div>
  );
}

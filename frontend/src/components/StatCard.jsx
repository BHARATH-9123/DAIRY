import React from 'react';

export default function StatCard({ label, value, icon: Icon, color = '#2563eb', bg = 'rgba(37, 99, 235, 0.1)', subtext }) {
  return (
    <div className="glass-card kpi-card">
      <div className="kpi-icon-wrap" style={{ background: bg, color }}>
        {Icon && <Icon size={24} />}
      </div>
      <div className="kpi-info">
        <span className="kpi-label">{label}</span>
        <span className="kpi-value">{value}</span>
        {subtext && <span style={{ fontSize: '0.78rem', color: '#64748b', marginTop: '0.2rem' }}>{subtext}</span>}
      </div>
    </div>
  );
}

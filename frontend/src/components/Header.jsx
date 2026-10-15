import React from 'react';
import { Calendar } from 'lucide-react';

export default function Header({ title, breadcrumb = 'M-Dairy Workspace', actions }) {
  const todayFormatted = new Date().toLocaleDateString('en-IN', {
    weekday: 'short',
    day: 'numeric',
    month: 'short',
    year: 'numeric'
  });

  return (
    <header className="top-header">
      <div className="header-left">
        <span className="breadcrumb">{breadcrumb}</span>
        <h2 className="page-title">{title}</h2>
      </div>

      <div className="header-right">
        <div className="header-date-badge">
          <Calendar size={15} color="#2563eb" />
          <span>{todayFormatted}</span>
        </div>
        {actions && <div className="header-actions">{actions}</div>}
      </div>
    </header>
  );
}

import React from 'react';

export default function Badge({ status, variant, children }) {
  let v = variant;
  if (!v && status) {
    const s = String(status).toUpperCase();
    if (s === 'ACTIVE' || s === 'PRESENT' || s === 'PAID') v = 'success';
    else if (s === 'PARTIALLY_PAID' || s === 'ISSUED') v = 'warning';
    else if (s === 'INACTIVE' || s === 'ABSENT' || s === 'OVERDUE') v = 'danger';
    else if (s === 'DRAFT') v = 'neutral';
    else v = 'primary';
  }

  return (
    <span className={`badge badge-${v || 'neutral'}`}>
      {children || status}
    </span>
  );
}

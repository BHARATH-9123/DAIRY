import React from 'react';
import { Inbox } from 'lucide-react';

export default function EmptyState({ title = 'No records found', message = 'There is currently no data to display.', action }) {
  return (
    <div style={{
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      justifyContent: 'center',
      padding: '3.5rem 1.5rem',
      textAlign: 'center',
      gap: '0.85rem'
    }}>
      <div style={{
        width: '56px',
        height: '56px',
        borderRadius: '50%',
        background: 'rgba(148, 163, 184, 0.12)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        color: '#64748b'
      }}>
        <Inbox size={28} />
      </div>
      <div>
        <h4 style={{ fontSize: '1.05rem', fontWeight: 700, color: 'var(--text-primary)' }}>{title}</h4>
        <p style={{ fontSize: '0.88rem', color: 'var(--text-secondary)', marginTop: '0.25rem', maxWidth: '380px' }}>
          {message}
        </p>
      </div>
      {action && <div style={{ marginTop: '0.5rem' }}>{action}</div>}
    </div>
  );
}

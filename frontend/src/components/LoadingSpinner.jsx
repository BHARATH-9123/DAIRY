import React from 'react';
import { Loader2 } from 'lucide-react';

export default function LoadingSpinner({ message = 'Loading data...' }) {
  return (
    <div style={{
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      justifyContent: 'center',
      padding: '3rem 1rem',
      gap: '0.75rem',
      color: 'var(--text-secondary)'
    }}>
      <Loader2 size={32} className="spin-animation" color="#2563eb" />
      <span style={{ fontSize: '0.9rem', fontWeight: 600 }}>{message}</span>
      <style>{`
        @keyframes spin {
          from { transform: rotate(0deg); }
          to { transform: rotate(360deg); }
        }
        .spin-animation {
          animation: spin 1s linear infinite;
        }
      `}</style>
    </div>
  );
}

import React, { useEffect } from 'react';
import { CheckCircle2, AlertCircle, Info, X } from 'lucide-react';

export default function Toast({ message, type = 'success', onClose, duration = 4000 }) {
  useEffect(() => {
    if (!message) return;
    const timer = setTimeout(() => {
      onClose();
    }, duration);
    return () => clearTimeout(timer);
  }, [message, duration, onClose]);

  if (!message) return null;

  const isSuccess = type === 'success';
  const isError = type === 'error';

  return (
    <div style={{
      position: 'fixed',
      bottom: '1.5rem',
      right: '1.5rem',
      zIndex: 200,
      background: 'rgba(255, 255, 255, 0.95)',
      backdropFilter: 'blur(16px)',
      border: `1px solid ${isSuccess ? 'rgba(5, 150, 105, 0.3)' : isError ? 'rgba(220, 38, 38, 0.3)' : 'var(--border-light)'}`,
      borderRadius: 'var(--radius-lg)',
      boxShadow: '0 10px 25px -5px rgba(15, 23, 42, 0.15)',
      padding: '0.85rem 1.25rem',
      display: 'flex',
      alignItems: 'center',
      gap: '0.75rem',
      maxWidth: '420px',
      animation: 'slideUp 0.25s ease'
    }}>
      {isSuccess && <CheckCircle2 size={20} color="#059669" />}
      {isError && <AlertCircle size={20} color="#dc2626" />}
      {!isSuccess && !isError && <Info size={20} color="#2563eb" />}

      <span style={{ fontSize: '0.9rem', fontWeight: 600, color: 'var(--text-primary)', flex: 1 }}>
        {message}
      </span>

      <button onClick={onClose} className="btn-icon" style={{ padding: '0.2rem' }}>
        <X size={16} />
      </button>
    </div>
  );
}

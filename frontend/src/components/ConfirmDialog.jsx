import React from 'react';
import Modal from './Modal';
import { AlertTriangle } from 'lucide-react';

export default function ConfirmDialog({ isOpen, onClose, onConfirm, title = 'Confirm Action', message, confirmText = 'Confirm', confirmVariant = 'danger', isLoading = false }) {
  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title={title}
      maxWidth="460px"
      footer={
        <>
          <button className="btn btn-secondary" onClick={onClose} disabled={isLoading}>
            Cancel
          </button>
          <button
            className={`btn btn-${confirmVariant}`}
            onClick={onConfirm}
            disabled={isLoading}
          >
            {isLoading ? 'Processing...' : confirmText}
          </button>
        </>
      }
    >
      <div style={{ display: 'flex', gap: '1rem', alignItems: 'flex-start' }}>
        <div style={{
          width: '40px',
          height: '40px',
          borderRadius: '50%',
          background: 'rgba(220, 38, 38, 0.1)',
          color: '#dc2626',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          flexShrink: 0
        }}>
          <AlertTriangle size={22} />
        </div>
        <div>
          <p style={{ color: 'var(--text-secondary)', fontSize: '0.95rem', lineHeight: 1.5 }}>
            {message}
          </p>
        </div>
      </div>
    </Modal>
  );
}

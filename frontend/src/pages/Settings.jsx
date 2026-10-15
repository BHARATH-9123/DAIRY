import React, { useState, useEffect } from 'react';
import Header from '../components/Header';
import Toast from '../components/Toast';
import LoadingSpinner from '../components/LoadingSpinner';
import { settingsService } from '../services/settingsService';
import {
  Settings as SettingsIcon,
  Save,
  Building,
  Phone,
  Mail,
  MapPin,
  TrendingUp,
  CreditCard,
  ShieldAlert
} from 'lucide-react';

export default function Settings() {
  const [formData, setFormData] = useState({
    businessName: 'M-DAIRY',
    tagline: 'From Farm To Home',
    phone: '',
    email: '',
    address: '',
    defaultCowSellingRate: 60,
    defaultBuffaloSellingRate: 80,
    defaultCowProcurementRate: 45,
    defaultBuffaloProcurementRate: 65,
    currencySymbol: '₹',
    quantityPrecision: 2
  });
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [toast, setToast] = useState(null);

  const fetchSettings = async () => {
    try {
      setLoading(true);
      const data = await settingsService.getSettings();
      if (data) {
        setFormData(data);
      }
    } catch (err) {
      setToast({ type: 'error', message: err.message || 'Failed to load business settings.' });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSettings();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      setSaving(true);
      await settingsService.updateSettings(formData);
      setToast({ type: 'success', message: 'Settings saved successfully in MySQL database!' });
    } catch (err) {
      setToast({ type: 'error', message: err.message || 'Failed to update settings.' });
    } finally {
      setSaving(false);
    }
  };

  return (
    <div>
      <Header title="Business Settings" breadcrumb="M-Dairy • System Configuration" />

      <div className="page-wrapper">
        {toast && <Toast {...toast} onClose={() => setToast(null)} />}

        {loading ? (
          <LoadingSpinner message="Loading settings..." />
        ) : (
          <form onSubmit={handleSubmit} style={{ maxWidth: '850px' }}>
            {/* Business Identity */}
            <div className="glass-card" style={{ marginBottom: '1.5rem' }}>
              <div className="card-header">
                <h3 className="card-title">
                  <Building size={20} color="#2563eb" />
                  <span>Dairy Information</span>
                </h3>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label className="form-label">Business Name *</label>
                  <input
                    type="text"
                    className="form-control"
                    required
                    value={formData.businessName}
                    onChange={(e) => setFormData({ ...formData, businessName: e.target.value })}
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Tagline</label>
                  <input
                    type="text"
                    className="form-control"
                    value={formData.tagline || ''}
                    onChange={(e) => setFormData({ ...formData, tagline: e.target.value })}
                  />
                </div>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label className="form-label">Official Phone</label>
                  <input
                    type="text"
                    className="form-control"
                    value={formData.phone || ''}
                    onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Official Email</label>
                  <input
                    type="email"
                    className="form-control"
                    value={formData.email || ''}
                    onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                  />
                </div>
              </div>

              <div className="form-group">
                <label className="form-label">Dairy Address</label>
                <textarea
                  className="form-control"
                  rows={2}
                  value={formData.address || ''}
                  onChange={(e) => setFormData({ ...formData, address: e.target.value })}
                />
              </div>
            </div>

            {/* Default Rates Configuration */}
            <div className="glass-card" style={{ marginBottom: '1.5rem' }}>
              <div className="card-header">
                <h3 className="card-title">
                  <TrendingUp size={20} color="#059669" />
                  <span>Default Rate Configuration</span>
                </h3>
              </div>

              <div style={{ background: 'rgba(5, 150, 105, 0.05)', padding: '0.85rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--success-border)', marginBottom: '1.25rem' }}>
                <p style={{ fontSize: '0.82rem', color: 'var(--text-secondary)' }}>
                  Changing default rates here applies to newly added customers and vendors. Existing saved attendance, historical rates, and issued bills are preserved and never silently overwritten.
                </p>
              </div>

              <h4 style={{ fontSize: '0.92rem', fontWeight: 700, color: '#2563eb', marginBottom: '0.75rem' }}>
                Customer Selling Rates (Per Litre)
              </h4>
              <div className="form-row" style={{ marginBottom: '1.25rem' }}>
                <div className="form-group">
                  <label className="form-label">Default Cow Selling Rate (₹)</label>
                  <input
                    type="number"
                    step="0.5"
                    min="0"
                    className="form-control"
                    required
                    value={formData.defaultCowSellingRate}
                    onChange={(e) => setFormData({ ...formData, defaultCowSellingRate: parseFloat(e.target.value) || 0 })}
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">Default Buffalo Selling Rate (₹)</label>
                  <input
                    type="number"
                    step="0.5"
                    min="0"
                    className="form-control"
                    required
                    value={formData.defaultBuffaloSellingRate}
                    onChange={(e) => setFormData({ ...formData, defaultBuffaloSellingRate: parseFloat(e.target.value) || 0 })}
                  />
                </div>
              </div>

              <h4 style={{ fontSize: '0.92rem', fontWeight: 700, color: '#7c3aed', marginBottom: '0.75rem' }}>
                Vendor Procurement Rates (Per Litre)
              </h4>
              <div className="form-row">
                <div className="form-group">
                  <label className="form-label">Default Cow Procurement Rate (₹)</label>
                  <input
                    type="number"
                    step="0.5"
                    min="0"
                    className="form-control"
                    required
                    value={formData.defaultCowProcurementRate}
                    onChange={(e) => setFormData({ ...formData, defaultCowProcurementRate: parseFloat(e.target.value) || 0 })}
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">Default Buffalo Procurement Rate (₹)</label>
                  <input
                    type="number"
                    step="0.5"
                    min="0"
                    className="form-control"
                    required
                    value={formData.defaultBuffaloProcurementRate}
                    onChange={(e) => setFormData({ ...formData, defaultBuffaloProcurementRate: parseFloat(e.target.value) || 0 })}
                  />
                </div>
              </div>
            </div>

            {/* Display & Precision Preferences */}
            <div className="glass-card" style={{ marginBottom: '1.5rem' }}>
              <div className="card-header">
                <h3 className="card-title">
                  <CreditCard size={20} color="#d97706" />
                  <span>Currency & Precision</span>
                </h3>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label className="form-label">Currency Symbol</label>
                  <input
                    type="text"
                    className="form-control"
                    value={formData.currencySymbol}
                    onChange={(e) => setFormData({ ...formData, currencySymbol: e.target.value })}
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Quantity Decimal Precision</label>
                  <select
                    className="form-control"
                    value={formData.quantityPrecision}
                    onChange={(e) => setFormData({ ...formData, quantityPrecision: parseInt(e.target.value) })}
                  >
                    <option value={1}>1 Decimal (e.g. 5.5 L)</option>
                    <option value={2}>2 Decimals (e.g. 5.50 L)</option>
                    <option value={3}>3 Decimals (e.g. 5.500 L)</option>
                  </select>
                </div>
              </div>
            </div>

            <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: '1rem' }}>
              <button type="submit" className="btn btn-primary" style={{ padding: '0.75rem 2rem' }} disabled={saving}>
                <Save size={18} />
                <span>{saving ? 'Saving Settings...' : 'Save Settings'}</span>
              </button>
            </div>
          </form>
        )}
      </div>
    </div>
  );
}

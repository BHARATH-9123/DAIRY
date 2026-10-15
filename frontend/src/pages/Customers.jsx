import React, { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import Header from '../components/Header';
import Modal from '../components/Modal';
import ConfirmDialog from '../components/ConfirmDialog';
import Badge from '../components/Badge';
import Toast from '../components/Toast';
import LoadingSpinner from '../components/LoadingSpinner';
import EmptyState from '../components/EmptyState';
import { customerService } from '../services/customerService';
import { formatCurrency, formatQuantity } from '../utils/formatters';
import {
  UserPlus,
  Search,
  Edit2,
  UserX,
  UserCheck,
  Phone,
  MapPin,
  CheckCircle,
  XCircle,
  Filter
} from 'lucide-react';

const initialForm = {
  customerCode: '',
  name: '',
  phone: '',
  address: '',
  milkType: 'COW',
  deliverySchedule: 'MORNING',
  morningCowQty: 0,
  morningBuffaloQty: 0,
  nightCowQty: 0,
  nightBuffaloQty: 0,
  cowRate: 60,
  buffaloRate: 80,
  notes: '',
  active: true,
};

export default function Customers() {
  const [searchParams, setSearchParams] = useSearchParams();
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [activeFilter, setActiveFilter] = useState('');

  // Modal & Form State
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [formData, setFormData] = useState(initialForm);
  const [formSubmitting, setFormSubmitting] = useState(false);

  // Deactivate Confirm State
  const [deactivateId, setDeactivateId] = useState(null);
  const [toast, setToast] = useState(null);

  const fetchCustomers = async () => {
    try {
      setLoading(true);
      const data = await customerService.getAll(search, activeFilter);
      setCustomers(data);
    } catch (err) {
      setToast({ type: 'error', message: err.message || 'Failed to load customers.' });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCustomers();
  }, [search, activeFilter]);

  useEffect(() => {
    if (searchParams.get('action') === 'add') {
      openAddModal();
      setSearchParams({});
    }
  }, [searchParams]);

  const openAddModal = () => {
    setEditingId(null);
    setFormData({
      ...initialForm,
      customerCode: `CUST-${String(customers.length + 1).padStart(3, '0')}`
    });
    setIsModalOpen(true);
  };

  const openEditModal = (c) => {
    setEditingId(c.id);
    setFormData({
      customerCode: c.customerCode,
      name: c.name,
      phone: c.phone || '',
      address: c.address || '',
      milkType: c.milkType,
      deliverySchedule: c.deliverySchedule,
      morningCowQty: c.morningCowQty,
      morningBuffaloQty: c.morningBuffaloQty,
      nightCowQty: c.nightCowQty,
      nightBuffaloQty: c.nightBuffaloQty,
      cowRate: c.cowRate,
      buffaloRate: c.buffaloRate,
      notes: c.notes || '',
      active: c.active,
    });
    setIsModalOpen(true);
  };

  const handleFormSubmit = async (e) => {
    e.preventDefault();
    if (!formData.name.trim() || !formData.customerCode.trim()) {
      setToast({ type: 'error', message: 'Customer code and name are required.' });
      return;
    }

    try {
      setFormSubmitting(true);
      if (editingId) {
        await customerService.update(editingId, formData);
        setToast({ type: 'success', message: 'Customer updated successfully.' });
      } else {
        await customerService.create(formData);
        setToast({ type: 'success', message: 'Customer added successfully.' });
      }
      setIsModalOpen(false);
      fetchCustomers();
    } catch (err) {
      setToast({ type: 'error', message: err.message || 'Operation failed.' });
    } finally {
      setFormSubmitting(false);
    }
  };

  const handleDeactivate = async () => {
    if (!deactivateId) return;
    try {
      await customerService.deactivate(deactivateId);
      setToast({ type: 'success', message: 'Customer safely deactivated. Historical records preserved.' });
      setDeactivateId(null);
      fetchCustomers();
    } catch (err) {
      setToast({ type: 'error', message: err.message || 'Failed to deactivate customer.' });
    }
  };

  const handleActivate = async (id) => {
    try {
      await customerService.activate(id);
      setToast({ type: 'success', message: 'Customer activated successfully.' });
      fetchCustomers();
    } catch (err) {
      setToast({ type: 'error', message: err.message || 'Failed to activate customer.' });
    }
  };

  return (
    <div>
      <Header
        title="Customer Directory"
        breadcrumb="M-Dairy • Customers"
        actions={
          <button className="btn btn-primary" onClick={openAddModal}>
            <UserPlus size={18} />
            <span>Add Customer</span>
          </button>
        }
      />

      <div className="page-wrapper">
        {toast && <Toast {...toast} onClose={() => setToast(null)} />}

        {/* Filter Bar */}
        <div className="glass-card" style={{ marginBottom: '1.25rem', padding: '1rem 1.25rem' }}>
          <div style={{ display: 'flex', gap: '1rem', flexWrap: 'wrap', alignItems: 'center' }}>
            <div style={{ flex: 1, minWidth: '240px', position: 'relative' }}>
              <Search size={18} color="#94a3b8" style={{ position: 'absolute', left: '0.85rem', top: '50%', transform: 'translateY(-50%)' }} />
              <input
                type="text"
                className="form-control"
                placeholder="Search by name, customer code, or phone..."
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                style={{ paddingLeft: '2.5rem' }}
              />
            </div>

            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <Filter size={16} color="#64748b" />
              <select
                className="form-control"
                value={activeFilter}
                onChange={(e) => setActiveFilter(e.target.value)}
                style={{ width: 'auto' }}
              >
                <option value="">All Statuses</option>
                <option value="true">Active Only</option>
                <option value="false">Inactive Only</option>
              </select>
            </div>
          </div>
        </div>

        {/* Table Content */}
        <div className="glass-card" style={{ padding: 0 }}>
          {loading ? (
            <LoadingSpinner message="Loading customer directory..." />
          ) : customers.length === 0 ? (
            <EmptyState
              title="No Customers Found"
              message={search ? "No customers match your search criteria." : "Start by registering your first customer."}
              action={
                <button className="btn btn-primary" onClick={openAddModal}>
                  <UserPlus size={16} />
                  <span>Register Customer</span>
                </button>
              }
            />
          ) : (
            <div className="table-responsive">
              <table className="glass-table">
                <thead>
                  <tr>
                    <th>Code</th>
                    <th>Name</th>
                    <th>Contact</th>
                    <th>Milk Type</th>
                    <th>Schedule</th>
                    <th>Selling Rates</th>
                    <th>Status</th>
                    <th style={{ textAlign: 'right' }}>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {customers.map((c) => (
                    <tr key={c.id}>
                      <td style={{ fontWeight: 700, fontFamily: 'var(--font-mono)' }}>{c.customerCode}</td>
                      <td>
                        <div style={{ fontWeight: 600 }}>{c.name}</div>
                        {c.address && (
                          <div style={{ fontSize: '0.78rem', color: 'var(--text-muted)', display: 'flex', alignItems: 'center', gap: '0.3rem' }}>
                            <MapPin size={12} /> {c.address}
                          </div>
                        )}
                      </td>
                      <td>
                        {c.phone ? (
                          <span style={{ display: 'flex', alignItems: 'center', gap: '0.3rem', fontSize: '0.85rem' }}>
                            <Phone size={13} color="#2563eb" /> {c.phone}
                          </span>
                        ) : (
                          <span style={{ color: 'var(--text-muted)' }}>-</span>
                        )}
                      </td>
                      <td>
                        <Badge variant="primary">{c.milkType}</Badge>
                      </td>
                      <td>
                        <span style={{ fontSize: '0.85rem', fontWeight: 600 }}>{c.deliverySchedule}</span>
                      </td>
                      <td>
                        <div style={{ fontSize: '0.85rem' }}>
                          <div>Cow: <strong>{formatCurrency(c.cowRate)}</strong>/L</div>
                          <div>Buf: <strong>{formatCurrency(c.buffaloRate)}</strong>/L</div>
                        </div>
                      </td>
                      <td>
                        <Badge status={c.active ? 'ACTIVE' : 'INACTIVE'} />
                      </td>
                      <td style={{ textAlign: 'right' }}>
                        <div style={{ display: 'inline-flex', gap: '0.4rem' }}>
                          <button
                            className="btn-icon"
                            title="Edit Customer"
                            onClick={() => openEditModal(c)}
                          >
                            <Edit2 size={16} />
                          </button>
                          {c.active ? (
                            <button
                              className="btn-icon"
                              title="Deactivate Customer"
                              style={{ color: '#dc2626' }}
                              onClick={() => setDeactivateId(c.id)}
                            >
                              <UserX size={16} />
                            </button>
                          ) : (
                            <button
                              className="btn-icon"
                              title="Activate Customer"
                              style={{ color: '#059669' }}
                              onClick={() => handleActivate(c.id)}
                            >
                              <UserCheck size={16} />
                            </button>
                          )}
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>

      {/* Add / Edit Customer Modal */}
      <Modal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title={editingId ? 'Edit Customer Information' : 'Add New Customer'}
        footer={
          <>
            <button className="btn btn-secondary" onClick={() => setIsModalOpen(false)} disabled={formSubmitting}>
              Cancel
            </button>
            <button className="btn btn-primary" onClick={handleFormSubmit} disabled={formSubmitting}>
              {formSubmitting ? 'Saving...' : editingId ? 'Update Customer' : 'Save Customer'}
            </button>
          </>
        }
      >
        <form onSubmit={handleFormSubmit}>
          <div className="form-row">
            <div className="form-group">
              <label className="form-label">Customer Code *</label>
              <input
                type="text"
                className="form-control"
                required
                value={formData.customerCode}
                onChange={(e) => setFormData({ ...formData, customerCode: e.target.value })}
                placeholder="e.g. CUST-001"
              />
            </div>
            <div className="form-group">
              <label className="form-label">Customer Name *</label>
              <input
                type="text"
                className="form-control"
                required
                value={formData.name}
                onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                placeholder="Full Name"
              />
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label className="form-label">Phone Number</label>
              <input
                type="text"
                className="form-control"
                value={formData.phone}
                onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                placeholder="10-digit mobile number"
              />
            </div>
            <div className="form-group">
              <label className="form-label">Address</label>
              <input
                type="text"
                className="form-control"
                value={formData.address}
                onChange={(e) => setFormData({ ...formData, address: e.target.value })}
                placeholder="Street, Area, Village"
              />
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label className="form-label">Milk Type</label>
              <select
                className="form-control"
                value={formData.milkType}
                onChange={(e) => setFormData({ ...formData, milkType: e.target.value })}
              >
                <option value="COW">Cow Only</option>
                <option value="BUFFALO">Buffalo Only</option>
                <option value="BOTH">Both (Cow & Buffalo)</option>
              </select>
            </div>
            <div className="form-group">
              <label className="form-label">Delivery Schedule</label>
              <select
                className="form-control"
                value={formData.deliverySchedule}
                onChange={(e) => setFormData({ ...formData, deliverySchedule: e.target.value })}
              >
                <option value="MORNING">Morning Only</option>
                <option value="NIGHT">Night Only</option>
                <option value="BOTH">Both (Morning & Night)</option>
              </select>
            </div>
          </div>

          {/* Default Quantities (Template for attendance, not automatic bills) */}
          <div style={{ marginTop: '0.75rem', marginBottom: '0.75rem' }}>
            <span style={{ fontSize: '0.85rem', fontWeight: 700, color: 'var(--text-primary)' }}>
              Configured Default Quantities (Litres)
            </span>
            <p style={{ fontSize: '0.78rem', color: 'var(--text-muted)' }}>
              These serve as starting values when marking attendance and do not become billable transactions without saved attendance.
            </p>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label className="form-label">Morning Cow (L)</label>
              <input
                type="number"
                step="0.1"
                min="0"
                className="form-control"
                value={formData.morningCowQty}
                onChange={(e) => setFormData({ ...formData, morningCowQty: parseFloat(e.target.value) || 0 })}
              />
            </div>
            <div className="form-group">
              <label className="form-label">Morning Buffalo (L)</label>
              <input
                type="number"
                step="0.1"
                min="0"
                className="form-control"
                value={formData.morningBuffaloQty}
                onChange={(e) => setFormData({ ...formData, morningBuffaloQty: parseFloat(e.target.value) || 0 })}
              />
            </div>
            <div className="form-group">
              <label className="form-label">Night Cow (L)</label>
              <input
                type="number"
                step="0.1"
                min="0"
                className="form-control"
                value={formData.nightCowQty}
                onChange={(e) => setFormData({ ...formData, nightCowQty: parseFloat(e.target.value) || 0 })}
              />
            </div>
            <div className="form-group">
              <label className="form-label">Night Buffalo (L)</label>
              <input
                type="number"
                step="0.1"
                min="0"
                className="form-control"
                value={formData.nightBuffaloQty}
                onChange={(e) => setFormData({ ...formData, nightBuffaloQty: parseFloat(e.target.value) || 0 })}
              />
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label className="form-label">Cow Selling Rate (₹/L) *</label>
              <input
                type="number"
                step="0.5"
                min="0"
                className="form-control"
                required
                value={formData.cowRate}
                onChange={(e) => setFormData({ ...formData, cowRate: parseFloat(e.target.value) || 0 })}
              />
            </div>
            <div className="form-group">
              <label className="form-label">Buffalo Selling Rate (₹/L) *</label>
              <input
                type="number"
                step="0.5"
                min="0"
                className="form-control"
                required
                value={formData.buffaloRate}
                onChange={(e) => setFormData({ ...formData, buffaloRate: parseFloat(e.target.value) || 0 })}
              />
            </div>
          </div>

          <div className="form-group">
            <label className="form-label">Notes</label>
            <textarea
              className="form-control"
              rows={2}
              value={formData.notes}
              onChange={(e) => setFormData({ ...formData, notes: e.target.value })}
              placeholder="Any special delivery instructions..."
            />
          </div>
        </form>
      </Modal>

      {/* Deactivate Confirmation */}
      <ConfirmDialog
        isOpen={!!deactivateId}
        onClose={() => setDeactivateId(null)}
        onConfirm={handleDeactivate}
        title="Deactivate Customer"
        message="Are you sure you want to deactivate this customer? They will not appear in daily attendance, but all historical bills, payments, and attendance will remain intact."
        confirmText="Deactivate"
      />
    </div>
  );
}

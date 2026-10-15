import React, { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import Header from '../components/Header';
import Modal from '../components/Modal';
import ConfirmDialog from '../components/ConfirmDialog';
import Badge from '../components/Badge';
import Toast from '../components/Toast';
import LoadingSpinner from '../components/LoadingSpinner';
import EmptyState from '../components/EmptyState';
import { vendorService } from '../services/vendorService';
import { formatCurrency } from '../utils/formatters';
import {
  Building2,
  PlusCircle,
  Search,
  Edit2,
  Trash2,
  Phone,
  MapPin,
  Filter,
  CheckCircle2,
  XCircle
} from 'lucide-react';

const initialForm = {
  vendorCode: '',
  name: '',
  phone: '',
  address: '',
  milkType: 'COW',
  collectionSchedule: 'MORNING',
  morningCowQty: 0,
  morningBuffaloQty: 0,
  nightCowQty: 0,
  nightBuffaloQty: 0,
  cowRate: 45,
  buffaloRate: 65,
  notes: '',
  active: true,
};

export default function Vendors() {
  const [searchParams, setSearchParams] = useSearchParams();
  const [vendors, setVendors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [activeFilter, setActiveFilter] = useState('');

  // Modal State
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [formData, setFormData] = useState(initialForm);
  const [formSubmitting, setFormSubmitting] = useState(false);

  // Deactivate state
  const [deactivateId, setDeactivateId] = useState(null);
  const [toast, setToast] = useState(null);

  const fetchVendors = async () => {
    try {
      setLoading(true);
      const data = await vendorService.getAll(search, activeFilter);
      setVendors(data);
    } catch (err) {
      setToast({ type: 'error', message: err.message || 'Failed to load vendors.' });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchVendors();
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
      vendorCode: `VEND-${String(vendors.length + 1).padStart(3, '0')}`
    });
    setIsModalOpen(true);
  };

  const openEditModal = (v) => {
    setEditingId(v.id);
    setFormData({
      vendorCode: v.vendorCode,
      name: v.name,
      phone: v.phone || '',
      address: v.address || '',
      milkType: v.milkType,
      collectionSchedule: v.collectionSchedule,
      morningCowQty: v.morningCowQty,
      morningBuffaloQty: v.morningBuffaloQty,
      nightCowQty: v.nightCowQty,
      nightBuffaloQty: v.nightBuffaloQty,
      cowRate: v.cowRate,
      buffaloRate: v.buffaloRate,
      notes: v.notes || '',
      active: v.active,
    });
    setIsModalOpen(true);
  };

  const handleFormSubmit = async (e) => {
    e.preventDefault();
    if (!formData.name.trim() || !formData.vendorCode.trim()) {
      setToast({ type: 'error', message: 'Vendor code and name are required.' });
      return;
    }

    try {
      setFormSubmitting(true);
      if (editingId) {
        await vendorService.update(editingId, formData);
        setToast({ type: 'success', message: 'Vendor updated successfully.' });
      } else {
        await vendorService.create(formData);
        setToast({ type: 'success', message: 'Vendor added successfully.' });
      }
      setIsModalOpen(false);
      fetchVendors();
    } catch (err) {
      setToast({ type: 'error', message: err.message || 'Operation failed.' });
    } finally {
      setFormSubmitting(false);
    }
  };

  const handleDeactivate = async () => {
    if (!deactivateId) return;
    try {
      await vendorService.deactivate(deactivateId);
      setToast({ type: 'success', message: 'Vendor safely deactivated. Financial history preserved.' });
      setDeactivateId(null);
      fetchVendors();
    } catch (err) {
      setToast({ type: 'error', message: err.message || 'Failed to deactivate vendor.' });
    }
  };

  const handleActivate = async (id) => {
    try {
      await vendorService.activate(id);
      setToast({ type: 'success', message: 'Vendor activated successfully.' });
      fetchVendors();
    } catch (err) {
      setToast({ type: 'error', message: err.message || 'Failed to activate vendor.' });
    }
  };

  return (
    <div>
      <Header
        title="Vendor Directory"
        breadcrumb="M-Dairy • Vendors & Procurement"
        actions={
          <button className="btn btn-primary" onClick={openAddModal}>
            <PlusCircle size={18} />
            <span>Add Vendor</span>
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
                placeholder="Search vendors by name, code, or phone..."
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
            <LoadingSpinner message="Loading vendors..." />
          ) : vendors.length === 0 ? (
            <EmptyState
              title="No Vendors Found"
              message={search ? "No vendors match your search criteria." : "Start by registering your dairy's milk suppliers."}
              action={
                <button className="btn btn-primary" onClick={openAddModal}>
                  <PlusCircle size={16} />
                  <span>Register Vendor</span>
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
                    <th>Collection</th>
                    <th>Procurement Rates</th>
                    <th>Status</th>
                    <th style={{ textAlign: 'right' }}>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {vendors.map((v) => (
                    <tr key={v.id}>
                      <td style={{ fontWeight: 700, fontFamily: 'var(--font-mono)' }}>{v.vendorCode}</td>
                      <td>
                        <div style={{ fontWeight: 600 }}>{v.name}</div>
                        {v.address && (
                          <div style={{ fontSize: '0.78rem', color: 'var(--text-muted)', display: 'flex', alignItems: 'center', gap: '0.3rem' }}>
                            <MapPin size={12} /> {v.address}
                          </div>
                        )}
                      </td>
                      <td>
                        {v.phone ? (
                          <span style={{ display: 'flex', alignItems: 'center', gap: '0.3rem', fontSize: '0.85rem' }}>
                            <Phone size={13} color="#059669" /> {v.phone}
                          </span>
                        ) : (
                          <span style={{ color: 'var(--text-muted)' }}>-</span>
                        )}
                      </td>
                      <td>
                        <Badge variant="success">{v.milkType}</Badge>
                      </td>
                      <td>
                        <span style={{ fontSize: '0.85rem', fontWeight: 600 }}>{v.collectionSchedule}</span>
                      </td>
                      <td>
                        <div style={{ fontSize: '0.85rem' }}>
                          <div>Cow: <strong>{formatCurrency(v.cowRate)}</strong>/L</div>
                          <div>Buf: <strong>{formatCurrency(v.buffaloRate)}</strong>/L</div>
                        </div>
                      </td>
                      <td>
                        <Badge status={v.active ? 'ACTIVE' : 'INACTIVE'} />
                      </td>
                      <td style={{ textAlign: 'right' }}>
                        <div style={{ display: 'inline-flex', gap: '0.4rem' }}>
                          <button
                            className="btn-icon"
                            title="Edit Vendor"
                            onClick={() => openEditModal(v)}
                          >
                            <Edit2 size={16} />
                          </button>
                          {v.active ? (
                            <button
                              className="btn-icon"
                              title="Deactivate Vendor"
                              style={{ color: '#dc2626' }}
                              onClick={() => setDeactivateId(v.id)}
                            >
                              <XCircle size={16} />
                            </button>
                          ) : (
                            <button
                              className="btn-icon"
                              title="Activate Vendor"
                              style={{ color: '#059669' }}
                              onClick={() => handleActivate(v.id)}
                            >
                              <CheckCircle2 size={16} />
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

      {/* Add / Edit Vendor Modal */}
      <Modal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title={editingId ? 'Edit Vendor Information' : 'Add New Vendor'}
        footer={
          <>
            <button className="btn btn-secondary" onClick={() => setIsModalOpen(false)} disabled={formSubmitting}>
              Cancel
            </button>
            <button className="btn btn-primary" onClick={handleFormSubmit} disabled={formSubmitting}>
              {formSubmitting ? 'Saving...' : editingId ? 'Update Vendor' : 'Save Vendor'}
            </button>
          </>
        }
      >
        <form onSubmit={handleFormSubmit}>
          <div className="form-row">
            <div className="form-group">
              <label className="form-label">Vendor Code *</label>
              <input
                type="text"
                className="form-control"
                required
                value={formData.vendorCode}
                onChange={(e) => setFormData({ ...formData, vendorCode: e.target.value })}
                placeholder="e.g. VEND-001"
              />
            </div>
            <div className="form-group">
              <label className="form-label">Vendor Name *</label>
              <input
                type="text"
                className="form-control"
                required
                value={formData.name}
                onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                placeholder="Supplier Name / Dairy Farm"
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
                placeholder="Mobile number"
              />
            </div>
            <div className="form-group">
              <label className="form-label">Address</label>
              <input
                type="text"
                className="form-control"
                value={formData.address}
                onChange={(e) => setFormData({ ...formData, address: e.target.value })}
                placeholder="Village / Location"
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
                <option value="COW">Cow Milk</option>
                <option value="BUFFALO">Buffalo Milk</option>
                <option value="BOTH">Both</option>
              </select>
            </div>
            <div className="form-group">
              <label className="form-label">Collection Schedule</label>
              <select
                className="form-control"
                value={formData.collectionSchedule}
                onChange={(e) => setFormData({ ...formData, collectionSchedule: e.target.value })}
              >
                <option value="MORNING">Morning Only</option>
                <option value="NIGHT">Night Only</option>
                <option value="BOTH">Both (Morning & Night)</option>
              </select>
            </div>
          </div>

          <div style={{ marginTop: '0.75rem', marginBottom: '0.75rem' }}>
            <span style={{ fontSize: '0.85rem', fontWeight: 700, color: 'var(--text-primary)' }}>
              Procurement Default Quantities (Litres)
            </span>
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
              <label className="form-label">Cow Procurement Rate (₹/L) *</label>
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
              <label className="form-label">Buffalo Procurement Rate (₹/L) *</label>
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
              placeholder="Bank details or procurement terms..."
            />
          </div>
        </form>
      </Modal>

      {/* Deactivate Confirmation */}
      <ConfirmDialog
        isOpen={!!deactivateId}
        onClose={() => setDeactivateId(null)}
        onConfirm={handleDeactivate}
        title="Deactivate Vendor"
        message="Are you sure you want to deactivate this vendor? Historical collection records and bills will remain preserved."
        confirmText="Deactivate"
      />
    </div>
  );
}

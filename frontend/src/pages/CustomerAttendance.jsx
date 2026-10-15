import React, { useState, useEffect } from 'react';
import Header from '../components/Header';
import Badge from '../components/Badge';
import Toast from '../components/Toast';
import LoadingSpinner from '../components/LoadingSpinner';
import EmptyState from '../components/EmptyState';
import { attendanceService } from '../services/attendanceService';
import { customerService } from '../services/customerService';
import { formatQuantity } from '../utils/formatters';
import {
  Calendar,
  Save,
  Search,
  Check,
  X,
  Milk,
  CheckCheck,
  ChevronLeft,
  ChevronRight
} from 'lucide-react';

export default function CustomerAttendance() {
  const [selectedDate, setSelectedDate] = useState(new Date().toISOString().split('T')[0]);
  const [attendanceList, setAttendanceList] = useState([]);
  const [customerMap, setCustomerMap] = useState({});
  const [summary, setSummary] = useState(null);
  const [loading, setLoading] = useState(true);
  const [savingId, setSavingId] = useState(null);
  const [savingAll, setSavingAll] = useState(false);
  const [search, setSearch] = useState('');
  const [toast, setToast] = useState(null);

  const fetchAttendanceAndCustomers = async () => {
    try {
      setLoading(true);
      const [attData, custData, sumData] = await Promise.all([
        attendanceService.getCustomerAttendance(selectedDate),
        customerService.getAll(null, true),
        attendanceService.getCustomerSummary(selectedDate)
      ]);

      const cMap = {};
      custData.forEach((c) => {
        cMap[c.id] = c;
      });
      setCustomerMap(cMap);
      setAttendanceList(attData);
      setSummary(sumData);
    } catch (err) {
      setToast({ type: 'error', message: err.message || 'Failed to load attendance.' });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAttendanceAndCustomers();
  }, [selectedDate]);

  const handleDateChange = (daysDelta) => {
    const current = new Date(selectedDate);
    current.setDate(current.getDate() + daysDelta);
    setSelectedDate(current.toISOString().split('T')[0]);
  };

  const handleStatusChange = (index, newStatus) => {
    const updated = [...attendanceList];
    const item = { ...updated[index] };
    item.status = newStatus;

    if (newStatus === 'PRESENT') {
      // If was previously 0 or absent, pre-load customer default starting quantities as editable values
      const cust = customerMap[item.customerId];
      if (cust) {
        if (!item.morningCowQty && !item.morningBuffaloQty && !item.nightCowQty && !item.nightBuffaloQty) {
          item.morningCowQty = cust.morningCowQty || 0;
          item.morningBuffaloQty = cust.morningBuffaloQty || 0;
          item.nightCowQty = cust.nightCowQty || 0;
          item.nightBuffaloQty = cust.nightBuffaloQty || 0;
        }
      }
    } else {
      // Absent attendance contributes zero billable litres
      item.morningCowQty = 0;
      item.morningBuffaloQty = 0;
      item.nightCowQty = 0;
      item.nightBuffaloQty = 0;
    }

    item.totalLitres = (Number(item.morningCowQty) || 0) + (Number(item.morningBuffaloQty) || 0) +
                       (Number(item.nightCowQty) || 0) + (Number(item.nightBuffaloQty) || 0);

    updated[index] = item;
    setAttendanceList(updated);
  };

  const handleQuantityChange = (index, field, value) => {
    const updated = [...attendanceList];
    const item = { ...updated[index] };
    const num = Math.max(0, parseFloat(value) || 0);
    item[field] = num;

    if (item.status === 'ABSENT' && num > 0) {
      item.status = 'PRESENT';
    }

    item.totalLitres = (Number(item.morningCowQty) || 0) + (Number(item.morningBuffaloQty) || 0) +
                       (Number(item.nightCowQty) || 0) + (Number(item.nightBuffaloQty) || 0);

    updated[index] = item;
    setAttendanceList(updated);
  };

  const saveIndividual = async (item) => {
    try {
      setSavingId(item.customerId);
      const payload = {
        id: item.id,
        customerId: item.customerId,
        attendanceDate: selectedDate,
        status: item.status,
        morningCowQty: item.morningCowQty || 0,
        morningBuffaloQty: item.morningBuffaloQty || 0,
        nightCowQty: item.nightCowQty || 0,
        nightBuffaloQty: item.nightBuffaloQty || 0,
        notes: item.notes || ''
      };

      const saved = await attendanceService.saveCustomerAttendance(payload);
      setToast({ type: 'success', message: `Saved attendance for ${item.customerName}` });

      // Refresh daily summary
      const sum = await attendanceService.getCustomerSummary(selectedDate);
      setSummary(sum);

      // Update current item with returned ID
      setAttendanceList((prev) =>
        prev.map((it) => (it.customerId === item.customerId ? saved : it))
      );
    } catch (err) {
      setToast({ type: 'error', message: err.message || 'Failed to save attendance.' });
    } finally {
      setSavingId(null);
    }
  };

  const saveAll = async () => {
    try {
      setSavingAll(true);
      const payloadList = attendanceList.map((item) => ({
        id: item.id,
        customerId: item.customerId,
        attendanceDate: selectedDate,
        status: item.status,
        morningCowQty: item.morningCowQty || 0,
        morningBuffaloQty: item.morningBuffaloQty || 0,
        nightCowQty: item.nightCowQty || 0,
        nightBuffaloQty: item.nightBuffaloQty || 0,
        notes: item.notes || ''
      }));

      await attendanceService.saveBatchCustomerAttendance(payloadList);
      setToast({ type: 'success', message: 'All customer attendance saved successfully!' });
      fetchAttendanceAndCustomers();
    } catch (err) {
      setToast({ type: 'error', message: err.message || 'Failed to save batch attendance.' });
    } finally {
      setSavingAll(false);
    }
  };

  const filteredList = attendanceList.filter((item) => {
    if (!search.trim()) return true;
    const q = search.toLowerCase();
    return (
      (item.customerName && item.customerName.toLowerCase().includes(q)) ||
      (item.customerCode && item.customerCode.toLowerCase().includes(q))
    );
  });

  return (
    <div>
      <Header
        title="Customer Attendance"
        breadcrumb="M-Dairy • Daily Milk Delivery"
        actions={
          <button className="btn btn-primary" onClick={saveAll} disabled={savingAll || loading}>
            <Save size={18} />
            <span>{savingAll ? 'Saving All...' : 'Save All Records'}</span>
          </button>
        }
      />

      <div className="page-wrapper">
        {toast && <Toast {...toast} onClose={() => setToast(null)} />}

        {/* Date Selector & Controls Bar */}
        <div className="glass-card" style={{ marginBottom: '1.25rem', padding: '1rem 1.25rem' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '1rem' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.6rem' }}>
              <button className="btn btn-sm btn-secondary" onClick={() => handleDateChange(-1)}>
                <ChevronLeft size={16} /> Previous Day
              </button>

              <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', background: 'white', padding: '0.35rem 0.75rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-light)' }}>
                <Calendar size={18} color="#2563eb" />
                <input
                  type="date"
                  value={selectedDate}
                  onChange={(e) => setSelectedDate(e.target.value)}
                  style={{ border: 'none', outline: 'none', fontWeight: 600, color: 'var(--text-primary)', cursor: 'pointer' }}
                />
              </div>

              <button className="btn btn-sm btn-secondary" onClick={() => handleDateChange(1)}>
                Next Day <ChevronRight size={16} />
              </button>
            </div>

            <div style={{ flex: 1, maxWidth: '320px', position: 'relative' }}>
              <Search size={16} color="#94a3b8" style={{ position: 'absolute', left: '0.75rem', top: '50%', transform: 'translateY(-50%)' }} />
              <input
                type="text"
                className="form-control"
                placeholder="Filter by customer..."
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                style={{ paddingLeft: '2.2rem', paddingBlock: '0.4rem', fontSize: '0.85rem' }}
              />
            </div>
          </div>
        </div>

        {/* Daily Summary Glass Ribbon */}
        {summary && (
          <div className="glass-card" style={{ marginBottom: '1.25rem', padding: '1rem 1.25rem', background: 'rgba(255, 255, 255, 0.9)' }}>
            <div style={{ display: 'flex', flexWrap: 'wrap', gap: '1.5rem', justifyContent: 'space-around', alignItems: 'center' }}>
              <div>
                <span style={{ fontSize: '0.78rem', color: 'var(--text-muted)', fontWeight: 600, textTransform: 'uppercase' }}>Attendance</span>
                <div style={{ fontSize: '1.15rem', fontWeight: 800 }}>
                  <span style={{ color: '#059669' }}>{summary.totalPresent} Present</span>
                  <span style={{ color: '#94a3b8', margin: '0 0.4rem' }}>•</span>
                  <span style={{ color: '#dc2626' }}>{summary.totalAbsent} Absent</span>
                </div>
              </div>

              <div>
                <span style={{ fontSize: '0.78rem', color: 'var(--text-muted)', fontWeight: 600, textTransform: 'uppercase' }}>Morning Total</span>
                <div style={{ fontSize: '1.15rem', fontWeight: 800, color: '#2563eb' }}>
                  {formatQuantity((summary.morningCowQty || 0) + (summary.morningBuffaloQty || 0))}
                </div>
              </div>

              <div>
                <span style={{ fontSize: '0.78rem', color: 'var(--text-muted)', fontWeight: 600, textTransform: 'uppercase' }}>Night Total</span>
                <div style={{ fontSize: '1.15rem', fontWeight: 800, color: '#4f46e5' }}>
                  {formatQuantity((summary.nightCowQty || 0) + (summary.nightBuffaloQty || 0))}
                </div>
              </div>

              <div>
                <span style={{ fontSize: '0.78rem', color: 'var(--text-muted)', fontWeight: 600, textTransform: 'uppercase' }}>Total Day Delivery</span>
                <div style={{ fontSize: '1.3rem', fontWeight: 800, color: 'var(--text-primary)' }}>
                  {formatQuantity(summary.totalLitres || 0)}
                </div>
              </div>
            </div>
          </div>
        )}

        {/* Attendance Grid / Table */}
        <div className="glass-card" style={{ padding: 0 }}>
          {loading ? (
            <LoadingSpinner message="Loading customer attendance..." />
          ) : filteredList.length === 0 ? (
            <EmptyState
              title="No Customers Available"
              message="No active customers are available to take attendance for this date."
            />
          ) : (
            <div className="table-responsive">
              <table className="glass-table">
                <thead>
                  <tr>
                    <th>Customer</th>
                    <th>Status</th>
                    <th style={{ textAlign: 'center' }}>Morning Cow (L)</th>
                    <th style={{ textAlign: 'center' }}>Morning Buf (L)</th>
                    <th style={{ textAlign: 'center' }}>Night Cow (L)</th>
                    <th style={{ textAlign: 'center' }}>Night Buf (L)</th>
                    <th style={{ textAlign: 'center' }}>Total</th>
                    <th style={{ textAlign: 'right' }}>Save</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredList.map((item, index) => {
                    const isPresent = item.status === 'PRESENT';
                    return (
                      <tr key={item.customerId} style={{ background: isPresent ? 'rgba(255, 255, 255, 0.4)' : 'rgba(241, 245, 249, 0.4)' }}>
                        <td>
                          <div style={{ fontWeight: 700, color: 'var(--text-primary)' }}>{item.customerName}</div>
                          <div style={{ fontSize: '0.78rem', fontFamily: 'var(--font-mono)', color: 'var(--text-muted)' }}>
                            {item.customerCode}
                          </div>
                        </td>

                        <td>
                          <div style={{ display: 'inline-flex', borderRadius: 'var(--radius-sm)', overflow: 'hidden', border: '1px solid var(--border-light)' }}>
                            <button
                              type="button"
                              onClick={() => handleStatusChange(index, 'PRESENT')}
                              style={{
                                padding: '0.35rem 0.65rem',
                                fontSize: '0.75rem',
                                fontWeight: 700,
                                border: 'none',
                                cursor: 'pointer',
                                background: isPresent ? '#059669' : '#ffffff',
                                color: isPresent ? '#ffffff' : '#64748b',
                                transition: 'all 0.15s ease'
                              }}
                            >
                              Present
                            </button>
                            <button
                              type="button"
                              onClick={() => handleStatusChange(index, 'ABSENT')}
                              style={{
                                padding: '0.35rem 0.65rem',
                                fontSize: '0.75rem',
                                fontWeight: 700,
                                border: 'none',
                                cursor: 'pointer',
                                background: !isPresent ? '#dc2626' : '#ffffff',
                                color: !isPresent ? '#ffffff' : '#64748b',
                                transition: 'all 0.15s ease'
                              }}
                            >
                              Absent
                            </button>
                          </div>
                        </td>

                        <td style={{ textAlign: 'center' }}>
                          <input
                            type="number"
                            step="0.1"
                            min="0"
                            className="form-control"
                            style={{ width: '80px', margin: '0 auto', textAlign: 'center', padding: '0.35rem' }}
                            value={item.morningCowQty ?? 0}
                            disabled={!isPresent}
                            onChange={(e) => handleQuantityChange(index, 'morningCowQty', e.target.value)}
                          />
                        </td>

                        <td style={{ textAlign: 'center' }}>
                          <input
                            type="number"
                            step="0.1"
                            min="0"
                            className="form-control"
                            style={{ width: '80px', margin: '0 auto', textAlign: 'center', padding: '0.35rem' }}
                            value={item.morningBuffaloQty ?? 0}
                            disabled={!isPresent}
                            onChange={(e) => handleQuantityChange(index, 'morningBuffaloQty', e.target.value)}
                          />
                        </td>

                        <td style={{ textAlign: 'center' }}>
                          <input
                            type="number"
                            step="0.1"
                            min="0"
                            className="form-control"
                            style={{ width: '80px', margin: '0 auto', textAlign: 'center', padding: '0.35rem' }}
                            value={item.nightCowQty ?? 0}
                            disabled={!isPresent}
                            onChange={(e) => handleQuantityChange(index, 'nightCowQty', e.target.value)}
                          />
                        </td>

                        <td style={{ textAlign: 'center' }}>
                          <input
                            type="number"
                            step="0.1"
                            min="0"
                            className="form-control"
                            style={{ width: '80px', margin: '0 auto', textAlign: 'center', padding: '0.35rem' }}
                            value={item.nightBuffaloQty ?? 0}
                            disabled={!isPresent}
                            onChange={(e) => handleQuantityChange(index, 'nightBuffaloQty', e.target.value)}
                          />
                        </td>

                        <td style={{ textAlign: 'center', fontWeight: 700, color: isPresent ? 'var(--text-primary)' : 'var(--text-muted)' }}>
                          {formatQuantity(item.totalLitres || 0)}
                        </td>

                        <td style={{ textAlign: 'right' }}>
                          <button
                            className="btn btn-sm btn-secondary"
                            onClick={() => saveIndividual(item)}
                            disabled={savingId === item.customerId}
                          >
                            <Save size={14} />
                            <span>{savingId === item.customerId ? '...' : 'Save'}</span>
                          </button>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

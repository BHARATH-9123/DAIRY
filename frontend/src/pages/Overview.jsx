import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Header from '../components/Header';
import StatCard from '../components/StatCard';
import LoadingSpinner from '../components/LoadingSpinner';
import Toast from '../components/Toast';
import { dashboardService } from '../services/dashboardService';
import { formatCurrency, formatQuantity } from '../utils/formatters';
import {
  Users,
  Building2,
  Milk,
  Receipt,
  AlertCircle,
  PlusCircle,
  CalendarCheck,
  TrendingUp,
  CreditCard,
  ArrowRight,
  Clock
} from 'lucide-react';

export default function Overview() {
  const navigate = useNavigate();
  const [summary, setSummary] = useState(null);
  const [loading, setLoading] = useState(true);
  const [toast, setToast] = useState(null);

  const fetchSummary = async () => {
    try {
      setLoading(true);
      const data = await dashboardService.getSummary();
      setSummary(data);
    } catch (err) {
      setToast({ type: 'error', message: err.message || 'Failed to load dashboard data.' });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSummary();
  }, []);

  return (
    <div>
      <Header title="Dairy Overview" breadcrumb="M-Dairy • Live Dashboard" />

      <div className="page-wrapper">
        {toast && <Toast {...toast} onClose={() => setToast(null)} />}

        {loading ? (
          <LoadingSpinner message="Fetching live dairy stats..." />
        ) : (
          <>
            {/* KPI Summary Cards */}
            <div className="kpi-grid">
              <StatCard
                label="Active Customers"
                value={summary?.totalActiveCustomers ?? 0}
                icon={Users}
                color="#2563eb"
                bg="rgba(37, 99, 235, 0.1)"
                subtext="Registered households"
              />
              <StatCard
                label="Active Vendors"
                value={summary?.totalActiveVendors ?? 0}
                icon={Building2}
                color="#059669"
                bg="rgba(5, 150, 105, 0.1)"
                subtext="Milk procurement partners"
              />
              <StatCard
                label="Today's Milk Delivery"
                value={formatQuantity(summary?.todayCustomerMilkDelivery ?? 0)}
                icon={Milk}
                color="#0284c7"
                bg="rgba(2, 132, 199, 0.1)"
                subtext="Delivered to customers"
              />
              <StatCard
                label="Today's Milk Collection"
                value={formatQuantity(summary?.todayVendorMilkCollection ?? 0)}
                icon={Milk}
                color="#4f46e5"
                bg="rgba(79, 70, 229, 0.1)"
                subtext="Collected from vendors"
              />
              <StatCard
                label="Current Month Customer Bill"
                value={formatCurrency(summary?.currentMonthCustomerBilling ?? 0)}
                icon={TrendingUp}
                color="#0891b2"
                bg="rgba(8, 145, 178, 0.1)"
                subtext="Revenue generated"
              />
              <StatCard
                label="Current Month Vendor Bill"
                value={formatCurrency(summary?.currentMonthVendorBilling ?? 0)}
                icon={Receipt}
                color="#7c3aed"
                bg="rgba(124, 58, 237, 0.1)"
                subtext="Procurement payables"
              />
              <StatCard
                label="Customer Outstanding"
                value={formatCurrency(summary?.outstandingCustomerPayments ?? 0)}
                icon={AlertCircle}
                color="#d97706"
                bg="rgba(217, 119, 6, 0.1)"
                subtext="Receivables pending"
              />
              <StatCard
                label="Vendor Outstanding"
                value={formatCurrency(summary?.outstandingVendorPayments ?? 0)}
                icon={CreditCard}
                color="#dc2626"
                bg="rgba(220, 38, 38, 0.1)"
                subtext="Vendor payments due"
              />
            </div>

            {/* Quick Actions & Recent Activity Grid */}
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(360px, 1fr))', gap: '1.5rem', marginTop: '1rem' }}>
              
              {/* Quick Actions Card */}
              <div className="glass-card">
                <div className="card-header">
                  <h3 className="card-title">Quick Actions</h3>
                </div>

                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(150px, 1fr))', gap: '0.85rem' }}>
                  <button
                    className="btn btn-secondary"
                    style={{ justifyContent: 'flex-start', padding: '0.85rem 1rem' }}
                    onClick={() => navigate('/customers?action=add')}
                  >
                    <PlusCircle size={18} color="#2563eb" />
                    <span>Add Customer</span>
                  </button>

                  <button
                    className="btn btn-secondary"
                    style={{ justifyContent: 'flex-start', padding: '0.85rem 1rem' }}
                    onClick={() => navigate('/vendors?action=add')}
                  >
                    <PlusCircle size={18} color="#059669" />
                    <span>Add Vendor</span>
                  </button>

                  <button
                    className="btn btn-secondary"
                    style={{ justifyContent: 'flex-start', padding: '0.85rem 1rem' }}
                    onClick={() => navigate('/customer-attendance')}
                  >
                    <CalendarCheck size={18} color="#0284c7" />
                    <span>Customer Attendance</span>
                  </button>

                  <button
                    className="btn btn-secondary"
                    style={{ justifyContent: 'flex-start', padding: '0.85rem 1rem' }}
                    onClick={() => navigate('/vendor-attendance')}
                  >
                    <CalendarCheck size={18} color="#4f46e5" />
                    <span>Vendor Attendance</span>
                  </button>

                  <button
                    className="btn btn-secondary"
                    style={{ justifyContent: 'flex-start', padding: '0.85rem 1rem' }}
                    onClick={() => navigate('/customer-billing')}
                  >
                    <Receipt size={18} color="#0891b2" />
                    <span>Customer Bill</span>
                  </button>

                  <button
                    className="btn btn-secondary"
                    style={{ justifyContent: 'flex-start', padding: '0.85rem 1rem' }}
                    onClick={() => navigate('/vendor-billing')}
                  >
                    <Receipt size={18} color="#7c3aed" />
                    <span>Vendor Bill</span>
                  </button>
                </div>
              </div>

              {/* Recent Activity Card */}
              <div className="glass-card">
                <div className="card-header">
                  <h3 className="card-title">Recent Transactions</h3>
                  <button className="btn btn-sm btn-secondary" onClick={fetchSummary}>Refresh</button>
                </div>

                {(!summary?.recentActivity || summary.recentActivity.length === 0) ? (
                  <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem', padding: '1rem 0' }}>
                    No recent bills or payments recorded yet.
                  </p>
                ) : (
                  <div style={{ display: 'flex', flexDirection: 'column', gap: '0.85rem' }}>
                    {summary.recentActivity.map((item) => (
                      <div
                        key={item.id}
                        style={{
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'space-between',
                          padding: '0.75rem',
                          borderRadius: 'var(--radius-md)',
                          background: 'rgba(255, 255, 255, 0.5)',
                          border: '1px solid rgba(226, 232, 240, 0.6)'
                        }}
                      >
                        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                          <div style={{
                            width: '36px',
                            height: '36px',
                            borderRadius: 'var(--radius-sm)',
                            background: item.type === 'BILL' ? 'rgba(37, 99, 235, 0.1)' : 'rgba(5, 150, 105, 0.1)',
                            color: item.type === 'BILL' ? '#2563eb' : '#059669',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center'
                          }}>
                            {item.type === 'BILL' ? <Receipt size={18} /> : <CreditCard size={18} />}
                          </div>
                          <div>
                            <p style={{ fontSize: '0.9rem', fontWeight: 600, color: 'var(--text-primary)' }}>{item.title}</p>
                            <p style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>{item.description}</p>
                          </div>
                        </div>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '0.3rem', color: 'var(--text-muted)', fontSize: '0.75rem' }}>
                          <Clock size={12} />
                          <span>{new Date(item.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</span>
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </div>

            </div>
          </>
        )}
      </div>
    </div>
  );
}

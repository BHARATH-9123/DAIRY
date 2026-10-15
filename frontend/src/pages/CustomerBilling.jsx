import React, { useState, useEffect } from 'react';
import Header from '../components/Header';
import Modal from '../components/Modal';
import Badge from '../components/Badge';
import Toast from '../components/Toast';
import LoadingSpinner from '../components/LoadingSpinner';
import EmptyState from '../components/EmptyState';
import { billingService } from '../services/billingService';
import { customerService } from '../services/customerService';
import { paymentService } from '../services/paymentService';
import { formatCurrency, formatQuantity, formatDate } from '../utils/formatters';
import {
  Receipt,
  FileText,
  PlusCircle,
  RefreshCw,
  Printer,
  CreditCard,
  Trash2,
  Filter,
  CheckCircle,
  AlertCircle,
  Calendar
} from 'lucide-react';

const currentYear = new Date().getFullYear();
const currentMonth = new Date().getMonth() + 1;

export default function CustomerBilling() {
  const [bills, setBills] = useState([]);
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [toast, setToast] = useState(null);

  // Filters
  const [selectedCustomer, setSelectedCustomer] = useState('');
  const [selectedMonth, setSelectedMonth] = useState(currentMonth);
  const [selectedYear, setSelectedYear] = useState(currentYear);
  const [selectedStatus, setSelectedStatus] = useState('');

  // Generation Modal
  const [isGenerateModalOpen, setIsGenerateModalOpen] = useState(false);
  const [genCustomerId, setGenCustomerId] = useState('');
  const [genMonth, setGenMonth] = useState(currentMonth);
  const [genYear, setGenYear] = useState(currentYear);
  const [generating, setGenerating] = useState(false);

  // Invoice Details Modal
  const [activeInvoice, setActiveInvoice] = useState(null);
  const [loadingDetails, setLoadingDetails] = useState(false);

  // Payment Recording Modal
  const [paymentModalOpen, setPaymentModalOpen] = useState(false);
  const [paymentBill, setPaymentBill] = useState(null);
  const [paymentAmount, setPaymentAmount] = useState('');
  const [paymentMethod, setPaymentMethod] = useState('CASH');
  const [paymentRef, setPaymentRef] = useState('');
  const [paymentDate, setPaymentDate] = useState(new Date().toISOString().split('T')[0]);
  const [submittingPayment, setSubmittingPayment] = useState(false);

  const fetchCustomers = async () => {
    try {
      const data = await customerService.getAll(null, null);
      setCustomers(data);
    } catch (err) {
      console.error(err);
    }
  };

  const fetchBills = async () => {
    try {
      setLoading(true);
      const data = await billingService.filterCustomerBills(
        selectedCustomer || null,
        selectedMonth || null,
        selectedYear || null,
        selectedStatus || null
      );
      setBills(data);
    } catch (err) {
      setToast({ type: 'error', message: err.message || 'Failed to load bills.' });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCustomers();
  }, []);

  useEffect(() => {
    fetchBills();
  }, [selectedCustomer, selectedMonth, selectedYear, selectedStatus]);

  const handleGenerateBill = async (recalculate = false) => {
    const custId = genCustomerId || selectedCustomer;
    if (!custId) {
      setToast({ type: 'error', message: 'Please select a customer.' });
      return;
    }

    try {
      setGenerating(true);
      const bill = await billingService.generateCustomerBill(
        custId,
        genMonth,
        genYear,
        recalculate
      );
      setToast({
        type: 'success',
        message: `Invoice ${bill.billNumber} generated successfully! Total: ${formatCurrency(bill.totalAmount)}`
      });
      setIsGenerateModalOpen(false);
      fetchBills();
    } catch (err) {
      setToast({ type: 'error', message: err.message || 'Failed to generate bill.' });
    } finally {
      setGenerating(false);
    }
  };

  const handleRecalculateDraft = async (bill) => {
    try {
      const updated = await billingService.generateCustomerBill(
        bill.customerId,
        bill.billingMonth,
        bill.billingYear,
        true
      );
      setToast({
        type: 'success',
        message: `Draft invoice ${updated.billNumber} recalculated with latest attendance! Amount: ${formatCurrency(updated.totalAmount)}`
      });
      fetchBills();
    } catch (err) {
      setToast({ type: 'error', message: err.message || 'Recalculation failed.' });
    }
  };

  const handleIssueInvoice = async (billId) => {
    try {
      await billingService.updateCustomerBillStatus(billId, 'ISSUED');
      setToast({ type: 'success', message: 'Invoice issued to customer.' });
      fetchBills();
    } catch (err) {
      setToast({ type: 'error', message: err.message || 'Failed to update status.' });
    }
  };

  const openViewInvoice = async (id) => {
    try {
      setLoadingDetails(true);
      const detailed = await billingService.getCustomerBillById(id);
      setActiveInvoice(detailed);
    } catch (err) {
      setToast({ type: 'error', message: err.message || 'Failed to load invoice details.' });
    } finally {
      setLoadingDetails(false);
    }
  };

  const openPaymentModal = (bill) => {
    setPaymentBill(bill);
    setPaymentAmount(bill.balanceAmount > 0 ? bill.balanceAmount : '');
    setPaymentMethod('CASH');
    setPaymentRef('');
    setPaymentDate(new Date().toISOString().split('T')[0]);
    setPaymentModalOpen(true);
  };

  const handleRecordPayment = async (e) => {
    e.preventDefault();
    if (!paymentAmount || parseFloat(paymentAmount) <= 0) {
      setToast({ type: 'error', message: 'Enter a valid payment amount.' });
      return;
    }

    try {
      setSubmittingPayment(true);
      await paymentService.recordCustomerPayment({
        partyId: paymentBill.customerId,
        billId: paymentBill.id,
        paymentDate,
        amount: parseFloat(paymentAmount),
        paymentMethod,
        referenceNumber: paymentRef,
        notes: `Payment towards bill ${paymentBill.billNumber}`
      });
      setToast({ type: 'success', message: `Payment of ${formatCurrency(paymentAmount)} recorded successfully!` });
      setPaymentModalOpen(false);
      fetchBills();
    } catch (err) {
      setToast({ type: 'error', message: err.message || 'Payment recording failed.' });
    } finally {
      setSubmittingPayment(false);
    }
  };

  return (
    <div>
      <Header
        title="Customer Billing"
        breadcrumb="M-Dairy • Invoices & Payments"
        actions={
          <button className="btn btn-primary" onClick={() => setIsGenerateModalOpen(true)}>
            <PlusCircle size={18} />
            <span>Generate Bill</span>
          </button>
        }
      />

      <div className="page-wrapper">
        {toast && <Toast {...toast} onClose={() => setToast(null)} />}

        {/* Filters */}
        <div className="glass-card" style={{ marginBottom: '1.25rem', padding: '1rem 1.25rem' }}>
          <div style={{ display: 'flex', gap: '1rem', flexWrap: 'wrap', alignItems: 'center' }}>
            <div style={{ flex: 1, minWidth: '200px' }}>
              <label className="form-label" style={{ fontSize: '0.75rem' }}>Customer</label>
              <select
                className="form-control"
                value={selectedCustomer}
                onChange={(e) => setSelectedCustomer(e.target.value)}
              >
                <option value="">All Customers</option>
                {customers.map((c) => (
                  <option key={c.id} value={c.id}>{c.customerCode} - {c.name}</option>
                ))}
              </select>
            </div>

            <div style={{ width: '130px' }}>
              <label className="form-label" style={{ fontSize: '0.75rem' }}>Month</label>
              <select
                className="form-control"
                value={selectedMonth}
                onChange={(e) => setSelectedMonth(Number(e.target.value))}
              >
                {Array.from({ length: 12 }, (_, i) => i + 1).map((m) => (
                  <option key={m} value={m}>
                    {new Date(2000, m - 1).toLocaleString('default', { month: 'short' })}
                  </option>
                ))}
              </select>
            </div>

            <div style={{ width: '110px' }}>
              <label className="form-label" style={{ fontSize: '0.75rem' }}>Year</label>
              <select
                className="form-control"
                value={selectedYear}
                onChange={(e) => setSelectedYear(Number(e.target.value))}
              >
                {[currentYear - 1, currentYear, currentYear + 1].map((y) => (
                  <option key={y} value={y}>{y}</option>
                ))}
              </select>
            </div>

            <div style={{ width: '150px' }}>
              <label className="form-label" style={{ fontSize: '0.75rem' }}>Status</label>
              <select
                className="form-control"
                value={selectedStatus}
                onChange={(e) => setSelectedStatus(e.target.value)}
              >
                <option value="">All Statuses</option>
                <option value="DRAFT">Draft</option>
                <option value="ISSUED">Issued</option>
                <option value="PARTIALLY_PAID">Partially Paid</option>
                <option value="PAID">Paid</option>
              </select>
            </div>

            <div style={{ marginTop: '1.25rem' }}>
              <button className="btn btn-secondary" onClick={fetchBills}>
                <Filter size={15} /> Apply
              </button>
            </div>
          </div>
        </div>

        {/* Bills Table */}
        <div className="glass-card" style={{ padding: 0 }}>
          {loading ? (
            <LoadingSpinner message="Loading customer bills..." />
          ) : bills.length === 0 ? (
            <EmptyState
              title="No Invoices Found"
              message="No customer bills have been generated for the selected period."
              action={
                <button className="btn btn-primary" onClick={() => setIsGenerateModalOpen(true)}>
                  <PlusCircle size={16} />
                  <span>Generate Bill</span>
                </button>
              }
            />
          ) : (
            <div className="table-responsive">
              <table className="glass-table">
                <thead>
                  <tr>
                    <th>Invoice No</th>
                    <th>Customer</th>
                    <th>Period</th>
                    <th style={{ textAlign: 'center' }}>Cow (L)</th>
                    <th style={{ textAlign: 'center' }}>Buf (L)</th>
                    <th style={{ textAlign: 'center' }}>Total (L)</th>
                    <th style={{ textAlign: 'right' }}>Total Bill</th>
                    <th style={{ textAlign: 'right' }}>Paid</th>
                    <th style={{ textAlign: 'right' }}>Balance</th>
                    <th style={{ textAlign: 'center' }}>Status</th>
                    <th style={{ textAlign: 'right' }}>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {bills.map((bill) => (
                    <tr key={bill.id}>
                      <td style={{ fontFamily: 'var(--font-mono)', fontWeight: 700 }}>{bill.billNumber}</td>
                      <td>
                        <div style={{ fontWeight: 600 }}>{bill.customerName}</div>
                        <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{bill.customerCode}</div>
                      </td>
                      <td>
                        <span style={{ fontSize: '0.85rem', fontWeight: 600 }}>
                          {String(bill.billingMonth).padStart(2, '0')}/{bill.billingYear}
                        </span>
                      </td>
                      <td style={{ textAlign: 'center' }}>{formatQuantity(bill.cowLitres)}</td>
                      <td style={{ textAlign: 'center' }}>{formatQuantity(bill.buffaloLitres)}</td>
                      <td style={{ textAlign: 'center', fontWeight: 700 }}>{formatQuantity(bill.totalLitres)}</td>
                      <td style={{ textAlign: 'right', fontWeight: 700, color: '#2563eb' }}>
                        {formatCurrency(bill.totalAmount)}
                      </td>
                      <td style={{ textAlign: 'right', color: '#059669', fontWeight: 600 }}>
                        {formatCurrency(bill.paidAmount)}
                      </td>
                      <td style={{ textAlign: 'right', color: bill.balanceAmount > 0 ? '#dc2626' : '#059669', fontWeight: 800 }}>
                        {formatCurrency(bill.balanceAmount)}
                      </td>
                      <td style={{ textAlign: 'center' }}>
                        <Badge status={bill.status} />
                      </td>
                      <td style={{ textAlign: 'right' }}>
                        <div style={{ display: 'inline-flex', gap: '0.4rem' }}>
                          <button
                            className="btn btn-sm btn-secondary"
                            title="View Invoice Details"
                            onClick={() => openViewInvoice(bill.id)}
                          >
                            <FileText size={14} />
                          </button>

                          {bill.status === 'DRAFT' && (
                            <>
                              <button
                                className="btn btn-sm btn-secondary"
                                title="Recalculate with updated attendance"
                                onClick={() => handleRecalculateDraft(bill)}
                              >
                                <RefreshCw size={14} color="#2563eb" />
                              </button>
                              <button
                                className="btn btn-sm btn-primary"
                                title="Issue Invoice"
                                onClick={() => handleIssueInvoice(bill.id)}
                              >
                                Issue
                              </button>
                            </>
                          )}

                          {bill.balanceAmount > 0 && (
                            <button
                              className="btn btn-sm btn-secondary"
                              title="Record Payment"
                              style={{ color: '#059669' }}
                              onClick={() => openPaymentModal(bill)}
                            >
                              <CreditCard size={14} />
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

      {/* Generate Bill Modal */}
      <Modal
        isOpen={isGenerateModalOpen}
        onClose={() => setIsGenerateModalOpen(false)}
        title="Generate Customer Monthly Bill"
        footer={
          <>
            <button className="btn btn-secondary" onClick={() => setIsGenerateModalOpen(false)} disabled={generating}>
              Cancel
            </button>
            <button className="btn btn-primary" onClick={() => handleGenerateBill(false)} disabled={generating}>
              {generating ? 'Calculating...' : 'Generate Invoice'}
            </button>
          </>
        }
      >
        <div className="form-group">
          <label className="form-label">Select Customer *</label>
          <select
            className="form-control"
            value={genCustomerId}
            onChange={(e) => setGenCustomerId(e.target.value)}
            required
          >
            <option value="">-- Choose Customer --</option>
            {customers.map((c) => (
              <option key={c.id} value={c.id}>
                {c.customerCode} - {c.name} ({c.milkType})
              </option>
            ))}
          </select>
        </div>

        <div className="form-row">
          <div className="form-group">
            <label className="form-label">Billing Month</label>
            <select
              className="form-control"
              value={genMonth}
              onChange={(e) => setGenMonth(Number(e.target.value))}
            >
              {Array.from({ length: 12 }, (_, i) => i + 1).map((m) => (
                <option key={m} value={m}>
                  {new Date(2000, m - 1).toLocaleString('default', { month: 'long' })}
                </option>
              ))}
            </select>
          </div>

          <div className="form-group">
            <label className="form-label">Billing Year</label>
            <select
              className="form-control"
              value={genYear}
              onChange={(e) => setGenYear(Number(e.target.value))}
            >
              {[currentYear - 1, currentYear, currentYear + 1].map((y) => (
                <option key={y} value={y}>{y}</option>
              ))}
            </select>
          </div>
        </div>

        <div style={{ background: 'rgba(37, 99, 235, 0.06)', padding: '0.85rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--primary-border)', marginTop: '0.5rem' }}>
          <p style={{ fontSize: '0.82rem', color: 'var(--text-secondary)' }}>
            <strong>Calculation Engine:</strong> Sums up all saved attendance with status <code>PRESENT</code> for the chosen month. Multiplies actual cow litres with cow selling rate and buffalo litres with buffalo selling rate. Default customer quantities without saved attendance are strictly excluded.
          </p>
        </div>
      </Modal>

      {/* Invoice Details / Print Preview Modal */}
      <Modal
        isOpen={!!activeInvoice}
        onClose={() => setActiveInvoice(null)}
        title={activeInvoice ? `Invoice: ${activeInvoice.billNumber}` : 'Invoice Details'}
        maxWidth="800px"
        footer={
          <>
            <button className="btn btn-secondary no-print" onClick={() => setActiveInvoice(null)}>
              Close
            </button>
            <button className="btn btn-primary no-print" onClick={() => window.print()}>
              <Printer size={16} />
              <span>Print Invoice</span>
            </button>
          </>
        }
      >
        {activeInvoice && (
          <div className="invoice-preview" style={{ padding: '0.5rem' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '2px solid var(--border-light)', paddingBottom: '1rem', marginBottom: '1.25rem' }}>
              <div>
                <h2 style={{ fontSize: '1.4rem', fontWeight: 800, color: 'var(--text-primary)' }}>M-DAIRY</h2>
                <p style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>From Farm To Home</p>
                <p style={{ fontSize: '0.82rem', color: 'var(--text-secondary)', marginTop: '0.25rem' }}>
                  Anand, Gujarat • Phone: +91 98765 43210
                </p>
              </div>
              <div style={{ textAlign: 'right' }}>
                <span style={{ fontSize: '0.85rem', fontWeight: 700, color: '#2563eb' }}>CUSTOMER INVOICE</span>
                <h3 style={{ fontSize: '1.1rem', fontWeight: 700, fontFamily: 'var(--font-mono)' }}>{activeInvoice.billNumber}</h3>
                <p style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>
                  Period: <strong>{String(activeInvoice.billingMonth).padStart(2, '0')}/{activeInvoice.billingYear}</strong>
                </p>
                <div style={{ marginTop: '0.35rem' }}>
                  <Badge status={activeInvoice.status} />
                </div>
              </div>
            </div>

            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '1.25rem' }}>
              <div>
                <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', textTransform: 'uppercase' }}>Billed To:</span>
                <h4 style={{ fontSize: '1.05rem', fontWeight: 700 }}>{activeInvoice.customerName}</h4>
                <p style={{ fontSize: '0.85rem', color: 'var(--text-secondary)' }}>Code: {activeInvoice.customerCode}</p>
                {activeInvoice.customerPhone && <p style={{ fontSize: '0.85rem', color: 'var(--text-secondary)' }}>Phone: {activeInvoice.customerPhone}</p>}
                {activeInvoice.customerAddress && <p style={{ fontSize: '0.85rem', color: 'var(--text-secondary)' }}>{activeInvoice.customerAddress}</p>}
              </div>

              <div style={{ textAlign: 'right' }}>
                <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', textTransform: 'uppercase' }}>Rate Card:</span>
                <p style={{ fontSize: '0.85rem' }}>Cow Rate: <strong>{formatCurrency(activeInvoice.cowRate)}</strong>/L</p>
                <p style={{ fontSize: '0.85rem' }}>Buffalo Rate: <strong>{formatCurrency(activeInvoice.buffaloRate)}</strong>/L</p>
              </div>
            </div>

            {/* Daily Itemized Breakdown */}
            <h5 style={{ fontSize: '0.9rem', fontWeight: 700, marginBottom: '0.5rem' }}>Itemized Attendance Breakdown</h5>
            <div className="table-responsive" style={{ maxHeight: '240px', overflowY: 'auto', marginBottom: '1.25rem' }}>
              <table className="glass-table" style={{ fontSize: '0.82rem' }}>
                <thead>
                  <tr>
                    <th>Date</th>
                    <th style={{ textAlign: 'center' }}>M-Cow</th>
                    <th style={{ textAlign: 'center' }}>M-Buf</th>
                    <th style={{ textAlign: 'center' }}>N-Cow</th>
                    <th style={{ textAlign: 'center' }}>N-Buf</th>
                    <th style={{ textAlign: 'center' }}>Total Litres</th>
                    <th style={{ textAlign: 'right' }}>Daily Amount</th>
                  </tr>
                </thead>
                <tbody>
                  {activeInvoice.items && activeInvoice.items.length > 0 ? (
                    activeInvoice.items.map((it) => (
                      <tr key={it.id || it.attendanceDate}>
                        <td>{formatDate(it.attendanceDate)}</td>
                        <td style={{ textAlign: 'center' }}>{formatQuantity(it.morningCowQty)}</td>
                        <td style={{ textAlign: 'center' }}>{formatQuantity(it.morningBuffaloQty)}</td>
                        <td style={{ textAlign: 'center' }}>{formatQuantity(it.nightCowQty)}</td>
                        <td style={{ textAlign: 'center' }}>{formatQuantity(it.nightBuffaloQty)}</td>
                        <td style={{ textAlign: 'center', fontWeight: 700 }}>{formatQuantity(it.dailyTotalLitres)}</td>
                        <td style={{ textAlign: 'right', fontWeight: 600 }}>{formatCurrency(it.dailyAmount)}</td>
                      </tr>
                    ))
                  ) : (
                    <tr>
                      <td colSpan={7} style={{ textAlign: 'center', color: 'var(--text-muted)' }}>
                        No present attendance days found for this period.
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>

            {/* Invoice Totals Table */}
            <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
              <div style={{ width: '280px', background: 'rgba(248, 250, 252, 0.8)', padding: '0.85rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-light)' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.4rem', fontSize: '0.85rem' }}>
                  <span>Cow Milk ({formatQuantity(activeInvoice.cowLitres)}):</span>
                  <strong>{formatCurrency(activeInvoice.cowAmount)}</strong>
                </div>
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.4rem', fontSize: '0.85rem' }}>
                  <span>Buffalo Milk ({formatQuantity(activeInvoice.buffaloLitres)}):</span>
                  <strong>{formatCurrency(activeInvoice.buffaloAmount)}</strong>
                </div>
                <div style={{ display: 'flex', justifyContent: 'space-between', borderTop: '1px solid var(--border-light)', paddingTop: '0.4rem', marginBottom: '0.4rem', fontSize: '1rem', fontWeight: 800 }}>
                  <span>Total Bill:</span>
                  <span style={{ color: '#2563eb' }}>{formatCurrency(activeInvoice.totalAmount)}</span>
                </div>
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.4rem', fontSize: '0.85rem', color: '#059669' }}>
                  <span>Paid:</span>
                  <strong>{formatCurrency(activeInvoice.paidAmount)}</strong>
                </div>
                <div style={{ display: 'flex', justifyContent: 'space-between', borderTop: '1px solid var(--border-light)', paddingTop: '0.4rem', fontSize: '1rem', fontWeight: 800, color: activeInvoice.balanceAmount > 0 ? '#dc2626' : '#059669' }}>
                  <span>Balance Due:</span>
                  <span>{formatCurrency(activeInvoice.balanceAmount)}</span>
                </div>
              </div>
            </div>
          </div>
        )}
      </Modal>

      {/* Record Payment Modal */}
      <Modal
        isOpen={paymentModalOpen}
        onClose={() => setPaymentModalOpen(false)}
        title={paymentBill ? `Record Payment • ${paymentBill.billNumber}` : 'Record Payment'}
        footer={
          <>
            <button className="btn btn-secondary" onClick={() => setPaymentModalOpen(false)} disabled={submittingPayment}>
              Cancel
            </button>
            <button className="btn btn-primary" onClick={handleRecordPayment} disabled={submittingPayment}>
              {submittingPayment ? 'Recording...' : 'Record Payment'}
            </button>
          </>
        }
      >
        <form onSubmit={handleRecordPayment}>
          <div style={{ background: 'rgba(5, 150, 105, 0.08)', padding: '0.85rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--success-border)', marginBottom: '1rem' }}>
            <p style={{ fontSize: '0.85rem', color: 'var(--text-secondary)' }}>
              Outstanding Balance: <strong style={{ color: '#dc2626', fontSize: '1.05rem' }}>{formatCurrency(paymentBill?.balanceAmount)}</strong>
            </p>
          </div>

          <div className="form-group">
            <label className="form-label">Payment Amount (₹) *</label>
            <input
              type="number"
              step="0.01"
              min="0.01"
              max={paymentBill?.balanceAmount}
              className="form-control"
              required
              value={paymentAmount}
              onChange={(e) => setPaymentAmount(e.target.value)}
            />
          </div>

          <div className="form-row">
            <div className="form-group">
              <label className="form-label">Payment Date *</label>
              <input
                type="date"
                className="form-control"
                required
                value={paymentDate}
                onChange={(e) => setPaymentDate(e.target.value)}
              />
            </div>

            <div className="form-group">
              <label className="form-label">Payment Method *</label>
              <select
                className="form-control"
                value={paymentMethod}
                onChange={(e) => setPaymentMethod(e.target.value)}
              >
                <option value="CASH">Cash</option>
                <option value="UPI">UPI / QR Code</option>
                <option value="BANK_TRANSFER">Bank Transfer</option>
                <option value="CHEQUE">Cheque</option>
              </select>
            </div>
          </div>

          <div className="form-group">
            <label className="form-label">Reference Number (Optional)</label>
            <input
              type="text"
              className="form-control"
              value={paymentRef}
              onChange={(e) => setPaymentRef(e.target.value)}
              placeholder="e.g. UPI Ref / Cheque No"
            />
          </div>
        </form>
      </Modal>
    </div>
  );
}

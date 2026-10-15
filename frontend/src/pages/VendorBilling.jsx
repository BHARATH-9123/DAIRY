import React, { useState, useEffect } from 'react';
import Header from '../components/Header';
import Modal from '../components/Modal';
import Badge from '../components/Badge';
import Toast from '../components/Toast';
import LoadingSpinner from '../components/LoadingSpinner';
import EmptyState from '../components/EmptyState';
import { billingService } from '../services/billingService';
import { vendorService } from '../services/vendorService';
import { paymentService } from '../services/paymentService';
import { formatCurrency, formatQuantity, formatDate } from '../utils/formatters';
import {
  FileSpreadsheet,
  FileText,
  PlusCircle,
  RefreshCw,
  Printer,
  CreditCard,
  Filter
} from 'lucide-react';

const currentYear = new Date().getFullYear();
const currentMonth = new Date().getMonth() + 1;

export default function VendorBilling() {
  const [bills, setBills] = useState([]);
  const [vendors, setVendors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [toast, setToast] = useState(null);

  // Filters
  const [selectedVendor, setSelectedVendor] = useState('');
  const [selectedMonth, setSelectedMonth] = useState(currentMonth);
  const [selectedYear, setSelectedYear] = useState(currentYear);
  const [selectedStatus, setSelectedStatus] = useState('');

  // Generation Modal
  const [isGenerateModalOpen, setIsGenerateModalOpen] = useState(false);
  const [genVendorId, setGenVendorId] = useState('');
  const [genMonth, setGenMonth] = useState(currentMonth);
  const [genYear, setGenYear] = useState(currentYear);
  const [generating, setGenerating] = useState(false);

  // Invoice Details Modal
  const [activeInvoice, setActiveInvoice] = useState(null);

  // Payment Modal
  const [paymentModalOpen, setPaymentModalOpen] = useState(false);
  const [paymentBill, setPaymentBill] = useState(null);
  const [paymentAmount, setPaymentAmount] = useState('');
  const [paymentMethod, setPaymentMethod] = useState('BANK_TRANSFER');
  const [paymentRef, setPaymentRef] = useState('');
  const [paymentDate, setPaymentDate] = useState(new Date().toISOString().split('T')[0]);
  const [submittingPayment, setSubmittingPayment] = useState(false);

  const fetchVendors = async () => {
    try {
      const data = await vendorService.getAll(null, null);
      setVendors(data);
    } catch (err) {
      console.error(err);
    }
  };

  const fetchBills = async () => {
    try {
      setLoading(true);
      const data = await billingService.filterVendorBills(
        selectedVendor || null,
        selectedMonth || null,
        selectedYear || null,
        selectedStatus || null
      );
      setBills(data);
    } catch (err) {
      setToast({ type: 'error', message: err.message || 'Failed to load vendor bills.' });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchVendors();
  }, []);

  useEffect(() => {
    fetchBills();
  }, [selectedVendor, selectedMonth, selectedYear, selectedStatus]);

  const handleGenerateBill = async (recalculate = false) => {
    const vId = genVendorId || selectedVendor;
    if (!vId) {
      setToast({ type: 'error', message: 'Please select a vendor.' });
      return;
    }

    try {
      setGenerating(true);
      const bill = await billingService.generateVendorBill(
        vId,
        genMonth,
        genYear,
        recalculate
      );
      setToast({
        type: 'success',
        message: `Vendor Bill ${bill.billNumber} generated! Payable: ${formatCurrency(bill.totalAmount)}`
      });
      setIsGenerateModalOpen(false);
      fetchBills();
    } catch (err) {
      setToast({ type: 'error', message: err.message || 'Failed to generate vendor bill.' });
    } finally {
      setGenerating(false);
    }
  };

  const handleRecalculateDraft = async (bill) => {
    try {
      const updated = await billingService.generateVendorBill(
        bill.vendorId,
        bill.billingMonth,
        bill.billingYear,
        true
      );
      setToast({
        type: 'success',
        message: `Draft bill ${updated.billNumber} recalculated! Updated Payable: ${formatCurrency(updated.totalAmount)}`
      });
      fetchBills();
    } catch (err) {
      setToast({ type: 'error', message: err.message || 'Recalculation failed.' });
    }
  };

  const handleIssueBill = async (billId) => {
    try {
      await billingService.updateVendorBillStatus(billId, 'ISSUED');
      setToast({ type: 'success', message: 'Vendor bill issued.' });
      fetchBills();
    } catch (err) {
      setToast({ type: 'error', message: err.message || 'Failed to update status.' });
    }
  };

  const openViewInvoice = async (id) => {
    try {
      const detailed = await billingService.getVendorBillById(id);
      setActiveInvoice(detailed);
    } catch (err) {
      setToast({ type: 'error', message: err.message || 'Failed to load bill details.' });
    }
  };

  const openPaymentModal = (bill) => {
    setPaymentBill(bill);
    setPaymentAmount(bill.balanceAmount > 0 ? bill.balanceAmount : '');
    setPaymentMethod('BANK_TRANSFER');
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
      await paymentService.recordVendorPayment({
        partyId: paymentBill.vendorId,
        billId: paymentBill.id,
        paymentDate,
        amount: parseFloat(paymentAmount),
        paymentMethod,
        referenceNumber: paymentRef,
        notes: `Procurement payment towards bill ${paymentBill.billNumber}`
      });
      setToast({ type: 'success', message: `Disbursed ${formatCurrency(paymentAmount)} to vendor.` });
      setPaymentModalOpen(false);
      fetchBills();
    } catch (err) {
      setToast({ type: 'error', message: err.message || 'Failed to record vendor payment.' });
    } finally {
      setSubmittingPayment(false);
    }
  };

  return (
    <div>
      <Header
        title="Vendor Billing"
        breadcrumb="M-Dairy • Milk Procurement Settlements"
        actions={
          <button className="btn btn-primary" onClick={() => setIsGenerateModalOpen(true)}>
            <PlusCircle size={18} />
            <span>Generate Vendor Bill</span>
          </button>
        }
      />

      <div className="page-wrapper">
        {toast && <Toast {...toast} onClose={() => setToast(null)} />}

        {/* Filters */}
        <div className="glass-card" style={{ marginBottom: '1.25rem', padding: '1rem 1.25rem' }}>
          <div style={{ display: 'flex', gap: '1rem', flexWrap: 'wrap', alignItems: 'center' }}>
            <div style={{ flex: 1, minWidth: '200px' }}>
              <label className="form-label" style={{ fontSize: '0.75rem' }}>Vendor</label>
              <select
                className="form-control"
                value={selectedVendor}
                onChange={(e) => setSelectedVendor(e.target.value)}
              >
                <option value="">All Vendors</option>
                {vendors.map((v) => (
                  <option key={v.id} value={v.id}>{v.vendorCode} - {v.name}</option>
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
            <LoadingSpinner message="Loading vendor bills..." />
          ) : bills.length === 0 ? (
            <EmptyState
              title="No Vendor Bills Found"
              message="No procurement settlement bills found for the selected filter."
              action={
                <button className="btn btn-primary" onClick={() => setIsGenerateModalOpen(true)}>
                  <PlusCircle size={16} />
                  <span>Generate Vendor Bill</span>
                </button>
              }
            />
          ) : (
            <div className="table-responsive">
              <table className="glass-table">
                <thead>
                  <tr>
                    <th>Bill Number</th>
                    <th>Vendor</th>
                    <th>Period</th>
                    <th style={{ textAlign: 'center' }}>Cow (L)</th>
                    <th style={{ textAlign: 'center' }}>Buf (L)</th>
                    <th style={{ textAlign: 'center' }}>Total (L)</th>
                    <th style={{ textAlign: 'right' }}>Gross Payable</th>
                    <th style={{ textAlign: 'right' }}>Disbursed</th>
                    <th style={{ textAlign: 'right' }}>Balance Due</th>
                    <th style={{ textAlign: 'center' }}>Status</th>
                    <th style={{ textAlign: 'right' }}>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {bills.map((bill) => (
                    <tr key={bill.id}>
                      <td style={{ fontFamily: 'var(--font-mono)', fontWeight: 700 }}>{bill.billNumber}</td>
                      <td>
                        <div style={{ fontWeight: 600 }}>{bill.vendorName}</div>
                        <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{bill.vendorCode}</div>
                      </td>
                      <td>
                        <span style={{ fontSize: '0.85rem', fontWeight: 600 }}>
                          {String(bill.billingMonth).padStart(2, '0')}/{bill.billingYear}
                        </span>
                      </td>
                      <td style={{ textAlign: 'center' }}>{formatQuantity(bill.cowLitres)}</td>
                      <td style={{ textAlign: 'center' }}>{formatQuantity(bill.buffaloLitres)}</td>
                      <td style={{ textAlign: 'center', fontWeight: 700 }}>{formatQuantity(bill.totalLitres)}</td>
                      <td style={{ textAlign: 'right', fontWeight: 700, color: '#7c3aed' }}>
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
                            title="View Bill Details"
                            onClick={() => openViewInvoice(bill.id)}
                          >
                            <FileText size={14} />
                          </button>

                          {bill.status === 'DRAFT' && (
                            <>
                              <button
                                className="btn btn-sm btn-secondary"
                                title="Recalculate with updated collections"
                                onClick={() => handleRecalculateDraft(bill)}
                              >
                                <RefreshCw size={14} color="#7c3aed" />
                              </button>
                              <button
                                className="btn btn-sm btn-primary"
                                title="Issue Bill"
                                onClick={() => handleIssueBill(bill.id)}
                              >
                                Issue
                              </button>
                            </>
                          )}

                          {bill.balanceAmount > 0 && (
                            <button
                              className="btn btn-sm btn-secondary"
                              title="Disburse Payment"
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

      {/* Generate Vendor Bill Modal */}
      <Modal
        isOpen={isGenerateModalOpen}
        onClose={() => setIsGenerateModalOpen(false)}
        title="Generate Vendor Procurement Bill"
        footer={
          <>
            <button className="btn btn-secondary" onClick={() => setIsGenerateModalOpen(false)} disabled={generating}>
              Cancel
            </button>
            <button className="btn btn-primary" onClick={() => handleGenerateBill(false)} disabled={generating}>
              {generating ? 'Calculating...' : 'Generate Bill'}
            </button>
          </>
        }
      >
        <div className="form-group">
          <label className="form-label">Select Vendor *</label>
          <select
            className="form-control"
            value={genVendorId}
            onChange={(e) => setGenVendorId(e.target.value)}
            required
          >
            <option value="">-- Choose Vendor --</option>
            {vendors.map((v) => (
              <option key={v.id} value={v.id}>
                {v.vendorCode} - {v.name} ({v.milkType})
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

        <div style={{ background: 'rgba(124, 58, 237, 0.06)', padding: '0.85rem', borderRadius: 'var(--radius-md)', border: '1px solid rgba(124, 58, 237, 0.25)', marginTop: '0.5rem' }}>
          <p style={{ fontSize: '0.82rem', color: 'var(--text-secondary)' }}>
            <strong>Procurement Rate Protection:</strong> Amounts are strictly computed using the vendor's agreed procurement rates multiplied by verified saved collections. Customer selling rates are never applied to vendor bills.
          </p>
        </div>
      </Modal>

      {/* Bill Preview Modal */}
      <Modal
        isOpen={!!activeInvoice}
        onClose={() => setActiveInvoice(null)}
        title={activeInvoice ? `Vendor Bill: ${activeInvoice.billNumber}` : 'Bill Details'}
        maxWidth="800px"
        footer={
          <>
            <button className="btn btn-secondary no-print" onClick={() => setActiveInvoice(null)}>
              Close
            </button>
            <button className="btn btn-primary no-print" onClick={() => window.print()}>
              <Printer size={16} />
              <span>Print Bill</span>
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
                  Procurement Department • Anand, Gujarat
                </p>
              </div>
              <div style={{ textAlign: 'right' }}>
                <span style={{ fontSize: '0.85rem', fontWeight: 700, color: '#7c3aed' }}>VENDOR PROCUREMENT BILL</span>
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
                <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', textTransform: 'uppercase' }}>Vendor Details:</span>
                <h4 style={{ fontSize: '1.05rem', fontWeight: 700 }}>{activeInvoice.vendorName}</h4>
                <p style={{ fontSize: '0.85rem', color: 'var(--text-secondary)' }}>Code: {activeInvoice.vendorCode}</p>
                {activeInvoice.vendorPhone && <p style={{ fontSize: '0.85rem', color: 'var(--text-secondary)' }}>Phone: {activeInvoice.vendorPhone}</p>}
              </div>

              <div style={{ textAlign: 'right' }}>
                <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', textTransform: 'uppercase' }}>Procurement Rates:</span>
                <p style={{ fontSize: '0.85rem' }}>Cow Rate: <strong>{formatCurrency(activeInvoice.cowRate)}</strong>/L</p>
                <p style={{ fontSize: '0.85rem' }}>Buffalo Rate: <strong>{formatCurrency(activeInvoice.buffaloRate)}</strong>/L</p>
              </div>
            </div>

            {/* Daily Itemized Breakdown */}
            <h5 style={{ fontSize: '0.9rem', fontWeight: 700, marginBottom: '0.5rem' }}>Daily Collection Breakdown</h5>
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
                    <th style={{ textAlign: 'right' }}>Amount</th>
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
                        No milk collected during this period.
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>

            {/* Bill Totals */}
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
                  <span>Gross Payable:</span>
                  <span style={{ color: '#7c3aed' }}>{formatCurrency(activeInvoice.totalAmount)}</span>
                </div>
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.4rem', fontSize: '0.85rem', color: '#059669' }}>
                  <span>Disbursed:</span>
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

      {/* Disburse Payment Modal */}
      <Modal
        isOpen={paymentModalOpen}
        onClose={() => setPaymentModalOpen(false)}
        title={paymentBill ? `Disburse Payment • ${paymentBill.billNumber}` : 'Record Vendor Payment'}
        footer={
          <>
            <button className="btn btn-secondary" onClick={() => setPaymentModalOpen(false)} disabled={submittingPayment}>
              Cancel
            </button>
            <button className="btn btn-primary" onClick={handleRecordPayment} disabled={submittingPayment}>
              {submittingPayment ? 'Processing...' : 'Disburse Payment'}
            </button>
          </>
        }
      >
        <form onSubmit={handleRecordPayment}>
          <div style={{ background: 'rgba(124, 58, 237, 0.08)', padding: '0.85rem', borderRadius: 'var(--radius-md)', border: '1px solid rgba(124, 58, 237, 0.25)', marginBottom: '1rem' }}>
            <p style={{ fontSize: '0.85rem', color: 'var(--text-secondary)' }}>
              Outstanding Payable: <strong style={{ color: '#dc2626', fontSize: '1.05rem' }}>{formatCurrency(paymentBill?.balanceAmount)}</strong>
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
              <label className="form-label">Payment Mode *</label>
              <select
                className="form-control"
                value={paymentMethod}
                onChange={(e) => setPaymentMethod(e.target.value)}
              >
                <option value="BANK_TRANSFER">Bank Transfer (NEFT/IMPS)</option>
                <option value="UPI">UPI / GPay / PhonePe</option>
                <option value="CASH">Cash</option>
                <option value="CHEQUE">Cheque</option>
              </select>
            </div>
          </div>

          <div className="form-group">
            <label className="form-label">Transaction / UTR Reference</label>
            <input
              type="text"
              className="form-control"
              value={paymentRef}
              onChange={(e) => setPaymentRef(e.target.value)}
              placeholder="e.g. UTR12345678"
            />
          </div>
        </form>
      </Modal>
    </div>
  );
}

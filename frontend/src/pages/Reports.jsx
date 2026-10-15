import React, { useState, useEffect } from 'react';
import Header from '../components/Header';
import LoadingSpinner from '../components/LoadingSpinner';
import EmptyState from '../components/EmptyState';
import Toast from '../components/Toast';
import { reportService } from '../services/reportService';
import { customerService } from '../services/customerService';
import { vendorService } from '../services/vendorService';
import { exportToCsv } from '../utils/exportCsv';
import { formatCurrency, formatQuantity } from '../utils/formatters';
import {
  BarChart3,
  Download,
  Printer,
  Calendar,
  Filter,
  FileSpreadsheet
} from 'lucide-react';

const reportTypes = [
  { id: 'daily-cust-att', name: 'Daily Customer Attendance' },
  { id: 'monthly-cust-att', name: 'Monthly Customer Attendance' },
  { id: 'daily-vend-att', name: 'Daily Vendor Attendance' },
  { id: 'monthly-vend-att', name: 'Monthly Vendor Attendance' },
  { id: 'cust-delivery', name: 'Customer Milk Delivery' },
  { id: 'vend-collection', name: 'Vendor Milk Collection' },
  { id: 'cust-billing', name: 'Customer Billing' },
  { id: 'vend-billing', name: 'Vendor Billing' },
  { id: 'cust-payments', name: 'Customer Payments' },
  { id: 'vend-payments', name: 'Vendor Payments' },
  { id: 'cust-balances', name: 'Outstanding Customer Balances' },
  { id: 'vend-balances', name: 'Outstanding Vendor Balances' },
];

const currentYear = new Date().getFullYear();
const currentMonth = new Date().getMonth() + 1;

export default function Reports() {
  const [selectedReport, setSelectedReport] = useState('cust-billing');
  const [reportData, setReportData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [toast, setToast] = useState(null);

  // Filter states
  const [date, setDate] = useState(new Date().toISOString().split('T')[0]);
  const [startDate, setStartDate] = useState(new Date(new Date().setDate(new Date().getDate() - 30)).toISOString().split('T')[0]);
  const [endDate, setEndDate] = useState(new Date().toISOString().split('T')[0]);
  const [month, setMonth] = useState(currentMonth);
  const [year, setYear] = useState(currentYear);
  const [customerId, setCustomerId] = useState('');
  const [vendorId, setVendorId] = useState('');

  // Dropdown lists
  const [customers, setCustomers] = useState([]);
  const [vendors, setVendors] = useState([]);

  useEffect(() => {
    customerService.getAll(null, null).then(setCustomers).catch(console.error);
    vendorService.getAll(null, null).then(setVendors).catch(console.error);
  }, []);

  const loadReport = async () => {
    try {
      setLoading(true);
      let data = [];
      switch (selectedReport) {
        case 'daily-cust-att':
          data = await reportService.getDailyCustomerAttendance(date);
          break;
        case 'monthly-cust-att':
          data = await reportService.getMonthlyCustomerAttendance(customerId || null, month, year);
          break;
        case 'daily-vend-att':
          data = await reportService.getDailyVendorAttendance(date);
          break;
        case 'monthly-vend-att':
          data = await reportService.getMonthlyVendorAttendance(vendorId || null, month, year);
          break;
        case 'cust-delivery':
          data = await reportService.getCustomerMilkDelivery(startDate, endDate);
          break;
        case 'vend-collection':
          data = await reportService.getVendorMilkCollection(startDate, endDate);
          break;
        case 'cust-billing':
          data = await reportService.getCustomerBilling(month, year);
          break;
        case 'vend-billing':
          data = await reportService.getVendorBilling(month, year);
          break;
        case 'cust-payments':
          data = await reportService.getCustomerPayments(startDate, endDate);
          break;
        case 'vend-payments':
          data = await reportService.getVendorPayments(startDate, endDate);
          break;
        case 'cust-balances':
          data = await reportService.getOutstandingCustomerBalances();
          break;
        case 'vend-balances':
          data = await reportService.getOutstandingVendorBalances();
          break;
        default:
          break;
      }
      setReportData(data);
    } catch (err) {
      setToast({ type: 'error', message: err.message || 'Failed to load report.' });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadReport();
  }, [selectedReport]);

  const handleExportCsv = () => {
    const filename = `m_dairy_${selectedReport}_${new Date().toISOString().split('T')[0]}.csv`;
    exportToCsv(reportData, filename);
  };

  const currentReportName = reportTypes.find((r) => r.id === selectedReport)?.name || 'Report';

  return (
    <div>
      <Header
        title="Business Reports & Audit"
        breadcrumb="M-Dairy • Analytics & Accounting"
        actions={
          <div style={{ display: 'flex', gap: '0.5rem' }}>
            <button
              className="btn btn-secondary no-print"
              onClick={handleExportCsv}
              disabled={loading || reportData.length === 0}
            >
              <Download size={16} />
              <span>Export CSV</span>
            </button>
            <button
              className="btn btn-primary no-print"
              onClick={() => window.print()}
              disabled={loading || reportData.length === 0}
            >
              <Printer size={16} />
              <span>Print</span>
            </button>
          </div>
        }
      />

      <div className="page-wrapper">
        {toast && <Toast {...toast} onClose={() => setToast(null)} />}

        {/* Report Selector Pills */}
        <div className="glass-card no-print" style={{ marginBottom: '1.25rem', padding: '1rem' }}>
          <label className="form-label" style={{ marginBottom: '0.6rem' }}>Select Report Category</label>
          <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap' }}>
            {reportTypes.map((r) => (
              <button
                key={r.id}
                type="button"
                className={`btn btn-sm ${selectedReport === r.id ? 'btn-primary' : 'btn-secondary'}`}
                onClick={() => setSelectedReport(r.id)}
              >
                {r.name}
              </button>
            ))}
          </div>
        </div>

        {/* Dynamic Filters Bar */}
        <div className="glass-card no-print" style={{ marginBottom: '1.25rem', padding: '1rem 1.25rem' }}>
          <div style={{ display: 'flex', gap: '1rem', flexWrap: 'wrap', alignItems: 'center' }}>
            {/* Date filter for daily reports */}
            {(selectedReport === 'daily-cust-att' || selectedReport === 'daily-vend-att') && (
              <div style={{ width: '180px' }}>
                <label className="form-label" style={{ fontSize: '0.75rem' }}>Date</label>
                <input
                  type="date"
                  className="form-control"
                  value={date}
                  onChange={(e) => setDate(e.target.value)}
                />
              </div>
            )}

            {/* Date range filters */}
            {(selectedReport === 'cust-delivery' || selectedReport === 'vend-collection' ||
              selectedReport === 'cust-payments' || selectedReport === 'vend-payments') && (
              <>
                <div style={{ width: '160px' }}>
                  <label className="form-label" style={{ fontSize: '0.75rem' }}>From Date</label>
                  <input
                    type="date"
                    className="form-control"
                    value={startDate}
                    onChange={(e) => setStartDate(e.target.value)}
                  />
                </div>
                <div style={{ width: '160px' }}>
                  <label className="form-label" style={{ fontSize: '0.75rem' }}>To Date</label>
                  <input
                    type="date"
                    className="form-control"
                    value={endDate}
                    onChange={(e) => setEndDate(e.target.value)}
                  />
                </div>
              </>
            )}

            {/* Month & Year filters */}
            {(selectedReport === 'monthly-cust-att' || selectedReport === 'monthly-vend-att' ||
              selectedReport === 'cust-billing' || selectedReport === 'vend-billing') && (
              <>
                <div style={{ width: '130px' }}>
                  <label className="form-label" style={{ fontSize: '0.75rem' }}>Month</label>
                  <select
                    className="form-control"
                    value={month}
                    onChange={(e) => setMonth(Number(e.target.value))}
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
                    value={year}
                    onChange={(e) => setYear(Number(e.target.value))}
                  >
                    {[currentYear - 1, currentYear, currentYear + 1].map((y) => (
                      <option key={y} value={y}>{y}</option>
                    ))}
                  </select>
                </div>
              </>
            )}

            {/* Customer filter */}
            {selectedReport === 'monthly-cust-att' && (
              <div style={{ width: '220px' }}>
                <label className="form-label" style={{ fontSize: '0.75rem' }}>Customer (Optional)</label>
                <select
                  className="form-control"
                  value={customerId}
                  onChange={(e) => setCustomerId(e.target.value)}
                >
                  <option value="">All Customers</option>
                  {customers.map((c) => (
                    <option key={c.id} value={c.id}>{c.customerCode} - {c.name}</option>
                  ))}
                </select>
              </div>
            )}

            {/* Vendor filter */}
            {selectedReport === 'monthly-vend-att' && (
              <div style={{ width: '220px' }}>
                <label className="form-label" style={{ fontSize: '0.75rem' }}>Vendor (Optional)</label>
                <select
                  className="form-control"
                  value={vendorId}
                  onChange={(e) => setVendorId(e.target.value)}
                >
                  <option value="">All Vendors</option>
                  {vendors.map((v) => (
                    <option key={v.id} value={v.id}>{v.vendorCode} - {v.name}</option>
                  ))}
                </select>
              </div>
            )}

            <div style={{ marginTop: '1.25rem' }}>
              <button className="btn btn-primary" onClick={loadReport} disabled={loading}>
                <Filter size={15} /> Run Report
              </button>
            </div>
          </div>
        </div>

        {/* Report Output Table */}
        <div className="glass-card" style={{ padding: 0 }}>
          <div style={{ padding: '1.25rem', borderBottom: '1px solid var(--border-light)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <h3 style={{ fontSize: '1.1rem', fontWeight: 800 }}>{currentReportName}</h3>
            <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
              {reportData.length} records generated
            </span>
          </div>

          {loading ? (
            <LoadingSpinner message="Generating report data..." />
          ) : reportData.length === 0 ? (
            <EmptyState
              title="No Data for Selected Filters"
              message="No records matched the filter criteria for this report."
            />
          ) : (
            <div className="table-responsive">
              <table className="glass-table">
                <thead>
                  <tr>
                    {Object.keys(reportData[0]).map((key) => (
                      <th key={key}>
                        {key.replace(/([A-Z])/g, ' $1').replace(/^./, (str) => str.toUpperCase())}
                      </th>
                    ))}
                  </tr>
                </thead>
                <tbody>
                  {reportData.map((row, idx) => (
                    <tr key={idx}>
                      {Object.entries(row).map(([k, v], cellIdx) => {
                        const isAmt = k.toLowerCase().includes('amount') || k.toLowerCase().includes('balance');
                        const isQty = k.toLowerCase().includes('litres') || k.toLowerCase().includes('qty');
                        return (
                          <td key={cellIdx} style={{ fontWeight: isAmt || isQty ? 700 : 'normal' }}>
                            {isAmt ? formatCurrency(v) : isQty ? formatQuantity(v) : String(v !== null && v !== undefined ? v : '-')}
                          </td>
                        );
                      })}
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

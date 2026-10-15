import api from './api';

export const reportService = {
  getDailyCustomerAttendance: (date) => api.get('/reports/daily-customer-attendance', { params: { date } }),
  getMonthlyCustomerAttendance: (customerId, month, year) => api.get('/reports/monthly-customer-attendance', { params: { customerId, month, year } }),
  getDailyVendorAttendance: (date) => api.get('/reports/daily-vendor-attendance', { params: { date } }),
  getMonthlyVendorAttendance: (vendorId, month, year) => api.get('/reports/monthly-vendor-attendance', { params: { vendorId, month, year } }),
  getCustomerMilkDelivery: (startDate, endDate) => api.get('/reports/customer-milk-delivery', { params: { startDate, endDate } }),
  getVendorMilkCollection: (startDate, endDate) => api.get('/reports/vendor-milk-collection', { params: { startDate, endDate } }),
  getCustomerBilling: (month, year) => api.get('/reports/customer-billing', { params: { month, year } }),
  getVendorBilling: (month, year) => api.get('/reports/vendor-billing', { params: { month, year } }),
  getCustomerPayments: (startDate, endDate) => api.get('/reports/customer-payments', { params: { startDate, endDate } }),
  getVendorPayments: (startDate, endDate) => api.get('/reports/vendor-payments', { params: { startDate, endDate } }),
  getOutstandingCustomerBalances: () => api.get('/reports/outstanding-customer-balances'),
  getOutstandingVendorBalances: () => api.get('/reports/outstanding-vendor-balances'),
};

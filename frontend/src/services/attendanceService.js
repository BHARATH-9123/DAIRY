import api from './api';

export const attendanceService = {
  // Customer Attendance
  getCustomerAttendance: (date) => api.get('/customer-attendance', { params: { date } }),
  saveCustomerAttendance: (dto) => api.post('/customer-attendance', dto),
  saveBatchCustomerAttendance: (list) => api.post('/customer-attendance/batch', list),
  getCustomerSummary: (date) => api.get('/customer-attendance/summary', { params: { date } }),
  getCustomerHistory: (customerId, startDate, endDate) =>
    api.get(`/customer-attendance/history/${customerId}`, { params: { startDate, endDate } }),

  // Vendor Attendance
  getVendorAttendance: (date) => api.get('/vendor-attendance', { params: { date } }),
  saveVendorAttendance: (dto) => api.post('/vendor-attendance', dto),
  saveBatchVendorAttendance: (list) => api.post('/vendor-attendance/batch', list),
  getVendorSummary: (date) => api.get('/vendor-attendance/summary', { params: { date } }),
  getVendorHistory: (vendorId, startDate, endDate) =>
    api.get(`/vendor-attendance/history/${vendorId}`, { params: { startDate, endDate } }),
};

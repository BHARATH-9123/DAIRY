import api from './api';

export const billingService = {
  // Customer Billing
  filterCustomerBills: (customerId, month, year, status) => {
    const params = {};
    if (customerId) params.customerId = customerId;
    if (month) params.month = month;
    if (year) params.year = year;
    if (status) params.status = status;
    return api.get('/customer-bills', { params });
  },

  getCustomerBillById: (id) => api.get(`/customer-bills/${id}`),

  generateCustomerBill: (customerId, billingMonth, billingYear, recalculateDraft = false) => {
    return api.post('/customer-bills/generate', {
      id: customerId,
      billingMonth,
      billingYear,
      recalculateDraft
    });
  },

  updateCustomerBillStatus: (id, status) => api.patch(`/customer-bills/${id}/status`, null, { params: { status } }),

  deleteDraftCustomerBill: (id) => api.delete(`/customer-bills/${id}`),

  // Vendor Billing
  filterVendorBills: (vendorId, month, year, status) => {
    const params = {};
    if (vendorId) params.vendorId = vendorId;
    if (month) params.month = month;
    if (year) params.year = year;
    if (status) params.status = status;
    return api.get('/vendor-bills', { params });
  },

  getVendorBillById: (id) => api.get(`/vendor-bills/${id}`),

  generateVendorBill: (vendorId, billingMonth, billingYear, recalculateDraft = false) => {
    return api.post('/vendor-bills/generate', {
      id: vendorId,
      billingMonth,
      billingYear,
      recalculateDraft
    });
  },

  updateVendorBillStatus: (id, status) => api.patch(`/vendor-bills/${id}/status`, null, { params: { status } }),

  deleteDraftVendorBill: (id) => api.delete(`/vendor-bills/${id}`),
};

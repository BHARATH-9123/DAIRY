import api from './api';

export const paymentService = {
  // Customer Payments
  getCustomerPayments: (customerId, startDate, endDate) => {
    const params = {};
    if (customerId) params.customerId = customerId;
    if (startDate) params.startDate = startDate;
    if (endDate) params.endDate = endDate;
    return api.get('/customer-payments', { params });
  },

  recordCustomerPayment: (paymentDto) => api.post('/customer-payments', paymentDto),

  // Vendor Payments
  getVendorPayments: (vendorId, startDate, endDate) => {
    const params = {};
    if (vendorId) params.vendorId = vendorId;
    if (startDate) params.startDate = startDate;
    if (endDate) params.endDate = endDate;
    return api.get('/vendor-payments', { params });
  },

  recordVendorPayment: (paymentDto) => api.post('/vendor-payments', paymentDto),
};

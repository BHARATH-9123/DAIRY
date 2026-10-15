import api from './api';

export const customerService = {
  getAll: (search, active) => {
    const params = {};
    if (search) params.search = search;
    if (active !== undefined && active !== '') params.active = active;
    return api.get('/customers', { params });
  },

  getById: (id) => api.get(`/customers/${id}`),

  create: (customer) => api.post('/customers', customer),

  update: (id, customer) => api.put(`/customers/${id}`, customer),

  deactivate: (id) => api.patch(`/customers/${id}/deactivate`),

  activate: (id) => api.patch(`/customers/${id}/activate`),
};

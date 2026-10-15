import api from './api';

export const vendorService = {
  getAll: (search, active) => {
    const params = {};
    if (search) params.search = search;
    if (active !== undefined && active !== '') params.active = active;
    return api.get('/vendors', { params });
  },

  getById: (id) => api.get(`/vendors/${id}`),

  create: (vendor) => api.post('/vendors', vendor),

  update: (id, vendor) => api.put(`/vendors/${id}`, vendor),

  deactivate: (id) => api.patch(`/vendors/${id}/deactivate`),

  activate: (id) => api.patch(`/vendors/${id}/activate`),
};

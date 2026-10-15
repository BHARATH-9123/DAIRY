import api from './api';

export const settingsService = {
  getSettings: () => api.get('/settings'),
  updateSettings: (settingsDto) => api.put('/settings', settingsDto),
};

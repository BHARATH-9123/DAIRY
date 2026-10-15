import axios from 'axios';

const configuredBaseUrl = (import.meta.env.VITE_API_URL || 'http://localhost:8080').replace(/\/+$/, '');
const api = axios.create({
  baseURL: configuredBaseUrl.endsWith('/api') ? configuredBaseUrl : `${configuredBaseUrl}/api`,
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.response.use(
  (response) => response.data,
  (error) => {
    let message = 'An error occurred while connecting to the server.';
    if (error.response && error.response.data) {
      if (error.response.data.message) {
        message = error.response.data.message;
      }
      if (error.response.data.validationErrors) {
        const details = Object.values(error.response.data.validationErrors).join(', ');
        message = `${message} (${details})`;
      }
    } else if (error.message) {
      message = error.message;
    }
    return Promise.reject(new Error(message));
  }
);

export default api;

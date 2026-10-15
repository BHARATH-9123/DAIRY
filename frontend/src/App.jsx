import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Sidebar from './components/Sidebar';
import Overview from './pages/Overview';
import Customers from './pages/Customers';
import Vendors from './pages/Vendors';
import CustomerAttendance from './pages/CustomerAttendance';
import VendorAttendance from './pages/VendorAttendance';
import CustomerBilling from './pages/CustomerBilling';
import VendorBilling from './pages/VendorBilling';
import Reports from './pages/Reports';
import Settings from './pages/Settings';

export default function App() {
  return (
    <BrowserRouter>
      <div className="app-container">
        <Sidebar />
        <main className="main-content">
          <Routes>
            <Route path="/" element={<Overview />} />
            <Route path="/customers" element={<Customers />} />
            <Route path="/vendors" element={<Vendors />} />
            <Route path="/customer-attendance" element={<CustomerAttendance />} />
            <Route path="/vendor-attendance" element={<VendorAttendance />} />
            <Route path="/customer-billing" element={<CustomerBilling />} />
            <Route path="/vendor-billing" element={<VendorBilling />} />
            <Route path="/reports" element={<Reports />} />
            <Route path="/settings" element={<Settings />} />
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </main>
      </div>
    </BrowserRouter>
  );
}

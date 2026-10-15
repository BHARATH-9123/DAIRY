import React from 'react';
import { NavLink } from 'react-router-dom';
import {
  LayoutDashboard,
  Users,
  Building2,
  CalendarCheck2,
  ClipboardList,
  ReceiptText,
  FileSpreadsheet,
  BarChart3,
  Settings as SettingsIcon,
  Milk
} from 'lucide-react';

const navItems = [
  { name: 'Overview', path: '/', icon: LayoutDashboard },
  { name: 'Customers', path: '/customers', icon: Users },
  { name: 'Vendors', path: '/vendors', icon: Building2 },
  { name: 'Customer Attendance', path: '/customer-attendance', icon: CalendarCheck2 },
  { name: 'Vendor Attendance', path: '/vendor-attendance', icon: ClipboardList },
  { name: 'Customer Billing', path: '/customer-billing', icon: ReceiptText },
  { name: 'Vendor Billing', path: '/vendor-billing', icon: FileSpreadsheet },
  { name: 'Reports', path: '/reports', icon: BarChart3 },
  { name: 'Settings', path: '/settings', icon: SettingsIcon },
];

export default function Sidebar() {
  return (
    <aside className="sidebar">
      <div className="sidebar-header">
        <div className="sidebar-logo-icon">
          <Milk size={22} strokeWidth={2.5} />
        </div>
        <div className="sidebar-brand">
          <h1>M-DAIRY</h1>
          <p>From Farm To Home</p>
        </div>
      </div>

      <nav className="sidebar-nav">
        {navItems.map((item) => {
          const Icon = item.icon;
          return (
            <NavLink
              key={item.path}
              to={item.path}
              className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}
              end={item.path === '/'}
            >
              <Icon size={19} />
              <span>{item.name}</span>
            </NavLink>
          );
        })}
      </nav>

      <div className="sidebar-footer">
        <span>v1.0 • Local Edition</span>
        <span style={{ fontSize: '0.75rem', color: '#059669', fontWeight: 600 }}>● Connected</span>
      </div>
    </aside>
  );
}

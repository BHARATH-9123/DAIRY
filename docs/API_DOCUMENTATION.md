# M-DAIRY - REST API Documentation

Base URL: `http://localhost:8080/api`

---

## 1. Customers API (`/api/customers`)
- `GET /api/customers?search={query}&active={true|false}`: List all customers with search and filter.
- `GET /api/customers/{id}`: Retrieve customer details by ID.
- `POST /api/customers`: Create new customer (with unique `customerCode`, defaults, rates).
- `PUT /api/customers/{id}`: Update customer profile.
- `PATCH /api/customers/{id}/deactivate`: Safely deactivate customer (preserves history).
- `PATCH /api/customers/{id}/activate`: Re-activate customer.

## 2. Vendors API (`/api/vendors`)
- `GET /api/vendors?search={query}&active={true|false}`: List all vendors.
- `GET /api/vendors/{id}`: Retrieve vendor details by ID.
- `POST /api/vendors`: Create new vendor (procurement rates & defaults).
- `PUT /api/vendors/{id}`: Update vendor.
- `PATCH /api/vendors/{id}/deactivate`: Deactivate vendor safely.
- `PATCH /api/vendors/{id}/activate`: Re-activate vendor.

## 3. Customer Attendance API (`/api/customer-attendance`)
- `GET /api/customer-attendance?date=YYYY-MM-DD`: Get attendance sheet for given date.
- `POST /api/customer-attendance`: Record/edit attendance for a single customer.
- `POST /api/customer-attendance/batch`: Batch save all customer attendance for a date.
- `GET /api/customer-attendance/summary?date=YYYY-MM-DD`: Daily delivery totals and attendance breakdown.
- `GET /api/customer-attendance/history/{customerId}?startDate=...&endDate=...`: Date range history.

## 4. Vendor Attendance API (`/api/vendor-attendance`)
- `GET /api/vendor-attendance?date=YYYY-MM-DD`: Get milk collection sheet for date.
- `POST /api/vendor-attendance`: Save vendor collection.
- `POST /api/vendor-attendance/batch`: Batch save vendor collections.
- `GET /api/vendor-attendance/summary?date=YYYY-MM-DD`: Daily procurement totals.

## 5. Customer Billing API (`/api/customer-bills`)
- `GET /api/customer-bills?customerId=&month=&year=&status=`: Filter customer bills.
- `GET /api/customer-bills/{id}`: View bill details with itemized daily attendance snapshot.
- `POST /api/customer-bills/generate`: Generate new monthly bill or recalculate existing draft (`recalculateDraft: true`).
- `PATCH /api/customer-bills/{id}/status?status={ISSUED|PAID}`: Update status.
- `DELETE /api/customer-bills/{id}`: Delete draft bill (only allowed for DRAFT).

## 6. Vendor Billing API (`/api/vendor-bills`)
- `GET /api/vendor-bills?vendorId=&month=&year=&status=`: Filter vendor procurement bills.
- `GET /api/vendor-bills/{id}`: View vendor bill with collection breakdown.
- `POST /api/vendor-bills/generate`: Generate or recalculate draft vendor bill.
- `PATCH /api/vendor-bills/{id}/status?status=`: Update status.
- `DELETE /api/vendor-bills/{id}`: Delete draft vendor bill.

## 7. Payments API (`/api/customer-payments` & `/api/vendor-payments`)
- `POST /api/customer-payments`: Record customer payment (updates bill paid & balance amounts).
- `GET /api/customer-payments?customerId=&startDate=&endDate=`: Filter payments.
- `POST /api/vendor-payments`: Disburse vendor payment.
- `GET /api/vendor-payments?vendorId=&startDate=&endDate=`: Filter vendor disbursements.

## 8. Dashboard & Reports (`/api/dashboard` & `/api/reports`)
- `GET /api/dashboard/summary`: Live dashboard summary KPIs and recent activity.
- `GET /api/reports/*`: 12 dedicated reports for attendance, delivery, collection, billing, payments, and balances.

## 9. Settings API (`/api/settings`)
- `GET /api/settings`: Business details, default selling and procurement rates.
- `PUT /api/settings`: Update settings.

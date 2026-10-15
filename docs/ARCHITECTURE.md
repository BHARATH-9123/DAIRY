# M-DAIRY - System Architecture

"From Farm To Home"

## 1. Overview
M-DAIRY is an enterprise-grade dairy management application engineered to handle the complete lifecycle of dairy operations:
- Customer delivery & subscriptions
- Vendor milk procurement & collection
- Daily session-based attendance (Morning & Night)
- Financial billing with strict separation between Cow & Buffalo rates
- Reconciled payment disbursements & collections
- Dynamic reports and audits with CSV export

---

## 2. Layered Architecture

```
+-----------------------------------------------------------+
|                    React Frontend (Vite)                  |
|  - iOS Glassmorphism Design System                        |
|  - Real-time Axios API Client                             |
|  - React Router (Zero-friction unauthenticated dashboard) |
+-----------------------------------------------------------+
                              |
                     REST APIs (JSON)
                              |
+-----------------------------------------------------------+
|              Spring Boot 3.3.4 Backend                    |
|  - Controller Layer (REST Endpoints)                      |
|  - Service Layer (Business Logic & Transactions)          |
|  - Repository Layer (Spring Data JPA)                     |
|  - Exception & Validation Advice                          |
+-----------------------------------------------------------+
                              |
                    Hibernate ORM / JDBC
                              |
+-----------------------------------------------------------+
|                   MySQL 8.0 Database                      |
|  - UTF-8 / utf8mb4 Relational Schema                      |
|  - Transactional Consistency (ACID)                       |
+-----------------------------------------------------------+
```

---

## 3. Financial Calculation Guarantees
1. **Separation of Rates**:
   - Customer Selling Rates and Vendor Procurement Rates are entirely decoupled.
   - Cow milk and Buffalo milk are calculated independently at their respective unit rates.
2. **Attendance to Billing Pipeline**:
   - Absent attendance contributes exactly **0.00 litres**.
   - Default quantities configured on customer/vendor entities are starting presets for attendance entry and never become billable transactions without saved, qualifying attendance records.
3. **Draft Recalculation**:
   - Draft bills can be recalculated dynamically as attendance is edited.
   - Already recorded payments and issued invoices are strictly protected from overwrite.
4. **Authoritative Precision**:
   - All financial and quantity calculations in Java utilize `BigDecimal` with explicit scale and `HALF_UP` rounding. Floating-point arithmetic is prohibited for currency.

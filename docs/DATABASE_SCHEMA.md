# M-DAIRY - Relational Database Schema

Engine: MySQL 8.0  
Database Name: `m_dairy`  
Character Set: `utf8mb4`  
Collation: `utf8mb4_unicode_ci`

---

## Tables & Relationships

```
+--------------------+       +------------------------+
|     customers      |       |  customer_attendance   |
|--------------------|       |------------------------|
| id (PK)            |1 --- *| id (PK)                |
| customer_code (UQ) |       | customer_id (FK)       |
| name               |       | attendance_date        |
| phone, address     |       | status (PRESENT/ABSENT)|
| milk_type          |       | morning_cow_qty        |
| delivery_schedule  |       | morning_buffalo_qty    |
| cow_rate           |       | night_cow_qty          |
| buffalo_rate       |       | night_buffalo_qty      |
| active             |       +------------------------+
+--------------------+
          | 1
          |
          *
+--------------------+       +------------------------+
|   customer_bills   |1 --- *|  customer_bill_items   |
|--------------------|       |------------------------|
| id (PK)            |       | id (PK)                |
| bill_number (UQ)   |       | bill_id (FK)           |
| customer_id (FK)   |       | attendance_date        |
| billing_month      |       | daily_total_litres     |
| billing_year       |       | daily_amount           |
| total_litres       |       +------------------------+
| total_amount       |
| paid_amount        |       +------------------------+
| balance_amount     |1 --- *|   customer_payments    |
| status             |       |------------------------|
+--------------------+       | id (PK)                |
                             | bill_id (FK nullable)  |
                             | payment_number (UQ)    |
                             | amount, payment_date   |
                             +------------------------+

(Similar mirror structure for vendors: vendors -> vendor_attendance, vendor_bills, vendor_bill_items, vendor_payments)
```

---

## Integrity & Constraints
1. **Attendance Uniqueness**:
   - `UNIQUE KEY uk_customer_attendance_date (customer_id, attendance_date)`
   - `UNIQUE KEY uk_vendor_attendance_date (vendor_id, attendance_date)`
2. **Bill Period Uniqueness**:
   - `UNIQUE KEY uk_customer_bill_period (customer_id, billing_month, billing_year)`
   - `UNIQUE KEY uk_vendor_bill_period (vendor_id, billing_month, billing_year)`
3. **Financial Safety**:
   - Quantities stored as `DECIMAL(10,2)`.
   - Monetary amounts stored as `DECIMAL(12,2)`.
   - Attendance deletion restricted if associated bills exist.

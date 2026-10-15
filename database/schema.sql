-- ============================================================
-- M-DAIRY - Database Schema Definition
-- Database: m_dairy (MySQL 8)
-- Charset: utf8mb4, Collation: utf8mb4_unicode_ci
-- ============================================================

CREATE DATABASE IF NOT EXISTS `m_dairy`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE `m_dairy`;

-- Drop existing tables in reverse dependency order if recreating
DROP TABLE IF EXISTS `vendor_bill_items`;
DROP TABLE IF EXISTS `vendor_payments`;
DROP TABLE IF EXISTS `vendor_bills`;
DROP TABLE IF EXISTS `vendor_attendance`;
DROP TABLE IF EXISTS `customer_bill_items`;
DROP TABLE IF EXISTS `customer_payments`;
DROP TABLE IF EXISTS `customer_bills`;
DROP TABLE IF EXISTS `customer_attendance`;
DROP TABLE IF EXISTS `vendors`;
DROP TABLE IF EXISTS `customers`;
DROP TABLE IF EXISTS `business_settings`;

-- 1. Business Settings
CREATE TABLE `business_settings` (
    `id` BIGINT NOT NULL PRIMARY KEY DEFAULT 1,
    `business_name` VARCHAR(150) NOT NULL DEFAULT 'M-DAIRY',
    `tagline` VARCHAR(255) DEFAULT 'From Farm To Home',
    `address` TEXT,
    `phone` VARCHAR(50) DEFAULT '+91 98765 43210',
    `email` VARCHAR(100) DEFAULT 'contact@mdairy.com',
    `default_cow_selling_rate` DECIMAL(10,2) NOT NULL DEFAULT 60.00,
    `default_buffalo_selling_rate` DECIMAL(10,2) NOT NULL DEFAULT 80.00,
    `default_cow_procurement_rate` DECIMAL(10,2) NOT NULL DEFAULT 45.00,
    `default_buffalo_procurement_rate` DECIMAL(10,2) NOT NULL DEFAULT 65.00,
    `currency_symbol` VARCHAR(10) NOT NULL DEFAULT '₹',
    `quantity_precision` INT NOT NULL DEFAULT 2,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Customers Table
CREATE TABLE `customers` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `customer_code` VARCHAR(50) NOT NULL,
    `name` VARCHAR(100) NOT NULL,
    `phone` VARCHAR(20) DEFAULT NULL,
    `address` TEXT DEFAULT NULL,
    `milk_type` VARCHAR(20) NOT NULL DEFAULT 'COW', -- 'COW', 'BUFFALO', 'BOTH'
    `delivery_schedule` VARCHAR(20) NOT NULL DEFAULT 'MORNING', -- 'MORNING', 'NIGHT', 'BOTH'
    `morning_cow_qty` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    `morning_buffalo_qty` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    `night_cow_qty` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    `night_buffalo_qty` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    `cow_rate` DECIMAL(10,2) NOT NULL DEFAULT 60.00,
    `buffalo_rate` DECIMAL(10,2) NOT NULL DEFAULT 80.00,
    `notes` TEXT DEFAULT NULL,
    `active` BOOLEAN NOT NULL DEFAULT TRUE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `uk_customer_code` UNIQUE (`customer_code`),
    INDEX `idx_customers_active` (`active`),
    INDEX `idx_customers_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Vendors Table
CREATE TABLE `vendors` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `vendor_code` VARCHAR(50) NOT NULL,
    `name` VARCHAR(100) NOT NULL,
    `phone` VARCHAR(20) DEFAULT NULL,
    `address` TEXT DEFAULT NULL,
    `milk_type` VARCHAR(20) NOT NULL DEFAULT 'COW', -- 'COW', 'BUFFALO', 'BOTH'
    `collection_schedule` VARCHAR(20) NOT NULL DEFAULT 'MORNING', -- 'MORNING', 'NIGHT', 'BOTH'
    `morning_cow_qty` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    `morning_buffalo_qty` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    `night_cow_qty` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    `night_buffalo_qty` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    `cow_rate` DECIMAL(10,2) NOT NULL DEFAULT 45.00, -- Procurement rate per litre
    `buffalo_rate` DECIMAL(10,2) NOT NULL DEFAULT 65.00, -- Procurement rate per litre
    `notes` TEXT DEFAULT NULL,
    `active` BOOLEAN NOT NULL DEFAULT TRUE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `uk_vendor_code` UNIQUE (`vendor_code`),
    INDEX `idx_vendors_active` (`active`),
    INDEX `idx_vendors_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. Customer Attendance Table
CREATE TABLE `customer_attendance` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `customer_id` BIGINT NOT NULL,
    `attendance_date` DATE NOT NULL,
    `status` VARCHAR(20) NOT NULL DEFAULT 'ABSENT', -- 'PRESENT', 'ABSENT'
    `morning_cow_qty` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    `morning_buffalo_qty` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    `night_cow_qty` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    `night_buffalo_qty` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    `notes` VARCHAR(255) DEFAULT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_cust_att_customer` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`) ON DELETE RESTRICT,
    CONSTRAINT `uk_customer_attendance_date` UNIQUE (`customer_id`, `attendance_date`),
    INDEX `idx_cust_att_date` (`attendance_date`),
    INDEX `idx_cust_att_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. Vendor Attendance Table
CREATE TABLE `vendor_attendance` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `vendor_id` BIGINT NOT NULL,
    `attendance_date` DATE NOT NULL,
    `status` VARCHAR(20) NOT NULL DEFAULT 'ABSENT', -- 'PRESENT', 'ABSENT'
    `morning_cow_qty` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    `morning_buffalo_qty` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    `night_cow_qty` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    `night_buffalo_qty` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    `notes` VARCHAR(255) DEFAULT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_vend_att_vendor` FOREIGN KEY (`vendor_id`) REFERENCES `vendors` (`id`) ON DELETE RESTRICT,
    CONSTRAINT `uk_vendor_attendance_date` UNIQUE (`vendor_id`, `attendance_date`),
    INDEX `idx_vend_att_date` (`attendance_date`),
    INDEX `idx_vend_att_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. Customer Bills Table
CREATE TABLE `customer_bills` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `bill_number` VARCHAR(50) NOT NULL,
    `customer_id` BIGINT NOT NULL,
    `billing_month` INT NOT NULL,
    `billing_year` INT NOT NULL,
    `cow_litres` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    `buffalo_litres` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    `total_litres` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    `cow_rate` DECIMAL(10,2) NOT NULL,
    `buffalo_rate` DECIMAL(10,2) NOT NULL,
    `cow_amount` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    `buffalo_amount` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    `total_amount` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    `paid_amount` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    `balance_amount` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    `status` VARCHAR(20) NOT NULL DEFAULT 'DRAFT', -- 'DRAFT', 'ISSUED', 'PAID', 'PARTIALLY_PAID'
    `notes` TEXT DEFAULT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_cust_bills_customer` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`) ON DELETE RESTRICT,
    CONSTRAINT `uk_customer_bill_period` UNIQUE (`customer_id`, `billing_month`, `billing_year`),
    CONSTRAINT `uk_customer_bill_number` UNIQUE (`bill_number`),
    INDEX `idx_cust_bill_period` (`billing_year`, `billing_month`),
    INDEX `idx_cust_bill_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. Customer Bill Items (Itemized attendance snapshot)
CREATE TABLE `customer_bill_items` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `bill_id` BIGINT NOT NULL,
    `attendance_date` DATE NOT NULL,
    `morning_cow_qty` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    `morning_buffalo_qty` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    `night_cow_qty` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    `night_buffalo_qty` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    `daily_total_litres` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    `cow_rate` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    `buffalo_rate` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    `daily_amount` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    CONSTRAINT `fk_cust_bill_items_bill` FOREIGN KEY (`bill_id`) REFERENCES `customer_bills` (`id`) ON DELETE CASCADE,
    INDEX `idx_cust_bill_items_date` (`attendance_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. Customer Payments Table
CREATE TABLE `customer_payments` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `payment_number` VARCHAR(50) NOT NULL,
    `customer_id` BIGINT NOT NULL,
    `bill_id` BIGINT DEFAULT NULL,
    `payment_date` DATE NOT NULL,
    `amount` DECIMAL(12,2) NOT NULL,
    `payment_method` VARCHAR(50) NOT NULL DEFAULT 'CASH', -- 'CASH', 'UPI', 'BANK_TRANSFER', 'CHEQUE'
    `reference_number` VARCHAR(100) DEFAULT NULL,
    `notes` TEXT DEFAULT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_cust_pay_customer` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`) ON DELETE RESTRICT,
    CONSTRAINT `fk_cust_pay_bill` FOREIGN KEY (`bill_id`) REFERENCES `customer_bills` (`id`) ON DELETE SET NULL,
    CONSTRAINT `uk_cust_payment_number` UNIQUE (`payment_number`),
    INDEX `idx_cust_pay_date` (`payment_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 9. Vendor Bills Table
CREATE TABLE `vendor_bills` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `bill_number` VARCHAR(50) NOT NULL,
    `vendor_id` BIGINT NOT NULL,
    `billing_month` INT NOT NULL,
    `billing_year` INT NOT NULL,
    `cow_litres` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    `buffalo_litres` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    `total_litres` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    `cow_rate` DECIMAL(10,2) NOT NULL,
    `buffalo_rate` DECIMAL(10,2) NOT NULL,
    `cow_amount` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    `buffalo_amount` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    `total_amount` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    `paid_amount` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    `balance_amount` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    `status` VARCHAR(20) NOT NULL DEFAULT 'DRAFT', -- 'DRAFT', 'ISSUED', 'PAID', 'PARTIALLY_PAID'
    `notes` TEXT DEFAULT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_vend_bills_vendor` FOREIGN KEY (`vendor_id`) REFERENCES `vendors` (`id`) ON DELETE RESTRICT,
    CONSTRAINT `uk_vendor_bill_period` UNIQUE (`vendor_id`, `billing_month`, `billing_year`),
    CONSTRAINT `uk_vendor_bill_number` UNIQUE (`bill_number`),
    INDEX `idx_vend_bill_period` (`billing_year`, `billing_month`),
    INDEX `idx_vend_bill_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 10. Vendor Bill Items (Itemized collection snapshot)
CREATE TABLE `vendor_bill_items` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `bill_id` BIGINT NOT NULL,
    `attendance_date` DATE NOT NULL,
    `morning_cow_qty` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    `morning_buffalo_qty` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    `night_cow_qty` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    `night_buffalo_qty` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    `daily_total_litres` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    `cow_rate` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    `buffalo_rate` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    `daily_amount` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    CONSTRAINT `fk_vend_bill_items_bill` FOREIGN KEY (`bill_id`) REFERENCES `vendor_bills` (`id`) ON DELETE CASCADE,
    INDEX `idx_vend_bill_items_date` (`attendance_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 11. Vendor Payments Table
CREATE TABLE `vendor_payments` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `payment_number` VARCHAR(50) NOT NULL,
    `vendor_id` BIGINT NOT NULL,
    `bill_id` BIGINT DEFAULT NULL,
    `payment_date` DATE NOT NULL,
    `amount` DECIMAL(12,2) NOT NULL,
    `payment_method` VARCHAR(50) NOT NULL DEFAULT 'CASH', -- 'CASH', 'UPI', 'BANK_TRANSFER', 'CHEQUE'
    `reference_number` VARCHAR(100) DEFAULT NULL,
    `notes` TEXT DEFAULT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_vend_pay_vendor` FOREIGN KEY (`vendor_id`) REFERENCES `vendors` (`id`) ON DELETE RESTRICT,
    CONSTRAINT `fk_vend_pay_bill` FOREIGN KEY (`bill_id`) REFERENCES `vendor_bills` (`id`) ON DELETE SET NULL,
    CONSTRAINT `uk_vend_payment_number` UNIQUE (`payment_number`),
    INDEX `idx_vend_pay_date` (`payment_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert default settings row if not exists
INSERT INTO `business_settings` (
    `id`, `business_name`, `tagline`, `address`, `phone`, `email`,
    `default_cow_selling_rate`, `default_buffalo_selling_rate`,
    `default_cow_procurement_rate`, `default_buffalo_procurement_rate`,
    `currency_symbol`, `quantity_precision`
) VALUES (
    1, 'M-DAIRY', 'From Farm To Home', 'Dairy Junction, Main Road, Anand, Gujarat',
    '+91 98765 43210', 'contact@mdairy.com',
    60.00, 80.00, 45.00, 65.00, '₹', 2
) ON DUPLICATE KEY UPDATE `id` = 1;

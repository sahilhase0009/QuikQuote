-- =============================================================================
-- QuoteFlow Seed Data
-- Demo Account Credentials:
-- Email: demo@primeoffice.com
-- Password: password123 (BCrypt: $2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymY05x3b.X8/o29w.C.Cie)
-- =============================================================================

-- Seed Users
INSERT INTO users (id, name, email, phone, password_hash, enabled, created_at, updated_at)
VALUES (1, 'Rahul Patil', 'demo@primeoffice.com', '+91 9876543210', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymY05x3b.X8/o29w.C.Cie', true, NOW(), NOW());

-- Seed Business Profile
INSERT INTO business_profiles (id, user_id, business_name, logo, address, city, state, country, pincode, phone, email, website, gst_number, pan_number, default_tax_percentage, quotation_prefix, quotation_validity_days, bank_name, bank_account_number, ifsc_code, terms_and_conditions, created_at, updated_at)
VALUES (1, 1, 'Prime Office Solutions', 'https://images.unsplash.com/photo-1497366216548-37526070297c?w=150', '102 Industrial Estate, MG Road', 'Mumbai', 'Maharashtra', 'India', '400001', '+91 9876543210', 'sales@primeoffice.com', 'www.primeoffice.com', '27AAAAA0000A1Z5', 'ABCDE1234F', 18.00, 'QT', 15, 'HDFC Bank', '50200012345678', 'HDFC0000123', '1. 50% advance payment required upon order confirmation.\n2. Delivery within 7-10 business days.\n3. Quotation valid for 15 days from date of issue.', NOW(), NOW());

-- Seed Customers
INSERT INTO customers (id, business_id, name, company_name, phone, email, address, city, state, pincode, notes, created_at, updated_at)
VALUES 
(1, 1, 'Amit Sharma', 'Apex Technologies', '+91 9820011223', 'amit@apextech.com', 'Building 4, SEEPZ, Andheri East', 'Mumbai', 'Maharashtra', '400096', 'Key corporate account', NOW(), NOW()),
(2, 1, 'Priya Nair', 'Design Studio One', '+91 9819988776', 'priya@designstudio.com', 'Suite 201, Express Towers, Nariman Point', 'Mumbai', 'Maharashtra', '400021', 'Interested in ergonomic furniture', NOW(), NOW()),
(3, 1, 'Rajesh Kumar', 'TechVision Systems', '+91 9765432109', 'rajesh@techvision.io', 'Plot 12, IT Park, Hinjewadi', 'Pune', 'Maharashtra', '411057', 'New branch office setup', NOW(), NOW());

-- Seed Products / Services
INSERT INTO products (id, business_id, name, description, category, unit, price, tax_percentage, active, created_at, updated_at)
VALUES 
(1, 1, 'Office Chair', 'High-back mesh ergonomic chair with lumbar support and 3D armrests', 'Furniture', 'Piece', 4500.00, 18.00, true, NOW(), NOW()),
(2, 1, 'Office Table', 'Modular executive desk with cable management and drawer pedestal', 'Furniture', 'Piece', 8000.00, 18.00, true, NOW(), NOW()),
(3, 1, 'Ergonomic Chair', 'Premium ergonomic chair with synchronized tilt mechanism', 'Furniture', 'Piece', 7500.00, 18.00, true, NOW(), NOW()),
(4, 1, 'Laptop Stand', 'Aluminum adjustable laptop riser stand with cooling ventilation', 'Accessories', 'Piece', 2000.00, 18.00, true, NOW(), NOW()),
(5, 1, 'Conference Table 8-Seater', 'Large boardroom wooden conference table with built-in power sockets', 'Furniture', 'Piece', 25000.00, 18.00, true, NOW(), NOW());

-- Seed Quotation 1 (SENT)
INSERT INTO quotations (id, business_id, customer_id, quotation_number, quotation_date, valid_until, status, subtotal, discount_type, discount_value, discount_amount, tax_amount, grand_total, notes, terms_and_conditions, created_at, updated_at)
VALUES (1, 1, 1, 'QT-2026-0001', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 15 DAY), 'SENT', 85000.00, 'FIXED', 5000.00, 5000.00, 14400.00, 94400.00, 'Delivery included to Andheri East office.', '1. 50% advance payment required upon order confirmation.\n2. Delivery within 7-10 business days.', NOW(), NOW());

INSERT INTO quotation_items (id, quotation_id, product_id, item_name, description, quantity, unit, unit_price, tax_percentage, discount, line_subtotal, line_tax, line_total)
VALUES 
(1, 1, 1, 'Office Chair', 'High-back mesh ergonomic chair with lumbar support', 10, 'Piece', 4500.00, 18.00, 0.00, 45000.00, 8100.00, 53100.00),
(2, 1, 2, 'Office Table', 'Modular executive desk with cable management', 5, 'Piece', 8000.00, 18.00, 0.00, 40000.00, 7200.00, 47200.00);

-- Seed Quotation 2 (ACCEPTED)
INSERT INTO quotations (id, business_id, customer_id, quotation_number, quotation_date, valid_until, status, subtotal, discount_type, discount_value, discount_amount, tax_amount, grand_total, notes, terms_and_conditions, created_at, updated_at)
VALUES (2, 1, 2, 'QT-2026-0002', DATE_SUB(CURDATE(), INTERVAL 5 DAY), DATE_ADD(CURDATE(), INTERVAL 10 DAY), 'ACCEPTED', 200000.00, 'PERCENTAGE', 10.00, 20000.00, 32400.00, 212400.00, 'Boardroom setup for Design Studio One.', 'Standard terms apply.', NOW(), NOW());

INSERT INTO quotation_items (id, quotation_id, product_id, item_name, description, quantity, unit, unit_price, tax_percentage, discount, line_subtotal, line_tax, line_total)
VALUES 
(3, 2, 5, 'Conference Table 8-Seater', 'Large boardroom wooden conference table', 2, 'Piece', 25000.00, 18.00, 0.00, 50000.00, 9000.00, 59000.00),
(4, 2, 3, 'Ergonomic Chair', 'Premium ergonomic chair with synchronized tilt', 20, 'Piece', 7500.00, 18.00, 0.00, 150000.00, 27000.00, 177000.00);

-- Seed Quotation 3 (DRAFT)
INSERT INTO quotations (id, business_id, customer_id, quotation_number, quotation_date, valid_until, status, subtotal, discount_type, discount_value, discount_amount, tax_amount, grand_total, notes, terms_and_conditions, created_at, updated_at)
VALUES (3, 1, 3, 'QT-2026-0003', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 15 DAY), 'DRAFT', 20000.00, 'PERCENTAGE', 0.00, 0.00, 3600.00, 23600.00, 'Draft quotation for tech vision stand order.', 'Standard terms apply.', NOW(), NOW());

INSERT INTO quotation_items (id, quotation_id, product_id, item_name, description, quantity, unit, unit_price, tax_percentage, discount, line_subtotal, line_tax, line_total)
VALUES 
(5, 3, 4, 'Laptop Stand', 'Aluminum adjustable laptop riser stand', 10, 'Piece', 2000.00, 18.00, 0.00, 20000.00, 3600.00, 23600.00);

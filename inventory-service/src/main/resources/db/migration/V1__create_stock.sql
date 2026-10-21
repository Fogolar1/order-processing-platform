CREATE TABLE stock (product_id VARCHAR(120) PRIMARY KEY, available INTEGER NOT NULL CHECK (available >= 0));
INSERT INTO stock(product_id, available) VALUES ('SKU-001', 100), ('SKU-002', 25) ON CONFLICT DO NOTHING;

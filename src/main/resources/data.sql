-- data.sql
-- Insertion de quelques produits de test
INSERT INTO product (id, name, description, price, sku) VALUES 
(1, 'T-shirt Homme', 'T-shirt en coton bio', 19.99, 'TSH-001'),
(2, 'Jeans Slim Femme', 'Jeans slim elastique', 49.99, 'JSF-002'),
(3, 'Veste en Cuir', 'Veste en cuir véritable', 199.99, 'VEC-003');

-- Insertion de stocks pour différents magasins
INSERT INTO stock (id, product_id, store_id, quantity) VALUES 
(1, 1, 'STORE-001', 50),  -- 50 T-shirts dans le magasin 1
(2, 1, 'STORE-002', 30),  -- 30 T-shirts dans le magasin 2
(3, 2, 'STORE-001', 20),  -- 20 Jeans dans le magasin 1
(4, 2, 'STORE-002', 25),  -- 25 Jeans dans le magasin 2
(5, 3, 'STORE-001', 10),  -- 10 Vestes dans le magasin 1
(6, 3, 'STORE-002', 5);   -- 5 Vestes dans le magasin 2

-- Insertion d'une réservation active
INSERT INTO reservation (id, product_id, store_id, quantity, created_at, expires_at, reservation_code, active)
VALUES (1, 3, 'STORE-001', 2, CURRENT_TIMESTAMP(), DATEADD('DAY', 1, CURRENT_TIMESTAMP()), 'RES-20250215-001', true);

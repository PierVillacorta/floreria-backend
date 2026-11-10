-- =====================================================
-- FLORERIA - esquema + datos iniciales (PostgreSQL)
-- Docker ejecuta este archivo UNA sola vez: cuando el volumen de datos
-- se crea por primera vez.
-- =====================================================
 
-- Catalogo de tipos de usuario
CREATE TABLE user_types (
    user_type_id  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    type_name     VARCHAR(30) NOT NULL UNIQUE
);
 
-- Usuarios 
CREATE TABLE users (
    user_id        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    full_name      VARCHAR(100) NOT NULL,
    last_name      VARCHAR(100),
    run            VARCHAR(9),
    email          VARCHAR(150) NOT NULL UNIQUE,
    password_hash  VARCHAR(255) NOT NULL,
    user_type_id   BIGINT NOT NULL REFERENCES user_types (user_type_id),
    account_status VARCHAR(10) NOT NULL DEFAULT 'ACTIVE'
                       CHECK (account_status IN ('ACTIVE', 'INACTIVE')),
    birth_date     DATE,
    address        VARCHAR(300),
    region         VARCHAR(100),
    commune        VARCHAR(100),
    phone          VARCHAR(15),
    created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
 
-- 3) Categorias
CREATE TABLE categories (
    category_id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    category_name  VARCHAR(100) NOT NULL UNIQUE
);
 
--  Productos 
CREATE TABLE products (
    product_id     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    product_code   VARCHAR(50) NOT NULL UNIQUE CHECK (char_length(product_code) >= 3),
    product_name   VARCHAR(100) NOT NULL,
    image_url      VARCHAR(500),
    stock          INTEGER NOT NULL DEFAULT 0 CHECK (stock >= 0),
    price          NUMERIC(10,2) NOT NULL CHECK (price >= 0),
    description    VARCHAR(500),
    category_id    BIGINT NOT NULL REFERENCES categories (category_id),
    critical_stock INTEGER NOT NULL DEFAULT 0 CHECK (critical_stock >= 0),
    created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
 
--  Items de carrito (hoy el carrito vive en el frontend, queda para uso futuro)
CREATE TABLE cart_items (
    cart_item_id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id        BIGINT NOT NULL REFERENCES users (user_id) ON DELETE CASCADE,
    product_id     BIGINT NOT NULL REFERENCES products (product_id) ON DELETE CASCADE,
    quantity       INTEGER NOT NULL DEFAULT 1 CHECK (quantity > 0),
    UNIQUE (user_id, product_id)
);
 
-- =====================================================
-- DATOS INICIALES
-- =====================================================
 
INSERT INTO user_types (type_name) VALUES ('ADMIN'), ('SELLER'), ('CUSTOMER');
 
INSERT INTO categories (category_name) VALUES
    ('Ramos'),
    ('Flores de temporada'),
    ('Flores exóticas'),
    ('Ramo de flores');
 
-- La categoria se resuelve por nombre (JOIN), asi no dependemos de ID fijos
INSERT INTO products (product_code, product_name, image_url, stock, price, description, category_id, critical_stock)
SELECT v.product_code, v.product_name, v.image_url, v.stock, v.price, v.description, c.category_id, v.critical_stock
FROM (VALUES
    ('ROS-001', 'Rosas', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS0IXOwqyCQq7S5BzPSwS--TD8KoesOwZCiy3BlHHxinUMq7BPu74-IcN60&s=10', 20, 25000, 'Hermoso ramo de Rosas', 'Ramos', 5),
    ('GIR-001', 'Girasoles', 'https://floresydetalles.cl/wp-content/uploads/2023/09/ramos-de-flores-con-girasoles-removebg-preview.png', 15, 20000, 'Hermoso ramo de Girasoles', 'Ramos', 5),
    ('TUL-001', 'Tulipanes', 'https://www.wineflowers.com/images/tulipanigialli_grande.jpg', 30, 80000, 'Hermoso ramo de Tulipanes', 'Flores de temporada', 5),
    ('ORQ-001', 'Orquídeas', 'https://floresyregalosmx.com/wp-content/uploads/Facetune_26-04-2025-11-00-17-3-scaled.jpeg', 2, 100000, 'Hermoso ramo de Orquídeas', 'Flores exóticas', 3),
    ('MAR-001', 'Margarita', 'https://www.exoticasflores.cl/wp-content/uploads/2022/07/Flores-condolencia-2203-1.jpg.webp', 50, 10000, 'Hermoso ramo de Margaritas', 'Flores de temporada', 10),
    ('LIR-001', 'Lirios', 'https://floresparis.cl/wp-content/uploads/2021/07/LILIRO.png', 18, 35000, 'Hermoso ramo de Lirios', 'Flores exóticas', 5),
    ('RGI-001', 'Rosas y girasoles', 'https://anneflowers.cl/wp-content/uploads/2025/04/img_0981.jpeg', 15, 25000, 'Hermoso ramo de rosas y girasoles', 'Ramo de flores', 5),
    ('RTU-001', 'Rosas y tulipanes', 'https://floresrosadona.cl/wp-content/uploads/2026/01/photoroom_20260126_121255.jpeg', 10, 45000, 'Hermoso ramo de rosas y tulipanes', 'Ramo de flores', 3),
    ('GTU-001', 'Girasoles y tulipanes', 'https://tiendadeleite.cl/wp-content/uploads/2024/03/IMG-20240327-WA0027.jpg', 14, 45000, 'Hermoso ramo de girasoles y tulipanes', 'Ramo de flores', 3),
    ('ROR-001', 'Rosa y Orquídeas', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSL3dPwqqUftM65J4bGxud-Nd9v4ADm6J3oRT-fIAF5P8MDw5d7_Sr8lR0X&s=10', 14, 80000, 'Hermoso ramo de rosas y Orquídeas', 'Ramo de flores', 3),
    ('MGI-001', 'Margaritas y girasoles', 'https://florasantiago.cl/cdn/shop/files/zoonramodemargaritasy5girasoles_73128c3d-a1e7-4ca1-963f-0d003931aa4d.jpg?v=1741186258', 25, 20000, 'Hermoso ramo de margaritas y girasoles', 'Ramo de flores', 5),
    ('LRO-001', 'Lirio y rosas', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSdCR76wx1QyQs2Va1IzwFu8-php7eeFs-uC2n_uuGv93IBlJGKTLjMfM4&s=10', 8, 55000, 'Hermoso ramo de Lirios y rosas', 'Ramo de flores', 3)
) AS v (product_code, product_name, image_url, stock, price, description, category_name, critical_stock)
JOIN categories c ON c.category_name = v.category_name;
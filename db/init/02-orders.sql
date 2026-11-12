-- Boleta: una compra de un usuario con los datos de entrega que ingreso al pagar
CREATE TABLE orders (
    order_id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id          BIGINT NOT NULL REFERENCES users (user_id),
    customer_name    VARCHAR(100) NOT NULL,
    customer_email   VARCHAR(150) NOT NULL,
    delivery_address VARCHAR(300) NOT NULL,
    delivery_region  VARCHAR(100) NOT NULL,
    delivery_commune VARCHAR(100) NOT NULL,
    delivery_notes   VARCHAR(500),
    total            NUMERIC(12,2) NOT NULL CHECK (total >= 0),
    status           VARCHAR(10) NOT NULL CHECK (status IN ('PAID', 'FAILED')),
    created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Detalle de boleta: un producto dentro de una compra
-- product_name y unit_price son una COPIA
-- si despues cambia su nombre o su precio  la boleta antigua no se altera
CREATE TABLE order_items (
    order_item_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    order_id      BIGINT NOT NULL REFERENCES orders (order_id) ON DELETE CASCADE,
    product_id    BIGINT NOT NULL REFERENCES products (product_id),
    product_name  VARCHAR(100) NOT NULL,
    unit_price    NUMERIC(10,2) NOT NULL CHECK (unit_price >= 0),
    quantity      INTEGER NOT NULL CHECK (quantity > 0),
    UNIQUE (order_id, product_id)
);

CREATE INDEX idx_orders_user ON orders (user_id);
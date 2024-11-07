CREATE TABLE tb_product (
    prd_id SERIAL primary key,
    prd_name VARCHAR(255),
    prd_image VARCHAR(255),
    prd_manufacturer VARCHAR(70),
    prd_price DECIMAL,
    prd_stock_quantity INT
)
CREATE TABLE tb_address(
    add_id SERIAL primary key,
    add_street VARCHAR(90),
    add_number INT,
    add_city VARCHAR(60),
    add_state VARCHAR(30),
    add_zipcode VARCHAR(9),
    add_costumer INT
)
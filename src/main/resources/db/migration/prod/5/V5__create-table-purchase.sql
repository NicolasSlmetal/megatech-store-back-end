CREATE TABLE tb_purchase (
     pre_id SERIAL primary key,
     pre_date TIMESTAMP,
     pre_customer_id INT
);

CREATE TABLE tb_purchase_product (
     tpp_pre_id INT,
     tpp_prd_id INT,
     tpp_quantity INT,
     CONSTRAINT pk_purchase_product PRIMARY KEY(tpp_pre_id, tpp_prd_id)
);

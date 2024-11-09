ALTER TABLE tb_costumer
    RENAME TO tb_customer;

ALTER TABLE tb_customer
    DROP CONSTRAINT fk_costumer_user;

ALTER TABLE tb_customer
    DROP COLUMN cst_user_id;

ALTER TABLE tb_customer
    ALTER COLUMN cst_id TYPE INT;

ALTER TABLE tb_customer
    ADD CONSTRAINT fk_customer_user FOREIGN KEY(cst_id) REFERENCES tb_user (user_id);

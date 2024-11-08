ALTER TABLE tb_costumer
    RENAME COLUMN cst_user TO cst_user_id;

ALTER TABLE tb_costumer
    ADD CONSTRAINT fk_costumer_user FOREIGN KEY (cst_user_id) REFERENCES tb_user (user_id);
ALTER TABLE tb_address
    RENAME COLUMN add_costumer TO add_costumer_id;

ALTER TABLE tb_address
    ADD CONSTRAINT fk_address_user FOREIGN KEY (add_costumer_id) REFERENCES tb_costumer (cst_id)
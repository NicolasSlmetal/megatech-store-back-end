ALTER TABLE tb_purchase
    ADD CONSTRAINT fk_purchase_customer FOREIGN KEY(pre_customer_id) REFERENCES tb_customer(cst_id);

ALTER TABLE tb_purchase_product
    ADD CONSTRAINT fk_tpp_purchase FOREIGN KEY (tpp_pre_id) REFERENCES tb_purchase (pre_id);

ALTER TABLE tb_purchase_product
    ADD CONSTRAINT fk_tpp_product FOREIGN KEY (tpp_prd_id) REFERENCES tb_product (prd_id);
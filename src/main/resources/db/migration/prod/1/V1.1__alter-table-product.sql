ALTER TABLE tb_product
    ADD COLUMN prd_entry_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE tb_product
    ADD CONSTRAINT unique_name_image UNIQUE (prd_name, prd_image);


ALTER TABLE tb_product
    ALTER COLUMN prd_name SET NOT NULL,
    ALTER COLUMN prd_image SET NOT NULL,
    ALTER COLUMN prd_manufacturer SET NOT NULL,
    ALTER COLUMN prd_price SET NOT NULL,
    ALTER COLUMN prd_stock_quantity SET NOT NULL;

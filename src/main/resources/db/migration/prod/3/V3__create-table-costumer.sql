CREATE TABLE tb_costumer(
    cst_id SERIAL primary key,
    cst_name VARCHAR(70),
    cst_cpf VARCHAR(11),
    cst_user INT,
    cst_registration_date DATE
)
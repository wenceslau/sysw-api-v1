--Define a coluna para not null

ALTER TABLE tb_core_sector ALTER COLUMN cod_business_unit_fk TYPE BIGINT, ALTER COLUMN cod_business_unit_fk SET NOT NULL;

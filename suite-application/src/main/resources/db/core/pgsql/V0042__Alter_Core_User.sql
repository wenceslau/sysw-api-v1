ALTER TABLE tb_core_user ADD ind_locked BOOLEAN NULL;
ALTER TABLE tb_core_user ADD val_count_locked INT NULL;
ALTER TABLE tb_core_user ADD user_hash VARCHAR(500) NULL;
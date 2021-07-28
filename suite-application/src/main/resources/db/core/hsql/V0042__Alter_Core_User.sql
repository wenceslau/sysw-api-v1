ALTER TABLE tb_core_user ADD COLUMN ind_locked BIT NULL;
ALTER TABLE tb_core_user ADD COLUMN val_count_locked INT NULL;
ALTER TABLE tb_core_user ADD COLUMN user_hash VARCHAR(500) NULL;
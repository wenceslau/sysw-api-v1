ALTER TABLE tb_core_application ADD nam_module_source VARCHAR(40) NULL;

ALTER TABLE tb_core_application ADD val_licence VARCHAR(500) NULL;

ALTER TABLE tb_core_application RENAME COLUMN val_header_logo TO val_image;

ALTER TABLE tb_core_application DROP COLUMN val_header_login;
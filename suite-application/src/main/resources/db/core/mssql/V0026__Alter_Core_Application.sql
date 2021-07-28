ALTER TABLE tb_core_application ADD nam_module_source VARCHAR(40) NULL;
ALTER TABLE tb_core_application ADD val_licence VARCHAR(500) NULL;
EXEC SP_RENAME 'tb_core_application.val_header_logo' , 'val_image', 'COLUMN';
ALTER TABLE tb_core_application DROP COLUMN val_header_login;
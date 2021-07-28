CREATE TABLE tb_core_permission(
	cod_record BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY,
	val_type INT NOT NULL,
	nam_application VARCHAR(100) NOT NULL,
	nam_module VARCHAR(100) NOT NULL,
	val_role VARCHAR(100) NOT NULL,
	val_key VARCHAR(100) NOT NULL,
	des_permission VARCHAR(250) NOT NULL,
	val_predecessor_permission VARCHAR(100) NULL,
	val_component VARCHAR(100) NULL,
	val_router VARCHAR(100) NULL,
	val_label  VARCHAR(100) NULL,
	val_root VARCHAR(100) NULL,
	val_icon VARCHAR(100) NULL,
	ind_toolbar BIT NULL,
	ind_root_toolbar BIT NULL,
	val_sequence INT NULL,
	sts_record BIT NOT NULL
);
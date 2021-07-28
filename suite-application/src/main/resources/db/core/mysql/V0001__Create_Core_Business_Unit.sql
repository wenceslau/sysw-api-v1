CREATE TABLE tb_core_business_unit (
	cod_record BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY,
	nam_business_unit VARCHAR(100) NOT NULL,
	des_business_unit VARCHAR(250) NOT NULL,
	val_applications VARCHAR(100) NOT NULL,
	val_unique_id VARCHAR(100) NOT NULL,
	val_license VARCHAR(500) NULL,
	val_image VARCHAR(100) NULL,
	ind_sector_required_db BIT NULL,
	sts_record BIT NOT NULL
);

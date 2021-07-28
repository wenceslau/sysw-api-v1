CREATE TABLE tb_core_application (
	cod_record BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY,
	nam_application VARCHAR(100) NOT NULL,
	val_display_name VARCHAR(250) NOT NULL,
	val_main_color VARCHAR(100) NOT NULL,
	val_header_logo VARCHAR(100) NOT NULL,
	val_header_login VARCHAR(100) NULL,
	ind_main BIT NULL,
	sts_record BIT NOT NULL
);

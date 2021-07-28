CREATE TABLE tb_core_notify (
	cod_record BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY,
	val_message VARCHAR(2000) NOT NULL,
	cod_user BIGINT NOT NULL,
	val_user_name VARCHAR(100) NOT NULL,
	dtt_notify DATETIME NOT NULL,
	cod_sector BIGINT NULL,
	val_sector_name VARCHAR(100) NULL
);
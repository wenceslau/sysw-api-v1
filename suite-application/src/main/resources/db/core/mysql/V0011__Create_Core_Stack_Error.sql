CREATE TABLE tb_core_stack_error (
	cod_record BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY,
	dtt_error DATETIME NOT NULL,
	val_message VARCHAR(500) NULL,
	val_causes VARCHAR(2000) NULL,
	val_stack VARCHAR(4000) NULL,
	val_fullStack TEXT NULL,
	cod_user BIGINT NOT NULL,
	cod_sector BIGINT NOT NULL
);
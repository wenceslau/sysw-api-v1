CREATE TABLE tb_core_stack_error (
	cod_record BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT pk_tbCoreStackError PRIMARY KEY,
	dtt_error DATETIME NOT NULL,
	val_message VARCHAR(500) NULL,
	val_causes VARCHAR(2000) NULL,
	val_stack VARCHAR(4000) NULL,
	val_fullStack TEXT NULL,
	cod_user BIGINT NOT NULL,
	cod_sector BIGINT NOT NULL
);
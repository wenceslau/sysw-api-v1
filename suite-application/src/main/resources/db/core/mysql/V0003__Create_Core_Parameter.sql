CREATE TABLE tb_core_parameter (
	cod_record BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY,
	nam_group VARCHAR(100) NOT NULL,
	val_key VARCHAR(100) NOT NULL,
	des_parameter VARCHAR(250),
	val_parameter VARCHAR(250) NOT NULL,
	sts_record BIT NOT NULL
);
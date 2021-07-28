CREATE TABLE tb_core_user_request (
	cod_record BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY,
	dtt_request DATETIME NOT NULL,
	val_address VARCHAR(50) NOT NULL,
	val_host VARCHAR(50) NULL,
	val_user_agent VARCHAR(500) NULL,
	val_app_agent VARCHAR(50) NULL,
	val_verb VARCHAR(10) NULL,
	val_path VARCHAR(125) NULL,
	val_url VARCHAR(250) NULL
);
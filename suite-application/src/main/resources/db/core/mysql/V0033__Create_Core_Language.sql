CREATE TABLE tb_core_language(
	cod_record BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY,
	val_key VARCHAR(100) NOT NULL,
	val_portugues VARCHAR(900) NULL,
	val_english VARCHAR(900) NULL,
	val_spanish VARCHAR(900) NULL,
	val_description VARCHAR(200) NULL,
	dtt_record DATETIME NOT NULL,
	dtt_update DATETIME NULL,
	sts_record BIT NOT NULL
);

CREATE UNIQUE INDEX idx_unique_tb_core_language ON tb_core_language (val_key)
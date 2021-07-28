CREATE TABLE tb_core_initializer(
	cod_record BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT pk_tbCoreInitializer PRIMARY KEY,
	dat_initializer DATETIME NOT NULL,
	nam_initializer VARCHAR(50) NOT NULL,
	des_initializer VARCHAR(1000) NOT NULL
);


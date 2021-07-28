CREATE TABLE tb_core_business_unit_parameter (
	cod_record BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT pk_tbCoreBusinessUnitParameter PRIMARY KEY,
	val_key VARCHAR(100) NOT NULL,
	des_parameter VARCHAR(500),
	val_parameter VARCHAR(250) NOT NULL,
	sts_record BIT NOT NULL,
	cod_business_unit_fk BIGINT NOT NULL,
	CONSTRAINT fk_tbCoreBusinessUnitParameter_tbCoreBusinessUnit FOREIGN KEY (cod_business_unit_fk) REFERENCES tb_core_business_unit(cod_record)
);
CREATE TABLE tb_core_profile (
	cod_record BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT pk_tbCoreProfile PRIMARY KEY,
	nam_profile VARCHAR(100) NOT NULL,
	typ_profile INT NOT NULL,
	sts_record BIT NOT NULL	,
	cod_business_unit_fk  BIGINT NULL,
	CONSTRAINT fk_tbCoreProfile_tbCoreBusinessUnit FOREIGN KEY (cod_business_unit_fk) REFERENCES tb_core_business_unit(cod_record)
);
GO

CREATE TABLE tb_core_profile_permisions (
	cod_profile BIGINT NOT NULL,
	cod_permission BIGINT NOT NULL,
	PRIMARY KEY (cod_profile, cod_permission),
	FOREIGN KEY (cod_profile) REFERENCES tb_core_profile(cod_record),
	FOREIGN KEY (cod_permission) REFERENCES tb_core_permission(cod_record)
);
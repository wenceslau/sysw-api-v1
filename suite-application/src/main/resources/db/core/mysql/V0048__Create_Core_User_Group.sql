
CREATE TABLE tb_core_user_group (
	cod_record BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY,
	nam_user_group VARCHAR(100) NOT NULL,
	des_user_group VARCHAR(100) NOT NULL,	
	sts_record BIT NOT NULL,
	cod_business_unit_fk BIGINT NOT NULL,
	CONSTRAINT fk_tbCoreUserGroup_tbCoreBusinessUnit FOREIGN KEY (cod_business_unit_fk) REFERENCES tb_core_business_unit(cod_record)
);


CREATE TABLE tb_core_user_group_user (
	cod_record BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY,
	cod_user_group BIGINT NOT NULL,
	cod_user BIGINT NOT NULL,
	FOREIGN KEY (cod_user_group) REFERENCES tb_core_user_group(cod_record),
	FOREIGN KEY (cod_user) REFERENCES tb_core_user(cod_record)
);
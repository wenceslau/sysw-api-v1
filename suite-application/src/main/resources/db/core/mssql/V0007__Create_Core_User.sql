CREATE TABLE tb_core_user (
	cod_record BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT pk_tbCoreUser PRIMARY KEY,
	val_name VARCHAR(100) NOT NULL,
	val_username VARCHAR(100) NOT NULL,
	val_email VARCHAR(100) NOT NULL,
	val_password VARCHAR(100) NOT NULL,
	ind_firstAccess BIT,
	val_hash INT NULL,
	ind_receive_notify BIT NULL,
	ind_view_notify BIT NULL,
	ind_send_notify BIT NULL,
	sts_record BIT NOT NULL,
	cod_profile_fk BIGINT NOT NULL,
	cod_business_unit_fk BIGINT NOT NULL,
	CONSTRAINT fk_tbCoreUser_tbCoreProfile FOREIGN KEY (cod_profile_fk) REFERENCES tb_core_profile(cod_record),
	CONSTRAINT fk_tbCoreUser_tbCoreBusinessUnit FOREIGN KEY (cod_business_unit_fk) REFERENCES tb_core_business_unit(cod_record)
);
GO

CREATE TABLE tb_core_user_sector (
	cod_record BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT pk_tbCoreUserSector PRIMARY KEY,
	cod_user BIGINT NOT NULL,
	cod_sector BIGINT NOT NULL,
	FOREIGN KEY (cod_user) REFERENCES tb_core_user(cod_record),
	FOREIGN KEY (cod_sector) REFERENCES tb_core_sector(cod_record)
 );
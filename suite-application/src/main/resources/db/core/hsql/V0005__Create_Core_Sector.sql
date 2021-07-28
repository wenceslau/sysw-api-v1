CREATE TABLE tb_core_sector (
	cod_record BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY,
	nam_sector VARCHAR(100) NOT NULL,
	des_sector VARCHAR(250) NOT NULL,
	val_unique_id VARCHAR(100) NOT NULL,
	val_image VARCHAR(100) NULL,
	nam_external_database VARCHAR(100) NULL,
	dtt_create_database DATETIME NULL,
	sts_record BIT NOT NULL,
	cod_data_service_fk BIGINT NULL,
	cod_business_unit_fk BIGINT NULL,
	CONSTRAINT `fk_tbCoreSector_tbCoreDataService` FOREIGN KEY (cod_data_service_fk) REFERENCES tb_core_data_service(cod_record),
	CONSTRAINT `fk_tbCoreSector_tbCoreBusinessUnit` FOREIGN KEY (cod_business_unit_fk) REFERENCES tb_core_business_unit(cod_record)
 );

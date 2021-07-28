CREATE TABLE tb_job_task (
	cod_record BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY,
	nam_task VARCHAR(100) NOT NULL,
	des_task VARCHAR(100) NOT NULL,
	val_frequency VARCHAR(100) NOT NULL,
	val_scheduler VARCHAR(100) NOT NULL,
	val_state VARCHAR(100) NOT NULL,
	val_ben_run VARCHAR(100) NOT NULL,
	dtt_last_run DATETIME NULL,
	ind_notify BIT NOT NULL,
	sts_record BIT NOT NULL,
	cod_business_unit_fk BIGINT NOT NULL	
);
	
CREATE TABLE tb_job_task_property (
	cod_record BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY,
	nam_property VARCHAR(100) NOT NULL,
	val_property VARCHAR(250) NOT NULL,
	des_property VARCHAR(250) NOT NULL,
	sts_record BIT NOT NULL,
	cod_task_fk  BIGINT NOT NULL,
	CONSTRAINT fk_tbTaskProperty_tbJobTask FOREIGN KEY (cod_task_fk) REFERENCES tb_job_task(cod_record)
);

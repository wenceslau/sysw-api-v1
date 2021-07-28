CREATE TABLE tb_job_user_action (
	cod_record BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT pk_tbJobUserAction PRIMARY KEY,
	nam_action VARCHAR(100) NOT NULL,
	nam_object VARCHAR(100) NOT NULL,
	idt_record BIGINT NOT NULL,
	val_record TEXT NOT NULL,
	dtt_record DATETIME NOT NULL,
	usr_record_fk BIGINT NULL,
	cod_sector_fk BIGINT NULL
);

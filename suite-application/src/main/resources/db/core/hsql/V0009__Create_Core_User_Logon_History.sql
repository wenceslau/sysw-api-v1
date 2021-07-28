CREATE TABLE tb_core_user_logon_history (
	cod_record BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY,
	val_ip_address VARCHAR(100) NOT NULL,
	val_browser VARCHAR(100),
	val_user_logon VARCHAR(100) NOT NULL,
	val_status_logon VARCHAR(100) NOT NULL,
	dat_logon DATE NOT NULL,
	dtt_record DATETIME NOT NULL,
	usr_record_fk BIGINT,
	CONSTRAINT `fk_tbCoreUserLogonHistory_tbCoreUser` FOREIGN KEY (usr_record_fk) REFERENCES tb_core_user(cod_record)
);

--Cria a coluna cod_application e define a FK

ALTER TABLE tb_core_permission ADD cod_application_fk BIGINT NULL;

ALTER TABLE tb_core_permission ADD CONSTRAINT fk_tbCorePermission_tbCoreApplication
  FOREIGN KEY (cod_application_fk)  REFERENCES tb_core_application (cod_record);
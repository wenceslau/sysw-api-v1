--Cria a coluna cod_application e define a FK

ALTER TABLE tb_core_sector ADD cod_application_fk BIGINT NULL;

ALTER TABLE tb_core_sector ADD CONSTRAINT fk_tbCoreSector_tbCoreApplication
  FOREIGN KEY (cod_application_fk)  REFERENCES tb_core_application (cod_record);
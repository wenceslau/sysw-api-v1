--Tabela de relacionamento muitos pra muitos entre businessUnit e aplicacao

CREATE TABLE tb_core_business_unit_application (
	cod_business_unit BIGINT NOT NULL,
	cod_application BIGINT NOT NULL,
	PRIMARY KEY (cod_business_unit, cod_application),
	FOREIGN KEY (cod_business_unit) REFERENCES tb_core_business_unit(cod_record),
	FOREIGN KEY (cod_application) REFERENCES tb_core_application(cod_record)
);
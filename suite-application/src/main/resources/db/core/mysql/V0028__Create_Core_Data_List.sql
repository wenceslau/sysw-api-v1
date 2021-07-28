CREATE TABLE tb_core_data_list(
	cod_record BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY,
	nam_list VARCHAR(50),
	sts_record BIT NOT NULL
);

CREATE TABLE tb_core_item_data_list(
	cod_record BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY,
	val_group VARCHAR(50),
	val_label_item VARCHAR(50),
	val_value_item VARCHAR(50),
	sts_record BIT NOT NULL,
	cod_data_list_fk BIGINT NOT NULL,	
	CONSTRAINT fk_tbCoreItemDataList_tbCoreDataList FOREIGN KEY (cod_data_list_fk) REFERENCES tb_core_data_list(cod_record)
);
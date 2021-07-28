--Adiciona coluna required db no setor e remove ba BU
ALTER TABLE tb_core_sector ADD ind_required_db BOOLEAN NULL;
ALTER TABLE tb_core_business_unit DROP COLUMN ind_sector_required_db;

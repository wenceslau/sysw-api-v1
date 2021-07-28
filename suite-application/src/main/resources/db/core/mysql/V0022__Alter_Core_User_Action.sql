--Nao ha necessidade de fk entre as tabelas, o user action dos outros modulos nao possui fk pois sao bases diferentes

ALTER TABLE tb_core_user_action DROP FOREIGN KEY fk_tbCoreUserAction_tbCoreUser;
ALTER TABLE tb_core_user_action DROP INDEX fk_tbCoreUserAction_tbCoreUser ;


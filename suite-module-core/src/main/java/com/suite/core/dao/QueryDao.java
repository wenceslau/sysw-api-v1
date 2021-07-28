package com.suite.core.dao;

import org.springframework.stereotype.Service;

import com.suite.app.base.Enum.DataType;
import com.suite.core.dto.ColumnDTO;
import com.suite.core.dto.DatabaseDTO;
import com.suite.core.dto.TableDTO;

/**
 * Classe que prover as query de execucao na classe DataDao
 * @author 4931pc_neto
 *
 */
@Service
public class QueryDao extends CoreDao {

	public String queryExistDataBase(String provider) {

		switch (provider) {

		case "MSSQL":
			return "SELECT name AS 'database' FROM sys.databases";
		case "ORACLE":
			return "";
		case "MYSQL":
			return "SELECT SCHEMA_NAME AS 'database' FROM INFORMATION_SCHEMA.SCHEMATA";
		case "PGSQL":
			return "SELECT datname AS database FROM pg_database;";
		case "ODBC":
			return "";
		default:
			return "";

		}

	}

	public String getQueryCreateDatabase(String provider, String databaseName) {

		switch (provider) {

		case "MSSQL":
		case "MYSQL":
			return "CREATE DATABASE " + databaseName;
		case "PGSQL":
			return "CREATE DATABASE " + databaseName;
		case "ORACLE":
			return "";
		case "ODBC":
			return "";
		default:
			return "";

		}

	}

	// Table

	public String queryListTable(DatabaseDTO database) {

		String databaseName = database.getName();
		String provider = database.getValueProperty("PROVIDER");

		switch (provider) {

		case "MSSQL":
			return "SELECT name AS 'table' FROM SYSOBJECTS WHERE xtype in('U','ET');";
		case "ORACLE":
			return "";
		case "MYSQL":
			return "SELECT table_name AS 'table' FROM information_schema.tables WHERE table_type = 'BASE TABLE' AND table_schema = '"
					+ databaseName + "';";
		case "PGSQL":
			return "SELECT table_name as table FROM information_schema.tables WHERE table_catalog = '" + databaseName
					+ "' and table_schema = 'public'";
		case "ODBC":
			return "";
		default:
			return "";

		}

	}

	public String queryListTableUniqueAndNumericPK(DatabaseDTO database) {

		String provider = database.getValueProperty("PROVIDER");

		switch (provider) {

		case "MSSQL":
			return "\r\n" +
					"SELECT \r\n" +
					"	Col.Constraint_Name AS constraintName, \r\n" +
					"	CASE WHEN Col.CONSTRAINT_SCHEMA != 'dbo' \r\n" +
					"	THEN Col.CONSTRAINT_SCHEMA + '.' + Col.Table_Name \r\n" +
					"	ELSE Col.Table_Name \r\n" +
					"	END AS tableName \r\n" +
					"FROM \r\n" +
					"	INFORMATION_SCHEMA.TABLE_CONSTRAINTS Tab, \r\n" +
					"	INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE Col, \r\n" +
					"	INFORMATION_SCHEMA.COLUMNS C \r\n" +
					"WHERE \r\n" +
					"	Col.Constraint_Name = Tab.Constraint_Name\r\n" +
					"	AND Col.Table_Name = Tab.Table_Name\r\n" +
					"	AND LOWER(Constraint_Type) = 'primary key' \r\n" +
					"	AND C.COLUMN_NAME = COL.COLUMN_NAME \r\n" +
					"	AND C.TABLE_NAME = COL.TABLE_NAME\r\n" +
					"	AND LOWER(C.DATA_TYPE) IN ('int', 'bigint') \r\n" +
					"GROUP BY col. CONSTRAINT_SCHEMA, Col.Constraint_Name, Col.Table_Name \r\n" +
					"HAVING COUNT(*) = 1";
		case "ORACLE":
			return "";
		case "MYSQL":
			return "\r\n" +
					"SELECT \r\n" +
					"	sta.index_name, \r\n" +
					"	tab.table_name \r\n" +
					"FROM \r\n" +
					"	INFORMATION_SCHEMA.tables tab, \r\n" +
					"	INFORMATION_SCHEMA.statistics sta, \r\n" +
					"	INFORMATION_SCHEMA.COLUMNS col \r\n" +
					"WHERE \r\n" +
					"	sta.table_schema = tab.table_schema \r\n" +
					"	AND sta.table_name = tab.table_name \r\n" +
					"	AND LOWER(sta.index_name) = 'primary' \r\n" +
					"	AND col.COLUMN_NAME = sta.COLUMN_NAME \r\n" +
					"	AND col.TABLE_NAME = sta.TABLE_NAME \r\n" +
					"	AND LOWER(col.DATA_TYPE) IN ('int', 'bigint') \r\n" +
					"	AND LOWER(tab.table_schema) = '" + database.getName().toLowerCase() + "' \r\n" +
					"GROUP BY sta.index_name, tab.table_name \r\n";
		// "HAVING COUNT(*) = 1";
		case "PGSQL":
			return "\r\n" +
					"SELECT \r\n" +
					"	Col.Constraint_Name AS constraintName, \r\n" +
					"	CASE WHEN Col.CONSTRAINT_SCHEMA != 'public' \r\n" +
					"	THEN Col.CONSTRAINT_SCHEMA || '.' || Col.Table_Name \r\n" +
					"	ELSE Col.Table_Name \r\n" +
					"	END AS tableName \r\n" +
					"FROM \r\n" +
					"	INFORMATION_SCHEMA.TABLE_CONSTRAINTS Tab, \r\n" +
					"	INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE Col, \r\n" +
					"	INFORMATION_SCHEMA.COLUMNS C \r\n" +
					"WHERE \r\n" +
					"	Col.Constraint_Name = Tab.Constraint_Name \r\n" +
					"	AND Col.Table_Name = Tab.Table_Name\r\n" +
					"	AND LOWER(Constraint_Type) = 'primary key' \r\n" +
					"	AND C.COLUMN_NAME = COL.COLUMN_NAME \r\n" +
					"	AND C.TABLE_NAME = COL.TABLE_NAME\r\n" +
					"	AND LOWER(C.DATA_TYPE) IN ('int', 'bigint') \r\n" +
					"GROUP BY col. CONSTRAINT_SCHEMA, Col.Constraint_Name, Col.Table_Name \r\n" +
					"HAVING COUNT(*) = 1";
		case "ODBC":
			return "";
		default:
			return "";

		}

	}

	public String queryColumnTablePK(TableDTO table) {

		String tableName = table.getName();
		String provider = table.getDatabase().getValueProperty("PROVIDER");

		switch (provider) {

		case "MSSQL":
		case "PGSQL":

			// TODO: COLOCAR O SELECT PARA SEPARA O SCHEMA, PQ PODE TER 2 TABELAS COM O MEMSO NOME EM ESQUEMA DIFERENTE
			int idx = tableName.indexOf('.');
			if (idx > 0)
				tableName = tableName.substring(idx + 1, tableName.length());

			return "\r\nSELECT  \r\n" +
					"	Col.COLUMN_NAME, \r\n" +
					"	C.DATA_TYPE \r\n" +
					"FROM  \r\n" +
					"    INFORMATION_SCHEMA.TABLE_CONSTRAINTS Tab, \r\n" +
					"    INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE Col, \r\n" +
					"    INFORMATION_SCHEMA.COLUMNS C \r\n" +
					"WHERE  \r\n" +
					"    Col.Constraint_Name = Tab.Constraint_Name \r\n" +
					"    AND Col.Table_Name = Tab.Table_Name \r\n" +
					"	 AND C.COLUMN_NAME = COL.COLUMN_NAME \r\n" +
					"    AND LOWER(Constraint_Type) = 'primary key'\r\n" +
					"	 AND LOWER(C.DATA_TYPE) IN ('int', 'bigint') \r\n" +
					"	 AND LOWER(Col.Table_Name) =  '" + tableName + "'";
		case "MYSQL":
			return "\r\nSELECT \r\n" +
					"   STA.COLUMN_NAME, \r\n" +
					"	COL.DATA_TYPE \r\n" +
					"FROM \r\n" +
					"	information_schema.tables AS TAB \r\n" +
					"INNER JOIN information_schema.statistics AS STA \r\n" +
					"	ON STA.table_schema = TAB.table_schema \r\n" +
					"	AND STA.table_name = TAB.table_name \r\n" +
					"	AND LOWER(STA.index_name) = 'primary'\r\n" +
					"INNER JOIN INFORMATION_SCHEMA.COLUMNS AS COL \r\n" +
					"	ON COL.COLUMN_NAME = STA.COLUMN_NAME \r\n" +
					"WHERE 	LOWER(TAB.table_name) = '" + tableName + "' \r\n" +
					"AND 	TAB.table_type = 'BASE TABLE'" +
					"AND    LOWER(COL.DATA_TYPE) IN ('int', 'bigint')";

		case "ORACLE":
		case "ODBC":
			return "";
		default:
			return "";

		}

	}

	public String queryCreateTable(TableDTO table) {

		//@formatter:off
		DatabaseDTO database = table.getDatabase();	
		String provider = database.getValueProperty("PROVIDER");
		
		StringBuffer columns = new StringBuffer("");
		StringBuffer query = new StringBuffer("");
		
		String primaryKey = "ID";
		
		for (ColumnDTO column : table.getColumns()) {
			
			//Verifica se o nome da PK foi informada
			if (column.isPrimaryKey()) {
				primaryKey = column.getName();
				continue;
			}
			
			columns.append(",\r\n ");
			columns.append(column.getName());
			columns.append(" ");
			columns.append(getTypeColumn(database, column));
			columns.append(" ");
			columns.append((column.isAllowNull() ? "NULL " : "NOT NULL "));			
		}
		
		query.append("CREATE TABLE ");
		query.append(table.getName());
		query.append(" ");
			
		switch (provider) {
		case "MSSQL":
			String pkName = "pk_"+table.getName().toLowerCase().replace("_", "");
			query.append("("+primaryKey+" BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT ");
			query.append(pkName);
			query.append(" PRIMARY KEY ");		
			break;
		case "MYSQL":
			query.append("("+primaryKey+" BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY ");		
			break;
		case "ORACLE":
			break;
		case "PGSQL":
			query.append("("+primaryKey+" BIGINT PRIMARY KEY NOT NULL GENERATED BY DEFAULT AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ) ");		
			break;
		case "ODBC":
			break;
		default:
			break;
			
		}
		
		query.append(columns);
		query.append(" ");
		query.append(")");	
		
		return query.toString();
		//@formatter:on
	}

	public String queryRenameTable(TableDTO table, String oldName) {

		//@formatter:off
		DatabaseDTO database = table.getDatabase();	
		String provider = database.getValueProperty("PROVIDER");
					
		switch (provider) {
		case "MSSQL":
			return "EXEC sp_RENAME " + oldName + ", " + table + ";";
		case "MYSQL":
			return "RENAME TABLE " + oldName + " TO " + table + ";";
		case "ORACLE":
			return "";
		case "PGSQL":
			return "ALTER TABLE " + oldName + " RENAME TO " + table + ";";
		case "ODBC":
			return "";
		default:
			return "";
			
		}
		//@formatter:on
	}

	public String queryCreateIndex(TableDTO table, String prefixName, boolean unique) {

		DatabaseDTO database = table.getDatabase();
		String provider = database.getValueProperty("PROVIDER");

		String strUNique = "";
		if (unique)
			strUNique = "UNIQUE";

		String indexName = prefixName + "_" + table.getName();
		String strColumns = "";

		for (ColumnDTO column : table.getColumns()) 
			strColumns += column.getName() + ", ";

		if (!strColumns.isEmpty())
			strColumns = strColumns.substring(0, strColumns.length() - 2);

		switch (provider) {

		case "MSSQL":
		case "MYSQL":
		case "ORACLE":
		case "PGSQL":
			return "CREATE " + strUNique + " INDEX " + indexName + " ON " + table.getName() + " (" + strColumns + ");";
		case "ODBC":
			return "";
		default:
			return "";

		}

	}

	/**
	 * De-Para para o tipo de coluna no sistema e para cada banco de dados
	 *
	 * @param dataService
	 * @param type
	 * @return
	 */
	public String getTypeColumn(DatabaseDTO database, ColumnDTO column) {

		String provider = database.getValueProperty("PROVIDER");

		String typeString = "";

		if (column.getType().equals(DataType.STRING) || column.getType().equals(DataType.FILE) ) {

			int size = column.getSizeText();
			typeString = "varchar(" + size + ")";

			if (size <= 0)
				typeString = "varchar(500)";
			
			if (size > 8001)
				typeString = "TEXT";
			
		}

		switch (provider) {

		case "MSSQL":
			switch (column.getType()) {

			case STRING:
			case FILE:
				return typeString;
			case BIGDECIMAL:
				return "decimal(18,4)";
			case DATE:
				return "date";
			case TIME:
				return "time(7)";
			case DATETIME:
				return "datetime";
			case BOOLEAN:
				return "bit";
			case INTEGER:
				return "int";
			case LONG:
			case OBJECT:
			case EXTERNALOBJECT:
				return "bigint";
			default:
				break;

			}
		case "MYSQL":
			switch (column.getType()) {

			case STRING:
			case FILE:
				return typeString;
			case BIGDECIMAL:
				return "decimal(18,4)";
			case DATE:
				return "date";
			case TIME:
				return "time(6)";
			case DATETIME:
				return "datetime";
			case BOOLEAN:
				return "bit";
			case INTEGER:
				return "int";
			case LONG:
			case OBJECT:
			case EXTERNALOBJECT:
				return "bigint";
			default:
				break;

			}
		case "ORACLE":
			return "";
		case "PGSQL":
			switch (column.getType()) {

			case STRING:
			case FILE:
				return typeString;
			case BIGDECIMAL:
				return "numeric(18,4)";
			case DATE:
				return "date";
			case TIME:
				return "time";
			case DATETIME:
				return "timestamp(6)";
			case BOOLEAN:
				return "boolean";
			case INTEGER:
				return "integer";
			case LONG:
			case OBJECT:
			case EXTERNALOBJECT:
				return "bigint";
			default:
				break;

			}
		case "ODBC":
			return "";
		default:
			return "";

		}

	}

}

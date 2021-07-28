package com.suite.core.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.suite.app.util.Utils;
import com.suite.core.dto.DatabaseDTO;
import com.suite.core.dto.TableDTO;
import com.suite.core.model.DataService;

/**
 * Classe de manipulacao de dados usando JDBC
 * @author wenceslau.neto
 */
@Service
public class DataDao extends QueryDao {

	// @Autowired
	// private QueryDao queryDao;

	/*-----DATA BASE-----*/

	/**
	 * Test de conexao com o DataService
	 * @param dataService
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public String testConnection(DataService dataService) throws ClassNotFoundException, SQLException {

		Connection con = null;
		Statement sts = null;
		Object ret = "";

		try {

			DatabaseDTO databaseDTO = new DatabaseDTO(dataService);
			String provider = databaseDTO.getValueProperty("PROVIDER");
			String databaseName = databaseDTO.getValueProperty("NAME");
			String query = queryTestConnection(provider);

			con = getConnection(databaseDTO, databaseName);
			sts = con.createStatement();

			List<String> listNameCols = new ArrayList<>();

			try (ResultSet rs = executeResultSet(sts, query)) {
				
				ResultSetMetaData rsmd = rs.getMetaData();
				int columnCount = rsmd.getColumnCount();
				for (int i = 1; i <= columnCount; i++)
					listNameCols.add(rsmd.getColumnName(i));

				while (rs.next()) {
													
					for (String colName : listNameCols)
						ret  += rs.getObject(colName)+ " ";					
				}
			}

			return ret.toString();

		} finally {

			closeStatment(sts);
			closeConnection(con);

		}

	}

	/**
	 * Valida a existencia de um banco, no database informado
	 * Eh usado no processo de select de dados
	 * @param dataService
	 * @param databaseName
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public boolean existDatabase(DatabaseDTO database) throws ClassNotFoundException, SQLException {

		boolean exist = false;
		Connection con = null;
		Statement sts = null;

		try {

			String provider = database.getValueProperty("PROVIDER");
			String databaseName = database.getName();

			String query = queryExistDataBase(provider);

			// Aqui deve se conectar usando o dataservice e nao o database
			con = getConnection(database);
			sts = con.createStatement();

			@SuppressWarnings("unused")
			String dbBase = "";

			try (ResultSet rs = executeResultSet(sts, query)) {

				while (rs.next()) {

					dbBase += rs.getString(1) + " ";

					if (databaseName.equals(rs.getString(1))) {

						exist = true;
						break;

					}

				}

			}

		} catch (ClassNotFoundException | SQLException e) {

			throw e;

		} finally {

			closeStatment(sts);

		}

		return exist;

	}

	/**
	 * Valida a existencia de um banco usando um dataService
	 * Eh usado para validar se um banco exixte para poder criar
	 * Apos o processo a conexao eh fechada
	 * @param dataService
	 * @param databaseName
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public boolean existDatabase(DataService dataService, String databaseToCreate) throws ClassNotFoundException, SQLException {

		boolean exist = false;
		Connection con = null;
		Statement sts = null;

		try {

			DatabaseDTO databaseDTO = new DatabaseDTO(dataService);
			String provider = databaseDTO.getValueProperty("PROVIDER");
			String databaseName = databaseDTO.getValueProperty("NAME");

			String query = queryExistDataBase(provider);

			// Aqui deve se conectar usando o dataservice e nao o database
			con = getConnection(databaseDTO, databaseName);
			sts = con.createStatement();

			@SuppressWarnings("unused")
			String dbBase = "";

			try (ResultSet rs = executeResultSet(sts, query)) {

				while (rs.next()) {

					dbBase += rs.getString(1) + " ";

					if (databaseToCreate.equals(rs.getString(1))) {

						exist = true;
						break;

					}

				}

			}

			commit(con);

		} catch (ClassNotFoundException | SQLException e) {

			roolback(con);
			throw e;

		} finally {

			closeStatment(sts);
			closeConnection(con);

		}

		return exist;

	}

	/**
	 * Lista os database da conexao em questao
	 * @param dataService
	 * @param databaseName
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public List<String> listDtabase(DatabaseDTO database) throws ClassNotFoundException, SQLException {

		List<String> list = new ArrayList<>();
		Connection con = null;
		Statement sts = null;

		try {

			String provider = database.getValueProperty("PROVIDER");
			String query = queryExistDataBase(provider);

			// Aqui deve se conectar usando o dataservice e nao o database
			con = getConnection(database);
			sts = con.createStatement();

			try (ResultSet rs = executeResultSet(sts, query)) {

				while (rs.next())
					list.add(rs.getString(1).toUpperCase());

			}

		} catch (ClassNotFoundException | SQLException e) {

			throw e;

		} finally {

			closeStatment(sts);

		}

		return list;

	}

	/**
	 * Cria uma base de dados
	 * @param dataService
	 * @param databaseName
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public void createDataBase(DataService dataService, String databaseToCreate) throws ClassNotFoundException, SQLException {

		Connection con = null;
		Statement sts = null;

		try {

			DatabaseDTO databaseDTO = new DatabaseDTO(dataService);
			String provider = databaseDTO.getValueProperty("PROVIDER");
			String databaseName = databaseDTO.getValueProperty("NAME");

			String query = getQueryCreateDatabase(provider, databaseToCreate);

			// Aqui deve se conectar usando o dataservice e nao o database
			con = getConnection(databaseDTO, databaseName);
			sts = con.createStatement();

			// Criacao de banco deve ter autocomit no PGSQL
			if (provider.contentEquals("PGSQL"))
				con.setAutoCommit(true);

			executeQuery(sts, query);

			commit(con);

		} catch (ClassNotFoundException | SQLException e) {

			roolback(con);
			throw e;

		} finally {

			closeStatment(sts);
			closeConnection(con);

		}

	}

	/*-----TABLE-----*/

	/**
	 * Valida a existencia da tabela na base
	 * @param table
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public boolean existTable(TableDTO table) throws SQLException, ClassNotFoundException {

		boolean exist = false;
		Connection con = null;
		Statement sts = null;

		try {

			String query = queryListTable(table.getDatabase());

			con = getConnection(table.getDatabase());
			sts = con.createStatement();

			try (ResultSet rs = executeResultSet(sts, query)) {

				while (rs.next())
					if (table.getName().equals(rs.getString(1).toLowerCase())) {

						exist = true;
						break;

					}

			}

		} catch (ClassNotFoundException | SQLException e) {

			throw e;

		} finally {

			closeStatment(sts);

		}

		return exist;

	}

	/**
	 * Cria tabela de dados
	 * @param table
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public void createTable(TableDTO table) throws ClassNotFoundException, SQLException {

		Connection con = null;
		Statement sts = null;

		try {

			String query = queryCreateTable(table);

			con = getConnection(table.getDatabase());
			sts = con.createStatement();

			executeQuery(sts, query);

		} catch (ClassNotFoundException | SQLException e) {

			throw e;

		} finally {

			closeStatment(sts);

		}

	}

	/**
	 * Renomeia a tabela
	 * @param table
	 * @param oldName
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public void renameTable(TableDTO table, String oldName) throws ClassNotFoundException, SQLException {

		Connection con = null;
		Statement sts = null;

		try {

			String query = queryRenameTable(table, oldName);

			con = getConnection(table.getDatabase());
			sts = con.createStatement();

			executeQuery(sts, query);

		} catch (ClassNotFoundException | SQLException e) {

			throw e;

		} finally {

			closeStatment(sts);

		}

	}

	/**
	 * Valida a existencia da tabela na base
	 * @param table
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public List<String> listTableOld(DatabaseDTO database) throws SQLException, ClassNotFoundException {

		Connection con = null;
		Statement sts = null;
		List<String> list = new ArrayList<>();

		try {

			String query = queryListTable(database);

			con = getConnection(database);
			sts = con.createStatement();

			try (ResultSet rs = executeResultSet(sts, query)) {

				while (rs.next())
					list.add(rs.getString(1));

			}

		} catch (ClassNotFoundException | SQLException e) {

			throw e;

		} finally {

			closeStatment(sts);

		}

		return list;

	}

	/**
	 * Valida a existencia da tabela na base
	 * @param table
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public List<String> listTableUniquePK(DatabaseDTO database) throws SQLException, ClassNotFoundException {

		Connection con = null;
		Statement sts = null;
		List<String> list = new ArrayList<>();

		try {

			String query = queryListTableUniqueAndNumericPK(database);

			con = getConnection(database);
			sts = con.createStatement();

			try (ResultSet rs = executeResultSet(sts, query)) {

				while (rs.next())
					list.add(rs.getString(2).toUpperCase());

			}

		} catch (ClassNotFoundException | SQLException e) {

			throw e;

		} finally {

			closeStatment(sts);

		}

		return list;

	}

	/**
	 * Valida a existencia da tabela na base
	 * @param table
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public String columnTablePK(TableDTO table) throws SQLException, ClassNotFoundException {

		Connection con = null;
		Statement sts = null;
		String column = null;

		try {

			String query = queryColumnTablePK(table);

			con = getConnection(table.getDatabase());
			sts = con.createStatement();

			try (ResultSet rs = executeResultSet(sts, query)) {

				if (rs.next()) {

					column = rs.getString(1).toUpperCase();
					column += " (" + Utils.fulloResume(rs.getString(2)) + ")";

				}

			}

		} catch (ClassNotFoundException | SQLException e) {

			throw e;

		} finally {

			closeStatment(sts);

		}

		return column;

	}

	/**
	 * Cria um index com no nome exclusive, para diferenciar
	 * dos index padrao criado pelo usuario
	 * @param table
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public void createNoUniqueIndex(TableDTO table) throws ClassNotFoundException, SQLException {

		Connection con = null;
		Statement sts = null;

		try {

			con = getConnection(table.getDatabase());

			if (!table.getColumns().isEmpty()) {

				String query = queryCreateIndex(table, "idx_exclusive", false);
				sts = con.createStatement();

				executeQuery(sts, query);

			}

		} catch (ClassNotFoundException | SQLException e) {

			throw e;

		} finally {

			closeStatment(sts);

		}

	}

}

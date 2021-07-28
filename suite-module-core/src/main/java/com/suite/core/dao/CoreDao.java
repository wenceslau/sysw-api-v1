package com.suite.core.dao;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.suite.app.base.Dao;
import com.suite.app.exception.WarningException;
import com.suite.app.service.EncodeService;
import com.suite.core.dto.DatabaseDTO;
import com.suite.core.dto.InfoConnectionDTO;
import com.suite.core.model.DataService;
import com.suite.core.model.Sector;
import com.suite.core.service.SectorService;

/**
 * Classe de gerenciamento de conexoes com o banco de dados usando JDBC
 * @author Wenceslau
 */
@Service
public abstract class CoreDao extends Dao {

	// Ocorreu um problema ao conectar no banco [%s], tente novamente. Se persistir solicite análise ao UN Admin da aplicação. Causa SQL: [%s]. Comando: [%s]
	//private String msgConnection = "msg_ocorreu_um_problema_a_c_n_b_[%s]_t_n_s_p_s_a_a_u_a_d_a_c_[%s]_c_[%s]";

	// Ocorreu um problema ao executar o comando [%s] no banco [%s], tente novamente. Se persistir solicite análise ao UN Admin da aplicação. Causa SQL: [%s]. Comando: [%s]
	private String msgCommand = "msg_ocorreu_um_problema_a_e_o_c_[%s]_n_b_[%s]_t_n_s_p_s_a_a_u_a_d_a_c_[%s]_c_[%s]"; 

	// Ocorreu um problema ao executar o insert no banco [%s], tente novamente. Se persistir solicite análise ao UN Admin da aplicação. Causa SQL: [%s]. Comando: [%s]
	//private String msgInsert = "msg_ocorreu_um_problema_a_e_o_i_n_b_[%s]_t_n_s_p_s_a_a_u_a_d_a_c_[%s]_c_[%s]";

	// Ocorreu um problema ao executar o update no banco [%s], tente novamente. Se persistir solicite análise ao UN Admin da aplicação. Causa SQL: [%s]. Comando: [%s]
	//private String msgUpdate = "msg_ocorreu_um_problema_a_e_o_u_n_b_[%s]_t_n_s_p_s_a_a_u_a_d_a_c_[%s]_c_[%s]";

	// Ocorreu um problema ao preparar o comando no banco [%s], tente novamente. Se persistir solicite análise ao UN Admin da aplicação. Causa SQL: [%s]. Comando: [%s]
	//private String msgPrepare = "msg_ocorreu_um_problema_a_p_o_c_n_b_[%s]_t_n_s_p_s_a_a_u_a_d_a_c_[%s]_c_[%s]";

	@Autowired
	protected SectorService sectorService;
	
	@Autowired
	private EncodeService rsaService;
	
	private static final ConcurrentHashMap<String, Connection> connectionPool = new ConcurrentHashMap<String, Connection>();

	private String urlInfo;
	private boolean autoCommit;

	/**
	 * Cria uma conexao com o banco de dados usando parametros do DataService
	 * para o banco definido no paramtro nameDatabase
	 * Essa conexao nao se mantem aberta em um poll, precisa ser usada e fechada
	 * @param databaseTO
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public Connection getConnection(DatabaseDTO databaseTO, String nameDatabase) throws ClassNotFoundException, SQLException {

		databaseTO.setName(nameDatabase);
		Connection connection = createConnection(databaseTO);

		return connection;

	}

	/**
	 * Cria uma conexao com o banco de dados usando parametros do DataService, dentro do objeto DatabaseDTO
	 * @param databaseTO
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public Connection getConnection(DatabaseDTO databaseTO) throws ClassNotFoundException, SQLException {

		String key = getKey(databaseTO);
		Connection connection = connectionPool.get(key);

		if (connection == null || connection.isClosed()) {

			connection = createConnection(databaseTO);

			connectionPool.remove(key);
			connectionPool.put(key, connection);

		} else {

			Statement sts = null;

			try {

				sts = connection.createStatement();
				String provider = databaseTO.getValueProperty("PROVIDER");
				sts.execute(queryTestConnection(provider));

			} catch (Exception e) {

				warn("Validacao conexao falhou, removendo conexao do pool e reabrindo: " + e.getMessage());
				forceCloseAndRemoveConnection(key);
				// chama novamente o metodo para criar conexao
				connection = createConnection(databaseTO);
				connectionPool.put(key, connection);

			} finally {

				closeStatment(sts);

			}

		}

		return connection;

	}

	/**
	 * Realiza commit de uma transacao
	 * Fecha a conexao de acordo com o parametro closeConnection
	 * @param closeConnection
	 */
	public void commit(DatabaseDTO databaseTO) {

		Connection connection = connectionPool.get(getKey(databaseTO));
		commit(connection);

	}

	/**
	 * Realiza roolback de uma transacao
	 * Fecha a conexao de acordo com o parametro closeConnection
	 * @param closeConnection
	 */
	public void roolback(DatabaseDTO databaseTO) {

		Connection connection = connectionPool.get(getKey(databaseTO));

		roolback(connection);

	}

	/**
	 * Fecha conexao com o banco
	 */
	public void closeConnection(DatabaseDTO databaseTO) {

		Connection connection = connectionPool.get(getKey(databaseTO));

		closeConnection(connection);

	}

	/**
	 * Realiza commit de uma transacao
	 * Fecha a conexao de acordo com o parametro closeConnection
	 * @param closeConnection
	 */
	public void commit(Connection connection) {

		if (connection == null)
			return;

		try {

			if (!connection.getAutoCommit())
				connection.commit();

		} catch (SQLException e) {

			error("Erro ao relizar commit", e);

		}

	}

	/**
	 * Realiza roolback de uma transacao
	 * Fecha a conexao de acordo com o parametro closeConnection
	 * @param closeConnection
	 */
	public void roolback(Connection connection) {

		if (connection == null)
			return;

		try {

			if (!connection.getAutoCommit())
				connection.rollback();

		} catch (SQLException e) {

			warn("Erro ao relizar roolback..." + e.getMessage());

		}

	}

	/**
	 * Fecha conexao com o banco
	 */
	public void closeConnection(Connection connection) {

		if (connection == null)
			return;

		try {

			connection.close();
			info("Conexao fechada! Closed? " + connection.isClosed() +" " + this.hashCode() + ": " + urlInfo);

		} catch (SQLException e) {

			warn("Erro ao fechar connexao..." + e.getMessage());

		} finally {

			connection = null;

		}

	}

	/* Metodos de execuca de querys para tratativa de erros pernalizados */

	private Connection getConnection(String url, String user, String pwd, int timeout, String dbname) {

		try {

			DriverManager.setLoginTimeout(timeout);
			return DriverManager.getConnection(url, user, pwd);

		} catch (SQLException e) {

			throw new WarningException(errMessage(msgCommand,"CONNECT", dbname, e.getMessage(), url), e);

		}

	}

	public ResultSet executeResultSet(Statement sts, String query) {

		try {

			debug(loggerMessage(sts.getConnection().getCatalog(), query));

			return sts.executeQuery(query);

		} catch (SQLException e) {

			//throw new WarningException(errMessage(infoDatabase(sts), msgCommand, e.getMessage(), query, ""), e);
			throw new WarningException(errMessage(msgCommand,"QUERY", infoDatabase(sts), e.getMessage(), query), e);


		}

	}

	public ResultSet executeResultSet(PreparedStatement sts) {

		try {

			debug(loggerMessage(sts.getConnection().getCatalog(), sts));

			return sts.executeQuery();

		} catch (SQLException e) {

			throw new WarningException(errMessage(msgCommand,"QUERY", infoDatabase(sts), e.getMessage(), sts), e);

		}

	}

	public boolean executeQuery(Statement sts, String query) {

		try {

			debug(loggerMessage(sts.getConnection().getCatalog(), query));

			return sts.execute(query);

		} catch (SQLException e) {

			throw new WarningException(errMessage(msgCommand,"QUERY", infoDatabase(sts), e.getMessage(), query), e);

		}

	}

	public int executeInsert(PreparedStatement sts) {

		try {

			debug(loggerMessage(sts.getConnection().getCatalog(), sts));

			return sts.executeUpdate();

		} catch (SQLException e) {

			throw new WarningException(errMessage(msgCommand,"INSERT", infoDatabase(sts), e.getMessage(), sts), e);

		}

	}

	public int executeUpdate(PreparedStatement sts) {

		try {

			debug(loggerMessage(sts.getConnection().getCatalog(), sts));

			return sts.executeUpdate();

		} catch (SQLException e) {

			throw new WarningException(errMessage(msgCommand,"UPDATE", infoDatabase(sts), e.getMessage(), sts), e);

		}

	}

	public PreparedStatement prepareStatement(Connection con, String query) {

		try {

			return con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

		} catch (SQLException e) {

			throw new WarningException(errMessage(msgCommand,"PREPARE QUERY", infoDatabase(con), e.getMessage(), query), e);

		}

	}

	private String infoDatabase(Statement sts) {

		try {

			return sts.getConnection().getCatalog();

		} catch (SQLException e) {

			return "unknow";

		}

	}

	private String infoDatabase(Connection con) {

		try {

			return con.getCatalog();

		} catch (SQLException e) {

			try {

				Sector sector = sectorService.findById(getCodeSectorContext());
				return sector.getNameExternalDatabase();

			} catch (Exception e1) {

			}

			return "unknow";

		}

	}

	private String loggerMessage(String dbName, Object query) {

		return ("Execute on: " + dbName + ": " + query).replace("\r", "").replace("\n", "");

	}

	private String errMessage(String msg, String command, String dbName, String cause, Object query) {

		return formatTranslate(msg, command, dbName, cause, "");

	}

	/* **************************************************************** */

	/**
	 * 
	 * 
	 * /**
	 * Fecha stament de execucao
	 * @param sts
	 */
	public void closeStatment(Statement sts) {

		if (sts != null) {

			try {

				sts.close();
				sts = null;

			} catch (SQLException e) {

				warn("Erro ao fechar stament..." + e.getMessage());

			}

		}

	}

	public List<InfoConnectionDTO> infoConnection() {

		List<InfoConnectionDTO> list = new ArrayList<>();

		for (String key : connectionPool.keySet()) {

			try {

				list.add(new InfoConnectionDTO(key + "", connectionPool.get(key).isClosed() + ""));

			} catch (SQLException e) {

				list.add(new InfoConnectionDTO(key + "", e.getMessage()));

			}

		}

		return list;

	}

	/**
	 * Realiza commit de uma transacao
	 * Fecha a conexao de acordo com o parametro closeConnection
	 * @param closeConnection
	 */
	public void forceCommit(String connKey) {

		Connection connection = connectionPool.get(connKey);

		commit(connection);

	}

	/**
	 * Realiza roolback de uma transacao
	 * Fecha a conexao de acordo com o parametro closeConnection
	 * @param closeConnection
	 */
	public void forceRoolback(String connKey) {

		Connection connection = connectionPool.get(connKey);

		roolback(connection);

	}

	public void forceCloseConnection(String key) {

		closeConnection(connectionPool.get(key));

	}

	public void forceCloseAndRemoveConnection(String key) {

		closeConnection(connectionPool.get(key));
		connectionPool.remove(key);

	}

	// Private Methods

	private Connection createConnection(DatabaseDTO databaseTO) throws ClassNotFoundException, SQLException {

		Connection connection;
		String url = "";
		String driver = "";

		DataService dataService = databaseTO.getDataService();

		String databaseName = databaseTO.getName();

		String provider = databaseTO.getValueProperty("PROVIDER");
		String host = databaseTO.getValueProperty("HOST");
		String port = databaseTO.getValueProperty("PORT");
		String instance = databaseTO.getValueProperty("INSTANCE");
		String user = databaseTO.getValueProperty("USER");
		String pwd = databaseTO.getValueProperty("PASSWORD");
		int timeout = Integer.parseInt(databaseTO.getValueProperty("TIMEOUT", "15"));

		switch (provider) {

		case "MSSQL":
			if (databaseName == null)
				databaseName = "master";

			String strInstance = "";
			if (instance != null && !instance.isEmpty())
				strInstance = ";instanceName=" + instance;

			driver = "net.sourceforge.jtds.jdbc.Driver";
			url = "jdbc:jtds:sqlserver://" + host + ":" + port + strInstance + ";DatabaseName=" + databaseName;// + ";loginTimeout="+timeout;

			//driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
			//url = "jdbc:sqlserver://" + host + ":" + port + strInstance + ";DatabaseName=" + databaseName;// + ";loginTimeout=3";

			break;
		case "ORACLE":
			if (databaseName == null)
				databaseName = "sys";

			driver = "oracle.jdbc.driver.OracleDriver";
			url = "jdbc:oracle:thin:@" + host + ":" + port + ":" + databaseName + "";
			break;
		case "MYSQL":
			if (databaseName == null)
				databaseName = "sys";

			driver = "com.mysql.jdbc.Driver";
			Class.forName(driver);
			url = "jdbc:mysql://" + host + ":" + port + "/" + databaseName + "?useSSL=false&allowPublicKeyRetrieval=true";
			break;
		case "PGSQL":
			if (databaseName == null)
				databaseName = "postgres";

			driver = "org.postgresql.Driver";
			Class.forName(driver);
			url = "jdbc:postgresql://" + host + ":" + port + "/" + databaseName;
			break;
		case "ODBC":
			driver = "sun.jdbc.odbc.JdbcOdbcDriver";
			url = "jdbc:odbc:" + databaseName + "";
			break;
		default:
			break;

		}
		
		String strPwd;

		try {
			strPwd = rsaService.getRsa().dencrypt(pwd);

		} catch (Exception e) {
			throw new RuntimeException("Database password invalid", e);
			
		}

		urlInfo = "DS: " + dataService.getCode() + " URL: " + url + " USR: " + user + " PWD: " + strPwd + " TIMEOUT: " + timeout;
		Class.forName(driver);

		info("Abrindo conexao..." + this.hashCode() + ": " + urlInfo);
		connection = getConnection(url, user, strPwd, timeout, databaseName);
		info("Conexao aberta...." + this.hashCode());

		// Toda transacao deve requerer commit e rollback
		connection.setAutoCommit(autoCommit);

		return connection;

	}

	private String getKey(DatabaseDTO databaseTO) {

		return getCodeUserContext() +
				"-" + getCodeSectorContext() +
				"-" + databaseTO.getDataService().getCode() +
				"-" + databaseTO.getName();

	}

	// Getters Setter

	public boolean isAutoCommit() {

		return autoCommit;

	}

	public void setAutoCommit(boolean autoCommit) {

		this.autoCommit = autoCommit;

	}

	public static void main(String[] args) throws SQLException, ClassNotFoundException {


		String property = "java.io.tmpdir";

		// Get the temporary directory and print it.
		String tempDir = System.getProperty(property);
		System.out.println("OS temporary directory is " + tempDir);

		System.out.println(System.getProperty("os.name").toLowerCase());

		File f = new File("");

		System.out.println("> " + f.getAbsolutePath());
		System.out.println("> " + f.getName());
		System.out.println("> " + f.getPath());

		f = new File("/");

		System.out.println("> " + f.getAbsolutePath());
		System.out.println("> " + f.getName());
		System.out.println("> " + f.getPath());

	}

	@Override
	protected String formatTranslate(String key, Object... args) {

		return sectorService.formatTranslate(key, args);
		
	}

	protected String queryTestConnection(String provider) {

		String query = "SELECT 1";
		if (provider.equals("ORACLE"))
			query = "SELECT * FROM v$version WHERE banner LIKE 'Oracle%'";
		else if (provider.equals("MYSQL"))
			query = "SELECT '', @@version_comment, @@version, @@version_compile_os";
		else if (provider.equals("PGSQL"))
			query = "SELECT '', version();";
		else if (provider.equals("MSSQL"))
			query = "SELECT '' as dtt, @@VERSION as version";

		return query;

	}
	
}

package com.suite.app.base;

/**
 * Enum
 * @author Wenceslau
 *
 */
public class Enum {

	/**
	 * Enummeradores para tipo de dados para banco
	 * @author Wenceslau
	 * 
	 */
	public enum DataTypeOld {

		INTEGER("INTEIRO"), 
		LONG("INTEIRO_LONGO"), 
		DECIMAL("DECIMAL"), 
		TEXT("TEXTO"), 
		DATE("DATA"), 
		TIME("HORA"), 
		DATETIME("DATAHORA"), 
		BOOLEAN("BOOLEANO"), 
		OBJECT("OBJETO"),
		UNKNOW("UNKNOW");

		private String type;

		private DataTypeOld(String type) {
			this.type = type;
		}

		public String getType() {
			return type;
		}
	}

	public enum DataType {

		
		STRING("STRING", "String"), 
		INTEGER("INTEGER", "Integer"), 
		LONG("LONG", "Long"), 
		DOUBLE("DOUBLE", "Double"), 
		FLOAT("FLOAT", "Float"), 
		BIGDECIMAL("BIGDECIMAL", "BigDecimal"), 
		DATE("DATE","LocalDate"), 
		DATETIME("DATETIME","LocalDateTime"), 
		TIME("TIME","LocalDateTime"),
		BOOLEAN("BOOLEAN", "Boolean"),
		OBJECT("OBJECT", "Long"),
		EXTERNALOBJECT("EXTERNALOBJECT", "Long"),
		FILE("FILE", "String"),
		UNKNOW("UNKNOW", "Object");

		private String name;
		private String value;

		private DataType(String name, String value) {

			this.name = name;
			this.value = value;

		}

		public String getValue() {

			return value;

		}

		public String getName() {

			return name;

		}
		
		@Override
		public String toString() {

			return getValue();

		}

	}
	
	public enum ItemLicense {
		
		USER("USER", "user"), 
		OBJECT("OBJECT", "object"), 
		ROUTINE("ROUTINE", "routine");

		private String name;
		private String value;

		private ItemLicense(String name, String value) {

			this.name = name;
			this.value = value;

		}

		public String getValue() {

			return value;

		}

		public String getName() {

			return name;

		}
		
		@Override
		public String toString() {

			return getValue();

		}

	}
	
	public enum Module {
		
		CORE("CORE", "CORE"), 
		SECURYT("SECURYT", "SECURYT"),
		JOB("JOB", "JOB"),;


		private String name;
		private String value;

		private Module(String name, String value) {

			this.name = name;
			this.value = value;

		}

		public String getValue() {

			return value;

		}

		public String getName() {

			return name;

		}
		
		@Override
		public String toString() {

			return getValue();

		}

	}
	
}

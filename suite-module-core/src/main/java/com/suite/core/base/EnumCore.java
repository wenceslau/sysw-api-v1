package com.suite.core.base;

/**
 * Classe de Enumerados usados no Modulo Core
 * @author 4931pc_neto
 *
 */
public class EnumCore {

	public enum QueryUseType {

		QUERY("QUERY"), OTHER("OTHER");

		private String type;

		private QueryUseType(String type) {

			this.type = type;

		}

		public String getType() {

			return type;

		}

	}

	public enum DataServiceType {

		DATABASE("DATABASE"), EMAILSERVICE("EMAILSERVICE"), LIBRARY("LIBRARY"), WEBSERVICE("WEBSERVICE");

		private String type;

		private DataServiceType(String type) {

			this.type = type;

		}

		public String getType() {

			return type;

		}

	}

	public enum Lang {

		PT("PT"), EN("EN"), ES("ES");

		private String value;

		private Lang(String value) {

			this.value = value;

		}

		public String getValue() {

			return value;

		}

	}

}

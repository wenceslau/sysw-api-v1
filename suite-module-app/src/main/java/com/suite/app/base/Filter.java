package com.suite.app.base;

import com.suite.app.util.Utils;

/**
 * Super Classe Filter a ser estendida por outras classes Filter
 * @author Wenceslau
 */
public abstract class Filter extends Base {

	/**
	 * Propriedade usada nos metodos Filter dos repository para limitar o total de resultados
	 */
	private Integer maxResults;

	/**
	 * Usada para definir o order by em um filtr, o conteudo pode concatenar varios campos, separados por ','
	 * ver classe @see com.suite.app.base.Repository metodo addOrderBy
	 */
	private String orderBy;

	/**
	 * Ordena de forma desc o retorno da ordenacao definido na propriedade orderBy
	 */
	private Boolean desc;

	public String getOrderBy() {

		return orderBy;

	}

	public void setOrderBy(String orderBy) {

		this.orderBy = orderBy;

	}

	public Boolean getDesc() {

		return desc;

	}

	public void setDesc(Boolean desc) {

		this.desc = desc;

	}

	public Integer getMaxResults() {

		return maxResults;

	}

	public void setMaxResults(Integer maxResults) {

		this.maxResults = maxResults;

	}

	/**
	 * toString em formato JSON
	 */
	public String toString() {

		return Utils.objectToJsonNotIncludNull(this);

	}

	public abstract Long getCode();

	public abstract Boolean getStatus();

}

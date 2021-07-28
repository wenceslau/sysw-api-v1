
package com.suite.core.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Pageable;

public class TableDTO {

	private DatabaseDTO database;

	private List<ColumnDTO> columns;

	private String name;

	private String clause;

	private String andUserGroup;

	private boolean selectAllColumns;

	private Pageable pageable;

	private int firstRow;

	private int rowCount;

	private int limitSearch;

	private String orderBy;

	private boolean orderDesc = true;

	private String scopeObject; // Escopo do objeto quando a tabela pertencer a um

	private Long attributeCode; // Codigo do atributo que corresponte a tabela quando for objeto externo

	public TableDTO() {

		super();
		andUserGroup = "";

	}

	public TableDTO(DatabaseDTO database) {

		this();
		this.database = database;

	}

	public TableDTO(DatabaseDTO database, String nameTable) {

		this(database);
		this.name = nameTable;

	}

	public TableDTO(DatabaseDTO database, String nameTable, String scopeObject) {

		this(database, nameTable);
		this.scopeObject = scopeObject;

	}

	public TableDTO(DatabaseDTO database, String nameTable, String scopeObject, Long attributeCode) {

		this(database, nameTable, scopeObject);
		this.attributeCode = attributeCode;

	}

	// public String getObjetoName() {
	//
	// return objetoName;
	//
	// }
	//
	//
	// public void setObjetoName(String objetoName) {
	//
	// this.objetoName = objetoName;
	//
	// }

	public DatabaseDTO getDatabase() {

		return database;

	}

	public void setDatabase(DatabaseDTO database) {

		this.database = database;

	}

	public void setScopeObject(String scopeObject) {

		this.scopeObject = scopeObject;

	}

	public void setAttributeCode(Long attributeCode) {

		this.attributeCode = attributeCode;

	}

	public void setName(String name) {

		this.name = name;

	}

	public String getName() {

		if (name != null)
			name = name.toLowerCase();
		return name;

	}

	public String getClause() {

		return clause;

	}

	public void setClause(String clause) {

		this.clause = clause;

	}

	public String getAndUserGroup() {

		return andUserGroup;

	}

	public void setAndUserGroup(String andUserGroup) {

		this.andUserGroup = andUserGroup;

	}

	public List<ColumnDTO> getColumns() {

		if (columns == null)
			columns = new ArrayList<>();
		return columns;

	}

	public void setColumns(List<ColumnDTO> columns) {

		this.columns = columns;

	}

	public void addColumn(ColumnDTO column) {

		getColumns().add(column);

	}

	public Pageable getPageable() {

		return pageable;

	}

	public void setPageable(Pageable pageable) {

		if (pageable != null) {
			int paginaAtual = pageable.getPageNumber();
			int totalRegistroPorPagina = pageable.getPageSize();
			int primeiroRegistroDaPagina = paginaAtual * totalRegistroPorPagina;
			setFirstRow(primeiroRegistroDaPagina);
			setRowCount(totalRegistroPorPagina);

		}

		this.pageable = pageable;

	}

	public int getFirstRow() {

		return firstRow;

	}

	public void setFirstRow(int firstRow) {

		this.firstRow = firstRow;

	}

	public int getRowCount() {

		return rowCount;

	}

	public void setRowCount(int rowCount) {

		this.rowCount = rowCount;

	}

	public int getLimitSearch() {

		return limitSearch;

	}

	public void setLimitSearch(int limitSearch) {

		this.limitSearch = limitSearch;

	}

	public String getOrderBy() {

		return orderBy;

	}

	public void setOrderBy(String orderBy) {

		this.orderBy = orderBy;

	}

	public boolean isSelectAllColumns() {

		return selectAllColumns;

	}

	public void setSelectAllColumns(boolean selectAllColumns) {

		this.selectAllColumns = selectAllColumns;

	}

	public String getScopeObject() {

		return scopeObject;

	}

	public Long getAttributeCode() {

		return attributeCode;

	}

	public boolean isOrderDesc() {

		return orderDesc;

	}

	public void setOrderDesc(boolean orderDesc) {

		this.orderDesc = orderDesc;

	}

	@Override
	public String toString() {

		return getName();

	}

}

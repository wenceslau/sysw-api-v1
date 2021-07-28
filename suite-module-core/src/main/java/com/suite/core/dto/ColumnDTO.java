package com.suite.core.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.suite.app.base.Enum.DataType;
import com.suite.app.util.DateTimeUtils;

public class ColumnDTO {

	private Integer index;
	private String name;
	private DataType type;
	private boolean allowNull;
	private boolean key;
	private boolean primaryKey;

	@JsonIgnore
	private Object value;
	private boolean useLike;
	private boolean useWhere;
	private Integer sizeText;
	private boolean useOr;
	private String operator = "=";

	private boolean autoIncrement;

	public ColumnDTO() {}

	public ColumnDTO(String name) {
		super();
		this.name = name;

	}

	public ColumnDTO(String name, DataType type) {
		super();
		this.name = name;
		this.type = type;

	}

	public ColumnDTO(String name, DataType type, Object value, boolean useWhere, boolean like) {
		super();
		this.name = name;
		this.type = type;
		this.value = value;
		this.useWhere = useWhere;
		this.useLike = like;

	}

	public ColumnDTO(String name, DataType type, Object value, boolean useWhere, boolean like, boolean useOr) {
		super();
		this.name = name;
		this.type = type;
		this.value = value;
		this.useWhere = useWhere;
		this.useLike = like;
		this.setUseOr(useOr);

	}

	public ColumnDTO(String name, DataType type, boolean allowNull) {
		super();
		this.name = name;
		this.type = type;
		this.allowNull = allowNull;

	}

	public ColumnDTO(String name, boolean key) {
		super();
		this.name = name;
		this.key = key;

	}

	public void setUseWhere(boolean useWhere) {
		this.useWhere = useWhere;

	}

	public boolean isUseWhere() {
		return useWhere;

	}

	public String getName() {
		return name;

	}

	public DataType getType() {
		return type;

	}

	public boolean isAllowNull() {
		return allowNull;

	}

	public boolean isKey() {
		return key;

	}

	public void setType(DataType type) {
		this.type = type;

	}

	public void setAllowNull(boolean allowNull) {
		this.allowNull = allowNull;

	}

	public Object getValue() {
		return value;

	}

	public boolean isUseLike() {
		return useLike;

	}

	public void setUseLike(boolean useLike) {
		this.useLike = useLike;

	}

	public boolean isPrimaryKey() {
		return primaryKey;

	}

	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;

	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public Integer getSizeText() {
		if (sizeText == null)
			sizeText = 0;

		return sizeText;

	}

	public void setSizeText(Integer stringSize) {
		this.sizeText = stringSize;

	}

	@JsonIgnore
	public String getFormatedValue() {
		if (value == null)
			return null;

		// valor vazio onde o tipo nao eh texto retorna null
		if (value.toString().isEmpty() && !type.equals(DataType.STRING))
			return null;

		// valor com o texto 'null' retorna null
		if (value.toString().toLowerCase().equals("null"))
			return null;

		switch (type) {

		case STRING:
			if (isUseLike())
				value += "%";

			return "'" + value.toString().replace("'", "''") + "'";
		case DATE:
			return "'" + DateTimeUtils.getStringDateTime(DataType.DATE, value.toString(), "yyyy-MM-dd") + "'";
		case DATETIME:
			return "'" + DateTimeUtils.getStringDateTime(DataType.DATETIME, value.toString(), "yyyy-MM-dd HH:mm:ss") + "'";
		case TIME:
			return "'" + DateTimeUtils.getStringDateTime(DataType.TIME, value.toString(), "HH:mm:ss") + "'";
		case BOOLEAN:
			return (value.toString().toUpperCase().equals("TRUE") ? "1" : "0");
		default:
			break;

		}

		return value.toString();

	}

	public void setValue(Object value) {
		this.value = value;

	}

	@Override
	public String toString() {
		return name;

	}

	public boolean isUseOr() {
		return useOr;

	}

	public void setUseOr(boolean useOr) {
		this.useOr = useOr;

	}

	public boolean isAutoIncrement() {
		return autoIncrement;

	}

	public void setAutoIncrement(boolean autoIncrement) {
		this.autoIncrement = autoIncrement;

	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

}

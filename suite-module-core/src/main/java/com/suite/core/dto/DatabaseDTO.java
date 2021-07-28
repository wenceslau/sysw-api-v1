package com.suite.core.dto;

import com.suite.core.model.Property;
import com.suite.core.model.DataService;

public class DatabaseDTO {

	private DataService dataService;
	private String name;
	private String connectionKey;
	private Long sectorCode;

	public DatabaseDTO() {

		super();

	}

	public DatabaseDTO(DataService dataService) {

		super();
		this.dataService = dataService;

	}

	public DatabaseDTO(DataService dataService, String nameDatabase) {

		super();
		this.dataService = dataService;
		this.name = nameDatabase;

	}

	public DatabaseDTO(DataService dataService, String name, String key) {

		super();
		this.dataService = dataService;
		this.name = name;
		this.connectionKey = key;

	}

	public DataService getDataService() {

		if (dataService == null)
			throw new RuntimeException("msg_o_servico_de_d_n_f_i");

		return dataService;

	}

	public void setDataService(DataService dataService) {

		this.dataService = dataService;

	}

	public void setName(String name) {

		this.name = name;

	}

	public String getName() {

		if (name == null)
			throw new RuntimeException("msg_o_nome_do_b_n_f_i");

		return name;

	}

	public Long getSectorCode() {

		return sectorCode;

	}

	public void setSectorCode(Long sectorCode) {

		this.sectorCode = sectorCode;

	}

	public String getConnectionKey() {

		return connectionKey;

	}

	public void setConnectionKey(String connectionKey) {

		this.connectionKey = connectionKey;

	}

	/**
	 * Recupera a propriedade do data service
	 * @param dataService
	 * @param nameProperty
	 * @return
	 */
	public String getValueProperty(String nameProperty, String... valueDefault) {

		if (getDataService() == null)
			throw new RuntimeException("msg_o_data_service_n_f_i");

		String name = getDataService().getType() + "_" + nameProperty.toUpperCase();

		Property property = getDataService().getProperties().stream().filter(x -> x.getName().equals(name))
				.findFirst().orElse(null);

		if (property == null && valueDefault.length != 0)
			return valueDefault[0];

		if (property == null)
			throw new RuntimeException("A propriedade [" + nameProperty + " ] não foi encontrada");

		return property.getValue();

	}

	@Override
	public String toString() {

		return name;

	}

}

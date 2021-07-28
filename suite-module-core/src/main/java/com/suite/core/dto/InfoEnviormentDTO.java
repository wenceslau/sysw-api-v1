package com.suite.core.dto;

import com.suite.security.licence.Licence;

public class InfoEnviormentDTO {

	private Long busiessUnitCode;
	private String busiessUnit;
	private String sector;
	private String dataService;
	private String dataBaseSector;
	private String countObject;
	private Boolean hasDatabaseSector;
	private Integer maxFileUploadBytes;
	private Licence sysMonkey;

	public Long getBusiessUnitCode() {

		return busiessUnitCode;

	}

	public void setBusiessUnitCode(Long busiessUnitCode) {

		this.busiessUnitCode = busiessUnitCode;

	}

	public String getBusiessUnit() {

		return busiessUnit;

	}

	public void setBusiessUnit(String busiessUnit) {

		this.busiessUnit = busiessUnit;

	}

	public String getSector() {

		return sector;

	}

	public void setSector(String sector) {

		this.sector = sector;

	}

	public String getDataService() {

		return dataService;

	}

	public void setDataService(String dataService) {

		this.dataService = dataService;

	}

	public String getDataBaseSector() {

		return dataBaseSector;

	}

	public void setDataBaseSector(String dataBaseSector) {

		this.dataBaseSector = dataBaseSector;

	}

	public String getCountObject() {

		return countObject;

	}

	public void setCountObject(String countObject) {

		this.countObject = countObject;

	}

	public Boolean getHasDatabaseSector() {

		return hasDatabaseSector;

	}

	public void setHasDatabaseSector(Boolean hasDatabaseSector) {

		this.hasDatabaseSector = hasDatabaseSector;

	}

	public Integer getMaxFileUploadBytes() {

		return maxFileUploadBytes;

	}

	public void setMaxFileUploadBytes(Integer maxFileUploadBytes) {

		this.maxFileUploadBytes = maxFileUploadBytes;

	}

	public Licence getSysMonkey() {

		return sysMonkey;

	}

	public void setSysMonkey(Licence sysMonkey) {

		this.sysMonkey = sysMonkey;

	}

}

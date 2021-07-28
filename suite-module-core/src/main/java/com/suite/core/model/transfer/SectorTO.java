
package com.suite.core.model.transfer;

import static org.springframework.beans.BeanUtils.copyProperties;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.suite.core.base.ModelCoreTO;
import com.suite.core.model.Sector;

/**
 * Classe que representa a entidade Setor
 * @author Wenceslau
 *
 */
public class SectorTO extends ModelCoreTO {

	private String name;

	private String description;

	private String uniqueId;

	private String image;

	private Boolean requiredDb = false;

	private ApplicationTO application;

	private DataServiceTO dataService;

	private String nameExternalDatabase;

	private LocalDateTime dateCreateDatabase;

	private BusinessUnitTO businessUnit;

	private String label;

	private SectorTO() {}

	public String getName() {

		return name;

	}

	public void setName(String name) {

		this.name = name;

	}

	public String getDescription() {

		return description;

	}

	public void setDescription(String description) {

		this.description = description;

	}

	public String getUniqueId() {

		return uniqueId;

	}

	public void setUniqueId(String uniqueId) {

		this.uniqueId = uniqueId;

	}

	public String getImage() {

		return image;

	}

	public void setImage(String image) {

		this.image = image;

	}

	public Boolean getRequiredDb() {

		return requiredDb;

	}

	public void setRequiredDb(Boolean requiredDb) {

		this.requiredDb = requiredDb;

	}

	public String getNameExternalDatabase() {

		return nameExternalDatabase;

	}

	public void setNameExternalDatabase(String nameExternalDatabase) {

		this.nameExternalDatabase = nameExternalDatabase;

	}

	public LocalDateTime getDateCreateDatabase() {

		return dateCreateDatabase;

	}

	public void setDateCreateDatabase(LocalDateTime dateCreateDatabase) {

		this.dateCreateDatabase = dateCreateDatabase;

	}

	public DataServiceTO getDataService() {

		return dataService;

	}

	public void setDataService(DataServiceTO dataService) {

		this.dataService = dataService;

	}

	public BusinessUnitTO getBusinessUnit() {

		return businessUnit;

	}

	public void setBusinessUnit(BusinessUnitTO businessUnit) {

		this.businessUnit = businessUnit;

	}

	public String getLabel() {

		return label;

	}

	public void setLabel(String label) {

		this.label = label;

	}

	public ApplicationTO getApplication() {

		return application;

	}

	public void setApplication(ApplicationTO application) {

		this.application = application;

	}

	public static SectorTO build(Sector source, String... ignoreProperties) {

		if (source == null)
			return null;

		SectorTO target = new SectorTO();
		copyProperties(source, target, "application", "dataService", "businessUnit");

		List<String> ignoreList = (ignoreProperties != null ? Arrays.asList(ignoreProperties) : null);

		if (ignoreList == null || !ignoreList.contains("application"))
			target.setBusinessUnit(BusinessUnitTO.build(source.getBusinessUnit(), ignoreProperties));
		if (ignoreList == null || !ignoreList.contains("dataService"))
			target.setApplication(ApplicationTO.build(source.getApplication(), ignoreProperties));
		if (ignoreList == null || !ignoreList.contains("businessUnit"))
			target.setDataService(DataServiceTO.build(source.getDataService(), ignoreProperties));

		System.out.println("SectorTO.build()" + source.getCode());
		return target;

	}

	public static List<SectorTO> build(List<Sector> sourceList, String... ignoreProperties) {

		List<SectorTO> targetList = new ArrayList<>();
		sourceList.stream().forEach((object -> {
			targetList.add(build(object, ignoreProperties));

		}));

		return targetList;

	}

}


package com.suite.core.model.transfer;

import static org.springframework.beans.BeanUtils.copyProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.suite.core.base.ModelCoreTO;
import com.suite.core.model.Permission;

public class PermissionTO extends ModelCoreTO {

	private String strApplication = "";

	private ApplicationTO application;

	private String role;

	private String module;

	private String key;

	private String description;

	private String component;

	private Integer sequenceRoot;

	private String root;

	private String label;

	private String icon;

	private Boolean rootToolbar;

	private Integer sequence;

	private String router;

	private String predecessorPermission;

	private String display;

	private Integer type;

	private PermissionTO() {}

	public String getStrApplication() {

		return strApplication;

	}

	public void setStrApplication(String strApplication) {

		this.strApplication = strApplication;

	}

	public ApplicationTO getApplication() {

		return application;

	}

	public void setApplication(ApplicationTO application) {

		this.application = application;

	}

	public String getRole() {

		return role;

	}

	public void setRole(String role) {

		this.role = role;

	}

	public String getDescription() {

		return description;

	}

	public void setDescription(String description) {

		this.description = description;

	}

	public String getModule() {

		return module;

	}

	public void setModule(String module) {

		this.module = module;

	}

	public String getComponent() {

		return component;

	}

	public void setComponent(String component) {

		this.component = component;

	}

	public String getLabel() {

		return label;

	}

	public void setLabel(String label) {

		this.label = label;

	}

	public String getIcon() {

		return icon;

	}

	public void setIcon(String icon) {

		this.icon = icon;

	}

	public String getKey() {

		return key;

	}

	public void setKey(String key) {

		this.key = key;

	}

	public String getPredecessorPermission() {

		return predecessorPermission;

	}

	public void setPredecessorPermission(String predecessorPermission) {

		this.predecessorPermission = predecessorPermission;

	}

	public String getDisplay() {

		return display;

	}

	public void setDisplay(String diplay) {

		this.display = diplay;

	}

	public String getRouter() {

		return router;

	}

	public void setRouter(String router) {

		this.router = router;

	}

	public Integer getSequence() {

		return sequence;

	}

	public void setSequence(Integer sequence) {

		this.sequence = sequence;

	}

	public String getRoot() {

		return root;

	}

	public void setRoot(String root) {

		this.root = root;

	}

	public Integer getType() {

		return type;

	}

	public void setType(Integer typePerfil) {

		this.type = typePerfil;

	}

	public Boolean getRootToolbar() {

		return rootToolbar;

	}

	public void setRootToolbar(Boolean rootToolbar) {

		this.rootToolbar = rootToolbar;

	}

	public Integer getSequenceRoot() {

		return sequenceRoot;

	}

	public void setSequenceRoot(Integer sequenceRoot) {

		this.sequenceRoot = sequenceRoot;

	}

	public static PermissionTO build(Permission source, String... ignoreProperties) {

		if (source == null)
			return null;

		PermissionTO target = new PermissionTO();
		copyProperties(source, target, "application");

		List<String> ignoreList = (ignoreProperties != null ? Arrays.asList(ignoreProperties) : null);

		if (ignoreList == null || !ignoreList.contains("application"))
			target.setApplication(ApplicationTO.build(source.getApplication(), ignoreProperties));

		System.out.println("Permission.build()" + source.getCode());
		return target;

	}

	public static List<PermissionTO> build(List<Permission> sourceList, String... ignoreProperties) {

		List<PermissionTO> targetList = new ArrayList<>();

		sourceList.stream().forEach((object -> {
			targetList.add(build(object, ignoreProperties));

		}));

		return targetList;

	}

}
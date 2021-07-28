package com.suite.core.model.viewer;

import static org.springframework.beans.BeanUtils.copyProperties;

import java.util.ArrayList;
import java.util.List;

import com.suite.core.base.ViewerCore;
import com.suite.core.model.resume.ApplicationResume;
import com.suite.core.model.Permission;

public class PermissionVwr extends ViewerCore {

	// private String application;

	private ApplicationResume application;

	private String role;

	private String module;

	private String key;

	private String description;

	private String component;

	private Integer sequenceRoot;

	private String root;

	private String label;

	private String icon;

	private Boolean toolbar;

	private Boolean rootToolbar;

	private Integer sequence;

	private String router;

	private String predecessorPermission;

	private String display;

	public ApplicationResume getApplication() {

		return application;

	}

	public void setApplication(ApplicationResume application) {

		this.application = application;

	}

	public String getRole() {

		return role;

	}

	public void setRole(String role) {

		this.role = role;

	}

	public String getModule() {

		return module;

	}

	public void setModule(String module) {

		this.module = module;

	}

	public String getKey() {

		return key;

	}

	public void setKey(String key) {

		this.key = key;

	}

	public String getDescription() {

		return description;

	}

	public void setDescription(String description) {

		this.description = description;

	}

	public String getComponent() {

		return component;

	}

	public void setComponent(String component) {

		this.component = component;

	}

	public String getRoot() {

		return root;

	}

	public void setRoot(String root) {

		this.root = root;

	}

	public Integer getSequenceRoot() {

		return sequenceRoot;

	}

	public void setSequenceRoot(Integer sequenceRoot) {

		this.sequenceRoot = sequenceRoot;

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

	public Boolean getToolbar() {

		return toolbar;

	}

	public void setToolbar(Boolean toolbar) {

		this.toolbar = toolbar;

	}

	public Boolean getRootToolbar() {

		return rootToolbar;

	}

	public void setRootToolbar(Boolean rootToolbar) {

		this.rootToolbar = rootToolbar;

	}

	public Integer getSequence() {

		return sequence;

	}

	public void setSequence(Integer sequence) {

		this.sequence = sequence;

	}

	public String getRouter() {

		return router;

	}

	public void setRouter(String router) {

		this.router = router;

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

	public void setDisplay(String display) {

		this.display = display;

	}
	
	public static PermissionVwr build(Permission source) {

		if (source == null)
			return null;

		PermissionVwr target = new PermissionVwr();
		copyProperties(source, target, "application");
		target.setApplication(ApplicationResume.build(source.getApplication()));

		return target;

	}

	public static List<PermissionVwr> build(List<Permission> sourceList) {

		List<PermissionVwr> targetList = new ArrayList<>();

		sourceList.stream().forEach((object -> {
			targetList.add(build(object));

		}));

		return targetList;

	}
}

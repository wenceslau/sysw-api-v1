package com.suite.core.dto;

import java.util.List;

public class MenuItemDTO {

	private String description;
	private String label;
	private String icon;
	private String routerLink;
	private String title;
	private String badge;
	private String id;
	private Integer sequence;
	private Integer sequenceRoot;
	private Boolean rootToolbar;
	private Object object;
	private boolean home;
	private List<MenuItemDTO> items;

	public MenuItemDTO() {}

	public MenuItemDTO(String label, Integer sequence) {
		super();
		this.label = label;
		this.sequence = sequence;

	}

	public MenuItemDTO(Integer sequenceRoot, String label, Boolean rootToolbar) {
		super();
		this.sequenceRoot = sequenceRoot;
		this.label = label;
		this.rootToolbar = rootToolbar;

	}

	public MenuItemDTO(String label, String routerLink) {
		super();
		this.label = label;
		this.routerLink = routerLink;

	}

	public MenuItemDTO(String label, String routerLink, String icon) {
		super();
		this.label = label;
		this.routerLink = routerLink;
		this.icon = icon;

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

	public String getRouterLink() {
		return routerLink;

	}

	public void setRouterLink(String routerLink) {
		this.routerLink = routerLink;

	}

	public String getTitle() {
		return title;

	}

	public void setTitle(String title) {
		this.title = title;

	}

	public String getBadge() {
		return badge;

	}

	public void setBadge(String badge) {
		this.badge = badge;

	}

	public String getId() {
		return id;

	}

	public void setId(String id) {
		this.id = id;

	}

	public Integer getSequence() {
		return sequence;

	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;

	}

	public Boolean getRootToolbar() {
		return rootToolbar;

	}

	public void setRootToolbar(Boolean rootToolbar) {
		this.rootToolbar = rootToolbar;

	}

	public List<MenuItemDTO> getItems() {
		return items;

	}

	public void setItems(List<MenuItemDTO> items) {
		this.items = items;

	}

	public Integer getSequenceRoot() {
		return sequenceRoot;

	}

	public void setSequenceRoot(Integer sequenceRoot) {
		this.sequenceRoot = sequenceRoot;

	}

	public String getDescription() {
		return description;

	}

	public void setDescription(String description) {
		this.description = description;

	}

	public Boolean getHome() {
		return home;

	}

	public void setHome(Boolean home) {
		this.home = home;

	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

}
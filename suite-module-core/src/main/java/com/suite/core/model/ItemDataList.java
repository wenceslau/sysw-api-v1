
package com.suite.core.model;

import static org.springframework.beans.BeanUtils.copyProperties;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.suite.core.base.ModelCore;

@Entity
@Table(name = "tb_core_item_data_list")
public class ItemDataList extends ModelCore {

	@Column(name = "val_group")
	private String group;

	@NotNull
	@Column(name = "val_label_item")
	private String labelItem;

	@NotNull
	@Column(name = "val_value_item")
	private String valueItem;

	@NotNull
	@Column(name = "des_item")
	private String description;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "cod_data_list_fk")
	private DataList dataList;

	public String getGroup() {

		return group;

	}

	public void setGroup(String group) {

		this.group = group;

	}

	public String getLabelItem() {

		return labelItem;

	}

	public void setLabelItem(String labelItem) {

		this.labelItem = labelItem;

	}

	public String getValueItem() {

		return valueItem;

	}

	public void setValueItem(String valueItem) {

		this.valueItem = valueItem;

	}

	public String getDescription() {

		return description;

	}

	public void setDescription(String description) {

		this.description = description;

	}

	public DataList getDataList() {

		return dataList;

	}

	public void setDataList(DataList dataList) {

		this.dataList = dataList;

	}

	public static ItemDataList build(ItemDataList source) {

		if (source == null)
			return null;

		ItemDataList target = new ItemDataList();
		copyProperties(source, target, "dataList");
		target.setDataList(DataList.build(source.getDataList()));

		return target;

	}

	public static List<ItemDataList> build(List<ItemDataList> sourceList) {

		List<ItemDataList> targetList = new ArrayList<>();
		sourceList.stream().forEach((object -> {
			targetList.add(build(object));

		}));

		return targetList;

	}

}

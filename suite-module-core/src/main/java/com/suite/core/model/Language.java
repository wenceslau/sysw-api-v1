
package com.suite.core.model;

import static org.springframework.beans.BeanUtils.copyProperties;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Classe que representa a entidade Linguagem
 * Ela armazena os dados traducao do sistema
 * @author Wenceslau
 *
 */
@Entity
@Table(name = "tb_core_language")
public class Language {

	/*
	 * PK do entidade
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cod_record")
	private Long code;

	/*
	 * 
	 */
	@NotNull
	@Column(name = "val_key")
	private String key;

	/*
	 * 
	 */
	@Column(name = "val_portugues")
	private String portugues;

	/*
	 * 
	 */
	@Column(name = "val_english")
	private String english;

	/*
	 * 
	 */
	@Column(name = "val_spanish")
	private String spanish;

	/*
	 * 
	 */
	@Column(name = "val_description")
	private String description;

	/*
	 * 
	 */
	@Column(name = "dtt_record")
	private LocalDateTime dateRecord;

	/*
	 * 
	 */
	@Column(name = "dtt_update")
	private LocalDateTime dateUpdate;

	/*
	 * Status do objeto
	 */
	@NotNull
	@Column(name = "sts_record")
	private Boolean status;

	public Long getCode() {

		return code;

	}

	public void setCode(Long code) {

		this.code = code;

	}

	public String getKey() {

		return key;

	}

	public void setKey(String key) {

		this.key = key;

	}

	public String getPortugues() {

		return portugues;

	}

	public void setPortugues(String portugues) {

		this.portugues = portugues;

	}

	public String getEnglish() {

		return english;

	}

	public void setEnglish(String english) {

		this.english = english;

	}

	public String getSpanish() {

		return spanish;

	}

	public void setSpanish(String spanish) {

		this.spanish = spanish;

	}

	public String getDescription() {

		return description;

	}

	public void setDescription(String description) {

		this.description = description;

	}

	public LocalDateTime getDateRecord() {

		return dateRecord;

	}

	public void setDateRecord(LocalDateTime dateRecord) {

		this.dateRecord = dateRecord;

	}

	public LocalDateTime getDateUpdate() {

		return dateUpdate;

	}

	public void setDateUpdate(LocalDateTime dateUpdate) {

		this.dateUpdate = dateUpdate;

	}

	public Boolean getStatus() {

		return status;

	}

	public void setStatus(Boolean status) {

		this.status = status;

	}

	public static Language build(Language source) {

		if (source == null)
			return null;

		Language target = new Language();
		copyProperties(source, target);

		return target;

	}

	public static List<Language> build(List<Language> sourceList) {

		List<Language> targetList = new ArrayList<>();
		sourceList.stream().forEach((object -> {
			targetList.add(build(object));
		}));

		return targetList;

	}}
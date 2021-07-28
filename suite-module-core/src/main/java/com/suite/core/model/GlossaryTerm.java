package com.suite.core.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "tb_dpg_glossary_term")
public class GlossaryTerm {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cod_record")
	private Long code;

	@Column(name = "term_initials")
	private String initials;

	@NotNull
	@Column(name = "nam_term")
	private String name;

	@Column(name = "nam_favorite_term")
	private String favoriteTerm;

	@Column(name = "sts_prefix")
	private boolean prefixo;

	@Column(name = "dtt_record")
	private LocalDateTime dtt_record;

	@Column(name = "dtt_update")
	private LocalDateTime dtt_update;

	public Long getCode() {
		return code;
	}

	public void setCode(Long code) {
		this.code = code;
	}

	public String getInitials() {
		return initials;
	}

	public void setInitials(String initials) {
		this.initials = initials;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFavoriteTerm() {
		return favoriteTerm;
	}

	public void setFavoriteTerm(String favoriteTerm) {
		this.favoriteTerm = favoriteTerm;
	}

	public boolean isPrefixo() {
		return prefixo;
	}

	public void setPrefixo(boolean prefixo) {
		this.prefixo = prefixo;
	}

	public LocalDateTime getDtt_record() {
		return dtt_record;
	}

	public void setDtt_record(LocalDateTime dtt_record) {
		this.dtt_record = dtt_record;
	}

	public LocalDateTime getDtt_update() {
		return dtt_update;
	}

	public void setDtt_update(LocalDateTime dtt_update) {
		this.dtt_update = dtt_update;
	}

}

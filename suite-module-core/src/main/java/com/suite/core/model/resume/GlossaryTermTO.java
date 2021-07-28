package com.suite.core.model.resume;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.suite.core.model.GlossaryTerm;

public class GlossaryTermTO {

	private Long code;

	private String name;
	private String initials;
	private String namFavoriteTerm;
	private boolean statusPrefix;
	private String dttRecord;
	private String dttUpdate;

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

	public String getNamFavoriteTerm() {
		return namFavoriteTerm;
	}

	public void setNamFavoriteTerm(String namFavoriteTerm) {
		this.namFavoriteTerm = namFavoriteTerm;
	}

	public boolean isStatusPrefix() {
		return statusPrefix;
	}

	public void setStatusPrefix(boolean statusPrefix) {
		this.statusPrefix = statusPrefix;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDttRecord() {
		return dttRecord;
	}

	public void setDttRecord(String dttRecord) {
		this.dttRecord = dttRecord;
	}

	public String getDttUpdate() {
		return dttUpdate;
	}

	public void setDttUpdate(String dttUpdate) {
		this.dttUpdate = dttUpdate;
	}

	public static GlossaryTermTO buildTO(GlossaryTerm termosGlossario) {
		if (termosGlossario == null)
			return null;

		GlossaryTermTO ot = new GlossaryTermTO();
		ot.setCode(termosGlossario.getCode());
		ot.setName(termosGlossario.getName());
		ot.setNamFavoriteTerm(termosGlossario.getFavoriteTerm());
		ot.setInitials(termosGlossario.getInitials());
		ot.setStatusPrefix(termosGlossario.isPrefixo());
		ot.setDttRecord(termosGlossario.getDtt_record().toString());
		ot.setDttUpdate(termosGlossario.getDtt_update().toString());

		return ot;
	}

	public static GlossaryTerm buildEntity(GlossaryTermTO termosGlossario) {
		if (termosGlossario == null)
			return null;

		GlossaryTerm ot = new GlossaryTerm();
		ot.setCode(termosGlossario.getCode());
		ot.setName(termosGlossario.getName());
		ot.setFavoriteTerm(termosGlossario.getNamFavoriteTerm());
		ot.setInitials(termosGlossario.getInitials());
		ot.setPrefixo(termosGlossario.isStatusPrefix());
		ot.setDtt_record(termosGlossario.getDttRecord() == null ? LocalDateTime.now()
				: LocalDateTime.parse(termosGlossario.getDttRecord()));
		ot.setDtt_update(LocalDateTime.now());

		return ot;
	}

	public static List<GlossaryTermTO> buildListTO(List<GlossaryTerm> listTermosGlossario) {
		List<GlossaryTermTO> list = new ArrayList<>();

		for (GlossaryTerm object : listTermosGlossario)
			list.add(buildTO(object));

		return list;
	}

}

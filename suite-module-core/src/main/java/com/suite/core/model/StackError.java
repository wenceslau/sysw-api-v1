
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

import com.suite.app.base.Model;

/**
 * Classe que representa a entidade StackError
 * @author Wenceslau
 *
 */
@Entity
@Table(name = "tb_core_stack_error")
public class StackError extends Model {

	/*
	 * PK da entidade
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cod_record")
	private Long code;

	/*
	 * Mensagem de erro da excecao
	 */
	@Column(name = "val_message")
	private String message;

	/*
	 * Causa da excecao
	 */
	@Column(name = "val_causes")
	private String causes;

	/*
	 * Pilha da excecao das classes do modulo
	 */
	@Column(name = "val_stack")
	private String stack;

	/*
	 * Pilha completa de excecao
	 */
	@Column(name = "val_fullStack")
	private String fullStack;

	/*
	 * Data do erro
	 */
	@NotNull
	@Column(name = "dtt_error")
	private LocalDateTime dateTimeError;

	/*
	 * Codigo do usuario logado no momento do erro
	 */
	@NotNull
	@Column(name = "cod_user")
	private Long codeUser;

	/*
	 * Codigo do setor logado no momento do erro
	 */
	@NotNull
	@Column(name = "cod_sector")
	private Long codeSector;

	public Long getCode() {

		return code;

	}

	public void setCode(Long code) {

		this.code = code;

	}

	public String getMessage() {

		return message;

	}

	public void setMessage(String message) {

		this.message = message;

	}

	public String getCauses() {

		return causes;

	}

	public void setCauses(String causes) {

		this.causes = causes;

	}

	public String getStack() {

		return stack;

	}

	public void setStack(String stack) {

		this.stack = stack;

	}

	public String getFullStack() {

		return fullStack;

	}

	public void setFullStack(String fullStack) {

		this.fullStack = fullStack;

	}

	public LocalDateTime getDateTimeError() {

		return dateTimeError;

	}

	public void setDateTimeError(LocalDateTime dateTimeError) {

		this.dateTimeError = dateTimeError;

	}

	public Long getCodeUser() {

		return codeUser;

	}

	public void setCodeUser(Long codeUser) {

		this.codeUser = codeUser;

	}

	public Long getCodeSector() {

		return codeSector;

	}

	public void setCodeSector(Long codeSector) {

		this.codeSector = codeSector;

	}

	@Override
	public String toString() {

		return "StackError [codeUser=" + codeUser + ", codeSector=" + codeSector + ", causes=" + causes + ", stack=" + stack
				+ ", dateTimeError=" + dateTimeError + "]";

	}

	@Override
	public Boolean getStatus() {

		return true;

	}

	@Override
	public void setStatus(Boolean status) {}

	public static StackError build(StackError source) {

		if (source == null)
			return null;

		StackError target = new StackError();
		copyProperties(source, target);

		return target;

	}

	public static List<StackError> build(List<StackError> sourceList) {

		List<StackError> targetList = new ArrayList<>();
		sourceList.stream().forEach((object -> {
			targetList.add(build(object));
		}));

		return targetList;

	}
}

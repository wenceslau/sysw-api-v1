package com.suite.job.base;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

import com.suite.app.base.Model;
import com.suite.core.util.UtilsCore;
import com.suite.job.listener.JobChangeListener;

/**
 * Super classe das entidades do modulo job.
 * As entidades do job possuem esses mesmos atributos
 * compartilham essa superclasse
 * Estende a classe base Model
 * @author Wenceslau
 *
 */
@MappedSuperclass
@EntityListeners(JobChangeListener.class)
public class ModelJob extends Model {

	/*
	 * PK do objeto
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cod_record")
	private Long code;

	/*
	 * Status do objeto
	 */
	@NotNull
	@Column(name = "sts_record")
	private Boolean status;

	/* Methods Override */

	/*
	 * Metodo abstrato sobrescrito
	 */
	@Override
	public Long getCode() {
		return code;

	}

	/*
	 * Metodo abstrato sobrescrito
	 */
	@Override
	public void setCode(Long code) {
		this.code = code;

	}

	/*
	 * Metodo abastrato sobrescrito
	 */
	@Override
	public Boolean getStatus() {
		// REMOVIDO DIA 20/06/2020
		// se o objeto esta sendo criado, ele tem status true;
		// if (code == null)
		// status = true;

		// se nao estiver sendo criado e o status for nulo, define false;
		if (status == null)
			status = false;

		return status;

	}

	/*
	 * Metodo abastrato sobrescrito
	 */
	@Override
	public void setStatus(Boolean status) {
		this.status = status;

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		return result;

	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ModelJob other = (ModelJob) obj;

		if (code == null) {

			if (other.code != null)
				return false;

		} else if (!code.equals(other.code))
			return false;

		return true;

	}

	@Override
	public String toString() {
		return UtilsCore.objectToJson(this);

	}

}

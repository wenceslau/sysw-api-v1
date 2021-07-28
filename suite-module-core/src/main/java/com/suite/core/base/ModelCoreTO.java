
package com.suite.core.base;

import com.suite.app.base.ModelTO;
import com.suite.core.model.User;
import com.suite.core.util.UtilsCore;

/**
 * Super classe das entidades do modulo core.
 * As entidades do core possuem esses mesmos atributos
 * compartilham essa superclasse
 * Estende a classe base Model
 * @author Wenceslau
 *
 */
public class ModelCoreTO extends ModelTO {

	private Long code;

	private Boolean status;

	private User userUpdate;

	public User getUserUpdate() {

		return userUpdate;

	}

	public void setUserUpdate(User userUpdate) {

		this.userUpdate = userUpdate;

	}

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
		ModelCoreTO other = (ModelCoreTO) obj;

		if (code == null) {
			if (other.code != null)
				return false;

		} else if (!code.equals(other.code))
			return false;

		return true;

	}

	public String getCodeStr() {

		return code + "";

	}

	@Override
	public String toString() {

		return UtilsCore.objectToJson(this);

	}

}

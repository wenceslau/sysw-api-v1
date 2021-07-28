package com.suite.core.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Classe que representa a entidade Notificacao
 * Classe nao estende a ModelCore porque a entidade
 * nao possui os atributos da superclasse
 * @author Wenceslau
 *
 */
@Entity
@Table(name = "tb_core_initializer")
public class Initializer {

	/*
	 * PK do entidade
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cod_record")
	private Long code;

	/*
	 * Data hora do envio da notificacao
	 */
	@NotNull
	@Column(name = "dat_initializer")
	private LocalDateTime dateInitializer;

	/*
	 * Mensagem da notificacao
	 */
	@NotNull
	@Column(name = "nam_initializer")
	private String name;

	/*
	 * Mensagem da notificacao
	 */
	@NotNull
	@Column(name = "des_initializer")
	private String description;

	public Long getCode() {

		return code;

	}

	public void setCode(Long code) {

		this.code = code;

	}

	public LocalDateTime getDateInitializer() {

		return dateInitializer;

	}

	public void setDateInitializer(LocalDateTime dateInitializer) {

		this.dateInitializer = dateInitializer;

	}

	public String getName() {

		return name;

	}

	public void setName(String name) {

		this.name = name;

	}

	public String getDescription() {

		return description;

	}

	public void setDescription(String description) {

		this.description = description;

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
		Initializer other = (Initializer) obj;

		if (code == null) {

			if (other.code != null)
				return false;

		} else if (!code.equals(other.code))
			return false;

		return true;

	}

}

package com.suite.core.repository.filter;

import com.suite.core.base.FilterCore;

/**
 * Classe usada como objeto de filtro para o Sector
 * @author Wenceslau
 *
 */
public class SectorFilter extends FilterCore {

	private String name;
	
	private boolean onlySectorFromAppLogged;


	public String getName() {

		return name;

	}

	public void setName(String name) {

		this.name = name;

	}

	
	public boolean isOnlySectorFromAppLogged() {
	
		return onlySectorFromAppLogged;
	
	}

	
	public void setOnlySectorFromAppLogged(boolean onlySectorFromAppLogged) {
	
		this.onlySectorFromAppLogged = onlySectorFromAppLogged;
	
	}
	

}

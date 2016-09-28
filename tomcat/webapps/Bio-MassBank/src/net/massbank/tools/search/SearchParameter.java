/*******************************************************************************
 *
 * Copyright (C) 2014 MassBank Project
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *******************************************************************************
 *
 * SearchParameter.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.tools.search;

import java.util.Map;
import java.util.TreeMap;

public class SearchParameter {
	protected Map<String, String> params = null;

	/**
	 * constructor
	 */
	public SearchParameter() {
		this.params = new TreeMap();
	}

	/**
	 * constructor
	 */
	public SearchParameter(Map<String, String> params) {
		this.params = params;
	}

	/**
	 * set instrument types
	 */
	public void setInstrumentTypes(String[] vals) {
		String catval = "";
		for ( int i = 0; i < vals.length; i++ ) {
			catval += vals[i] + ";";
		}
		this.params.put("instrument_types", catval);
	}

	/**
	 *  set MS types
	 */
	public void setMsTypes(String[] vals) {
		String catval = "";
		for ( int i = 0; i < vals.length; i++ ) {
			catval += vals[i] + ";";
		}
		this.params.put("ms_types", catval);
	}


	/**
	 * set ion mode
	 */
	public void setIonMode(String val) {
		this.params.put("ion_mode", val);
	}

	/**
	 * set result format
	 */
	public void changeResultFormat() {
		this.params.put("result_format", "true");
	}

	/**
	 * get instrument types
	 */
	public String[] getInstrumentTypes() {
		String key = "instrument_types";
		if ( this.params.containsKey(key) ) {
			String val = this.params.get(key);
			String[] vals = val.split(";");
			return vals;
		}
		else {
			return null;
		}
	}

	/**
	 * get MS types
	 */
	public String[] getMsTypes() {
		String key = "ms_types";
		if ( this.params.containsKey(key) ) {
			String val = this.params.get(key);
			String[] vals = val.split(";");
			return vals;
		}
		else {
			return null;
		}
	}

	/**
	 * get ion mode
	 * 
	 */
	public String getIonMode() {
		return this.params.get("ion_mode");
	}


	/**
	 * set result format
	 */
	public boolean getResultFormat() {
		String val = params.get("result_format");
		if ( val == null || val.equals("") ) {
			return false;
		}
		else {
			return true;
		}
	}


	/**
	 * SearchParameter to Map
	 */
	public Map<String, String> toMap() {
		return this.params;
	}

}

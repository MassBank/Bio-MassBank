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
 * SpectrumSearchParameter.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.tools.search.spectrum;

import java.util.Map;
import net.massbank.tools.search.SearchParameter;

public class SpectrumSearchParameter extends SearchParameter {

	/**
	 * constructor
	 */
	public SpectrumSearchParameter() {
		super();
	}

	/**
	 * constructor
	 */
	public SpectrumSearchParameter(Map<String, String> params) {
		super(params);
	}

	/**
	 * set m/z
	 */
	public void setMzs(String[] vals) {
		String catval = "";
		for ( int i = 0; i < vals.length; i++ ) {
			catval += vals[i];
			if ( i < vals.length - 1 ) { 
				catval += ",";
			}
		}
		super.params.put("mzs", catval);
	}

	/**
	 * set intensities
	 */
	public void setIntensities(String[] vals) {
		String catval = "";
		for ( int i = 0; i < vals.length; i++ ) {
			catval += vals[i];
			if ( i < vals.length - 1 ) { 
				catval += ",";
			}
		}
		super.params.put("intensities", catval);
	}

	/**
	 * set tolerance of m/z
	 */
	public void setTolerance(String val) {
		super.params.put("tolerance", val);
	}

	/**
	 * set unit of tolerance
	 */
	public void setUnitOfTolerance(String val) {
		super.params.put("tol_unit", val);
	}

	/**
	 * set cutoff
	 */
	public void setCutoff(String val) {
		super.params.put("cutoff", val);
	}

	/**
	 * set precursor
	 */
	public void setPrecursor(String val) {
		super.params.put("precursor", val);
	}

	/**
	 * get m/z
	 */
	public String[] getMzs() {
		String key = "mzs";
		if ( super.params.containsKey(key) ) {
			String val = super.params.get(key);
			String[] vals = val.split(",");
			return vals;
		}
		else {
			return null;
		}
	}

	/**
	 * get intensities
	 */
	public String[] getIntensities() {
		String key = "intensities";
		if ( super.params.containsKey(key) ) {
			String val = super.params.get(key);
			String[] vals = val.split(",");
			return vals;
		}
		else {
			return null;
		}
	}

	/**
	 * get tolerance of m/z
	 */
	public String getTolerance() {
		return super.params.get("tolerance");
	}

	/**
	 * get  unit of tolerance
	 */
	public String getUnitOfTolerance() {
		return super.params.get("tol_unit");
	}

	/**
	 * get cutoff
	 */
	public String getCutoff() {
		return super.params.get("cutoff");
	}

	/**
	 * get precursor
	 */
	public String getPrecursor() {
		return super.params.get("precursor");
	}
}

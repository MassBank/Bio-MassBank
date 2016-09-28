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
 * PeakSearchParameter.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.tools.search.peak;

import java.util.Map;
import net.massbank.tools.search.SearchParameter;


public class PeakSearchParameter extends SearchParameter {

	/**
	 * constructor
	 */
	public PeakSearchParameter() {
		super();
	}

	/**
	 * constructor
	 */
	public PeakSearchParameter(Map<String, String> params) {
		super(params);
	}

	/**
	 * set m/z
	 */
	public void setMzs(String[] vals) {
		String catval = "";
		for ( int i = 0; i < vals.length; i++ ) {
			catval += vals[i] + ",";
		}
		super.params.put("mzs", catval);
	}

	/**
	 * set relative intensity
	 */
	public void setRelativeIntensity(String val) {
		super.params.put("relative_intensity", val);
	}
	/**
	 * set tolerance of m/z
	 */
	public void setTolerance(String val) {
		super.params.put("tolerance", val);
	}

	/**
	 * set search type ("peak" or "diff")
	 */
	public void setSearchType(String val) {
		super.params.put("search_type", val);
	}

	/**
	 * set search condition
	 */
	public void setSearchCondition(String val) {
		super.params.put("search_cond", val);
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
	 * get relative intensity
	 */
	public String getRelativeIntensity() {
		return super.params.get("relative_intensity");
	}

	/**
	 * get tolerance of m/z
	 */
	public String getTolerance() {
		return super.params.get("tolerance");
	}

	/**
	 * get search type
	 */
	public String getSearchType() {
		return super.params.get("search_type");
	}

	/**
	 * get search condition
	 */
	public String getSearchCondition() {
		return super.params.get("search_cond");
	}
}

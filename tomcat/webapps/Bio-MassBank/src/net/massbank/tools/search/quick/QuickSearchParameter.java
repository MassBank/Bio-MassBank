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
 * QuickSearchParameter.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.tools.search.quick;

import java.util.Map;
import net.massbank.tools.search.SearchParameter;

public class QuickSearchParameter extends SearchParameter {

	/**
	 * constructor
	 */
	public QuickSearchParameter() {
		super();
	}

	/**
	 * constructor
	 */
	public QuickSearchParameter(Map<String, String> params) {
		super(params);
	}

	/**
	 * set compound name
	 */
	public void setCompoundName(String val) {
		super.params.put("compound_name", val);
	}

	/**
	 * set exact mass
	 */
	public void setExactMass(String val) {
		super.params.put("exact_mass", val);
	}

	/**
	 * set tolerance
	 */
	public void setTolerance(String val) {
		super.params.put("tolerance", val);
	}

	/**
	 * set formula
	 */
	public void setFormula(String val) {
		super.params.put("formula", val);
	}

	/**
	 * set site no
	 */
	public void setSiteNo(String val) {
		super.params.put("site_no", val);
	}

	/**
	 * set keyword
	 */
	public void setKeyword(String val) {
		super.params.put("keyword", val);
	}

	/**
	 * get compound name
	 */
	public String getCompoundName() {
		return super.params.get("compound_name");
	}

	/**
	 * get exact mass
	 */
	public String getExactMass() {
		return super.params.get("exact_mass");
	}

	/**
	 * get tolerance
	 */
	public String getTolerance() {
		return super.params.get("tolerance");
	}

	/**
	 * get tolerance
	 */
	public String getFormula() {
		return super.params.get("formula");
	}

	/**
	 * get keyword
	 */
	public String getKeyword() {
		return super.params.get("keyword");
	}
}

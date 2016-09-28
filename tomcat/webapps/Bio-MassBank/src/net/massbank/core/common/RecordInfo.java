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
 * RecordInfo.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.core.common;

import java.util.Map;

public class RecordInfo {
	protected Map<String, String> info = null;

	/**
	 * constructor
	 */
	public RecordInfo(Map<String, String> info) {
		this.info = info;
	}

	/**
	 * get ID
	 */
	public String getId() {
		return this.info.get("ID");
	}

	/**
	 * get record title
	 */
	public String getRecordTitle() {
		String cname    = this.info.get("COMPOUND_NAME");
		String instType = this.info.get("INSTRUMENT_TYPE");
		String msType   = this.info.get("MS_TYPE");
		String addition = this.info.get("TITLE_ADDITION");
		return cname + "; " + instType + "; " + msType + "; " + addition;
	}

	/**
	 * get compound no
	 */
	public String getCompoundNo() {
		return String.valueOf(info.get("COMPOUND_NO"));
	}

	/**
	 * get compound name
	 */
	public String getCompoundName() {
		return info.get("COMPOUND_NAME");
	}

	/**
	 * get formula
	 */
	public String getFormula() {
		return this.info.get("FORMULA");
	}

	/**
	 * get exact mass
	 */
	public String getExactMass() {
		return String.valueOf(this.info.get("EXACT_MASS"));
	}

	/**
	 * get presursor m/z
	 */
	public String getPrecursor() {
		return String.valueOf(this.info.get("PRECURSOR_MZ"));
	}

	/**
	 * get ion mode
	 */
	public String getIonMode() {
		return String.valueOf(this.info.get("ION"));
	}

	/**
	 * get molfile data
	 */
	public String getMolfileData() {
		return this.info.get("MOLFILE");
	}

	/**
	 * get peaks
	 */
	public String getPeaks() {
		return this.info.get("PEAK");
	}

	/**
	 * get site number
	 */
	public String getSiteNo() {
		return this.info.get("SITE_NO");
	}

}

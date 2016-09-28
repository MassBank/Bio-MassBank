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
 * RecordSet.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.core.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecordSet {
	protected Map<String, Object> resultMap = new HashMap();
	protected List<String> recordList = new ArrayList();

	/**
	 * constructor
	 */
	public RecordSet(Map resultMap, List recordList) {
		this.resultMap = resultMap;
		this.recordList = recordList;
	}

	/**
	 * get compound no
	 */
	public String getCompoundNo() {
		return String.valueOf(this.resultMap.get("COMPOUND_NO"));
	}


	/**
	 * get compound name
	 */
	public String getCompoundName() {
		return (String)this.resultMap.get("COMPOUND_NAME");
	}

	/**
	 * get formula
	 */
	public String getFormula() {
		return (String)this.resultMap.get("FORMULA");
	}

	/**
	 * get exact mass
	 */
	public String getExactMass() {
		return String.valueOf(this.resultMap.get("EXACT_MASS"));
	}

	/**
	 * get molfile
	 */
	public String getMolfile() {
		return (String)this.resultMap.get("MOLFILE");
	}

	/**
	 * get number of record list
	 */
	public int getRecordListSize() {
		return this.recordList.size();
	}

	/**
	 * get record title
	 */
	public String getRecordTitle(int index) {
		String info = this.recordList.get(index);
		String[] vals = info.split("\t");
		return vals[0];
	}

	/**
	 * get record ID
	 */
	public String getId(int index) {
		String info = this.recordList.get(index);
		String[] vals = info.split("\t");
		return vals[1];
	}

	/**
	 * get site number
	 */
	public String getSiteNo() {
		return (String)this.resultMap.get("SITE_NO");
	}
}

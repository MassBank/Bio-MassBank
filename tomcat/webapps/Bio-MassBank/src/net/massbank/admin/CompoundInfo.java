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
 * CompoundInfo.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.admin;

import java.util.Map;

public class CompoundInfo {
	protected Map<String, Object> info = null;

	/**
	 * constructor
	 */
	public CompoundInfo(Map<String, Object> info) {
		this.info = info;
	}

	/**
	 * get compound no
	 */
	public int getCompoundNo() {
		return (Integer)this.info.get("COMPOUND_NO");
	}

	/**
	 * get compound name
	 */
	public String getCompoundName() {
		return (String)info.get("COMPOUND_NAME");
	}

	/**
	 * get InChI code
	 */
	public String getInchiCode() {
		return (String)this.info.get("INCHI");
	}

	/**
	 * get molfile data
	 */
	public String getMolfileData() {
		return (String)this.info.get("MOLFILE");
	}
}

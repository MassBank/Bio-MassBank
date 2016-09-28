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
 * CommonParameter.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.core.common;

import java.util.Map;
import java.util.TreeMap;

public class CommonParameter {
	protected Map<String, String> params = null;

	/**
	 * constructor
	 */
	public CommonParameter() {
		this.params = new TreeMap<String, String>();
	}

	/**
	 * constructor
	 */
	public CommonParameter(Map<String, String> params) {
		this.params = params;
	}

	/**
	 * set IDs
	 */
	public void setIds(String[] vals) {
		String catval = "";
		for ( int i = 0; i < vals.length; i++ ) {
			catval += vals[i] + ";";
		}
		this.params.put("ids", catval);
	}

	/**
	 * get IDs
	 */
	public String[] getIds() {
		String key = "ids";
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
	 * 
	 */
	public String getJoinId() {
		String[] ids = getIds();
		StringBuilder joinId = new StringBuilder();
		if ( ids.length > 0 ) {
			for ( int i = 0; i < ids.length; i++ ) {
				joinId.append("'" + ids[i] + "'");
				if ( i < ids.length - 1 ) {
					joinId.append(",");
				}
			}
		}
		return joinId.toString();
	}

	/**
	 * 
	 */
	public Map<String, String> toMap() {
		return this.params;
	}
}

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
 * Result.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.core.common;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class Results {
	List<Map<String, String>> results = null;

	/**
	 * constructor
	 */
	public Results(List<Map<String, String>> results) {
		this.results = results;
	}

	/**
	 * get ID list
	 */
	public String[] getAllIds() {
		if ( this.results == null ) {
			return null;
		}
		List<String> list = new ArrayList<String>();
		for ( int i = 0; i < this.results.size(); i++ ) {
			Map<String, String> map = (Map<String, String>)this.results.get(i);
			String id = map.get("ID");
			list.add(id);
		}
		return list.toArray(new String[]{});
	}

	/**
	 * get site number
	 */
	public String getSiteNo(String id) {
		return getValueById("SITE_NO", id);
	}

	/**
	 * 
	 */
	protected String getValueById(String key, String id) {
		if ( this.results == null ) {
			return null;
		}
		for ( int i = 0; i < this.results.size(); i++ ) {
			Map<String, String> map = (Map<String, String>)this.results.get(i);
			String getId = map.get("ID");
			if ( getId.equals(id) ) {
				String val = String.valueOf(map.get(key));
				return val;
			}
		}
		return "";
	}

	/**
	 * 
	 */
	protected Map<String, String> getResultById(String id) {
		if ( this.results == null ) {
			return null;
		}
		for ( int i = 0; i < this.results.size(); i++ ) {
			Map<String, String> map = (Map<String, String>)this.results.get(i);
			String getId = map.get("ID");
			if ( getId.equals(id) ) {
				return map;
			}
		}
		return null;
	}

}

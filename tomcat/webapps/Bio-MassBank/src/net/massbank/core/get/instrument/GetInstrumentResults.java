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
 * GetInstrumentResult.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.core.get.instrument;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.ArrayList;

public class GetInstrumentResults {
	List results = null;

	/**
	 * constructor
	 */
	public GetInstrumentResults(List<Map<String, String>> results) {
		this.results = results;
	}

	/**
	 * get instrument types
	 */
	public String[] getInstTypes() {
		Set<String> instTypeList = new TreeSet();
		for ( int i = 0; i < this.results.size(); i++ ) {
			Map map = (Map)this.results.get(i);
			String instType = (String)map.get("INSTRUMENT_TYPE");
			if ( !instTypeList.contains(instType) ) {
				instTypeList.add(instType);
			}
		}
		return instTypeList.toArray(new String[]{});
	}

	/**
	 * get instrument type groups
	 */
	public Map<String, String[]> getInstTypeGroups() {
		final String[] ionization = { "ESI", "EI", "Others" };
		List<String>[] list = new ArrayList[ionization.length];
		for ( int i = 0; i < ionization.length; i++ ) {
			list[i] = new ArrayList();
		}
		String[] instTypes = getInstTypes();
		int j = 0;
		for ( String instType : instTypes ) {
			boolean isOthers = true;
			for ( j = 0; j < ionization.length; j++ ) {
				if ( instType.indexOf(ionization[j]) >= 0 ) {
					list[j].add(instType);
					isOthers = false;
					break;
				}
			}
			if ( isOthers ) {
				list[ionization.length-1].add(instType);
			}
		}

		Map<String, String[]> instTypeGroup = new TreeMap();
		for ( int k = 0; k < ionization.length; k++ ) {
			if ( list[k].size() > 0 ) {
				instTypeGroup.put( ionization[k], list[k].toArray(new String[]{}) );
			}
		}
		return instTypeGroup;
	}

	/**
	 * get MS types
	 */
	public String[] getMsTypes() {
		Set<String> msTypeList = new TreeSet();
		for ( int i = 0; i < this.results.size(); i++ ) {
			Map map = (Map)this.results.get(i);
			String msType = (String)map.get("MS_TYPE");
			if ( !msTypeList.contains(msType) ) {
				msTypeList.add(msType);
			}
		}
		return msTypeList.toArray(new String[]{});
	}
}

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
 * RecordSetList.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.core.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class RecordSetList {
	List<RecordSet> recordSets = new ArrayList();

	/**
	 * constructor
	 */
	public RecordSetList(List<Map> results) {
		Map<String, Object> mapRecordList = new HashMap();
		Map<String, Integer> mapIndexList = new TreeMap();
		List newRecordList = null;
		for ( int i = 0; i < results.size(); i++ ) {
			Map<String, Object> map = (Map)results.get(i);
			String cname = (String)map.get("COMPOUND_NAME");
			List<String> recordList = (List)map.get("RECORD_LIST");
			if ( mapRecordList.containsKey(cname) ) {
				newRecordList = (List)mapRecordList.get(cname);
				newRecordList.addAll(recordList);
			}
			else {
				newRecordList = new ArrayList();
				newRecordList.addAll(recordList);
				mapRecordList.put(cname, newRecordList);
				mapIndexList.put(cname, i);
			}
		}


		Map<String, Object> mapResult = null;
		Iterator it = mapIndexList.keySet().iterator();
		while ( it.hasNext() ) {
			String cname = (String)it.next();
			int index = (Integer)mapIndexList.get(cname);
			List recordList = (List)mapRecordList.get(cname);
			Collections.sort(recordList);
			Map<String, Object> resultMap = (Map)results.get(index);
			RecordSet set = new RecordSet(resultMap, recordList);
			this.recordSets.add(set);
		}
	}

	/**
	 *
	 */
	public int getListSize() {
		return this.recordSets.size();
	}

	/**
	 *
	 */
	public RecordSet getRecordSet(int index) {
		return this.recordSets.get(index);
	}
}

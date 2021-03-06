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
 * GetRecordInfoInvoker.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.core.get.record;


import java.util.Map;
import java.util.TreeMap;
import net.massbank.core.common.BaseInvoker;
import net.massbank.core.common.RecordInfo;

public class GetRecordInfoInvoker extends BaseInvoker {
	public static final String REQ_TYPE = "get_record_info";

	/**
	 * constructor
	 */
	public GetRecordInfoInvoker(String requestUrl, String[] ids) {
		super(requestUrl, REQ_TYPE, ids);
	}

	public GetRecordInfoInvoker(String requestUrl, String[] ids, String siteNo) {
		super(requestUrl, REQ_TYPE, ids, siteNo);
	}

	/**
	 * get results
	 */
	@Override public Map<String, RecordInfo> getResults() {
		if ( super.results == null || super.results.size() == 0 ) {
			return null;
		}
		Map<String, RecordInfo> mapResults = new TreeMap();
		int num = super.results.size();
		for ( int i = 0; i < num; i++ ) {
			Map<String, String> map = (Map)super.results.get(i);
			RecordInfo info = new RecordInfo(map);
			String id = info.getId();
			mapResults.put(id, info);
		}
		return mapResults;
	}
}

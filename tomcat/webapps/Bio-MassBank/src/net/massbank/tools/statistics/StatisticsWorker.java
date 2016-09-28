/*******************************************************************************
 *
 * Copyright (C) 2013 MassBank Project
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
 * RecordIndexWorker.java
 *
 * ver 1.0.1 2013.04.15
 *
 ******************************************************************************/
package net.massbank.tools.statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import net.massbank.core.BaseWorker;

public class StatisticsWorker extends BaseWorker {

	/**
	 * constructor
	 */
	public StatisticsWorker(String dbName, Map<String, String> params) {
		super(dbName, params);
	}

	/**
	 * call
	 */
	@Override public List call() throws Exception {
		List results = new ArrayList();
		String[] sqls = {
			 "select 'CONTRIB', count(ID) as cnt from RECORD",
			 "select INSTRUMENT_TYPE, count(I.INSTRUMENT_NO) as cnt from RECORD R, INSTRUMENT I"
			+ " where I.INSTRUMENT_NO=R.INSTRUMENT_NO group by I.INSTRUMENT_NO",
			"select MS_TYPE, count(MS_TYPE) as cnt from RECORD group by MS_TYPE",
			"select ION, count(ION) as cnt from RECORD group by ION"
		};
		for ( int i = 0; i < sqls.length; i++ ) {
			String resKey = "";
			String name = "";
			String cnt = "";
			List res = (List)qr.query(con, sqls[i], rsh);
			for ( int j = 0; j < res.size(); j++ ) {
				Map<String, Object> map = (Map)res.get(j);
				for ( Iterator it = map.keySet().iterator(); it.hasNext(); ) {
					String key = (String)it.next();
					String val = String.valueOf(map.get(key));
					if ( key.equals("cnt") ) {
						cnt = val;
					}
					else {
						name = val;
						resKey = key;
					}
				}
				Map<String, String> resMap = new HashMap();
				resMap.put(resKey, name + "=" + cnt);
				results.add(resMap);
			}
		}

		Map<String, Integer> sampleList = new HashMap();
		String sql = "select FULL_TEXT FROM RECORD";
		List<Object[]> res = (List)qr.query(con, sql, new ArrayListHandler());
		for ( Object[] fields : res ) {
			String fullText = String.valueOf(fields[0]);
			String[] lines = fullText.split("\n");
			for ( String line: lines ) {
				String tag = "SP$SAMPLE";
				int pos = line.indexOf(tag);
				if ( pos >= 0 ) {
					String sampleName = line.substring(tag.length() + 2);
					int cnt = 1;
					if ( sampleList.containsKey(sampleName) ) {
						cnt = sampleList.get(sampleName) + 1;
					}
					sampleList.put(sampleName, cnt);
					break;
				}
			}
		}

		for ( Iterator it = sampleList.keySet().iterator(); it.hasNext(); ) {
			String sampleName = (String)it.next();
			String cnt = String.valueOf(sampleList.get(sampleName));
			Map<String, String> resMap = new HashMap();
			resMap.put("SAMPLE", sampleName + "=" + cnt);
			results.add(resMap);
		}
		return results;
	}
}

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
 * GetRecordInfoWorker.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.core.get.record;

import java.util.List;
import java.util.Map;
import net.massbank.core.BaseWorker;
import net.massbank.core.common.CommonParameter;

import org.apache.commons.dbutils.DbUtils;

public class GetRecordInfoWorker extends BaseWorker {

	/**
	 * constructor
	 */
	public GetRecordInfoWorker(String dbName, Map<String, String> params) {
		super(dbName, params);
	}

	/**
	 * call
	 */
	@Override public List call() throws Exception {
		CommonParameter cparam = new CommonParameter(super.params);
		String joinId = cparam.getJoinId();
		String sql1 = "select ID, MS_TYPE, TITLE_ADDITION, PRECURSOR_MZ, ION,"
					+ " INSTRUMENT_TYPE, COMPOUND_NAME, FORMULA, EXACT_MASS, MOLFILE"
					+ " from RECORD R, COMPOUND_INFO C, INSTRUMENT I"
					+ " where R.COMPOUND_NO=C.COMPOUND_NO and R.INSTRUMENT_NO=I.INSTRUMENT_NO"
					+ " and ID in(" + joinId + ")";
		List<Map<String, String>> results1 = (List)qr.query(con, sql1, rsh);

		String sql2 = "select ID, MZ, RELATIVE from PEAK where ID in(" + joinId + ") "
					+ "order by ID, MZ";
		List<Map<String, String>> results2 = (List)qr.query(con, sql2, rsh);
		if ( results2.size() > 0 ) {
			String prevId = "";
			StringBuilder sb = new StringBuilder();
			for ( int row = 0; row < results2.size(); row++ ) {
				Map<String, String> map2 = (Map)results2.get(row);
				String id      = map2.get("ID");
				String mz      = String.valueOf(map2.get("MZ"));
				String relInte = String.valueOf(map2.get("RELATIVE"));
				if ( row > 0 && !id.equals(prevId) ) {
					for ( Map<String, String> map1: results1 ) {
						if ( prevId.equals(map1.get("id")) ) {
							map1.put("PEAK", sb.toString());
							break;
						}
					}
					sb.setLength(0);
				}
				sb.append(mz + "," + relInte + ";");
				prevId = id;
				for ( Map<String, String> map1: results1 ) {
					if ( id.equals(map1.get("id")) ) {
						map1.put("PEAK", sb.toString());
						break;
					}
				}
			}
		}
		DbUtils.closeQuietly(con);
		return results1;
	}
}

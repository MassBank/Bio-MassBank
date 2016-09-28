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
 * PeakSearchWorker.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.tools.search.peak;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.massbank.core.BaseWorker;
import net.massbank.core.GetDbUtil;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.handlers.ArrayListHandler;

public class PeakSearchWorker extends BaseWorker {
	private String[] mzs         = null;
	private String relInte       = "";
	private String tol           = "";
	private String ionMode       = "";
	private String searchType    = "";
	private String searchCond    = "";
	private String[] instTypes   = null;
	private String[] msTypes     = null;
	private boolean resultFormat = false;

	/**
	 * constructor
	 */
	public PeakSearchWorker(String dbName, Map<String, String> params) {
		super(dbName, params);
		PeakSearchParameter sp = new PeakSearchParameter(params);
		this.mzs          = sp.getMzs();
		this.relInte      = sp.getRelativeIntensity();
		this.tol          = sp.getTolerance();
		this.ionMode      = sp.getIonMode();
		this.searchType   = sp.getSearchType();
		this.searchCond   = sp.getSearchCondition();
		this.instTypes    = sp.getInstrumentTypes();
		this.msTypes      = sp.getMsTypes();
		this.resultFormat = sp.getResultFormat();
	}

	/**
	 * call
	 */
	@Override public List call() throws Exception {
		String[] ids = search();
		int num = ids.length;
		if ( num == 0 ) {
			return null;
		}
		List<Map> results = null;
		if ( this.resultFormat ) {
			results = GetDbUtil.getRecordSet(con, ids, instTypes, msTypes, ionMode);
		}
		else {
			results = GetDbUtil.getRecordInfo(con, ids, instTypes, msTypes, ionMode, "");
		}
		DbUtils.closeQuietly(con);
		return results;
	}

	/*
	 * search
	 */
	private String[] search()  throws Exception {
		String sql = "";
		double dblTol = Double.parseDouble(tol);
		List<String> idList1 = new ArrayList();
		List<String> idList2= new ArrayList();
		for ( int i = 0; i < mzs.length; i++ ) {
			double dblMz = Double.parseDouble(mzs[i]);
			String min = String.valueOf(dblMz - (dblTol + 0.00001));
			String max = String.valueOf(dblMz + (dblTol + 0.00001));
			if ( searchType.equals("peak_diff") ) {
				String tableName = "PEAK_HEAP";
				if ( !GetDbUtil.checkHeapTableExists(con) ) {
					tableName = "PEAK";
				}
				sql = "select t1.ID from " + tableName + " as t1 left join " + tableName + " as t2 on t1.ID = t2.ID "
					+ "where (t1.MZ between t2.MZ + " + min + " and t2.MZ + " + max + ") "
					+ "and t1.RELATIVE > " + relInte + " and t2.RELATIVE > " + relInte;
			}
			else {
				sql = "select distinct ID from PEAK where (MZ between " + min + " and " + max + ") "
					+ "and RELATIVE > " + relInte + " order by ID";
			}
			List results = qr.query(con, sql, new ArrayListHandler());
			for ( int j = 0; j < results.size(); j++ ) {
				Object[] items = (Object[])results.get(j);
				String id = (String)items[0];
				if ( searchCond.equals("and") ) {
					if ( i == 0 ) {
						idList1.add(id);
					}
					else {
						if ( idList1.contains(id) ) {
							idList2.add(id);
						}
					}
				}
				else if ( searchCond.equals("or") ) {
					if ( !idList1.contains(id) ) {
						idList1.add(id);
					}
				}
			}
			if ( searchCond.equals("and") && i > 0 ) {
				idList1 = new ArrayList(idList2);
				idList2.clear();
			}
		}
		return idList1.toArray(new String[]{});
	}
}

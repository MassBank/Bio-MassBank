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
 * QuickSearchWorker.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.tools.search.quick;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import net.massbank.core.BaseWorker;
import net.massbank.core.GetDbUtil;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.handlers.ArrayListHandler;

public class QuickSearchWorker extends BaseWorker {
	private String compoundName  = "";
	private double emass         = 0;
	private double tolerance     = 0;
	private String formula       = "";
	private String ionMode       = "";
	private String keyword       = "";
	private String[] instTypes   = null;
	private String[] msTypes     = null;

	/**
	 * constructor
	 */
	public QuickSearchWorker(String dbName, Map<String, String> params) {
		super(dbName, params);
		QuickSearchParameter sp = new QuickSearchParameter(params);
		this.compoundName = sp.getCompoundName();
		String val1 = sp.getExactMass();
		if ( val1 != null && !val1.equals("") ) {
			this.emass = Double.parseDouble(val1);
		}
		String val2 = sp.getTolerance();
		if ( val2 != null && !val2.equals("") ) {
			this.tolerance = Double.parseDouble(val2);
		}
		this.formula   = sp.getFormula();
		this.keyword   = sp.getKeyword();
		this.ionMode   = sp.getIonMode();
		this.instTypes = sp.getInstrumentTypes();
		this.msTypes   = sp.getMsTypes();
	}

	/**
	 * call
	 */
	@Override public List call() throws Exception {
		List<Map> results = null;
		String[] ids = search();
		if ( ids != null ) {
			results = GetDbUtil.getRecordSet(con, ids, instTypes, msTypes, ionMode);
		}
		DbUtils.closeQuietly(con);
		return results;
	}

	/*
	 * search
	 */
	private String[] search()  throws Exception {
		String sql = "";
		if ( !this.keyword.equals("") ) {
			sql = "select ID from RECORD where FULL_TEXT like '%" + keyword + "%'";
		}
		else {
			sql = "select distinct R.ID from RECORD R, COMPOUND_INFO C, COMPOUND_NAMES N "
			   + "where R.COMPOUND_NO=C.COMPOUND_NO and R.ID=N.ID";
			String conditions = "";
			if ( !this.compoundName.equals("") ) {
				conditions += " and convert(N.COMPOUND_NAME using utf8) collate utf8_unicode_ci like '%" + this.compoundName + "%'";
			}
			if ( this.emass > 0 ) {
				double val1 = this.emass - (this.tolerance + 0.00001);
				double val2 = this.emass + (this.tolerance + 0.00001);
				conditions += " and EXACT_MASS between " + String.valueOf(val1) + " and " + String.valueOf(val2);
			}
			if ( !this.formula.equals("") ) {
				String key = formula.replace("*", "%");
				conditions += " and FORMULA like '" + key + "'";
			}
			if ( !conditions.equals("") ) {
				sql += conditions;
			}
		}
		List results = qr.query(con, sql, new ArrayListHandler());
		int num = results.size();
		String[] ids = null;
		if ( num > 0 ) {
			ids = new String[num];
			for ( int i = 0; i < num; i++ ) {
				Object[] fields = (Object[])results.get(i);
				ids[i] = (String)fields[0];
			}
		}
		return ids;
	}
}

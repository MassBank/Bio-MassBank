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
 * FormulaSearch.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.tools.search;

import java.sql.Connection;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import net.massbank.core.GetDbUtil;
import net.massbank.core.common.ChemicalFormulaUtils;

public class FormulaSearch {
	private int ionMode = 1;
	private int tolerancePpm = 50;
	private String[] mzs = null;

	/**
	 *
	 */
	public FormulaSearch(String queryPeakString, int cutoff, int tolerancePpm, int ionMode) {
		this.ionMode = ionMode;
		this.tolerancePpm = tolerancePpm;
		this.mzs = ChemicalFormulaUtils.getMzArray(queryPeakString, cutoff);
	}

	
	/*
	 * search
	 */
	public List<Map> doSearch() {
		List<Map> results = new ArrayList();
		Map<String, Map> tempList = new HashMap();
		try {
			List<String[]> ionMassList = ChemicalFormulaUtils.getIonMassList(this.ionMode);
			Map<Double, String> queryFormulaList = ChemicalFormulaUtils.getMatchedFormulas(this.mzs, this.tolerancePpm, ionMassList, this.ionMode);
			results.add(queryFormulaList);

			String formulas = "";
			Set keys = queryFormulaList.keySet();
			for ( Iterator iterator = keys.iterator(); iterator.hasNext(); ) {
				double mz = (Double)iterator.next();
				String formula = queryFormulaList.get(mz);
				formulas += "'" + formula + "',";
			}
			formulas = formulas.substring(0, formulas.length() - 1);
			Connection con = GetDbUtil.connectDb("FORMULA_SEARCH");
			String sql1 = "select distinct COMPOUND_ID, FORMULA from PEAK_ANNOTATION A where COMPOUND_ID != '' "
						+ "and FORMULA in(" + formulas + ") and ION_MODE=" + this.ionMode + " order by COMPOUND_ID, FORMULA";
			QueryRunner qr1 = new QueryRunner();
			List res1 = qr1.query(con, sql1, new ArrayListHandler());
			Map<String, String> matchedFormulaList = new HashMap();
			for ( int i = 0; i < res1.size(); i++ ) {
				Object[] fields1 = (Object[])res1.get(i);
				String cid = String.valueOf(fields1[0]);
				String formula = String.valueOf(fields1[1]);
				String matchedFormula = "";
				if ( matchedFormulaList.containsKey(cid) ) {
					matchedFormula = (String)matchedFormulaList.get(cid) + "," + formula;
				}
				else {
					matchedFormula = formula;
				}
				matchedFormulaList.put(cid, matchedFormula);
			}

			Map<String, Integer> cntMap = new HashMap();
			for ( Iterator it1 = matchedFormulaList.keySet().iterator(); it1.hasNext(); ) {
				String cid = (String)it1.next();
				String matchedFormula = matchedFormulaList.get(cid);
				String[] matchedFormulas = matchedFormula.split(",");
				int hitNum = matchedFormulas.length;
				cntMap.put(cid, hitNum);
			}


			List entries1 = new ArrayList(cntMap.entrySet());
			Collections.sort(entries1,
				new Comparator() {
					public int compare(Object o1, Object o2) {
						Map.Entry e1 =(Map.Entry)o1;
						Map.Entry e2 =(Map.Entry)o2;
						Integer val1 = (Integer)e1.getValue();
						Integer val2 = (Integer)e2.getValue();
						return val1.compareTo(val2);
					}
				}
			);

			int num = 0; 
			for( int i = entries1.size() - 1; i >= 0; i-- ){
				Map.Entry me = (Map.Entry)entries1.get(i);
				String cid = (String)me.getKey();
				String hitNum = String.valueOf(me.getValue());
				Map<String, String> child = new HashMap();
				child.put("cid", cid);
				child.put("hit", hitNum);
				tempList.put(cid, child);
				if ( ++num == 20 ) {
					break;
				}
			}


			Map<String, Double> scoreMap = new HashMap();
			Iterator it2 = tempList.keySet().iterator();
			while ( it2.hasNext() ) {
				String cid = (String)it2.next();
				Map<String, String> child = tempList.get(cid);
				String hitNum = (String)child.get("hit");

				String sql2 = "select FORMULA, COMPOUND_IDS FROM PEAK_ANNOTATION "
									 + "where COMPOUND_ID='" + cid + "' and ION_MODE='" + this.ionMode + "'";
//				System.out.println(sql2);
				QueryRunner qr2 = new QueryRunner();
				List res2 = qr2.query(con, sql2, new ArrayListHandler());
				String cids = "";
				String annotatedFormula = "";
				for ( int i = 0; i < res2.size(); i++ ) {
					Object[] fields2 = (Object[])res2.get(i);
					String formula = String.valueOf(fields2[0]);
					cids = String.valueOf(fields2[1]);
					if ( i > 0 ) {
						annotatedFormula += ",";
					}
					annotatedFormula += formula;
				}
				String matchedFormula = (String)matchedFormulaList.get(cid);
				child.put("annotatedFormula", annotatedFormula);
				child.put("matchedFormula", matchedFormula);
				child.put("cids", cids);

				String[] annotatedFormulas = annotatedFormula.split(",");
				double dblHitNum = Double.parseDouble(hitNum);
				double rate = dblHitNum / annotatedFormulas.length;
				double score = dblHitNum + rate;
				scoreMap.put(cid, score);
			}

			List entries2 = new ArrayList(scoreMap.entrySet());
			Collections.sort(entries2,
				new Comparator() {
					public int compare(Object o1, Object o2) {
						Map.Entry e1 =(Map.Entry)o1;
						Map.Entry e2 =(Map.Entry)o2;
						Double val1 = (Double)e1.getValue();
						Double val2 = (Double)e2.getValue();
						return val1.compareTo(val2);
					}
				}
			);

			for( int i = entries2.size() - 1; i >= 0; i-- ){
				Map.Entry me = (Map.Entry)entries2.get(i);
				String cid = (String)me.getKey();
				Map<String, String> child = tempList.get(cid);

				String sql3 = "select C.ID, COMPOUND_NAME, EXACT_MASS, FORMULA, MOLFILE FROM COMPOUND_INFO C, MOLFILE M "
										 + "where C.ID='" + cid + "' and C.ID=M.ID order by C.ID";
				QueryRunner qr3 = new QueryRunner();
				List res3 = qr3.query(con, sql3, new ArrayListHandler());
				Object[] fields3 = (Object[])res3.get(0);
				String strcname = String.valueOf(fields3[1]);
				String emass    = String.valueOf(fields3[2]);
				String cformula = String.valueOf(fields3[3]);
				String molfile  = String.valueOf(fields3[4]);
				String[] cnames = strcname.split(";");
				child.put("cname", cnames[0]);
				child.put("emass", emass);
				child.put("cformula", cformula);
				child.put("molfile", molfile);
				results.add(child);
			}
			DbUtils.closeQuietly(con);
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}
		return results;
	}
}

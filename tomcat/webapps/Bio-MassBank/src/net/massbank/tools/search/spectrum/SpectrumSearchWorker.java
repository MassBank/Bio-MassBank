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
 * SpectrumSearchWorker.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.tools.search.spectrum;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import net.massbank.core.BaseWorker;
import net.massbank.core.GetDbUtil;

public class SpectrumSearchWorker extends BaseWorker {
	private int threshold = 3;
	private String[] mzs         = null;
	private String[] intensities = null;
	private String tolerance     = "";
	private String unit          = "";
	private String ionMode       = "";
	private String cutoff        = "";
	private String precursor     = "";
	private String[] instTypes   = null;
	private String[] msTypes     = null;
	private double queryLen = 0;
	private Map<String, Double> queryMzList = new TreeMap();
	private Map<String, List> queryValList  = new HashMap();
	private Map<String, List> hitValList    = new HashMap();
	private Map<String, Integer> hitCntList = new HashMap();

	/**
	 * constructor
	 */
	public SpectrumSearchWorker(String dbName, Map<String, String> params) {
		super(dbName, params);
		SpectrumSearchParameter sp = new SpectrumSearchParameter(params);
		this.mzs         = sp.getMzs();
		this.intensities = sp.getIntensities();
		this.tolerance   = sp.getTolerance();
		this.unit        = sp.getUnitOfTolerance();
		this.ionMode     = sp.getIonMode();
		this.cutoff      = sp.getCutoff();
		this.precursor   = sp.getPrecursor();
		this.instTypes   = sp.getInstrumentTypes();
		this.msTypes     = sp.getMsTypes();
	}

	/**
	 * call
	 */
	@Override public List call() throws Exception {
		setQuery();
		search();
		List<String> scoreList = getScoreList();
		List<Map> results = null;
		int num = scoreList.size();
		if ( num > 0 ) {
			String[] ids = new String[num];
			String[] scores = new String[num];
			for ( int i = 0; i < num; i++ ) {
				String val = scoreList.get(i);
				String[] items = val.split("=");
				ids[i] = items[0];
				scores[i] = items[1];
			}
			results = GetDbUtil.getRecordInfo(con, ids, instTypes, msTypes, ionMode, precursor);
			if ( results != null ) {
				for ( Map map: results ) {
					String id = (String)map.get("ID");
					for ( int i = 0; i < num; i++ ) {
						if ( ids[i].equals(id) ) {
							map.put("SCORE", scores[i]);
							break;
						}
					}
				}
			}
		}
		DbUtils.closeQuietly(con);
		return results;
	}

	/*
	 * set score
	 */
	private List<String> getScoreList()  throws Exception {
		ByteArrayOutputStream buff = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(buff);

		List<String> scoreList = new ArrayList();
		Set keys = this.hitValList.keySet();
		for ( Iterator iterator = keys.iterator(); iterator.hasNext(); ) {
			String strId = (String)iterator.next();
			List<Double> hList = this.hitValList.get(strId);
			List<Double> qList = this.queryValList.get(strId);
			int hitNum = hList.size();
			if ( hitNum <= this.threshold ) {
				continue;
			}
			double sum = 0;
			double len = 0;
			int cnt = 0;
			String sql = "select MZ, RELATIVE from PEAK where ID = '" + strId + "' and RELATIVE >= " + this.cutoff;
			List results = qr.query(con, sql, new ArrayListHandler());
			for ( int i = 0; i < results.size(); i++ ) {
				Object[] fields = (Object[])results.get(i);
				String strMz = String.valueOf(fields[0]);
				String strInte = String.valueOf(fields[1]);
				double dblMz   = Double.parseDouble(strMz);
				int iInte = Integer.parseInt(strInte);
				double dblVal = Math.sqrt(iInte * dblMz * dblMz / 100);
				String key = strId + " " + strMz;
				int mul = 0;
				if ( hitCntList.containsKey(key) ) {
					mul = hitCntList.get(key);
				}
				if ( mul == 0 ) {
					mul = 1;
				}
				len += dblVal * dblVal * mul;
				sum += dblVal * mul;
				cnt += mul;
		 	}
			double score = 0;
			double cos = 0;
			for ( int i = 0; i < hList.size(); i++ ) {
				double hitVal = hList.get(i);
				double queryVal = qList.get(i);
				cos += queryVal * hitVal;
			}
			if ( this.queryLen * len == 0 ) {
				score = 0;
			}
			else {
				score = cos / Math.sqrt(this.queryLen * len);
			}
			if ( score >= 0.9999 ) {
				score = 0.999999999999;
			}
			else if ( score < 0 ) {
				score = 0;
			}
			score += hitNum;
			ps.printf("%s=%.12f", strId, score);
			String val = buff.toString();
			buff.reset();
			scoreList.add(val);
		}
		return scoreList;
	}

	/*
	 * search
	 */
	private void search() throws Exception {
		double min = 0;
		double max = 0;
		Set keys = queryMzList.keySet();
		for ( Iterator iterator = keys.iterator(); iterator.hasNext(); ) {
			String strQueryMz = (String)iterator.next();
			double queryMz = Double.parseDouble(strQueryMz);
			double queryVal = queryMzList.get(strQueryMz);
			double tol = Double.parseDouble(this.tolerance);
			if ( this.unit.equals("unit") ) {
				min = queryMz - tol;
				max = queryMz + tol;
			}
			else {
				min = queryMz * (1 - tol / 1000000);
				max = queryMz * (1 + tol / 1000000);
			}
			min -= 0.00001;
			max += 0.00001;

			String sql = "select ID, MZ, RELATIVE from PEAK"
				+ " where RELATIVE >= " + this.cutoff + " and"
				+ " (MZ between " + String.valueOf(min) + " and " + String.valueOf(max) + ") order by ID,MZ";
			List results = qr.query(con, sql, new ArrayListHandler());
			Map<String, Double> mapMzs = new HashMap();
			Map<String, Integer> mapIntes = new HashMap();
			for ( int j = 0; j < results.size(); j++ ) {
				Object[] fields = (Object[])results.get(j);
				String strHitId  = (String)fields[0];
				double hitMz = (Double)fields[1];
				int hitInte = (Integer)fields[2];


				if ( mapMzs.containsKey(strHitId) ) {
					double mz = mapMzs.get(strHitId);
					double diff1 = Math.abs(hitMz - queryMz);
					double diff2 = Math.abs(mz - queryMz);
					if ( diff1 < diff2 ) {
						mapMzs.put(strHitId, hitMz);
						mapIntes.put(strHitId, hitInte);
					}
				}
				else {
					mapMzs.put(strHitId, hitMz);
					mapIntes.put(strHitId, hitInte);
				}
			}

			Iterator it = mapMzs.keySet().iterator();
			while ( it.hasNext() ) {
				String strHitId = (String)it.next();
				double hitMz = mapMzs.get(strHitId);
				int hitInte = mapIntes.get(strHitId);
				double hitVal = Math.sqrt(hitInte * hitMz * hitMz / 100);

				List hList = null;
				List qList = null;
				if ( this.hitValList.containsKey(strHitId) ) {
					hList = this.hitValList.get(strHitId);
					qList = this.queryValList.get(strHitId);
				}
				else{
					hList = new ArrayList();
					qList = new ArrayList();
				}
				hList.add(hitVal);
				qList.add(queryVal);
				this.hitValList.put(strHitId, hList);
				this.queryValList.put(strHitId, qList);

				String key2 = strHitId + " " + String.valueOf(hitMz);
				int cnt = 0;
				if ( this.hitCntList.containsKey(key2) ) {
					cnt = this.hitCntList.get(key2);
				}
				this.hitCntList.put(key2, ++cnt);
			}
		}
	}

	/*
	 * set query peaks
	 */
	private void setQuery() {
		int cnt = 0;
		for ( int i = 0; i < this.mzs.length; i++ ) {
			double dblMz   = Double.parseDouble(this.mzs[i]);
			double dblInte = Double.parseDouble(this.intensities[i]);
			if ( dblInte < 1 ) {
				dblInte = 1;
			}
			else if ( dblInte > 999 ) {
				dblInte = 999;
			}
			if ( dblInte < Double.parseDouble(this.cutoff) ) {
				continue;
			}
			double val = Math.sqrt(dblInte * dblMz * dblMz / 100);
			if ( val > 0 ) {
				this.queryMzList.put(this.mzs[i], val);
				this.queryLen += val * val;
				cnt++;
			}
		}
		if ( cnt <= this.threshold ) {
			this.threshold = cnt - 1;
		}
	}
}

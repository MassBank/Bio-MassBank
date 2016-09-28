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
 * StatisticsInvoker.java
 *
 * ver 1.0.0 2013.02.15
 *
 ******************************************************************************/
package net.massbank.tools.statistics;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import net.massbank.core.common.BaseInvoker;
import net.massbank.core.common.GetConfig;

public class StatisticsInvoker extends BaseInvoker {
	public static final String REQ_TYPE = "statistics";

	/**
	 * constructor
	 */
	public StatisticsInvoker(String requestUrl) {
		super(requestUrl, REQ_TYPE);
	}

	/**
	 * get results
	 */
	@Override  public StatisticsResult getResults() {
		GetConfig conf = new GetConfig();
		String[] siteNames = conf.getSiteNames();
		Map<String, Integer> contributorList = new TreeMap();
		Map<String, Integer> instTypeList = new TreeMap();
		Map<String, Integer> msTypeList = new TreeMap();
		Map<String, Integer> sampleNameList = new TreeMap();
		String contributor = "";
		int cntContri = 0;
		int cntInst = 0;
		int cntMs = 0;
		int cntPos = 0;
		int cntNeg = 0;
		int cntSample = 0;
		for ( int i = 0; i < super.results.size(); i++ ) {
			String siteNo = "";
			Map<String, Object> map = (Map)super.results.get(i);
			for ( Iterator it = map.keySet().iterator(); it.hasNext(); ) {
				String key = (String)it.next();
				String val = (String)map.get(key);
				String[] items = val.split("=");
				int cnt = 0;
				String name = items[0];
				if ( items.length == 2 ) {
					cnt = Integer.parseInt(items[1]);
				}
				if ( key.equals("CONTRIB") ) {
					cntContri = cnt;
					contributor = "";
				}
				else if ( key.equals("SITE_NO") && contributor.equals("") ) {
					siteNo = val;
					contributor = siteNames[Integer.parseInt(siteNo)];
					contributorList.put(contributor, cntContri);
				}
				else if ( key.equals("INSTRUMENT_TYPE") ) {
					if ( instTypeList.containsKey(name) ) {
						cntInst = instTypeList.get(name) + cnt;
					}
					else {
						cntInst = cnt;
					}
					instTypeList.put(name, cntInst);
				}
				else if ( key.equals("MS_TYPE") ) {
					if ( msTypeList.containsKey(name) ) {
						cntMs = msTypeList.get(name) + cnt;
					}
					else {
						cntMs = cnt;
					}
					msTypeList.put(name, cntMs);
				}
				else if ( key.equals("ION") ) {
					if ( Integer.parseInt(name) > 0 ) {
						cntPos += cnt;
					}
					else {
						cntNeg += cnt;
					}
				}
				else if ( key.equals("SAMPLE") ) {
					if ( msTypeList.containsKey(name) ) {
						cntSample = msTypeList.get(name) + cnt;
					}
					else {
						cntSample = cnt;
					}
					sampleNameList.put(name, cntSample);
				}
			}
		}
		StatisticsResult stats = new StatisticsResult();
		stats.setContributorList(contributorList);
		stats.setInstumentTypeList(instTypeList);
		stats.setMsTypeList(msTypeList);
		stats.setSampleNameList(sampleNameList);
		stats.setIon(cntPos, cntNeg);
		return stats;
	}
}

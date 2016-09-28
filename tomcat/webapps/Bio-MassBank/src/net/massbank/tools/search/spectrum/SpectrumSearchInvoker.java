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
 * SpectrumSearchInvoker.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.tools.search.spectrum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.massbank.core.common.BaseInvoker;
import net.massbank.tools.search.spectrum.SpectrumSearchParameter;
import net.massbank.tools.search.spectrum.SpectrumSearchResult;

public class SpectrumSearchInvoker extends BaseInvoker {
	public static final String REQ_TYPE = "spectrum_search";

	/**
	 * constructor
	 */
	public SpectrumSearchInvoker(String requestUrl, SpectrumSearchParameter sparam) {
		super(requestUrl, REQ_TYPE, sparam.toMap());
	}

	/**
	 * get results
	 */
	@Override public List<SpectrumSearchResult> getResults() {
		if ( super.results == null || super.results.size() == 0 ) {
			return null;
		}
		Map<Integer, Double> scoreList = new HashMap();
		for ( int i = 0; i < super.results.size(); i++ ) {
			Map<String, String> map = (Map)super.results.get(i);
			SpectrumSearchResult info = new SpectrumSearchResult(map);
			String id = info.getId();
			double score = Double.parseDouble(info.getScore());
			scoreList.put(new Integer(i), score);
		}

		List entries = new ArrayList(scoreList.entrySet());
		Collections.sort(entries,
			new Comparator() {
				public int compare(Object o1, Object o2) {
					Map.Entry e1 =(Map.Entry)o1;
					Map.Entry e2 =(Map.Entry)o2;
					Double val1 = ((Double)e1.getValue()) % 1.0;
					Double val2 = ((Double)e2.getValue()) % 1.0;
					return val1.compareTo(val2);
				}
			}
		);

		List<SpectrumSearchResult> resultList = new ArrayList();
		for( int i = entries.size() - 1; i >= 0; i-- ){
			Map.Entry me = (Map.Entry)entries.get(i);
			Integer index = (Integer)me.getKey();
			Map<String, String> map = (Map)super.results.get(index);
			SpectrumSearchResult info = new SpectrumSearchResult(map);
			String id = info.getId();
			String score = info.getScore();
			resultList.add(info);
		}
		return resultList;
	}
}

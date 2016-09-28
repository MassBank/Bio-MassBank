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
 * QuickSearchInvoker.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.tools.search.quick;

import net.massbank.core.common.BaseInvoker;
import net.massbank.core.common.RecordInfoList;
import net.massbank.core.common.RecordSetList;

public class QuickSearchInvoker extends BaseInvoker {
	public static final String REQ_TYPE = "quick_search";

	/**
	 * constructor
	 */
	public QuickSearchInvoker(String requestUrl, QuickSearchParameter sparam) {
		super(requestUrl, REQ_TYPE, sparam.toMap());
	}

	/**
	 * get results
	 */
	@Override public RecordInfoList getResults() {
		if ( super.results == null || super.results.size() == 0 ) {
			return null;
		}
		return new RecordInfoList(super.results);
	}

	/**
	 * get result sets
	 */
	public RecordSetList getResultSets() {
		if ( super.results == null || super.results.size() == 0 ) {
			return null;
		}
		return new RecordSetList(super.results);
	}
}

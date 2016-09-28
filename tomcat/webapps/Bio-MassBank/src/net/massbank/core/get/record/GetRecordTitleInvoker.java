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
 * GetRecordTitleInvoker.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.core.get.record;

import net.massbank.core.common.BaseInvoker;
import net.massbank.core.common.RecordInfoList;


public class GetRecordTitleInvoker extends BaseInvoker {
	public static final String REQ_TYPE = "get_record_title";

	/**
	 * constructor
	 */
	public GetRecordTitleInvoker(String requestUrl) {
		super(requestUrl, REQ_TYPE);
	}

	/**
	 * constructor
	 */
	public GetRecordTitleInvoker(String requestUrl, String[] ids) {
		super(requestUrl, REQ_TYPE, ids);
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
}

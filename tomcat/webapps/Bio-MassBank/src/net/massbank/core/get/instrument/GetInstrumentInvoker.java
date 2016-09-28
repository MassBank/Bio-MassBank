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
 * GetInstrumentInvoker.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.core.get.instrument;

import net.massbank.core.common.BaseInvoker;

public class GetInstrumentInvoker extends BaseInvoker {
	public static final String REQ_TYPE = "get_instrument";

	/**
	 * constructor
	 */
	public GetInstrumentInvoker(String requestUrl) {
		super(requestUrl, REQ_TYPE);
	}

	/**
	 * get results 
	 */
	@Override public GetInstrumentResults getResults() {
		if ( super.results == null || super.results.size() == 0 ) {
			return null;
		}
		return new GetInstrumentResults(super.results);
	}
}

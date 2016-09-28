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
 * DispatchWorker.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.core;

import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Map;

import net.arnx.jsonic.JSON;
import net.massbank.core.common.CoreUtil;

public class DispatchWorker extends BaseWorker {
	private String url = "";

	/**
	 * constructor
	 */
	public DispatchWorker(String url, String dbName, Map<String, String> params) {
		super(params);
		super.params.put("ext_db_name", dbName);
		this.url = url;
	}

	/**
	 * call
	 */
	@Override public List call() {
		List results = null;
		try {
			String res = CoreUtil.invokeDispatcher(url, params);
			if ( res != null && !res.equals("") ) {
				results = (List)JSON.decode(res, Map.class);
			}
		}
		catch (SocketTimeoutException se) {
			System.out.println(this.url + " " + se.getMessage());
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		return results;
	}
}

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
 * BaseInvoker.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.core.common;

import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.arnx.jsonic.JSON;


public class BaseInvoker implements InvokerInterface {
	protected String servletBaseUrl = "";
	protected Map<String, String> params = new TreeMap<String, String>();
	protected List results = null;

	/**
	 * constructor1
	 */
	public BaseInvoker(String requestUrl, String reqType) {
		this.servletBaseUrl = CoreUtil.getBaseUrl(requestUrl);
		this.params.put("type", reqType);
	}

	/**
	 * constructor2
	 */
	public BaseInvoker(String requestUrl, String reqType, String[] ids) {
		setParameter(requestUrl, reqType, ids, null);
	}

	/**
	 * constructor3
	 */
	public BaseInvoker(String requestUrl, String reqType, String[] ids, String siteNo) {
		setParameter(requestUrl, reqType, ids, siteNo);
	}

	/**
	 * constructor4
	 */
	public BaseInvoker(String requestUrl, String reqType, Map<String, String> params) {
		this.servletBaseUrl = CoreUtil.getBaseUrl(requestUrl);
		this.params = params;
		this.params.put("type", reqType);
	}

	/**
	 * invoke
	 */
	public void invoke() throws SocketTimeoutException {
		try {
			String res = CoreUtil.invokeDispatcher(this.servletBaseUrl, this.params);
			if ( res != null && !res.equals("") ) {
				this.results = (List)JSON.decode(res, List.class);
			}
		}
		catch (SocketTimeoutException se) {
			throw se;
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * get results
	 */
	public <T> T getResults() {
		return (T)this.results;
	}

	/*
	 *
	 */
	private void setParameter(String requestUrl, String reqType, String[] ids, String siteNo) {
		this.servletBaseUrl = CoreUtil.getBaseUrl(requestUrl);
		CommonParameter cparam = new CommonParameter();
		cparam.setIds(ids);
		this.params = cparam.toMap();
		this.params.put("type", reqType);
		if ( siteNo != null ) {
			this.params.put("site_no", siteNo);
		}
	}
}

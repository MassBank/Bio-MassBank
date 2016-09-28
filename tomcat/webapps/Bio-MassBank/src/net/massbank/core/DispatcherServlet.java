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
 * DispatcherServlet.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.core;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.arnx.jsonic.JSON;
import net.massbank.core.FileUtil;
import net.massbank.core.common.GetConfig;
import net.massbank.core.get.instrument.GetInstrumentInvoker;
import net.massbank.core.get.instrument.GetInstrumentWorker;
import net.massbank.core.get.record.GetRecordDataInvoker;
import net.massbank.core.get.record.GetRecordDataWorker;
import net.massbank.core.get.record.GetRecordInfoInvoker;
import net.massbank.core.get.record.GetRecordInfoWorker;
import net.massbank.core.get.record.GetRecordSetInvoker;
import net.massbank.core.get.record.GetRecordSetWorker;
import net.massbank.core.get.record.GetRecordTitleInvoker;
import net.massbank.core.get.record.GetRecordTitleWorker;
import net.massbank.core.common.URLSubDirectory;
import net.massbank.admin.GetConfigDirect;
import net.massbank.tools.search.peak.PeakSearchInvoker;
import net.massbank.tools.search.peak.PeakSearchWorker;
import net.massbank.tools.search.quick.QuickSearchInvoker;
import net.massbank.tools.search.quick.QuickSearchWorker;
import net.massbank.tools.search.spectrum.SpectrumSearchInvoker;
import net.massbank.tools.search.spectrum.SpectrumSearchWorker;
import net.massbank.tools.statistics.StatisticsInvoker;
import net.massbank.tools.statistics.StatisticsWorker;

import org.apache.commons.lang.StringUtils;

public class DispatcherServlet extends HttpServlet {
	public static GetConfig conf = null;
	private ResponseCache cache = null;
	public static String appDirName = "";

	/**
	 * 
	 */
	public void init() throws ServletException {
		String appPath = getServletContext().getRealPath("/");
		String separator = File.separator;
		if ( separator.equals("\\") ) {
			separator = "\\\\";
		}
		String[] vals = appPath.split(separator);
		this.appDirName = vals[vals.length-1];
		this.conf = new GetConfigDirect(appPath);
		this.cache = new ResponseCache();
		String urls[] = this.conf.getSiteUrls();
		if ( urls[0].indexOf("/" + URLSubDirectory.SUBDIR_NAME ) == -1 ) {
			URLSubDirectory.status = URLSubDirectory.DONT_NEED;
		}
		else {
			URLSubDirectory.status = URLSubDirectory.NEED;
		}
	}

	/**
	 * receives HTTP requests
	 */
	public void service(HttpServletRequest req, HttpServletResponse res)
		throws ServletException, IOException {

		String[] siteUrls = this.conf.getSiteUrls();
		String[] dbNames = this.conf.getDbNames();

		PrintWriter out = res.getWriter();
		String type = "";
		boolean isDirect = false;
		boolean isForward = false;
		String extDbName = "";
		String siteNo = "";
		String queryString = "";
		Map<String, String> params = new TreeMap();
		Enumeration names = req.getParameterNames();
		while ( names.hasMoreElements() ) {
			String key = (String)names.nextElement();
			String val = req.getParameter(key);
			if ( key.equals("type") ) {
				type = val;
			}
			else if ( key.equals("ext_db_name") ) {
				isForward = true;
				extDbName = val;
			}
			else if ( key.equals("site_no") && !val.equals("") ) {
				isDirect = true;
				siteNo = val;
			}
			else {
				params.put(key, val);
			}
			queryString += key + "=" + val + "&";
		}
		queryString = StringUtils.chompLast(queryString, "&");

		String response = null;
		try {
			response = this.cache.getValue(queryString);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		if ( response == null ) {
			List<Callable<List>> taskList = new ArrayList();
			Callable<List> task = null;
			if ( isForward ) {
				task = createWorker(type, extDbName, params);
			}
			else {
				String myServerUrl = siteUrls[0];
				for ( int i = 0; i < siteUrls.length; i++ ) {
					if ( isDirect && i != Integer.parseInt(siteNo) ) {
						continue;
					}
					String url = siteUrls[i];
					String dbName = dbNames[i];
					if ( !myServerUrl.equals(url) ) {
						task = new DispatchWorker(url, dbName, params);
					}
					else {
						task = createWorker(type, dbName, params);
					}
					taskList.add(task);
				}
			}

			try {
				ExecutorService exsv = Executors.newFixedThreadPool(2);
				List<Future<List>> resultsList = exsv.invokeAll(taskList);
				List<Map> resultsListNew = new ArrayList();
				int siteNumber = 0;
				if ( isDirect ) {
					siteNumber = Integer.parseInt(siteNo);
				}
				for ( Future<List> future: resultsList ) {
					if ( future != null ) {
						List<Map> results = future.get();
						if ( results != null && results.size() > 0 ) {
							for ( Map<String, String> map: results ) {
								map.put("SITE_NO", String.valueOf(siteNumber));
								resultsListNew.add(map);
							}
						}
					}
					siteNumber++;
				}
				response = JSON.encode(resultsListNew);
				this.cache.setValue(queryString, response);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		out.print(response);
	}


	/**
	 * create workers instance
	 */
	private Callable<List> createWorker(String type, String dbName, Map<String, String> params) {
		Callable<List> task = null;
		if ( type.equals(GetInstrumentInvoker.REQ_TYPE)       ) { task = new GetInstrumentWorker(dbName, params);  }
		else if ( type.equals(GetRecordInfoInvoker.REQ_TYPE)  ) { task = new GetRecordInfoWorker(dbName, params);  }
		else if ( type.equals(GetRecordSetInvoker.REQ_TYPE)   ) { task = new GetRecordSetWorker(dbName, params);   }
		else if ( type.equals(GetRecordDataInvoker.REQ_TYPE)  ) { task = new GetRecordDataWorker(dbName, params);  }
		else if ( type.equals(GetRecordTitleInvoker.REQ_TYPE) ) { task = new GetRecordTitleWorker(dbName, params); }
		else if ( type.equals(PeakSearchInvoker.REQ_TYPE) )     { task = new PeakSearchWorker(dbName, params);     }
		else if ( type.equals(QuickSearchInvoker.REQ_TYPE) )    { task = new QuickSearchWorker(dbName, params);    }
		else if ( type.equals(SpectrumSearchInvoker.REQ_TYPE) ) { task = new SpectrumSearchWorker(dbName, params); }
		else if ( type.equals(StatisticsInvoker.REQ_TYPE) )     { task = new StatisticsWorker(dbName, params);     }
		return task;
	}
}

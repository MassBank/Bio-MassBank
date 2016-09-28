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
 * CoreUtil.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.core.common;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import net.massbank.core.common.URLSubDirectory;

public class CoreUtil {

	/**
	 * invoke DispatcherServlet
	 */
	public static String invokeDispatcher(String servletBaseUrl, Map<String, String> param)
		throws Exception {
		if ( servletBaseUrl.equals("") ) {
			return null;
		}
		GetConfig conf = new GetConfig(servletBaseUrl);
		int timeout = conf.getTimeout();

		String reqParam = "";
		int num = param.size();
		if ( num > 0 ) {
			int cnt = 0;
			Iterator it = param.keySet().iterator();
			while ( it.hasNext() ) {
				String key = (String)it.next();
				String val = param.get(key);
				reqParam += key + "=" + val;
				if ( ++cnt < num ) {
					reqParam += "&";
				}
			}
		}

		StringBuilder sb = new StringBuilder("");
		if ( !servletBaseUrl.endsWith("/") ) {
			servletBaseUrl += "/";
		}
		if ( URLSubDirectory.status == URLSubDirectory.DONT_NEED ) {
			servletBaseUrl = servletBaseUrl.replace(URLSubDirectory.SUBDIR_NAME + "/", "" );
		}
		String reqUrl = servletBaseUrl + "DispatcherServlet";
		URL url = new URL(reqUrl);
		HttpURLConnection con = (HttpURLConnection)url.openConnection();
		con.setDoOutput(true);
		con.setConnectTimeout(timeout * 1000);
		con.setReadTimeout(timeout * 1000);
		PrintStream psm = new PrintStream(con.getOutputStream());
		psm.print(reqParam);

		InputStreamReader is = new InputStreamReader(con.getInputStream(), "UTF-8");
		BufferedReader br = new BufferedReader(is);
		boolean isStartSpace = true;
		String line = "";
		while ( (line = br.readLine()) != null ) {
	 		if ( isStartSpace ) {
				if ( line.equals("") ) {
					continue;
				}
				else {
					isStartSpace = false;
				}
			}
			if ( !line.equals("") ) {
				sb.append(line);
			}
		}
		br.close();
		con.disconnect();
		return sb.toString();
	}

	/**
	 * get base url
	 */
	public static String getBaseUrl(String requestUrl) {
		return getBaseUrl(requestUrl, 1);
	}

	/**
	 * get base url
	 */
	public static String getBaseUrl(String requestUrl, int level) {
		String url = "";
		int pos1 = requestUrl.indexOf("/", new String("http://").length());
		if ( pos1 == -1 ) {
			return requestUrl;
		}
		int pos2 = 0;
		for ( int i = 0; i < level; i++  ) {
			pos2 = requestUrl.indexOf("/", pos1 + 1);
			if ( pos2 == -1 ) {
				pos2 = pos1;
			}
			pos1 = pos2;
		}
		return requestUrl.substring(0, pos2 + 1);
	}

	/**
	 *
	 */
	public static String padMolfile(String molfile) {
		StringBuilder moldata = new StringBuilder();
		String[] lines = molfile.split("\n");
		for ( int j = 0; j < lines.length; j++ ) {
			String line = lines[j];
			if ( j < 3 && line.trim().equals("") ) {
				moldata.append("*\n");
			}
			else {
				moldata.append(line + "\n");
			}
		}
		return moldata.toString();
	}

	/**
	 *
	 */
	public static double getNumber(String val) {
		double num = 0;
		if ( val != null ) {
			try {
				num = Double.parseDouble(val);
			}
			catch (Exception e) {
			}
		}
		return num;
	}

}

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
 * CookieManager.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.applet.search;

import java.applet.Applet;
import java.util.ArrayList;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;
import netscape.javascript.JSUtil;

public class CookieManager {

	private boolean isCookie = false;
	private JSObject win;
    private JSObject doc;
	private String cookieName = "Applet";
	private int expDate = 30;


	/**
	 *
	 */
	public CookieManager(Applet applet, String name, int expDate, boolean isCookie) {
		try {
			this.win = JSObject.getWindow(applet);
			this.doc = (JSObject)win.getMember("document");
			
			if (!name.trim().equals("")) {
				this.cookieName = name;
			}
			this.expDate = expDate;
			this.isCookie = isCookie;
			
			if ( !this.isCookie ) {
				updateCookie("");
			}
		}
		catch (JSException e) {
			System.out.println("browser window object doesn't exist.");
			System.out.println(JSUtil.getStackTrace(e));
			return;
		}
	}
	
	/**
	 *
	 */
	public boolean setCookie(String key, ArrayList<String> valueList) {
		if ( !isCookie || win == null || doc == null ) {
			return false;
		}
		
		String param = "";
		String values = getCookie();
		if (values.trim().length() != 0) {
			String[] data = values.split(";");
			for (int i=0; i<data.length; i++) {
				if (!data[i].split("=")[0].trim().equals(key)) {
					param += data[i] + ";";
				}
			}
		}
		
		if (valueList.size() != 0) {
			param += key + "=";
			for (int i=0; i<valueList.size(); i++) {
				param += valueList.get(i);
				if (i+1 < valueList.size()) {
					param += ",";
				}
			}
			param += ";";
		}
		
		return updateCookie(param);
	}
	
	/**
	 *
	 */
	private boolean updateCookie(String param) {
		try {
			JSObject date = (JSObject)win.eval("new Date()");
			Double time = Double.parseDouble(String.valueOf(date.call("getTime", null)));
			
			if ( !param.equals("") ) {
				time += ((double)expDate * 24d * 60d * 60d * 1000d);
			} else {
				time -= (double)expDate;
			}
			try {
				time = Double.parseDouble(String.valueOf(date.call("setTime", new Object[]{time})));
			}
			catch (Exception e) {
				time = Double.parseDouble(String.valueOf(date.eval("setTime(" + String.valueOf(time) + ")")));
			}
			String gmtTime = String.valueOf(date.call("toGMTString", null));
			
			String paramVal = cookieName + "=" + win.call("escape", new Object[]{param});
			String timeVal = "expires=" + gmtTime;
			String cookieVal = paramVal + "; " + timeVal;
			doc.setMember("cookie", cookieVal);
		}
		catch (JSException jse) {
			System.out.println("Unsupported javascript was used.");
			System.out.println(JSUtil.getStackTrace(jse));
			return false;
		}
		return true;
	}
	
	/**
	 *
	 */
	private String getCookie() {
		
		String values = "";
		try {
			String tmpAllCookie = (String)doc.getMember("cookie");
			if (tmpAllCookie != null) {
				String[] allCookie = tmpAllCookie.split(";");
				String[] tmp;
				for (int i=0; i<allCookie.length; i++) {
					tmp = allCookie[i].split("=");
					if (tmp[0].trim().equals(cookieName)) {
						if (tmp.length == 2) {
							try {
								values = String.valueOf(win.eval("unescape('" +  tmp[1].trim() +"')"));
							}
							catch (JSException e) {
								values = String.valueOf(win.call("unescape", new Object[]{tmp[1].trim()}));
							}
						}
						break;
					}
				}
			}
		}
		catch (JSException e) {
			System.out.println(JSUtil.getStackTrace(e));
			values = "";
		}
		
		return values;
	}
	
	/**
	 *
	 */
	public ArrayList<String> getCookie(String key) {
		
		ArrayList<String> valueList = new ArrayList<String>();
		
		if ( !isCookie || win == null || doc == null ) {
			return valueList;
		}
		
		String values = getCookie();
		
		
		String val = "";
		if (values.trim().length() != 0) {
			String[] data = values.split(";");
			String[] item;
			for (int i=0; i<data.length; i++) {
				item = data[i].split("=");
				if (item[0].trim().equals(key)) {
					if (item.length == 2) {
						val = item[1].trim();
					}
					break;
				}
			}
		}
		String[] tmp = val.split(",");
		for (int i=0; i<tmp.length; i++) {
			if (!tmp[i].equals("")) {
				valueList.add(tmp[i]);
			}
		}
		
		return valueList;
	}
}

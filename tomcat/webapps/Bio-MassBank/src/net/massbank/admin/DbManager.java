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
 * DbManager.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.admin;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import javax.servlet.jsp.JspWriter;
import net.massbank.core.common.GetConfig;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.NumberUtils;
import org.apache.commons.lang.StringUtils;

public class DbManager {
	public static final String NO = "NO";
	public static final String DB_NAME = "DB Name";
	public static final String SHORT_LABEL = "Short Label";
	public static final String LONG_LABEL = "Long Label";
	public static final String URL = "URL";
	public static final String URL_TYPE = "URL Type";
	private AdminInfo adminInfo = new AdminInfo();
	private HashMap<String, String> inputs = null;
	private int inputNo = 0;
	private String inputDbName  = "";
	private String inputSlabel  = "";
	private String inputLlabel  = "";
	private String inputUrl     = "";
	private boolean inputType = false;
	private SetConfig setConf = null;
	private JspWriter out = null;

	/**
	 * constructor
	 */
	public DbManager(HashMap<String, String> inputs, String rootPath, JspWriter out) {
		this.inputs = inputs;
		this.inputNo     = NumberUtils.stringToInt(inputs.get("siteNo"));
		this.inputDbName = inputs.get("siteDb");
		this.inputSlabel = inputs.get("siteShortLabel");
		this.inputLlabel = inputs.get("siteLongLabel");
		this.inputUrl    = inputs.get("siteUrl");
		this.inputType   = BooleanUtils.toBoolean(inputs.get("siteType"));
		this.setConf = new SetConfig(rootPath);
		this.out = out;
	}

	/**
	 * add
	 */
	public boolean add() {
		if ( !checkInputValue(true) ) {
			return false;
		}
		String sql = "create database " + this.inputDbName;
		if ( !AdminDbUtil.updateDB("", sql) ) {
			return false;
		}
		GetConfig conf = new GetConfig();
		String[] confDbNames = conf.getDbNames();
		String[] tableNames = { "PEAK", "RECORD", "COMPOUND_INFO", "COMPOUND_NAMES", "INSTRUMENT" };
		for ( String tableName : tableNames ) {
			sql = "create table " + this.inputDbName
				+ "." + tableName+ " like " + confDbNames[0] + "." + tableName;
			if ( !AdminDbUtil.updateDB("", sql) ) {
				return false;
			}
		}
		return this.setConf.add(this.inputSlabel, this.inputLlabel, this.inputUrl, this.inputDbName);
	}


	/*
	 * edit
	 */
	public boolean edit() {
		if ( !checkInputValue(false) ) {
			return false;
		}
		ArrayList<Integer> internalNoList = new ArrayList();
		int[] indexes = null;
		if ( this.inputNo == 0 ) {
			GetConfig conf = new GetConfig();
			String[] urls = conf.getSiteUrls();
			String serverUrl = urls[0];
			for ( int i = 0; i < urls.length; i++ ) {
				if ( serverUrl.equals(urls[i]) ) {
					internalNoList.add(i);
				}
			}
			indexes = new int[internalNoList.size()];
			for ( int i = 0; i < internalNoList.size(); i++ ) {
				indexes[i] = internalNoList.get(i);
			}
		}
		else {
			indexes = new int[]{this.inputNo};
		}
		return this.setConf.update(indexes, this.inputSlabel, this.inputLlabel, this.inputUrl);
	}

	/**
	 * delete
	 */
	public boolean delete() {
		GetConfig conf = new GetConfig();
		String[] dbNames = conf.getDbNames();
		String sql = "drop database " + dbNames[this.inputNo];
		if ( !AdminDbUtil.updateDB("", sql) ) {
			return false;
		}
		return this.setConf.delete(this.inputNo);
	}


	/*
	 * get informations
	 */
	public String[] getInformations() {
		return this.adminInfo.getList();
	}

	/**
	 * check input values
	 */
	private boolean checkInputValue(boolean isCheckDB) {
		boolean ret = true;
		String msg = "";
		Collection vals = this.inputs.values();
		Set keySet = this.inputs.keySet();
		Iterator ite = keySet.iterator();
		while ( ite.hasNext() ) {
			Object key = ite.next();
			String val = (String)this.inputs.get(key);
			if ( StringUtils.isBlank(val) ) {
				adminInfo.add(AdminInfo.INFO_LEVEL_ERROR, msg, "");
				ret = false;
			}
			else if ( !StringUtils.isAsciiPrintable(val) ) {
				adminInfo.add(AdminInfo.INFO_LEVEL_ERROR, msg, "" );
				ret = false;
			}
		}

		boolean isUrl = true;
		try {
			new URL(this.inputUrl);
			if ( this.inputUrl.replaceAll("http://", "").replaceAll("https://", "").trim().equals("") ) {
				isUrl = false;
			}
		}
		catch ( MalformedURLException e) {
			isUrl = false;
		}
		if ( !isUrl ) {
			try {
				out.println( "<font color=\"red\"><b>The URL is not correct.</b></font><br>" );
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			ret = false;
		}

		if ( isCheckDB ) {
			String[] chars = { "\\", "/", ":", "*", "?", "\"", "<", ">", "|", "." };
			if ( StringUtils.indexOfAny(this.inputDbName, chars) != -1 ) {
				msg = DB_NAME + "contains illegal characters";
				adminInfo.add(AdminInfo.INFO_LEVEL_ERROR, msg, "" );
				ret = false;
			}
			else if ( AdminDbUtil.existDB(this.inputDbName) ) {
				try {
					out.println( "<font color=\"red\"><b>Can't create database '" + this.inputDbName + "' as it already exists.</b></font><br>" );
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				ret = false;
			}
		}
		return ret;
	}
}

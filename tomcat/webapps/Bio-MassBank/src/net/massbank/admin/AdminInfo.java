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
 * AdminInfo.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.admin;

import java.util.ArrayList;
import java.util.List;

public class AdminInfo {
	public final static String INFO_LEVEL_ERROR = "Error";
	public final static String INFO_LEVEL_WARN  = "Warning";
	public final static String INFO_LEVEL_INFO  = "Information";
	private List<String> infoList = new ArrayList();

	/**
	 * constructor
	 */
	public AdminInfo() {
	}

	/*
	 * add information list
	 */
	public void add(String level, String msg, String info) {
		String style = "msgFont";
		if ( level.equals(INFO_LEVEL_ERROR) ) {
			style = "errFont";
		}
		else if ( level.equals(INFO_LEVEL_WARN) ) {
			style = "warnFont";
		}
		String result = "<i>" + level + "</i>&nbsp;:&nbsp;"
				+ "<span class=\"" + style + "\">" + msg + "</span>&nbsp;&nbsp;" + info + "<br>";
		this.infoList.add(result);
	}


	/*
	 * get information list
	 */
	public String[] getList() {
		return this.infoList.toArray(new String[]{});
	}
}
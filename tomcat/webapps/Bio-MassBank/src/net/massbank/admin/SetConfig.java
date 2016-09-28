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
 * SetConfig.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.admin;

import java.io.File;
import java.util.List;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

public class SetConfig {
	private static XMLConfiguration conf = null;
	private String path = "";

	/**
	 * constructor
	 */
	public SetConfig(String rootPath) {
		this.path =  rootPath + File.separator + "massbank.conf";
		try {
			this.conf = new XMLConfiguration(path);
		}
		catch ( ConfigurationException e ) {
			e.printStackTrace();
		}
	}

	/**
	 * add setting
	 */
	public boolean add(String sLabel, String lLabel, String url, String dbName) {
		List<Object> list = this.conf.getList("Related.Name");
		this.conf.addProperty("Related", "");
		String key = "Related(" + String.valueOf(list.size()) + ")";
		this.conf.addProperty(key + ".Name", sLabel);
		this.conf.addProperty(key + ".LongName", lLabel);
		this.conf.addProperty(key + ".URL", url);
		this.conf.addProperty(key + ".DB", dbName);
		return save();
	}

	/**
	 * update setting
	 */
	public boolean update(int[] indexes, String sLabel, String lLabel, String url) {
		int cnt = indexes.length;
		for ( int i = 0; i < cnt; i++ ) {
			int index = indexes[i];
			if ( index == 0 ) {
				this.conf.setProperty("MyServer.Name", sLabel);
				this.conf.setProperty("MyServer.LongName", sLabel);
				this.conf.setProperty("MyServer.URL", url);
			}
			else {
				String key = "Related(" + String.valueOf(index - 1) + ")";
				if ( cnt == 1 ) {
					this.conf.setProperty(key + ".Name", sLabel);
					this.conf.setProperty(key + ".LongName", lLabel);
				}
				this.conf.setProperty(key + ".URL", url);
			}
		}
		return save();
	}

	/**
	 * delete setting
	 */
	public boolean delete(int index) {
		if ( index == 0 ) {
			return false;
		}
		String key = "Related(" + String.valueOf(index - 1) + ")";
		this.conf.clearProperty(key + ".Name");
		this.conf.clearProperty(key + ".LongName");
		this.conf.clearProperty(key + ".URL");
		this.conf.clearProperty(key + ".DB");
		this.conf.clearProperty(key);
		return save();
	}

	/**
	 * Saves the configuration 
	 */
	private boolean save() {
		try {
			this.conf.save();
			return true;
		}
		catch ( Exception e ) {
			e.printStackTrace();
			return false;
		}
	}
}

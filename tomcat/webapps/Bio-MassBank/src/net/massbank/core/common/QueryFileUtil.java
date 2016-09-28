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
 * QueryFileUtil.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.core.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class QueryFileUtil {
	private List<String> nameList = new ArrayList();
	private List<String> peakList = new ArrayList();
	private String filePath = "";

	/**
	 * constructor
	 */
	public QueryFileUtil(String filePath) throws Exception {
		this.filePath = filePath;
		File f = new File(filePath);
		FileReader in = new FileReader(f);
		read(in);
	}

	/**
	 * constructor
	 */
	public QueryFileUtil(URL url) throws Exception {
		URLConnection con = url.openConnection();
		InputStreamReader in = new InputStreamReader(con.getInputStream());
		read(in);
	}

	/**
	 * get query names
	 */
	public String[] getQueryNames() {
		return this.nameList.toArray(new String[]{});
	}

	/**
	 * get m/z
	 */
	public String[] getPeaks() {
		return this.peakList.toArray(new String[]{});
	}

	/**
	 * delete temporary file
	 */
	public void delete() {
		if ( this.filePath.equals("") ) {
			return;
		}
		File f = new File(this.filePath);
		if ( f.exists() ) {
			f.delete();
		}
	}

	/**
	 * read
	 */
	private void read(Reader in) throws Exception {
		String line = "";
		String name = "";
		String peak = "";
		int lineNo = 0;
		int cnt = 1;

		List<String[]> queryList = new ArrayList();
		File f = new File(filePath);
		BufferedReader br = new BufferedReader(in);
		while ( ( line = br.readLine() ) != null ) {
			line = line.trim();
			if ( line.startsWith("//") ) {
				continue;
			}
			else if ( line.matches("^Name:.*") ) {
				name = line.replaceFirst("^Name: *", "").trim();
			}
			else if ( line.matches(".*:.*") ) { }
			else if ( line.equals("") ) {
				if ( lineNo > 0 ) {
					addList(name, peak);
					cnt++;
					name = "";
					peak = "";
					lineNo = 0;
				}
			}
			else {
				peak += line;
				if ( !line.substring(line.length()-1).equals(";") ) {
					peak += ";";
				}
				lineNo++;
			}
		}
		in.close();
		if ( lineNo > 0 ) {
			addList(name, peak);
		}
	}

	/**
	 * add list
	 */
	private void addList(String name, String peak) {
		DecimalFormat df = new DecimalFormat("000000");
		int no = this.nameList.size() + 1;
		if ( name.equals("") ) {
			name = "Compound_" + df.format(no);
		}
		this.nameList.add(name);

		String[] lines = peak.split(";");
		String peak2 = "";
		for ( String line: lines ) {
			String val = line.trim();
			if ( !val.equals("") ) {
				String val2 = val.replaceAll(" +", ",");
				val2 = val2.replaceAll("\t+", ",");
				peak2 += val2 + ";";
			}
		}
		this.peakList.add(peak2);
	}
}

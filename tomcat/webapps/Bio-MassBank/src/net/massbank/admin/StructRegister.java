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
 * StructRegister.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.admin;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.massbank.core.FileUtil;
import net.massbank.core.GetDbUtil;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.io.FileUtils;

public class StructRegister {
	private Connection con = null;
	private String dbName = "";
	private String dataPath = "";
	private String[] fileNames = null;
	private AdminInfo adminInfo = new AdminInfo();
	private final String LIST_FILE_NAME = "list.tsv";

	/**
	 * constructor
	 */
	public StructRegister(String dbName, String dataPath) {
		this.dbName = dbName;
		this.dataPath = dataPath;
	}

	/*
	 * execution of the registration
	 */
	public boolean execute() {
		if ( this.dbName.equals("") ) {
			return false;
		}
		String msg = "";
		List<String> tsvList = parseListFile();
		if ( tsvList == null || tsvList.size() == 0 ) {
			msg = "list.tsv invalid";
			adminInfo.add(AdminInfo.INFO_LEVEL_ERROR, msg, "");
			return false;
		}
		List<CompoundInfo> compoundInfoList = AdminDbUtil.getCompoundInfoList(this.dbName);
		if ( compoundInfoList == null ) {
			msg = "Internal error (failed to execute SQL)";
			adminInfo.add(AdminInfo.INFO_LEVEL_WARN, msg, "getCompoundInfoList");
			return false;
		}

		List<String> registList = new ArrayList();
		for ( String line : tsvList ) {
			String[] vals = line.split("\t");
			String cname1   = vals[0];
			String fileName = vals[1];
			boolean isFound = false;
			for ( CompoundInfo info : compoundInfoList ) {
				int cno        = info.getCompoundNo();
				String cname2  = info.getCompoundName();
				String moldata = info.getMolfileData();
				String[] items = cname2.split("/");
				if ( cname1.equals(cname2) || cname1.equals(items[items.length-1].trim()) ) {
					if ( moldata.equals("") ) {
						registList.add(String.valueOf(cno) + "\t" + line);
					}
					else {
						msg = "Duplicate entry";
						adminInfo.add(AdminInfo.INFO_LEVEL_WARN, msg, "compound name:<b>" + cname1 + "</b>");
					}
					isFound = true;
					break;
				}
			}
			if ( !isFound ) {
				msg = "unregistered record";
				adminInfo.add(AdminInfo.INFO_LEVEL_WARN, msg, "compound name:<b>" + cname1 + "</b>");
			}
		}
		boolean ret = registerMolfile(registList);
		return ret;
	}

	/*
	 * get informations
	 */
	public String[] getInformations() {
		return this.adminInfo.getList();
	}

	/*
	 * parse list.tsv
	 */
	private List<String> parseListFile() {
		Map<String, String> map = new HashMap();
		List<String> list = new ArrayList();
		String filePath = this.dataPath + File.separator + LIST_FILE_NAME;
		String fullText = "";
		try {
			fullText = FileUtils.readFileToString(new File(filePath));
		}
		catch ( IOException e ){
			e.printStackTrace();
			String msg = "Internal error (failed to read file)";
			adminInfo.add(AdminInfo.INFO_LEVEL_ERROR, msg, filePath);
		}
		String[] lines = fullText.split("\n");
		for ( int i = 0; i < lines.length; i++ ) {
			String line = lines[i].trim();
			String[] vals = line.split("\t");
			if ( vals.length != 2 ) {
				continue;
			}
			String cname = vals[0];
			String fileName = vals[1];
			if ( map.containsKey(cname) || map.containsValue(fileName) ) {
				String msg = "Duplicate entry";
				String info = "list.tsv line:" + String.valueOf(i+1) + "&nbsp;&nbsp;[<i>" + line + "</i>&nbsp;]";
				adminInfo.add(AdminInfo.INFO_LEVEL_WARN, msg,  info );
			}
			else {
				map.put(cname, fileName);
				list.add(cname + "\t" + fileName);
			}
		}
		return list;
	}



	/**
	 * register molfile
	 */
	private boolean registerMolfile(List<String> registList) {
		String sql = "";
		String msg = "";
		Connection con = null;
		String fileName = "";
		try {
			con = GetDbUtil.connectDb(this.dbName);
			con.setAutoCommit(false);
			QueryRunner qr = new QueryRunner();
			for ( String line : registList ) {
				String[] items = line.split("\t");
				String cno      = items[0];
				String cname1   = items[1];
				fileName        = items[2];
				String filePath = this.dataPath + File.separator + fileName;
				File file = new File(filePath);
				if ( !file.exists() ) {
					msg = "Molfile not found";
					adminInfo.add(AdminInfo.INFO_LEVEL_WARN, msg, fileName);
				}
				String moldata = FileUtils.readFileToString(new File(filePath));
				sql = "update COMPOUND_INFO set MOLFILE=? where COMPOUND_NO=?";
				Object[] vals = new Object[]{ moldata, Integer.parseInt(cno) };
				int cnt = qr.update(con, sql, vals);
			}
			DbUtils.commitAndCloseQuietly(con);
			return true;
		}
		catch ( SQLException e ) {
			msg = "Internal error (failed to execute SQL)";
			adminInfo.add(AdminInfo.INFO_LEVEL_ERROR, msg, sql);
			e.printStackTrace();
		}
		catch ( IOException e ) {
			msg = "Internal error (failed to read file)";
			adminInfo.add(AdminInfo.INFO_LEVEL_ERROR, msg, fileName);
			e.printStackTrace();
		}

		try {
			if ( con != null ) {
				DbUtils.rollback(con);
			}
		}
		catch ( SQLException e2 ) {
			e2.printStackTrace();
		}
		DbUtils.closeQuietly(con);
		return false;
	}
}

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
 * CreateStructureImage.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.admin;

import java.io.File;
import java.sql.Connection;
import java.util.List;
import java.util.TreeMap;

import net.massbank.core.FileUtil;
import net.massbank.core.GetDbUtil;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.io.FileUtils;

public class CreateStructureImage implements Runnable {

	/**
	 *
	 */
	public CreateStructureImage() {
	}

	/**
	 * run
	 */
	public void run() {
		String[] sizes = { "100", "180", "600" };
		try {
			List<String> dbList = AdminDbUtil.getExistDB();
			for ( int i = 0; i < dbList.size(); i++ ) {
				String dbName = dbList.get(i);
				String sPath = FileUtil.getStructureImagePath(dbName, "S");
				String mPath = FileUtil.getStructureImagePath(dbName, "M");
				String lPath = FileUtil.getStructureImagePath(dbName, "L");
				String[] paths = { sPath, mPath, lPath };
				for ( int j = 0; j < paths.length; j++ ) {
					String path = paths[j];
					FileUtils.forceMkdir(new File(path));
					File dir = new File(path);
					File[] files = dir.listFiles();
					TreeMap<String, String> compoundNoList = new TreeMap();
					for ( int k = 0; k < files.length; k++ ) {
						File file = files[k];
						String fileName = file.getName();
						String cno = fileName.replace(".gif", "");
						compoundNoList.put(cno, "");
					}
					Connection con = GetDbUtil.connectDb(dbName);
					String sql = "select COMPOUND_NO, MOLFILE from COMPOUND_INFO where MOLFILE<>'' order by COMPOUND_NO";
					QueryRunner qr = new QueryRunner();
					List results = qr.query(con, sql, new ArrayListHandler());
					for ( int k = 0; k < results.size(); k++ ) {
						Object[] items = (Object[])results.get(k);
						String cno = items[0].toString();
						String moldata = items[1].toString();
						if ( !compoundNoList.containsKey(cno) ) {
							String outPath = paths[j] + cno + ".gif";
							exec(moldata, outPath, sizes[j]);
						}
					}
					DbUtils.closeQuietly(con);
				}
			}
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}
	}

	/**
	 *
	 */
	public boolean exec(String moldata, String outPath, String size) {
		String rootPath = FileUtil.getAppRootPath();
		moldata = moldata.replaceAll("=", "");
		moldata = moldata.replace("\r", "");
		moldata = moldata.replace("\n", "@LF@");
		String cmd = "java -jar \"" + rootPath + "applet" + File.separator
					+ "MolView.jar\" size=" + size + " out_gif_path=" + outPath + " moldata=\"" + moldata + "\"";
		CommandLine cmdLine = CommandLine.parse(cmd);
		DefaultExecutor executor = new DefaultExecutor();
		try {
			executor.setExitValue(0);
			executor.execute(cmdLine);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}
}

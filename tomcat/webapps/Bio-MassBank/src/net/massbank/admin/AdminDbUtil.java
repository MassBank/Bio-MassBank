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
 * AdminDbUtil.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.admin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.massbank.core.GetDbUtil;
import net.massbank.core.common.GetConfig;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.lang.StringUtils;

public class AdminDbUtil {

	/*
	 * get exist database
	 */
	public static List<String> getExistDB() throws Exception {
		GetConfig conf = new GetConfig();
		String[] confDbNames = conf.getDbNames();
		String[] dbNames = getDBNames();
		ArrayList<String> existDbList = new ArrayList();
		for ( String name : dbNames ) {
			String dbName = name.toLowerCase();
			for ( int j = 0; j < confDbNames.length; j++ ) {
				if ( dbName.equals(confDbNames[j].toLowerCase()) ) {
					existDbList.add(confDbNames[j]);
					break;
				}
			}
		}
		Collections.sort(existDbList);
		return existDbList;
	}

	/*
	 * get instrument information
	 */
	public static List<Map> getInstrument(Connection con) throws Exception {
		String sql = "select * from INSTRUMENT order by INSTRUMENT_NO";
		QueryRunner qr = new QueryRunner();
		List<Map> results = (List)qr.query(con, sql, new MapListHandler());
		return results;
	}

	/*
	 * get registerd record list
	 */
	public static Map<String, Map> getRegisteredRecordList(String selDbName) throws Exception {
		Connection con = GetDbUtil.connectDb(selDbName);
		String sql = "select ID, COMPOUND_NAME, INSTRUMENT_TYPE, MS_TYPE, TITLE_ADDITION "
			+ "from (select ID, COMPOUND_NAME, INSTRUMENT_NO, MS_TYPE, TITLE_ADDITION "
			+ "from RECORD R left join COMPOUND_INFO C on R.COMPOUND_NO=C.COMPOUND_NO) as M "
			+ "left join INSTRUMENT I on M.INSTRUMENT_NO=I.INSTRUMENT_NO group by ID order by ID";
		QueryRunner qr = new QueryRunner();
		List<Map> res = (List)qr.query(con, sql, new MapListHandler());

		Map<String, Map> recordList = new TreeMap();
		for ( int i = 0; i < res.size(); i++ ) {
			Map<String, Object> map1 = res.get(i);
			String id       = (String)map1.get("ID");
			String cname    = (String)map1.get("COMPOUND_NAME");
			String instType = (String)map1.get("INSTRUMENT_TYPE");
			String msType   = (String)map1.get("MS_TYPE");
			String addition = (String)map1.get("TITLE_ADDITION");

			String info = "";
			if ( cname == null ) {
				info += "unregistered [<i>CH$NAME</i>]";
				cname = "N/A";
			}
			if ( instType == null ) {
				if ( !info.equals("") ) {
					info += "<br>";
				}
				info += "unregistered [<i>INSTRUMENT</i>]";
				instType = "N/A";
			}
			String title = "";
			if ( cname != null ) {
				title = cname + "; " + instType + "; " + msType + "; " + addition;
			}
			Map<String, String> map2 = new HashMap();
			map2.put("RECORD_TITLE", title);
			map2.put("INFO", info);
			recordList.put(id, map2);
		}
		DbUtils.closeQuietly(con);
		return recordList;
	}

	/*
	 * get registerd record IDs
	 */
	public static String[] getRegisteredRecordIds(Connection con) throws SQLException {
		String sql = "select ID from RECORD order by ID";
		QueryRunner qr = new QueryRunner();
		List<Object[]> results = (List)qr.query(con, sql, new ArrayListHandler());
		List<String> idList = new ArrayList();
		for ( Object[] vals : results ) {
			idList.add((String)vals[0]);
		}
		return idList.toArray(new String[]{});
	}

	/*
	 * get chemical compound infomation list
	 */
	public static List<CompoundInfo> getCompoundInfoList(String selDbName) {
		try {
			Connection con = GetDbUtil.connectDb(selDbName);
			String sql = "select COMPOUND_NO, COMPOUND_NAME, INCHI, MOLFILE"
									+ " from COMPOUND_INFO order by COMPOUND_NAME";
			QueryRunner qr = new QueryRunner();
			List<Map> results = (List)qr.query(con, sql, new MapListHandler());
			List<CompoundInfo> list = new ArrayList();
			for ( Map result : results ) {
				CompoundInfo info = new CompoundInfo(result);
				list.add(info);
			}
			DbUtils.closeQuietly(con);
			return list;
		}
		catch (SQLException e){
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * delete moldata
	 */
	public static boolean deleteMoldata(String selDbName, String[] cnos) {
		Connection con = null;
		String sql = "";
		try {
			con = GetDbUtil.connectDb(selDbName);
			con.setAutoCommit(false);
			QueryRunner qr = new QueryRunner();
			sql = "update COMPOUND_INFO set MOLFILE='' "
				+ "where COMPOUND_NO in('" + StringUtils.join(cnos, "','") + "')";
			int cnt = qr.update(con, sql);
			DbUtils.commitAndCloseQuietly(con);
			return true;
		}
		catch ( SQLException e ){
			e.printStackTrace();
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

	/*
	 * check if DB exists
	 */
	public static boolean existDB(String dbName) {
		String[] dbNames = null;
		try {
			dbNames = getDBNames();
		}
		catch ( Exception e ){
			e.printStackTrace();
			return false;
		}
		for ( String name : dbNames ) {
			if ( dbName.toLowerCase().equals(name.toLowerCase()) ) {
				return true;
			}
		}
		return false;
	}

	/*
	 * update database
	 */
	public static boolean updateDB(String dbName, String sql) {
		Connection con = null;
		try {
			con = GetDbUtil.connectDb(dbName);
			QueryRunner qr = new QueryRunner();
			int cnt = qr.update(con, sql);
			DbUtils.closeQuietly(con);
			return true;
		}
		catch ( SQLException e ){
			e.printStackTrace();
			DbUtils.closeQuietly(con);
			return false;
		}
	}

	/*
	 * get DB names
	 */
	public static String[] getDBNames() throws Exception {
		Connection con = GetDbUtil.connectDb("");
		ArrayList<String> existDbList = new ArrayList();
		GetConfig conf = new GetConfig();
		String[] confDbNames = conf.getDbNames();
		String sql = "show databases";
		QueryRunner qr = new QueryRunner();
		List<String[]> results = (List)qr.query(con, sql, new ArrayListHandler());
		DbUtils.closeQuietly(con);
		int num = results.size();
		String[] dbNames = new String[num];
		for ( int i = 0; i < num; i++ ) {
			Object[] vals = results.get(i);
			dbNames[i] = (String)vals[0];
		}
		return dbNames;
	}
}

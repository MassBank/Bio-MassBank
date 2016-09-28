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
 * GetDbUtil.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.massbank.core.common.CoreUtil;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;


public class GetDbUtil {

	/*
	 * connect database
	 */
	public static Connection connectDb(String dbName) throws SQLException {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://localhost/" + dbName;
			Connection con = DriverManager.getConnection(url, "massbank", "massbank2014");
			return con;
		}
		catch ( Exception e ) {
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * get record info
	 */
	public static List getRecordInfo(
		Connection con, String[] ids, String[] instTypes,
		String[] msTypes, String ionMode, String precursor) throws Exception {
		if ( ids == null || ids.length == 0 ) {
			return null;
		}
		String sql = "select C.COMPOUND_NO, ID, MS_TYPE, TITLE_ADDITION, PRECURSOR_MZ, ION,"
					+ " INSTRUMENT_TYPE, COMPOUND_NAME, FORMULA, EXACT_MASS, MOLFILE"
					+ " from RECORD R, COMPOUND_INFO C, INSTRUMENT I where ";
		String conditon = getSQLCondition(con, ids, instTypes, msTypes, ionMode, precursor);
		if ( conditon.equals("") ) {
			return null;
		}
		sql += conditon;
		QueryRunner qr = new QueryRunner();
		List<Map<String, String>> results = (List)qr.query(con, sql, new MapListHandler());
		for ( int i = 0; i < results.size(); i++ ) {
			Map<String, Object> map = (Map)results.get(i);
			String molfile  = (String)map.get("MOLFILE");
			map.put("MOLFILE", CoreUtil.padMolfile(molfile));
		}
		return results;
	}

	/*
	 * get record set
	 */
	public static List getRecordSet(
		Connection con, String[] ids, String[] instTypes,
		String[] msTypes, String ionMode)  throws Exception {

		if ( ids == null || ids.length == 0 ) {
			return null;
		}
		String sql = "select C.COMPOUND_NO, ID, MS_TYPE, TITLE_ADDITION, PRECURSOR_MZ, ION,"
					+ " INSTRUMENT_TYPE, COMPOUND_NAME, FORMULA, EXACT_MASS"
					+ " from RECORD R, COMPOUND_INFO C, INSTRUMENT I where ";
		String conditon = getSQLCondition(con, ids, instTypes, msTypes, ionMode, "");
		if ( conditon.equals("") ) {
			return null;
		}
		sql += conditon;

		QueryRunner qr = new QueryRunner();
		List<Map<String, String>> results1 = (List)qr.query(con, sql, new MapListHandler());
		List<Map<String, Object>> results2 = new ArrayList();
		if ( results1.size() > 0 ) {
			List<String> recordList = new ArrayList();
			int prevNo = 0;
			Map<String, Object> prevMap = null;
			for ( int i = 0; i < results1.size(); i++ ) {
				Map<String, Object> map = (Map)results1.get(i);
				int no          = (Integer)map.get("COMPOUND_NO");
				String id       = (String)map.get("ID");
				String cname    = (String)map.get("COMPOUND_NAME");
				String instType = (String)map.get("INSTRUMENT_TYPE");
				String msType   = (String)map.get("MS_TYPE");
				String addition = (String)map.get("TITLE_ADDITION");
				String title  = cname + "; " + instType + "; " + msType + "; " + addition;
				String record = title + "\t" + id;
				if ( prevNo != 0 && prevNo != no ) {
					results2.add( cerateResultSet(prevMap, recordList) );
					recordList = new ArrayList();
				}
				recordList.add(record);
				prevNo = no;
				prevMap = map;
			}
			results2.add( cerateResultSet(prevMap, recordList) );
		}
		return results2;
	}

	/*
	 * get sql condition
	 */
	private static String getSQLCondition(
		Connection con, String[] ids, String[] instTypes,
		String[] msTypes, String ionMode, String precursor)  throws Exception {

		int num1 = ids.length;
		StringBuilder joinId = new StringBuilder();
		for ( int i = 0; i < num1; i++ ) {
			joinId.append("'" + ids[i] + "'");
			if ( i < num1 - 1) {
				joinId.append(",");
			}
		}

		boolean isInstAll = false;
		String joinInstNumber = "";
		if ( instTypes == null ) {
			isInstAll = true;
		}
		else {
			for ( int i = 0; i < instTypes.length; i++ ) {
				if ( instTypes[i].toLowerCase().equals("all") ) {
					isInstAll = true;
					break;
				}
			}
		}
		if ( !isInstAll ) {
			String[] instNumbers = getInstrumentNumbers(con, instTypes);
			int num2 = instNumbers.length;
			if ( num2 == 0 ) {
				return "";
			}
			else {
				for ( int i = 0; i < num2; i++ ) {
					joinInstNumber += instNumbers[i];
					if ( i < num2 - 1) {
						joinInstNumber += ",";
					}
				}
			}
		}
		String joinMsType = "";
		if ( msTypes != null ) {
			int num3 = msTypes.length;
			for ( int i = 0; i < num3; i++ ) {
				if ( msTypes[i].toLowerCase().equals("all") ) {
					joinMsType = "";
					break;
				}
				else {
					joinMsType += "'" + msTypes[i] + "'";
					if ( i < num3 - 1) {
						joinMsType += ",";
					}
				}
			}
		}

		String where = "ID in(" + joinId.toString() + ")"
					 + " and R.COMPOUND_NO=C.COMPOUND_NO and R.INSTRUMENT_NO=I.INSTRUMENT_NO";
		if ( ionMode.matches("^[-.0-9]+$") ) {
			int ionNum = Integer.parseInt(ionMode);
			if ( ionNum > 0 ) {
				where += " and ION > 0";
			}
			else if ( ionNum < 0 ) {
				where += " and ION < 0";
			}
		}
		if ( !joinInstNumber.equals("") ) {
			where += " and R.INSTRUMENT_NO in(" + joinInstNumber + ")";
		}
		if ( !joinMsType.equals("") ) {
			where += " and MS_TYPE in(" + joinMsType + ")";
		}
		if ( precursor != null && !precursor.equals("") ) {
			where += " and PRECURSOR_MZ=" + precursor;
		}
		return where;
	}


	/*
	 * get instrument numbers
	 */
	public static String[] getInstrumentNumbers(Connection con, String[] instTypes) throws Exception {
		String joinInstType = "";
		for ( int i = 0; i < instTypes.length; i++ ) {
			joinInstType += "'" + instTypes[i] + "'";
			if ( i < instTypes.length - 1) {
				joinInstType += ",";
			}
		}
		String sql = "select INSTRUMENT_NO from INSTRUMENT where INSTRUMENT_TYPE in(" + joinInstType + ")";
		QueryRunner qr = new QueryRunner();
		List results = qr.query(con, sql, new ArrayListHandler());
		int num = results.size();
		String[] numbers = new String[num];
		for ( int j = 0; j < num; j++ ) {
			Object[] items = (Object[])results.get(j);
			numbers[j] = String.valueOf(items[0]);
		}
		return numbers;
	}

	/*
	 * check if heap table exists
	 */
	public static boolean checkHeapTableExists(Connection con) throws Exception {
		String sql = "show tables like 'PEAK_HEAP'";
		QueryRunner qr = new QueryRunner();
		Object[] res1 = qr.query(con, sql, new ArrayHandler());
		if ( res1 == null ) {
			return false;
		}
		sql = "select count(*) from 'PEAK_HEAP'";
		Object[] res2 = qr.query(con, sql, new ArrayHandler());
		if ( res2[0] == "0" ) {
			return false;
		}
		return true;
	}

	/**
	 *
	 */
	private static Map cerateResultSet(Map map, List recordList){
		Map<String, Object> newMap = new TreeMap();
		String[] keys = { "COMPOUND_NO", "COMPOUND_NAME", "FORMULA", "EXACT_MASS", "MOLFILE" };
		for ( String key : keys ) {
			newMap.put(key, map.get(key));
		}
		newMap.put("RECORD_LIST", recordList);
		return newMap;
	}
}

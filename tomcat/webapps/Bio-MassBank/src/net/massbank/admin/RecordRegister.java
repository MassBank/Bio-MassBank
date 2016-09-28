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
 * RecordRegister.java
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
import net.massbank.core.GetDbUtil;
import net.massbank.core.ResponseCache;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

public class RecordRegister {
	private Map<String, Integer> instNoList = new HashMap();
	private Map<String, Integer> compoundNoList = new HashMap();
	private int lastInstNo = 0;
	private int lastCompoundNo = 0;
	private Connection con = null;
	private String dbName = "";
	private String dataPath = "";
	private String[] ids = null;
	private String id = "";
	private String title  = "";
	private String formula = "";
	private String emass = "";
	private String smiles = "";
	private String inchi = "";
	private String instName = "";
	private String instType = "";
	private String msType = "";
	private String ionMode = "";
	private String fullText = "";
	private double precusor = 0;
	private List<String> peaks = new ArrayList();
	private List<String> cnames = new ArrayList();
	private AdminInfo adminInfo = new AdminInfo();
	private QueryRunner qr = null;
	private ResponseCache cache = null;

	/**
	 * constructor
	 */
	public RecordRegister(String dbName, String dataPath, String[] ids) {
		this.dbName = dbName;
		this.dataPath = dataPath;
		this.ids = ids;
		this.qr = new QueryRunner();
		this.cache = new ResponseCache();
	}

	/*
	 * register
	 */
	public boolean register() {
		if ( this.dbName.equals("") ) {
			return false;
		}
		String msg = "";
		try {
			this.con = GetDbUtil.connectDb(this.dbName);
			String regIds[] = AdminDbUtil.getRegisteredRecordIds(this.con);
			boolean isDuplicate = false;
			for ( String id : this.ids ) {
				for ( String regId : regIds ) {
					if ( id.equals(regId) ) {
						msg = "Duplicate entry";
						adminInfo.add(AdminInfo.INFO_LEVEL_ERROR, msg, "ACCESSION:&nbsp;" + id);
						isDuplicate = true;
					}
				}
			}
			if ( isDuplicate ) {
				return false;
			}

			List<Map> list = AdminDbUtil.getInstrument(con);
			int instNo = 0;
			for ( Map map : list ) {
				instNo = (Integer)map.get("INSTRUMENT_NO");
				String instType = (String)map.get("INSTRUMENT_TYPE");
				String instName = (String)map.get("INSTRUMENT_NAME");
				String key = instType + "_" + instName;
				this.instNoList.put(key, instNo);
			}
			this.lastInstNo = instNo;
			this.compoundNoList = getCompoundNoList();

			con.setAutoCommit(false);
			for ( String id : this.ids ) {
				String filePath = this.dataPath + File.separator + id + ".txt" ;
				if ( !getRecordValue(filePath) ) {
					return false;
				}
				else {
					if ( !registerRecord() ) {
						return false;
					}
					if ( !cleanInstrument() ) {
						return false;
					}
				}
			}
			DbUtils.commitAndCloseQuietly(this.con);
			this.cache.clear();
			return true;
		}
		catch ( Exception e ) {
			e.printStackTrace();
			return false;
		}
		finally {
			DbUtils.closeQuietly(this.con);
		}
	}

	/*
	 * delete
	 */
	public boolean delete() {
		try {
			this.con = GetDbUtil.connectDb(this.dbName);
			this.con.setAutoCommit(false);
			if ( !deleteRecord() ) {
				return false;
			}
			if ( !cleanInstrument() ) {
				return false;
			}
			DbUtils.commitAndCloseQuietly(this.con);
			this.cache.clear();
			return true;
		}
		catch ( Exception e ) {
			e.printStackTrace();
			return false;
		}
	}

	/*
	 * get information
	 */
	public String[] getInformations() {
		return this.adminInfo.getList();
	}

	/**
	 * regist record values
	 */
	private boolean getRecordValue(String filePath) {
		this.id = ""; this.title  = ""; this.formula = "";
		this.emass = ""; this.smiles = ""; this.inchi = "";
		this.instName = ""; this.instType = ""; this.msType = "";
		this.ionMode = ""; this.precusor = 0;
		this.peaks = new ArrayList();
		this.cnames = new ArrayList();
		boolean isPeak = false;

		try {
			this.fullText = FileUtils.readFileToString(new File(filePath));
		}
		catch ( IOException e ){
			e.printStackTrace();
			String msg = "Internal error (failed to read file)";
			adminInfo.add(AdminInfo.INFO_LEVEL_ERROR, msg, filePath);
			return false;
		}
		String[] lines = fullText.split("\n");
		for ( String line: lines ) {
			line = line.trim();
			if ( line.startsWith("//") ) { break; }
			int pos1 = line.indexOf(": ");
			if ( pos1 >= 0 ) {
				String tag = line.substring(0, pos1);
				String val = line.substring(pos1 + 2);
				String subTag = "";
				String subVal = "";
				int pos2 = val.indexOf(" ");
				if ( pos2 >= 0 ) {
					subTag = val.substring(0, pos2);
					subVal = val.substring(pos2 + 1);
				}

				if ( tag.equals("CH$NAME") )                 { this.cnames.add(val); }
				else if ( tag.equals("ACCESSION") )          { this.id       = val;  }
				else if ( tag.equals("RECORD_TITLE") )       { this.title    = val;  }
				else if ( tag.equals("CH$FORMULA") )         { this.formula  = val;  }
				else if ( tag.equals("CH$EXACT_MASS") )      { this.emass    = val;  }
				else if ( tag.equals("CH$SMILES")     )      { this.smiles   = val;  }
				else if ( tag.equals("CH$IUPAC") )           { this.inchi    = val;  }
				else if ( tag.equals("AC$INSTRUMENT") )      { this.instName = val;  }
				else if ( tag.equals("AC$INSTRUMENT_TYPE") ) { this.instType = val;  }
				else if ( tag.equals("AC$MASS_SPECTROMETRY") ) {
					if ( subTag.equals("MS_TYPE") )          { this.msType   = subVal; }
					else if ( subTag.equals("ION_MODE") )    { this.ionMode  = subVal; }
				}
				else if ( tag.equals("MS$FOCUSED_ION") ) {
					if ( subTag.equals("PRECURSOR_M/Z") )   {
						int pos = subVal.indexOf("/");
						if ( pos >= 0 ) {
							subVal = subVal.substring(pos + 1);
						}
						this.precusor = Double.parseDouble(subVal);
					}
				}
				else if ( tag.equals("PK$PEAK") ) {
					isPeak = true;
				}
			}
			else {
				if ( isPeak ) {
					this.peaks.add(line);
				}
			}
		}
		return true;
	}

	/**
	 * regist record
	 */
	private boolean registerRecord() {
		String[] items1 = this.title.split(";");
		String cname = items1[0].trim();
		String key = cname + "_" + inchi;
		int cno = 0;
		boolean existConpoundInfo = false;
		if ( this.compoundNoList.containsKey(key) ) {
			cno = this.compoundNoList.get(key);
			existConpoundInfo = true;
		}
		else {
			cno = ++this.lastCompoundNo;
			this.compoundNoList.put(key, cno);
		}

		String additon = "";
		if ( items1.length > 3 ) {
			int pos = title.indexOf(";", (items1[0].length() + items1[1].length() + 2) );
			if ( pos >= 0 ) {
				additon = title.substring(pos+1);
			}
		}

		int ion = 0;
		if ( this.ionMode.equals("POSITIVE") ) {
			ion = 1;
		}
		else if ( this.ionMode.equals("NEGATIVE") ) {
			ion = -1;
		}

		int instNo = 0;
		boolean existInstrumentInfo = false;
		key = this.instType + "_" + this.instName;
		if ( this.instNoList.containsKey(key) ) {
			instNo = this.instNoList.get(key);
			existInstrumentInfo = true;
		}
		else {
			instNo = ++this.lastInstNo;
			this.instNoList.put(key, instNo);
		}

		String sql = "";
		try {
			sql = "insert into RECORD(ID, COMPOUND_NO, INSTRUMENT_NO, MS_TYPE, "
				+ "TITLE_ADDITION, ION, PRECURSOR_MZ, FULL_TEXT) values(?, ?, ?, ?, ?, ?, ?, ?)";
			Object[] vals1 = new Object[]{
				this.id, cno, instNo, this.msType, additon, ion, this.precusor, this.fullText
			};
			int cnt1 = qr.update(this.con, sql, vals1);

			sql = "insert into COMPOUND_NAMES(ID, COMPOUND_NAME) values(?, ?)";
			for ( String name: cnames.toArray(new String[]{}) ) {
				Object[] vals2 = new Object[]{ id, name };
				int cnt2 = qr.update(con, sql, vals2);
			}

			sql = "insert into PEAK(ID, MZ, INTENSITY, RELATIVE) values(?, ?, ?, ?)";
			for ( String peak: this.peaks.toArray(new String[]{}) ) {
				String[] items2 = peak.split(" ");
				double mz = Double.parseDouble(items2[0]);
				float inte = Float.parseFloat(items2[1]);
				int relative = Integer.parseInt(items2[2]);
				Object[] vals3 = new Object[]{ id, mz, inte, relative };
				int cnt3 = qr.update(this.con, sql, vals3);
			}

			if ( !existConpoundInfo ) {
				sql = "insert into COMPOUND_INFO(COMPOUND_NO, COMPOUND_NAME, FORMULA"
							+ ", EXACT_MASS, SMILES, INCHI, MOLFILE) values(?, ?, ?, ?, ?, ?, ?)";
				if ( this.emass.equals("N/A") ) {
					this.emass = "0";
				}
				Object[] vals4 = new Object[]{ cno, cname,
										this.formula, this.emass, this.smiles, this.inchi, "" };
				int cnt4 = qr.update(this.con, sql, vals4);
			}

			if ( !existInstrumentInfo ) {
				sql = "insert into INSTRUMENT(INSTRUMENT_NO, INSTRUMENT_TYPE, INSTRUMENT_NAME) values(?, ?, ?)";
				Object[] vals5 = new Object[]{ instNo, this.instType, this.instName };
				int cnt5 = qr.update(this.con, sql, vals5);
			}
			return true;
		}
		catch ( SQLException e ) {
			String msg = "Internal error (failed to execute SQL)";
			adminInfo.add(AdminInfo.INFO_LEVEL_ERROR, msg, sql);
			e.printStackTrace();
			try {
				if ( this.con != null ) {
					DbUtils.rollback(this.con);
				}
			}
			catch ( SQLException e2 ) {
				e.printStackTrace();
			}
			return false;
		}
	}

	/**
	 * get list of compound number
	 */
	private Map<String, Integer> getCompoundNoList() throws Exception {
		String sql = "select COMPOUND_NAME, COMPOUND_NO, INCHI from COMPOUND_INFO order by COMPOUND_NO";
		List<Map<String, Object>> results = (List)this.qr.query(this.con, sql, new MapListHandler());
		int cno = 0;
		Map<String, Integer> list = new HashMap();
		for ( Map<String, Object> map : results ) {
			String cname = (String)map.get("COMPOUND_NAME");
			String inchi = (String)map.get("INCHI");
			String key = cname + "_" + inchi;
			cno = (Integer)map.get("COMPOUND_NO");
			list.put(key, cno);
		}
		this.lastCompoundNo = cno;
		return list;
	}

	/*
	 * delete records
	 */
	public boolean deleteRecord() {
		String sql = "";
		try {
			String[] dbTableNames = { "RECORD", "PEAK", "COMPOUND_NAMES" };
			int cnt = 0;
			for ( String tableName : dbTableNames ) {
				sql = "delete from " + tableName + " where ID in('" + StringUtils.join(this.ids, "','") + "')";
				cnt += this.qr.update(con, sql);
			}
			if ( cnt > 0 ) {
				sql = "select C.COMPOUND_NO from RECORD R right join COMPOUND_INFO C "
						   + "on R.COMPOUND_NO=C.COMPOUND_NO where R.COMPOUND_NO is null";
				List<Object> res = (List)qr.query(this.con, sql, new ArrayListHandler());
				int num = res.size();
				if ( num > 0 ) {
					String[] nos = new String[num];
					for ( int i = 0; i < num; i++ ) {
						Object[] fields = (Object[])res.get(i);
						nos[i] = String.valueOf(fields[0]);
					}
					sql = "delete from COMPOUND_INFO where COMPOUND_NO in('" + StringUtils.join(nos, "','") + "')";
					cnt = qr.update(this.con, sql);
				}
			}
			return true;
		}
		catch (SQLException e){
			e.printStackTrace();
			try {
				if ( con != null ) {
					DbUtils.rollback(con);
				}
			}
			catch ( SQLException e2 ) {
				e2.printStackTrace();
			}
			return false;
		}
	}


	/*
	 * clean instrument information
	 */
	public boolean cleanInstrument() {
		try {
			String sql1 = "select I.INSTRUMENT_NO from INSTRUMENT I left join RECORD R "
				+ "on I.INSTRUMENT_NO=R.INSTRUMENT_NO where ID is null group by I.INSTRUMENT_NO";
			List<Object[]> results = (List)this.qr.query(this.con, sql1, new ArrayListHandler());
			int num = results.size();
			if ( num > 0 ) {
				String[] numbers = new String[num];
				for ( int i = 0; i < num; i++ ) {
					Object[] fields = (Object[])results.get(i);
					numbers[i] = String.valueOf(fields[0]);
				}
				String sql2 = "delete from INSTRUMENT "
						+ "where INSTRUMENT_NO in('" + StringUtils.join(numbers, "','") + "')";
				int cnt = this.qr.update(this.con, sql2);
			}
			return true;
		}
		catch (SQLException e){
			e.printStackTrace();
			try {
				if ( this.con != null ) {
					DbUtils.rollback(this.con);
				}
			}
			catch ( SQLException e2 ) {
				e.printStackTrace();
			}
			return false;
		}
	}
}

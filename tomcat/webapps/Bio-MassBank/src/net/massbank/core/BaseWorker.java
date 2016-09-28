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
 * BaseWorker.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.core;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.sql.Connection;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import net.massbank.core.GetDbUtil;


public class BaseWorker implements Callable {
	protected String dbName = "";
	protected Map<String, String> params = null;
	protected Connection con = null;
	protected QueryRunner qr = null;
	protected ResultSetHandler rsh = null;

	/**
	 * constructor
	 */
	public BaseWorker(String dbName, Map<String, String> params) {
		this.dbName = dbName;
		this.params = params;
		try {
			this.con = GetDbUtil.connectDb(dbName);
			this.qr = new QueryRunner();
			this.rsh = new MapListHandler();
		}
		catch ( Exception ex ) {
			ex.printStackTrace();
		}
	}

	/**
	 * constructor
	 */
	public BaseWorker(Map<String, String> params) {
		this.params = params;
	}

	/**
	 * call
	 */
	public List call() throws Exception {
		List results = new ArrayList();
		String sql = "";
		results = (List)qr.query(con, sql, rsh);
		DbUtils.closeQuietly(con);
		return results;
	}
}

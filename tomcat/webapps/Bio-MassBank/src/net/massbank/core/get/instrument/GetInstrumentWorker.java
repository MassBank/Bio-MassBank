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
 * GetInstrumentWorker.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.core.get.instrument;

import java.util.Map;
import java.util.List;
import org.apache.commons.dbutils.DbUtils;
import net.massbank.core.BaseWorker;

public class GetInstrumentWorker extends BaseWorker {

	/**
	 * constructor
	 */
	public GetInstrumentWorker(String dbName, Map<String, String> params) {
		super(dbName, params);
	}

	/**
	 * call
	 */
	@Override public List call() throws Exception {
		List results = null;
		String sql = "select distinct I.INSTRUMENT_NO, INSTRUMENT_TYPE, INSTRUMENT_NAME, MS_TYPE "
				  + "from INSTRUMENT I, RECORD R where I.INSTRUMENT_NO=R.INSTRUMENT_NO "
				  + "order by I.INSTRUMENT_NO, MS_TYPE";
		results = (List)qr.query(con, sql, rsh);
		DbUtils.closeQuietly(con);
		return results;
	}

}

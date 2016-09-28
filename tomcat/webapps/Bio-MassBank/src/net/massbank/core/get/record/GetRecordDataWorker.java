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
 * GetRecordDataWorker.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.core.get.record;

import java.util.Map;
import java.util.List;
import org.apache.commons.dbutils.DbUtils;
import net.massbank.core.BaseWorker;
import net.massbank.core.common.CommonParameter;

public class GetRecordDataWorker extends BaseWorker {

	/**
	 * constructor
	 */
	public GetRecordDataWorker(String dbName, Map<String, String> params) {
		super(dbName, params);
	}

	/**
	 * call
	 */
	@Override public List call() throws Exception {
		String sql = "select ID, FULL_TEXT, COMPOUND_NAME,"
					+ "INSTRUMENT_TYPE, MS_TYPE, TITLE_ADDITION from RECORD R, COMPOUND_INFO C, INSTRUMENT I";
		if ( params.size() > 0 ) {
			CommonParameter sp = new CommonParameter(super.params);
			String joinId = sp.getJoinId();
			sql += " where R.ID in(" + joinId + ") and ";
		}
		sql += "R.COMPOUND_NO=C.COMPOUND_NO and R.INSTRUMENT_NO=I.INSTRUMENT_NO";
		List results = (List)qr.query(con, sql, rsh);
		DbUtils.closeQuietly(con);
		return results;
	}
}

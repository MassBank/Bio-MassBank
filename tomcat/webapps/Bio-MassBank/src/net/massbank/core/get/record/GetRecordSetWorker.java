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
 * GetRecordGroupWorker.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.core.get.record;

import java.util.List;
import java.util.Map;
import net.massbank.core.BaseWorker;
import net.massbank.core.GetDbUtil;
import net.massbank.core.common.CommonParameter;

public class GetRecordSetWorker extends BaseWorker {

	/**
	 * constructor
	 */
	public GetRecordSetWorker(String dbName, Map<String, String> params) {
		super(dbName, params);
	}

	/**
	 * call
	 */
	@Override public List call() throws Exception {
		CommonParameter cparam = new CommonParameter(super.params);
		String[] ids = cparam.getIds();
		return GetDbUtil.getRecordSet(con, ids, null, null, "");
	}
}

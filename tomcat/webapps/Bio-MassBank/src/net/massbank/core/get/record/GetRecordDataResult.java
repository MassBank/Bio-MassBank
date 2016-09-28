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
 * GetRecordDataResult.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.core.get.record;

import java.util.Map;
import java.util.List;
import net.massbank.core.common.Results;

public class GetRecordDataResult extends Results {

	/**
	 * constructor
	 */
	public GetRecordDataResult(List<Map<String, String>> results) {
		super(results);
	}

	/**
	 * 
	 */
	public String getFullText(String id) {
		return super.getValueById("FULL_TEXT", id);
	}

	/**
	 * get record title
	 */
	public String getRecordTitle(String id) {
		String cname = super.getValueById("COMPOUND_NAME", id);
		String instType = super.getValueById("INSTRUMENT_TYPE", id);
		String msType = super.getValueById("MS_TYPE", id);
		String addition = super.getValueById("TITLE_ADDITION", id);
		return cname + "; " + instType + "; " + msType + "; " + addition;
	}
}

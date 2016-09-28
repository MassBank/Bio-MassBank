<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%
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
 * FSpectrumSearchResult.jsp
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="net.arnx.jsonic.JSON" %>
<%@ page import="org.apache.commons.lang.NumberUtils" %>
<%@ page import="net.massbank.tools.search.FormulaSearch" %>
<%
	Enumeration names = request.getParameterNames();
	String peaks = "";
	String formulas = "";
	String strPpm = "";
	String strCutoff = "";
	String strIonMode = "";
	while ( names.hasMoreElements() ) {
		String key = (String)names.nextElement();
		String val = request.getParameter(key);
		if ( key.equals("peaks") ) {
			peaks = val;
		}
		else if ( key.equals("formula") ) {
			formulas = val;
		}
		else if ( key.equals("ppm") ) {
			strPpm = val;
		}
		else if ( key.equals("ion_mode") ) {
			strIonMode = val;
		}
	}

	int ppm = 50;
	if ( NumberUtils.isNumber(strPpm) ) {
		ppm = NumberUtils.stringToInt(strPpm);
	}

	int cutoff = 5;
	if ( NumberUtils.isNumber(strCutoff) ) {
		cutoff = NumberUtils.stringToInt(strCutoff);
	}

	int ionMode = 1;
	if ( strIonMode.equals("-1") ) {
		ionMode = -1;
	}
	FormulaSearch fs = new FormulaSearch(peaks, cutoff, ppm, ionMode);
	List<Map<String,String>> results = (List)fs.doSearch();
	String res = JSON.encode(results);
	out.print(res);
%>

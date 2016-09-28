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
 * PeakSearch.jsp
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
%>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Enumeration" %>
<%
	final int NUM_FORMULA_STD = 6;
	String searchType = "peak";
	String searchBy = "mz";
	String relInte  = "100";
	String tol  = "0.3";
	String ionMode  = "1";
	String mode = "and";
	boolean isFirst = true;
	List<String> instGrpList = new ArrayList<String>();
	List<String> instTypeList = new ArrayList<String>();
	Hashtable<String, String> params = new Hashtable<String, String>();
	int paramCnt = 0;
	Enumeration names = request.getParameterNames();
	if ( names.hasMoreElements() ) {
		isFirst = false;
	}
	while ( names.hasMoreElements() ) {
		String key = (String)names.nextElement();
		if ( key.equals("inst_grp") ) {
			String[] vals = request.getParameterValues(key);
			instGrpList = Arrays.asList(vals);
		}
		else if ( key.equals("inst") ) {
			String[] vals = request.getParameterValues(key);
			instTypeList = Arrays.asList(vals);
		}
		else {
			String val = request.getParameter(key);
			if ( key.equals("searchType") )	searchType = val;
			else if ( key.equals("mode") )	mode     = val;
			else if ( key.equals("inte") )	relInte  = val;
			else if ( key.equals("tol") )	tol      = val;
			else if ( key.equals("ion") )	ionMode  = val;
			else if ( key.indexOf("mz") >= 0 || key.indexOf("op") >= 0 ) {
				params.put( key, val );
			}
		}
	}

	if ( paramCnt > 0 ) {
		isFirst = false;
	}
	String instGrp = "";
	for ( int i = 0; i < instGrpList.size(); i++ ) {
		instGrp += instGrpList.get(i);
		if ( i < instGrpList.size() - 1 ) {
			instGrp += ",";
		}
	}
	String instType = "";
	for ( int i = 0; i < instTypeList.size(); i++ ) {
		instType += instTypeList.get(i);
		if ( i < instTypeList.size() - 1 ) {
			instType += ",";
		}
	}
%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<meta http-equiv="Content-Style-Type" content="text/css">
	<meta http-equiv="Content-Script-Type" content="text/javascript">
	<meta http-equiv="imagetoolbar" content="no">
	<link rel="stylesheet" type="text/css" href="../css/Common.css">
	<script type="text/javascript" src="../script/jquery.js"></script>
	<script type="text/javascript" src="../script/Common.js"></script>
	<script type="text/javascript" src="../script/AtomicMass.js"></script>
	<script type="text/javascript" src="../script/PeakSearch.js"></script>
	<title>Bio-MassBank | Database | Peak Search</title>
</head>
<body onload="initFocus();">
<iframe src="./menu.jsp?select_menu_no=3" width="100%" height="26" frameborder="0" marginwidth="0" scrolling="no" class="menu"></iframe>
<div id="common_page_head">Peak Search</div>
<div id="main">
	<form name="form_query" method="get" action="./SearchResult.jsp" style="display:inline">
		<table border="0" cellpadding="0">
			<tr>
				<td width="90"><b>Search of</b></td>
				<td width="100">
					<input type="radio" name="searchType" value="peak" onClick="changeSearchType(this.value)"<% if(searchType.equals("peak")) out.print(" checked"); %>><b><i>Peaks</i></b>
				</td>
				<td width="20"></td>
				<td width="170">
					<input type="radio" name="searchType" value="peak_diff" onClick="changeSearchType(this.value)"<% if(searchType.equals("peak_diff")) out.print(" checked"); %>><b><i>Peak&nbsp;Differences</i></b>
				</td>
			</tr>
			<tr>
				<td></td>
				<td id="underbar1" height="2"<% if(searchType.equals("peak")) out.print(" bgcolor=\"OliveDrab\""); %>></td>
				<td></td>
				<td id="underbar2" height="2"<% if(searchType.equals("peak_diff")) out.print(" bgcolor=\"DarkViolet\""); %>></td>
			</tr>
		</table>
		<br>
<%
	String mzLabel = "<i>m/z</i>";
	String allowImage = "<img src=\"../image/arrow_peak.gif\" alt=\"\">";
	if ( searchType.equals("peak_diff") ) {
		mzLabel = "<i>m/z</i> Diff.";
		allowImage = "<img src=\"../image/arrow_diff.gif\" alt=\"\">";
	}
	out.println( "\t\t<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">" );
	out.println( "\t\t\t<tr>" );
	out.println( "\t\t\t\t<td valign=\"top\">" );
	out.println( "\t\t\t\t\t<table border=\"0\" cellpadding=\"0\" cellspacing=\"12\" class=\"form-box\">" );
	out.println( "\t\t\t\t\t\t<tr>" );
	out.println( "\t\t\t\t\t\t\t<th></th>" );
	out.println( "\t\t\t\t\t\t\t<th id=\"mz\">" + mzLabel + "</th>" );
	out.println( "\t\t\t\t\t\t\t<th>Formula</th>" );
	out.println( "\t\t\t\t\t\t</tr>" );
	final String[] logic = { "and", "or" };
	String lblLogic = logic[0].toUpperCase();
	String[] mz = new String[NUM_FORMULA_STD];
	String[] op = new String[NUM_FORMULA_STD];
	for ( int i = 0; i < NUM_FORMULA_STD; i++ ) {
		String key = "mz" + String.valueOf(i);
		if ( params.containsKey(key) ) {
			mz[i] = (String)params.get(key);
			op[i] = (String)params.get( "op" + String.valueOf(i) );
		}
		else {
			mz[i] = "";
			op[i] = "";
		}
		out.println( "\t\t\t\t\t\t<tr>" );
		if ( i == 0 ) {
			out.println( "\t\t\t\t\t\t\t<td>" );
			out.println( "\t\t\t\t\t\t\t\t<select name=\"op0\" tabindex=\"5\" onChange=\"changeSearchCondition(this.value.toUpperCase())\">" );
			for ( int j = 0; j < logic.length; j++ ) {
				out.print( "\t\t\t\t\t\t\t\t\t\t<option value=\"" + logic[j] + "\"" );
				if ( logic[j].equals(op[0]) ) {
					out.print( " selected" );
					lblLogic = logic[j].toUpperCase();
				}
				out.println( ">" + logic[j].toUpperCase() + "</option>" );
			}
			out.println( "\t\t\t\t\t\t\t\t</select>" );
			out.println( "\t\t\t\t\t\t\t</td>" );
		}
		else {
			String eleId = "logic" + String.valueOf(i);
			out.println( "\t\t\t\t\t\t\t<td align=\"right\"><span id=\"" + eleId + "\">" + lblLogic + "</span>&nbsp;</td>" );
		}
		
		// m/z
		out.println( "\t\t\t\t\t\t\t<td><input name=\"mz" + i + "\" type=\"text\" size=\"14\" value=\"" + mz[i] + "\" class=\"Mass\" tabindex=\"" + (i+5) + "\"></td>" );
		
		// Formula
		out.println( "\t\t\t\t\t\t\t<td>" );
		out.println( "\t\t\t\t\t\t\t\t<span id=\"arrow" + i + "\">" + allowImage + "</span>" );
		out.println( "\t\t\t\t\t\t\t\t<input name=\"for" + i + "\" type=\"text\" size=\"20\" value=\"\" class=\"Formula\" tabindex=\"" + (i+11) + "\">" );
		out.println( "\t\t\t\t\t\t\t</td>" );
		out.println( "\t\t\t\t\t\t</tr>" );
	}
%>
						<tr>
							<td colspan="3" height="1"></td>
						</tr>
						<tr>
							<td colspan="3">
								<b>Rel.Intensity</b>&nbsp;<input name="inte" type="text" size="10" value="<%= relInte %>" tabindex="17">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>Tolerance</b>&nbsp;<input name="tol" type="text" size="10" value="<%= tol %>" tabindex="18">
							</td>
						</tr>
						<tr>
							<td colspan="3" align="right">
								<input type="button" name="reset" value="Reset" onClick="resetForm()">
							</td>
						</tr>
					</table>
					<br>
					<table>
						<tr>
							<td>
								<input type="submit" value="Search" onclick="return checkSubmit();" class="search" tabindex="19">
							</td>
						</tr>
					</table>
				</td>
				<td valign="top" style="padding:0px 15px;">
					<jsp:include page="Instrument.jsp" flush="true">
						<jsp:param name="ion" value="<%= ionMode %>" />
						<jsp:param name="first" value="<%= isFirst %>" />
						<jsp:param name="inst_grp" value="<%= instGrp %>" />
						<jsp:param name="inst" value="<%= instType %>" />
					</jsp:include>
				</td>
			</tr>
		</table>
	</div>
	</form>
	<br>
</div>
<iframe src="../copyrightline.html" width="100%" height="50" frameborder="0" marginwidth="20" scrolling="no"></iframe>
</body>
</html>

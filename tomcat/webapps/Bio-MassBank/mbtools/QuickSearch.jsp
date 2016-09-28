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
 * QuickSearch.jsp
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Enumeration" %>

<%
	String searchType = (String)request.getParameter("searchType");
	if ( searchType == null ) {
		searchType = "keyword";
	}
	boolean isKeyword = false;
	String postJspName = "";
	if ( searchType.equals("keyword") ) {
		isKeyword = true;
	}
	postJspName = "SearchResult.jsp";
	
	String cname = "";
	String emass = "";
	String tolerance = "0.3";
	String formula  = "";
	String peakData = "";
	String cutOff   = "5";
	String num      = "20";
	String ionMode  = "1";
	boolean isFirst = true;
	List instGrpList = new ArrayList<String>();
	List instTypeList = new ArrayList<String>();
	int paramCnt = 0;
	Enumeration names = request.getParameterNames();
	while ( names.hasMoreElements() ) {
		String key = (String)names.nextElement();
		if ( !key.equals("searchType") ) {
			paramCnt++;
		}
		if ( key.equals("inst_grp") ) {
			String[] vals = request.getParameterValues( key );
			instGrpList = Arrays.asList(vals);
		}
		else if ( key.equals("inst") ) {
			String[] vals = request.getParameterValues( key );
			instTypeList = Arrays.asList(vals);
		}
		else {
			String val = request.getParameter( key );
			if ( key.equals("compound_name") )  { cname     = val; }
			else if ( key.equals("emass") )     { emass     = val; }
			else if ( key.equals("tolerance") ) { tolerance = val; }
			else if ( key.equals("formula") )   { formula   = val; }
			else if ( key.equals("qpeak") )     { peakData  = val; }
			else if ( key.equals("cutoff") )    { cutOff    = val; }
			else if ( key.equals("num") )       { num       = val; }
			else if ( key.equals("ion") )       { ionMode   = val; }
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
	<link rel="stylesheet" type="text/css" href="../css/Common.css">
	<script type="text/javascript" src="../script/Common.js"></script>
	<script type="text/javascript" src="../script/QuickSearch.js"></script>
	<title>Bio-MassBank | Database | Quick Search</title>
</head>
<body onload="initFocus();">
<iframe src="./menu.jsp?select_menu_no=2" width="100%" height="26" frameborder="0" marginwidth="0" scrolling="no" class="menu"></iframe>
<div id="common_page_head">Quick Search</div>
<div id="main">
	<form name="change" method="post" action="QuickSearch.jsp" style="display:inline">
		<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td width="180">
					<input type="radio" name="searchType" value="keyword" onClick="changeSearchType()"<% if(isKeyword) out.print(" checked"); %>><b><i>Search by Keyword</i></b>
				</td>
				<td width="50"></td>
				<td width="150">
					<input type="radio" name="searchType" value="peak" onClick="changeSearchType()"<% if(!isKeyword) out.print(" checked"); %>><b><i>Search by Peak</i></b>
				</td>
			</tr>
			<tr>
				<td id="underbar1" height="2"<% if(isKeyword) out.print(" bgcolor=\"IndianRed\""); %>></td>
				<td></td>
				<td id="underbar2" height="2"<% if(!isKeyword) out.print(" bgcolor=\"Goldenrod\""); %>></td>
			</tr>
			<tr>
				<td colspan="3" height="10"></td>
			</tr>
		</table>
	</form>
	<form name="form_query" method="get" action="<% out.print(postJspName); %>" style="display:inline">
		<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td valign="top">
<%
	if ( isKeyword ) {
%>
					<table border="0" cellpadding="0" cellspacing="15" class="form-box">
						<tr>
							<td>
								<b>Compound Name</b>&nbsp;
								<input name="compound_name" type="text" size="38" value="<%= cname %>">
							</td>
						</tr>
						<tr>
							<td>
								<b>Exact Mass</b>&nbsp;
								<input name="emass" type="text" size="15" value="<%= emass %>">
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>Tolerance</b>&nbsp;
								<input name="tolerance" type="text" size="6" value="<%= tolerance %>">
							</td>
						</tr>
						<tr>
							<td>
								<b>Formula</b>&nbsp;
								<input name="formula" type="text" size="20" value="<%= formula %>">
								<br>( e.g. C6H7N5, C5H*N5, C5* )
							</td>
						</tr>
						<tr>
							<td colspan="2" align="right">
								<input type="button" value="Reset" onClick="resetForm();">
							</td>
						</tr>
					</table>
<%
	} else {
%>
					<table border="0" cellpadding="0" cellspacing="15" style="background-color:WhiteSmoke;border:1px silver solid;">
						<tr>
							<td>
								<b>Peak Data</b><br>
								<textarea name="qpeak" cols="40" rows="10"><%= peakData %></textarea><br>
								m/z and relative intensities(0-999), delimited by a space.<br>
								<input type="button" value="Example1" onClick="insertExample1()">
								<input type="button" value="Example2" onClick="insertExample2()">
							</td>
						</tr>
						<tr>
							<td>
								<hr size=1 color="silver">
								<b>Cutoff threshold of relative intensities</b>&nbsp;&nbsp;<input name="cutoff" type="text" size="4" value="<%= cutOff %>"><br>
							</td>
						</tr>
						<tr>
							<td>
								<b>Number of Results</b>
								<select name="num">
									<option value="20"<%  if(num.equals("20"))  out.print(" selected"); %>>20</option>
									<option value="50"<%  if(num.equals("50"))  out.print(" selected"); %>>50</option>
									<option value="100"<% if(num.equals("100")) out.print(" selected"); %>>100</option>
									<option value="500"<% if(num.equals("500")) out.print(" selected"); %>>500</option>
								</select>
							</td>
						</tr>
					</table>
<%
	}
%>
					<br>
					<table>
						<tr>
							<td>
								<input type="submit" value="Search" onclick="<% if(!isKeyword){out.print("beforeSubmit(); ");} %>return checkSubmit();" class="search">
								<input type="hidden" name="searchType" value="<% if(isKeyword){out.print("keyword");}else{out.print("spectrum");}%>">
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
	</form>
</div>
<iframe src="../copyrightline.html" width="100%" height="50" frameborder="0" marginwidth="20" scrolling="no"></iframe>
</body>
</html>

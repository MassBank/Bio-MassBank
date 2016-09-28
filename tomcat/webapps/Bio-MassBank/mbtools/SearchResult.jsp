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
 * SearchResult.jsp
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
%>
<%@ page import="org.apache.commons.lang.NumberUtils" %>
<%@ page import="net.massbank.core.common.RecordInfo" %>
<%@ page import="net.massbank.tools.search.BasicSearch" %>
<%@ page import="net.massbank.tools.search.peak.PeakSearchParameter" %>
<%@ page import="net.massbank.tools.search.quick.QuickSearchParameter" %>
<%
	BasicSearch bs = new BasicSearch(request, out);
	String searchType = request.getParameter("searchType");
	String pageTitle = "";
	String headTitle = "";
	String menuNo = "0";
	if ( searchType == null ) {
		pageTitle = headTitle = "Record Index Results";
		searchType = "keyword";
		menuNo = "0";
	}
	else if ( searchType.equals("keyword") ) {
		pageTitle = headTitle = "Quick Search Results";
		menuNo = "2";
	}
	else if ( searchType.equals("spectrum") ) {
		pageTitle = headTitle = "Quick Search Results";
		menuNo = "2";
	}
	else if ( searchType.equals("peak") || searchType.equals("peak_diff") ) {
		pageTitle = headTitle = "Peak Search Results";
		menuNo = "3";
	}
	else {
		return;
	}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">
<head>
<meta http-equiv="X-UA-Compatible" content="IE=EmulateIE9">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="Content-Script-Type" content="text/javascript">
<meta http-equiv="Content-Style-Type" content="text/css">
<link rel="stylesheet" type="text/css" href="../css/jquery-ui-1.8.17.custom.css" />
<link rel="stylesheet" type="text/css" href="../css/ui.jqgrid.css" />
<link rel="stylesheet" type="text/css" href="../css/Common.css">
<script type="text/javascript" src="../script/jquery.js"></script>
<script type="text/javascript" src="../script/grid.locale-en.js"></script>
<script type="text/javascript" src="../script/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="../script/curvycorners.js"></script>
<script type="text/javascript" src="../script/Common.js"></script>
<% if ( searchType.equals("spectrum") ) { %>
<script type="text/javascript" src="../script/QPeakSearchResult.js"></script>
<% } else { %>
<script type="text/javascript" src="../script/SearchResult.js"></script>
<% } %>
<title>Bio-MassBank | Database | <%=pageTitle%></title>
</head>
<body>
<iframe src="./menu.jsp?select_menu_no=<%=menuNo%>" width="100%" height="26" frameborder="0" marginwidth="0" scrolling="no" class="menu"></iframe>
<div id="common_page_head"><%=headTitle%></div>
<div id="main">
<%
	bs.execute();
	bs.showSearchParam();
	out.println( "<br>" );
	String queryString1 = request.getQueryString();
	String queryString2 = "";
	int viewPage = 1;
	int pos1 = queryString1.indexOf("&page=");
	if ( pos1 >= 0 ) {
		queryString2 = queryString1.substring(0, pos1);
		int start = pos1 + 6;
		int pos2 = queryString1.indexOf("&", start);
		if ( pos2 == -1 ) {
			pos2 = queryString1.length();
		}
		else {
			queryString2 += queryString1.substring(pos2);
		}
		String val = queryString1.substring(start, pos2);
		if ( NumberUtils.isNumber(val) ) {
			viewPage = NumberUtils.stringToInt(val);
		}
	}
	else {
		queryString2 = queryString1;
	}

	if ( searchType.equals("spectrum") ) {
		bs.showQPeakResultTable(queryString2, viewPage);
	}
	else {
		bs.showResultTable(queryString2, viewPage, true);
	}
%>
</div>
<iframe src="../copyrightline.html" width="100%" height="50" frameborder="0" marginwidth="20" scrolling="no"></iframe>
</body>
</html>

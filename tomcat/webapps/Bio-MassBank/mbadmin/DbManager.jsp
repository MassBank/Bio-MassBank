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
 * DbManager.jsp
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
%>
<%@ page import="java.io.File" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.net.InetAddress" %>
<%@ page import="java.net.UnknownHostException" %>
<%@ page import="org.apache.commons.lang.NumberUtils" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="net.massbank.core.DispatcherServlet" %>
<%@ page import="net.massbank.admin.DbManager" %>
<%@ page import="net.massbank.admin.AdminUtil" %>
<%@ page import="net.massbank.admin.AdminDbUtil" %>
<%@ page import="net.massbank.admin.GetConfigDirect" %>
<%!
	private final String STATUS_OK   = "<span class=\"msgFont\">OK</span>";
	private final String STATUS_WARN = "<span class=\"warnFont\">WARN</span>";
	private final String STATUS_ERR  = "<span class=\"errFont\">ERROR</span>";
	private final String URL_TYPE_INTERNAL = "internal";
	private final String URL_TYPE_EXTERNAL = "external";
%>
<%
	String hostName = "";
	String ipAddress = "";
	try {
		hostName = InetAddress.getLocalHost().getHostName().toLowerCase();
		ipAddress = InetAddress.getLocalHost().getHostAddress();
	}
	catch (UnknownHostException e) {
		e.printStackTrace();
	}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="Content-Style-Type" content="text/css">
<meta http-equiv="Content-Script-Type" content="text/javascript">
<link rel="stylesheet" type="text/css" href="../css/Admin.css">
<script type="text/javascript" src="../script/jquery.js"></script>
<script type="text/javascript" src="../script/Common.js"></script>
<script type="text/javascript" src="../script/DbManager.js"></script>
<title>Admin | Database Manager</title>
</head>
<body onLoad="initLoad('<%=hostName%>','<%=ipAddress%>');">
<iframe src="menu.jsp?select_menu_no=2" width="100%" height="30" frameborder="0" marginwidth="0" scrolling="no" title=""></iframe>
<h2>Database Manager</h2>
<div id="main">
<%
	request.setCharacterEncoding("utf-8");
	String os = System.getProperty("os.name");
	String rootPath = application.getRealPath("");
	HashMap<String, String> inputs = new HashMap();
	String act = "";
	Enumeration<String> names = (Enumeration<String>)request.getParameterNames();
	while ( names.hasMoreElements() ) {
		String key = (String)names.nextElement();
		String val = request.getParameter(key);
		if ( key.equals("act") ) {
			act = request.getParameter(key);
		}
		else {
			inputs.put(key, val);
		}
	}
	int reqNo = -1;
	if ( inputs.containsKey("siteNo") ) {
		reqNo = NumberUtils.stringToInt(inputs.get("siteNo"));
	}

	boolean ret = true;
	if ( !act.equals("") ) {
		DbManager manage = new DbManager(inputs, rootPath, out);
		if ( act.equals("add") ) {
			ret = manage.add();
		}
		else if ( act.equals("edit") ) {
			ret = manage.edit();
		}
		else if ( act.equals("delete") ) {
			ret = manage.delete();
		}
	}
	GetConfigDirect conf = new GetConfigDirect(rootPath);
	DispatcherServlet.conf = conf;
	String[] dbNames  = conf.getDbNames();
	String[] urls     = conf.getSiteUrls();
	String[] sLabels  = conf.getSiteNames();
	String[] lLabels  = conf.getSiteLongNames();

	String selDb = "";
	String selShortLabel = "";
	String selLongLabel = "";
	String selUrl = "";
	String isUrlIntChecked = " checked";
	String isUrlExtChecked = "";
	String isUrlIntDisabled = "";
	String isUrlExtDisabled = "";
	String intLabelClass = "";
	String extLabelClass = "";
	if ( act.equals("add") || act.equals("edit") ) {
		if ( act.equals("add") ) {
			reqNo = dbNames.length - 1;
		}
		if ( ret ) {
			selDb = dbNames[reqNo];
			selShortLabel = sLabels[reqNo];
			selLongLabel = lLabels[reqNo];
			selUrl = urls[reqNo];
		}
		else {
			selDb = inputs.get("siteDb");
			selShortLabel = inputs.get("siteShortLabel");
			selLongLabel = inputs.get("siteLongLabel");
			selUrl = inputs.get("siteUrl");
		}
		if ( AdminUtil.isInternalUrl(selUrl) ) {
			isUrlIntChecked = " checked";
			isUrlExtDisabled = " disabled";
			extLabelClass = "readOnly";
		}
		else {
			isUrlExtChecked = " checked";
			isUrlIntDisabled = " disabled";
			intLabelClass = "readOnly";
		}
	}
	out.println( "<form name=\"formEdit\" method=\"post\" action=\"./DbManager.jsp\" onSubmit=\"doWait();\">" );
	out.println( "<div style=\"width:980px; border: 2px Gray solid; padding:15px; background-color:WhiteSmoke;\">" );
	out.println( "<table width=\"97%\" align=\"center\" cellspacing=\"2\" cellpadding=\"2\">" );
	out.println( "<tr>" );
	out.println( "<td width=\"50\" title=\"Database No.\"><b>No.</b></td>" );
	out.println( "<td width=\"35\"></td>" ) ;
	out.println( "<td width=\"180\" title=\"Database Name\"><b>DB Name</b></td>" );
	out.println( "<td width=\"180\" title=\"Short Label\"><b>Short Label</b></td>" );
	out.println( "<td title=\"Long Label\"><b>Long Label</b></td>" );
	out.println( "</tr>" );
	out.println( "<tr height=\"10\">" );
	out.println( "<td>" );
	out.println( "<select name=\"siteNo\" style=\"width:100%;\" onChange=\"selectNo();\">" );
	if ( reqNo == -1 ) {
		out.println( "<option value=\"-1\" selected>+</option>" );
	}
	else {
		out.println( "<option value=\"-1\">+</option>" );
	}

	for ( int i = 0; i < dbNames.length; i++ ) {
		if ( reqNo == i ) {
			out.println( "<option value=\"" + i + "\" selected>" + i + "</option>" );
		}
		else {
			out.println( "<option value=\"" + i + "\">" + i + "</option>" );
		}
	}
	out.println( "</select>" );
	out.println( "</td>" );
	out.println( "<td></td>" ) ;
	out.println( "<td><input type=\"text\" style=\"width:98%;\" name=\"siteDb\" value=\"" + selDb + "\"></td>" );
	out.println( "<td><input type=\"text\" style=\"width:98%;\" name=\"siteShortLabel\" value=\"" + selShortLabel + "\"></td>" );
	out.println( "<td><input type=\"text\" style=\"width:100%;\" name=\"siteLongLabel\" value=\"" + selLongLabel + "\"></td>" );
	out.println( "</tr>" );

	out.println( "<tr>" );
	out.println( "<td colspan=\"2\"></td>" );
	out.println( "<td title=\"Site URL Type\"><b>URL Type</b></td>" );
	out.println( "<td title=\"URL\"><b>URL</b></td>" );
	out.println( "</tr>" );

	out.println( "<tr>" );
	out.println( "<td colspan=\"2\"></td>" );
	out.println( "<td>" );
	out.println( "<input type=\"radio\" name=\"siteType\" value=\"" + URL_TYPE_INTERNAL + "\" onClick=\"selectUrlType();\"" + isUrlIntChecked + isUrlIntDisabled + "> <span id=\"intLabel\" class=\"" + intLabelClass + "\">internal</span>&nbsp;&nbsp;&nbsp;&nbsp;" );
	out.println( "<input type=\"radio\" name=\"siteType\" value=\"" + URL_TYPE_EXTERNAL + "\" onClick=\"selectUrlType();\"" + isUrlExtChecked + isUrlExtDisabled + "> <span id=\"extLabel\" class=\"" + extLabelClass + "\">external</span>" );
	out.println( "</td>" );
	out.println( "<td colspan=\"2\" id=\"inputUrl\"><input type=\"text\" style=\"width:100%;\" name=\"siteUrl\" value=\"" + selUrl + "\"></td>" );
	out.println( "</tr>");

	out.println( "<tr>");
	out.println( "<td colspan=\"5\" align=\"right\" height=\"40px\">" );
	String[] buttonValues = { "Add", "Edit", "Delete" };
	for ( String buttonValue : buttonValues ) {
		String buttonName = "btn" + buttonValue;
		String action = buttonValue.toLowerCase();
		String sctipt = "return checkInputValue('" + action + "','" + hostName + "','" + ipAddress + "')";
		out.println( "<input type=\"submit\" name=\"" + buttonName + "\" style=\"width:100px;\""
					+ " value=\"" + buttonValue + "\" onClick=\"" + sctipt + "\" disabled>" );
	}
	out.println( "</td>" );
	out.println( "</tr>" );
	out.println( "</table>" );
	out.println( "</div>" );
	out.println( "<input type=\"hidden\" name=\"act\" value=\"\">" );
	out.println( "</form>" );

	out.println( "\t<table width=\"980\" cellspacing=\"1\" cellpadding=\"0\" bgcolor=\"Lavender\" class=\"fixed\">" );
	out.println( "\t\t<thead>");
	out.println( "\t\t<tr class=\"rowHeader\">");
	out.println( "\t\t\t<td width=\"30\">No.</td>" );
	out.println( "\t\t\t<td width=\"90\">DB Name</td>" );
	out.println( "\t\t\t<td>URL</td>" );
	out.println( "\t\t\t<td width=\"120\">Short Label</td>" );
	out.println( "\t\t\t<td width=\"150\">Long Label</td>" );
	out.println( "\t\t\t<td width=\"64\">Status</td>" );
	out.println( "\t\t\t<td width=\"215\">Details</td>" );
	out.println( "\t\t</tr>");
	out.println( "\t\t</thead>");

	for ( int i = 0; i < dbNames.length; i++ ) {
		String status = STATUS_OK;
		StringBuilder details = new StringBuilder();
		String url = urls[i];
		String dbName = StringEscapeUtils.escapeHtml(dbNames[i]);
		String sLabel = StringEscapeUtils.escapeHtml(sLabels[i]);
		String lLabel = StringEscapeUtils.escapeHtml(lLabels[i]);
		String urlType = "";
		if ( dbName.equals("") || sLabel.equals("") || lLabel.equals("") || url.equals("") ) {
			status = STATUS_ERR;
			details.append( "<span class=\"errFont\">massbank.conf is wrong.</span><br>" );
		}
		if ( AdminUtil.isInternalUrl(url) ) {
			urlType = "internal";
			if ( !AdminDbUtil.existDB(dbName) ) {
				status = STATUS_ERR;
				details.append( "<span class=\"errFont\">database not exist in MySQL.</span><br>" );
			}
		}
		else {
			urlType = "external";
			details.append( "<span class=\"msgFont\">external database.</span><br>" );
		}

		out.println( "\t\t<tr class=\"rowEnable\" id=\"row" + i + "\">" );
		out.println( "\t\t\t<td class=\"manage\" id=\"no" + i + "No\">" + i + "</td>");
		out.println( "\t\t\t<td class=\"manage\" id=\"no" + i + "Db\">" + dbName + "</td>");
		out.println( "\t\t\t<td class=\"manage\" id=\"no" + i + "Url\"><a href=\"" + url + "\" target=\"_blank\" class=\"urlFont\">" + url + "</a></td>");
		out.println( "\t\t\t<td class=\"manage\" id=\"no" + i + "ShortLabel\">" + sLabel + "</td>");
		out.println( "\t\t\t<td class=\"manage\" id=\"no" + i + "LongLabel\">" + lLabel + "</td>");
		out.println( "\t\t\t<td class=\"center\" id=\"no" + i + "Status\">" + status + "</td>" );
		out.println( "\t\t\t<td class=\"details\">" + details.toString() );
		out.println( "\t\t\t\t<input type=\"hidden\" id=\"no" + i + "Type\" value=\"" +  urlType + "\">");
		out.println( "\t\t\t</td>" );
		out.println( "\t\t</tr>");
	}
	out.println( "</table>" );
%>
</div>
</body>
</html>

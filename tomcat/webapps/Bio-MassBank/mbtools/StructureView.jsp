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
 * StructureView.jsp
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
%>

<%@ page import="java.io.UnsupportedEncodingException" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="org.apache.commons.dbutils.DbUtils" %>
<%@ page import="org.apache.commons.dbutils.QueryRunner" %>
<%@ page import="org.apache.commons.dbutils.handlers.ArrayListHandler" %>
<%@ page import="net.massbank.core.GetDbUtil" %>
<%@ page import="net.massbank.core.common.CoreUtil" %>
<%
	String moldata = "";
	if ( request.getParameter( "molfile" ) != null ) {
		moldata = request.getParameter("molfile");
	}
	String dbName = "";
	if ( request.getParameter("db_name") != null ) {
		dbName = request.getParameter("db_name");
	}
	String compoundNo = "";
	if ( request.getParameter( "cno" ) != null ) {
		compoundNo = request.getParameter("cno");
	}
	String compoundName = "";
	if ( request.getParameter( "compound_name" ) != null ) {
		compoundName = request.getParameter("compound_name");
	}
	if ( moldata.equals("") && !dbName.equals("") && !compoundNo.equals("") ) {
		try {
			Connection con = GetDbUtil.connectDb(dbName);
			QueryRunner qr = new QueryRunner();
			String sql = "select COMPOUND_NAME, MOLFILE from COMPOUND_INFO where COMPOUND_NO=" + compoundNo;
			List<Object[]> results = (List)qr.query(con, sql, new ArrayListHandler());
			DbUtils.closeQuietly(con);
			Object[] vals = results.get(0);
			compoundName = (String)vals[0];
			moldata = ((String)vals[1]).replaceAll("\n","@LF@");
		}
		catch ( SQLException e ) {
			e.printStackTrace();
		}
	}
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="Content-Style-Type" content="text/css">
<meta http-equiv="Content-Script-Type" content="text/javascript">
<script type="text/javascript" src="../script/Common.js"></script>
<title><%=compoundName%></title>
</head>
<body style="margin-top:2px;">
	<table  cellspacing="0" cellpadding="0" border="0">
		<tr>
			<td><font style="font-size:9pt;font-family:Arial;color:navy"><b><%=compoundName%></b></font></td>
		</tr>
		<tr>
			<td>
				<applet code="net.massbank.applet.molview.MolView.class" archive="../applet/MolView.jar" width="200" height="200">
					<param name="compound_name" value="<%=compoundName%>">
					<param name="moldata" value="<%=moldata%>">
				</applet>
			</td>
		</tr>
	</table>
</body>
</html>

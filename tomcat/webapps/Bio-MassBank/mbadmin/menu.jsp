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
 * menu.jsp
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="Content-Style-Type" content="text/css">
<link rel="stylesheet" type="text/css" href="../css/Admin.css">
<title>Admin | Menu Link</title>
</head>
<body>
<%
	String[] menuNames = { "Record&nbsp;Registration", "Structure&nbsp;Registration", "Database&nbsp;Manager", "Home"};
	String[] linkUrls = { "./RecordRegister.jsp", "./StructRegister.jsp", "./DbManager.jsp", "../index.jsp" };

	out.println("<table id=\"menu\">");
	out.println("\t<tr>");
	out.println("\t\t<td align=\"left\">");
	out.print("\t\t\t<img src=\"../image/massbank-icon-black.gif\" align=\"absmiddle\">");
	out.println("<b style=\"color:LightCyan\">&nbsp;MassBank&nbsp;Administration&nbsp;Tool</b>&nbsp;&nbsp;&nbsp;<br>");
	out.println("\t\t</td>");

	String selectMenu = request.getParameter("select_menu_no");
	if ( selectMenu == null ) {
		selectMenu = "0";
	}
	int selectMenuNo = Integer.parseInt(selectMenu);
	for ( int i = 0; i < menuNames.length; i++ ) {
		out.println("\t\t<td><a href=\"" + linkUrls[i] + "\" target=\"_parent\">" + menuNames[i] + "</a></td>");
		out.println("\t\t<td>&nbsp;</td>");
	}
	out.println("\t\t<td width=\"100%\"></td>");
	out.println("\t</tr>");

	out.println("\t<tr>");
	out.println("\t\t<td>&nbsp;</td>");
	for ( int i = 0; i < menuNames.length; i++ ) {
		if ( i == selectMenuNo ) {
 			out.println("\t\t<td height=\"5\" class=\"selected\"></td>");
		}
		else {
 			out.println("\t\t<td height=\"5\" class=\"notSelected\"></td>");
		}
		out.println("\t\t<td>&nbsp;</td>");
	}
	out.println("\t\t<td width=\"100%\"></td>");
	out.println("\t</tr>");
	out.println("</table>");
%>
</body>
</html>

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
 * MultipleDisplay.jsp
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
%>
<%@ page import="java.util.*" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<meta http-equiv="Content-Type" content=\"text/html; charset=utf-8">
<meta http-equiv="Content-Style-Type" content="text/css">
<meta http-equiv="imagetoolbar" content="no">
<link rel="stylesheet" type="text/css" href="../css/Common.css">
<script type="text/javascript" src="../script/Common.js"></script>
<title>Bio-MassBank | Database | Multiple Display</title>
</head>
<body>
<iframe src="./menu.jsp" width="100%" height="26" frameborder="0" marginwidth="0" scrolling="no" class="menu"></iframe>
<div id="common_page_head">Multiple Display</div>
<div id="main">
<%
	String type = "";
	Enumeration names = request.getParameterNames();
	StringBuilder sb = new StringBuilder("");
	while ( names.hasMoreElements() ) {
		String key = (String)names.nextElement();
		String val = request.getParameter( key );
		sb.append("\t\t\t<param name=\"" + key + "\" value=\"" + val + "\">\n");
	}

	String ids = request.getParameter("id");
	String[] idList = ids.split(",");
	String h = Integer.toString(250 * idList.length );
	out.println( "\t\t<applet code=\"net.massbank.applet.display.DisplayApplet.class\" "
			+ "archive=\"../applet/DisplayApplet.jar\" width=\"980\" height=\"" + h + "\">" );
	out.print( sb.toString());
//	out.println( "\t\t\t<param name=\"id\" value=\"" + ids + "\">" );
	out.println( "\t\t\t<param name=\"is_multi\" value=\"true\">" );
	out.println( "\t\t</applet>" );
%>
</div>
<iframe src="../copyrightline.html" width="100%" height="50" frameborder="0" marginwidth="20" scrolling="no"></iframe>
</body>
</html>


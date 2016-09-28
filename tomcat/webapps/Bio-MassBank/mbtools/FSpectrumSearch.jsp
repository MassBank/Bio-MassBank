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
 * SpectrumSearch.jsp
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
%>
<%@ page import="java.io.File" %>
<%@ page import="java.io.FileReader" %>
<%@ page import="java.io.BufferedReader" %>
<%@ page import="net.massbank.core.FileUpload" %>
<%
	String fileName = "";
	if ( request.getParameter( "file" ) != null ) {;
		fileName = request.getParameter( "file" );
	}

	if ( fileName.equals("") ) {
		try {
			String tempName = "";
			boolean isMultipart = false;
			FileUpload upload = new FileUpload(request);
			isMultipart = upload.isMultipart();
			if ( isMultipart ) {
				String[] fileNames = upload.saveFiles("massbank", ".txt");
				tempName = fileNames[0];
			}
%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<meta http-equiv="Content-Style-Type" content="text/css">
	<link rel="stylesheet" type="text/css" href="../css/Common.css">
	<script type="text/javascript" src="../script/Common.js"></script>
	<title>Bio-MassBank | Database | Spectrum Search</title>
</head>
<body>
<iframe src="./menu.jsp?select_menu_no=1" width="100%" height="26" frameborder="0" marginwidth="0" scrolling="no" class="menu"></iframe>
<div id="common_page_head">Spectrum Search by Chemical Formula&nbsp;&nbsp;<span style="font-size:18px;color:Blue;">Prototype</span></div>
<div id="main">
	<form action="./FSpectrumSearch.jsp" enctype="multipart/form-data" method="POST">
		<img src="../image/file.gif" align="left">Query&nbsp;File
		<input type="file" name="File" size="32">&nbsp;
		<input type="submit" value="File Read" onClick="return checkFileExtention(forms[0].File.value);">&nbsp;&nbsp;
	</form>
	<applet code="net.massbank.applet.formulaSearch.FormulaSearchApplet.class" archive="../applet/FSearchApplet.jar" width="100%" height="100%" MAYSCRIPT>
<%
			if ( isMultipart ) {
				out.println( "\t\t<param name=\"file\" value=\"" + tempName + "\">" );
			}
			else {
				String qid = request.getParameter("qid");
				if ( qid != null && qid != "" ) {
					out.println( "\t\t<param name=\"qid\" value=\"" + qid + "\">" );
				}
			}
			out.println( "\t</applet>" );
			out.println( "\t</div>" );
			out.println( "</body>" );
			out.println( "</html>" );
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	else {
		String tempDir = System.getProperty("java.io.tmpdir");
		String filePath = tempDir + File.separator  + fileName;
		BufferedReader in = new BufferedReader( new FileReader(filePath) );
		String line = "";
		while ( ( line = in.readLine() ) != null ) {
			out.println( line );
		}
		in.close();
		File f = new File( filePath );
		f.delete();
	}
%>

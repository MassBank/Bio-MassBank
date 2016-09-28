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
 * StructRegister.jsp
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
%>
<%@ page import="java.io.File" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="org.apache.commons.io.FileUtils" %>
<%@ page import="org.apache.commons.io.FilenameUtils" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="net.massbank.core.GetDbUtil" %>
<%@ page import="net.massbank.core.FileUtil" %>
<%@ page import="net.massbank.core.FileUpload" %>
<%@ page import="net.massbank.core.common.CoreUtil" %>
<%@ page import="net.massbank.admin.AdminDbUtil" %>
<%@ page import="net.massbank.admin.StructRegister" %>
<%@ page import="net.massbank.admin.CompoundInfo" %>
<%!
	private final String MOLDATA_DIR_NAME = "moldata";
	private final String STATUS_OK   = "<span class=\"msgFont\">OK</span>";
	private final String STATUS_WARN = "<span class=\"warnFont\">WARN</span>";
	private final String STATUS_ERR  = "<span class=\"errFont\">ERROR</span>";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="Content-Style-Type" content="text/css">
<meta http-equiv="Content-Script-Type" content="text/javascript">
<link rel="stylesheet" type="text/css" href="../css/Admin.css">
<script type="text/javascript" src="../script/Common.js"></script>
<script type="text/javascript" src="../script/Admin.js"></script>
<title>Admin | Structure Registration</title>
</head>
<body>
<iframe src="./menu.jsp?select_menu_no=1" width="100%" height="30" frameborder="0" marginwidth="0" scrolling="no" class="menu"></iframe>
<h2>Structure&nbsp;Registration</h2>
<div id="main">
<%
	request.setCharacterEncoding("utf-8");
	String reqUrl = request.getRequestURL().toString();
	String baseUrl = CoreUtil.getBaseUrl(reqUrl);
	String tempDir = System.getProperty("java.io.tmpdir");
	FileUpload upload = new FileUpload(request);
	String act = "";
	String selDbName = "";
	String[] cnos = null;
	Map<String, String[]> params = upload.getRequestParameters();
	for ( Map.Entry<String, String[]> e : params.entrySet() ) {
		String key = e.getKey();
		String[] vals = e.getValue();
		if ( key.equals("act") )     { act = vals[0];       }
		else if ( key.equals("db") ) { selDbName = vals[0]; }
		else if ( key.equals("id") ) { cnos = vals;         }
	}

	List<String> dbNameList = AdminDbUtil.getExistDB();
	if ( dbNameList.size() == 0 ) {
//		out.println( msgErr( "[" + dbRootPath + "]&nbsp;&nbsp;directory not exist." ) );
		return;
	}
	String[] dbNames = dbNameList.toArray(new String[]{});
	out.println( "\t<form name=\"form_regist\" action=\"./StructRegister.jsp\" "
		+ "enctype=\"multipart/form-data\" method=\"post\" onSubmit=\"doWait();\">" );
	out.println( "\t<span class=\"baseFont\">Database :</span>&nbsp;" );
	out.println( "\t<select name=\"db\" class=\"db\">" );
	out.println( "\t\t<option value=\"\">------------------</option>" );
	for ( String dbName : dbNames ) {
		out.print( "\t\t<option value=\"" + dbName + "\"" );
		if ( dbNames.length == 1 || dbName.equals(selDbName) ) {
			out.print( " selected" );
		}
		out.println( ">" + dbName + "</option>" );
	}
	out.println( "\t</select>" );
	out.println( "\t<span style=\"padding-left: 30px;\"></span><span class=\"baseFont\">Structure&nbsp;Archive:</span>" );
	out.println( "\t<input type=\"file\" name=\"file\" size=\"60\">" );
	out.println( "\t<input id=\"regist\" type=\"submit\" value=\"Registration\" onClick=\"return doRegister();\"><br><br>" );
	if ( act.equals("get") ) {
		out.println( "\t<input id=\"delete\" type=\"submit\" value=\"Delete\" onClick=\"return doDelete();\">" );
	}
	out.println( "\t<input id=\"get\" type=\"submit\" value=\"Get List\" onClick=\"return doGet();\"><br>" );

	if ( act.equals("get") ) {
		List<CompoundInfo> compoundInfoList = AdminDbUtil.getCompoundInfoList(selDbName);
		if ( compoundInfoList == null ) {
			String info = "<i>Error</i>&nbsp;:&nbsp;"
				 + "<span class=\"errFont\">Internal error (failed to execute SQL)</span><br>";
			out.println(info);
			return;
		}

		int regCnt = 0;
		for ( CompoundInfo info : compoundInfoList ) {
			String moldata = info.getMolfileData();
			if ( !moldata.equals("") ) {
				regCnt++;
			}
		}
		NumberFormat nf = NumberFormat.getNumberInstance();
		out.println( "\t<div class=\"count baseFont\">" + nf.format(regCnt)
				   + " registered / " + nf.format(compoundInfoList.size()) + " compounds&nbsp;</div>" );

		out.println( "\t<table table width=\"980\" cellspacing=\"1\" cellpadding=\"0\" bgcolor=\"Lavender\">" );
		out.println( "\t\t<tr class=\"rowHeader\">");
		out.println( "\t\t\t<td width=\"25\"><input type=\"checkbox\" name=\"chkAll\" onClick=\"checkAll();\"></td>" );
		out.println( "\t\t\t<td width=\"430\">Compound Name</td>" );
		out.println( "\t\t\t<td width=\"80\">Structure</td>" );
		out.println( "\t\t\t<td width=\"70\">Status</td>" );
		out.println( "\t\t\t<td width=\"200\">Details</td>" );
		out.println( "\t\t</tr>");

		int cnt = 0;
		for ( CompoundInfo info : compoundInfoList ) {
			int cno = info.getCompoundNo();
			String cname  = StringEscapeUtils.escapeHtml(info.getCompoundName());
			String moldata = info.getMolfileData();
			String detail = "";
			String status = STATUS_OK;
			boolean existMoldata = true;
			String className = "rowEnable";
			if ( moldata.equals("") ) {
				detail = "unregistered molfile data";
				status = STATUS_WARN;
				existMoldata = false;
				className = "rowDisable";
			}

			String url = "../mbtools/StructureView.jsp?db_name=" + selDbName + "&cno=" + cno;
			out.println( "\t\t<tr class=\"" + className + "\" id=\"row" + String.valueOf(cnt) + "\">" );
			out.println( "\t\t\t<td>" );
			String html = "\t\t\t\t<input type=\"checkbox\" name=\"id\" "
						+ "value=\"" + cno + "\" onClick=\"check(" + cnt + ");\"";
			if ( !existMoldata ) { html += " disabled"; }
			html += ">";
			out.println( html );

			out.println( "\t\t\t</td>" );

			out.println( "\t\t\t<td class=\"leftIndent\">" + cname + "</td>" );
			out.println( "\t\t\t<td class=\"center\">" );
			html = "\t\t\t\t<input type=\"button\" "
				 + "onClick=\"popupMolView('" + url + "');\" value=\"View\" title=\"" + cname + "\"";
			if ( !existMoldata ) { html += " disabled"; }
			html += ">";
			out.println( html );

			out.println( "\t\t\t</td>" );
			out.println( "\t\t\t<td align=\"center\">" + status + "</td>" );
			out.println( "\t\t\t<td class=\"details\">" + detail + "</td>" );
			out.println( "\t\t</tr>" );
			cnt++;
		}
		out.println( "\t</table>" );
	}
	//-------------------------------------------------------------------------
	// Registration
	//-------------------------------------------------------------------------
	else if ( act.equals("register") ) {
		out.println("<br><b>.. Done.</b><br>");
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd_HHmmssSS");
		File file = new File( tempDir + File.separator + sdf.format(new Date()) );
		file.mkdir();
		String outPath = file.getPath() + File.separator;
		String[] fileNames = upload.saveFiles(outPath);
		if ( fileNames == null ) {
			String info = "<i>Error</i>&nbsp;:&nbsp;"
				 + "<span class=\"errFont\">Internal error (failed to upload file)</span><br>";
			out.println(info);
			return;
		}
		String uploadFilePath = outPath + fileNames[0];
		if ( !FileUtil.unzip(uploadFilePath) ) {
			String info = "<i>Error</i>&nbsp;:&nbsp;"
				 + "<span class=\"errFont\">Internal error (failed to unzip)</span><br>";
			out.println(info);
			return;
		}
		String molDataPath = FilenameUtils.getFullPath(uploadFilePath) + MOLDATA_DIR_NAME + File.separator;
		StructRegister register = new StructRegister(selDbName, molDataPath);
		boolean ret = register.execute();
		if ( ret ) {
			out.println("<span class=\"success\">Registration Success!</span><br>");
		}
		String[] infos = register.getInformations();
		if ( infos.length > 0 ) {
			out.println("<hr class=\"admin_info\">");
			for ( String info : infos ) {
				out.println(info);
			}
		}
		FileUtils.deleteQuietly(new File(outPath));
	}
	//-------------------------------------------------------------------------
	// delete
	//-------------------------------------------------------------------------
	else if ( act.equals("delete") ) {
		if ( selDbName != "" && cnos != null ) {
			boolean ret = AdminDbUtil.deleteMoldata(selDbName, cnos);
			out.println("<br><b>.. Done.</b>");
		}
	}
	out.println( "\t<input type=\"hidden\" name=\"act\" value=\"\">" );
	out.println( "</form>" );
%>
</div>
</body>
</html>

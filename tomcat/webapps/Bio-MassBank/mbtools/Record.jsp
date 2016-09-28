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
 * Record.jsp
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
%>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.lang.StringBuilder" %>
<%@ page import="java.util.regex.Matcher" %>
<%@ page import="java.util.regex.Pattern" %>
<%@ page import="net.massbank.core.get.record.GetRecordDataInvoker" %>
<%@ page import="net.massbank.core.get.record.GetRecordDataResult" %>
<%
	final Map<String, String> mapFormat =
		new HashMap<String, String>() {{
		put("LICENSE",                     "http://creativecommons.org/licenses/");
//		put("PUBLICATION",                 "http://www.ncbi.nlm.nih.gov/pubmed/%s?dopt=Citation");
		put("COMMENT: [MS]",               "http://bio.massbank.jp/mbtools/Record.jsp?id=%s");
		put("COMMENT: [MSn]",              "http://bio.massbank.jp/mbtools/Record.jsp?id=%s");
		put("COMMENT: [Merging]",          "http://bio.massbank.jp/mbtools/Record.jsp?id=%s");
		put("COMMENT: [Merged]",           "http://bio.massbank.jp/mbtools/Record.jsp?id=%s");
		put("COMMENT: [Meta]",             "http://webs2.kazusa.or.jp/metabolonote/index.php/%s");
		put("COMMENT: [Metabolonote]",     "http://webs2.kazusa.or.jp/metabolonote/index.php/%s");
		put("COMMENT: [MassBase ID]",      "http://webs2.kazusa.or.jp/massbase/index.php?action=Massbase_ShowPeakListPage&md_id=%s");
		put("COMMENT: [Mass spectrometry]","");
		put("COMMENT: [Chromatography]",   "");
		put("COMMENT: [Profile]",          "../DB/profile/%s/%s");
		put("COMMENT: [Mixture]",          "Dispatcher.jsp?type=disp&id=%s&site=%s");
		put("CH$LINK: CAS",                "http://webbook.nist.gov/cgi/cbook.cgi?ID=%s");
		put("CH$LINK: CAYMAN",             "http://www.caymanchem.com/app/template/Product.vm/catalog/%s");
		put("CH$LINK: CHEBI",              "http://www.ebi.ac.uk/chebi/searchId.do?chebiId=CHEBI:%s");
		put("CH$LINK: CHEMPDB",            "http://www.ebi.ac.uk/msd-srv/chempdb/cgi-bin/cgi.pl?FUNCTION=getByCode&amp;CODE=%s");
		put("CH$LINK: CHEMSPIDER",         "http://www.chemspider.com/%s");
		put("CH$LINK: FLAVONOIDVIEWER",    "http://www.metabolome.jp/software/FlavonoidViewer/");
		put("CH$LINK: HMDB",               "http://www.hmdb.ca/metabolites/%s");
		put("CH$LINK: KAPPAVIEW",          "http://kpv.kazusa.or.jp/kpv4/compoundInformation/view.action?id=%s");
		put("CH$LINK: KNAPSACK",           "http://kanaya.naist.jp/knapsack_jsp/info.jsp?sname=C_ID&word=%s");
		put("CH$LINK: LIPIDBANK",          "http://lipidbank.jp/cgi-bin/detail.cgi?id=%s");
		put("CH$LINK: LIPIDMAPS",          "http://www.lipidmaps.org/data/get_lm_lipids_dbgif.php?LM_ID=%s");
		put("CH$LINK: NIKKAJI",            "http://nikkajiweb.jst.go.jp/nikkaji_web/pages/top.jsp?SN=%s&CONTENT=syosai");
		put("CH$LINK: OligosaccharideDataBase",  "http://www.fukuyama-u.ac.jp/life/bio/biochem/%s.html%s");
		put("CH$LINK: OligosaccharideDataBase2D","http://www.fukuyama-u.ac.jp/life/bio/biochem/%s.html");
		put("SP$LINK: NCBI-TAXONOMY",            "http://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?id=%s");
		put("MS$RELATED_MS: PREVIOUS_SPECTRUM", "Dispatcher.jsp?type=disp&id=%s");
	}};
	String id = request.getParameter("id");
	if ( id == null ) {
		return;
	}

	String title = id + "&nbsp;Record Not Found";
	String reqUrl = request.getRequestURL().toString();
	GetRecordDataInvoker inv = new GetRecordDataInvoker(reqUrl, new String[]{id});
	inv.invoke();
	GetRecordDataResult result = inv.getResults();
	boolean isFound = false;
	if ( result != null ) {
		isFound = true;
		title = result.getRecordTitle(id);
	}

	String res = "";
	StringBuilder sb = new StringBuilder("");
	sb.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n");
	sb.append("<html lang=\"en\">\n");
	sb.append("<head>\n");
	sb.append("\t<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n");
	sb.append("\t<meta http-equiv=\"Content-Style-Type\" content=\"text/css\">\n");
	sb.append("\t<meta http-equiv=\"Content-Script-Type\" content=\"text/javascript\">\n");
	sb.append("\t<link rel=\"stylesheet\" type=\"text/css\" href=\"../css/Common.css\">\n");
	sb.append("\t<script type=\"text/javascript\" src=\"../script/Common.js\"></script>\n");
	sb.append("\t<title>" + title + "&nbsp;Mass Spectrum</title>\n");
	sb.append("</head>\n");
	sb.append("<body>\n");
	if ( isFound ) {
		String text = result.getFullText(id);
		String siteNo = result.getSiteNo(id);
		sb.append("<iframe src=\"./menu.jsp\" width=\"100%\" height=\"26\" frameborder=\"0\" marginwidth=\"0\" scrolling=\"no\" class=\"menu\"></iframe>\n");
		sb.append("<div id=\"record_page_head\">\n" );
		sb.append("MassBank Record:&nbsp;" + id + "<br>\n");
		sb.append("\t<span id=\"record_page_title\">" + title + "</span>\n");
		sb.append("</div>\n");
		sb.append("<div id=\"record_page_contents\">\n" );
		sb.append("\t<applet code=\"net.massbank.applet.display.DisplayApplet.class\" archive=\"../applet/DisplayApplet.jar\" width=\"980\" height=\"210\">\n");
		Enumeration<String> names = (Enumeration<String>)request.getParameterNames();
		while ( names.hasMoreElements() ) {
			String key = (String)names.nextElement();
			String val = request.getParameter(key);
			sb.append("\t\t<param name=\"" + key + "\" value=\"" + val + "\">\n");
		}
		sb.append("\t</applet>\n");
		sb.append("\t<hr size=\"1\">\n");

		String[] lines = text.split("\n");
		sb.append("\t<pre>\n");
		String head = "";
		String prevHead = "";
		for ( String line : lines ) {
			int pos = line.indexOf("$");
			if ( pos >= 0 ) {
				head = line.substring(0,3);
				if ( !head.equals(prevHead) ) {
					sb.append("<hr size=\"1\" color=\"silver\" width=\"98%\" align=\"left\">");
				}
				prevHead = head;
			}

			int pos1 = line.indexOf(":");
			if ( pos1 >= 0 ) {
				int c1 = pos1;
				int c2 = pos1 + 2;
				int pos2 = 0;
				if ( !head.equals("") ) {
					pos2 = line.indexOf(" ", pos1 + 2);
					if ( pos2 >= 0 ) {
						c1 = pos2;
						c2 = pos2 + 1;
					}
				}
				else {
					pos2 = line.indexOf("]", pos1 + 2);
					if ( pos2 >= 0 ) {
						c1 = pos2 + 1;
						c2 = pos2 + 2;
					}
				}

				String tag = line.substring(0, c1);
				String val = line.substring(c2, line.length());
				if ( mapFormat.containsKey(tag) ) {
					String fmt = mapFormat.get(tag);
					val = val.trim();
					if ( tag.equals("COMMENT: [Meta]") || tag.equals("COMMENT: [Metabolonote]")) {
						String[] items = val.split("_");
						if ( items.length > 1 ) {
							String url = String.format(fmt, items[0] + ":/" + items[1] + "/" + items[2] + "/" + items[3]);
							val = items[0] + "_" + items[1] + "_" + items[2] + "_" + items[3];
							line = tag + " <a href=\"" + url + "\" target=\"_blank\">" + val + "</a>";
							if ( items.length == 5 ) {
								line += "_" + items[4];
							}
						}
					}
					else {
						String[] vals = null;
						if ( line.indexOf("CH$LINK") >= 0 ) {
							vals = val.split(" ");
						}
						else {
							vals = new String[]{val};
						}
						if ( head.equals("") && tag.indexOf("COMMENT") == -1 ) {
							tag += ":";
						}
						line = tag;
						for ( int i = 0; i < vals.length; i++ ) {
							String url = String.format(fmt, vals[i]);
							line += " <a href=\"" + url + "\" target=\"_blank\">" + vals[i] + "</a>";
						}
					}
				}
				else {
					int pos3 = line.indexOf("CH$LINK: PUBCHEM");
					if ( pos3 >= 0 ) {
						String param = "";
						String items[] = val.split(":");
						if ( items.length == 1 ) {
							param = "sid=" + items[0];
						}
						else {
							param = items[0].toLowerCase() + "=" + items[1];
							tag += " " + items[0] + ":";
							val = items[1];
						}
						val = val.trim();
						String url = "http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?" + param;
						line = tag + "<a href=\"" + url + "\" target=\"_blank\">" + val + "</a>";
					}
					int pos4 = line.indexOf("CH$LINK: KEGG");
					if ( pos4 >= 0 ) {
						String[] vals = null;
						if ( line.indexOf("CH$LINK") >= 0 ) {
							vals = val.split(" ");
						}
						else {
							vals = new String[]{val};
						}
						line = tag;
						for ( int i = 0; i < vals.length; i++ ) {
							String param = "";
							String pre = vals[i].substring(0,1);
							if ( pre.equals("G") ) {
								param = "gl:" + vals[i];
							}
							else if ( pre.equals("D") ) {
								param = "dr:" + vals[i];
							}
							else {
								param = "cpd:" + vals[i];
							}
							vals[i] = vals[i].trim();
							String url = "http://www.genome.jp/dbget-bin/www_bget?" + param;
							line += " <a href=\"" + url + "\" target=\"_blank\">" + vals[i] + "</a>";
						}
					}
				}
			}
			sb.append(line + "\n");
		}
		sb.append("\t</pre>\n");
	}
	else {
		// record not found
		sb.append( "\t<font size=\"+2\"><b>" + title + "</b></font><br><br>");
	}
	sb.append("</div>\n");
	sb.append("<iframe src=\"../copyrightline.html\" width=\"100%\" height=\"50\" frameborder=\"0\" marginwidth=\"20\" scrolling=\"no\"></iframe>");
	sb.append("</body>\n");
	sb.append("</html>\n");
	res = sb.toString();
	out.println(res);
%>

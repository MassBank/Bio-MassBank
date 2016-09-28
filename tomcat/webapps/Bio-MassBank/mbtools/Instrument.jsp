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
 * Instrument.jsp
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
%>
<%@ page import="java.util.*" %>
<%@ page import="java.net.*" %>
<%@ page import="java.io.UnsupportedEncodingException" %>
<%@ page import="net.massbank.core.DispatcherServlet" %>
<%@ page import="net.massbank.core.common.GetConfig" %>
<%@ page import="net.massbank.core.get.instrument.GetInstrumentInvoker" %>
<%@ page import="net.massbank.core.get.instrument.GetInstrumentResults" %>
<%!
	private final String COOKIE_COMMON = "Common";
	private final String COOKIE_INSTGRP = "INSTGRP";
	private final String COOKIE_INST = "INST";
	private final String COOKIE_MS = "MS";
	private final String COOKIE_ION = "ION";
	
	/**
	 *
	 */
	public String getCookie(Cookie c, String key) {
		String cookieValue = "";
		if (c != null) {
			String tmpValues = "";
			try {
				tmpValues = URLDecoder.decode(c.getValue(), "utf-8");
			}
			catch ( UnsupportedEncodingException e ) {
				e.printStackTrace();
			}
			if ( tmpValues.trim().length() != 0 ) {
				String[] data = tmpValues.split(";");
				for ( int i = 0; i < data.length; i++ ) {
					String[] item = data[i].split("=");
					if ( item[0].trim().equals(key) ) {
						if ( item.length == 2 ) {
							cookieValue = item[1].trim();
						}
						break;
					}
				}
			}
		}
		return cookieValue;
	}
%>
<%
	String reqUrl = request.getRequestURL().toString();
	GetInstrumentInvoker inv = new GetInstrumentInvoker(reqUrl);
	inv.invoke();
	GetInstrumentResults results = inv.getResults();
	String[] instTypes = results.getInstTypes();
	Map<String, String[]> instTypeGroups = results.getInstTypeGroups();
	String[] msInfo = results.getMsTypes();

	Cookie commonCookie = null;
	GetConfig conf = DispatcherServlet.conf;
	if ( conf == null ) {
		conf = new GetConfig();
	}
	boolean enableCookie = conf.isCookie();
	if ( enableCookie ) {
		final Cookie[] allCookies = request.getCookies();
		if ( allCookies != null ) {
			for ( int i = 0; i < allCookies.length; i++ ) {
				if ( allCookies[i].getName().equals(COOKIE_COMMON) ) {
					commonCookie = allCookies[i];
				}
			}
		}
	}

	boolean isDefault = true;
	String first = request.getParameter("first");
	if ( first != null && first.equals("false") ) {
		isDefault = false;
	}
	String[] chkInstType = null;
	String[] chkInstGrp = null;
	String[] chkMsType = null;
	String ionMode = "";

	if ( commonCookie != null ) {
		isDefault = false;
		chkInstType = getCookie(commonCookie, COOKIE_INST).split(",");
		chkInstGrp = getCookie(commonCookie, COOKIE_INSTGRP).split(",");
		chkMsType = getCookie(commonCookie, COOKIE_MS).split(",");
		ionMode = getCookie(commonCookie, COOKIE_ION);
	}
	else {
		isDefault = true;
		String tmpInstType = request.getParameter("inst");
		if ( tmpInstType != null && !tmpInstType.equals("") ) {
			chkInstType = tmpInstType.split(",");
		}
		String tmpInstGrp = request.getParameter("inst_grp");
		if ( tmpInstGrp != null && !tmpInstGrp.equals("") ) {
			chkInstGrp = tmpInstGrp.split(",");
		}
		String tmpMsType = request.getParameter("ms");
		if ( tmpMsType != null && !tmpMsType.equals("") ) {
			chkMsType = tmpMsType.split(",");
		}
		String tmpIonMode = request.getParameter("ion");
		if ( tmpIonMode != null ) {
			ionMode = tmpIonMode;
		}
	}
	if ( !ionMode.equals("1") && !ionMode.equals("0") && !ionMode.equals("-1") ) {
		ionMode = "1";
	}
	String jsCookie = "setCookie('" + enableCookie + "','" + COOKIE_COMMON + "','" + COOKIE_INSTGRP
			+ "','" + COOKIE_INST + "', '" + COOKIE_MS + "', '" + COOKIE_ION + "');\"";
	out.println( "\t\t\t\t\t\t<table width=\"340\" class=\"condition\">" );
	out.println( "\t\t\t\t\t\t\t<tr>" );
	out.println( "\t\t\t\t\t\t\t\t<td colspan=\"2\" class=\"condition_title\">" );
	out.println( "\t\t\t\t\t\t\t\t\t<b>Instrument&nbsp;Type</b>" );
	out.println( "\t\t\t\t\t\t\t\t</td>" );
	out.println( "\t\t\t\t\t\t\t</tr>" );
	out.println( "\t\t\t\t\t\t</table>" );
	out.println( "\t\t\t\t\t\t<div class=\"instrument-scroll\">" );
	out.println( "\t\t\t\t\t\t\t<table width=\"310\">" );
	Iterator it = instTypeGroups.keySet().iterator();
	while ( it.hasNext() ) {
		String key = (String)it.next();
		String[] list = instTypeGroups.get(key);
		out.println( "\t\t\t\t\t\t\t\t<tr valign=\"top\">" );
		out.println( "\t\t\t\t\t\t\t\t\t<td width=\"80\" style=\"padding:5px;\">" );
		out.print( "\t\t\t\t\t\t\t\t\t\t<input type=\"checkbox\" name=\"inst_grp\" id=\"inst_grp_" + key + "\""
					+ " value=\"" + key + "\" onClick=\"selBoxGrp('" + key + "', " + list.length + ");"
					+ jsCookie );
		if ( isDefault ) {
			if ( key.equals("ESI") ) {
				out.print( " checked" );
			}
		}
		else {
			if ( chkInstGrp != null ) {
				for ( int lp = 0; lp < chkInstGrp.length; lp++ ) {
					if ( key.equals(chkInstGrp[lp]) ) {
						out.print( " checked" );
						break;
					}
				}
			}
		}
		out.print( ">" + key );
		out.println( "\t\t\t\t\t\t\t\t\t</td>" );
		out.println( "\t\t\t\t\t\t\t\t\t<td style=\"padding:5px;\">" );
		for ( int j = 0; j < list.length; j++ ) {
				String val = list[j];
				out.print( "\t\t\t\t\t\t\t\t\t\t<input type=\"checkbox\" name=\"inst\" id=\"inst_" + key + j + "\""
					 + " value=\"" + val + "\" onClick=\"selBoxInst('" + key + "'," + list.length + ");" + jsCookie );
				if ( isDefault ) {
					if ( key.equals("ESI") ) {
						out.print( " checked" );
					}
				}
				else {
					if ( chkInstType != null ) {
						for ( int lp = 0; lp < chkInstType.length; lp++ ) {
							if ( val.equals(chkInstType[lp]) ) {
								out.print( " checked" );
								break;
							}
						}
					}
				}
				out.println( ">" + val + "<br>" );
		}
		out.println( "\t\t\t\t\t\t\t\t\t</td>" );
		out.println( "\t\t\t\t\t\t\t\t</tr>" );
		if ( it.hasNext() ) {
			out.println( "\t\t\t\t\t\t\t\t<tr>" );
			out.println( "\t\t\t\t\t\t\t\t\t<td colspan=\"2\"><hr width=\"96%\" size=\"1\" color=\"silver\" align=\"center\"></td>" );
			out.println( "\t\t\t\t\t\t\t\t</tr>" );
		}
	}
	out.println( "\t\t\t\t\t\t\t</table>" );
	out.println( "\t\t\t\t\t\t</div><br>" );

	out.println( "\t\t\t\t\t\t<table width=\"340\" class=\"condition\">" );
	out.println( "\t\t\t\t\t\t\t<tr>" );
	String condMsTitle = "condMsTitle";
	out.println( "\t\t\t\t\t\t\t\t<td colspan=\"2\" id=\"" + condMsTitle + "\" class=\"condition_title\">" );
	out.println( "\t\t\t\t\t\t\t\t\t<b>MS&nbsp;Type</b>" );
	out.println( "\t\t\t\t\t\t\t\t</td>" );
	out.println( "\t\t\t\t\t\t\t</tr>" );
	out.println( "\t\t\t\t\t\t\t<tr>" );
	out.println( "\t\t\t\t\t\t\t\t<td colspan=\"2\" class=\"condition_item\">" );
	if ( msInfo.length > 0 ) {
		String allCheked = "";
		if ( isDefault ) {
			allCheked = " checked";
		}
		else {
			if ( chkMsType != null ) {
				for ( int lp = 0; lp < chkMsType.length; lp++ ) {
					if ( "all".equals(chkMsType[lp]) ) {
						allCheked = " checked";
						break;
					}
				}
			}
		}
		String js1 = "selAllMs(" + msInfo.length + ", 0);";
		out.println( "\t\t\t\t\t\t\t\t\t<input type=\"checkbox\" name=\"ms\" id=\"ms_MS0\" value=\"all\" "
				+ "onClick=\"" + js1 + jsCookie + allCheked + ">All&nbsp;&nbsp;&nbsp;" );
	}
	else {
		out.println( "\t\t\t\t\t\t\t\t\t&nbsp;" );
	}

	String js1 = "selMs(" + msInfo.length + ", 0);";
	for ( int i = 0; i < msInfo.length; i++ ) {
		out.print( "\t\t\t\t\t\t\t\t\t<input type=\"checkbox\" name=\"ms\" id=\"ms_MS" + (i+1) + "\""
				+ " value=\"" + msInfo[i] + "\" onClick=\"" + js1 + jsCookie );
		if ( isDefault ) {
			out.print( " checked" );
		}
		else {
			if ( chkMsType != null ) {
				for ( int lp = 0; lp < chkMsType.length; lp++ ) {
					if ( msInfo[i].equals(chkMsType[lp]) || chkMsType[lp].equals("all") ) {
						out.print( " checked" );
						break;
					}
				}
			}
		}
		out.println( ">" + msInfo[i] + "&nbsp;" );
	}
	out.println( "\t\t\t\t\t\t\t\t</td>" );
	out.println( "\t\t\t\t\t\t\t</tr>" );
	out.println( "\t\t\t\t\t\t</table><br>" );

	out.println( "\t\t\t\t\t\t<table width=\"340\" class=\"condition\">" );
	out.println( "\t\t\t\t\t\t\t<tr>" );
	out.println( "\t\t\t\t\t\t\t\t<td colspan=\"2\" class=\"condition_title\">" );
	out.println( "\t\t\t\t\t\t\t\t\t<b>Ion&nbsp;Mode</b>" );
	out.println( "\t\t\t\t\t\t\t\t</td>" );
	out.println( "\t\t\t\t\t\t\t</tr>" );
	out.println( "\t\t\t\t\t\t\t<tr>" );
	out.println( "\t\t\t\t\t\t\t\t<td colspan=\"2\" class=\"condition_item\">" );

	String[] ionVals = { "1", "-1", "0" };
	String[] ionStrs = { "Positive&nbsp;&nbsp;", "Negative&nbsp;&nbsp;&nbsp;&nbsp;", "Both" };
	for ( int i = 0; i < ionVals.length; i++ ) {
		out.print( "\t\t\t\t\t\t\t\t\t<input type=\"radio\" name=\"ion\" value=\"" + ionVals[i] + "\" "
				+ "onClick=\"" + jsCookie );
		if ( ionMode.equals(ionVals[i]) ) {
			out.print( " checked" );
		}
		out.println( ">" +  ionStrs[i] );
	}
	out.println( "\t\t\t\t\t\t\t\t</td>" );
	out.println( "\t\t\t\t\t\t\t</tr>" );
	out.println( "\t\t\t\t\t\t</table>" );
%>

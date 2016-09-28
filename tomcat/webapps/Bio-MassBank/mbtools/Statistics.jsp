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
 * Statistics.jsp
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
%>
<%@ page import="net.massbank.core.common.GetConfig" %>
<%@ page import="net.massbank.core.common.CoreUtil" %>
<%@ page import="net.massbank.tools.statistics.StatisticsInvoker" %>
<%@ page import="net.massbank.tools.statistics.StatisticsResult" %>
<%!
	public String makeTable(String[] names, String[] urls, String[] numbers, int numSplit) {
		int width = 820;
		StringBuilder html = new StringBuilder();
		html.append("<div style=\"width:" + width + "px;\">\n");
		html.append("\t<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n");

		for ( int i = 0; i < names.length; i++ ) {
			if ( (i % numSplit) == 0 ) {
				html.append("\t\t<tr>\n");
				html.append("\t\t\t<td width=\"20\"></td>\n");
			}
			html.append("\t\t\t<td width=\"" + String.valueOf(width/numSplit) + "px\">" );
			html.append("<a href=\"" + urls[i] + "\" target=\"_top\">" + names[i] + "</a>&nbsp;(" + numbers[i] + ")</td>\n" );
			if ( ((i+1) % numSplit) == 0 || i == names.length - 1 ) {
				html.append("\t\t</tr>\n");
			}
		}
		html.append("\t</table>\n");
		html.append("</div>\n");
		html.append("<br clear=\"all\"><br>\n");
		return html.toString();
	}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="Content-Style-Type" content="text/css">
<link rel="stylesheet" type="text/css" href="../css/Common.css">
<script type="text/javascript" src="../script/jquery.js"></script>
<script type="text/javascript" src="../script/curvycorners.js"></script>
<script type="text/javascript">
window.onload = function() {
	var settings = {
		tl: { radius: 10 },
		tr: { radius: 10 },
		bl: { radius: 10 },
		br: { radius: 10 },
		antiAlias: true
	}
	new curvyCorners(settings, "#statistics");
}
</script>
<title>Bio-MassBank | Statistics</title>
</head>
<body>
<%
	String reqUrl = request.getRequestURL().toString();
	StatisticsInvoker inv = new StatisticsInvoker(reqUrl);
	inv.invoke();
	StatisticsResult results = inv.getResults();
	String baseUrl = CoreUtil.getBaseUrl(reqUrl);
	GetConfig conf = new GetConfig(baseUrl);
	String[] siteNames = conf.getSiteNames();
	String[] siteLongNames = conf.getSiteLongNames();
	String[] siteUrls = conf.getSiteUrls();
	String resultUrl = siteUrls[0] + "mbtools/SearchResult.jsp?";

	String[] contributors = results.getContributors();
	String[] numContributors = new String[contributors.length];
	String[] urls1 = new String[contributors.length];
	for ( int i = 0; i < contributors.length; i++ ) {
		numContributors[i] = results.getNumOfContributor(contributors[i]);
		int siteNo = 0;
		for ( int j = 0; j < siteNames.length; j++ ) {
			if ( siteNames[j].equals(contributors[i]) ) {
				contributors[i] = siteLongNames[j];
				siteNo = j;
				break;
			}
		}
		urls1[i] = resultUrl + "inst=all&ms=all&ion=0&site_no=" + String.valueOf(siteNo);
	}

	String[] instTypes = results.getInstumentTypes();
	String[] numInstTypes = new String[instTypes.length];
	String[] urls2 = new String[instTypes.length];
	for ( int i = 0; i < instTypes.length; i++ ) {
		numInstTypes[i] = results.getNumOfInstumentType(instTypes[i]);
		urls2[i] = resultUrl + "inst=" + instTypes[i] + "&ms=all&ion=0";
	}

	String[] msTypes =  results.getMsTypes();
	String[] numMsTypes = new String[msTypes.length];
	String[] urls3 = new String[msTypes.length];
	for ( int i = 0; i < msTypes.length; i++ ) {
		numMsTypes[i] = results.getNumOfMsType(msTypes[i]);
		urls3[i] = resultUrl + "inst=all&ms=" + msTypes[i] + "&ion=0";
	}

	String[] ionModes = { "Positive", "Negative" };
	String[] numIonModes = { results.getNumOfIonPos(), results.getNumOfIonNeg() };
	String[] urls4 = {
		resultUrl + "inst=all&ms=all&ion=1", resultUrl + "inst=all&ms=all&ion=-1"
	};

	String[] sampleNames = results.getSampleNames();
	String[] numSampleNames = new String[sampleNames.length];
	String[] urls5 = new String[sampleNames.length];
	for ( int i = 0; i < sampleNames.length; i++ ) {
		numSampleNames[i] = results.getNumOfSampleName(sampleNames[i]);
		urls5[i] = resultUrl + "keyword=SP$SAMPLE: " + sampleNames[i];
	}

	String html1 = makeTable(contributors, urls1, numContributors, 3);
	String html2 = makeTable(instTypes, urls2, numInstTypes, 3);
	String html3 = makeTable(msTypes, urls3, numMsTypes, 4);
	String html4 = makeTable(ionModes, urls4, numIonModes, 4);
	String html5 = makeTable(sampleNames, urls5, numSampleNames, 2);

	out.println( "<div id=\"statistics\">" );
	out.println( "<br>");
	out.println( "<li><b>Contributor</b></li>" );
	out.println( html1 );
	out.println( "<li><b>Instrument Type</b></li>" );
	out.println( html2 );
	out.println( "<li><b>MS Type</b></li>" );
	out.println( html3 );
	out.println( "<li><b>Ion Mode</b></li>" );
	out.println( html4 );
	out.println( "<li><b>Sample Name</b></li>" );
	out.println( html5 );
	out.println( "</div>" );
%>
</body>
</html>

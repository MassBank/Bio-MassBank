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
 * BasicSearch.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.tools.search;

import java.net.SocketTimeoutException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import javax.servlet.jsp.JspWriter;
import javax.servlet.http.HttpServletRequest;
import net.massbank.core.common.GetConfig;
import net.massbank.core.common.RecordSet;
import net.massbank.core.common.RecordSetList;
import net.massbank.tools.search.peak.PeakSearchInvoker;
import net.massbank.tools.search.peak.PeakSearchParameter;
import net.massbank.tools.search.quick.QuickSearchInvoker;
import net.massbank.tools.search.quick.QuickSearchParameter;
import net.massbank.tools.search.spectrum.SpectrumSearchInvoker;
import net.massbank.tools.search.spectrum.SpectrumSearchParameter;
import net.massbank.tools.search.spectrum.SpectrumSearchResult;


public class BasicSearch {
	public static final int REF_PEAK      = 1;
	public static final int REF_PEAK_DIFF = 2;
	public static final int REF_QUICK     = 3;
	public static final int REF_SPECTRUM  = 4;
	private JspWriter out = null;
	private int referrer  = 0;
	private String reqUrl = "";
	private Object searchParams = null;
	private Hashtable<String, Object> reqParams = null;
	private RecordSetList recSetList = null;
	private List<SpectrumSearchResult> ssResultList = null;

	/**
	 *
	 */
	public BasicSearch(HttpServletRequest req, JspWriter out) {
		this.reqUrl = req.getRequestURL().toString();
		this.out = out;

		String searchType = req.getParameter("searchType");
		if ( searchType == null ) {
			this.referrer = REF_QUICK;
		}
		else if ( searchType.equals("peak") ) {
			this.referrer = REF_PEAK;
		}
		else if ( searchType.equals("peak_diff") ) {
			this.referrer = REF_PEAK_DIFF;
		}
		else if ( searchType.equals("keyword") ) {
			this.referrer = REF_QUICK;
		}
		else if ( searchType.equals("spectrum") ) {
			this.referrer = REF_SPECTRUM;
		}

		this.reqParams = new Hashtable<String, Object>();
		Enumeration names = req.getParameterNames();
		while ( names.hasMoreElements() ) {
			String key = (String)names.nextElement();
			if ( !key.equals("inst") && !key.equals("ms") ) {
				String val = req.getParameter(key).trim();
				reqParams.put(key, val);
			}
			else {
				String[] vals = req.getParameterValues(key);
				reqParams.put(key, vals);
			}
		}

		String[] instTypes = (String[])reqParams.get("inst");
		if ( instTypes == null ) {
			instTypes = new String[]{"all"};
		}
		else {
			for ( int i = 0; i < instTypes.length; i++ ) {
				if ( instTypes[i].equals("all") ) {
					instTypes = new String[]{"all"};
					break;
				}
			}
		}

		String[] msTypes = (String[])reqParams.get("ms");
		if ( msTypes == null ) {
			msTypes = new String[]{"all"};
		}
		else {
			for ( int i = 0; i < msTypes.length; i++ ) {
				if ( msTypes[i].equals("all") ) {
					msTypes = new String[]{"all"};
					break;
				}
			}
		}

		String ion = (String)reqParams.get("ion");
		String siteNo = (String)reqParams.get("site_no");
		if ( ion == null )    { ion = ""; }
		if ( siteNo == null ) { siteNo = ""; }

		if ( this.referrer == REF_PEAK || this.referrer == REF_PEAK_DIFF ) {
			String op = (String)reqParams.get("op0");
			String tol = ((String)reqParams.get( "tol" )).replaceAll(" ", "").replaceAll("　", "");
			String inte = ((String)reqParams.get( "inte" )).replaceAll(" ", "").replaceAll("　", "");

			List mzs = new ArrayList();
			for ( int i = 0; i < 6; i++ ) {
				String mz = (String)reqParams.get( "mz"  + i );
				if ( mz == null ) {
					break;
				}
				mz = mz.replaceAll(" ", "").replaceAll("　", "").replaceAll("&#12288;", "");
				if ( mz.equals("") ) {
					continue;
				}
				mzs.add(mz);
			}
			PeakSearchParameter sp = new PeakSearchParameter();
			sp.setMzs((String[])mzs.toArray(new String[]{}));
			sp.setRelativeIntensity(inte);
			sp.setTolerance(tol);
			sp.setSearchType(searchType);
			sp.setSearchCondition(op);
			sp.setIonMode(ion);
			sp.setInstrumentTypes(instTypes);
			sp.setMsTypes(msTypes);
			sp.changeResultFormat();
			this.searchParams = sp;
		}
		else if ( this.referrer == REF_QUICK ) {
			String cname = (String)reqParams.get("compound_name");
			String emass = (String)reqParams.get("emass");
			String tolerance = (String)reqParams.get("tolerance");
			String formula = (String)reqParams.get("formula");
			String keyword = (String)reqParams.get("keyword");
			if ( cname == null )     { cname = ""; }
			if ( emass == null )     { emass = ""; }
			if ( tolerance == null ) { tolerance = ""; }
			if ( formula == null )   { formula = ""; }
			if ( keyword == null )   { keyword = ""; }
			QuickSearchParameter sp = new QuickSearchParameter();
			sp.setCompoundName(cname);
			sp.setExactMass(emass);
			sp.setTolerance(tolerance);
			sp.setFormula(formula);
			sp.setIonMode(ion);
			sp.setInstrumentTypes(instTypes);
			sp.setMsTypes(msTypes);
			sp.setSiteNo(siteNo);
			sp.setKeyword(keyword);
			sp.changeResultFormat();
			this.searchParams = sp;
		}
		else if ( this.referrer == REF_SPECTRUM ) {
			String qpeak = (String)reqParams.get("qpeak");
			String[] lines = qpeak.split("\n|;");
			List mzs = new ArrayList();
			List intes = new ArrayList();
			for ( String line: lines ) {
				String val = line.trim();
				if ( !val.equals("") ) {
					String val2 = val.replaceAll(" +", ",");
					val2 = val2.replaceAll("\t+", ",");
					String[] vals = val2.split(",");
					mzs.add(vals[0]);
					intes.add(vals[1]);
				}
			}
			String cutoff = (String)reqParams.get("cutoff");
			if ( cutoff == null ) { cutoff = ""; }
			SpectrumSearchParameter sp = new SpectrumSearchParameter();
			sp.setMzs((String[])mzs.toArray(new String[]{}));
			sp.setIntensities((String[])intes.toArray(new String[]{}));
			sp.setCutoff(cutoff);
			sp.setTolerance("0.3");
			sp.setUnitOfTolerance("unit");
			sp.setIonMode(ion);
			sp.setInstrumentTypes(instTypes);
			sp.setMsTypes(msTypes);
			this.searchParams = sp;
		}
	}

	/**
	 * execute
	 */
	public void execute() {
		if ( this.referrer == REF_PEAK || this.referrer == REF_PEAK_DIFF ) {
			PeakSearchInvoker inv = new PeakSearchInvoker(this.reqUrl, (PeakSearchParameter)this.searchParams);
			try { inv.invoke(); }
			catch (SocketTimeoutException se) {}
			this.recSetList = inv.getResultSets();
		}
		else if ( this.referrer == REF_QUICK ) {
			QuickSearchInvoker inv = new QuickSearchInvoker(this.reqUrl, (QuickSearchParameter)this.searchParams);
			try { inv.invoke(); }
			catch (SocketTimeoutException se) {}
			this.recSetList = inv.getResultSets();
		}
		else if ( this.referrer == REF_SPECTRUM ) {
			SpectrumSearchInvoker inv = new SpectrumSearchInvoker(this.reqUrl, (SpectrumSearchParameter)this.searchParams);
			try { inv.invoke(); }
			catch (SocketTimeoutException se) {}
			this.ssResultList = inv.getResults();
		}
	}

	/**
	 *
	 */
	public void outNoResultHtml() throws Exception {
		out.println( "<html>" );
		out.println( "<head>" );
		out.println( " <link rel=\"stylesheet\" type=\"text/css\" href=\"../css/Common.css\">" );
		out.println( " <title>Bio-MassBank | Database | Results</title>" );
		out.println( "</head>" );
		out.println( "<body class=\"msbkFont cursorDefault\">" );
		out.println( "<h1>Results</h1>" );
		out.println( "<iframe src=\"./menu.jsp\" width=\"860px\" height=\"30px\" frameborder=\"0\" marginwidth=\"0\" scrolling=\"no\"></iframe>" );
		out.println( "<hr size=\"1\">" );
		out.println( "<b>Search Parameters :</b><br>" );
		out.println( "<div class=\"divSpacer9px\"></div>" );
		out.println( "<hr size=\"1\">" );
		out.println( "<table width=\"800px\" cellpadding=\"0\" cellspacing=\"0\">" );
		out.println( " <tr>" );
		out.println( "  <td>" );
		out.println( "   <b>Results : <font color=\"green\">0 Hit.</font></b>" );
		out.println( "  </td>" );
		out.println( " </tr>" );
		out.println( "</table>" );
		out.println( "</form>" );
		out.println( "<hr size=\"1\">" );
		out.println( "<iframe src=\"../copyrightline.html\" width=\"800px\" height=\"20px\" frameborder=\"0\" marginwidth=\"0\" scrolling=\"no\"></iframe>" );
		out.println( "</body>" );
		out.println( "</html>" );
	}

	/**
	 *
	 */
	public void showResultTable(String queryString, int page, boolean isBioMassBank) throws Exception {
		DecimalFormat numFormat = new DecimalFormat("###,###,###");

		GetConfig conf = new GetConfig();
		String[] urls = conf.getSiteUrls();
		String[] dbNames = conf.getDbNames();
		if ( this.recSetList == null || this.recSetList.getListSize() == 0 ) {
			out.println( "0hit" );
			return;
		}

		final int num = 25;
		int start = (page - 1) * num;
		int end = page * num;
		int numCompound = this.recSetList.getListSize();
		if ( start > numCompound ) {
			return;
		}
		if ( end > numCompound ) {
			end = numCompound;
		}
		int lastPage = (int)Math.ceil((double)numCompound / num);

		String burl = "./SearchResult.jsp?" + queryString;
		String pageMenuHtml = "";
		if ( page != 1 ) {
			String url0 = burl + "&page=1";
			pageMenuHtml += "&nbsp;&nbsp;<a href=\"" + url0 + "\" taget=\"_self\">First</a>";
			String url1 = burl + "&page=" + (page - 1);
			pageMenuHtml += "&nbsp;&nbsp;<a href=\"" + url1 + "\" taget=\"_self\">Prev</a>";
		}
		else {
			pageMenuHtml = "First";
			pageMenuHtml += "&nbsp;&nbsp;Prev";
		}

		int pageStart = page - 5;
		if ( pageStart < 1 ) {
			pageStart = 1;
		}
		int pageEnd = pageStart + 9;
		if ( pageEnd > lastPage ) {
			pageEnd = lastPage;
		}
		for ( int i = pageStart; i <= pageEnd; i++ ) {
			if ( page == i ) {
				pageMenuHtml += "&nbsp;&nbsp;<b>" + String.valueOf(i) + "</b>";
			}
			else {
				String url2 = burl + "&page=" + String.valueOf(i);
				pageMenuHtml += "&nbsp;&nbsp;<a href=\"" + url2 + "\" taget=\"_self\">" + String.valueOf(i) + "</a>";
			}
		}

		if ( lastPage - page > 1 ) {
			String url3 = burl + "&page=" + (page + 1);
			pageMenuHtml += "&nbsp;&nbsp;<a href=\"" + url3 + "\">Next</a>";
		}
		else {
			pageMenuHtml += "&nbsp;&nbsp;<a>Next</a>";
		}

		if ( lastPage > 1 && page != lastPage ) {
			String url4 = burl + "&page=" + lastPage;
			pageMenuHtml += "&nbsp;&nbsp;<a href=\"" + url4 + "\">Last</a>";
		}
		else {
			pageMenuHtml += "&nbsp;&nbsp;Last";
		}
		pageMenuHtml += "&nbsp;&nbsp;&nbsp;(&nbsp;Total&nbsp;" + lastPage + "&nbsp;Page&nbsp;)";

		StringBuilder sbNameList    = new StringBuilder("");
		StringBuilder sbFomulaList  = new StringBuilder("");
		StringBuilder sbEmassList   = new StringBuilder("");
		StringBuilder sbImgUrlList  = new StringBuilder("");
		List<StringBuilder> allRecordlist = new ArrayList();
		int numHit = 0;
		int numDisplay1 = 1;
		int numDisplay2 = 0;
		for ( int i = 0; i < numCompound; i++ ) {
			RecordSet recSet = this.recSetList.getRecordSet(i);
			int n = recSet.getRecordListSize();
			numHit += n;
			if ( i < start ) {
				numDisplay1 += n;
			}
			if ( i < end ) {
				numDisplay2 += n;
			}
		}

		for ( int i = start; i < end; i++ ) {
			RecordSet recSet = this.recSetList.getRecordSet(i);
			String cno     = recSet.getCompoundNo();
			String cname   = recSet.getCompoundName();
			String formula = recSet.getFormula();
			String emass   = recSet.getExactMass();
			int siteNo  = Integer.parseInt(recSet.getSiteNo());
			sbNameList.append("<input type=\"hidden\" value=\"" + cname + "\">\n");
			sbFomulaList.append("<input type=\"hidden\" value=\"" + formula + "\">\n");
			sbEmassList.append("<input type=\"hidden\" value=\"" + emass + "\">\n");
			String url = urls[siteNo] + "structure_img/" + dbNames[siteNo] + "/S/" + cno + ".gif";
			sbImgUrlList.append("<input type=\"hidden\" value=\"" + url + "\">\n");

			StringBuilder sbRecordList  = new StringBuilder("");
			for ( int j = 0; j < recSet.getRecordListSize(); j++ ) {
				String title = "";
				if ( isBioMassBank ) {
					String rtitle = recSet.getRecordTitle(j);
					String[] items = rtitle.split(";");
					for ( int k = 1; k < items.length; k++ ) {
						title += items[k] + ";";
					}
				}
				else {
					title = recSet.getRecordTitle(j);
				}
				String id = recSet.getId(j);
				String val = title + "\t" + id;
				sbRecordList.append("<input type=\"hidden\" value=\"" + val + "\">\n");
			}
			allRecordlist.add(sbRecordList);
		}

		out.println("<div style=\"width:470px;float:left\">");
		out.println("<b>Results&nbsp;:&nbsp;<font color=\"green\">" + numFormat.format(numHit) + "&nbsp;Hit.</font>&nbsp;&nbsp;<font size=\"2\" color=\"green\">(&nbsp;" + numFormat.format(numDisplay1) + "&nbsp;-&nbsp;" + numFormat.format(numDisplay2) + "&nbsp;Displayed&nbsp;)</font></b>");
		out.println("</div>");
		out.println("<div align=\"right\" style=\"width:470px;float:left;\">");
		out.println("<input type=\"button\" id=\"open_all\" value=\"Open All Tree\" onClick=\"openAllTree()\">");
		out.println("<input type=\"button\" id=\"open_all\" value=\"Multiple Display\" onClick=\"showSpectra()\">");
		out.println("<input type=\"button\" id=\"open_all\" value=\"Spectrum Search\" onClick=\"searchSpectrum()\">");
		out.println("</div>");
		out.println("<br><br>");
		out.println(pageMenuHtml);
		out.println("<br><br>");
		out.println("<table id=\"result_list\"></table>");
		out.println("<div id=\"pager\"></div>");

		out.println("<div id=\"compound_name_list\">");
		out.print( sbNameList.toString() );
		out.println("</div>");
		out.println("<div id=\"formula_list\">");
		out.print( sbFomulaList.toString() );
		out.println("</div>");
		out.println("<div id=\"emass_list\">");
		out.print( sbEmassList.toString() );
		out.println("</div>");
		out.println("<div id=\"img_url_list\">");
		out.print( sbImgUrlList.toString() );
		out.println("</div>");

		for ( int i = 0; i < allRecordlist.size(); i++ ) {
			out.println("<div id=\"record_list_" + String.valueOf(i+1) + "\">");
			out.print( allRecordlist.get(i).toString() );
			out.println("</div>");
		}
		out.println(pageMenuHtml);
	}



	/**
	 *
	 */
	public void showQPeakResultTable(String queryString, int page) throws Exception {
		GetConfig conf = new GetConfig();
		String[] urls = conf.getSiteUrls();
		String[] dbNames = conf.getDbNames();
		if ( this.ssResultList == null || this.ssResultList.size() == 0 ) {
			out.println( "0hit" );
			return;
		}

		final int num = 25;
		int start = (page - 1) * num;
		int end = page * num;
		int hit = this.ssResultList.size();
		if ( start > hit ) {
			return;
		}
		if ( end > hit ) {
			end = hit;
		}
		int lastPage = (int)Math.ceil((double)hit / num);

		String burl = "./SearchResult.jsp?" + queryString;
		String pageMenuHtml = "";
		if ( page != 1 ) {
			String url0 = burl + "&page=1";
			pageMenuHtml += "&nbsp;&nbsp;<a href=\"" + url0 + "\" taget=\"_self\">First</a>";
			String url1 = burl + "&page=" + (page - 1);
			pageMenuHtml += "&nbsp;&nbsp;<a href=\"" + url1 + "\" taget=\"_self\">Prev</a>";
		}
		else {
			pageMenuHtml = "First";
			pageMenuHtml += "&nbsp;&nbsp;Prev";
		}

		int pageStart = page - 5;
		if ( pageStart < 1 ) {
			pageStart = 1;
		}
		int pageEnd = pageStart + 9;
		if ( pageEnd > lastPage ) {
			pageEnd = lastPage;
		}
		for ( int i = pageStart; i <= pageEnd; i++ ) {
			if ( page == i ) {
				pageMenuHtml += "&nbsp;&nbsp;<b>" + String.valueOf(i) + "</b>";
			}
			else {
				String url2 = burl + "&page=" + String.valueOf(i);
				pageMenuHtml += "&nbsp;&nbsp;<a href=\"" + url2 + "\" taget=\"_self\">" + String.valueOf(i) + "</a>";
			}
		}

		if ( lastPage - page > 1 ) {
			String url3 = burl + "&page=" + (page + 1);
			pageMenuHtml += "&nbsp;&nbsp;<a href=\"" + url3 + "\">Next</a>";
		}
		else {
			pageMenuHtml += "&nbsp;&nbsp;<a>Next</a>";
		}

		if ( lastPage > 1 && page != lastPage ) {
			String url4 = burl + "&page=" + lastPage;
			pageMenuHtml += "&nbsp;&nbsp;<a href=\"" + url4 + "\">Last</a>";
		}
		else {
			pageMenuHtml += "&nbsp;&nbsp;Last";
		}
		pageMenuHtml += "&nbsp;&nbsp;&nbsp;(&nbsp;Total&nbsp;" + lastPage + "&nbsp;Page&nbsp;)";

		StringBuilder sbIdList      = new StringBuilder("");
		StringBuilder sbTitleList   = new StringBuilder("");
		StringBuilder sbFomulaList  = new StringBuilder("");
		StringBuilder sbEmassList   = new StringBuilder("");
		StringBuilder sbImgUrlList  = new StringBuilder("");
		StringBuilder sbScoreList   = new StringBuilder("");
		List<StringBuilder> allRecordlist = new ArrayList();
		for ( int i = start; i < end; i++ ) {
			SpectrumSearchResult result = this.ssResultList.get(i);
			String cno     = result.getCompoundNo();
			String id      = result.getId();
			String title   = result.getRecordTitle();
			String formula = result.getFormula();
			String emass   = result.getExactMass();
			String hitScore = result.getScore();
			String score = "";
			int pos = hitScore.indexOf(".");
			if ( pos > 0 ) {
				score = "0" + hitScore.substring(pos);
			}
			else {
				score = "0";
			}
			int siteNo = Integer.parseInt(result.getSiteNo());
			sbIdList.append("<input type=\"hidden\" value=\"" + id + "\">\n");
			sbTitleList.append("<input type=\"hidden\" value=\"" + title + "\">\n");
			sbFomulaList.append("<input type=\"hidden\" value=\"" + formula + "\">\n");
			sbEmassList.append("<input type=\"hidden\" value=\"" + emass + "\">\n");
			String url = urls[siteNo] + "structure_img/" + dbNames[siteNo] + "/S/" + cno + ".gif";
			sbImgUrlList.append("<input type=\"hidden\" value=\"" + url + "\">\n");
			sbScoreList.append("<input type=\"hidden\" value=\"" + score + "\">\n");
		}
		out.println("<div style=\"width:470px;float:left\">");
		out.println("<b>Results&nbsp;:&nbsp;<font color=\"green\">" + hit + "&nbsp;Hit.</font>&nbsp;&nbsp;<font size=\"2\" color=\"green\">(&nbsp;" + (start+1) + "&nbsp;-&nbsp;" + end + "&nbsp;Displayed&nbsp;)</font></b>");
		out.println("</div>");
		out.println("<div align=\"right\" style=\"width:470px;float:left\">");
		out.println("<input type=\"button\" id=\"open_all\" value=\"Multiple Display\" onClick=\"showSpectra()\">");
		out.println("<input type=\"button\" id=\"open_all\" value=\"Spectrum Search\" onClick=\"searchSpectrum()\">");
		out.println("</div>");
		out.println("<br><br>");
		out.println(pageMenuHtml);
		out.println("<br><br>");
		out.println("<table id=\"result_list\"></table>");
		out.println("<div id=\"pager\"></div>");

		out.println("<div id=\"id_list\">");
		out.print( sbIdList.toString() );
		out.println("</div>");
		out.println("<div id=\"title_list\">");
		out.print( sbTitleList.toString() );
		out.println("</div>");
		out.println("<div id=\"formula_list\">");
		out.print( sbFomulaList.toString() );
		out.println("</div>");
		out.println("<div id=\"emass_list\">");
		out.print( sbEmassList.toString() );
		out.println("</div>");
		out.println("<div id=\"img_url_list\">");
		out.print( sbImgUrlList.toString() );
		out.println("</div>");
		out.println("<div id=\"score_list\">");
		out.print( sbScoreList.toString() );
		out.println("</div>");
		out.println(pageMenuHtml);
	}

	/**
	 *
	 */
	public void showSearchParam() throws Exception {
		out.println( "<div id=\"search_parameter\">" );
		out.println( "<b>Search Parameters</b><br>" );
		if ( this.referrer == REF_PEAK || this.referrer == REF_PEAK_DIFF ) {
			showPeakSearchParam();
			out.println("<br>");
		}
		else if ( this.referrer == REF_QUICK ) {
			showQuickSearchParam();
		}
		showOtherParam();
		out.println( "</div>" );
	}

	/**
	 *
	 */
	private void showPeakSearchParam() throws Exception {
		PeakSearchParameter sp = (PeakSearchParameter)this.searchParams;
		String[] mzs = sp.getMzs();
		String inte = sp.getRelativeIntensity();
		String tol = sp.getTolerance();
		String cond = sp.getSearchCondition();

		String mzStrings = "";
		for ( int i = 0; i < mzs.length; i++ ) {
			mzStrings += mzs[i];
			if ( i < mzs.length - 1 ) {
				mzStrings += "&nbsp;" + cond + "&nbsp";
			}
		}
		String stype = sp.getSearchType();
		String paramName = "<i>m/z</i>";
		if ( stype.equals("peak_diff") ) {
			paramName += "&nbsp;diff";
		}
		out.println( "&nbsp;&nbsp;" + paramName + ":&nbsp;<b>[" + mzStrings + "]</b>&nbsp;&nbsp;" );
		out.println( "Rel.Intensity:<b>" + inte + "</b>&nbsp;&nbsp;" );
		out.println( "Tolerance(unit):<b>" + tol + "</b>&nbsp;&nbsp;" );
	}

	/**
	 *
	 */
	private void showQuickSearchParam() throws Exception {
		QuickSearchParameter sp = (QuickSearchParameter)this.searchParams;
		String cname     = sp.getCompoundName();
		String emass     = sp.getExactMass();
		String tolerance = sp.getTolerance();
		String formula   = sp.getFormula();
		if ( cname != null && !cname.equals("") ) {
			out.println( "&nbsp;&nbsp;Compound Name:&nbsp;<b>" + cname + "</b>");
		}
		if ( emass != null && !emass.equals("") ) {
			out.println( "&nbsp;&nbsp;Exact Mass:&nbsp;<b>" + emass + "</b>(Tolerance:" + tolerance + ")" );
		}
		if ( formula != null && !formula.equals("") ) {
			out.println( "&nbsp;&nbsp;Formula:&nbsp;<b>" + formula + "</b>" );
		}
	}

	/**
	 *
	 */
	private void showOtherParam() throws Exception {
		String ion = "";
		String[] instTypes = null;
		String[] msTypes = null;
		if ( this.referrer == REF_PEAK || this.referrer == REF_PEAK_DIFF ) {
			PeakSearchParameter sp = (PeakSearchParameter)this.searchParams;
			ion = sp.getIonMode();
			instTypes = sp.getInstrumentTypes();
			msTypes = sp.getMsTypes();
		}
		else if ( this.referrer == REF_QUICK ) {
			QuickSearchParameter sp = (QuickSearchParameter)this.searchParams;
			ion = sp.getIonMode();
			instTypes = sp.getInstrumentTypes();
			msTypes = sp.getMsTypes();
		}
		else if ( this.referrer == REF_SPECTRUM ) {
			SpectrumSearchParameter sp = (SpectrumSearchParameter)this.searchParams;
			ion = sp.getIonMode();
			instTypes = sp.getInstrumentTypes();
			msTypes = sp.getMsTypes();
		}

		String ionMode = "Both";
		if ( ion.equals("1") ) {
			ionMode = "Positive";
		}
		else if ( ion.equals("-1") ) {
			ionMode = "Negative";
		}
		String inst = "";
		for ( int i = 0; i < instTypes.length; i++ ) {
			if ( i > 0 ) {
				inst += ", ";
			}
			inst += instTypes[i];
		}
		String ms = "";
		for ( int i = 0; i < msTypes.length; i++ ) {
			if ( i > 0 ) {
				ms += ", ";
			}
			ms += msTypes[i];
		}
		out.println( "&nbsp;&nbsp;Instrument Type:&nbsp;<b>" + inst + "</b><br>");
		out.println( "&nbsp;&nbsp;MS Type:&nbsp;<b>" + ms + "</b><br>");
		out.println( "&nbsp;&nbsp;Ion Mode:&nbsp;<b>" + ionMode + "</b>" );
	}


	/**
	 *
	 */
	public void showRecordIndexType(String indexType, String indexKey) throws Exception {
	}

	/*
	 *
	 */
	private String getParamValue(Hashtable<String, Object> reqParams, String key) {
		return ((String)reqParams.get("key") != null) ? (String)reqParams.get("key") : "";
	}
}

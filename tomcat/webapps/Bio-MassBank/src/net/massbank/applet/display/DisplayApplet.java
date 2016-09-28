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
 * DisplayApplet.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.applet.display;

import java.awt.Point;
import java.awt.Dimension;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.applet.AppletContext;
import javax.swing.Timer;
import javax.swing.BoxLayout;
import javax.swing.JApplet;
import javax.swing.JOptionPane;
import javax.swing.JToggleButton;
import net.massbank.applet.common.Peak;
import net.massbank.core.get.record.GetRecordInfoInvoker;
import net.massbank.core.get.record.GetRecordInfoInvoker;
import net.massbank.core.common.RecordInfo;
import net.massbank.core.common.CoreUtil;
import net.massbank.tools.search.peak.PeakSearchParameter;


/**
 * DisplayApplet
 */
public class DisplayApplet extends JApplet
{
	private static final long serialVersionUID = 1L;
	public long lastClickedTime = 0;
	public Timer timer = null;
	public int massRangeMax = 0;
	public boolean underDrag = false;
	public Point fromPos = null;
	public Point toPos = null;
	public double xscale = 0;
	public double yscale = 0;
	public double massStart = 0;
	public double massRange = 0;
	public int intensityRange = PlotPanel.INTENSITY_MAX;
	public List<Peak> peakList = new ArrayList();;
	public List<Integer> precursorList = new ArrayList();
	public List<JToggleButton> showAllBtnList = new ArrayList();
	public List<JToggleButton> massDiffBtnList = new ArrayList();
	public static AppletContext appContext = null;
	public static String baseUrl = "";
	public static int hitNum = 0;
	public static String[] hitDiffVals = null;
	public static ArrayList<Double>[][] hitPeaks1 = null;
	public static ArrayList<Double>[][] hitPeaks2 = null;
	public static String searchType = "";
	public static boolean showAll = false;
	public static JToggleButton[][] diffBtnList = null;

	/**
	 * 
	 */
	public void init() {
		this.appContext = getAppletContext();
		String codeBase = getCodeBase().toString();
		this.baseUrl = CoreUtil.getBaseUrl(codeBase).replace("mbtools/", "");

		setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));
		boolean isMulti = false;
		String multi = getParameter("is_multi");
		if ( multi != null && multi.toLowerCase().equals("true") ) {
			isMulti = true;
		}

		String pid = getParameter("id");
		String[] ids = pid.split(",");

		ArrayList<String> qmzs = new ArrayList();
		String op = "and";
		String tol = "0";
		String inte = "5";
		PeakSearchParameter sp = new PeakSearchParameter();

		String val1 = getParameter("searchType");
		if ( val1 != null && (val1.equals("peak") || val1.equals("peak_diff")) ) {
			this.searchType = val1;
			String val2 = getParameter("op0");
			if ( val2 != null )  {
				op = val2;
			}
			for ( int i = 0; i < 5; i++ ) {
				String val3 = getParameter( "mz" + String.valueOf(i) );
				if ( val3 != null )  {
					double num3 = CoreUtil.getNumber(val3);
					if ( num3 != 0 ) {
						qmzs.add(val3);
					}
				}
			}

			String val4 = getParameter("tol");
			if ( val4 != null )  {
				double num4 = CoreUtil.getNumber(val4);
				if ( num4 != 0 ) {
					tol = val4;
				}
			}

			String val5 = getParameter("inte");
			if ( val5 != null )  {
				double num5 = CoreUtil.getNumber(val5);
				if ( num5 != 0 ) {
					inte = val5;
				}
			}

			sp.setMzs((String[])qmzs.toArray(new String[]{}));
			sp.setRelativeIntensity(inte);
			sp.setTolerance(tol);
			sp.setSearchType(searchType);
			sp.setSearchCondition(op);

			this.hitPeaks1 = new ArrayList[ids.length][qmzs.size()];
			this.hitPeaks2 = new ArrayList[ids.length][qmzs.size()];
			this.hitDiffVals = qmzs.toArray(new String[]{});
			this.diffBtnList = new JToggleButton[ids.length][this.hitDiffVals.length];
		}

		GetRecordInfoInvoker inv = new GetRecordInfoInvoker(this.baseUrl, ids);
		try {
			inv.invoke();
		}
		catch ( SocketTimeoutException se ) {
			JOptionPane.showMessageDialog(null, "Server error: Timeout", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		Map<String, RecordInfo> infoList = inv.getResults();
		for ( int i = 0; i < ids.length; i++ ) {
			RecordInfo info = infoList.get(ids[i]);
			ParentPanel parentPane = new ParentPanel(info, sp, this, isMulti);
			add(parentPane);

			if ( ids.length > 1 ) {
				add(ParentPanel.createSeparatorLine());
			}
		}
		initPlotPanel();
		repaint();
	}

	/*
	 *
	 */
	public void initPlotPanel() {
		this.massRange = -1;
		for ( int i = 0; i < this.peakList.size(); i++ ) {
			Peak peak = this.peakList.get(i);
			int precursor = this.precursorList.get(i);
			double max = peak.compMaxMzPrecusor(precursor);
			if ( this.massRange < max ) {
				this.massRange = max;
			}
		}

		if ( this.massRange != 0.0 && (this.massRange % 100.0) == 0.0 ) {
			this.massRange += 100.0;
		}
		this.massRange = (double)Math.ceil(this.massRange / 100.0) * 100.0;
		this.massRangeMax = (int)this.massRange;
		this.massStart = 0;
		this.intensityRange = PlotPanel.INTENSITY_MAX;
	}
}

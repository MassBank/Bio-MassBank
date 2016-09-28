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
 * SearchApplet.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.applet.search;

import java.applet.AppletContext;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import net.massbank.core.common.GetConfig;
import net.massbank.core.common.CoreUtil;
import net.massbank.core.common.QueryFileUtil;
import net.massbank.core.common.RecordInfo;
import net.massbank.core.common.RecordInfoList;
import net.massbank.core.get.record.GetRecordInfoInvoker;
import net.massbank.core.get.instrument.GetInstrumentInvoker;
import net.massbank.core.get.instrument.GetInstrumentResults;
import net.massbank.core.get.record.GetRecordTitleInvoker;
import net.massbank.tools.search.spectrum.SpectrumSearchParameter;
import net.massbank.tools.search.spectrum.SpectrumSearchInvoker;
import net.massbank.tools.search.spectrum.SpectrumSearchResult;
import net.massbank.applet.common.Peak;


/**
 * SearchPage クラス
 */
@SuppressWarnings("serial")
public class SearchApplet extends JApplet {

	public static String baseUrl = "";
	private static String toolsUrl = "";
	private static int PRECURSOR = -1;
	private static float TOLERANCE = 0.3f;
	public static int CUTOFF_THRESHOLD = 5;
	private static final int LEFT_PANEL_WIDTH = 430;

	private static final int TAB_ORDER_DB = 0;
	private static final int TAB_ORDER_FILE = 1;
	private static final int TAB_RESULT_DB = 0;
	private static final int TAB_VIEW_COMPARE = 0;
	private static final int TAB_VIEW_PACKAGE = 1;

	public static final String COL_LABEL_NAME = "Record Title";
	public static final String COL_LABEL_SCORE = "Score";
	public static final String COL_LABEL_HIT = "Hit";
	public static final String COL_LABEL_ID = "ID";
	public static final String COL_LABEL_ION = "Ion";
	public static final String COL_LABEL_CONTRIBUTOR = "Contributor";
	public static final String COL_LABEL_NO = "No.";
	public static final int PEAK_SEARCH_PARAM_NUM = 6;

	public static final String TABLE_QUERY_FILE = "QueryFile";
	public static final String TABLE_QUERY_DB = "QueryDb";
	public static final String TABLE_RESULT = "Result";

	private TableSorter fileSorter = null;
	private TableSorter querySorter = null;
	private TableSorter resultSorter = null;
	private JTable queryFileTable = null;
	private JTable queryDbTable = null;
	private JTable resultTable = null;
	private PeakPanel queryPlot = new PeakPanel(false, this);
	private PeakPanel resultPlot = new PeakPanel(false, this);
	private PeakPanel compPlot = new PeakPanel(true, this);
	private JTabbedPane queryTabPane = new JTabbedPane();
	private JTabbedPane resultTabPane = new JTabbedPane();
	private JTabbedPane viewTabPane = new JTabbedPane();
	private JScrollPane queryFilePane = null;
	private JScrollPane resultPane = null;
	private JScrollPane queryDbPane = null;
	private JButton btnAll = new JButton("Get all data");

	private String saveSearchName = "";

	private JButton etcPropertyButton = new JButton("Search Parameter Setting");

	private boolean isRecActu;
	private boolean isRecInteg;

	private JRadioButton tolUnit1 = new JRadioButton("unit", true);
	private JRadioButton tolUnit2 = new JRadioButton("ppm");

	private Map<String, String[]> instGroup;
	private String[] msTypes;
	private LinkedHashMap<String, JCheckBox> instChecks;
	private HashMap<String, Boolean> isInstChecks;
	private LinkedHashMap<String, JCheckBox> msChecks;
	private HashMap<String, Boolean> isMsChecks;
	private LinkedHashMap<String, JRadioButton> ionRadio;
	private HashMap<String, Boolean> isIonRadio;

	private boolean isSubWindow = false;
	private JLabel hitLabel = new JLabel(" ");
	private ArrayList<String[]> nameList = new ArrayList<String[]>();
	private ArrayList nameListAll = new ArrayList();

	public static String[] siteNameList;
	private JPanel parentPanel2 = null;

	private static final Cursor waitCursor = new Cursor(Cursor.WAIT_CURSOR);
	public static AppletContext context = null;
	public static int initAppletWidth = 0;
	public static int initAppletHight = 0;
	private static final int MAX_DISPLAY_NUM = 30;
	private static final String COOKIE_TOL = "TOL";
	private static final String COOKIE_CUTOFF = "CUTOFF";
	private static final String COOKIE_INST = "INST";
	private static final String COOKIE_MS = "MS";
	private static final String COOKIE_ION = "ION";

	private CookieManager cm = null;
	private ProgressDialog dlg = null;
	private SpectrumSearchParameter ssp = null;
	private String[] queryFilePeaks = null;
	private String queryPeakString = "";
	public boolean existRecord = true;

	/**
	 *
	 */
	public void init() {
		context = getAppletContext();
		initAppletWidth = getWidth();
		initAppletHight = getHeight();
		String codeBase = getCodeBase().toString();
		this.baseUrl = CoreUtil.getBaseUrl(codeBase).replace("mbtools/", "");
		this.toolsUrl = CoreUtil.getBaseUrl(codeBase, 2);
		GetConfig conf = new GetConfig(this.baseUrl);
		this.siteNameList = conf.getSiteNames();

		cm = new CookieManager(this, "SerchApplet", 30, conf.isCookie());
		initTolInfo();
		initCutoffInfo();
		GetInstrumentInvoker inv = new GetInstrumentInvoker(this.baseUrl);
		try {
			inv.invoke();
		}
		catch ( SocketTimeoutException se ) {
			JOptionPane.showMessageDialog(null, "Server error(1): Timeout", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		GetInstrumentResults results = inv.getResults();
		if ( results == null ) {
			this.existRecord = false;
		}
		else {
			this.instGroup = results.getInstTypeGroups();
			initInstInfo();
			this.msTypes = results.getMsTypes();
			initMsInfo();
			initIonInfo();
		}

		createWindow();
		this.dlg = new ProgressDialog(getFrame());
		if (getParameter("file") != null) {
			loadQueryFile(getParameter("file"));
		}

		String qid = getParameter("qid");
		if ( qid != null && qid != "") {
			DefaultTableModel dm = (DefaultTableModel) querySorter.getTableModel();
			dm.setRowCount(0);
			String[] qids = qid.split(",");
			String baseUrl = getCodeBase().toString().replace("mbtools/", "");
			GetRecordInfoInvoker inv1 = new GetRecordInfoInvoker(baseUrl, qids);
			try {
				inv1.invoke();
			}
			catch ( SocketTimeoutException se ) {
				JOptionPane.showMessageDialog(null, "Server error: Timeout", "Error", JOptionPane.ERROR_MESSAGE);
			}
			Map<String, RecordInfo> infoList = inv1.getResults();
			for ( int i = 0; i < qids.length; i++ ) {
				String id = qids[i];
				RecordInfo info = infoList.get(id);
				String title = info.getRecordTitle();
				String siteNo = info.getSiteNo();
				String site = siteNameList[Integer.parseInt(siteNo)];
				String[] idNameSite = new String[] { id, title, siteNo };
				nameList.add(idNameSite);
				String[] idNameSite2 = new String[] { id, title, site, String.valueOf(i + 1) };
				dm.addRow(idNameSite2);
			}
		}
	}

	/**
	 *
	 */
	public void setAllPlotAreaRange() {
		queryPlot.setIntensityRange(PeakPanel.INTENSITY_MAX);
		compPlot.setIntensityRange(PeakPanel.INTENSITY_MAX);
		resultPlot.setIntensityRange(PeakPanel.INTENSITY_MAX);
		Peak qPeak = queryPlot.getPeaks(0);
		Peak rPeak = resultPlot.getPeaks(0);
		if (qPeak == null && rPeak == null)
			return;
		double qMax = 0d;
		double rMax = 0d;
		if (qPeak != null)
			qMax = qPeak.compMaxMzPrecusor(queryPlot.getPrecursor());
		if (rPeak != null)
			rMax = rPeak.compMaxMzPrecusor(resultPlot.getPrecursor());
		if (qMax > rMax) {
			queryPlot.setPeaks(null, -1);
			setAllPlotAreaRange(queryPlot);
		} else {
			resultPlot.setPeaks(null, -1);
			setAllPlotAreaRange(resultPlot);
		}
	}

	/**
	 *
	 */
	public void setAllPlotAreaRange(PeakPanel panel) {
		if (panel == queryPlot) {
			compPlot.setMass(queryPlot.getMassStart(),
					queryPlot.getMassRange(), queryPlot.getIntensityRange());
			resultPlot.setMass(queryPlot.getMassStart(), queryPlot.getMassRange(),
					queryPlot.getIntensityRange());
		}
		else if (panel == compPlot) {
			queryPlot.setMass(compPlot.getMassStart(), compPlot.getMassRange(),
					compPlot.getIntensityRange());
			resultPlot.setMass(compPlot.getMassStart(), compPlot.getMassRange(),
					compPlot.getIntensityRange());
		}
		else if (panel == resultPlot) {
			queryPlot.setMass(resultPlot.getMassStart(), resultPlot.getMassRange(),
					resultPlot.getIntensityRange());
			compPlot.setMass(resultPlot.getMassStart(), resultPlot.getMassRange(),
					resultPlot.getIntensityRange());
		}
	}

	/**
	 *
	 */
	public int getMaxIntensity(double start, double end) {
		Peak qPaek = queryPlot.getPeaks(0);
		Peak dPeak = resultPlot.getPeaks(0);
		int qm = 0;
		int dm = 0;
		if (qPaek != null)
			qm = qPaek.getMaxIntensity(start, end);
		if (dPeak != null)
			dm = dPeak.getMaxIntensity(start, end);
		return Math.max(qm, dm);
	}

	/**
	 *
	 */
	private void createWindow() {
		ToolTipManager ttm = ToolTipManager.sharedInstance();
		ttm.setInitialDelay(50);
		ttm.setDismissDelay(8000);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		Border border = BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),
				new EmptyBorder(1, 1, 1, 1));
		mainPanel.setBorder(border);

		DefaultTableModel fileDm = new DefaultTableModel();
		fileSorter = new TableSorter(fileDm, TABLE_QUERY_FILE);
		queryFileTable = new JTable(fileSorter) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		queryFileTable.addMouseListener(new TblMouseListener());
		fileSorter.setTableHeader(queryFileTable.getTableHeader());
		queryFileTable.setRowSelectionAllowed(true);
		queryFileTable.setColumnSelectionAllowed(false);
		queryFileTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		String[] col = { COL_LABEL_NO, COL_LABEL_NAME, COL_LABEL_ID };
		((DefaultTableModel) fileSorter.getTableModel()).setColumnIdentifiers(col);
		(queryFileTable.getColumn(queryFileTable.getColumnName(0))).setPreferredWidth(44);
		(queryFileTable.getColumn(queryFileTable.getColumnName(1))).setPreferredWidth(LEFT_PANEL_WIDTH - 44);
		(queryFileTable.getColumn(queryFileTable.getColumnName(2))).setPreferredWidth(70);

		ListSelectionModel lm = queryFileTable.getSelectionModel();
		lm.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lm.addListSelectionListener(new LmFileListener());
		queryFilePane = new JScrollPane(queryFileTable);
		queryFilePane.addMouseListener(new PaneMouseListener());
		queryFilePane.setPreferredSize(new Dimension(300, 300));

		DefaultTableModel resultDm = new DefaultTableModel();
		resultSorter = new TableSorter(resultDm, TABLE_RESULT);
		resultTable = new JTable(resultSorter) {
			@Override
			public String getToolTipText(MouseEvent me) {
				Point pt = me.getPoint();
				int row = rowAtPoint(pt);
				if (row < 0) {
					return null;
				} else {
					int nameCol = getColumnModel().getColumnIndex(COL_LABEL_NAME);
					return "<html><body bgcolor=\"#FFD700\"><b>&nbsp;"
							+ getValueAt(row, nameCol) + "&nbsp;</b></body></html>";
				}
			}
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		resultTable.addMouseListener(new TblMouseListener());
		resultSorter.setTableHeader(resultTable.getTableHeader());

		JPanel dbPanel = new JPanel();
		dbPanel.setLayout(new BorderLayout());
		resultPane = new JScrollPane(resultTable);
		resultPane.addMouseListener(new PaneMouseListener());

		resultTable.setRowSelectionAllowed(true);
		resultTable.setColumnSelectionAllowed(false);
		resultTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		String[] col2 = { COL_LABEL_NAME, COL_LABEL_SCORE, COL_LABEL_HIT,
				COL_LABEL_ID, COL_LABEL_ION, COL_LABEL_CONTRIBUTOR, COL_LABEL_NO };

		resultDm.setColumnIdentifiers(col2);
		(resultTable.getColumn(resultTable.getColumnName(0)))
				.setPreferredWidth(LEFT_PANEL_WIDTH - 180);
		(resultTable.getColumn(resultTable.getColumnName(1))).setPreferredWidth(70);
		(resultTable.getColumn(resultTable.getColumnName(2))).setPreferredWidth(20);
		(resultTable.getColumn(resultTable.getColumnName(3))).setPreferredWidth(70);
		(resultTable.getColumn(resultTable.getColumnName(4))).setPreferredWidth(20);
		(resultTable.getColumn(resultTable.getColumnName(5))).setPreferredWidth(70);
		(resultTable.getColumn(resultTable.getColumnName(6))).setPreferredWidth(50);

		ListSelectionModel lm2 = resultTable.getSelectionModel();
		lm2.addListSelectionListener(new LmResultListener());

		resultPane.setPreferredSize(new Dimension(LEFT_PANEL_WIDTH, 200));
		dbPanel.add(resultPane, BorderLayout.CENTER);

		DefaultTableModel dbDm = new DefaultTableModel();
		querySorter = new TableSorter(dbDm, TABLE_QUERY_DB);
		queryDbTable = new JTable(querySorter) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		queryDbTable.addMouseListener(new TblMouseListener());
		querySorter.setTableHeader(queryDbTable.getTableHeader());
		queryDbPane = new JScrollPane(queryDbTable);
		queryDbPane.addMouseListener(new PaneMouseListener());

		int h = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		queryDbPane.setPreferredSize(new Dimension(LEFT_PANEL_WIDTH, h));
		queryDbTable.setRowSelectionAllowed(true);
		queryDbTable.setColumnSelectionAllowed(false);
		queryDbTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		String[] col3 = { COL_LABEL_ID, COL_LABEL_NAME, COL_LABEL_CONTRIBUTOR, COL_LABEL_NO };
		DefaultTableModel model = (DefaultTableModel) querySorter.getTableModel();
		model.setColumnIdentifiers(col3);

		queryDbTable.getColumn(queryDbTable.getColumnName(0))
				.setPreferredWidth(70);
		queryDbTable.getColumn(queryDbTable.getColumnName(1))
				.setPreferredWidth(LEFT_PANEL_WIDTH - 70);
		queryDbTable.getColumn(queryDbTable.getColumnName(2))
				.setPreferredWidth(70);
		queryDbTable.getColumn(queryDbTable.getColumnName(3))
				.setPreferredWidth(50);

		ListSelectionModel lm3 = queryDbTable.getSelectionModel();
		lm3.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lm3.addListSelectionListener(new LmQueryDbListener());

		JPanel btnPanel = new JPanel();
		btnAll.addActionListener(new BtnAllListener());
		btnPanel.add(btnAll);

		parentPanel2 = new JPanel();
		parentPanel2.setLayout(new BoxLayout(parentPanel2, BoxLayout.PAGE_AXIS));
		parentPanel2.add(btnPanel);
		parentPanel2.add(queryDbPane);

		JPanel paramPanel = new JPanel();
		paramPanel.add(etcPropertyButton);
		etcPropertyButton.setMargin(new Insets(0, 10, 0, 10));
		etcPropertyButton.addActionListener(new ActionListener() {
			private ParameterSetWindow ps = null;
			public void actionPerformed(ActionEvent e) {
				if ( existRecord ) {
					if (!isSubWindow) {
						ps = new ParameterSetWindow();
					} else {
						ps.requestFocus();
					}
				}
				else {
					JOptionPane.showMessageDialog(null, "There are no record in the database.",
								"Warning", JOptionPane.WARNING_MESSAGE);
				}
			}
		});

		JPanel optionPanel = new JPanel();
		optionPanel.setLayout(new BoxLayout(optionPanel, BoxLayout.Y_AXIS));
		optionPanel.add(paramPanel);

		queryTabPane.addTab("DB", parentPanel2);
		queryTabPane.setToolTipTextAt(TAB_ORDER_DB, "Query from DB.");
		queryTabPane.addTab("File", queryFilePane);
		queryTabPane.setToolTipTextAt(TAB_ORDER_FILE, "Query from user file.");
		queryTabPane.setSelectedIndex(TAB_ORDER_DB);
		queryTabPane.setFocusable(false);
		queryTabPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				reset();
				queryTabPane.update(queryTabPane.getGraphics());
				if (queryTabPane.getSelectedIndex() == TAB_ORDER_DB) {
					parentPanel2.update(parentPanel2.getGraphics());
					updateSelectQueryTable(queryDbTable);
				} else if (queryTabPane.getSelectedIndex() == TAB_ORDER_FILE) {
					queryFilePane.update(queryFilePane.getGraphics());
					updateSelectQueryTable(queryFileTable);
				}
			}
		});

		JPanel queryPanel = new JPanel();
		queryPanel.setLayout(new BorderLayout());
		queryPanel.add(queryTabPane, BorderLayout.CENTER);
		queryPanel.add(optionPanel, BorderLayout.SOUTH);
		queryPanel.setMinimumSize(new Dimension(0, 170));

		JPanel jtp2Panel = new JPanel();
		jtp2Panel.setLayout(new BorderLayout());
		jtp2Panel.add(dbPanel, BorderLayout.CENTER);
		jtp2Panel.add(hitLabel, BorderLayout.SOUTH);
		jtp2Panel.setMinimumSize(new Dimension(0, 70));
		Color colorGreen = new Color(0, 128, 0);
		hitLabel.setForeground(colorGreen);

		resultTabPane.addTab("Result", jtp2Panel);
		resultTabPane.setToolTipTextAt(TAB_RESULT_DB, "Result of DB hit.");
		resultTabPane.setFocusable(false);

		queryPlot.setMinimumSize(new Dimension(0, 100));
		compPlot.setMinimumSize(new Dimension(0, 120));
		resultPlot.setMinimumSize(new Dimension(0, 100));
		int height = initAppletHight / 3;
		JSplitPane jsp_cmp2db = new JSplitPane(JSplitPane.VERTICAL_SPLIT, compPlot, resultPlot);
		JSplitPane jsp_qry2cmp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, queryPlot,
				jsp_cmp2db);
		jsp_cmp2db.setDividerLocation(height);
		jsp_qry2cmp.setDividerLocation(height - 25);
		jsp_qry2cmp.setMinimumSize(new Dimension(190, 0));

		viewTabPane.addTab("Compare View", jsp_qry2cmp);
		viewTabPane.setToolTipTextAt(TAB_VIEW_COMPARE, "Comparison of query and result spectrum.");
		viewTabPane.setSelectedIndex(TAB_VIEW_COMPARE);
		viewTabPane.setFocusable(false);

		JSplitPane jsp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, queryPanel,
				resultTabPane);
		jsp.setDividerLocation(310);
		jsp.setMinimumSize(new Dimension(180, 0));
		jsp.setOneTouchExpandable(true);

		JSplitPane jsp2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, jsp,
				viewTabPane);
		int divideSize = (int)(initAppletWidth * 0.4);
		divideSize = (divideSize >= 180) ? divideSize : 180;
		jsp2.setDividerLocation(divideSize);
		jsp2.setOneTouchExpandable(true);

		mainPanel.add(jsp2, BorderLayout.CENTER);
		add(mainPanel);

		queryPlot.setApplet(this);
		compPlot.setApplet(this);
		resultPlot.setApplet(this);
	}

	/*
	 *
	 */
	private void reset() {
		queryPlot.clear();
		compPlot.clear();
		resultPlot.clear();
		queryPlot.setPeaks(null, 0);
		compPlot.setPeaks(null, 1);
		resultPlot.setPeaks(null, 0);

		if ( this.resultTabPane.getTabCount() > 0 ) {
			this.resultTabPane.setSelectedIndex(0);
		}
		DefaultTableModel dataModel = (DefaultTableModel)resultSorter.getTableModel();
		dataModel.setRowCount(0);
		hitLabel.setText(" ");
	}

	/**
	 *
	 */
	private void loadQueryFile(String fileName) {
		DefaultTableModel dataModel = (DefaultTableModel) fileSorter.getTableModel();
		dataModel.setRowCount(0);
		try {
			URL url = new URL(this.toolsUrl + "SpectrumSearch.jsp?file=" + fileName);
			QueryFileUtil qf = new QueryFileUtil(url);
			String[] names = qf.getQueryNames();
			DecimalFormat df = new DecimalFormat("000000");
			for ( int i = 0; i < names.length; i++ ) {
				String num = String.valueOf(i+1);
				String id = "US" + df.format(i+1);
				dataModel.addRow(new Object[]{num, names[i], id });
			}
			queryTabPane.setSelectedIndex(TAB_ORDER_FILE);
			this.queryFilePeaks = qf.getPeaks();
		}
		catch (Exception ie) {
			ie.printStackTrace();
			// ERROR：サーバーエラー
			JOptionPane.showMessageDialog(null, "Server error.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
	}

	/**
	 *
	 */
	private void searchSpectrum(String peakString, String precursor, String queryName, String queryKey) {
		reset();
		if (queryTabPane.getSelectedIndex() == TAB_ORDER_DB) {
			queryPlot.setSpectrumInfo(queryName, queryKey, precursor, PeakPanel.SP_TYPE_QUERY, false);
		}
		else if (queryTabPane.getSelectedIndex() == TAB_ORDER_FILE) {
			queryPlot.setSpectrumInfo(queryName, queryKey, precursor, PeakPanel.SP_TYPE_QUERY, true);
		}

		String[] lines = peakString.split(";");
		List<String> mzList = new ArrayList();
		List<String> inteList = new ArrayList();
		for ( String line : lines ) {
			String[] vals = line.split(",");
			mzList.add(vals[0]);
			inteList.add(vals[1]);
		}
		if ( mzList.size() == 0 ) {
			queryPlot.setNoPeak(true);
			hitLabel.setText(" 0 Hit.    ("
					+ ((PRECURSOR < 1) ? "" : "Precursor : " + PRECURSOR + ", ")
					+ "Tolerance : "
					+ TOLERANCE
					+ " "
					+ ((tolUnit1.isSelected()) ? tolUnit1.getText() : tolUnit2.getText()) + ", Cutoff threshold : "
					+ CUTOFF_THRESHOLD + ")");
			this.setCursor(Cursor.getDefaultCursor());
			return;
		}
		SearchApplet.this.queryPeakString = peakString;

		ssp = new SpectrumSearchParameter();
		ssp.setMzs(mzList.toArray(new String[]{}));
		ssp.setIntensities(inteList.toArray(new String[]{}));

		ssp.setTolerance(String.valueOf(TOLERANCE));
		String unit = "unit";
		if ( tolUnit2.isSelected() ) {
			unit = "ppm";
		}
		ssp.setUnitOfTolerance(unit);
		ssp.setCutoff(String.valueOf(CUTOFF_THRESHOLD));
		String ion = "0";
		if ( isIonRadio.get("Posi") ) {
			ion = "1";
		}
		else if (isIonRadio.get("Nega")) {
			ion = "-1";
		}
		ssp.setIonMode(ion);
		if ( PRECURSOR > 0 ) {
			ssp.setPrecursor(String.valueOf(PRECURSOR));
		}

		List<String> instTypes = new ArrayList();
		boolean isInstAll = true;
		for ( Iterator it = isInstChecks.keySet().iterator(); it.hasNext(); ) {
			String key = (String)it.next();
			if ( isInstChecks.get(key) ) {
				instTypes.add(key);
			}
			else {
				isInstAll = false;
			}
		}
		if ( isInstAll ) {
			instTypes.add("all");
		}
		ssp.setInstrumentTypes(instTypes.toArray(new String[]{}));

		List<String> msTypes = new ArrayList();
		boolean isMsAll = true;
		for ( Iterator it = isMsChecks.keySet().iterator(); it.hasNext(); ) {
			String key = (String)it.next();
			if ( isMsChecks.get(key) ) {
				msTypes.add(key);
			}
			else {
				isMsAll = false;
			}
		}
		if ( isMsAll ) {
			msTypes.add("all");
		}
		ssp.setMsTypes(msTypes.toArray(new String[]{}));
		setOperationEnbled(false);
		dlg.setVisible(true);

		SwingWorker worker = new SwingWorker() {
			List<SpectrumSearchResult> results = null;
			public Object construct() {
				SpectrumSearchInvoker inv = new SpectrumSearchInvoker(SearchApplet.this.baseUrl, SearchApplet.this.ssp);
				try {
					inv.invoke();
					results = inv.getResults();
				}
				catch ( SocketTimeoutException se ) {
					hitLabel.setText("Search timeout");
					SearchApplet.this.setCursor(Cursor.getDefaultCursor());
				}
				return null;
			}
			public void finished() {
				setOperationEnbled(true);
				dlg.setVisible(false);

				int total = 0;
				if ( results != null ) {
					total = results.size();
					DefaultTableModel dataModel = (DefaultTableModel)resultSorter.getTableModel();
					int no = 0;
					for ( SpectrumSearchResult result : results) {
						String id  = result.getId();
						String title  = result.getRecordTitle();
						String hitScore = result.getScore();
						String score = "";
						String hit = "";
						int pos = hitScore.indexOf(".");
						if ( pos > 0 ) {
							score = "0" + hitScore.substring(pos);
							hit = hitScore.substring(0, pos);
						}
						else {
							score = "0";
							hit = hitScore;
						}
						Double dblScore = Double.parseDouble(score);
						Integer ihit = Integer.parseInt(hit);

						int iIon = Integer.parseInt(result.getIonMode());
						String ion = "";
						if ( iIon > 0 )      { ion = "P"; }
						else if ( iIon < 0 ) { ion = "N"; }
						else                 { ion = "-"; }

						String siteNo = result.getSiteNo();
						String siteName = siteNameList[Integer.parseInt(siteNo)];
						Object[] rowData = { title, dblScore, ihit, id, ion, siteName, String.valueOf(++no) };
						dataModel.addRow(rowData);
					}
				}

				Peak peak = new Peak(SearchApplet.this.queryPeakString);
				queryPlot.setPeaks(peak, 0);
				compPlot.setPeaks(peak, 0);
				resultTabPane.setSelectedIndex(0);
				setAllPlotAreaRange(queryPlot);
				SearchApplet.this.setCursor(Cursor.getDefaultCursor());

				String msg = " " + total + " Hit.    ("
						+ ((PRECURSOR < 1) ? "" : "Precursor : " + PRECURSOR + ", ")
						+ "Tolerance : " + TOLERANCE + " "
						+ ((tolUnit1.isSelected()) ? tolUnit1.getText()
								: tolUnit2.getText()) + ", Cutoff threshold : "
						+ CUTOFF_THRESHOLD + ")";
				hitLabel.setText(msg);
				hitLabel.setToolTipText(msg);
			}
		};
		worker.start();
	}

	/**
	 *
	 */
	private void updateSelectQueryTable(JTable tbl) {
		this.setCursor(waitCursor);
		int selRow = tbl.getSelectedRow();
		if (selRow >= 0) {
			tbl.clearSelection();
			Color defColor = tbl.getSelectionBackground();
			tbl.setRowSelectionInterval(selRow, selRow);
			tbl.setSelectionBackground(Color.PINK);
			tbl.update(tbl.getGraphics());
			tbl.setSelectionBackground(defColor);
		}
		this.setCursor(Cursor.getDefaultCursor());
	}

	/**
	 *
	 */
	private boolean getSpectrumForQuery(String searchName) {
		DefaultTableModel dataModel = (DefaultTableModel) querySorter.getTableModel();
		dataModel.setRowCount(0);
		GetRecordTitleInvoker inv = new GetRecordTitleInvoker(this.baseUrl);
		try {
			inv.invoke();
		}
		catch ( SocketTimeoutException se ) {
			JOptionPane.showMessageDialog(null, "Server error(2): Timeout", "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		RecordInfoList infoList = inv.getResults();
		if ( infoList == null ) {
			JOptionPane.showMessageDialog(null, "There are no record in the database.",
								"Warning", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		int num = infoList.getListSize();
		this.nameList.clear();
		for ( int i = 0; i < num; i++ ) {
			RecordInfo info = infoList.getRecordInfo(i);
			String id     = info.getId();
			String title  = info.getRecordTitle();
			String siteNo = info.getSiteNo();
			String[] cutIdNameSite = new String[] { id, title, siteNo };
			this.nameList.add(cutIdNameSite);
			String siteName = siteNameList[Integer.parseInt(siteNo)];
			dataModel.addRow(new String[]{ id, title, siteName, String.valueOf(i+1) });
		}
		return true;
	}

	/**
	 *
	 */
	private void showRecordPage(JTable eventTbl) {
		int selRows[] = eventTbl.getSelectedRows();
		int colIndexId = eventTbl.getColumnModel().getColumnIndex(COL_LABEL_ID);
		String id = (String)eventTbl.getValueAt(selRows[0], colIndexId);
		String url = this.toolsUrl + "Record.jsp?id=" + id;
		try {
			context.showDocument(new URL(url), "_blank");
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 *
	 */
	private void recListPopup(MouseEvent e) {
		JTable tbl = null;
		JScrollPane pane = null;
		try {
			tbl = (JTable)e.getSource();
		}
		catch (ClassCastException cce) {
			pane = (JScrollPane)e.getSource();
			if (pane.equals(queryDbPane)) {
				tbl = queryDbTable;
			}
			else if (pane.equals(resultPane)) {
				tbl = resultTable;
			}
			if (pane.equals(queryFilePane)) {
				tbl = queryFileTable;
			}
		}
		int rowCnt = tbl.getSelectedRows().length;

		JMenuItem item1 = new JMenuItem("Show Record");
		item1.addActionListener(new PopupShowRecordListener(tbl));
		JMenuItem item2 = new JMenuItem("Multiple Display");
		item2.addActionListener(new PopupMultipleDisplayListener(tbl));

		if (tbl.equals(queryFileTable)) {
			item1.setEnabled(false);
			item2.setEnabled(false);
		}
		else if (rowCnt == 0) {
			item1.setEnabled(false);
			item2.setEnabled(false);
		}
		else if (rowCnt == 1) {
			item1.setEnabled(true);
			item2.setEnabled(false);
		}
		else if (rowCnt > 1) {
			item1.setEnabled(false);
			item2.setEnabled(true);
		}

		JPopupMenu popup = new JPopupMenu();
		popup.add(item1);
		if (tbl.equals(resultTable)) {
			popup.add(item2);
		}
		popup.show(e.getComponent(), e.getX(), e.getY());
	}


	/**
	 *
	 */
	private void initTolInfo() {
		ArrayList<String> valueList = cm.getCookie(COOKIE_TOL);

		if (valueList.size() != 0) {
			try {
				TOLERANCE = Float.valueOf(valueList.get(0));
			} catch (Exception e) {
			}

			if (valueList.contains(tolUnit2.getText())) {
				tolUnit1.setSelected(false);
				tolUnit2.setSelected(true);
			} else {
				tolUnit1.setSelected(true);
				tolUnit2.setSelected(false);
			}
		} else {
			TOLERANCE = 0.3f;
			valueList.add(String.valueOf(TOLERANCE));
			if (tolUnit1.isSelected()) {
				valueList.add(tolUnit1.getText());
			}
			else {
				valueList.add(tolUnit2.getText());
			}
			cm.setCookie(COOKIE_TOL, valueList);
		}
	}

	/**
	 *
	 */
	private void initCutoffInfo() {
		ArrayList<String> valueList = cm.getCookie(COOKIE_CUTOFF);

		if (valueList.size() != 0) {
			try {
				CUTOFF_THRESHOLD = Integer.valueOf(valueList.get(0));
			} catch (Exception e) {
			}
		} else {
			CUTOFF_THRESHOLD = 5;
			valueList.add(String.valueOf(CUTOFF_THRESHOLD));
			cm.setCookie(COOKIE_CUTOFF, valueList);
		}
	}

	/**
	 *
	 */
	private void initInstInfo() {
		instChecks = new LinkedHashMap<String, JCheckBox>();
		isInstChecks = new HashMap<String, Boolean>();

		ArrayList<String> valueGetList = cm.getCookie(COOKIE_INST);
		ArrayList<String> valueSetList = new ArrayList<String>();

		boolean checked = false;

		for (Iterator it = this.instGroup.keySet().iterator(); it.hasNext(); ) {
			String key = (String)it.next();
			String[] list = this.instGroup.get(key);
			for ( int j = 0; j < list.length; j++ ) {
				String val = list[j];
				JCheckBox chkBox;

				if (valueGetList.size() != 0) {
					if (valueGetList.contains(val)) {
						chkBox = new JCheckBox(val, true);
						checked = true;
					} else {
						chkBox = new JCheckBox(val, false);
					}
				} else {
					if ( isDefaultInst(val) ) {
						chkBox = new JCheckBox(val, true);
						checked = true;
						valueSetList.add(val);
					} else {
						chkBox = new JCheckBox(val, false);
					}
				}

				instChecks.put(val, chkBox);
				isInstChecks.put(val, chkBox.isSelected());
			}
		}

		if (instChecks.size() == 0 && isInstChecks.size() == 0) {
			JOptionPane.showMessageDialog(null,
					"Instrument Type is not registered in the database.",
					"Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		if ( !checked ) {
			for ( Iterator it = instChecks.keySet().iterator(); it.hasNext();) {
				String key = (String)it.next();
				((JCheckBox)instChecks.get(key)).setSelected(true);
				isInstChecks.put(key, true);
				valueSetList.add(key);
			}
		}

		if (valueGetList.size() == 0) {
			cm.setCookie(COOKIE_INST, valueSetList);
		}
	}

	/**
	 *
	 */
	private void initMsInfo() {
		msChecks = new LinkedHashMap<String, JCheckBox>();
		isMsChecks = new HashMap<String, Boolean>();

		ArrayList<String> valueGetList = cm.getCookie(COOKIE_MS);
		ArrayList<String> valueSetList = new ArrayList<String>();

		boolean checked = false;

		for ( int i = 0; i < this.msTypes.length; i++ ) {
			String msType = this.msTypes[i];
			JCheckBox chkBox;

			if (valueGetList.size() != 0) {
				if ( valueGetList.contains(msType) ) {
					checked = true;
				}
			}
			else {
				checked = true;
				valueSetList.add(msType);
			}
			chkBox = new JCheckBox(msType, checked);
			msChecks.put(msType, chkBox);
			isMsChecks.put(msType, chkBox.isSelected());
		}

		if (msChecks.size() == 0 && isMsChecks.size() == 0) {
			JOptionPane.showMessageDialog(null,
					"MS Type is not registered in the database.",
					"Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		if ( !checked ) {
			for (Iterator it = msChecks.keySet().iterator(); it.hasNext();) {
				String key = (String)it.next();
				((JCheckBox)msChecks.get(key)).setSelected(true);
				isMsChecks.put(key, true);
				valueSetList.add(key);
			}
		}

		if (valueGetList.size() == 0) {
			cm.setCookie(COOKIE_MS, valueSetList);
		}
	}

	/**
	 *
	 */
	private void initIonInfo() {
		final String keyPosi = "Posi";
		final String keyNega = "Nega";
		final String keyBoth = "Both";

		ionRadio = new LinkedHashMap<String, JRadioButton>();
		isIonRadio = new HashMap<String, Boolean>();

		ArrayList<String> valueList = cm.getCookie(COOKIE_ION);

		JRadioButton ionPosi = new JRadioButton("Positive");
		JRadioButton ionNega = new JRadioButton("Negative");
		JRadioButton ionBoth = new JRadioButton("Both");

		if (valueList.size() != 0) {
			ionPosi.setSelected(valueList.contains(keyPosi));
			ionNega.setSelected(valueList.contains(keyNega));
			ionBoth.setSelected(valueList.contains(keyBoth));
		}
		else {
			ionPosi.setSelected(true);
			ionNega.setSelected(false);
			ionBoth.setSelected(false);
			valueList.add(keyPosi);
			cm.setCookie(COOKIE_ION, valueList);
		}

		ionRadio.put(keyPosi, ionPosi);
		ionRadio.put(keyNega, ionNega);
		ionRadio.put(keyBoth, ionBoth);
		isIonRadio.put(keyPosi, ionPosi.isSelected());
		isIonRadio.put(keyNega, ionNega.isSelected());
		isIonRadio.put(keyBoth, ionBoth.isSelected());
	}

	/**
	 *
	 */
	private boolean isDefaultInst(String inst) {

		if ( inst.indexOf("ESI") != -1 ||
			 inst.indexOf("APPI") != -1 ||
			 inst.indexOf("MALDI") != -1 ) {

			return true;
		}
		return false;
	}

	/**
	 *
	 */
	protected Frame getFrame() {
		for (Container p = getParent(); p != null; p = p.getParent()) {
			if (p instanceof Frame) return (Frame)p;
		}
		return null;
	}

	/**
	 *
	 */
	private void setOperationEnbled(boolean value) {
		queryFileTable.setEnabled(value);
		queryDbTable.setEnabled(value);
		etcPropertyButton.setEnabled(value);
		btnAll.setEnabled(value);
		queryTabPane.setEnabled(value);
		resultTabPane.setEnabled(value);
		viewTabPane.setEnabled(value);
	}

	/**
	 *
	 */
	class ParameterSetWindow extends JFrame {

		private final int LABEL_SIZE_L = 0;
		private final int LABEL_SIZE_M = 1;
		private final int LABEL_SIZE_S = 2;
		private final JTextField preField;
		private final JTextField tolField;
		private final JTextField cutoffField;
		private boolean isTolUnit1 = tolUnit1.isSelected();
		private boolean isTolUnit2 = tolUnit2.isSelected();

		public ParameterSetWindow() {

			setResizable(false);

			ArrayList<Component> keyListenerList = new ArrayList<Component>();
			keyListenerList.add(this);
			Container container= getContentPane();
			initMainContainer(container);

			JPanel delimPanel;
			JPanel labelPanel;
			JPanel itemPanel;

			// Tolerance
			labelPanel = newLabelPanel("Tolerance of m/z", " Tolerance of m/z. ", LABEL_SIZE_L, 2);
			JPanel tolPanel = new JPanel();
			tolPanel.setLayout(new BoxLayout(tolPanel, BoxLayout.X_AXIS));
			tolField = new JTextField(String.valueOf(TOLERANCE), 5);
			tolField.setHorizontalAlignment(JTextField.RIGHT);
			keyListenerList.add(tolField);
			keyListenerList.add(tolUnit1);
			keyListenerList.add(tolUnit2);
			ButtonGroup tolGroup = new ButtonGroup();
			tolGroup.add(tolUnit1);
			tolGroup.add(tolUnit2);
			tolPanel.add(tolUnit1);
			tolPanel.add(tolUnit2);

			itemPanel = new JPanel();
			initItemPanel(itemPanel);
			itemPanel.add(wrappTextPanel(tolField), itemPanelGBC(0d, 0d, 0, 0, 1, 1));
			itemPanel.add(tolPanel, itemPanelGBC(0d, 0d, 1, 0, 1, 1));
			itemPanel.add(new JPanel(), itemPanelGBC(1d, 0d, 2, 0, GridBagConstraints.REMAINDER, 1));

			delimPanel = new JPanel();
			initDelimPanel(delimPanel, true);
			delimPanel.add(labelPanel, delimPanelGBC(0d, 0d, 0, 0, 1, 1));
			delimPanel.add(itemPanel, delimPanelGBC(0d, 0d, 1, 0, 1, 1));
			delimPanel.add(new JPanel(), itemPanelGBC(1d, 0d, 2, 0, GridBagConstraints.REMAINDER, 1));
			container.add(delimPanel, mainContainerGBC(0, 0, 1, 1));

			// Cutoff Thresholds
			labelPanel = newLabelPanel("Cutoff Threshold", " Cutoff threshold of intensities. ", LABEL_SIZE_L, 2);
			cutoffField = new JTextField(String.valueOf(CUTOFF_THRESHOLD), 5);
			cutoffField.setHorizontalAlignment(JTextField.RIGHT);
			keyListenerList.add(cutoffField);

			itemPanel = new JPanel();
			initItemPanel(itemPanel);
			itemPanel.add(wrappTextPanel(cutoffField), itemPanelGBC(0d, 0d, 0, 0, 1, 1));
			itemPanel.add(new JPanel(), itemPanelGBC(1d, 0d, 1, 0, GridBagConstraints.REMAINDER, 1));

			delimPanel = new JPanel();
			initDelimPanel(delimPanel, true);
			delimPanel.add(labelPanel, delimPanelGBC(0d, 0d, 0, 0, 1, 1));
			delimPanel.add(itemPanel, delimPanelGBC(0d, 0d, 1, 0, 1, 1));
			delimPanel.add(new JPanel(), itemPanelGBC(1d, 0d, 2, 0, GridBagConstraints.REMAINDER, 1));
			container.add(delimPanel, mainContainerGBC(0, 1, 1, 1));

			// Instrument Type
			labelPanel = newLabelPanel("Instrument Type",
					"&nbsp;Instrument type.&nbsp;", LABEL_SIZE_L, 2);

			final JCheckBox chkBoxInstAll = new JCheckBox("All");
			chkBoxInstAll.setSelected(isInstAll());
			final JCheckBox chkBoxInstDefault = new JCheckBox("Default");
			chkBoxInstDefault.setSelected(isInstDefault());
			keyListenerList.add(chkBoxInstAll);
			keyListenerList.add(chkBoxInstDefault);

			itemPanel = new JPanel();
			initItemPanel(itemPanel);
			itemPanel.add(chkBoxInstAll, itemPanelGBC(0d, 0d, 0, 0, 1, 1));
			itemPanel.add(new JPanel(), itemPanelGBC(0.1d, 0d, 1, 0, 1, 1));
			itemPanel.add(chkBoxInstDefault, itemPanelGBC(0d, 0d, 2, 0, 1, 1));
			itemPanel.add(new JPanel(), itemPanelGBC(1d, 0d, 3, 0, GridBagConstraints.REMAINDER, 0));

			delimPanel = new JPanel();
			initDelimPanel(delimPanel, true);
			delimPanel.add(labelPanel, delimPanelGBC(0d, 0d, 0, 0, 1, 1));
			delimPanel.add(itemPanel, delimPanelGBC(0d, 0d, 1, 0, 1, 1));

			JPanel instPanel = new JPanel();
			initItemPanel(instPanel);

			int keyNum = 0;
			boolean isSep = false;
			for (Iterator it = SearchApplet.this.instGroup.keySet().iterator(); it.hasNext();) {
				String key = (String)it.next();
				itemPanel = new JPanel();
				initItemPanel(itemPanel);

				String[] list = SearchApplet.this.instGroup.get(key);
				int valNum = 0;
				for ( int j = 0; j < list.length; j++ ) {
					String val = list[j];

					if (keyNum != 0 && valNum == 0) {
						itemPanel.add(new JSeparator(), itemPanelGBC(0d, 0d, 0, valNum, 3, 1));
						valNum += 1;
						isSep = true;
					}

					JCheckBox chkBox = (JCheckBox)instChecks.get(val);
					keyListenerList.add(chkBox);
					itemPanel.add(chkBox, itemPanelGBC(0d, 0d, 1, valNum, 1, 1));
					valNum += 1;
				}

				if (valNum > 0) {
					labelPanel = newLabelPanel(key, null, LABEL_SIZE_S, 2);
					if (isSep) {
						itemPanel.add(labelPanel, itemPanelGBC(0d, 0d, 0, 1, 1, 1));
					} else {
						itemPanel.add(labelPanel, itemPanelGBC(0d, 0d, 0, 0, 1, 1));
					}
					itemPanel.add(new JPanel(), itemPanelGBC(1d, 1d, 2, 0, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER));
					instPanel.add(itemPanel, itemPanelGBC(0d, 0d, 0, keyNum, 1, 1));
					keyNum += 1;
					isSep = false;
				}
			}
			instPanel.add(new JPanel(), itemPanelGBC(1d, 1d, 0, keyNum, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER));
			JScrollPane scroll = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			scroll.setPreferredSize(new Dimension(240, 250));
			scroll.getVerticalScrollBar().setUnitIncrement(60);
			scroll.setViewportView(instPanel);

			delimPanel.add(scroll, delimPanelGBC(0d, 0d, 1, 1, 1, 1));
			delimPanel.add(new JPanel(), itemPanelGBC(1d, 0d, 2, 0, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER));
			container.add(delimPanel, mainContainerGBC(0, 2, 1, 1));



			// MS Type
			labelPanel = newLabelPanel("MS Type",
					"&nbsp;MS type.&nbsp;", LABEL_SIZE_L, 2);

			JPanel msPanel = new JPanel();
			msPanel.setLayout(new BoxLayout(msPanel, BoxLayout.X_AXIS));

			final JCheckBox chkBoxMsAll = new JCheckBox("All");
			chkBoxMsAll.setSelected(isMsAll());
			keyListenerList.add(chkBoxMsAll);
			msPanel.add(chkBoxMsAll);

			for (Iterator it = msChecks.keySet().iterator(); it.hasNext();) {
				String key = (String)it.next();

				JCheckBox chkBox = (JCheckBox)msChecks.get(key);
				keyListenerList.add(chkBox);
				msPanel.add(chkBox);
				msPanel.add(new JLabel(" "));
			}

			itemPanel = new JPanel();
			initItemPanel(itemPanel);
			itemPanel.add(msPanel, itemPanelGBC(0d, 0d, 0, 0, 1, 1));
			itemPanel.add(new JPanel(), itemPanelGBC(1d, 0d, 1, 0, GridBagConstraints.REMAINDER, 1));

			delimPanel = new JPanel();
			initDelimPanel(delimPanel, true);
			delimPanel.add(labelPanel, delimPanelGBC(0d, 0d, 0, 0, 1, 1));
			delimPanel.add(itemPanel, delimPanelGBC(0d, 0d, 1, 0, 1, 1));
			delimPanel.add(new JPanel(), itemPanelGBC(1d, 0d, 2, 0, GridBagConstraints.REMAINDER, 1));
			container.add(delimPanel, mainContainerGBC(0, 3, 1, 1));

			// Ion Mode
			labelPanel = newLabelPanel("Ion Mode",
					"&nbsp;Ion mode.&nbsp;", LABEL_SIZE_L, 2);

			JPanel ionPanel = new JPanel();
			ionPanel.setLayout(new BoxLayout(ionPanel, BoxLayout.X_AXIS));

			ButtonGroup ionGroup = new ButtonGroup();
			for (Iterator i=ionRadio.keySet().iterator(); i.hasNext();) {
				String key = (String)i.next();

				JRadioButton rdoBtn = (JRadioButton)ionRadio.get(key);
				keyListenerList.add(rdoBtn);
				ionGroup.add(rdoBtn);
				ionPanel.add(rdoBtn);
			}

			itemPanel = new JPanel();
			initItemPanel(itemPanel);
			itemPanel.add(ionPanel, itemPanelGBC(0d, 0d, 0, 0, 1, 1));
			itemPanel.add(new JPanel(), itemPanelGBC(1d, 0d, 1, 0, GridBagConstraints.REMAINDER, 1));

			delimPanel = new JPanel();
			initDelimPanel(delimPanel, true);
			delimPanel.add(labelPanel, delimPanelGBC(0d, 0d, 0, 0, 1, 1));
			delimPanel.add(itemPanel, delimPanelGBC(0d, 0d, 1, 0, 1, 1));
			delimPanel.add(new JPanel(), itemPanelGBC(1d, 0d, 2, 0, GridBagConstraints.REMAINDER, 1));
			container.add(delimPanel, mainContainerGBC(0, 4, 1, 1));

			// Precursor m/z
			labelPanel = newLabelPanel("Precursor m/z", " Precursor m/z. ", LABEL_SIZE_L, 2);
			preField = new JTextField(((PRECURSOR < 0) ? "" : String.valueOf(PRECURSOR)), 5);
			preField.setHorizontalAlignment(JTextField.RIGHT);
			keyListenerList.add(preField);

			itemPanel = new JPanel();
			initItemPanel(itemPanel);
			itemPanel.add(wrappTextPanel(preField), itemPanelGBC(0d, 0d, 0, 0, 1, 1));
			itemPanel.add(new JPanel(), itemPanelGBC(1d, 0d, 2, 0, GridBagConstraints.REMAINDER, 1));

			delimPanel = new JPanel();
			initDelimPanel(delimPanel, true);
			delimPanel.add(labelPanel, delimPanelGBC(0d, 0d, 0, 0, 1, 1));
			delimPanel.add(itemPanel, delimPanelGBC(0d, 0d, 1, 0, 1, 1));
			delimPanel.add(new JPanel(), itemPanelGBC(1d, 0d, 2, 0, GridBagConstraints.REMAINDER, 1));
			container.add(delimPanel, mainContainerGBC(0, 5, 1, 1));

			// ボタン
			final JButton okButton = new JButton("OK");
			keyListenerList.add(okButton);
			final JButton cancelButton = new JButton("Cancel");
			keyListenerList.add(cancelButton);
			JPanel btnPanel = new JPanel();
			btnPanel.add(okButton);
			btnPanel.add(cancelButton);
			container.add(btnPanel, mainContainerGBC(0, 6, 1, 1));

			chkBoxInstAll.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					for (Iterator i=instChecks.keySet().iterator(); i.hasNext(); ) {
						String key = (String)i.next();
						if (chkBoxInstAll.isSelected()) {
							((JCheckBox)instChecks.get(key)).setSelected(true);
						} else {
							((JCheckBox)instChecks.get(key)).setSelected(false);
						}
					}
					chkBoxInstDefault.setSelected(isInstDefault());
				}
			});

			chkBoxInstDefault.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					for (Iterator i=instChecks.keySet().iterator(); i.hasNext(); ) {
						String key = (String)i.next();
						if (chkBoxInstDefault.isSelected()) {
							// Allチェックをはずしてデフォルト選択
							chkBoxInstAll.setSelected(false);
							if ( isDefaultInst(key) ) {
								((JCheckBox)instChecks.get(key)).setSelected(true);
							} else {
								((JCheckBox)instChecks.get(key)).setSelected(false);
							}
						} else {
							((JCheckBox)instChecks.get(key)).setSelected(false);
						}
					}
					chkBoxInstAll.setSelected(isInstAll());
				}
			});

			for (Iterator i=instChecks.keySet().iterator(); i.hasNext();) {
				String key = (String)i.next();
				((JCheckBox)instChecks.get(key)).addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						chkBoxInstAll.setSelected(isInstAll());
						chkBoxInstDefault.setSelected(isInstDefault());
					}
				});
			}

			chkBoxMsAll.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					for (Iterator it = msChecks.keySet().iterator(); it.hasNext(); ) {
						String key = (String)it.next();
						if (chkBoxMsAll.isSelected()) {
							((JCheckBox)msChecks.get(key)).setSelected(true);
						} else {
							((JCheckBox)msChecks.get(key)).setSelected(false);
						}
					}
				}
			});

			for (Iterator it = msChecks.keySet().iterator(); it.hasNext();) {
				String key = (String)it.next();
				((JCheckBox)msChecks.get(key)).addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						chkBoxMsAll.setSelected(isMsAll());
					}
				});
			}

			okButton.addActionListener(new ActionListener() {
				private final Color defColor = okButton.getBackground();
				private void startProc() {
					okButton.setBackground(Color.PINK);
					okButton.update(okButton.getGraphics());
					ParameterSetWindow.this.setCursor(waitCursor);
				}
				private void endProc() {
					if (!ParameterSetWindow.this.getCursor().equals(Cursor.getDefaultCursor())) {
						ParameterSetWindow.this.setCursor(Cursor.getDefaultCursor());
					}
					okButton.setBackground(defColor);
				}
				public void actionPerformed(ActionEvent e) {

					startProc();
					try {
						preField.setText(preField.getText().trim());
						if (!preField.getText().equals("")) {
							int num = Integer.parseInt(preField.getText());
							if (num < 1) {
								JOptionPane.showMessageDialog(null,
										"[Precursor m/z]  Value must be an integer of 1 or more.",
										"Warning",
										JOptionPane.WARNING_MESSAGE);
								endProc();
								return;
							}
						}
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(null,
								"[Precursor m/z]  Value must be an integer of 1 or more.",
								"Warning",
								JOptionPane.WARNING_MESSAGE);
						endProc();
						return;
					}

					try {
						tolField.setText(tolField.getText().trim());
						float num = Float.parseFloat(tolField.getText());
						if (num < 0) {
							JOptionPane.showMessageDialog(null,
									"[Tolerance]  Value must be an positive numerical value.",
									"Warning",
									JOptionPane.WARNING_MESSAGE);
							endProc();
							return;
						}
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(null,
								"[Tolerance]  Value must be an numerical value.",
								"Warning",
								JOptionPane.WARNING_MESSAGE);
						endProc();
						return;
					}

					try {
						cutoffField.setText(cutoffField.getText().trim());
						int num = Integer.parseInt(cutoffField.getText());
						if (num < 0) {
							JOptionPane.showMessageDialog(null,
									"[Cutoff Threshold]  Value must be an positive integer.",
									"Warning",
									JOptionPane.WARNING_MESSAGE);
							endProc();
							return;
						}
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(null,
								"[Cutoff Threshold]  Value must be an integer.",
								"Warning",
								JOptionPane.WARNING_MESSAGE);
						endProc();
						return;
					}
					if (instChecks.size() == 0) {
						JOptionPane.showMessageDialog(null,
								"[Instrument Type]  Instrument type is not registered in the database.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						endProc();
						return;
					}
					if (!isInstCheck()) {
						JOptionPane.showMessageDialog(null,
								"[Instrument Type]  Select one or more checkbox.",
								"Warning",
								JOptionPane.WARNING_MESSAGE);
						endProc();
						return;
					}
					if (msChecks.size() == 0) {
						JOptionPane.showMessageDialog(null,
								"[MS Type]  MS type is not registered in the database.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						endProc();
						return;
					}
					if (!isMsCheck()) {
						JOptionPane.showMessageDialog(null,
								"[MS Type]  Select one or more checkbox.",
								"Warning",
								JOptionPane.WARNING_MESSAGE);
						endProc();
						return;
					}

					if (isPreChange()
							|| isTolChange()
							|| isCutoffChange()
							|| isInstChange()
							|| isMsChange()
							|| isIonChange()) {

						preChange(true);
						tolChange(true);
						cutoffChange(true);
						instChange(true);
						msChange(true);
						ionChange(true);

						resultPlot.setSpectrumInfo("", "", "", "", false);
						switch (queryTabPane.getSelectedIndex()) {
						case TAB_ORDER_DB :
							updateSelectQueryTable(queryDbTable);
							break;
						case TAB_ORDER_FILE :
							updateSelectQueryTable(queryFileTable);
							break;
						}
					}

					endProc();

					dispose();
					isSubWindow = false;
				}
			});

			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					preChange(false);
					tolChange(false);
					cutoffChange(false);
					instChange(false);
					msChange(false);
					ionChange(false);

					dispose();
					isSubWindow = false;
				}
			});

			for (int i = 0; i < keyListenerList.size(); i++) {
				keyListenerList.get(i).addKeyListener(new KeyAdapter() {
					public void keyReleased(KeyEvent e) {
						if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
							preChange(false);
							tolChange(false);
							cutoffChange(false);
							instChange(false);
							msChange(false);
							ionChange(false);

							dispose();
							isSubWindow = false;
						}
					}
				});
			}
			setTitle("Search Parameter Setting");
			pack();
			Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
			setLocation((int)(d.getWidth() / 2 - getWidth() / 2),
					(int)(d.getHeight() / 2 - getHeight() / 2));
			setVisible(true);

			addWindowListener(new WindowAdapter() {
				public void windowOpened(WindowEvent e) {
					isSubWindow = true;
				}
				public void windowClosing(WindowEvent e) {
					cancelButton.doClick();
				}
			});
		}

		/**
		 *
		 */
		private boolean isPreChange() {

			if (preField.getText().equals("")) {
				if (PRECURSOR != -1) {
					return true;
				}
			}
			else if (Integer.parseInt(preField.getText()) != PRECURSOR) {
				return true;
			}
			return false;
		}

		/**
		 *
		 */
		private void preChange(boolean isChange) {
			if (isChange) {
				if (preField.getText().equals("")) {
					PRECURSOR = -1;
				}
				else {
					PRECURSOR = Integer.parseInt(preField.getText());
				}
			}
		}

		/**
		 *
		 */
		private boolean isTolChange() {
			if (Float.parseFloat(tolField.getText()) != TOLERANCE) {
				return true;
			}
			else if (isTolUnit1 != tolUnit1.isSelected()
					|| isTolUnit2 != tolUnit2.isSelected()) {
				return true;
			}
			return false;
		}

		/**
		 *
		 */
		private void tolChange(boolean isChange) {

			if (isChange) {
				ArrayList<String> valueList = new ArrayList<String>();

				TOLERANCE = Float.parseFloat(tolField.getText());
				valueList.add(String.valueOf(TOLERANCE));

				isTolUnit1 = tolUnit1.isSelected();
				isTolUnit2 = tolUnit2.isSelected();
				if (tolUnit2.isSelected()) {
					valueList.add(tolUnit2.getText());
				}
				else {
					valueList.add(tolUnit1.getText());
				}
				cm.setCookie(COOKIE_TOL, valueList);
			}
			else {
				tolUnit1.setSelected(isTolUnit1);
				tolUnit2.setSelected(isTolUnit2);
			}
		}

		/**
		 *
		 */
		private boolean isCutoffChange() {

			if (Integer.parseInt(cutoffField.getText()) != CUTOFF_THRESHOLD) {
				return true;
			}
			return false;
		}

		/**
		 *
		 */
		private void cutoffChange(boolean isChange) {

			if (isChange) {
				ArrayList<String> valueList = new ArrayList<String>();

				CUTOFF_THRESHOLD = Integer.parseInt(cutoffField.getText());
				valueList.add(String.valueOf(CUTOFF_THRESHOLD));
				cm.setCookie(COOKIE_CUTOFF, valueList);
			}
		}

		/**
		 *
		 */
		private boolean isInstAll() {

			if (instChecks.size() == 0) {
				return false;
			}
			for (Iterator j=instChecks.keySet().iterator(); j.hasNext(); ) {
				String key = (String)j.next();

				if ( !((JCheckBox)instChecks.get(key)).isSelected() ) {
					return false;
				}
			}
			return true;
		}

		/**
		 *
		 */
		private boolean isInstDefault() {

			if (instChecks.size() == 0) {
				return false;
			}
			for (Iterator j=instChecks.keySet().iterator(); j.hasNext(); ) {
				String key = (String)j.next();
				if ( isDefaultInst(key) ) {
					if ( !((JCheckBox)instChecks.get(key)).isSelected() ) {
						return false;
					}
				} else {
					if ( ((JCheckBox)instChecks.get(key)).isSelected() ) {
						return false;
					}
				}
			}
			return true;
		}

		/**
		 *
		 */
		private boolean isInstCheck() {

			if (instChecks.size() == 0) {
				return false;
			}
			for (Iterator j=instChecks.keySet().iterator(); j.hasNext(); ) {
				String key = (String)j.next();
				if ( ((JCheckBox)instChecks.get(key)).isSelected() ) {
					return true;
				}
			}
			return false;
		}

		/**
		 *
		 */
		private boolean isInstChange() {

			if (isInstChecks.size() == 0) {
				return false;
			}
			for (Iterator it = isInstChecks.keySet().iterator(); it.hasNext(); ) {
				String key = (String)it.next();
				boolean before = (boolean)isInstChecks.get(key);
				boolean after = ((JCheckBox)instChecks.get(key)).isSelected();
				if (before != after) {
					return true;
				}
			}
			return false;
		}

		/**
		 *
		 */
		private void instChange(boolean isChange) {
			ArrayList<String> valueList = new ArrayList<String>();

			if (isInstChecks.size() == 0) {
				return;
			}
			for (Iterator i=isInstChecks.keySet().iterator(); i.hasNext(); ) {
				String key = (String)i.next();
				boolean before = (boolean)isInstChecks.get(key);
				boolean after = ((JCheckBox)instChecks.get(key)).isSelected();
				if (before != after) {
					if (isChange) {
						isInstChecks.put(key, after);
					}
					else {
						((JCheckBox)instChecks.get(key)).setSelected(before);
					}
				}
				if ( ((JCheckBox)instChecks.get(key)).isSelected() ) {
					valueList.add(key);
				}
			}
			if (isChange) {
				cm.setCookie(COOKIE_INST, valueList);
			}
		}

		/**
		 *
		 */
		private boolean isMsAll() {
			if (msChecks.size() == 0) {
				return false;
			}
			for (Iterator it = msChecks.keySet().iterator(); it.hasNext(); ) {
				String key = (String)it.next();
				if ( !((JCheckBox)msChecks.get(key)).isSelected() ) {
					return false;
				}
			}
			return true;
		}

		/**
		 *
		 */
		private boolean isMsCheck() {
			if (msChecks.size() == 0) {
				return false;
			}
			for (Iterator it = msChecks.keySet().iterator(); it.hasNext(); ) {
				String key = (String)it.next();
				if ( ((JCheckBox)msChecks.get(key)).isSelected() ) {
					return true;
				}
			}
			return false;
		}

		/**
		 *
		 */
		private boolean isMsChange() {
			if (isMsChecks.size() == 0) {
				return false;
			}
			for (Iterator it = isMsChecks.keySet().iterator(); it.hasNext(); ) {
				String key = (String)it.next();
				boolean before = (boolean)isMsChecks.get(key);
				boolean after = ((JCheckBox)msChecks.get(key)).isSelected();
				if (before != after) {
					return true;
				}
			}
			return false;
		}

		/**
		 *
		 */
		private void msChange(boolean isChange) {
			ArrayList<String> valueList = new ArrayList<String>();
			if (isMsChecks.size() == 0) {
				return;
			}
			for (Iterator it = isMsChecks.keySet().iterator(); it.hasNext(); ) {
				String key = (String)it.next();
				boolean before = (boolean)isMsChecks.get(key);
				boolean after = ((JCheckBox)msChecks.get(key)).isSelected();
				if (before != after) {
					if (isChange) {
						isMsChecks.put(key, after);
					}
					else {
						((JCheckBox)msChecks.get(key)).setSelected(before);
					}
				}
				if ( ((JCheckBox)msChecks.get(key)).isSelected() ) {
					valueList.add(key);
				}
			}
			if (isChange) {
				cm.setCookie(COOKIE_MS, valueList);
			}
		}

		/**
		 *
		 */
		private boolean isIonChange() {
			for (Iterator it = isIonRadio.keySet().iterator(); it.hasNext(); ) {
				String key = (String)it.next();

				boolean before = (boolean)isIonRadio.get(key);
				boolean after = ((JRadioButton)ionRadio.get(key)).isSelected();

				if (before != after) {
					return true;
				}
			}
			return false;
		}

		/**
		 *
		 */
		private void ionChange(boolean isChange) {
			ArrayList<String> valueList = new ArrayList<String>();
			for (Iterator it = isIonRadio.keySet().iterator(); it.hasNext(); ) {
				String key = (String)it.next();

				boolean before = (boolean)isIonRadio.get(key);
				boolean after = ((JRadioButton)ionRadio.get(key)).isSelected();

				if (before != after) {
					if (isChange) {
						isIonRadio.put(key, after);
					}
					else {
						((JRadioButton)ionRadio.get(key)).setSelected(before);
					}
				}
				if ( ((JRadioButton)ionRadio.get(key)).isSelected() ) {
					valueList.add(key);
				}
			}
			if (isChange) {
				cm.setCookie(COOKIE_ION, valueList);
			}
		}

		/**
		 *
		 */
		private void initMainContainer(Container c) {
			c.setLayout(new GridBagLayout());
		}

		/**
		 *
		 */
		private GridBagConstraints mainContainerGBC(int x, int y, int w, int h) {

			GridBagConstraints gbc = new GridBagConstraints();

			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.NORTHWEST;
			gbc.weightx = 1.0d;
			gbc.weighty = 1.0d;
			gbc.insets = new Insets(15, 15, 0, 15);

			gbc.gridx = x;
			gbc.gridy = y;
			gbc.gridwidth = w;
			gbc.gridheight = h;

			return gbc;
		}

		/**
		 *
		 */
		private void initDelimPanel(JPanel p, boolean isBorder) {
			p.setLayout(new GridBagLayout());
			if (isBorder) {
				Border border = BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),
						new EmptyBorder(3, 3, 3, 3));
				p.setBorder(border);
			}
		}

		/**
		 *
		 */
		private GridBagConstraints delimPanelGBC(double wx, double wy, int x, int y, int w, int h) {

			GridBagConstraints gbc = new GridBagConstraints();

			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.NORTHWEST;
			gbc.weightx = wx;
			gbc.weighty = wy;
			gbc.insets = new Insets(2, 2, 2, 2);

			gbc.gridx = x;
			gbc.gridy = y;
			gbc.gridwidth = w;
			gbc.gridheight = h;

			return gbc;
		}

		/**
		 *
		 */
		private JPanel newLabelPanel(String label, String tooltip, int size, int labelIndent) {
			for (int i=0; i<labelIndent; i++) {
				label = "&nbsp;" + label;
			}
			JLabel l = new JLabel("<html>" + label + "</html>");

			switch (size) {
				case LABEL_SIZE_L:
					l.setPreferredSize(new Dimension(110, 20));
					l.setMinimumSize(new Dimension(110, 20));
					break;
				case LABEL_SIZE_M:
					l.setPreferredSize(new Dimension(85, 20));
					l.setMinimumSize(new Dimension(85, 20));
					break;
				case LABEL_SIZE_S:
					l.setPreferredSize(new Dimension(45, 20));
					l.setMinimumSize(new Dimension(45, 20));
					break;
				default:
					break;
			}

			if (tooltip != null) {
				l.setToolTipText("<html>" + tooltip + "</html>");
			}

			JPanel p = new JPanel();
			p.setLayout(new GridBagLayout());

			GridBagConstraints gbc = new GridBagConstraints();

			gbc.fill = GridBagConstraints.NONE;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.weightx = 0.0d;
			gbc.weighty = 0.0d;
			gbc.insets = new Insets(2, 2, 2, 2);

			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;

			p.add(l, gbc);

			return p;
		}

		/**
		 *
		 */
		private JPanel wrappTextPanel(JTextField t) {
			JPanel p = new JPanel();
			p.setLayout(new GridBagLayout());

			GridBagConstraints gbc = new GridBagConstraints();

			gbc.fill = GridBagConstraints.NONE;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.weightx = 0.0d;
			gbc.weighty = 0.0d;
			gbc.insets = new Insets(2, 2, 2, 2);

			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;

			p.add(t, gbc);

			return p;
		}

		/**
		 *
		 */
		private void initItemPanel(JPanel p) {
			p.setLayout(new GridBagLayout());
		}

		/**
		 *
		 */
		private GridBagConstraints itemPanelGBC(double wx, double wy, int x, int y, int w, int h) {

			GridBagConstraints gbc = new GridBagConstraints();

			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.NORTHWEST;
			gbc.weightx = wx;
			gbc.weighty = wy;
			gbc.insets = new Insets(2, 2, 2, 2);

			gbc.gridx = x;
			gbc.gridy = y;
			gbc.gridwidth = w;
			gbc.gridheight = h;

			return gbc;
		}
	}

	/**
	 *
	 */
	class LmFileListener implements ListSelectionListener {
		/**
		 *
		 */
		public void valueChanged(ListSelectionEvent le) {
			if ( le.getValueIsAdjusting() ) {
				return;
			}
			int selectRowIndex = queryFileTable.getSelectedRow();
			if ( selectRowIndex < 0 ) {
				reset();
				return;
			}
			SearchApplet.this.setCursor(waitCursor);

			int colIndexNo = queryFileTable.getColumnModel().getColumnIndex(COL_LABEL_NO);
			int colIndexName = queryFileTable.getColumnModel().getColumnIndex(COL_LABEL_NAME);
			String peakString = queryFilePeaks[selectRowIndex];
			String name = (String)queryFileTable.getValueAt(selectRowIndex, colIndexName);
			String key = String.valueOf(queryFileTable.getValueAt(selectRowIndex, colIndexNo));
			searchSpectrum(peakString, "", name, key);
		}
	}

	/**
	 *
	 */
	class LmResultListener implements ListSelectionListener {

		/**
		 *
		 */
		public void valueChanged(ListSelectionEvent le) {
			if (le.getValueIsAdjusting()) {
				return;
			}
			int[] selectRowIndexs = resultTable.getSelectedRows();
			if ( selectRowIndexs.length != 1 ) {
				resultPlot.clear();
				compPlot.setPeaks(null, 1);
				resultPlot.setPeaks(null, 0);
				setAllPlotAreaRange();
				if ( selectRowIndexs.length < 1 ) {
					return;
				}
			}

			SearchApplet.this.setCursor(waitCursor);

			int colIndexId = resultTable.getColumnModel().getColumnIndex(COL_LABEL_ID);
			int colIndexName = resultTable.getColumnModel().getColumnIndex(COL_LABEL_NAME);
			int colIndexContrb = resultTable.getColumnModel().getColumnIndex(COL_LABEL_CONTRIBUTOR);
			if ( selectRowIndexs.length == 1 ) {
				int selectRowIndex = selectRowIndexs[0];
				String id = (String)resultTable.getValueAt(selectRowIndex, colIndexId);
				String name = (String)resultTable.getValueAt(selectRowIndex, colIndexName);
				String siteName = (String)resultTable.getValueAt(selectRowIndex, colIndexContrb);
				String siteNo = "0";
				for ( int i = 0; i < siteNameList.length; i++ ) {
					if ( siteName.equals(siteNameList[i]) ) {
						siteNo = Integer.toString(i);
						break;
					}
				}
				GetRecordInfoInvoker inv2 = new GetRecordInfoInvoker(SearchApplet.this.baseUrl, new String[]{id}, siteNo);
				try {
					inv2.invoke();
				}
				catch ( SocketTimeoutException se ) {
					JOptionPane.showMessageDialog(null, "Server error(3): Timeout", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				Map<String, RecordInfo> results = inv2.getResults();
				RecordInfo info = results.get(id);
				String peakString = info.getPeaks();
				String precursor  = info.getPrecursor();

				Peak peak = new Peak(peakString);
				resultPlot.clear();
				resultPlot.setPeaks(peak, 0);
				resultPlot.setSpectrumInfo(name, id, precursor, PeakPanel.SP_TYPE_RESULT, false);
				compPlot.setPeaks(peak, 1);
				setAllPlotAreaRange();
				compPlot.setTolerance(String.valueOf(TOLERANCE), tolUnit1.isSelected());
			}
			SearchApplet.this.setCursor(Cursor.getDefaultCursor());
		}
	}

	/**
	 *
	 */
	class LmQueryDbListener implements ListSelectionListener {
		/**
		 *
		 */
		public void valueChanged(ListSelectionEvent le) {
			if ( le.getValueIsAdjusting() ) {
				return;
			}
			int selectRowIndex = queryDbTable.getSelectedRow();
			if ( selectRowIndex < 0 ) {
				reset();
				return;
			}
			SearchApplet.this.setCursor(waitCursor);

			int colIndexId = queryDbTable.getColumnModel().getColumnIndex(COL_LABEL_ID);
			int colIndexName = queryDbTable.getColumnModel().getColumnIndex(COL_LABEL_NAME);

			int nameListIndex = -1;
			if ( !querySorter.isSorting() ) {
				nameListIndex = selectRowIndex;
			}
			else {
				String tmpId = (String)queryDbTable.getValueAt(selectRowIndex, colIndexId);
				for ( int i = 0; i < nameList.size(); i++ ) {
					if ( nameList.get(i)[0].equals(tmpId) ) {
						nameListIndex = i;
						break;
					}
				}
			}
			String[] vals = (String[])nameList.get(nameListIndex);
			String id = vals[0];
			String site = vals[2];

			GetRecordInfoInvoker inv = new GetRecordInfoInvoker(SearchApplet.this.baseUrl, new String[]{id}, site);
			try {
				inv.invoke();
			}
			catch ( SocketTimeoutException se ) {
				JOptionPane.showMessageDialog(null, "Server error(4): Timeout", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			Map<String, RecordInfo> results = inv.getResults();
			RecordInfo info = results.get(id);
			String peakString = info.getPeaks();
			String precursor = info.getPrecursor();
			String name = (String)queryDbTable.getValueAt(selectRowIndex, colIndexName);
			String key = (String)queryDbTable.getValueAt(selectRowIndex, colIndexId);
			searchSpectrum(peakString, precursor, name, key);
		}
	}

	/**
	 *
	 */
	class TblMouseListener extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {
			super.mouseClicked(e);
			if (SwingUtilities.isLeftMouseButton(e)) {

				JTable tbl = (JTable)e.getSource();

				if (e.getClickCount() == 2 && !tbl.equals(queryFileTable)) {
					showRecordPage(tbl);
				}
				else if (e.getClickCount() == 1) {

					if (e.isShiftDown() || e.isControlDown()) {
						return;
					}

					int selRow[] = tbl.getSelectedRows();
					int idCol = tbl.getColumnModel().getColumnIndex(COL_LABEL_ID);
				}
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			super.mouseReleased(e);
			if (SwingUtilities.isRightMouseButton(e)) {
				recListPopup(e);
			}
		}
	}

	/**
	 *
	 */
	class PaneMouseListener extends MouseAdapter {

		@Override
		public void mouseReleased(MouseEvent e) {
			super.mouseReleased(e);
			if (SwingUtilities.isRightMouseButton(e)) {
				recListPopup(e);
			}
		}
	}

	/**
	 *
	 */
	class BtnAllListener implements ActionListener {

		/**
		 *
		 */
		public void actionPerformed(ActionEvent e) {
			JButton btn = btnAll;
			Color defColor = btn.getBackground();
			btn.setBackground(Color.PINK);
			btn.update(btn.getGraphics());

			SearchApplet.this.setCursor(waitCursor);

			if (nameListAll.size() == 0) {
				getSpectrumForQuery("");
				nameListAll = new ArrayList(nameList);
			}
			else {
				reset();
				nameList = new ArrayList(nameListAll);
				try {
					DefaultTableModel dataModel = (DefaultTableModel) querySorter.getTableModel();
					queryDbTable.clearSelection();
					dataModel.setRowCount(0);
					for (int i = 0; i < nameListAll.size(); i++) {
						String[] item = (String[]) nameListAll.get(i);
						String id = item[0];
						String name = item[1];
						String site = siteNameList[Integer.parseInt(item[2])];
						String[] idNameSite = new String[] { id, name, site, String.valueOf(i + 1) };
						dataModel.addRow(idNameSite);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			SearchApplet.this.setCursor(Cursor.getDefaultCursor());
			btn.setBackground(defColor);
		}
	}

	/**
	 *
	 */
	class PopupShowRecordListener implements ActionListener {
		private JTable eventTbl;

		/**
		 *
		 */
		public PopupShowRecordListener(JTable eventTbl) {
			this.eventTbl = eventTbl;
		}

		/**
		 *
		 */
		public void actionPerformed(ActionEvent e) {
			showRecordPage(eventTbl);
		}
	}

	/**
	 *
	 */
	class PopupMultipleDisplayListener implements ActionListener {
		private JTable eventTbl;
		/**
		 *
		 */
		public PopupMultipleDisplayListener(JTable eventTbl) {
			this.eventTbl = eventTbl;
		}

		/**
		 *
		 */
		public void actionPerformed(ActionEvent e) {
			int selRows[] = eventTbl.getSelectedRows();
			int idCol = eventTbl.getColumnModel().getColumnIndex(SearchApplet.COL_LABEL_ID);
			String ids = "";
			for ( int i = 0; i < selRows.length; i++ ) {
				int row = selRows[i];
				String id = (String)eventTbl.getValueAt(row, idCol);
				ids += id;
				if ( i < selRows.length - 1 ) {
					ids += ",";
				}
			}
			String url = SearchApplet.toolsUrl + "MultipleDisplay.jsp?id=" + ids;
			try {
				context.showDocument(new URL(url), "_blank");
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 *
	 */
	class PeakComparator implements Comparator<Object> {
		public int compare(Object o1, Object o2) {
			String mz1 = String.valueOf(o1).split("\t")[0];
			String mz2 = String.valueOf(o2).split("\t")[0];
			return Double.valueOf(mz1).compareTo(Double.valueOf(mz2));
		}
	}
}

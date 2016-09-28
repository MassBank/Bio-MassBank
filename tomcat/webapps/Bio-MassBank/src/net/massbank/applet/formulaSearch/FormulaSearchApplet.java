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
 * FormulaSearchApplet.java
 *
 * ver 1.0.0 2013.05.29
 *
 ******************************************************************************/
package net.massbank.applet.formulaSearch;

import java.applet.AppletContext;
import java.awt.Font;
import java.awt.Point;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.Timer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
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
import javax.swing.table.TableColumnModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import net.massbank.core.common.GetConfig;
import net.massbank.core.common.CoreUtil;
import net.massbank.core.common.QueryFileUtil;
import net.massbank.core.common.RecordInfo;
import net.massbank.core.common.RecordInfoList;
import net.massbank.core.common.ChemicalFormulaUtils;
import net.massbank.core.get.record.GetRecordInfoInvoker;
import net.massbank.core.get.instrument.GetInstrumentInvoker;
import net.massbank.core.get.instrument.GetInstrumentResults;
import net.massbank.core.get.record.GetRecordTitleInvoker;
import net.massbank.tools.search.spectrum.SpectrumSearchParameter;
import net.massbank.tools.search.spectrum.SpectrumSearchInvoker;
import net.massbank.tools.search.spectrum.SpectrumSearchResult;
import net.massbank.applet.common.Peak;
import net.arnx.jsonic.JSON;


/**
 * Formula Search Applet
 */
@SuppressWarnings("serial")
public class FormulaSearchApplet extends JApplet {

	public static String baseUrl = "";
	private static String toolsUrl = "";
	private static int PRECURSOR = -1;
	private static int TOLERANCE = 50;
	public static int CUTOFF_THRESHOLD = 5;
	public static final int LEFT_PANEL_WIDTH = 430;

	private static final int TAB_ORDER_DB = 0;
	private static final int TAB_ORDER_FILE = 1;
	private static final int TAB_RESULT_DB = 0;
	public static final String TABLE_QUERY_FILE = "QueryFile";
	public static final String TABLE_QUERY_DB = "QueryDb";
	public static final String TABLE_RESULT = "Result";

	private TableSorter fileSorter = null;
	private TableSorter querySorter = null;
	private TableSorter resultSorter = null;
	private JTable queryFileTable = null;
	private JTable queryDbTable = null;
	private JTable resultTable = null;
	private JPanel paramPanel = new JPanel();
	private DefaultTableModel dtmQueryDbTbl   = null;
	private DefaultTableModel dtmQueryFileTbl = null;
	private DefaultTableModel dtmResultTbl    = null;
	private JTabbedPane queryTabPane = new JTabbedPane();
	private JTabbedPane resultTabPane = new JTabbedPane();
	private JScrollPane queryFilePane = null;
	private JScrollPane resultPane = null;
	private JScrollPane queryDbPane = null;
	private JScrollPane plotPane = null;
	private ParentPanelExt parentPane = null;
	private JButton btnAll = new JButton("Get all data");
	private JLabel hitLabel = new JLabel(" ");
	private ArrayList<String[]> nameList = new ArrayList<String[]>();
	private ArrayList nameListAll = new ArrayList();
	private JPanel parentPanel2 = null;
	public static String[] siteNameList;

	private static final Cursor waitCursor = new Cursor(Cursor.WAIT_CURSOR);
	public static AppletContext context = null;
	public static int initAppletWidth = 0;
	public static int initAppletHight = 0;
	private static final int MAX_DISPLAY_NUM = 30;
	private static final String COOKIE_TOL = "TOL";
	private static final String COOKIE_CUTOFF = "CUTOFF";

	private CookieManager cm = null;
	private ProgressDialog dlg = null;
	private String[] queryFilePeaks = null;
	private static String queryPeakString = "";

	private JPanel mainPanel = new JPanel();
	private JPanel queryPanel = new JPanel();
	private JPanel dbPanel = new JPanel();

	public long lastClickedTime = 0;
	public Timer timer = null;
	public double xscale = 0;
	public double yscale = 0;
	public double massStart = 0;
	public double massRange = 0;
	public int massRangeMax = 0;
	public int intensityRange = PlotPanelExt.INTENSITY_MAX;
	public static boolean showAll = false;
	public boolean underDrag = false;
	public Point fromPos = null;
	public Point toPos = null;
	public static JRadioButton rbtnPos = null;
	public JRadioButton rbtnNeg = null;
	public JTextField tolField = null;
	public JTextField cutoffField = null;
	public List<Map> searchResults = null;
	public boolean[][] isMatched = null;

	/**
	 * メインプログラム
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
		createWindow();
		this.dlg = new ProgressDialog(getFrame());
		if ( getParameter("file") != null ) {
			loadQueryFile(getParameter("file"));
		}
	}

	/**
	 * ウインドウ生成
	 */
	private void createWindow() {
		ToolTipManager ttm = ToolTipManager.sharedInstance();
		ttm.setInitialDelay(50);
		ttm.setDismissDelay(8000);

		this.mainPanel = new JPanel();
		this.mainPanel.setLayout(new BorderLayout());
		Border border = BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(), new EmptyBorder(1, 1, 1, 1));
		this.mainPanel.setBorder(border);
		this.dbPanel.setLayout(new BorderLayout());

		createQueryFileTable();
		createParamaterPanel();
		createResultTable();
		createQueryDbTable();

		int height = initAppletHight / 2;
		this.parentPane = new ParentPanelExt(this);
		JSplitPane jsp1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, this.queryPanel, this.parentPane);
		jsp1.setDividerLocation(height);
		int divideSize = (int)(initAppletWidth * 0.4);
		if (divideSize > 550) {
			divideSize = 550;
		}
		jsp1.setDividerLocation(divideSize);
		jsp1.setOneTouchExpandable(true);

		JSplitPane jsp2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, jsp1, this.resultTabPane);
		jsp2.setDividerLocation(height);
		jsp2.setMinimumSize(new Dimension(180, 0));
		jsp2.setOneTouchExpandable(true);

		this.mainPanel.add(jsp2, BorderLayout.CENTER);
		add(this.mainPanel);
	}

	/**
	 * Query DB表示テーブル作成
	 */
	private void createQueryDbTable() {
		this.dtmQueryDbTbl = new DefaultTableModel();
		querySorter = new TableSorter(this.dtmQueryDbTbl, TABLE_QUERY_DB);
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

		String[] colNames = { "No.", "Name", "ID", "Contrb" };
		int[] colWidths = { 30, 300, 90, 70 };
		dtmQueryDbTbl.setColumnIdentifiers(colNames);
		DefaultTableColumnModel colModel = (DefaultTableColumnModel)queryDbTable.getColumnModel();
		DefaultTableCellRenderer ren = new DefaultTableCellRenderer();
		ren.setHorizontalAlignment(JLabel.CENTER);
		colModel.getColumn(0).setCellRenderer(ren);
		for ( int i = 0; i < colWidths.length; i++ ) {
			colModel.getColumn(i).setPreferredWidth(colWidths[i]);
		}

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

		this.queryPanel = new JPanel();
		this.queryPanel.setLayout(new BorderLayout());
		this.queryPanel.add(queryTabPane, BorderLayout.CENTER);
		this.queryPanel.add(paramPanel, BorderLayout.SOUTH);
		this.queryPanel.setMinimumSize(new Dimension(0, 170));

		JPanel jtp2Panel = new JPanel();
		jtp2Panel.setLayout(new BorderLayout());
		jtp2Panel.add(this.dbPanel, BorderLayout.CENTER);
		jtp2Panel.add(hitLabel, BorderLayout.SOUTH);
		jtp2Panel.setMinimumSize(new Dimension(0, 70));
		Color colorGreen = new Color(0, 128, 0);
		hitLabel.setForeground(colorGreen);

		this.resultTabPane.addTab("Result", jtp2Panel);
		this.resultTabPane.setToolTipTextAt(TAB_RESULT_DB, "Result of DB hit.");
		this.resultTabPane.setFocusable(false);
	}

	/**
	 * Query File表示テーブル作成
	 */
	private void createQueryFileTable() {
		this.dtmQueryFileTbl = new DefaultTableModel();
		fileSorter = new TableSorter(this.dtmQueryFileTbl, TABLE_QUERY_FILE);
		queryFileTable = new JTable(fileSorter);
		queryFileTable.addMouseListener(new TblMouseListener());
		fileSorter.setTableHeader(queryFileTable.getTableHeader());
		queryFileTable.setRowSelectionAllowed(true);
		queryFileTable.setColumnSelectionAllowed(false);
		queryFileTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		String[] colNames = { "No.", "Name", "ID" };
		int[] colWidths = { 30, 330, 90 };
		dtmQueryFileTbl.setColumnIdentifiers(colNames);
		DefaultTableColumnModel colModel = (DefaultTableColumnModel)queryFileTable.getColumnModel();
		DefaultTableCellRenderer ren = new DefaultTableCellRenderer();
		ren.setHorizontalAlignment(JLabel.CENTER);
		colModel.getColumn(0).setCellRenderer(ren);
		colModel.getColumn(2).setCellRenderer(ren);
		for ( int i = 0; i < colWidths.length; i++ ) {
			colModel.getColumn(i).setPreferredWidth(colWidths[i]);
		}
		ListSelectionModel lm = queryFileTable.getSelectionModel();
		lm.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lm.addListSelectionListener(new LmFileListener());
		queryFilePane = new JScrollPane(queryFileTable);
		queryFilePane.addMouseListener(new PaneMouseListener());
		queryFilePane.setPreferredSize(new Dimension(300, 300));
	}

	/**
	 * Resultテーブル作成
	 */
	private void createResultTable() {
		this.dtmResultTbl = new DefaultTableModel();
		resultSorter = new TableSorter(this.dtmResultTbl, TABLE_RESULT);
		resultTable = new JTable(resultSorter) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		resultTable.addMouseListener(new TblMouseListener());
		resultSorter.setTableHeader(resultTable.getTableHeader());
		DefaultTableModel dtmResultTbl = (DefaultTableModel)resultSorter.getTableModel();
		resultPane = new JScrollPane(resultTable);
		resultPane.addMouseListener(new PaneMouseListener());
		resultTable.setRowSelectionAllowed(true);
		resultTable.setColumnSelectionAllowed(false);
		resultTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		ListSelectionModel lm2 = resultTable.getSelectionModel();
		lm2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lm2.addListSelectionListener(new LmResultListener());

		resultPane.setPreferredSize(new Dimension(LEFT_PANEL_WIDTH, 200));
		this.dbPanel.add(resultPane, BorderLayout.CENTER);
	}

	/**
	 * パラメータ入力パネル
	 */
	private void createParamaterPanel() {
		// Tolerance
		JLabel label1 = new JLabel("       Tolerance(ppm) ");
		tolField = new JTextField(String.valueOf(TOLERANCE), 5);
		tolField.setMaximumSize(new Dimension(50, 20));
		tolField.setHorizontalAlignment(JTextField.RIGHT);
		tolField.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if ( e.getKeyCode() == KeyEvent.VK_ENTER ) {
					int ppm = Integer.parseInt(tolField.getText());
					if ( ppm != TOLERANCE ) {
						TOLERANCE = ppm;
						searchSpectrum();
					}
				}
			}
		});
		paramPanel.add(label1);
		paramPanel.add(tolField);

		// Cutoff Thresholds
		JPanel cutoffPanel = new JPanel();
		JLabel label3 = new JLabel("           Cutoff ");
		cutoffField = new JTextField(String.valueOf(CUTOFF_THRESHOLD), 5);
		cutoffField.setMaximumSize(new Dimension(40, 20));
		cutoffField.setHorizontalAlignment(JTextField.RIGHT);
		cutoffField.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if ( e.getKeyCode() == KeyEvent.VK_ENTER ) {
					int cutoff = Integer.parseInt(cutoffField.getText());
					if ( cutoff != CUTOFF_THRESHOLD ) {
						CUTOFF_THRESHOLD = cutoff;
						searchSpectrum();
					}
				}
			}
		});

		paramPanel.add(label3);
		paramPanel.add(cutoffField);

		// Ion Mode
		JPanel ionPanel = new JPanel();
		JLabel label4 = new JLabel("           ");
		ButtonGroup btnGroupIon = new ButtonGroup();
		rbtnPos = new JRadioButton("Positive", true);
		rbtnNeg = new JRadioButton("Negative");
		rbtnPos.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				searchSpectrum();
			}
		});
		rbtnNeg.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				searchSpectrum();
			}
		});
		btnGroupIon.add(rbtnPos);
		btnGroupIon.add(rbtnNeg);
		paramPanel.add(label4);
		paramPanel.add(rbtnPos);
		paramPanel.add(rbtnNeg);
		paramPanel.setLayout(new BoxLayout(paramPanel, BoxLayout.X_AXIS));
	}

	/*
	 *
	 */
	private void reset() {
		if ( this.resultTabPane.getTabCount() > 0 ) {
			this.resultTabPane.setSelectedIndex(0);
		}
		this.dtmResultTbl.setRowCount(0);
		hitLabel.setText(" ");
	}

	/**
	 * ファイル読み込み処理
	 */
	private void loadQueryFile(String fileName) {
		DefaultTableModel dtm = (DefaultTableModel)fileSorter.getTableModel();
		dtm.setRowCount(0);
		try {
			URL url = new URL(this.toolsUrl + "SpectrumSearch.jsp?file=" + fileName);
			QueryFileUtil qf = new QueryFileUtil(url);
			String[] names = qf.getQueryNames();
			DecimalFormat df = new DecimalFormat("000000");
			for ( int i = 0; i < names.length; i++ ) {
				String num = String.valueOf(i+1);
				String id = "US" + df.format(i+1);
				dtm.addRow(new Object[]{num, names[i], id });
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
	 * DB検索
	 */
	private void searchSpectrum() {
		reset();
		setOperationEnbled(false);
		dlg.setVisible(true);

		SwingWorker worker = new SwingWorker() {
			public Object construct() {
				return null;
			}
			public void finished() {
				setOperationEnbled(true);
				dlg.setVisible(false);
				String ppm = tolField.getText();
				String cutoff = cutoffField.getText();
				String ionMode = "-1";
				if ( FormulaSearchApplet.rbtnPos.isSelected() ) {
					ionMode = "1";
				}

				int total = 0;
				String reqUrl = toolsUrl + "FSpectrumSearchResult.jsp";
				String reqParam = "peaks=" + FormulaSearchApplet.queryPeakString + "&ppm=" + ppm + "&cutoff=" + cutoff + "&ion_mode=" + ionMode;
				try {
					URL url = new URL(reqUrl + "?" + reqParam);
					HttpURLConnection con = (HttpURLConnection)url.openConnection();
					con.setDoOutput(true);
					con.setConnectTimeout(5000);
					con.setReadTimeout(60000);
					InputStreamReader is = new InputStreamReader(con.getInputStream(), "UTF-8");
					BufferedReader br = new BufferedReader(is);
					boolean isStartSpace = true;
					String line = "";
					StringBuilder sb = new StringBuilder("");
					while ( (line = br.readLine()) != null ) {
						sb.append(line);
					}
					br.close();
					con.disconnect();
					String res = sb.toString();
					searchResults = (List)JSON.decode(res, List.class);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				if ( searchResults != null ) {
					total = searchResults.size() - 1;
					setResultTable();
				}

				Peak peak = new Peak(FormulaSearchApplet.queryPeakString);
				FormulaSearchApplet.this.parentPane.setPeak(peak);
				if ( total > 0 ) {
					resultTable.setRowSelectionInterval(1,1);
				}
				FormulaSearchApplet.this.setCursor(Cursor.getDefaultCursor());

				String msg = " " + total + " Hit.    ("
						+ "Tolerance : " + TOLERANCE + " ppm"
						+ ", Cutoff threshold : " + CUTOFF_THRESHOLD + ")";
				hitLabel.setText(msg);
				hitLabel.setToolTipText(msg);
			}
		};
		worker.start();
	}


	/**
	 * クエリーの選択状態を更新
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
	private void getSpectrumForQuery() {
		String param = "";
		this.dtmQueryDbTbl.setRowCount(0);
		GetRecordTitleInvoker inv = new GetRecordTitleInvoker(this.baseUrl);
		try {
			inv.invoke();
		}
		catch ( SocketTimeoutException se ) {
			JOptionPane.showMessageDialog(null, "Server error(2): Timeout", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		RecordInfoList infoList = inv.getResults();
		int num = infoList.getListSize();
		if ( num == 0 ) {
			return;
		}
		this.nameList.clear();
		for ( int i = 0; i < num; i++ ) {
			RecordInfo info = infoList.getRecordInfo(i);
			String id     = info.getId();
			String title  = info.getRecordTitle();
			String siteNo = info.getSiteNo();
			String[] vals = { id, title, siteNo };
			this.nameList.add(vals);
			String siteName = siteNameList[Integer.parseInt(siteNo)];
			this.dtmQueryDbTbl.addRow(new String[]{ String.valueOf(i+1), title, id, siteName });
		}
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
		btnAll.setEnabled(value);
		queryTabPane.setEnabled(value);
		resultTabPane.setEnabled(value);
	}

	/**
	 * 
	 */
	private void setResultTable() {
		List<String> queryMzList = new ArrayList();
		List<String> queryFormulaList = new ArrayList();
		List<String> queryFormulaList2 = new ArrayList();
		String[] vals1 = { "No.", "Name", "Formula", "Exact Mass", "Hit" };
		String[] vals2 = { "", "", "", "", "" };
		for ( int i = 0; i < vals1.length; i++ ) {
			queryMzList.add(vals1[i]);
			queryFormulaList2.add(vals2[i]);
		}
		Map mapQueryInfo = searchResults.get(0);
		for ( Iterator it = mapQueryInfo.keySet().iterator(); it.hasNext(); ) {
			String mz = (String)it.next();
			String formula = (String)mapQueryInfo.get(mz);
			queryMzList.add(String.valueOf(mz));
			queryFormulaList.add(formula);
			queryFormulaList2.add(formula);
		}
		String[] queryMzs = queryMzList.toArray(new String[]{});
		String[] queryFormulas = queryFormulaList2.toArray(new String[]{});

		DefaultTableModel dtmResultTbl = (DefaultTableModel)resultSorter.getTableModel();
		dtmResultTbl.setRowCount(0);
		dtmResultTbl.setColumnIdentifiers(queryMzs);
		dtmResultTbl.addRow(queryFormulas);

		String cname     = "";
		String emass     = "";
		String cformula  = "";
		String hit       = "";
		String matchedFormula = "";
		int no = 1;
		this.isMatched = new boolean[searchResults.size()][queryFormulaList.size()+6];
		for ( int i = 1; i < searchResults.size(); i++ ) {
			Map mapResults = searchResults.get(i);
			for ( Iterator it = mapResults.keySet().iterator(); it.hasNext(); ) {
				String key = (String)it.next();
				String val = String.valueOf(mapResults.get(key));
				if      ( key.equals("cname") )    { cname = val; }
				else if ( key.equals("emass") )    { emass = val; }
				else if ( key.equals("cformula") ) { cformula = val; }
				else if ( key.equals("hit") )      { hit = val; }
				else if ( key.equals("matchedFormula") ) { matchedFormula = val; }
			}
			String ionMode = "Positive";
			if ( !FormulaSearchApplet.rbtnPos.isSelected() ) {
				ionMode = "Negative";
			}
			List rowDataList = new ArrayList();
			rowDataList.add(no++);
			rowDataList.add(cname);
			rowDataList.add(cformula);
			rowDataList.add(emass);
			rowDataList.add(hit);

			String[] matchedFormulas = matchedFormula.split(",");
			if ( matchedFormulas.length > 0 ) {
				for ( int j = 0; j < queryFormulaList.size(); j++ ) {
					String queryFormula = queryFormulaList.get(j);
					String val = "";
					int row = i;
					int col = j + 5;
					this.isMatched[row][col] = false;
					for ( String formula : matchedFormulas ) {
						if ( queryFormula.equals(formula) ) {
							val = "matching";
							this.isMatched[row][col] = true;
							break;
						}
					}
					rowDataList.add(val);
				}
				dtmResultTbl.addRow(rowDataList.toArray(new Object[]{}));
			}
		}

		DefaultTableColumnModel colModel = (DefaultTableColumnModel)resultTable.getColumnModel();
		ResultTableRenderer ren = new ResultTableRenderer();
		int[] colWidths = { 30, 220, 90, 80, 30 };
		for ( int i = 0; i < queryMzs.length; i++ ) {
			TableColumn tcol = colModel.getColumn(i);
			int width = 80;
			if ( i < colWidths.length ) {
				width = colWidths[i];
			}
			tcol.setPreferredWidth(width);
			tcol.setCellRenderer(ren);
		}
	}

	/**
	 * 
	 * 
	 */
	class ResultTableRenderer extends DefaultTableCellRenderer {
		public Component getTableCellRendererComponent(
				JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column ){
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if ( table == resultTable ) {
				if ( row == 0 && column >= 5 ) {
					setBackground(Color.BLUE);
					setForeground(Color.WHITE);
				}
				else if ( FormulaSearchApplet.this.isMatched[row][column] ) {
					setBackground(Color.ORANGE );
					setForeground(Color.BLACK);
				}
				else if ( (row > 0 && !isSelected) || (row == 0 && column >=0 && column <= 5 ) ) {
					setBackground(Color.WHITE);
					setForeground(Color.BLACK);
				}
				if ( column != 1 && column != 2 ) {
					setHorizontalAlignment(JLabel.CENTER);
				}
				else {
					setHorizontalAlignment(JLabel.LEFT);
				}
			}
			return this;
		}
	}

	/**
	 * Fileタブのテーブルリストモデルリスナークラス
	 */
	class LmFileListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent le) {
			if ( le.getValueIsAdjusting() ) {
				return;
			}
			int selectRowIndex = queryFileTable.getSelectedRow();
			if ( selectRowIndex < 0 ) {
				reset();
				return;
			}
			FormulaSearchApplet.this.setCursor(waitCursor);

			TableColumnModel tcModel = queryFileTable.getColumnModel();
			int colIndexNo = tcModel.getColumnIndex("No.");
			int colIndexName = tcModel.getColumnIndex("Name");
			FormulaSearchApplet.queryPeakString = queryFilePeaks[selectRowIndex];
			String name = (String)queryFileTable.getValueAt(selectRowIndex, colIndexName);
			String key = String.valueOf(queryFileTable.getValueAt(selectRowIndex, colIndexNo));
			searchSpectrum();
		}
	}

	/**
	 * Resultタブのテーブルリストモデルリスナークラス
	 */
	class LmResultListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent le) {
			if ( le.getValueIsAdjusting() ) {
				return;
			}
			int row = resultTable.getSelectedRow();
			if ( row == -1 ) {
				return;
			}
			FormulaSearchApplet.this.setCursor(waitCursor);
			Map result = searchResults.get(row);
			String ppm = tolField.getText();
			FormulaSearchApplet.this.parentPane.setResultInfo(result, Integer.parseInt(ppm));
			FormulaSearchApplet.this.setCursor(Cursor.getDefaultCursor());
		}
	}

	/**
	 * クエリDBタブのテーブルリストモデルリスナークラス
	 */
	class LmQueryDbListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent le) {
			if ( le.getValueIsAdjusting() ) {
				return;
			}
			int selectRowIndex = queryDbTable.getSelectedRow();
			if ( selectRowIndex < 0 ) {
				reset();
				return;
			}
			FormulaSearchApplet.this.setCursor(waitCursor);

			int colIndexId = queryDbTable.getColumnModel().getColumnIndex("ID");
			int colIndexName = queryDbTable.getColumnModel().getColumnIndex("Name");

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
			String siteNo = vals[2];

			GetRecordInfoInvoker inv = new GetRecordInfoInvoker(FormulaSearchApplet.this.baseUrl, new String[]{id}, siteNo);
			try {
				inv.invoke();
			}
			catch ( SocketTimeoutException se ) {
				JOptionPane.showMessageDialog(null, "Server error(4): Timeout", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			Map<String, RecordInfo> results = inv.getResults();
			RecordInfo info = results.get(id);
			FormulaSearchApplet.queryPeakString = info.getPeaks();
			String precursor = info.getPrecursor();
			String name = (String)queryDbTable.getValueAt(selectRowIndex, colIndexName);
			String key = (String)queryDbTable.getValueAt(selectRowIndex, colIndexId);
			searchSpectrum();
		}
	}

	/**
	 * テーブルマウスリスナークラス
	 */
	class TblMouseListener extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {
			super.mouseClicked(e);
			if (SwingUtilities.isLeftMouseButton(e)) {
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			super.mouseReleased(e);
			if (SwingUtilities.isRightMouseButton(e)) {
			}
		}
	}

	/**
	 * ペインマウスリスナー
	 */
	class PaneMouseListener extends MouseAdapter {
		@Override
		public void mouseReleased(MouseEvent e) {
			super.mouseReleased(e);
			if (SwingUtilities.isRightMouseButton(e)) {
			}
		}
	}

	/**
	 * Allボタンリスナークラス
	 */
	class BtnAllListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JButton btn = btnAll;
			Color defColor = btn.getBackground();
			btn.setBackground(Color.PINK);
			btn.update(btn.getGraphics());
			FormulaSearchApplet.this.setCursor(waitCursor);
			if ( nameListAll.size() == 0 ) {
				getSpectrumForQuery();
				nameListAll = new ArrayList(nameList);
			}
			else {
				reset();
				nameList = new ArrayList(nameListAll);
				try {
					queryDbTable.clearSelection();
					dtmQueryDbTbl.setRowCount(0);
					for (int i = 0; i < nameListAll.size(); i++) {
						String[] item = (String[]) nameListAll.get(i);
						String id = item[0];
						String name = item[1];
						String site = siteNameList[Integer.parseInt(item[2])];
						String[] vals = new String[] { String.valueOf(i + 1), name, id };
						dtmQueryDbTbl.addRow(vals);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			FormulaSearchApplet.this.setCursor(Cursor.getDefaultCursor());
			btn.setBackground(defColor);
		}
	}
}

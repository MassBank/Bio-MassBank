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
 * ParentPanelExt.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.applet.formulaSearch;

import java.net.URL;
import java.util.TreeSet;
import java.util.Iterator;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.TreeMap;
import java.util.ArrayList;
import java.math.BigDecimal;
import javax.swing.JApplet;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Component;
import javax.swing.border.LineBorder;
import net.massbank.core.common.RecordInfo;
import net.massbank.core.common.ChemicalFormulaUtils;
import net.massbank.applet.common.Peak;
import net.massbank.applet.common.MolViewPanelExt;


public class ParentPanelExt extends JPanel
{
	public static int RIGHT_PANEL_WIDTH = 220;
	private RecordInfo info = null;
	private FormulaSearchApplet applet = null;
	private PlotPanelExt plotPane = null;
	private JPanel leftPane = null;
	private JPanel rightPane = null;
	private JPanel infoPane = new JPanel();
	private JLabel nameLabel = new JLabel("");
	private JLabel[] infoLabels = null;
	private JLabel annoLabel = new JLabel("");
	private String[] LEBELS = { "  Formula", "", "  Exact Mass", "", "", "", "", "" };
	private int panelHeight = 0;
	private Color colorMintCream = new Color(0xF5, 0xFF, 0xFA);
	private JButton[] btnDbIDs = new JButton[2];

	/*
	 *
	 */
	public ParentPanelExt(FormulaSearchApplet applet) {
		this.applet = applet;
		this.panelHeight = applet.initAppletHight / 2;
		this.leftPane = createLeftPanel();
		this.rightPane = createRightPanel();
		add(this.leftPane);
		add(this.rightPane);
		setLayout( new BoxLayout(this, BoxLayout.X_AXIS) );
	}

	/*
	 *
	 */
	public void setPeak(Peak peak) {
		this.plotPane.setPeak(peak);
	}

	/*
	 *
	 */
	public void setResultInfo(Map result, int ppm) {
		if ( result == null ) {
			return;
		}
		String cid = (String)result.get("cid");
		String cids = (String)result.get("cids");
		String cname = (String)result.get("cname");
		String cformula = (String)result.get("cformula");
		String emass  = (String)result.get("emass");
		String molfile = (String)result.get("molfile");
		String annotatedFormula = (String)result.get("annotatedFormula");
		String matchedFormula = (String)result.get("matchedFormula");
		String[] annotatedFormulas = annotatedFormula.split(",");
		String[] matchedFormulas = matchedFormula.split(",");

		String[] dbIDs = cids.split(",");
		this.nameLabel.setText(cname);
		for ( int i = 0; i < LEBELS.length; i++ ) {
			this.infoLabels[i].setText(LEBELS[i]);
		}
		this.infoLabels[1].setText(cformula);
		this.infoLabels[3].setText(emass);
		this.btnDbIDs[0].setText("");
		this.btnDbIDs[1].setText("");
		int num1 = 4;
		int num2 = 0;
		for ( int i = 0; i < dbIDs.length; i++ ) {
			String id = dbIDs[i];
			String url = "http://kanaya.naist.jp/knapsack_jsp/information.jsp?sname=C_ID&word=" + id;
			String val1 = "  KNApSAcK ID";
			if ( id.indexOf("CS") >= 0 ) {
				id = id.replace("CS", "");
				url = "http://www.chemspider.com/" + id;
				val1 = "  ChemSpider ID";
			}
			else if ( id.length() == 6 ) {
				url = "http://www.genome.jp/dbget-bin/www_bget?";
				String first_ch = id.substring(0, 1);
				if ( first_ch.equals("C") ) {
					url += "cpd:" + id;
				}
				else {
					url += "dr:" + id;
				}
				val1 = "  KEGG ID";
			}
			if ( i == 1 ) {
				num1 = 6;
				num2 = 1;
			}
			String val2 = "<html><a href=\"" + url + "\" target=\"_blank\">" + id + "</a></html>";
			this.infoLabels[num1].setText(val1);
			this.btnDbIDs[num2].setText(val2);
			if ( i == 1 ) {
				break;
			}
		}

		String html = "<html>";
		html += "<br><b>Peak Annotation : " + String.valueOf(annotatedFormulas.length) + "</b>";
		html += "<table border=\"0\" cellspacing=\"1\" cellpadding=\"0\" bgcolor=\"#000000\">";
		html += "<tr><th width=\"85\" style=\"background-color:#000000;color:#ffffff\">Exact Mass</td><th width=\"95\" style=\"background-color:#000000;color:#ffffff\">Formula</td></tr>";

		TreeMap<Double, String> mapAnnoFormulas = new TreeMap();
		for ( int i = 0; i < annotatedFormulas.length; i++ ) {
			String aformula = annotatedFormulas[i];
			double amass = ChemicalFormulaUtils.calcExactMass(aformula);
			mapAnnoFormulas.put(amass, aformula);
		}

		Iterator it1 = mapAnnoFormulas.keySet().iterator();
		while ( it1.hasNext() ) {
			Object amass = it1.next();
			String aformula = mapAnnoFormulas.get(amass);
			html += "<tr><td style=\"background-color:#ffffff;text-align:right\">" + String.valueOf(amass) + "&nbsp;&nbsp;</td>";
			html += "<td style=\"background-color:#ffffff;text-align:left\">&nbsp;&nbsp;" + aformula + "</td></tr>";
		}
		html += "</table>";
		html += "</html>";
		this.annoLabel.setText(html);

		if ( !molfile.equals("") ) {
			JPanel molPane = (MolViewPanelExt)new MolViewPanelExt(200, true, false, this.applet, "", molfile);
			molPane.setBackground(colorMintCream);
			molPane.setMinimumSize( new Dimension(200, 200) );
			this.rightPane.remove(0);
			this.rightPane.add(molPane, 0);
		}
		this.plotPane.setMatchedFormula(matchedFormulas, ppm);
		this.rightPane.revalidate();
	}


	/*
	 *
	 */
	private JPanel createLeftPanel() {
		JPanel leftPane = new JPanel();
		OperationPanel opePane = new OperationPanel(this.applet);
		opePane.setLayout( new FlowLayout(FlowLayout.LEFT, 0, 0) );
		opePane.setMaximumSize( new Dimension(opePane.getMaximumSize().width, 100) );
		int panelWidth = applet.initAppletWidth - (applet.LEFT_PANEL_WIDTH + RIGHT_PANEL_WIDTH);
		this.plotPane = new PlotPanelExt(this.applet);
		this.plotPane.setPreferredSize(new Dimension(panelWidth, panelHeight - (opePane.getPreferredSize().height + 5)));
		leftPane.add(this.plotPane);
		leftPane.add(opePane);
		leftPane.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		Dimension dime = new Dimension(panelWidth, leftPane.getPreferredSize().height);
		leftPane.setPreferredSize(dime);
		return leftPane;
	}

	/*
	 *
	 */
	private JPanel createRightPanel() {
		JPanel rightPane = new JPanel();
		rightPane.setLayout(new BoxLayout(rightPane, BoxLayout.Y_AXIS));
		JPanel boxPane = new JPanel();
		JLabel boxLbl = new JLabel("");
		boxLbl.setPreferredSize( new Dimension(190, 190) );
		boxLbl.setBackground(new Color(0xF8,0xF8,0xFF));
		boxLbl.setBorder( new LineBorder(Color.LIGHT_GRAY, 1) );
		boxLbl.setOpaque(true);
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(1, 1, 1, 1);
		layout.setConstraints(boxLbl, gbc);
		boxPane.add(boxLbl);
		Dimension dime = new Dimension(200, 200);
		boxPane.setPreferredSize(dime);
		boxPane.setMinimumSize(dime);
		boxPane.setBackground(colorMintCream);
		rightPane.add(boxPane);

		this.infoPane = new JPanel();
		this.infoPane.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
		this.nameLabel.setForeground(Color.MAGENTA);
		this.nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
		this.nameLabel.setPreferredSize(new Dimension(200, 25) );
		this.infoPane.add(this.nameLabel);

		boolean odd = true;
		this.infoLabels = new JLabel[LEBELS.length];
		int num = 0;
		for ( int i = 0; i < LEBELS.length; i++ ) {
			this.infoLabels[i] = new JLabel();
			this.infoLabels[i].setOpaque(false);
			if ( odd ) {
				this.infoLabels[i].setForeground(Color.BLACK);
				this.infoLabels[i].setBackground(Color.WHITE);
				this.infoLabels[i].setPreferredSize( new Dimension(90, 18) );
				this.infoPane.add(this.infoLabels[i]);
				odd = false;
			}
			else {
				if ( i == 1 || i == 3 ) {
					this.infoLabels[i].setForeground(new Color(57, 127, 0));
					this.infoLabels[i].setBackground(Color.WHITE);
					this.infoLabels[i].setPreferredSize( new Dimension(100, 18) );
					this.infoPane.add(this.infoLabels[i]);
				}
				else {
					this.btnDbIDs[num] = new JButton("");
					this.btnDbIDs[num].addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							String com = ae.getActionCommand();
							try {
								int pos1 = com.indexOf("http");
								if ( pos1 >= 0 ) {
									int pos2 = com.indexOf("target");
									String url = com.substring(pos1, pos2 - 2);
									applet.getAppletContext().showDocument(new URL(url), "_blank");
								}
							}
							catch ( Exception e ) {
								e.printStackTrace();
							}
						}
					});
					this.btnDbIDs[num].setBorderPainted(false);
					this.btnDbIDs[num].setContentAreaFilled(false);
					this.btnDbIDs[num].setPreferredSize( new Dimension(90, 18) );
					this.btnDbIDs[num].setHorizontalAlignment(JButton.LEFT);
					this.btnDbIDs[num].setMargin(new Insets(0, 0, 0, 0));
					this.infoPane.add(this.btnDbIDs[num++]);
				}
				odd = true;
			}
		}
		this.infoPane.add(this.annoLabel);

		Dimension dime1 = new Dimension(RIGHT_PANEL_WIDTH - 10, 2000);
		this.infoPane.setPreferredSize(dime1);
		this.infoPane.setMaximumSize(dime1);
		this.infoPane.setBackground(colorMintCream);
		JScrollPane infoScrPane = new JScrollPane(this.infoPane);
		infoScrPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		infoScrPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		infoScrPane.setBorder(new BevelBorder(BevelBorder.RAISED));
		rightPane.add(infoScrPane);

		Dimension dime2 = new Dimension(RIGHT_PANEL_WIDTH, panelHeight);
		rightPane.setPreferredSize(dime2);
		rightPane.setMinimumSize(dime2);
		rightPane.setBackground(colorMintCream);
		return rightPane;
	}
}

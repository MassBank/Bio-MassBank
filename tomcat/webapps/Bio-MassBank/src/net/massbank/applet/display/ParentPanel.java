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
 * ParentPanel.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.applet.display;

import java.awt.Insets;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.math.BigDecimal;
import javax.swing.JApplet;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.border.LineBorder;
import net.massbank.core.common.RecordInfo;
import net.massbank.applet.common.Peak;
import net.massbank.applet.common.MolViewPanelExt;
import net.massbank.tools.search.peak.PeakSearchParameter;


public class ParentPanel extends JPanel
{
	private static final int panelWidth = 780;
	private RecordInfo info = null;
	private int index = 0;
	private PeakSearchParameter sp = null;
	private boolean isMulti = false;
	private DisplayApplet applet = null;


	/**
	 *
	 */
	public ParentPanel(RecordInfo info, PeakSearchParameter sp, DisplayApplet applet, boolean isMulti) {
		this.info = info;
		this.sp = sp;
		this.applet = applet;
		this.isMulti = isMulti;
		JPanel leftPane = createLeftPanel();
		JPanel rightPane = createRightPanel();
		add(leftPane);
		add(rightPane);
		setLayout( new BoxLayout(this, BoxLayout.X_AXIS) );
	}

	/**
	 *
	 */
	private JPanel createLeftPanel() {
		String id        = this.info.getId();
		String title     = this.info.getRecordTitle();
		String emass     = this.info.getExactMass();
		String ion       = this.info.getIonMode();
		String precursor = this.info.getPrecursor();
		String siteNo    = this.info.getSiteNo();
		String peaks     = this.info.getPeaks();

		JPanel leftPane = new JPanel();
		if ( isMulti ) {
			TitlePanel panel = new TitlePanel(title, id, siteNo);
			leftPane.add(panel);
		}
		else {
			JLabel label = new JLabel("Mass Spectrum");
			label.setOpaque(true);
			label.setBackground(Color.WHITE);
			label.setPreferredSize(new Dimension(panelWidth, label.getPreferredSize().height));
			leftPane.add(label);
		}


		PlotPanel plotPane = new PlotPanel(this.index, this.applet);
		OperationPanel opePane = new OperationPanel(this.index, this.applet);
		double baseMass = 0;
		if ( !emass.equals("") && !emass.equals("0") ) {
			double H = 1.0078250321;
			double dblEmass = Double.parseDouble(emass);
			if ( ion.equals("1") ) {
				dblEmass += H;
			}
			else if ( ion.equals("-1") ) {
				dblEmass += H;
			}
			baseMass = new BigDecimal(dblEmass).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
		}

		Peak peak = new Peak(peaks);
		int prec = 0;
		if ( !precursor.equals("") ) {
			prec = Integer.parseInt(precursor);
		}
		plotPane.setParameter(peak, this.sp, prec, baseMass);
		plotPane.setPreferredSize( new Dimension(panelWidth, 170) );
		plotPane.repaint();

		ArrayList<Double> mzList1 = null;
		ArrayList<Double> mzList2 = null;
		if ( !applet.searchType.equals("") ) {
			String[] qmzs = sp.getMzs();
			int qinte = Integer.parseInt(sp.getRelativeIntensity());
			double qtol = Double.parseDouble(sp.getTolerance());
			int num = peak.getCount();
			for ( int i = 0; i < num; i++ ) {
				double mz1 = peak.getMz(i);
				int inte1 = peak.getIntensity(i);
				if ( inte1 < qinte ) {
					continue;
				}
				if ( applet.searchType.equals("peak") ) {
					if ( applet.hitPeaks1[this.index][0] == null ) {
						mzList1 = new ArrayList<Double>();
					}
					for ( int k = 0; k < qmzs.length; k++ ) {
						double qmz1 = Double.parseDouble(qmzs[k]) - qtol;
						double qmz2 = Double.parseDouble(qmzs[k]) + qtol;
						if ( mz1 >= qmz1 && mz1 <= qmz2 ) {
							mzList1.add(mz1);
						}
					}
					applet.hitPeaks1[this.index][0] = mzList1;
				}
				else {
					for ( int j = i + 1; j < num; j++ ) {
						double mz2 = peak.getMz(j);
						int inte2 = peak.getIntensity(j);
						if ( inte2 < qinte ) {
							continue;
						}
						double diff = mz2 - mz1;
						for ( int k = 0; k < qmzs.length; k++ ) {
							double qdiff1 = Double.parseDouble(qmzs[k]) - qtol;
							double qdiff2 = Double.parseDouble(qmzs[k]) + qtol;
							if ( diff >= qdiff1 && diff <= qdiff2 ) {
								if ( applet.hitPeaks1[this.index][k] == null ) {
									mzList1 = new ArrayList<Double>();
								}
								else {
									mzList1 = applet.hitPeaks1[this.index][k];
								}
								if ( applet.hitPeaks2[this.index][k] == null ) {
									mzList2 = new ArrayList<Double>();
								}
								else {
									mzList2 = applet.hitPeaks2[this.index][k];
								}
								mzList1.add(mz1);
								mzList2.add(mz2);
								applet.hitPeaks1[this.index][k] = mzList1;
								applet.hitPeaks2[this.index][k] = mzList2;
							}
						}
					}
				}
			}
		}

		opePane.setLayout( new FlowLayout(FlowLayout.LEFT, 0, 0) );
		opePane.setMaximumSize( new Dimension(opePane.getMaximumSize().width, 100) );
		leftPane.add(plotPane);
		leftPane.add(opePane);
		leftPane.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		leftPane.setPreferredSize(new Dimension(panelWidth, leftPane.getPreferredSize().height));
		return leftPane;
	}

	/**
	 *
	 */
	private JPanel createRightPanel() {
		String formula = this.info.getFormula();
		String emass   = this.info.getExactMass();
		String moldata = this.info.getMolfileData();

		JPanel rightPane = new JPanel();
		if ( this.isMulti ) {
			String[] labels = new String[]{ "   Formula: ", formula,"   Exact Mass: ", emass };
			boolean isOdd = true;
			for ( String val: labels ) {
				JLabel label = new JLabel(val);
				label.setOpaque(true);
				if ( isOdd ) {
					label.setForeground(Color.BLACK);
					label.setBackground(Color.WHITE);
					label.setPreferredSize( new Dimension(84, 17) );
					isOdd = false;
				}
				else {
					label.setForeground(new Color(57, 127, 0));
					label.setBackground(Color.WHITE);
					label.setPreferredSize( new Dimension(116, 17) );
					isOdd = true;
				}
				rightPane.add(label);
			}
		}
		else {
			JLabel label = new JLabel("Chemical Structure");
			rightPane.add(label);
		}

		JPanel pane3 = null;
		if ( !moldata.equals("") ) {
			pane3 = (MolViewPanelExt)new MolViewPanelExt(200, true, false, this.applet, "", moldata);
		}
		else {
			JLabel lbl = new JLabel( "Not Available", JLabel.CENTER );
			lbl.setPreferredSize( new Dimension(180, 180) );
			lbl.setBackground(new Color(0xF8,0xF8,0xFF));
			lbl.setBorder( new LineBorder(Color.BLACK, 1) );
			lbl.setOpaque(true);
			GridBagLayout layout = new GridBagLayout();
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.insets = new Insets(1, 1, 1, 1);
			layout.setConstraints(lbl, gbc);
			pane3 = new JPanel();
			pane3.setPreferredSize( new Dimension(200, 200) );
			pane3.setBackground(Color.WHITE);
			pane3.add(lbl);
		}
		rightPane.add(pane3);

		JLabel lbl3 = new JLabel("");
		lbl3.setPreferredSize( new Dimension(200, 30) );
		rightPane.add(lbl3);
		rightPane.setPreferredSize( new Dimension(200, 260) );
		rightPane.setBackground(Color.WHITE);
		rightPane.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		rightPane.setMaximumSize(new Dimension(200, rightPane.getMaximumSize().height));
		return rightPane;
	}

	/**
	 * 
	 */
	public static JPanel createSeparatorLine() {
		JPanel spacePane = new JPanel();
		spacePane.setPreferredSize( new Dimension(panelWidth, 2) );
		spacePane.setBackground(Color.white);
		return spacePane;
	}
}

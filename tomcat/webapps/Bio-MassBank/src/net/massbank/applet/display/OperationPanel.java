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
 * OperationPanel.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.applet.display;

import java.util.List;
import java.util.ArrayList;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import javax.swing.JToggleButton;

class OperationPanel extends JPanel implements ActionListener {
	private JToggleButton btnShowAll = null;
	private JToggleButton btnMassDiff = null;
	private String comNameDiff = "show_diff";
	private int index = 0;
	private boolean isPrevSelected = false;
	private DisplayApplet applet = null;

	/**
	 *
	 */
	public OperationPanel(int index, DisplayApplet applet) {
		this.index = index;
		this.applet = applet;
		String[] strs = new String[]{"<<", "<", ">", ">>"};
		for ( int i = 0; i < strs.length; i++ ) {
			JButton btn = new JButton(strs[i]);
			btn.setActionCommand(strs[i]);
			btn.addActionListener(this);
			btn.setMargin(new Insets(0, 0, 0, 0));
			add(btn);
		}
		this.btnShowAll = new JToggleButton("show all m/z");
		this.btnShowAll.setActionCommand("mz");
		this.btnShowAll.addActionListener(this);
		this.btnShowAll.setMargin(new Insets(0, 0, 0, 0));
		btnMassDiff = new JToggleButton("mass difference");
		btnMassDiff.setActionCommand("msdiff");
		btnMassDiff.addActionListener(this);
		btnMassDiff.setMargin(new Insets(0, 0, 0, 0));
		add(this.btnShowAll);
		add(this.btnMassDiff);
		applet.showAllBtnList.add(this.btnShowAll);
		applet.massDiffBtnList.add(this.btnMassDiff);
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	}

	/**
	 * 
	 */
	public void actionPerformed(ActionEvent ae) {
		String com = ae.getActionCommand();
		double start = this.applet.massStart;
		double range = this.applet.massRange;
		double max   = this.applet.massRangeMax;
		if ( com.equals("<<") )      { this.applet.massStart = Math.max(0, start - range); }
		else if ( com.equals("<") )  { this.applet.massStart = Math.max(0, start - range / 4); }
		else if ( com.equals(">") )  { this.applet.massStart = Math.min(max - range, start + range / 4); }
		else if ( com.equals(">>") ) { this.applet.massStart = Math.min(max - range, start + range); }
		else if ( com.equals("mz") || com.equals("msdiff") ) {
			boolean isSelect1 = this.btnShowAll.isSelected();
			boolean isSelect2 = this.btnMassDiff.isSelected();
			if ( isSelect1 && isSelect2 ) {
				if ( this.isPrevSelected && isSelect2 ) {
					isSelect1 = false;
				}
				else {
					isSelect2 = false;
				}
			}
			for ( int i = 0; i < this.applet.showAllBtnList.size(); i++ ) {
				this.applet.showAllBtnList.get(i).setSelected(isSelect1);
			}
			for ( int i = 0; i < this.applet.massDiffBtnList.size(); i++ ) {
				this.applet.massDiffBtnList.get(i).setSelected(isSelect2);
			}
			this.applet.showAll = isSelect1;
			this.isPrevSelected = isSelect1;
		}

		int pos = com.indexOf(comNameDiff);
		if ( pos >= 0 ) {
			int num = Integer.parseInt(com.substring(comNameDiff.length()));
			this.applet.hitNum = num;
			for ( int i = 0; i < applet.diffBtnList.length; i++ ) {
				for ( int j = 0; j < applet.diffBtnList[i].length; j++ ) {
					applet.diffBtnList[i][j].setSelected(false);
				}
				applet.diffBtnList[i][num].setSelected(true);
			}
		}
		this.applet.repaint();
	}

	/**
	 * 
	 */
	public void addDiffButton() {
		for ( int i = 0; i < this.applet.hitDiffVals.length; i++ ) {
			JToggleButton diffbtn = new JToggleButton( "Diff." + this.applet.hitDiffVals[i] );
			diffbtn.setActionCommand( comNameDiff + Integer.toString(i) );
			diffbtn.addActionListener(this);
			diffbtn.setMargin( new Insets(0, 0, 0, 0) );
			add(diffbtn);
			applet.diffBtnList[this.index][i] = diffbtn;
		}
		applet.diffBtnList[this.index][0].setSelected(true);
	}
}

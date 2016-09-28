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
package net.massbank.applet.formulaSearch;

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
	private FormulaSearchApplet applet = null;

	public OperationPanel(FormulaSearchApplet applet) {
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
		add(this.btnShowAll);
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
			this.btnShowAll.setSelected(isSelect1);
			this.applet.showAll = isSelect1;
		}
		this.applet.repaint();
	}
}

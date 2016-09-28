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
 * ProgressDialog.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.applet.formulaSearch;

import javax.swing.*;
import java.awt.*;

public class ProgressDialog extends JDialog {

	public ProgressDialog(Frame parent){
		super(parent, false);
		setDialog("");
	}

	public ProgressDialog(Frame parent, String msg){
		super(parent, false);
		setDialog(msg);
	}

	public void setDialog(String msg) {
		setUndecorated(true);

		JPanel panel = new JPanel();
		panel.setBackground(new Color(0x0000FF));
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JLabel label = new JLabel();
		label.setFont(new Font("Dialog", (Font.ITALIC|Font.BOLD), 22));
		label.setForeground(Color.white);
		String text = "Searching...";
		if ( !msg.equals("") ) {
			text = msg;
		}
		label.setText(text);
		panel.add(label);

		UIManager.put("ProgressBar.repaintInterval", new Integer(20));
		UIManager.put("ProgressBar.cycleTime", new Integer(1000));
		JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		panel.add(progressBar);
		add(panel);
		pack();

		setLocationRelativeTo(null);
	}
}

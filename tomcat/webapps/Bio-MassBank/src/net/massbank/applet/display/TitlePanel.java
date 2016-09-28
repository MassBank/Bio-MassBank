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
 * TitlePanel.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.applet.display;

import java.net.URL;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.SwingConstants;

class TitlePanel extends JPanel implements ActionListener {
	private String id = "";
	private String siteNo = "";

	/**
	 * 
	 */
	public TitlePanel(String title, String id, String siteNo) {
		JButton btn = new JButton();
		JLabel idLabel = new JLabel(id + ":");
		idLabel.setPreferredSize(new Dimension(120, 16));
		btn.add(idLabel);
		JLabel titleLabel = new JLabel(title);
		titleLabel.setPreferredSize(new Dimension(600, 16));
		titleLabel.setForeground(Color.BLUE);
		btn.add(titleLabel);
		btn.addActionListener(this);
		btn.setPreferredSize(new Dimension(770, 26));
		btn.setHorizontalAlignment(SwingConstants.LEFT);
		btn.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		add(btn);
		setLayout( new FlowLayout(FlowLayout.LEFT) );
		setMaximumSize( new Dimension(this.getMaximumSize().width, 100) );

		this.id = id;
		this.siteNo = siteNo;
	}

	/**
	 * 
	 */
	public void actionPerformed(ActionEvent ae) {
		try {
			String targetUrl = DisplayApplet.baseUrl + "mbtools/Record.jsp?id=" + this.id;
			DisplayApplet.appContext.showDocument(new URL(targetUrl), "_blank");
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}
	}
}

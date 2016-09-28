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
 * MolViewZoom.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.applet.common;

import java.awt.Insets;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class MolViewZoom extends JFrame implements ActionListener
{
	private static final int ZOOM_RECT_SIZE = 400;
	private static final float ZOOM_RATE_MAX = 1.5f;
	private float zoomRate = 1.0f;
	private float zoomStep = ZOOM_RATE_MAX - zoomRate;
	private MolViewPanelExt viewPane = null;
	private JButton btnZoom = null;
	private ImageIcon icon1 = null;
	private ImageIcon icon2 = null;
	private boolean isZoomIn = false;

	/**
	 * constructor
	 */
	public MolViewZoom(boolean isApplication, JApplet applet, String name, String data) {
		final int btnSize = 32;
		this.viewPane = new MolViewPanelExt(ZOOM_RECT_SIZE, false, isApplication, applet, name, data);
		
		this.zoomRate = this.viewPane.getZoomScale();
		this.zoomStep = ZOOM_RATE_MAX - this.zoomRate;
		
		ClassLoader cl = this.getClass().getClassLoader();
		this.icon1 = new ImageIcon(cl.getResource("images/zoomin.png"));
		this.icon2 = new ImageIcon(cl.getResource("images/zoomout.png"));
		this.btnZoom = new JButton( this.icon1 );
		this.btnZoom.setMargin(new Insets(0,0,0,0));
		this.btnZoom.setMaximumSize(new Dimension(btnSize, btnSize));
		this.btnZoom.setPreferredSize(new Dimension(btnSize, btnSize));
		this.btnZoom.addActionListener(this);
		
		JPanel parentPane = new JPanel();
		JPanel btnPane = new JPanel();
		btnPane.setLayout(new BoxLayout(btnPane, BoxLayout.Y_AXIS));
		btnPane.add(this.btnZoom);
		btnPane.add(Box.createRigidArea(new Dimension(0,5)));
		parentPane.add(this.viewPane);
		parentPane.add(btnPane);
		add(parentPane);

		setTitle(name);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setVisible(true);
		setSize( getPreferredSize().width, getPreferredSize().height );
	}

	/**
	 *
	 */
	public void actionPerformed(ActionEvent e) {
		if ( !this.isZoomIn ) {
			this.zoomRate += this.zoomStep;
			this.btnZoom.setIcon(this.icon2);
			this.isZoomIn = true;
		}
		else {
			this.zoomRate -= this.zoomStep;
			this.btnZoom.setIcon(this.icon1);
			this.isZoomIn = false;
		}
		this.viewPane.zoomChangeTo(this.zoomRate);
		setSize( getPreferredSize().width, getPreferredSize().height );
		setVisible(true);
	}
}

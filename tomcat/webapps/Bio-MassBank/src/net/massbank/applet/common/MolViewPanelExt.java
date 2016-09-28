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
 * MolViewPanelExt.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.applet.common;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import javax.swing.JApplet;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class MolViewPanelExt extends MolViewPanel implements MouseListener {
	MolViewZoom frame = null;
	boolean isParent = true;
	boolean isApplication = false;
	JApplet applet = null;
	String name = "";
	String data = "";

	/**
	 * constructor
	 */
	public MolViewPanelExt(
		int size, boolean isParent, boolean isApplication, JApplet applet, String name, String data) {
		if ( applet != null ) {
			setApplet(applet);
			this.applet = applet;
		}
		this.isApplication = isApplication;
		this.name = name;
		this.data = data;
		init();
		float zoomRate = 1.0f;
		boolean isRateCalc = false;
		Point2D.Float pos = null;
		int psize;
		int rsize;
		while(true) {
			read("", name, data, pos);
			unselectedSymbols();
			if ( isApplication ) {
				setOffSet(0);
			}
			psize = (int)(size / (zoomRate * 1.1));
			rsize = (int)((psize - getOffSet() * 2) / 1.1);
			if ( isRateCalc ) {
				setScale(getScale() * zoomRate);
			}
			Dimension d = getSymbolSize();
			if(d.getWidth() > d.getHeight()) {
				setRectBound(rsize, 1);
			}
			else {
				setRectBound(1, rsize);
			}

			float bWidth = getPictureSize().width + (getPictureMargin() * 2);
			float bHeight = getPictureSize().height + (getPictureMargin() * 2);
			if ( !isRateCalc ) {
				if ( bWidth >= bHeight ) {
					if ( bWidth >= size ) {
						zoomRate = size / bWidth;
					}
					else {
						zoomRate = bWidth / size;
					}
				}
				else {
					if ( bHeight >= size ) {
						zoomRate = size / bHeight;
					}
					else {
						zoomRate = bHeight / size;
					}
				}
				isRateCalc = true;
			}
			else {
				break;
			}
			float xPos = Math.abs(size - (getPictureSize().width) * zoomRate) / 2 - (getOffSet() * zoomRate);
			float yPos = Math.abs(size - (getPictureSize().height) * zoomRate) / 2 - (getOffSet() * zoomRate);
			pos = new Point2D.Float(xPos, yPos);
		}
		
		zoomChangeTo(zoomRate);
		setPaperSize( new Dimension(psize, psize) );
		addMouseListener(this);
		this.isParent = isParent;
	}

	/**
	 *
	 */
	private void showFrame() {
		if ( this.isParent ) {
			if ( this.frame == null ) {
				this.frame = new MolViewZoom(this.isApplication, this.applet, this.name, this.data);
			}
			this.frame.setVisible(true);
		}
	}

	/**
	 *
	 */
	public void mouseClicked(MouseEvent e) {
		if ( SwingUtilities.isLeftMouseButton(e) ) {
			showFrame();
		}
	}
	
	/**
	 *
	 */
	public void mousePressed(MouseEvent e) {
		if ( SwingUtilities.isLeftMouseButton(e) ) {
			showFrame();
		}
	}

	public void mouseReleased(MouseEvent e){}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e)  {}
	public void mouseDragged(MouseEvent e) {}
}


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
 * MolViewPanel.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.applet.common;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import metabolic.MolFigure;
import canvas.DrawPane;

@SuppressWarnings("serial")
public class MolViewPanel extends DrawPane
{
	private boolean alreadyRead;
	private draw2d.MOLformat mft;
	private MolFigure mf;
	Dimension size;

	/**
	 *
	 */
	public MolViewPanel() {
		super(null, null);
		mft = new draw2d.MOLformat();
		alreadyRead = false;
		size = new Dimension(-1, -1);
	}

	/**
	 * 
	 */
	public void init() {
		util.MolMass.init();
		try {
			prepareMenusForPopups(false);
		}
		catch ( ExceptionInInitializerError e ) {
		}
	}

	/**
	 * 
	 */
	public void clear() {
		if( mf != null ) {
			mf.clear();
		}
	}

	/**
	 *
	 */
	public void read(String id, String name, String data, Point2D.Float pos) {
		mft.read(data);
		set(id, name, pos);
		alreadyRead = true;
		size = getPaperSize();
		unselectAllSymbols();
	}

	/**
	 *
	 */
	public void read(String id, String name, File file, Point2D.Float pos)
		throws FileNotFoundException, IOException {
		java.io.BufferedReader br = new java.io.BufferedReader(new InputStreamReader(new FileInputStream(file)));
		mft.read(br);
		set(id, name, pos);
		alreadyRead = true;
		size = getPaperSize();
		unselectAllSymbols();
	}

	/**
	 *
	 */
	public void unselectedSymbols() {
		unselectAllSymbols();
	}

	/**
	 *
	 */
	public boolean isAlreadyRead() {
		return alreadyRead;
	}

	/**
	 *
	 */
	protected void setOffSet(int o) {
		super.setOffSet(o);
	}
	
	/**
	 *
	 */
	protected int getOffSet() {
		return super.getOffSet();
	}
	
	/**
	 *
	 */
	public void setPictureMargin(int margin) {
		mf.setPictureMargin(margin);
	}
	
	/**
	 *
	 */
	public int getPictureMargin() {
		return mf.getPictureMargin();
	}
	
	/**
	 *
	 */
	public void setRectBound(float w, float h) {
		mf.setRectBound(w, h);
	}

	/**
	 *
	 */
	public Rectangle2D.Float getRectBound() {
		return mf.getRectBound();
	}

	/**
	 *
	 */
	public Rectangle2D.Float getBoundingBox() {
		return mf.getBoundingBox();
	}
	
	/**
	 *
	 */
	public void setScale(float s) {
		mf.setScale(s);
	}

	/**
	 *
	 */
	public float getScale() {
		return mf.getScale();
	}

	/**
	 *
	 */
	public Dimension getSymbolSize() {
		return size;
	}

	/**
	 *
	 */
	private void set(String id, String name, Point2D.Float pos) {
		mf = new MolFigure(id, name, mft);
		mf.initialization(this, new Point2D.Float(0, 0), 20);
		mf.setRectBound();
		newDraw(id, true);
		if ( pos == null ) {
			pos = new Point2D.Float(5, 5);
		}
		getLayer().addNew(mf, pos, 0);
		Dimension d = getPictureSize();
		setPaperSize(d);
		setBackground(Color.WHITE);
		super.repaint();
	}
}

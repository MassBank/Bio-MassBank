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
 * PlotPanel.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.applet.display;

import java.awt.Font;
import java.awt.Point;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.math.BigDecimal;
import javax.swing.Timer;
import javax.swing.JPanel;
import javax.swing.JApplet;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import net.massbank.applet.common.Peak;
import net.massbank.tools.search.peak.PeakSearchParameter;

class PlotPanel extends JPanel
	implements MouseListener, MouseMotionListener
{
	public static final int INTENSITY_MAX = 1000;
	private static final int MARGIN = 15;
	private static final int MIN_RANGE = 5;
	private static final int DEF_EX_PANE_SIZE = 150;

	private static final long serialVersionUID = 1L;
	private int index;
	private Peak peak = null;
	private double baseMass = 0;
	private int precursor = 0;
	private int panelWidth = 0;
	private int panelHeight = 0;
	private static final Color[] colorTbl = {
		new Color(0xD2,0x69,0x48), new Color(0x22,0x8B,0x22),
		new Color(0x41,0x69,0xE1), new Color(0xBD,0x00,0x8B),
		new Color(0x80,0x80,0x00), new Color(0x8B,0x45,0x13	),
		new Color(0x9A,0xCD,0x32)
	};
	private PeakSearchParameter sp = null;
	private Point cursorPoint = null;
	private DisplayApplet applet = null;
	public Graphics g = null;
	private ArrayList<Double> mzList1 = null;
	private ArrayList<Double> mzList2 = null;
	private int mz1Cnt = 0;
	private int mz2Cnt = 0;
	private double mz1Prev = 0;
	private double mz2Prev = 0;
	private JPopupMenu selectPopup = null;
	private JPopupMenu contextPopup = null;


	/**
	 * 
	 */
	public PlotPanel(int index, DisplayApplet applet) {
		this.index = index;
		this.applet = applet;
		addMouseListener(this);
		addMouseMotionListener(this);
		cursorPoint = new Point();
	}

	/**
	 * 
	 */
	public void setParameter(Peak peak, PeakSearchParameter sp, int precursor, double baseMass) {
		this.peak = peak;
		this.sp = sp;
		this.baseMass = baseMass;
		this.precursor = precursor;
		applet.peakList.add(peak);
		applet.precursorList.add(precursor);
	}

	/**
	 * 
	 */
	int getStep(int range) {
		int val = 0;
		if ( range < 20 )       { val = 2;  }
		else if ( range < 50 )  { val = 5;  }
		else if ( range < 100 ) { val = 10; }
		else if ( range < 250 ) { val = 25; }
		else if ( range < 500 ) { val = 50; }
		else                    { val = 100;}
		return val;
	}

	/**
	 *
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		this.g = g;

		drawChartFrame();
		if ( this.peak == null || this.peak.getCount() == 0 ) {
			drawNoPeak();
		}
		else {
			drawPeakBar();

			if ( applet.searchType.equals("peak_diff") ) {
				drawHitDiffPos();
			}

			//
			if ( applet.massDiffBtnList.get(this.index).isSelected() && this.baseMass > 0 ) {
				drawMassDiff();
			}
		}

		if ( this.precursor > 0 ) {
			drawPrecursorMark();
		}

		if ( applet.underDrag ) {
			fillRectRange();
		}
	}

	/**
	 *
	 */
	private void drawChartFrame() {
	 	int marginTop = 0;
		if ( applet.searchType.equals("peak_diff") ) {
			marginTop = 70;
		}
		else {
			marginTop = MARGIN;
		}

		this.panelWidth = getWidth();
		this.panelHeight = getHeight();
		applet.xscale = (this.panelWidth - 2.0d * MARGIN) / applet.massRange;
		applet.yscale = (this.panelHeight - (double)(MARGIN + marginTop) ) / applet.intensityRange;

		g.setColor(Color.white);
		g.fillRect(0, 0, this.panelWidth, this.panelHeight);

		g.setFont(g.getFont().deriveFont(9.0f));
		g.setColor(Color.lightGray);

		int x = MARGIN;
		int y = this.panelHeight - MARGIN;
		g.drawLine(x, marginTop, x, y);
		g.drawLine(x, y, this.panelWidth - MARGIN, y);

		int step = getStep((int)applet.massRange);
		int start = (step - (int)applet.massStart % step) % step;
		for (int i = start; i < (int)applet.massRange; i += step) {
			x = MARGIN + (int)(i * applet.xscale);
			g.drawLine(x, y, x, y + 3);
			String mzStr = formatMass(i + applet.massStart, true);
			g.drawString(mzStr, x - 5, this.panelHeight - 1);
		}

		for (int i = 0; i <= applet.intensityRange; i += applet.intensityRange / 5) {
			y = this.panelHeight - MARGIN - (int)(i * applet.yscale);
			g.drawLine(MARGIN - 2, y, MARGIN, y);
			g.drawString(String.valueOf(i), 0, y);
		}
	}

	/**
	 *
	 */
	private void drawPeakBar() {
		if ( applet.searchType.equals("peak") || applet.searchType.equals("peak_diff") ) {
			this.mzList1 = applet.hitPeaks1[this.index][applet.hitNum];
			this.mzList2 = applet.hitPeaks2[this.index][applet.hitNum];
		}

		boolean isOnPeak;
		boolean isSelectPeak;
		int start = this.peak.getIndex(applet.massStart);
		int end = this.peak.getIndex(applet.massStart + applet.massRange);
		boolean isShowAll = applet.showAllBtnList.get(0).isSelected();

		this.mz1Cnt = 0;
		this.mz2Cnt = 0;
		this.mz1Prev = 0;
		this.mz2Prev = 0;
		for ( int i = start; i < end; i++ ) {
			isOnPeak = false;
			isSelectPeak = this.peak.isSelectPeakFlag(i);
			double mz = this.peak.getMz(i);
			int its = this.peak.getIntensity(i);
			int w = (int)(applet.xscale / 8);
			int h = (int)(its * applet.yscale);
			int x = MARGIN + (int) ((mz - applet.massStart) * applet.xscale) - (int) Math.floor(applet.xscale / 8);
			int y = this.panelHeight - MARGIN - h;
			
			if ( h == 0 ) {
				y -= 1;
				h = 1;
			}
			if ( w < 2 ) {
				w = 2;
			}
			else if ( w < 3 ) {
				w = 3;
			}
			
			if ( MARGIN >= x ) {
				w = (w - (MARGIN - x) > 0) ? (w - (MARGIN - x)) : 1;
				x = MARGIN + 1;
			}
			
			if ( x <= cursorPoint.getX() 
					&& cursorPoint.getX() <= (x + w)
					&& y <= cursorPoint.getY() 
					&& cursorPoint.getY() <= (y + h)) {
				
				isOnPeak = true;
			}
			
			Color color = Color.black;
			if ( applet.searchType.equals("peak") || applet.searchType.equals("peak_diff") ) {
				color = getColorForPeak(mz);
			}
			boolean isHit = true;
			if ( color == Color.black ) {
				isHit = false;
			}

			Color onCursorColor = Color.blue;
			Color selectColor = Color.cyan.darker();

			boolean isSelectedMassDiff = applet.massDiffBtnList.get(this.index).isSelected();
			if ( isSelectedMassDiff && this.baseMass > 0 ) {
				color = Color.magenta;
			}
			if ( isSelectPeak ) {
				color = selectColor;
			}
			else if ( isOnPeak ) {
				color = onCursorColor;
			}
			g.setColor(color);

			g.fill3DRect(x, y, w, h, true);
			if ( isSelectedMassDiff && this.baseMass > 0 ) {
				String msDiff = this.peak.getDiff(i, this.baseMass);
				String sign = "";
				if ( Double.parseDouble(msDiff) > 0 ) {
					sign = "+";
				}
				g.setColor(Color.magenta);
				g.drawString(sign + msDiff,
						x, this.panelHeight - MARGIN - (int)(its * applet.yscale));
			}
			else if ( its > applet.intensityRange * 0.4 || applet.showAll || isHit || isOnPeak || isSelectPeak ) {
				float fontSize = 9.0f;
				if ( isOnPeak ) {
					color = onCursorColor;
					fontSize = 14.0f;
					if ( isSelectPeak ) {
						color = selectColor;
					}
				}
				else if ( isSelectPeak ) {
					color = selectColor;
				}
				else if ( applet.showAll && its > applet.intensityRange * 0.4 ) {
					color = Color.red;
				}
				g.setFont(g.getFont().deriveFont(fontSize));
				g.setColor(color);
				
				String mzStr = formatMass(mz, false);
				g.drawString(mzStr, x, y);
			}
			
			if ( isOnPeak || isSelectPeak ) {
				if ( isOnPeak ) {
					g.setColor(onCursorColor);
				}
				if ( isSelectPeak ) {
					g.setColor(selectColor);
				}
				g.drawLine(MARGIN + 4, y, MARGIN - 4, y);
				g.setColor(Color.lightGray);
				g.setFont(g.getFont().deriveFont(9.0f));
				if ( isOnPeak && isSelectPeak ) {
					g.setColor(Color.gray);
				}
				g.drawString(String.valueOf(its), MARGIN + 7, y + 1);
			}

			g.setColor(Color.black);
		}
	}

	/**
	 *
	 */
	private void drawNoPeak() {
		g.setFont( new Font("Arial", Font.ITALIC, 24) );
		g.setColor( Color.LIGHT_GRAY );
		g.drawString( "No peak was observed.", this.panelWidth / 2 - 110, this.panelHeight / 2 );
	}

	/**
	 *
	 */
	private void drawPrecursorMark() {
		int xPre = getBaseX(this.precursor);
		int yPre = this.panelHeight - MARGIN;
		if ( xPre >= MARGIN && xPre <= this.panelWidth - MARGIN ) {
			int [] xp = { xPre, xPre + 6, xPre - 6 };
			int [] yp = { yPre, yPre + 6, yPre + 6 };
			g.setColor( Color.RED );
			g.fillPolygon( xp, yp, xp.length );
		}
	}

	/**
	 *
	 */
	private void drawMassDiff() {
		if ( this.baseMass > applet.massStart && this.baseMass <= applet.massStart + applet.massRange) {
			g.setColor(Color.black);
			int bx = getBaseX(this.baseMass);

		    g.setFont(g.getFont().deriveFont(14.0f));
			g.drawString(String.valueOf(this.baseMass), bx, MARGIN);
			g.setFont(g.getFont().deriveFont(9.0f));

			Graphics2D g2 = (Graphics2D)g;
			g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{4.0f}, 0.0f));
			g2.draw(new Line2D.Float(bx, this.panelHeight, bx, MARGIN));
		}
	}

	/*
	 *
	 */
	private int getBaseX(double base) {
		int bx = MARGIN + (int)((base - applet.massStart) * applet.xscale) - (int)Math.floor(applet.xscale / 8);
		double bw = applet.xscale / 8;
		if ( MARGIN >= bx ) {
			bw = bw - (MARGIN - bx);
			bx = MARGIN;
		}
		if ( bw < 2.0 ) {
			bw = 2.0;
		}
		bx += new BigDecimal(bw).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
		int align = new BigDecimal(2.0 / getStep((int)applet.massRange) * 2.0).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
		if ( align < 1 ) {
			align = 1;
		}
		bx -= align;
		return bx;
	}

	/**
	 *
	 */
	private void fillRectRange() {
		int xpos = Math.min(applet.fromPos.x, applet.toPos.x);
		int width = Math.abs(applet.fromPos.x - applet.toPos.x);
		g.setXORMode(Color.white);
		g.setColor(Color.yellow);
		g.fillRect(xpos, 0, width, this.panelHeight - MARGIN);
		g.setPaintMode();
	}

	/**
	 * 
	 */
	public void mousePressed(MouseEvent e) {
		if ( SwingUtilities.isLeftMouseButton(e) ) {
			if( applet.timer != null && applet.timer.isRunning() ) {
				return;
			}
			applet.fromPos = applet.toPos = e.getPoint();
		}
	}

	/**
	 * 
	 */
	public void mouseDragged(MouseEvent e) {
		if ( SwingUtilities.isLeftMouseButton(e) ) {
			if( applet.timer != null && applet.timer.isRunning() ) {
				return;
			}
			applet.underDrag = true;
			applet.toPos = e.getPoint();
			repaint();
		}
	}

	/**
	 * 
	 */
	public void mouseReleased(MouseEvent e) {
		if ( SwingUtilities.isLeftMouseButton(e) ) {
			if (!applet.underDrag || (applet.timer != null && applet.timer.isRunning())) {
				return;
			}
			applet.underDrag = false;
			if ((applet.fromPos != null) && (applet.toPos != null)) {
				if ( Math.min(applet.fromPos.x, applet.toPos.x) < 0 ) {
					applet.massStart = Math.max(0, applet.massStart - applet.massRange / 3);
				}
				else if (Math.max(applet.fromPos.x, applet.toPos.x) > getWidth()) {
					applet.massStart = Math.min(applet.massRangeMax - applet.massRange, applet.massStart + applet.massRange / 3);
				}
				else {
					if ( this.peak != null) {
						applet.timer = new Timer(30,
								new AnimationTimer(Math.abs(applet.fromPos.x - applet.toPos.x),
										Math.min(applet.fromPos.x, applet.toPos.x)));
						applet.timer.start();
					}
					else {
						applet.fromPos = applet.toPos = null;
						repaint();
					}
				}
			}
		}
	}

	/**
	 * 
	 */
	public void mouseClicked(MouseEvent e) {
		if ( SwingUtilities.isLeftMouseButton(e) ) {
			long interSec = (e.getWhen() - applet.lastClickedTime);
			applet.lastClickedTime = e.getWhen();
			if ( interSec <= 280 ) {
				applet.fromPos = applet.toPos = null;
				applet.initPlotPanel();
				repaint();
			}
		}
	}

	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}

	/**
	 *
	 */
	public void mouseMoved(MouseEvent e) {
		if ((selectPopup != null && selectPopup.isVisible())
				|| contextPopup != null && contextPopup.isVisible()) {
			
			return;
		}
		cursorPoint = e.getPoint();
		this.repaint();
	}

	/**
	 *
	 */
	private String formatMass(double mass, boolean isForce) {
		final int ZERO_DIGIT = 4;
		String massStr = String.valueOf(mass);
		if (isForce) {
			if (massStr.indexOf(".") == -1) {
				massStr += ".0000";
			}
			else {
				if (massStr.indexOf(".") != -1) {
					String [] tmpMzStr = massStr.split("\\.");
					if (tmpMzStr[1].length() <= ZERO_DIGIT) {
						int addZeroCnt = ZERO_DIGIT - tmpMzStr[1].length();
						for (int j=0; j<addZeroCnt; j++) {
							massStr += "0";
						}
					}
					else {
						if (tmpMzStr[1].length() > ZERO_DIGIT) {
							massStr = tmpMzStr[0] + "." + tmpMzStr[1].substring(0, ZERO_DIGIT);
						}
					}
				}
			}
		}
		else {
			if (massStr.indexOf(".") != -1) {
				String [] tmpMzStr = massStr.split("\\.");
				if (tmpMzStr[1].length() > ZERO_DIGIT) {
					massStr = tmpMzStr[0] + "." + tmpMzStr[1].substring(0, ZERO_DIGIT);
				}
			}
		}
		return massStr;
	}

	/**
	 *
	 */
	class AnimationTimer implements ActionListener {
		private static final int LOOP = 15;
		private int loopCoef;
		private int minx;
		private double tmpMassStart;
		private double tmpMassRange;
		private int tmpIntensityRange;
		private int movex;

		public AnimationTimer(int w, int x) {
			this.loopCoef = 0;
			this.minx = x;
			int width = w;
			movex = 0 + MARGIN;
			double xs = (getWidth() - 2.0d * MARGIN) / applet.massRange;
			this.tmpMassStart = applet.massStart + ((this.minx - MARGIN) / xs);
			this.tmpMassRange = 10 * (width / (10 * xs));
			if ( this.tmpMassRange < MIN_RANGE ) {
				this.tmpMassRange = MIN_RANGE;
			}

			if ( applet.massRange <= applet.massRangeMax ) {
				int max = 0;
				double start = Math.max(this.tmpMassStart, 0.0d);
				for ( int i = 0; i < applet.peakList.size(); i++ ) {
					int inte = applet.peakList.get(i).getMaxIntensity(start, start + this.tmpMassRange);
					if ( max < inte ) {
						max = inte;
					}
				}
				this.tmpIntensityRange = (int)((1.0d + max / 50.0d) * 50.0d);
				if ( this.tmpIntensityRange > INTENSITY_MAX ) {
					this.tmpIntensityRange = INTENSITY_MAX;
				}
			}
		}

		public void actionPerformed(ActionEvent e) {
			applet.xscale = (getWidth() - 2.0d * MARGIN) / applet.massRange;
			int xpos = (this.movex + this.minx) / 2;
			if ( Math.abs(applet.massStart - this.tmpMassStart) <= 2
			  && Math.abs(applet.massRange - this.tmpMassRange) <= 2 ) {
				xpos = this.minx;
				applet.massStart = this.tmpMassStart;
				applet.massRange = this.tmpMassRange;
				applet.timer.stop();
				applet.repaint();
			}
			else {
				this.loopCoef++;
				applet.massStart += ((this.tmpMassStart + applet.massStart) / 2 - applet.massStart) * this.loopCoef / LOOP;
				applet.massRange += ((this.tmpMassRange + applet.massRange) / 2 - applet.massRange) * this.loopCoef / LOOP;
				applet.intensityRange += ((this.tmpIntensityRange + applet.intensityRange) / 2 - applet.intensityRange) * this.loopCoef / LOOP;
				if ( this.loopCoef >= LOOP ) {
					this.movex = xpos;
					this.loopCoef = 0;
				}
			}
			repaint();
		}
	}

	/**
	 *
	 */
	private Color getColorForPeak(double mz) {
		int num = 1;
		boolean isHit = false;

		if ( mzList1 != null ) {
			for ( int i = 0; i < this.mzList1.size(); i++ ) {
				double mz1 = this.mzList1.get(i);
				if ( mz == mz1 ) {
					if ( mz1 - this.mz1Prev >= 1 ) {
						num = this.mz1Cnt++;
					}
					this.mz1Prev = mz1;
					isHit = true;
					break;
				}
			}
		}

		if ( !isHit && mzList2 != null ) {
			for ( int i = 0; i < this.mzList2.size(); i++ ) {
				double mz2 = this.mzList2.get(i);
				if ( mz == mz2 ) {
					if ( mz2 - this.mz2Prev >= 1 ) {
						num = this.mz2Cnt++;
					}
					else {
					}
					this.mz2Prev = mz2;
					isHit = true;
					break;
				}
			}
		}

		Color color = Color.black;
		if ( isHit ) {
			if ( num >= colorTbl.length ) {
				num = 0;
			}
			color = colorTbl[num];
		}
		return color;
	}


	/**
	 *
	 */
	private void drawHitDiffPos() {
		String diffmz = applet.hitDiffVals[applet.hitNum];
		int pos = diffmz.indexOf(".");
		if ( pos > 0 ) {
			BigDecimal bgMzDiff = new BigDecimal(diffmz);
			diffmz = (bgMzDiff.setScale(1, BigDecimal.ROUND_DOWN)).toString(); 
		}

		double mz1Prev = 0;
		int hitCnt = 0;
		if ( mzList1 != null ) {
			for ( int i = 0; i < this.mzList1.size(); i++ ) {
				double mz1 = this.mzList1.get(i);
				double mz2 = this.mzList2.get(i);
				if ( mz1 - mz1Prev >= 1 ) {
					g.setColor(Color.GRAY);
	
					int barWidth = (int)Math.floor(applet.xscale / 8);
					int x1 = MARGIN + (int)((mz1 - applet.massStart) * applet.xscale) - barWidth / 2;
					int x2 = MARGIN + (int)((mz2 - applet.massStart) * applet.xscale) - barWidth / 2;
					int xc = x1 + (x2 - x1) / 2 - 12;
					int y = this.panelHeight - ( MARGIN + (int)((INTENSITY_MAX + MARGIN*2) * applet.yscale) + 5 + ((hitCnt+1) * 12) );
					int xm = (int)(diffmz.length() * 5) + 4;
					int padding = 5;
					g.drawLine( x1, y , xc, y );
					g.drawLine( xc + xm + padding, y, x2, y );
					g.drawLine( x1, y, x1, y + 4 );
					g.drawLine( x2, y, x2, y + 4 );
					g.setColor( colorTbl[hitCnt++] );
					g.fillRect( xc, y - padding, (xc + xm + padding) - xc, padding * 2 );
					g.setColor( Color.WHITE );
					g.drawString( diffmz, xc + padding , y + 3 );
				}
				mz1Prev = mz1;
			}
		}
	}
}

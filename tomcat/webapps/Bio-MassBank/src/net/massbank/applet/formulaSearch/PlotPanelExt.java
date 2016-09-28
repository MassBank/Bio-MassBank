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
 * PlotPanelExt.java
 *
 * ver 1.0.0 2013.06.18
 *
 ******************************************************************************/
package net.massbank.applet.formulaSearch;

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
import net.massbank.core.common.ChemicalFormulaUtils;
import net.massbank.applet.common.Peak;

class PlotPanelExt extends JPanel
	implements MouseListener, MouseMotionListener
{
	public static final int INTENSITY_MAX = 1000;
	private static final int MARGIN = 15;
	private static final int MIN_RANGE = 5;
	private static final int DEF_EX_PANE_SIZE = 150;

	private static final long serialVersionUID = 1L;
	private Peak peak = null;
	private int panelWidth = 0;
	private int panelHeight = 0;
	private Point cursorPoint = null;
	private FormulaSearchApplet applet = null;
	public Graphics g = null;
	private JPopupMenu selectPopup = null;
	private JPopupMenu contextPopup = null;
	private String[] matchedFormulas = null;
	private int ppm = 0;


	/**
	 * 
	 */
	public PlotPanelExt(FormulaSearchApplet applet) {
		this.applet = applet;
		addMouseListener(this);
		addMouseMotionListener(this);
		cursorPoint = new Point();
	}

	/*
	 *
	 */
	public void init() {
		applet.massRange = peak.getMz(peak.getCount() - 1);
		if ( applet.massRange != 0.0 && (applet.massRange % 100.0) == 0.0 ) {
			applet.massRange += 100.0;
		}
		applet.massRange = (double)Math.ceil(applet.massRange / 100.0) * 100.0 + 10;
		applet.massRangeMax = (int)applet.massRange;
		applet.massStart = 0;
		applet.intensityRange = PlotPanelExt.INTENSITY_MAX;
		repaint();
	}

	/**
	 * 
	 */
	public void setPeak(Peak peak) {
		this.peak = peak;
		this.matchedFormulas = null;
		init();
	}

	/**
	 * 
	 */
	public void setMatchedFormula(String[] matchedFormulas, int ppm) {
		this.matchedFormulas = matchedFormulas;
		this.ppm = ppm;
		repaint();
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
	 * (非 Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		this.g = g;
		drawChartFrame();
		if ( this.peak == null || this.peak.getCount() == 0 ) {
			return;
		}
		else {
			drawPeakBar();
		}

		if ( applet.underDrag ) {
			fillRectRange();
		}
	}

	/**
	 * 
	 */
	private void drawChartFrame() {
	 	int marginTop = 50;
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

		//---------------------------------------------
		// x軸
		//---------------------------------------------
		int step = getStep((int)applet.massRange);
		int start = (step - (int)applet.massStart % step) % step;
		for (int i = start; i < (int)applet.massRange; i += step) {
			x = MARGIN + (int)(i * applet.xscale);
			g.drawLine(x, y, x, y + 3);
			String mzStr = formatMass(i + applet.massStart, true);
			g.drawString(mzStr, x - 5, this.panelHeight - 1);
		}

		//---------------------------------------------
		// y軸
		//---------------------------------------------
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
		boolean isOnPeak;
		boolean isSelectPeak;
		int start = this.peak.getIndex(applet.massStart);
		int end = this.peak.getIndex(applet.massStart + applet.massRange);

		ArrayList<String> displayedFormulaList = new ArrayList();
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
			
			// y軸より左側には描画しないように調整
			if ( MARGIN >= x ) {
				w = (w - (MARGIN - x) > 0) ? (w - (MARGIN - x)) : 1;
				x = MARGIN + 1;
			}
			
			// カーソルピーク上判定
			if ( x <= cursorPoint.getX() 
					&& cursorPoint.getX() <= (x + w)
					&& y <= cursorPoint.getY() 
					&& cursorPoint.getY() <= (y + h)) {
				
				isOnPeak = true;
			}

			boolean isMatch = false;
			String formula = "";
			if ( this.matchedFormulas != null ) {
				for ( int j = 0; j < this.matchedFormulas.length; j++ ) {
					formula = this.matchedFormulas[j];
					double mass = ChemicalFormulaUtils.calcExactMass(formula);
					double delta = new BigDecimal(mass * ((double)ppm / 1000000)).setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
					double min = mass - delta;
					double max = mass + delta;
					if ( mz >= min && mz <= max ) {
						isMatch = true;
						break;
					}
				}
			}

			Color color = Color.black;
			Color onCursorColor = Color.blue;
			Color selectColor = Color.cyan.darker();

			if ( isSelectPeak ) {
				color = selectColor;
			}
			else if ( isOnPeak ) {
				color = onCursorColor;
			}
			else if ( isMatch ) {
				color = Color.RED;
			}
			g.setColor(color);
			g.fill3DRect(x, y, w, h, true);

			//----------------------------------------------------
			// m/z値を描画
			// 1) "all m/z"ボタンONで、intensityが400以上は「赤色」
			// 2) "all m/z"ボタンOFFで、ヒットピークはバーと同色
			// 3) それ以外は黒色
			//----------------------------------------------------
			if ( its > applet.intensityRange * 0.4 || applet.showAll || isOnPeak || isSelectPeak || isMatch ) {
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
				g.setFont(g.getFont().deriveFont(fontSize));
				g.setColor(color);
				
				String mzStr = formatMass(mz, false);
				g.drawString(mzStr, x, y - 1);
			}

			//----------------------------------------------------
			// 分子式を描画
			//----------------------------------------------------
			if ( isMatch && !formula.equals("") && !displayedFormulaList.contains(formula) ) {
				displayedFormulaList.add(formula);
				int y2 = this.panelHeight - MARGIN - (int)(its * applet.yscale) - 13;
				int xm = (int)(formula.length() * 5.4) + 12;
				g.setColor(new Color(0xFF, 0x45, 0x00));
				g.fillRect( x - 1, y2 - 9, xm, 11 );
				g.setColor( Color.WHITE );
				g.setFont(g.getFont().deriveFont(10.0f));
				g.drawString( formula, x, y2 );
			}

			//----------------------------------------------------
			// 強度値を描画
			//----------------------------------------------------
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
	 * マウスでドラッグした領域を黄色い線で囲む
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
				init();
//				repaint();
			}
		}
	}

	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}

	/**
	 * マウスムーブイベント
	 * @see java.awt.event.MouseMotionListener#mouseClicked(java.awt.event.MouseEvent)
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
	 * m/zの表示用フォーマット
	 * 画面表示用にm/zの桁数を合わせて返却する
	 * @param mass フォーマット対象のm/z
	 * @param isForce 桁数強制統一フラグ（true:0埋めと切捨てを行う、false:切捨てのみ行う）
	 * @return フォーマット後のm/z
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
	 * 拡大処理をアニメーション化するクラス
	 */
	class AnimationTimer implements ActionListener {
		private static final int LOOP = 15;
		private int loopCoef;
		private int minx;
		private double tmpMassStart;
		private double tmpMassRange;
		private int tmpIntensityRange;
		private int movex;

		/**
		 * 
		 */
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
				double start = Math.max(this.tmpMassStart, 0.0d);
				int max = peak.getMaxIntensity(start, start + this.tmpMassRange);
				this.tmpIntensityRange = (int)((1.0d + max / 50.0d) * 50.0d);
				if ( this.tmpIntensityRange > INTENSITY_MAX ) {
					this.tmpIntensityRange = INTENSITY_MAX;
				}
			}
		}

		/**
		 * 
		 */
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
}

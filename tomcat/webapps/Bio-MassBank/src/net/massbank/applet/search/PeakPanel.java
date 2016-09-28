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
 * PeakPanel.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.applet.search;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import net.massbank.applet.common.Peak;

/**
 * ピークパネル クラス
 */
@SuppressWarnings("serial")
public class PeakPanel extends JPanel {

	public static final int INTENSITY_MAX = 1000;
	private static final int MARGIN = 12;
	private static final int MASS_RANGE_MIN = 5;
	private static int massRangeMax = 0;
	private Peak peaks1 = null;
	private Peak peaks2 = null;
	private double massStart = 0;
	private double massRange = 0;
	private int intensityRange = INTENSITY_MAX;
	private boolean head2tail = false;
	private Point fromPos = null;
	private Point toPos = null;
	private double xscale = 0;
	private String tolVal = null;
	private boolean tolUnit = true;
	private Point cursorPoint = null;
	private String typeLbl1 = " ";
	private String typeLbl2 = " ";
	public static final String SP_TYPE_QUERY = "Query";
	public static final String SP_TYPE_COMPARE = "Compare";
	public static final String SP_TYPE_RESULT = "Result";
	
	private static final String SP_TYPE_MERGED = "MERGED SPECTRUM";
	private int TYPE_LABEL_1 = 1;
	private int TYPE_LABEL_2 = 2;
	
	private JLabel nameLbl = null;
	private int precursor = 0;
	private boolean isNoPeak = false;
	private ArrayList<String> selectPeakList = null;

	private long lastClickedTime = 0;

	private JButton leftMostBtn = null;
	private JButton leftBtn = null;
	private JButton rightBtn = null;
	private JButton rightMostBtn = null;

	private JToggleButton mzDisp = null;
	private JToggleButton mzHitDisp = null;

	private static boolean isInitRate = false;

	public BufferedImage structImgM = null;
	public BufferedImage structImgS = null;
	public String formula = "";
	public String emass = "";
	private SearchApplet applet = null;

	/**
	 *
	 */
	public PeakPanel(boolean isHead2Tail, SearchApplet applet) {
		this.applet = applet;
		selectPeakList = new ArrayList<String>();
		head2tail = isHead2Tail;
		
		if ( head2tail ) {
			typeLbl1 = SP_TYPE_COMPARE;
			typeLbl2 = " ";
		}
		
		GridBagConstraints gbc = null;
		GridBagLayout gbl = new GridBagLayout();
		
		JPanel typePane1 = new TypePane(TYPE_LABEL_1, new Color(153 , 153, 153), 16);
		typePane1.setMinimumSize(new Dimension(22, 76));
		typePane1.setPreferredSize(new Dimension(22, 76));
		typePane1.setMaximumSize(new Dimension(22, 76));
		
		JPanel typePane2 = new TypePane(TYPE_LABEL_2, new Color(0 , 0, 255), 9);
		typePane2.setPreferredSize(new Dimension(22, 0));
		
		JPanel typePane = new JPanel();
		typePane.setLayout(new BoxLayout(typePane, BoxLayout.Y_AXIS));
		typePane.add(typePane1);
		typePane.add(typePane2);
		
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.weightx = 0;
		gbc.weighty = 1;
		gbc.gridheight = GridBagConstraints.REMAINDER;
		gbl.setConstraints(typePane, gbc);	
		
		
		PlotPane plotPane = new PlotPane();		
		
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbl.setConstraints(plotPane, gbc);
		
		
		ButtonPane btnPane =new ButtonPane();	
		
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.gridheight = GridBagConstraints.REMAINDER;
		gbl.setConstraints(btnPane, gbc);
		
		setLayout(gbl);
		add(typePane);
		add(plotPane);
		add(btnPane);
	}

	/**
	 *
	 */
	class TypePane extends JPanel {
		
		private int lblNo = -1;
		private Color fontColor = new Color(0, 0, 0);
		private int fontSize = 1;
		
		/**
		 *
		 */
		private TypePane() {
		}
		
		/**
		 *
		 */
		public TypePane(int lbl, Color color, int size) {
			this.lblNo = lbl;
			this.fontColor = color;
			this.fontSize = size;
		}
		
		/**
		 *
		 */
	    public void paintComponent(Graphics g) {
			if ((!head2tail && peaks1 != null) 
					|| (head2tail && peaks2 != null)
					|| (!head2tail && peaks1 == null && isNoPeak) ) {
				Graphics2D g2 = (Graphics2D)g;
				
				g2.setPaint(fontColor);
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				FontRenderContext frc = new FontRenderContext(null, true, true);
				Font font;
				Shape shape;
				if (lblNo == TYPE_LABEL_1) {
					font = new Font("Arial", Font.ITALIC, fontSize);
					shape = new TextLayout(typeLbl1, font, frc).getOutline(null);
				}
				else {
					font = new Font("Arial", Font.ITALIC | Font.BOLD, fontSize);
					shape = new TextLayout(typeLbl2, font, frc).getOutline(null);
				}
				Rectangle2D b = shape.getBounds();
				AffineTransform at1 = AffineTransform.getRotateInstance(Math.toRadians(-90), b.getX(), b.getY());
				AffineTransform at2;
				if (lblNo == TYPE_LABEL_1) {
					at2 = AffineTransform.getTranslateInstance(3, b.getWidth() + b.getHeight() + 5);
				}
				else {
					at2 = AffineTransform.getTranslateInstance(7, getHeight() + 1);
				}
				g2.fill(at2.createTransformedShape(at1.createTransformedShape(shape)));
			}
		}
	}
	
	/**
	 *
	 */
	class PlotPane extends JPanel implements MouseListener, MouseMotionListener {
		
		private JPopupMenu selectPopup = null;
		private JPopupMenu contextPopup = null;

		private Timer timer = null;
		
		private boolean underDrag = false;
		
		private final int STATUS_NORAML = 0;
		private final int STATUS_NEXT_LAST = 1;
		private final int STATUS_CLOSED = 2;
		
		private final Color onCursorColor = Color.blue;
		
		/**
		 *
		 */
		public PlotPane() {
			cursorPoint = new Point();
			addMouseListener(this);
			addMouseMotionListener(this);
		}

		/**
		 *
		 */
		private int stepCalc(int range) {
			if (range < 10) {
				return 1;
			}
			if (range < 20) {
				return 2;
			}
			if (range < 50) {
				return 5;
			}
			if (range < 100) {
				return 10;
			}
			if (range < 250) {
				return 25;
			}
			if (range < 500) {
				return 50;
			}
			return 100;
		}

		/**
		 *
		 */
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			int width = getWidth();
			int height = getHeight();
			xscale = (width - 2.0d * MARGIN) / massRange;
			double yscale = (height - 2.0d * MARGIN) / intensityRange;
			g.setColor(Color.white);
			g.fillRect(0, 0, width, height);

			if ( !head2tail && peaks1 != null) {
				boolean isSizeM = false;
				if ( structImgM != null && height > structImgM.getHeight() ) {
					g.drawImage(structImgM, (width - structImgM.getWidth()), 0, null);
					isSizeM = true;
				}
				else if ( structImgS != null && height > structImgS.getHeight() ) {
					g.drawImage(structImgS, (width - structImgS.getWidth()), 5, null);
					isSizeM = false;
				}

				if ( !formula.equals("") ) {
					String info = formula + " (" + emass + ")";
					int xPos = 0;
					int fontSize = 0;
					if ( isSizeM ) {
						xPos = width - info.length() * 7;
						fontSize = 12;
					}
					else {
						xPos = width - info.length() * 6;
						fontSize = 10;
					}
					g.setFont(new Font("SansSerif",Font.BOLD,fontSize));
					g.setColor(new Color(0x008000));
					g.drawString(info, xPos - 2, 12);
				}
			}

			g.setFont(g.getFont().deriveFont(9.0f));
			g.setColor(Color.lightGray);
			if (!head2tail) {
				g.drawLine(MARGIN, MARGIN, MARGIN, height - MARGIN);
				g.drawLine(MARGIN, height - MARGIN, width - MARGIN, height
						- MARGIN);
				int step = stepCalc((int)massRange);
				int start = (step - (int)massStart % step) % step;
				for (int i = start; i < (int)massRange; i += step) {
					g.drawLine(MARGIN + (int)(i * xscale), height - MARGIN,
							MARGIN + (int)(i * xscale), height - MARGIN + 2);
					g.drawString(formatMass(i + massStart, true), MARGIN
							+ (int)(i * xscale) - 5, height - 1);
				}
				for (int i = 0; i <= intensityRange; i += intensityRange / 5) {
					g.drawLine(MARGIN - 2,
							height - MARGIN - (int)(i * yscale), MARGIN,
							height - MARGIN - (int)(i * yscale));
					g.drawString(String.valueOf(i), 0, height - MARGIN
							- (int)(i * yscale));
				}
			} else {
				g.drawLine(MARGIN, MARGIN, MARGIN, height - MARGIN);
				g.drawLine(MARGIN, height / 2, width - MARGIN, height / 2);
				int step = stepCalc((int)massRange);
				int start = (step - (int)massStart % step) % step;
				for (int i = start; i < (int)massRange; i += step) {
					g.drawLine(MARGIN + (int)(i * xscale), height / 2 + 1,
							MARGIN + (int)(i * xscale), height / 2 - 1);

					g.drawString(formatMass(i + massStart, true), MARGIN
							+ (int)(i * xscale) - 5, height - 1);
				}
				for (int i = 0; i <= intensityRange; i += intensityRange / 5) {
					g.drawLine(MARGIN - 2, height / 2 - (int)(i * yscale) / 2,
							MARGIN, height / 2 - (int)(i * yscale) / 2);

					g.drawString(String.valueOf(i), 0, height / 2
							- (int)(i * yscale) / 2);

					g.drawLine(MARGIN - 2, height / 2 + (int)(i * yscale) / 2,
							MARGIN, height / 2 + (int)(i * yscale) / 2);

					g.drawString(String.valueOf(i), 0, height / 2
							+ (int)(i * yscale) / 2);
				}
			}
			if (!head2tail) {
				int start, end;
				if (peaks1 != null) {
					int its, x, y, w, h;
					double mz;
					boolean isOnPeak;
					boolean isSelectPeak;
					
					start = peaks1.getIndex(massStart);
					end = peaks1.getIndex(massStart + massRange);
					
					for (int i=start; i<end; i++) {
						
						mz = peaks1.getMz(i);
						its = peaks1.getIntensity(i);
						isOnPeak = false;
						isSelectPeak = peaks1.isSelectPeakFlag(i);
						
						x = MARGIN + (int)((mz - massStart) * xscale) - (int)Math.floor(xscale / 8);
						y = height - MARGIN - (int)(its * yscale);
						w = (int)(xscale / 8);
						h = (int)(its * yscale);
						
						if (h == 0) {
							y -= 1;
							h = 1;
						}
						if (w < 2) {
							w = 2;
						} else if (w < 3) {
							w = 3;
						}
						
						if (MARGIN >= x) {
							w = (w - (MARGIN - x) > 0) ? (w - (MARGIN - x)) : 1;
							x = MARGIN + 1;
						}

						if (x <= cursorPoint.getX() 
								&& cursorPoint.getX() <= (x + w)
								&& y <= cursorPoint.getY() 
								&& cursorPoint.getY() <= (y + h)) {
							
							isOnPeak = true;
						}
						
						
						g.setColor(Color.black);
						g.setFont(g.getFont().deriveFont(9.0f));
						if (isOnPeak) {
							g.setColor(onCursorColor);
							g.setFont(g.getFont().deriveFont(14.0f));
							if (isSelectPeak) {
								g.setColor(Color.cyan.darker());
							}
							g.drawString(formatMass(mz, false), x, y);
						}
						else if (isSelectPeak) {
							g.setColor(Color.cyan.darker());
							g.drawString(formatMass(mz, false), x, y);
						}
						else if (mzDisp.isSelected()) {
							if (its > intensityRange * 0.4) {
								g.setColor(Color.red);
							}
							g.drawString(formatMass(mz, false), x, y);
							g.setColor(Color.black);
						}
						else {
							if (its > intensityRange * 0.4) {
								g.drawString(formatMass(mz, false), x, y);
							}
						}
						g.fill3DRect(x, y, w, h, true);
						
						
						if (isOnPeak || isSelectPeak) {
							if (isOnPeak) {
								g.setColor(onCursorColor);
							}
							if (isSelectPeak) {
								g.setColor(Color.cyan.darker());
							}
							g.drawLine(MARGIN + 4, y, MARGIN - 4, y);
							g.setColor(Color.lightGray);
							g.setFont(g.getFont().deriveFont(9.0f));
							if (isOnPeak && isSelectPeak) {
								g.setColor(Color.gray);
							}
							g.drawString(String.valueOf(its), MARGIN + 7, y + 1);
						}
					}

					if ( PeakPanel.this.precursor > 0 ) {
						int preX = MARGIN + (int)((PeakPanel.this.precursor - massStart) * xscale) - (int)Math.floor(xscale / 8);
						if ( preX >= MARGIN 
								&& preX <= width - MARGIN ) {
							
							int[] xp = { preX, preX+6, preX-6 };
							int[] yp = { height - MARGIN, height-MARGIN+5, height-MARGIN+5 };
							g.setColor( Color.RED );
							g.fillPolygon( xp, yp, 3 );
						}
					}
					
					allBtnCtrl(true);
				}
				else if (isNoPeak) {
					g.setFont(new Font("Arial", Font.ITALIC, 24));
					g.setColor(Color.lightGray);
					g.drawString("No peak was observed.", width / 2 - 110,
							height / 2);
					allBtnCtrl(false);
				} else {
					selectPeakList.clear();
					allBtnCtrl(false);
				}
			}
			else if (peaks2 != null) {
				int start1 = peaks1.getIndex(massStart);
				int end1 = peaks1.getIndex(massStart + massRange);
				int start2 = peaks2.getIndex(massStart);
				int end2 = peaks2.getIndex(massStart + massRange);
				if (end1 > 0) {
					end1 -= 1;
				}
				if (end2 > 0) {
					end2 -= 1;
				}
				int ind1 = start1;
				int ind2 = start2;
				double mz1 = peaks1.getMz(ind1);
				double mz2 = peaks2.getMz(ind2);
				int its1 = peaks1.getIntensity(ind1);
				int its2 = peaks2.getIntensity(ind2);

				int x = 0, y = 0, y2 = 0, w = 0, h = 0, h2 = 0;
				boolean isMz1Update = false;
				boolean isMz2Update = false;
				int mz1status = STATUS_NORAML;
				int mz2status = STATUS_NORAML;
				if (peaks1.getMz(end1) < massStart) {
					mz1status = STATUS_CLOSED;
				}
				if (peaks2.getMz(end2) < massStart) {
					mz2status = STATUS_CLOSED;
				}
				boolean isMatchPeak = false;
				while (mz1status < STATUS_CLOSED || mz2status < STATUS_CLOSED) {
					isMz1Update = false;
					isMz2Update = false;
					isMatchPeak = false;
					if (ind1 == end1 && mz1status == STATUS_NORAML) {
						mz1status = STATUS_NEXT_LAST;
					}
					if (ind2 == end2 && mz2status == STATUS_NORAML) {
						mz2status = STATUS_NEXT_LAST;
					}

					w = (int) (xscale / 8);
					if (w < 2) {
						w = 2;
					} else if (w < 3) {
						w = 3;
					}
					
					g.setColor(Color.black);
					
					if (mz1 == mz2) {
						
						isMatchPeak = true;
						x = MARGIN + (int)((mz1 - massStart) * xscale) - (int)Math.floor(xscale / 8);
						y = height / 2 - (int)((its1 * yscale) / 2);
						y2 = height / 2 + 1;
						h = (int)((its1 * yscale) / 2);
						h2 = (int)((its2 * yscale) / 2);
						if (h == 0) {
							h = 1;
						}
						if (h2 == 0) {
							h2 = 1;
						}

						if (MARGIN > x) {
							w = (w - (MARGIN - x) > 0) ? (w - (MARGIN - x)) : 1;
							x = MARGIN + 1;
						}
						
						if (mzDisp.isSelected()) {
							if ((int)(its1 * yscale) / 2 >= ((height - MARGIN * 2) / 2) * 0.4) {
								g.setColor(Color.red);
							}
							g.drawString(formatMass(mz1, false), x, y);
							g.setColor(Color.black);

							if ((int)(its2 * yscale) / 2 >= ((height - MARGIN * 2) / 2) * 0.4) {
								g.setColor(Color.red);
							}
							g.drawString(formatMass(mz2, false), x, (y2 + h2 + 7));
							g.setColor(Color.black);
						} else {
							if (!mzHitDisp.isSelected()) {
								if ((int)(its1 * yscale) / 2 >= ((height - MARGIN * 2) / 2) * 0.4) {
									g.drawString(formatMass(mz1, false), x, y);
								}
								if ((int)(its2 * yscale) / 2 >= ((height - MARGIN * 2) / 2) * 0.4) {
									g.drawString(formatMass(mz2, false), x, (y2
											+ h2 + 7));
								}
							}
						}

						if (its1 >= SearchApplet.CUTOFF_THRESHOLD
								&& its2 >= SearchApplet.CUTOFF_THRESHOLD) {
							g.setColor(Color.red);

							if (mzHitDisp.isSelected()) {
								g.drawString(formatMass(mz1, false), x, y);
								g.drawString(formatMass(mz2, false), x,
										(y2 + h2 + 7));
							}
						}

						if (mz1status == STATUS_NEXT_LAST) {
							mz1status = STATUS_CLOSED;
						}
						if (mz2status == STATUS_NEXT_LAST) {
							mz2status = STATUS_CLOSED;
						}
						isMz1Update = true;
						isMz2Update = true;
					} else if ((mz2 < mz1 && mz2status != STATUS_CLOSED)
							|| mz1status == STATUS_CLOSED) {
						x = MARGIN + (int)((mz2 - massStart) * xscale) - (int)Math.floor(xscale / 8);
						y = height / 2 + 1;
						h = (int)(its2 * yscale / 2);
						
						if (h == 0) {
							h = 1;
						}
						if (MARGIN > x) {
							w = (w - (MARGIN - x) > 0) ? (w - (MARGIN - x)) : 1;
							x = MARGIN + 1;
						}
						if (mzDisp.isSelected()) {
							if (h >= ((height - MARGIN * 2) / 2) * 0.4) {
								g.setColor(Color.red);
							}
							g.drawString(formatMass(mz2, false), x, (y + h + 7));
							g.setColor(Color.black);
						} else if (!mzHitDisp.isSelected()
								&& h >= ((height - MARGIN * 2) / 2) * 0.4) {
							g.drawString(formatMass(mz2, false), x, (y + h + 7));
						}

						if (checkTolerance(true, mz2, its2, peaks1)) {
							g.setColor(Color.magenta);
							if (mzHitDisp.isSelected()) {
								g.drawString(formatMass(mz2, false), x,
										(y + h + 7));
							}
						}

						if (mz2status == STATUS_NEXT_LAST) {
							mz2status = STATUS_CLOSED;
						}
						isMz2Update = true;
					} else if ((mz1 < mz2 && mz1status != STATUS_CLOSED)
							|| mz2status == STATUS_CLOSED) {
						x = MARGIN + (int)((mz1 - massStart) * xscale) - (int)Math.floor(xscale / 8);
						y = height / 2 - (int)((its1 * yscale) / 2);
						h = (int)((its1 * yscale) / 2);
						if (h == 0) {
							h = 1;
						}
						if (MARGIN > x) {
							w = (w - (MARGIN - x) > 0) ? (w - (MARGIN - x)) : 1;
							x = MARGIN + 1;
						}
						if (mzDisp.isSelected()) {
							if (h >= ((height - MARGIN * 2) / 2) * 0.4) {
								g.setColor(Color.red);
							}
							g.drawString(formatMass(mz1, false), x, y);
							g.setColor(Color.black);
						} else if (!mzHitDisp.isSelected()
								&& h >= ((height - MARGIN * 2) / 2) * 0.4) {
							g.drawString(formatMass(mz1, false), x, y);
						}

						if (checkTolerance(false, mz1, its1, peaks2)) {
							g.setColor(Color.red);
							if (mzHitDisp.isSelected()) {
								g.drawString(formatMass(mz1, false), x, y);
							}
						}

						if (mz1status == STATUS_NEXT_LAST) {
							mz1status = STATUS_CLOSED;
						}
						isMz1Update = true;
					} else {
					}

					g.fill3DRect(x, y, w, h, true);
					if (isMatchPeak) {
						g.fill3DRect(x, y2, w, h2, true);
					}
					g.setColor(Color.black);

					if (isMz1Update) {
						if (ind1 < end1) {
							mz1 = peaks1.getMz(++ind1);
							its1 = peaks1.getIntensity(ind1);
						}
					}
					if (isMz2Update) {
						if (ind2 < end2) {
							mz2 = peaks2.getMz(++ind2);
							its2 = peaks2.getIntensity(ind2);
						}
					}
				}
				allBtnCtrl(true);
			} else {
				allBtnCtrl(false);
				if (head2tail) {
					mzHitDisp.setSelected(false);
					mzHitDisp.setEnabled(false);
				}
			}

			if ((!head2tail && peaks1 != null) || (head2tail && peaks2 != null)) {
				if (underDrag) {
					g.setXORMode(Color.white);
					g.setColor(Color.yellow);
					int xpos = Math.min(fromPos.x, toPos.x);
					width = Math.abs(fromPos.x - toPos.x);
					g.fillRect(xpos, MARGIN, width, height - MARGIN * 2);
					g.setPaintMode();
				}
			}
		}

		/**
		 *
		 */
		public void mousePressed(MouseEvent e) {
			if (SwingUtilities.isLeftMouseButton(e)) {
				if (timer != null && timer.isRunning()) {
					return;
				}

				fromPos = toPos = e.getPoint();
			}
		}

		/**
		 *
		 */
		public void mouseDragged(MouseEvent e) {
			if (SwingUtilities.isLeftMouseButton(e)) {
				if (timer != null && timer.isRunning()) {
					return;
				}

				underDrag = true;
				toPos = e.getPoint();
				PeakPanel.this.repaint();
			}
		}

		/**
		 *
		 */
		public void mouseReleased(MouseEvent e) {
			if (SwingUtilities.isLeftMouseButton(e)) {
				if (!underDrag || (timer != null && timer.isRunning())) {
					return;
				}
				underDrag = false;
				if ((fromPos != null) && (toPos != null)) {
					if (Math.min(fromPos.x, toPos.x) < 0)
						massStart = Math.max(0, massStart - massRange / 3);

					else if (Math.max(fromPos.x, toPos.x) > getWidth())
						massStart = Math.min(massRangeMax - massRange, massStart
								+ massRange / 3);
					else {
						if ((!head2tail && peaks1 != null)
								|| (head2tail && peaks2 != null)) {

							PeakPanel.this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
							
							isInitRate = false;
							
							timer = new Timer(30,
									new AnimationTimer(Math.abs(fromPos.x - toPos.x),
											Math.min(fromPos.x, toPos.x)));
							timer.start();
						} else {
							fromPos = toPos = null;
							PeakPanel.this.repaint();
						}
					}
				}
			}
			else if (SwingUtilities.isRightMouseButton(e)) {
				
				if (timer != null && timer.isRunning()) {
					return;
				}
				
				if (head2tail) {
					return;
				}
				
				contextPopup = new JPopupMenu();
				
				JMenuItem item1 = null;
				item1 = new JMenuItem("Peak Search");
				item1.setActionCommand("search");
				item1.addActionListener(new ContextPopupListener());
				item1.setEnabled(false);
				contextPopup.add(item1);
				
				JMenuItem item2 = null;
				item2 = new JMenuItem("Select Reset");
				item2.setActionCommand("reset");
				item2.addActionListener(new ContextPopupListener());
				item2.setEnabled(false);
				contextPopup.add(item2);
				
				if (peaks1 != null) {
					if (selectPeakList.size() != 0) {
						item1.setEnabled(true);
						item2.setEnabled(true);
					}
				}
				
				contextPopup.show(e.getComponent(), e.getX(), e.getY());
			}
		}

		/**
		 *
		 */
		public void mouseClicked(MouseEvent e) {
			if (timer != null && timer.isRunning()) {
				return;
			}

			if (SwingUtilities.isLeftMouseButton(e)) {
				long interSec = (e.getWhen() - lastClickedTime);
				lastClickedTime = e.getWhen();
				if (interSec <= 280) {
					if ((!head2tail && peaks1 != null)
							|| (head2tail && peaks2 != null)) {
						applet.setAllPlotAreaRange();
						fromPos = toPos = null;
						intensityRange = INTENSITY_MAX;
						isInitRate = true;
					}
				}
				else {
					if (applet == null) {
						return;
					}
					Point p = e.getPoint();
					if (head2tail || peaks1 == null) {
						return;
					}

					ArrayList<Integer> tmpClickPeakList = new ArrayList<Integer>();

					int height = getHeight();
					double yscale = (height - 2.0d * MARGIN) / intensityRange;
					int start, end, its, tmpX, tmpY, tmpWidth, tmpHight;
					double mz;
					start = peaks1.getIndex(massStart);
					end = peaks1.getIndex(massStart + massRange);

					for (int i = start; i < end; i++) {

						mz = peaks1.getMz(i);
						its = peaks1.getIntensity(i);
						tmpX = MARGIN + (int) ((mz - massStart) * xscale)
								- (int) Math.floor(xscale / 8);
						tmpY = height - MARGIN - (int) (its * yscale);
						tmpWidth = (int) (xscale / 8);
						tmpHight = (int) (its * yscale);

						if (MARGIN > tmpX) {
							tmpWidth = tmpWidth - (MARGIN - tmpX);
							tmpX = MARGIN;
						}

						if (tmpWidth < 2) {
							tmpWidth = 2;
						} else if (tmpWidth < 3) {
							tmpWidth = 3;
						}
						if (tmpX <= p.getX() && p.getX() <= (tmpX + tmpWidth)
								&& tmpY <= p.getY()
								&& p.getY() <= (tmpY + tmpHight)) {

							tmpClickPeakList.add(i);
						}
					}
					if (tmpClickPeakList.size() == 1) {
						int index = tmpClickPeakList.get(0);
						if (!peaks1.isSelectPeakFlag(index)) {
							if (peaks1.getSelectPeakNum() < SearchApplet.PEAK_SEARCH_PARAM_NUM) {
								selectPeakList.add(String.valueOf(peaks1
										.getMz(index)));
								peaks1.setSelectPeakFlag(index, true);
							} else {
								JOptionPane.showMessageDialog(PeakPanel.this,
										"<html>&nbsp;<i>m/z</i> of " + SearchApplet.PEAK_SEARCH_PARAM_NUM + " peak or more cannot be selected.&nbsp;</html>",
										"Warning",
										JOptionPane.WARNING_MESSAGE);
								cursorPoint = new Point();
							}
						} else if (peaks1.isSelectPeakFlag(index)) {
							selectPeakList.remove(String.valueOf(peaks1
									.getMz(index)));
							peaks1.setSelectPeakFlag(index, false);
						}
						PeakPanel.this.repaint();
					}
					else if (tmpClickPeakList.size() >= 2) {
						selectPopup = new JPopupMenu();
						JMenuItem item = null;
						int index = -1;
						for (int i = 0; i < tmpClickPeakList.size(); i++) {
							index = tmpClickPeakList.get(i);
							item = new JMenuItem(String.valueOf(peaks1.getMz(index)));
							selectPopup.add(item);
							item.addActionListener(new SelectMZPopupListener(index));

							if (peaks1.getSelectPeakNum() >= SearchApplet.PEAK_SEARCH_PARAM_NUM
									&& !peaks1.isSelectPeakFlag(index)) {
								item.setEnabled(false);
							}
						}
						selectPopup.show(e.getComponent(), e.getX(), e.getY());
					}
				}
			}
		}

		/**
		 *
		 */
		public void mouseEntered(MouseEvent e) {
		}

		/**
		 *
		 */
		public void mouseExited(MouseEvent e) {
		}

		/**
		 *
		 */
		public void mouseMoved(MouseEvent e) {
			if (applet == null || head2tail || peaks1 == null) {
				return;
			}
			if ((selectPopup != null && selectPopup.isVisible())
					|| contextPopup != null && contextPopup.isVisible()) {
				
				return;
			}
			
			cursorPoint = e.getPoint();
			PeakPanel.this.repaint();
		}
		

		/**
		 *
		 */
		class AnimationTimer implements ActionListener {
			
			private final int LOOP = 15;
			private int loopCoef;
			private int toX;
			private int fromX;
			private double tmpMassStart;
			private double tmpMassRange;
			private int tmpIntensityRange;
			private int movex;

			/**
			 *
			 */
			public AnimationTimer(int from, int to) {
				loopCoef = 0;
				toX = to;
				fromX = from;
				movex = 0 + MARGIN;
				double xs = (getWidth() - 2.0d * MARGIN) / massRange;
				tmpMassStart = massStart + ((toX - MARGIN) / xs);
				tmpMassRange = 10 * (fromX / (10 * xs));
				if (tmpMassRange < MASS_RANGE_MIN) {
					tmpMassRange = MASS_RANGE_MIN;
				}
				if ((peaks1 != null) && (massRange <= massRangeMax)) {
					int max = 0;
					double start = Math.max(tmpMassStart, 0.0d);
					max = applet.getMaxIntensity(start, start + tmpMassRange);
					if (peaks2 != null)
						max = Math.max(max, peaks2.getMaxIntensity(start, start
								+ tmpMassRange));
					tmpIntensityRange = (int) ((1.0d + max / 50.0d) * 50.0d);
					if (tmpIntensityRange > INTENSITY_MAX)
						tmpIntensityRange = INTENSITY_MAX;
				}
			}

			/**
			 *
			 */
			public void actionPerformed(ActionEvent e) {
				xscale = (getWidth() - 2.0d * MARGIN) / massRange;
				int xpos = (movex + toX) / 2;
				if (Math.abs(massStart - tmpMassStart) <= 2
						&& Math.abs(massRange - tmpMassRange) <= 2) {
					xpos = toX;
					massStart = tmpMassStart;
					massRange = tmpMassRange;
					timer.stop();
					applet.setAllPlotAreaRange(PeakPanel.this);
					PeakPanel.this.setCursor(Cursor.getDefaultCursor());
				} else {
					loopCoef++;
					massStart = massStart
							+ (((tmpMassStart + massStart) / 2 - massStart)
									* loopCoef / LOOP);
					massRange = massRange
							+ (((tmpMassRange + massRange) / 2 - massRange)
									* loopCoef / LOOP);
					intensityRange = intensityRange
							+ (((tmpIntensityRange + intensityRange) / 2 - intensityRange)
									* loopCoef / LOOP);
					if (loopCoef >= LOOP) {
						movex = xpos;
						loopCoef = 0;
					}
				}
				PeakPanel.this.repaint();
			}
		}
		
		/**
		 *
		 */
		class SelectMZPopupListener implements ActionListener {
			private int index = -1;

			/**
			 *
			 */
			public SelectMZPopupListener(int index) {
				this.index = index;
			}

			/**
			 *
			 */
			public void actionPerformed(ActionEvent e) {

				if (!peaks1.isSelectPeakFlag(index)
						&& peaks1.getSelectPeakNum() < SearchApplet.PEAK_SEARCH_PARAM_NUM) {
					selectPeakList.add(String.valueOf(peaks1.getMz(index)));
					peaks1.setSelectPeakFlag(index, true);
				} else if (peaks1.isSelectPeakFlag(index)) {
					selectPeakList.remove(String.valueOf(peaks1.getMz(index)));
					peaks1.setSelectPeakFlag(index, false);
				}

				cursorPoint = new Point();
				PeakPanel.this.repaint();
			}
		}
		
		/**
		 *
		 */
		class ContextPopupListener implements ActionListener {
			
			/**
			 *
			 */
			public ContextPopupListener() {
			}

			/**
			 *
			 */
			public void actionPerformed(ActionEvent e) {
				String com = e.getActionCommand();
				if ( com.equals("search") ) {
					String param = "searchType=peak&tol=0&inte=5&op0=and";
					for ( int i = 0; i < peaks1.getSelectPeakNum(); i++ ) {
						param += "&mz" + i + "=" + selectPeakList.get(i);
					}
					String reqUrl = SearchApplet.baseUrl + "mbtools/SearchResult.jsp?" + param;
					try {
						SearchApplet.context.showDocument(new URL(reqUrl), "_blank");
					}
					catch (Exception ex) {
						ex.printStackTrace();
					}
				}
				else if ( com.equals("reset") ) {
					if ( peaks1 != null ) {
						selectPeakList = new ArrayList<String>();
						peaks1.initSelectPeakFlag();
					}
				}
				cursorPoint = new Point();
				PeakPanel.this.repaint();
			}
		}
	}
	
	/**
	 *
	 */
	class ButtonPane extends JPanel implements ActionListener {
		/**
		 *
		 */
		public ButtonPane() {
			leftMostBtn = new JButton("<<");
			leftMostBtn.setActionCommand("<<");
			leftMostBtn.addActionListener(this);
			leftMostBtn.setMargin(new Insets(0, 0, 0, 0));
			leftMostBtn.setEnabled(false);

			leftBtn = new JButton(" < ");
			leftBtn.setActionCommand("<");
			leftBtn.addActionListener(this);
			leftBtn.setMargin(new Insets(0, 0, 0, 0));
			leftBtn.setEnabled(false);

			rightBtn = new JButton(" > ");
			rightBtn.setActionCommand(">");
			rightBtn.addActionListener(this);
			rightBtn.setMargin(new Insets(0, 0, 0, 0));
			rightBtn.setEnabled(false);

			rightMostBtn = new JButton(">>");
			rightMostBtn.setActionCommand(">>");
			rightMostBtn.addActionListener(this);
			rightMostBtn.setMargin(new Insets(0, 0, 0, 0));
			rightMostBtn.setEnabled(false);

			if (!isInitRate) {
				leftMostBtn.setEnabled(true);
				leftBtn.setEnabled(true);
				rightBtn.setEnabled(true);
				rightMostBtn.setEnabled(true);
			}
			else {
				leftMostBtn.setEnabled(false);
				leftBtn.setEnabled(false);
				rightBtn.setEnabled(false);
				rightMostBtn.setEnabled(false);
			}
			
			mzDisp = new JToggleButton("show all m/z");
			mzDisp.setActionCommand("mz");
			mzDisp.addActionListener(this);
			mzDisp.setMargin(new Insets(0, 0, 0, 0));
			mzDisp.setSelected(false);
			mzDisp.setEnabled(false);

			if (head2tail) {
				mzHitDisp = new JToggleButton("show hit m/z");
				mzHitDisp.setActionCommand("mzhit");
				mzHitDisp.addActionListener(this);
				mzHitDisp.setMargin(new Insets(0, 0, 0, 0));
				mzHitDisp.setSelected(false);
				mzHitDisp.setEnabled(false);
			}

			nameLbl = new JLabel();
			nameLbl.setForeground(Color.blue);
			
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			add(leftMostBtn);
			add(leftBtn);
			add(rightBtn);
			add(rightMostBtn);
			add(mzDisp);
			if (head2tail) {
				add(mzHitDisp);
			}
			else {
				add(nameLbl);
			}
		}

		/**
		 *
		 */
		public void actionPerformed(ActionEvent ae) {
			String com = ae.getActionCommand();
			if (com.equals("<<")) {
				massStart = Math.max(0, massStart - massRange);
			} else if (com.equals("<")) {
				massStart = Math.max(0, massStart - massRange / 4);
			} else if (com.equals(">")) {
				massStart = Math.min(massRangeMax - massRange, massStart + massRange / 4);
			} else if (com.equals(">>")) {
				massStart = Math.min(massRangeMax - massRange, massStart + massRange);
			} else if (com.equals("mz")) {
				if (head2tail && mzDisp.isSelected()) {
					mzHitDisp.setSelected(false);
				}
			} else if (com.equals("mzhit")) {
				if (mzHitDisp.isSelected()) {
					mzDisp.setSelected(false);
				}
			}
			applet.setAllPlotAreaRange(PeakPanel.this);
			PeakPanel.this.repaint();
		}
	}

	/**
	 *
	 */
	public void clear() {
		peaks1 = peaks2 = null;
		massStart = 0;
		massRangeMax = 0;
		massRange = 0;
		intensityRange = INTENSITY_MAX;
		isNoPeak = false;
		isInitRate = true;
		if (!head2tail) {
			setSpectrumInfo("", "", "", "", false);
		}
	}

	/**
	 *
	 */
	public void setPeaks(Peak p, int index) {
		if (index == 0) {
			peaks1 = p;
			if (!head2tail) {
				selectPeakList.clear();
			}
		} else if (index == 1) {
			peaks2 = p;
		}

		if (peaks1 != null) {
			massRange = peaks1.compMaxMzPrecusor(this.precursor);
		}

		if (peaks2 != null) {
			massRange = Math.max(peaks2.compMaxMzPrecusor(this.precursor), massRange);
			mzHitDisp.setEnabled(true);
			mzHitDisp.setSelected(true);
		}
		
		if (massRange != 0d && (massRange % 100.0d) == 0d) {
			massRange += 100.0d;
		}
		massRange = Math.ceil(massRange / 100.0d) * 100.0d;

		massStart = 0;
		intensityRange = INTENSITY_MAX;
		massRangeMax = (int)massRange;

		this.repaint();
	}

	/**
	 *
	 */
	public Peak getPeaks(int index) {
		if (index == 0) {
			return peaks1;
		}
		return peaks2;
	}

	/**
	 *
	 */
	public double getMassStart() {
		return massStart;
	}
	
	/**
	 *
	 */
	public double getMassRange() {
		return massRange;
	}
	

	/**
	 *
	 */
	public void setMass(double s, double r, int i) {
		massStart = s;
		massRange = r;
		intensityRange = i;
		this.repaint();
	}

	/**
	 *
	 */
	public int getIntensityRange() {
		return intensityRange;
	}

	/**
	 *
	 */
	public void setIntensityRange(int range) {
		intensityRange = range;
	}

	/**
	 *
	 */
	public void setApplet(SearchApplet obj) {
		applet = obj;
	}

	/**
	 *
	 */
	public void setTolerance(String val, boolean unit) {
		tolVal = val;
		tolUnit = unit;
	}
	
	
	/**
	 *
	 */
	public void setSpectrumInfo(String name, String key, String precursor, String spType, boolean invalid) {
		
		typeLbl1 = " ";
		typeLbl2 = " ";
		if (key.length() != 0 ) {
			typeLbl1 = spType;
			if ( !invalid ) {
				if ( name.indexOf("MERGED") != -1 ) {
					typeLbl2 = SP_TYPE_MERGED;
				}
			}
		}
		
		nameLbl.setText("  " + name);
		if (name.trim().length() != 0 && key.trim().length() != 0) {
			nameLbl.setToolTipText(key + ":  " + name);
		}

		if ( precursor.equals("") ) {
			this.precursor = 0;
		}
		else {
			this.precursor = Integer.parseInt(precursor);
		}
	}
	
	/**
	 *
	 */
	public int getPrecursor() {
		return PeakPanel.this.precursor;
	}
	
	/**
	 *
	 */
	public void setNoPeak(boolean isNoPeak) {
		this.isNoPeak = isNoPeak;
	}
	
	/**
	 *
	 */
	private void allBtnCtrl(boolean enable) {
		if (enable) {
			if (!isInitRate) {
				leftMostBtn.setEnabled(true);
				leftBtn.setEnabled(true);
				rightBtn.setEnabled(true);
				rightMostBtn.setEnabled(true);
			}
			else {
				leftMostBtn.setEnabled(false);
				leftBtn.setEnabled(false);
				rightBtn.setEnabled(false);
				rightMostBtn.setEnabled(false);
			}
		}
		else {
			leftMostBtn.setEnabled(false);
			leftBtn.setEnabled(false);
			rightBtn.setEnabled(false);
			rightMostBtn.setEnabled(false);
			mzDisp.setSelected(false);
		}
		
		mzDisp.setEnabled(enable);
	}
	
	/**
	 *
	 */
	private boolean checkTolerance(boolean mode, double compMz, int compIts, Peak peaks) {
		if (compIts < SearchApplet.CUTOFF_THRESHOLD) {
			return false;
		}

		double tolerance = 0;
		long lngTolerance = 0;
		long mz1;
		long mz2;
		int its1 = 0;
		int its2 = 0;
		long minusRange;
		long plusRange;
		final int TO_INTEGER_VAL = 100000;
		tolerance = Double.parseDouble(tolVal);
		
		mz1 = mz2 = (long) (compMz * TO_INTEGER_VAL);
		for (int i = peaks.getCount() - 1; i >= 0; i--) {
			if (mode) {
				mz1 = (long) (peaks.getMz(i) * TO_INTEGER_VAL);
				its1 = peaks.getIntensity(i);
			} else {
				mz2 = (long) (peaks.getMz(i) * TO_INTEGER_VAL);
				its2 = peaks.getIntensity(i);
			}
			if (tolUnit) {
				lngTolerance = (int) (tolerance * TO_INTEGER_VAL);
				minusRange = mz1 - lngTolerance;
				plusRange = mz1 + lngTolerance;
			}
			else {
				minusRange = (long) (mz1 * (1 - tolerance / 1000000));
				plusRange = (long) (mz1 * (1 + tolerance / 1000000));
			}

			if ((mode && plusRange < mz2) || (!mode && minusRange > mz2)) {
				return false;
			}

			if (minusRange <= mz2 && mz2 <= plusRange) {
				if ((mode && its1 >= SearchApplet.CUTOFF_THRESHOLD)
						|| (!mode && its2 >= SearchApplet.CUTOFF_THRESHOLD)) {

					return true;
				}
			}
		}
		return false;
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
	public void loadStructGif(String gifMUrl, String gifSUrl) {
		try {
			if ( !gifMUrl.equals("") ) {
				this.structImgM = ImageIO.read(new URL(gifMUrl));
			}
			else {
				this.structImgM = null;
			}
			if ( !gifSUrl.equals("") ) {
				this.structImgS = ImageIO.read(new URL(gifSUrl));
			}
			else {
				this.structImgS = null;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 *
	 */
	public void setCompoundInfo(String formula, String emass) {
		this.formula = formula;
		this.emass = emass;
	}
}

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
 * MolView.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.applet.molview;

import java.io.IOException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import net.massbank.applet.common.MolViewPanelExt;

@SuppressWarnings("serial")
public class MolView extends JApplet
{
	private static final int DEF_RECT_SIZE = 200;
	private String baseUrl = "";
	private String compoundName = "";
	private int rectSize = DEF_RECT_SIZE;
	private String outDir = "";
	private JApplet applet = this;
	private boolean isApplication = false;
	private String molfileName = "";
	private String moldata = "";
	private String gifPath = "";


	/**
	 * constructor
	 */
	public MolView() {}


	/**
	 * main
	 */
	public static void main(String[] args) {
		MolView molView = new MolView();
		molView.isApplication = true;
		for ( String param : args ) {
			param = param.replaceAll("\"", "");
			String[] items = param.split("=");
			if ( items.length != 2 ) {
				continue;
			}
			String key = items[0].trim();
			String val = items[1].trim();
			if ( key.equals("size") ) {
				if ( val != null && !val.equals("") ) {
					molView.rectSize = Integer.parseInt( val.trim() );
				}
			}
			else if ( key.equals("out_dir") ) {
				if ( !val.equals("") ) {
					molView.outDir = val.trim();
				}
			}
			else if ( key.equals("molfile") ) {
				molView.molfileName = val.trim();
			}
			else if ( key.equals("moldata") ) {
				molView.moldata = val;
			}
			else if ( key.equals("out_gif_path") ) {
				molView.gifPath = val;
			}
		}
		molView.init();
	}

	/**
	 * Initialization
	 */
	public void init() {
		boolean isMolExist = false;
		File outFile = null;

		if ( !isApplication ) {
			if ( getParameter("compound_name") != null ) {
				this.compoundName = getParameter("compound_name").trim();
			}
			if ( getParameter("size") != null ) {
				String size = getParameter("size").trim();
				rectSize = Integer.parseInt(size);
			}
			if ( getParameter("moldata") != null ) {
				isMolExist = true;
				String data = getParameter("moldata");
				moldata = data.replace("@data=", "");
				moldata = moldata.replace("@LF@", "\n");
			}
			if ( this.compoundName.equals("") && moldata.equals("") ) {
				System.out.println("No paramater.");
				return;
			}
		}
		else {
			if ( (molfileName.equals("") || this.outDir.equals("")) && gifPath.equals("") ) {
				System.out.println("No paramater.");
				return;
			}

			if ( !moldata.equals("") ) {
				isMolExist = true;
				moldata = moldata.replace("@LF@", "\n");
				moldata = moldata.replaceAll("M\\s\\s.*\n", "");
				moldata += "M  END\n";
			}
			if ( molfileName.endsWith(".mol") ) {
				File molfile = new File(molfileName);
				BufferedReader br = null;
				try {
					br = new BufferedReader(new FileReader(molfile));
					String line;
					while ( (line = br.readLine()) != null ) {
						if ( line.startsWith("M  ") && !line.equals("M  END") ) {
							continue;
						}
						moldata += line +"\n";
					}
				}
				catch ( Exception e ) {
					System.out.println("File not found.");
				}
				finally {
					if ( br != null ) {
						try { br.close();
						}
						catch (IOException ioe) {}
					}
				}
				if ( !moldata.equals("") ) {
					isMolExist = true;
				}
				outFile = new File((new File(this.outDir)).getPath() + "/"
						+ (new File(molfileName).getName()).replaceAll(".mol", ".gif"));
			}
			else if ( !gifPath.equals("") ) {
				outFile = new File(gifPath);
			}
		}

		if ( isMolExist ) {
			MolViewPanelExt mvp = new MolViewPanelExt(rectSize, true,
									isApplication, this.applet, this.compoundName, moldata);
			setSize( mvp.getPreferredSize().width, mvp.getPreferredSize().height );
			setContentPane(mvp);
			if ( isApplication ) {
				gifOut(mvp, outFile);
			}
		}
		else {
			JLabel lbl = new JLabel( "Not Available", JLabel.CENTER );
			lbl.setOpaque(true);
			lbl.setBackground(new Color(0xF8,0xF8,0xFF));
			lbl.setLayout(new BorderLayout());
			lbl.setSize(rectSize, rectSize);
			setContentPane(lbl);
			if ( isApplication ) {
				gifOut(lbl, outFile);
			}
		}
	}

	/**
	 * output gif image
	 */
	private boolean gifOut(JComponent c, File outFile) {
		boolean ret = false;
		if ( outFile == null ) {
			return ret;
		}
		BufferedImage bi = null;
		if ( c instanceof MolViewPanelExt ) {
			bi = new BufferedImage(rectSize, rectSize, BufferedImage.TYPE_INT_BGR);
			Graphics2D g2 = bi.createGraphics();
			g2.setColor(Color.white);
			g2.fillRect(0, 0, rectSize, rectSize);
			((MolViewPanelExt)c).paintComponent(g2);
		}
		else {
			bi = new BufferedImage(rectSize, rectSize, BufferedImage.TYPE_INT_BGR);
			c.paint(bi.createGraphics());
		}
		try {
			ImageIO.write(bi, "gif", outFile);
			ret = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}
}

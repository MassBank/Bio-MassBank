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
 * GetConfig.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.core.common;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import net.massbank.core.common.URLSubDirectory;

public class GetConfig {
	protected Element root = null;

	/**
	 * constructor1
	 */ 
	public GetConfig() {
		String baseUrl = trimBaseUrl("http://localhost/MassBankDevelKit/");
		setRootElement(baseUrl);
	}

	/**
	 * constructor2
	 */ 
	public GetConfig(String baseUrl) {
		setRootElement(trimBaseUrl(baseUrl));
	}

	/**
	 * constructor3
	 */ 
	public GetConfig(boolean isDirect) {
	}

	/**
	 * 
	 */ 
	public String[] getSiteLongNames() {
		return getSetting("LongName");
	}

	/**
	 * 
	 */ 
	public String[] getDbNames() {
		return getSetting("DB");
	}

	/**
	 * 
	 */ 
	public String[] getSiteUrls() {
		return getSetting("URL");
	}
	
	/**
	 * 
	 */ 
	public String[] getSiteNames() {
		return getSetting("Name");
	}

	/**
	 * 
	 */ 
	public int getTimeout() {
		int val = 120;
		String ret = getValByTagName( "Timeout" );
		if ( !ret.equals("") ) {
			val = Integer.parseInt(ret);
		}
		return val;
	}

	/**
	 * 
	 */ 
	public boolean isTraceEnable() {
		boolean val = false;
		String ret = getValByTagName( "TraceLog" );
		if ( ret.equals("true") ) {
			val = true;
		}
		return val;
	}

	/**
	 * 
	 */ 
	public boolean isCookie() {
		boolean val = false;
		String ret = getValByTagName( "Cookie" );
		if ( ret.equals("true") ) {
			val = true;
		}
		return val;
	}

	/**
	 *
	 */
	public int getPollInterval() {
		int val = 30;
		String ret = getValByTagName( "PollingInterval" );
		if ( !ret.equals("") ) {
			val = Integer.parseInt(ret);
		}
		return val;
	}


	/*
	 *
	 */
	private void setRootElement(String baseUrl) {
		String url = baseUrl + "massbank.conf";
		try {
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = dbfactory.newDocumentBuilder();
			Document doc = builder.parse(url);
			this.root = doc.getDocumentElement();
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	private String[] getSetting( String tagName ) {
		String[] infoList = null;
		String info1 = this.getServerSetting(tagName);
		String[] info2 = this.getRelatedSetting(tagName);
		if ( info2 == null ) {
			infoList = new String[1];
		}
		else {
			int len = info2.length;
			infoList = new String[len+1];
			for ( int i = 0; i < len; i++ ) {
				infoList[i+1] = info2[i];
			}
		}
		infoList[0] = info1;
		return infoList;
	}
	
	/**
	 *
	 */
	private String getServerSetting(String tagName) {
		String val = "";
		try {
			NodeList nodeList = this.root.getElementsByTagName( "MyServer" );
			if ( nodeList == null ) {
				return val;
			}
			Element child = (Element)nodeList.item(0);
			NodeList childNodeList = child.getElementsByTagName( tagName );
			Element child2 = (Element)childNodeList.item(0);
			if ( child2 != null ) {
				if ( tagName.equals("FrontServer") || tagName.equals("MiddleServer") ) {
					val = child2.getAttribute("URL");
				}
				else {
					Node node = child2.getFirstChild();
					if ( node != null ) {
						val = node.getNodeValue();
					}
				}
				if ( val == null ) {
					val = "";
				}
			}
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}
		return val;
	}

	/**
	 *
	 */
	private String[] getRelatedSetting(String tagName) {
		String[] vals = null;
		try {
			NodeList nodeList = this.root.getElementsByTagName( "Related" );
			if ( nodeList == null ) {
				return null;
			}
			int len = nodeList.getLength();
			vals = new String[len];
			for ( int i = 0; i < len; i++ ) {
				Element child = (Element)nodeList.item(i);
				NodeList childNodeList = child.getElementsByTagName( tagName );
				Element child2 = (Element)childNodeList.item(0);
				vals[i] = "";
				if ( child2 == null ) {
					continue;
				}
				Node node = child2.getFirstChild();
				if ( node == null ) {
					continue;
				}
				String val = node.getNodeValue();
				if ( val != null ) {
					vals[i] = val;
				}
			}
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}
		return vals;
	}

	/**
	 *
	 */ 
	private String getValByTagName( String tagName ) {
		String val = "";
		try {
			NodeList nodeList = this.root.getElementsByTagName( tagName );
			Element child = (Element)nodeList.item(0);
			val = child.getFirstChild().getNodeValue();
		}
		catch ( Exception e ) {
			System.out.println("\"" + tagName + "\" tag doesn't exist in massbank.conf.");
		}
		return val;
	}

	/**
	 *
	 */ 
	private String trimBaseUrl( String baseUrl ) {
		if ( URLSubDirectory.status == URLSubDirectory.DONT_NEED ) {
			baseUrl = baseUrl.replace( URLSubDirectory.SUBDIR_NAME + "/", "" );
		}
		return baseUrl;
	}
}

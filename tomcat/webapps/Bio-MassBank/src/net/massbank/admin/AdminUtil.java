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
 * AdminUtil.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.admin;

import java.net.InetAddress;

public class AdminUtil {

	/**
	 *
	 */
	public static boolean isInternalUrl(String url) {
		String hostName = "";
		String ipAddress = "";
		try {
			hostName = InetAddress.getLocalHost().getHostName().toLowerCase();
			ipAddress = InetAddress.getLocalHost().getHostAddress();
		}
		catch ( Exception e) {
			e.printStackTrace();
		}
		String url2 = url.toLowerCase();
		if ( url2.indexOf("localhost") != -1 || url2.indexOf("127.0.0.1") != -1
		  || url2.indexOf(hostName) != -1 || url2.indexOf(ipAddress) != -1 ) {
			return true;
		}
		else {
			return false;
		}
	}
}

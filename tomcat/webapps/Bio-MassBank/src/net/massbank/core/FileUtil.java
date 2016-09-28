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
 * FileUtil.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.io.FilenameUtils;

public class FileUtil {

	/*
	 * uncompress zip file
	 */
	public static boolean unzip(String filePath) {
		String dirPath = FilenameUtils.getFullPath(filePath);
		try {
			ZipInputStream zis = new ZipInputStream(new FileInputStream(filePath));
			ZipEntry entry = null;
			while ( (entry = zis.getNextEntry()) != null ) {
				String path = dirPath + entry.getName();
				if ( entry.isDirectory() ) {
					new File(path).mkdirs();
				}
				else {
					File parent = new File(path).getParentFile();
					if ( parent != null ) {
						parent.mkdirs();
					}
					FileOutputStream out = new FileOutputStream(path);
					byte[] buf = new byte[1024];
					int size = 0;
					while ( (size = zis.read(buf)) != -1 ) {
						out.write(buf, 0, size);
					}
					out.close();
				}
				zis.closeEntry();
			}
			zis.close();
			return true;
		}
		catch ( Exception e ) {
			e.printStackTrace();
			return false;
		}
	}

	/*
	 * Get the path to the application root
	 */
	public static String getAppRootPath() {
		String home = System.getProperty("catalina.home");
		String path = home + File.separator + "webapps" + File.separator + DispatcherServlet.appDirName + File.separator;
		return path;
	}

	/**
	 * 
	 */
	public static String getStructureImagePath(String dbName, String size) {
		String rootPath = getAppRootPath();
		String imagePath = rootPath + "structure_img" + File.separator
						 + dbName + File.separator + size + File.separator;
		return imagePath;
	}
}

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
 * FileUpload.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * FileUpload.java
 */
public class FileUpload extends ServletFileUpload {
	private List<FileItem> itemList = null;
	private HttpServletRequest request = null;
	private boolean isMultipart = false;

	/**
	 * constructor
	 */
	public FileUpload(HttpServletRequest request) throws Exception {
		super();
		String tempPath = System.getProperty("java.io.tmpdir");
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(1024);
		factory.setRepository(new File(tempPath));
		setFileItemFactory(factory);
		setSizeMax(-1);
		setHeaderEncoding("utf-8");
		if ( isMultipartContent(request) ) {
			this.itemList = parseRequest(request);
			this.isMultipart = true;
		}
		this.request = request;
	}

	/**
	 * 
	 */
	public boolean isMultipart() {
		return this.isMultipart;
	}

	/**
	 * get request parameters
	 */
	public Map<String, String[]> getRequestParameters() {
		Map<String, String[]> map = new HashMap();
		if ( isMultipart ) {
			for ( FileItem fItem : this.itemList ) {
				if ( fItem.isFormField() ) {
					String key = fItem.getFieldName();
					String val = fItem.getString();
					if ( key != null && !key.equals("") ) {
						String[] vals = null;
						if ( map.containsKey(key) ) {
							String[] vals2 = map.get(key);
							int num = vals2.length;
							vals = new String[num + 1];
							System.arraycopy(vals2, 0, vals, 0, num);
							vals[num] = val;
						}
						else {
							vals = new String[]{val};
						}
						map.put(key, vals);
					}
				}
			}
		}
		else {
			Enumeration<String> names = (Enumeration<String>)request.getParameterNames();
			while ( names.hasMoreElements() ) {
				String key = (String)names.nextElement();
				String[] vals = request.getParameterValues(key);
				map.put(key, vals);
			}
		}
		return map;
	}

	/**
	 * save upload files
	 */
	public String[] saveFiles(String outPath) throws Exception {
		return saveFiles(outPath, null, null);
	}

	/**
	 * save upload files
	 */
	public String[] saveFiles(String prefix, String suffix) throws Exception {
		return saveFiles(null, prefix, suffix);
	}

	/**
	 * save upload files
	 */
	public String[] saveFiles(String outPath, String prefix, String suffix) throws Exception {
		List<String> fileNameList = new ArrayList();
		for ( FileItem fItem : this.itemList ) {
			if ( !fItem.isFormField() ) {
				File saveFile = null;
				String fileName = "";
				if ( outPath == null ) {
					saveFile = File.createTempFile( "massbank", ".txt" );
					fileName= saveFile.getName();
				}
				else {
					File originFile = new File(fItem.getName());
					fileName = originFile.getName();
					saveFile = new File( outPath + File.separator + fileName );
				}
				fItem.write(saveFile);
				fItem.delete();
				fileNameList.add(fileName);
			}
		}
		
		if ( fileNameList.size() == 0 ) {
			return null;
		}
		else {
			return fileNameList.toArray(new String[]{});
		}
	}

}

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
 * RecordValidator.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.admin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.math.NumberUtils;

public class RecordValidator {
	private final String RECFILE_EXTENSION = "txt";

	private String recDataPath = "";
	private List<String> entryIdList = new ArrayList();
	private AdminInfo adminInfo = new AdminInfo();

	/**
	 * constructor
	 */
	public RecordValidator(String recDataPath) {
		this.recDataPath = recDataPath;
	}

	/*
	 * execute
	 */
	public boolean execute() {
		File file = new File(this.recDataPath);
		String msg = "";
		if ( !file.exists() ) {
			msg = "recdata invalid(1)";
			adminInfo.add(AdminInfo.INFO_LEVEL_ERROR, msg, recDataPath);
			return false;
		}
		if ( !file.isDirectory() ) {
			msg = "recdata invalid(2)";
			adminInfo.add(AdminInfo.INFO_LEVEL_ERROR, msg, recDataPath);
			return false;
		}
		String[] fileNameList = file.list();
		if ( fileNameList.length == 0 ) {
			msg = "Record file not found";
			adminInfo.add(AdminInfo.INFO_LEVEL_ERROR, msg, recDataPath);
			return false;
		}
		boolean ret = true;
		for ( int i = 0; i < fileNameList.length; i++ ) {
			String fileName = fileNameList[i];
			try {
				String[] data = getRecordData(fileName);
				if ( data != null ) {
					if ( checkFields(fileName, data) ) {
						String id = FilenameUtils.removeExtension(fileName);
						if ( !this.entryIdList.contains(id) ) {
							this.entryIdList.add(id);
						}
					}
					else {
						ret = false;
					}
				}
			}
			catch ( Exception e ) {
				e.printStackTrace();
				msg = "Internal error (failed to read file)";
				adminInfo.add(AdminInfo.INFO_LEVEL_ERROR, msg, fileName);
				ret = false;
				break;
			}
		}
		return ret;
	}

	/*
	 * get entry IDs
	 */
	public String[] getEntryIds() {
		return this.entryIdList.toArray(new String[]{});
	}

	/*
	 * get informations
	 */
	public String[] getInformations() {
		return this.adminInfo.getList();
	}

	/*
	 * get record data
	 */
	private String[] getRecordData(String fileName) throws Exception {
		File file = new File(this.recDataPath + fileName);
		if (   file.isDirectory() || file.isHidden()
			|| !FilenameUtils.isExtension(fileName, RECFILE_EXTENSION) ) {
			return null;
		}

		boolean isDoubleByte = false;
		List<String> data = new ArrayList();
		String line = "";
		BufferedReader br = null;
		br = new BufferedReader(new FileReader(file));
		while ( (line = br.readLine()) != null ) {
			data.add(line);
			if ( !isDoubleByte ) {
				byte[] bytes = line.getBytes("MS932");
				if ( bytes.length != line.length() ) {
					isDoubleByte = true;
				}
			}
		}
		br.close();
		if ( isDoubleByte ) {
			String msg = "This record contains double-byte characters";
			adminInfo.add(AdminInfo.INFO_LEVEL_WARN, msg, fileName);
		}
		return data.toArray(new String[]{});
	}

	/*
	 * check fields
	 */
	private boolean checkFields(String fileName, String[] data) {
		final String[] tagNames = new String[] {
			"ACCESSION: ", "RECORD_TITLE: ", "DATE: ", "AUTHORS: ", "LICENSE: ",
			"AC$INSTRUMENT: ", "AC$INSTRUMENT_TYPE: ","AC$MASS_SPECTROMETRY: MS_TYPE ",
			"AC$MASS_SPECTROMETRY: ION_MODE ","PK$NUM_PEAK: ", "PK$PEAK: "
		};

		String msg = "";
		boolean ret = false;
		for ( int i = 0; i < tagNames.length; i++ ) {
			ret = false;
			String tagName = tagNames[i];
			String val = "";
			List<String> peakList = new ArrayList<String>();
			boolean existsField = false;
			boolean existsValue = false;
			boolean isPeakField = false;
			for ( int j = 0; j < data.length; j++ ) {
				String line = data[j].trim();
				if ( line.startsWith("//") || line.startsWith("RELATED_RECORD:") ) {
					break;
				}
				else if ( isPeakField ) {
					if ( !line.equals("") ) {
						peakList.add(line);
					}
				}
				else if ( line.indexOf(tagName) != -1 ) {
					existsField = true;
					val = line.replace(tagName, "");
					if ( !val.trim().equals("") ) {
						existsValue = true;
					}
					if ( tagName.equals("PK$PEAK: ") ) {
						isPeakField = true;
					}
					else {
						break;
					}
				}
			}

			if ( !existsField ) {
				msg = "\"" + tagName +"\"" + " field not found";
				adminInfo.add(AdminInfo.INFO_LEVEL_ERROR, msg, fileName);
				break;
			}
			if ( !existsValue ) {
				msg = "Invalid \"" + tagName + "\" value";
				adminInfo.add(AdminInfo.INFO_LEVEL_ERROR, msg, fileName);
				break;
			}

			if ( tagName.equals("ACCESSION: ") ) {
				if ( !val.equals(FilenameUtils.removeExtension(fileName)) ) {
					msg = "Wrong \"ACCESSION\" value";
					adminInfo.add(AdminInfo.INFO_LEVEL_ERROR, msg, fileName);
					break;
				}
			}
			else if ( isPeakField ) {
				List<String> mzList = new ArrayList();
				String[] peakLines = peakList.toArray(new String[]{});
				for ( int j = 0; j < peakLines.length; j++ ) {
					String[] vals = peakLines[j].split(" ");
					String mz = vals[0];
					String intensity = vals[1];
					if (   mzList.contains(mz)
						|| !NumberUtils.isNumber(mz)
						|| !NumberUtils.isNumber(intensity) ) {
						msg = "Invalid \"PK$PEAK\" value";
						adminInfo.add(AdminInfo.INFO_LEVEL_ERROR, msg, fileName);
						break;
					}
					mzList.add(mz);
				}
			}
			ret = true;
		}
		return ret;
	}
}

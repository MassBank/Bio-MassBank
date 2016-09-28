/*******************************************************************************
 *
 * Copyright (C) 2013 MassBank Project
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
 * StatisticsResult.java
 *
 * ver 1.0.0 2013.02.15
 *
 ******************************************************************************/
package net.massbank.tools.statistics;

import java.text.NumberFormat;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class StatisticsResult {
	private Map<String, Integer> contributorList = new TreeMap();
	private Map<String, Integer> instTypeList = new TreeMap();
	private Map<String, Integer> msTypeList = new TreeMap();
	private Map<String, Integer> sampleNameList = new TreeMap();
	private int cntPos = 0;
	private int cntNeg = 0;
	private int cntSpectra = 0;
	private NumberFormat nf = null;

	/**
	 * constructor
	 */
	public StatisticsResult() {
		this.nf = NumberFormat.getInstance();
	}


	/**
	 *
	 */
	public String getNumOfSpectra() {
		return nf.format(this.cntSpectra);
	}

	/**
	 *
	 */
	public String[] getContributors() {
		Set keySet = this.contributorList.keySet();
		return (String[])keySet.toArray(new String[]{});
	}


	/**
	 *
	 */
	public String[] getInstumentTypes() {
		Set keySet = this.instTypeList.keySet();
		return (String[])keySet.toArray(new String[]{});
	}

	/**
	 *
	 */
	public String[] getMsTypes() {
		Set keySet = this.msTypeList.keySet();
		return (String[])keySet.toArray(new String[]{});
	}


	/**
	 *
	 */
	public String[] getSampleNames() {
		Set keySet = this.sampleNameList.keySet();
		return (String[])keySet.toArray(new String[]{});
	}


	/**
	 *
	 */
	public String getNumOfContributor(String name) {
		return nf.format(this.contributorList.get(name));
	}

	/**
	 *
	 */
	public String getNumOfInstumentType(String name) {
		return nf.format(this.instTypeList.get(name));
	}

	/**
	 *
	 */
	public String getNumOfMsType(String name) {
		return nf.format(this.msTypeList.get(name));
	}

	/**
	 *
	 */
	public String getNumOfIonPos() {
		return nf.format(this.cntPos);
	}

	/**
	 *
	 */
	public String getNumOfIonNeg() {
		return nf.format(this.cntNeg);
	}

	/**
	 *
	 */
	public String getNumOfSampleName(String name) {
		return nf.format(this.sampleNameList.get(name));
	}


	/**
	 *
	 */
	public void setContributorList(Map<String, Integer> list) {
		this.contributorList = list;
	}

	/**
	 *
	 */
	public void setInstumentTypeList(Map<String, Integer> list) {
		this.instTypeList = list;
	}

	/**
	 *
	 */
	public void setMsTypeList(Map<String, Integer> list) {
		this.msTypeList = list;
	}

	/**
	 *
	 */
	public void setCountSpectra(int cnt) {
		this.cntSpectra = cnt;
	}

	/**
	 *
	 */
	public void setIon(int cntPos, int cntNeg) {
		this.cntPos = cntPos;
		this.cntNeg = cntNeg;
	}

	/**
	 *
	 */
	public void setSampleNameList(Map<String, Integer> list) {
		this.sampleNameList = list;
	}

}

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
 * Peak.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.applet.common;

import java.util.Vector;
import java.math.BigDecimal;

public class Peak {

	private double[] mzs;
	private int[] intensities;
	private boolean[] selectPeakFlag;


	/**
	 *
	 */
	public Peak(String val) {
		String[] lines = val.split(";");
		int num = lines.length;
		this.mzs = new double[num];
		this.intensities = new int[num];
		for ( int i = 0; i < num; i++ ) {
			String[] items = lines[i].split(",");
			this.mzs[i] = Double.parseDouble(items[0]);
			this.intensities[i] = Integer.parseInt(items[1]);
		}
		initSelectPeakFlag();
	}

	/**
	 *
	 */
	public double compMaxMzPrecusor(int precursor) {
		double mzMax = 0;
		if ( this.mzs.length == 0 ) {
			mzMax = 0d;
		}
		else {
			mzMax = this.mzs[mzs.length-1];
		}
		return Math.max(mzMax, (double)precursor);
	}

	/**
	 *
	 */
	public int getMaxIntensity(double start, double end) {
		int max = 0;
		for( int i = 0; i < intensities.length; i++ ){
			if ( this.mzs[i] > end ) {
				break;
			}
			if ( start <= this.mzs[i] ){
				if ( max < this.intensities[i] ) {
					max = this.intensities[i];
				}
			}
		}
		return max;
	}

	/**
	 *
	 */
	public int getCount() {
		return this.mzs.length;
	}

	/**
	 *
	 */
	public double getMz(int index) {
		if( index < 0 || index >= this.mzs.length) {
			return -1.0d;
		}
		return this.mzs[index];
	}

	/**
	 *
	 */
	public int getIntensity(int index) {
		if ( index < 0 || index >= this.mzs.length ) {
			return -1;
		}
		return this.intensities[index];
	}

	/**
	 *
	 */
	public int getIndex(double target) {
		int i = 0;
		for ( i = 0; i < mzs.length; i++ ){
			if ( mzs[i] >= target ) {
				break;
			}
		}
		return i;
	}

	/**
	 *
	 */
	public String getDiff(int index, double baseMass) {
		String diff = new BigDecimal(mzs[index] - baseMass).setScale(1, BigDecimal.ROUND_HALF_UP).toString();
		return diff;
	}

	/**
	 *
	 */
	public boolean isSelectPeakFlag(int index) {
		return selectPeakFlag[index];
	}

	/**
	 *
	 */
	public void setSelectPeakFlag(int index, boolean flag) {
		this.selectPeakFlag[index] = flag;
	}

	/**
	 *
	 */
	public void initSelectPeakFlag() {
		this.selectPeakFlag = new boolean[this.mzs.length];
	}

	/**
	 *
	 */
	public int getSelectPeakNum() {
		int num = 0;
		for ( boolean flag: this.selectPeakFlag ) {
			if ( flag ) num++;
		}
		return num;
	}
}

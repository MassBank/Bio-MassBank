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
 * ChemicalFormulaUtils.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.core.common;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.math.BigDecimal;

public class ChemicalFormulaUtils {
	public static double calcExactMass(String formula) {
		final HashMap<String, Double> atomicMass =
			new HashMap<String, Double>() {{
				put("H", 1.007825032);
				put("Be", 9.0121821);
				put("B" , 11.0093055);
				put("C" , 12.00000000);
				put("N" , 14.003074005);
				put("O" , 15.994914622);
				put("F" , 18.99840320);
				put("Na", 22.98976967);
				put("Al", 26.98153844);
				put("Si", 27.976926533);
				put("P" , 30.97376151);
				put("S" , 31.97207069);
				put("Cl", 34.96885271);
				put("K" , 38.963706);
				put("V" , 50.9439637);
				put("Cr", 51.9405119);
				put("Fe", 55.9349421);
				put("Ni", 57.9353479);
				put("Co", 58.9332001);
				put("Cu", 62.9296011);
				put("Zn", 63.9291466);
				put("Ge", 73.9211782);
				put("Br", 78.9183376);
				put("Mo", 97.9054078);
				put("Pd", 105.903483);
				put("Sn", 119.9021966);
				put("I" , 126.904468);
				put("Pt", 194.964774);
				put("Hg", 201.970626);
		}};
		Map<String, Integer> atomList = getAtomList(formula);
		double mass = 0;
		Set keys = atomList.keySet();
		for ( Iterator iterator = keys.iterator(); iterator.hasNext(); ) {
			String atom = (String)iterator.next();
			int num = atomList.get(atom);
			mass += atomicMass.get(atom) * num;
		}
		double emass = new BigDecimal(mass).setScale(5, BigDecimal.ROUND_HALF_UP).doubleValue();
		return emass;
	}




	/**
	 * 
	 */
	public static Map<String, Integer> getAtomList(String formula) {
		Map<String, Integer> atomList = new HashMap();
		int startPos = 0;
		int endPos = formula.length();
		int i = 0;
		for ( int pos = 1; pos <= endPos; pos++ ) {
			String chr = "";
			if ( pos < endPos ) {
				chr = formula.substring( pos, pos + 1 );
			}
			if ( pos == endPos || (!isNumber(chr) && chr.equals(chr.toUpperCase())) ) {
				String item = formula.substring( startPos, pos );

				boolean isFound = false;
				for ( i = 1; i < item.length(); i++ ) {
					chr = item.substring(i, i + 1);
					if ( isNumber(chr) ) {
						isFound = true;
						break;
					}
				}
				String atom = item.substring(0, i);
				int num = 1;
				if ( isFound ) {
					num = Integer.parseInt(item.substring(i));
				}
				if ( atomList.get(atom) != null ) {
					num = num + atomList.get(atom);
				}
				atomList.put(atom, num);

				startPos = pos;
			}
		}
		return atomList;
	}

	/**
	 *
	 */
	public static String swapFormula(String formula) {
		String[] atomSequece = new String[]{
			"C", "H", "Cl", "F", "I", "N", "O", "P", "S", "Si"
		};
		Map<String, Integer> atomList = getAtomList(formula);
		String swapFormula = "";
		Set keys = atomList.keySet();
		for ( int i = 0; i < atomSequece.length; i++ ) {
			for ( Iterator iterator = keys.iterator(); iterator.hasNext(); ) {

				String atom = (String)iterator.next();
				int num = atomList.get(atom);

				if ( atom.equals(atomSequece[i]) )  {
					swapFormula += atom;
					if ( num > 1 ) {
						swapFormula += String.valueOf(num);
					}
					break;
				}
			}
		}
		return swapFormula;
	}

	/**
	 *
	 */
	public static List<String[]> getIonMassList(int ionMode) throws IOException {
		List<String[]> massList = new ArrayList();
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String conUrl = "jdbc:mysql://localhost/FORMULA_SEARCH";
			Connection con = DriverManager.getConnection(conUrl, "bird", "bird2006");
			Statement stmt = con.createStatement();
			String sql = "select FORMULA, EXACT_MASS from ION_FORMULA_LIST where ION_MODE=" + ionMode + " order by EXACT_MASS";
			ResultSet rs = stmt.executeQuery(sql);
			while ( rs.next() ) {
				String formula = rs.getString("FORMULA");
				String mass = rs.getString("EXACT_MASS");
				massList.add(new String[]{formula,mass});
			}
			rs.close();
			stmt.close();
			con.close();
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}
		return massList;
	}

	/**
	 *
	 */
	public static String[] getMzArray(String peakString, int cutoff) {
		List<String> mzList = new ArrayList();
		String[] peaks = peakString.split(";");
		for ( String peak: peaks ) {
			String val = peak.trim();
			String[] pair = val.split(",");
			String mz = pair[0];
			int inte = Integer.parseInt(pair[1]);
			if ( inte >= cutoff ) {
				mzList.add(mz);
			}
		}
		Collections.sort(mzList, new Comparator(){
			public int compare(Object obj1, Object obj2){
				String s1 = (String) obj1;
				String s2 = (String) obj2;
				return Double.valueOf(s1).compareTo(Double.valueOf(s2));
			}
		});
		return mzList.toArray(new String[]{});
	}

	/**
	 *
	 */
	public static Map<Double, String> getMatchedFormulas(
		String[] mzs, int tolerancePpm, List<String[]>massList, int ionMode) throws IOException {
		double e = 0.00054858;
		Map<Double, String> formulaList = new TreeMap();
		for ( String mz: mzs ) {
			double dblMz = Double.parseDouble(mz);
			if ( ionMode > 0 ) {
				dblMz += e;
			}
			else {
				dblMz -= e;
			}
			double delta = new BigDecimal(dblMz * ((double)tolerancePpm / 1000000)).setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
			double min = dblMz - delta;
			double max = dblMz + delta;
			for ( String[] items: massList ) {
				String formula = items[0];
				String mass = items[1];
				double dblMass = Double.parseDouble(mass);
				double d1 = dblMz - dblMass;
				if ( d1 < 0 ) {
					d1 = -d1;
				}
				if ( min <= dblMass && dblMass <= max ) {
					if ( formulaList.containsKey(dblMz) ) {
						String prevFormula = formulaList.get(dblMz);
						double d2 = dblMz - calcExactMass(prevFormula);
						if ( d2 < 0 ) {
							d2 = -d2;
						}
						if ( d1 < d2 ) {
							formulaList.put(dblMz, formula);
						}
					}
					else {
						formulaList.put(dblMz, formula);
					}
				}
				else if ( max < dblMass ) {
					break;
				}
			}
		}
		return formulaList;
	}

	/*
	 *
	 */
	public static String getNLoss(String formula1, String formula2) {
		Map<String, Integer> atomList1 = getAtomList(formula1);
		Map<String, Integer> atomList2 = getAtomList(formula2);
		String nloss = "";
		int foundCnt = 0;
		for ( Map.Entry<String, Integer> e1 : atomList1.entrySet()) {
			String atom1 = e1.getKey();
			int num1 = e1.getValue();
			boolean isFound = false;
			for ( Map.Entry<String, Integer> e2 : atomList2.entrySet()) {
				String atom2 = e2.getKey();
				int num2 = e2.getValue();
				if ( atom1.equals(atom2) ) {
					if ( num1 >= num2 ) {
						if ( num1 - num2 == 1 ) {
							nloss += atom1;
						}
						else if ( num1 - num2 > 1 ) {
							nloss += atom1 + String.valueOf(num1 - num2);
						}
						isFound = true;
						break;
					}
					else {
						return "";
					}
				}
			}
			if ( isFound ) {
				foundCnt++;
			}
			else {
				nloss += atom1;
				if ( num1 > 1 ) {
					nloss += String.valueOf(num1);
				}
			}
		}
		if ( foundCnt < atomList2.size() ) {
			return "";
		}
		return swapFormula(nloss);
	}

	/*
	 *
	 */
	public static boolean isNumber(String chr) {
		try {
			int n = Integer.parseInt(chr);
			return true;
		}
		catch ( NumberFormatException e ) {
			return false;
		}
	}
}

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
 * PeakSearch.js
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
var op = (window.opera) ? 1 : 0;								//OP
var ie = (!op && document.all) ? 1 : 0;							//IE
var ns4 = (document.layers) ? 1 : 0;							//NS4
var ns6 = (document.getElementById&&!document.all) ? 1 : 0;		//NS6

/**
 *
 */
$(function() {
	$("input.Formula").massCalc();
});

/**
 *
 */
function initFocus() {
	document.forms[0].mz0.focus();
	return;
}

/**
 *
 */
function changeSearchType(reqType) {
	var color1 = "OliveDrab";
	var color2 = "DarkOrchid";
	if( reqType == document.forms[0].searchType[0].value || reqType == "" ) {
		val = "<i>m/z</i>";
		color2 = "White";
	}
	else {
		val = "<i>m/z</i>&nbsp;Dif.";
		color1 = "White";
	}
	var ele = document.getElementById( "mz" );
	ele.innerHTML = val;
	document.getElementById( "underbar1" ).bgColor = color1;
	document.getElementById( "underbar2" ).bgColor = color2;

	var arrowObj = null;
	for ( cnt = 0; cnt < 6; cnt++ ) {
		arrowObj = document.getElementById("arrow" + cnt);
		if ( reqType == "peak") {
			arrowObj.innerHTML = "<img src=\"../image/arrow_peak.gif\" alt=\"\">";
		}
		else if ( reqType == "peak_diff" ) {
			arrowObj.innerHTML = "<img src=\"../image/arrow_diff.gif\" alt=\"\">";
		}
	}

	initFocus();
}

/**
 *
 */
function changeSearchCondition(logic) {
	for ( i = 1; i <= 5; i++ ) {
		ele = document.getElementById( "logic" + String(i) );
		ele.innerHTML = logic;
	}
}


/**
 *
 */
$.fn.massCalc = function() {
	
	$(this).each(function() {
		var prevFormula = "";
		var targetIndex = 0;
		$(this).keydown(function(e) {
			prevFormula = $(this).val();
			targetIndex = $("input.Formula").index(this);
		});
		
		$(this).keyup(function(e) {
			var inputFormula = $(this).val();
			if ( prevFormula == inputFormula ) {
				return;
			}
			var mass = "";
			if ( inputFormula != "" ) {
				var atomicArray = new Array();
				inputFormula = inputFormula.replace(/^[\s　]+|[\s　]+$/g, "");
				inputFormula = inputFormula.replace(/[Ａ-Ｚａ-ｚ０-９]/g, toHalfChar);
				atomicArray = getAtomicArray(inputFormula);
				mass = massCalc(atomicArray);
			}
			$("input.Mass:eq(" + targetIndex + ")").val(mass);
		});
	});
}

/**
 *
 */
function setMZ(index, fom) {
	var mass = "";
	
	if (fom != "") {
		var atomicArray = new Array();
		var fom = fom.replace(/^[\s　]+|[\s　]+$/g, "");
		var newFom = fom.replace(/[Ａ-Ｚａ-ｚ０-９]/g, toHalfChar);
		atomicArray = getAtomicArray(newFom);
		mass = massCalc(atomicArray);
	}
	mzObj = eval("document.forms[0].mz" + index);
	mzObj.value = mass;
}

/**
 *
 */
function getAtomicArray(formula) {
	
	var atomicArray = new Array();
	var nextChar = "";
	var subStrIndex = 0;
	var endChrFlag = 0;
	
	for (i=0; i<formula.length; i++) {
		
		if ((i+1) < formula.length) {
			nextChar = formula.charAt(i+1);
		} else {
			endChrFlag = 1;
		}
		
		if (endChrFlag == 1 || nextChar.match(/[A-Z]/)) {
			atomicArray.push(formula.substring(subStrIndex,i+1));
			subStrIndex = i+1;
		}
	}
	
	return atomicArray;
}

/**
 *
 */
function massCalc(atomicArray) {
	var mass = "";
	var massArray = new Array();
	for (i=0; i<atomicArray.length; i++) {
		var atom = "";
		var atomNum = 0;
		var subStrIndex = 0;
		var atomNumFlag = 0;
		
		for (j=0; j<atomicArray[i].length; j++) {
			if (atomicArray[i].search(/[0-9]/) != -1) {
				subStrIndex = atomicArray[i].search(/[0-9]/);
				atomNumFlag = 1;
				break;
			}
		}
		
		if (!atomNumFlag) {
			atomNum = 1;
			atomicArray[i] = atomicArray[i].replace(/ /g, "");
			atomicArray[i] = atomicArray[i].replace(/　/g, "");
			subStrIndex = atomicArray[i].length;
		}
		
		atom = atomicArray[i].substring(0,subStrIndex);
		
		if (atomNumFlag) {
			atomNum = atomicArray[i].substr(subStrIndex);
		}
		
		if (atomicMass[atom]) {
			massArray[i] = atomicMass[atom] * atomNum;
		} else {
			mass = "-";
			return mass;
		}
	}
	
	for (i=0; i<massArray.length; i++) {
		mass = eval(mass + massArray[i]);
	}
	if (mass.toString() == "NaN") {
		mass = "-";
		return mass;
	}
	
	mass += "";
	if (mass.indexOf(".") == -1) {
		mass += ".00000";
	}
	else {
		var tmpMass = mass.split(".");
		if (tmpMass[1].length > 5) {
			mass = tmpMass[0] + "." + tmpMass[1].substring(0, 5);
		}
		else {
			var zeroCnt = 5 - tmpMass[1].length;
			for (var i=0; i<zeroCnt; i++) {
				mass += "0";
			}
		}
	}
	
	return mass;
}

/**
 *
 */
function toHalfChar(fullChar) {
	var halfChr = "";
	var halfCharList = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
					   "abcdefghijklmnopqlstuvwxyz" +
					    "0123456789";
	var fullCharList = "ＡＢＣＤＥＦＧＨＩＪＫＬＭＮＯＰＱＲＳＴＵＶＷＸＹＺ" +
					   "ａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚ" +
					   "０１２３４５６７８９";
	var index = fullCharList.indexOf(fullChar);
	halfChr = halfCharList.charAt(index);
	
	return halfChr;
}

/**
 *
 */
function resetForm() {
	var f1 = document.forms[0];
	for ( i = 0; i < 6; i++ ) {
		if ( i > 0 ) {
			f1["op" + i].value = "and";
		}
		f1["mz" + i].value = "";
		f1["fom" + i].value = "";
	}
	f1.int.value = "100";
	f1.tol.value = "0.3";

	initFocus();
}

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
 * MassCalc.js
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/

/**
 *
 */
$(function() {
	$.fn.initFocus();
	$("*").exitMassCalc();
	$("input.fFormula").fmCalc();
	$("input.fMass").resultFocus();
	$("input.mMass").mfCalc();
	$("textarea.mFormula").resultFocus();
});

/**
 *
 */
$.fn.initFocus = function() {
	$("input[name='type']:radio:eq(0)").focus();
}

/**
 *
 */
$.fn.exitMassCalc = function() {
	$(this).each(function() {
		$(this).keyup(function(e) {
			if ( e.keyCode == 27 ) {
				window.opener = window;
				window.close();
			}
		});
	});
}

/**
 *
 */
$.fn.resultFocus = function() {
	$(this).click(function() {
		if ( $(this).get(0).tagName.toLowerCase() == "input" ) {
			$(this).select();
		}
		else if ( $(this).get(0).tagName.toLowerCase() == "textarea" ) {
		}
	});
}

/**
 *
 */
$.fn.mfCalc = function() {
	
	var jsonFiles = ["ion_mass.json", "nloss_mass.json"];
	var formulaList = [];
	var isInit = true;
	
	for (var i=0; i<jsonFiles.length; i++) {
		$.getJSON(
			jsonFiles[i],
			function(jsonData){
				if (i == 0) {
					formulaList = $.merge([], jsonData);
				}
				else {
					formulaList = $.merge(formulaList, jsonData);
				}
			}
		);
	}
	
	$(this).focus(function(){
		if ( isInit ) {
			for (var i=0; i<formulaList.length; i++) {
				var formula = formulaList[i][0];
				var mass = formulaList[i][1];
				if (mass.indexOf(".") == -1) {
					mass += ".00000";
				}
				else {
					var tmpMass = mass.split(".");
					if (tmpMass[1].length <= 5) {
						var zeroCnt = 5 - tmpMass[1].length;
						for (var j=0; j<zeroCnt; j++) {
							mass += "0";
						}
					}
				}
				formulaList[i][1] = mass;
			}
			
			var chkStorage = {};
			var tmpList = [];
			for (var i=0; i<formulaList.length; i++) {
				var value = formulaList[i];
				if ( !(value in chkStorage) ) {
					chkStorage[value] = true;
					tmpList.push(value);
				}
			}
			formulaList = $.merge([], tmpList);
			formulaList.sort();
			isInit = false;
		}
	});
	
	$(this).keyup(function(e) {
		var inputMz = $(this).val();
		var matchFormula = new Array();
		if ( inputMz != "" ) {
			inputMz = inputMz.replace(/^[\s　]+|[\s　]+$/g, "");
			
			inputMz = inputMz.replace(/[Ａ-Ｚａ-ｚ０-９．]/g, toHalfChar);
			
			for (var i in formulaList) {
				var formula = formulaList[i][0];
				var mass = formulaList[i][1];
				if (mass.indexOf(inputMz) == 0) {
					matchFormula.push(formula + " (" + mass + ")");
				}
				else if (inputMz.indexOf(mass) == 0) {
					matchFormula.push(formula + " (" + mass + ")");
				}
			}
			matchFormula.sort();
		}
		if ( matchFormula.length > 0 || inputMz == "" ) {
			$("textarea.mFormula").val(matchFormula.join("\n"));
		}
		else {
			$("textarea.mFormula").val("-");
		}
	});
}

/**
 *
 */
$.fn.fmCalc = function() {
	
	$(this).each(function() {
		
		var prevFormula = "";
		var targetIndex = 0;
		
		$(this).keydown(function(e) {
			prevFormula = $(this).val();
			targetIndex = $("input.fFormula").index(this);
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
				inputFormula = inputFormula.replace(/[Ａ-Ｚａ-ｚ０-９．]/g, toHalfChar);
				atomicArray = getAtomicArray(inputFormula);
				mass = massCalc(atomicArray);
			}
			$("input.fMass:eq(" + targetIndex + ")").val(mass);
		});
	});
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
					   "0123456789.";
	var fullCharList = "ＡＢＣＤＥＦＧＨＩＪＫＬＭＮＯＰＱＲＳＴＵＶＷＸＹＺ" +
					   "ａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚ" +
					   "０１２３４５６７８９．";
	var index = fullCharList.indexOf(fullChar);
	halfChr = halfCharList.charAt(index);
	
	return halfChr;
}


/**
 *
 */
function changeType(type) {
	if (type == "fm") {
		$("#fCalc").show();
		$("#mCalc").hide();
		$("input[name='type']:radio").val(["fm"]);
		$("input[name='type']:radio:eq(0)").focus();
		$("span[name='typeLbl']:eq(0)").css("text-decoration", "underline");
		$("span[name='typeLbl']:eq(1)").css("text-decoration", "none");
	}
	else {
		$("#fCalc").hide();
		$("#mCalc").show();
		$("input[name='type']:radio").val(["mf"]);
		$("input[name='type']:radio:eq(1)").focus();
		$("span[name='typeLbl']:eq(0)").css("text-decoration", "none");
		$("span[name='typeLbl']:eq(1)").css("text-decoration", "underline");
	}
}

/**
 *
 */
function resetForm() {
	var f1 = document.forms[0];
	for (var i=0; i<6; i++ ) {
		f1["mz" + i].value = "";
		f1["fom" + i].value = "";
	}
	$("input.fFormula:eq(0)").focus();
}

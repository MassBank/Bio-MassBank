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
 * Common.js
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/

var op  = (window.opera) ? 1 : 0;								// OP
var ie  = (!op && document.all) ? 1 : 0;						// IE
var ns4 = (document.layers) ? 1 : 0;							// NS4
var ns6 = (document.getElementById&&!document.all) ? 1 : 0;		// NS6

/**
 *
 */
function setCookie(isCookieConf, cookieName, keyInstGrp, keyInst, keyMs, keyIon) {
	if (window.navigator.cookieEnabled) {
		var cookieInstGrp = keyInstGrp + "=";
		var cookieInst = keyInst + "=";
		var cookieMs = keyMs + "=";
		var cookieIon = keyIon + "=";

		var instGrpVals = document.getElementsByName("inst_grp");
		for ( var i = 0; i < instGrpVals.length; i++ ) {
			if (instGrpVals[i].checked) {
				cookieInstGrp += instGrpVals[i].value + ",";
			}
		}
		if (cookieInstGrp.substring(cookieInstGrp.length - 1) == ",") {
			cookieInstGrp = cookieInstGrp.substring(0, cookieInstGrp.length - 1);
		}
		
		var instVals = document.getElementsByName("inst");
		for (var i=0; i<instVals.length; i++) {
			if (instVals[i].checked) {
				cookieInst += instVals[i].value + ",";
			}
		}
		if (cookieInst.substring(cookieInst.length - 1) == ",") {
			cookieInst = cookieInst.substring(0, cookieInst.length - 1);
		}
		

		var msVals = document.getElementsByName("ms");
		for ( var i=0; i < msVals.length; i++ ) {
			if (msVals[i].checked) {
				cookieMs += msVals[i].value + ",";
			}
		}
		if (cookieMs.substring(cookieMs.length - 1) == ",") {
			cookieMs = cookieMs.substring(0, cookieMs.length - 1);
		}

		var ionVals = document.getElementsByName("ion");
		for (var i=0; i<ionVals.length; i++) {
			if (ionVals[i].checked) {
				cookieIon += ionVals[i].value;
				break;
			}
		}
		var cookieVal = cookieInstGrp + ";" + cookieInst + ";" + cookieMs + ";" + cookieIon;
		var date = new Date();
		var time = date.getTime();
		if ( isCookieConf ) {
			// 30day
			time += 30 * 24 * 60 * 60 * 1000;
		} else {
			time -= 30;
		}
		date.setTime(time);
		var gmtTime = date.toGMTString();
		document.cookie = cookieName + "=" + escape(cookieVal) + ";expires=" + gmtTime;
	}
}

/**
 * 
 */
function selBoxGrp(key, num) {
	isCheck = document.getElementById("inst_grp_" + key).checked;
	for ( i = 0; i < num; i++ ) {
		id = "inst_" + key + String(i);
		obj = document.getElementById(id);
		obj.checked = isCheck;
	}
}

/**
 * 
 */
function selBoxInst(key, num) {
	allOn = true;
	for ( i = 0; i < num; i++ ) {
		id = "inst_" + key + String(i);
		obj1 = document.getElementById(id);
		if ( !obj1.checked ) {
			allOn = false;
			break;
		}
	}
	obj2 = document.getElementById("inst_grp_" + key);
	if ( allOn ) {
		obj2.checked = true;
	}
	else {
		obj2.checked = false;
	}
}

function selAllMs(num, isAdv) {
	var addStr = "";
	if ( isAdv == 1 ) {
		addStr = "adv_";
	}
	var isCheck = document.getElementById("ms_" + addStr + "MS0").checked;
	for ( i=1; i<=num; i++ ) {
		id = "ms_" + addStr + "MS" + String(i);
		obj = document.getElementById(id);
		obj.checked = isCheck;
	}
}

/**
 *
 */
function selMs(num, isAdv) {
	var addStr = "";
	if ( isAdv == 1 ) {
		addStr = "adv_";
	}
	var isAllCheck = true;
	for ( i=1; i<=num; i++ ) {
		id = "ms_" + addStr + "MS" + String(i);
		obj = document.getElementById(id);
		if ( !obj.checked ) {
			isAllCheck = false;
			break;
		}
	}
	document.getElementById("ms_" + addStr + "MS0").checked = isAllCheck;
}

/**
 *
 */
function checkFileExtention(path) {
	var file;
	var ext;
	var invalidList = new Array(
		"xls", "xlsx", "doc", "docx", "ppt", "pptx", "pdf",
		"bmp", "jpg", "gif", "png", "cab", "lzh", "tar", "zip",
		"exe", "wma", "aac", "mp3");

	if ( path == "" ) {
		alert("No file.");
		return false;
	}
	
	file = path.substring(path.lastIndexOf('/', path.length) + 1);
	file = file.substring(file.lastIndexOf('\\', file.length) + 1);
	ext = file.substring(file.lastIndexOf('.', file.length) + 1);
	
	for (var i=0; i<invalidList.length; i++) {
		if (invalidList[i] == ext.toLowerCase()) {
			alert("The extension is illegal.");
			return false;
		}
	}
	return true;
}

/*
 *
 */
function checkSubmit() {
	var isCheck = false;
	var obj = document.form_query["inst"];
	if ( obj.length > 1 ) {
		for ( i = 0; i < obj.length; i++ ) {
			if ( obj[i].checked ) {
				isCheck = true;
				break;
			}
		}
	}
	else {
		if ( obj.checked ) {
			isCheck = true;
		}
	}
	if ( !isCheck ) {
		alert( "Please select one or more checkboxs of the \"Instrument Type\"." );
		return false;
	}
	return true;
}

/**
 *
 */
function doWait() {
	var msg = (arguments[0] != null) ? arguments[0] : "please wait...";
	
	var objBody = document.body;
	var objDiv = document.createElement("div");
	objDiv.setAttribute("id", "wait");
	
	if ( document.addEventListener ) {
		document.addEventListener( 'keydown', function(e){e.preventDefault();}, false);
	} else if (document.attachEvent) {
		document.attachEvent("onkeydown", function(){window.event.keyCode=0;return false;});
	}
	
	var sLeft = document.documentElement.scrollLeft || document.body.scrollLeft;
	var sTop = document.documentElement.scrollTop || document.body.scrollTop;
	objBody.style.overflowY = "hidden";
	objBody.style.overflowX = "hidden";
	
	var w = document.body.clientWidth + "px";
	var h = document.body.clientHeight + "px";
	
	with(objDiv.style){
		backgroundColor = "#FFFFFF";
		filter = "alpha(opacity=80)";
		opacity = 0.8;
		position = "absolute";
		left = sLeft + "px";
		top = sTop + "px";
		cursor = "wait";
		zIndex = 9999;
		width = w
		height = h;
	}
	objDiv.innerHTML = [
		"<table width='" + w + "' height='" + h + "' border='0' cellspacing='0' cellpadding='0' onSelectStart='return false;' onMouseDown='return false;'>",
		"<tr>",
		"<td align='center' valign='middle'><b><i><font size='+2'>" + msg + "</font></i></b>&nbsp;&nbsp;<img src='../image/wait.gif' alt=''></td>",
		"</tr>",
		"</table>"
	].join("\n");
	objBody.appendChild(objDiv);
}


/**
 *
 */
function openMassCalc() {
	var url = location.href;
	if ( url.indexOf("/index") != -1 ) {
		url = url.substring(0, url.indexOf("/index") + 1);
	}
	else if ( url.indexOf("/ja") != -1 ) {
		url = url.substring(0, url.indexOf("/ja") + 1);
	}
	else if ( url.indexOf("/en") != -1 ) {
		url = url.substring(0, url.indexOf("/en") + 1);
	}
	else {
		url = url.substring(0, url.indexOf("/jsp") + 1);
	}
	url += "MassCalc.html";
	if ( ie ) {
		leftX = window.screenLeft + document.body.clientWidth - 350;
		topY =  window.screenTop + 20;
	}
	else {
		leftX = window.screenX + document.body.clientWidth - 350;
		topY =  window.screenTop + 20;
	}
	win = window.open(url, "MassCalc",
		'width=380,height=380,menubar=no,toolbar=no,scrollbars=no,status=no,left=' + leftX + ',top=' + topY + ',screenX=' + leftX + ',screenY=' + topY);
	win.focus();
}

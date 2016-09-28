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
 * DbManager.js
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
var hostName
var ipAddress;
var inputUrlHtml;

/**
 *
 */
function initLoad(hostName, ipAddress) {
	this.hostName = hostName;
	this.ipAddress = ipAddress;
	this.inputUrlHtml = document.getElementById("inputUrl").innerHTML;
	if ( !!document.formEdit ) {
		var objForm = document.formEdit;
		var siteNo = objForm.siteNo.value;
		objForm.siteNo.focus();
		setBgColor();
		if ( siteNo == -1 ) {
			objForm.siteDb.readOnly = false;
			objForm.siteDb.className = "";
			objForm.siteUrl.readOnly = true;
			objForm.siteUrl.className = "readOnly";
			objForm.btnAdd.disabled = false;
			objForm.btnEdit.disabled = true;
			objForm.btnDelete.disabled = true;
			document.formEdit.siteUrl.value = getMyServerUrl();
		}
		else if ( siteNo == 0 ) {
			objForm.siteDb.readOnly = true;
			objForm.siteDb.className = "readOnly";
			objForm.btnAdd.disabled = true;
			objForm.btnEdit.disabled = false;
			objForm.btnDelete.disabled = true;
			showInternalUrlSelect();
		}
		else {
			var tagid = "no" + siteNo;
			if ( document.getElementById(tagid + "Type" ).value == "internal" ) {
				objForm.siteDb.readOnly = true;
				objForm.siteDb.className = "readOnly";
				objForm.siteUrl.readOnly = true;
				objForm.siteUrl.className = "readOnly";
			}
			objForm.btnAdd.disabled = true;
			objForm.btnEdit.disabled = false;
			objForm.btnDelete.disabled = false;
		}
	}
}

/**
 *
 */
function selectNo() {
	var objForm = document.formEdit;
	var siteNo = objForm.siteNo.value;
	var siteNoList = document.formEdit.siteNo.options;
	setBgColor();
	showInputUrl();

	if ( siteNo == -1 ) {
		objForm.siteDb.value = "";
		objForm.siteShortLabel.value = "";
		objForm.siteLongLabel.value = "";
		objForm.siteType[0].checked = true;
		objForm.siteType[0].disabled = false;
		objForm.siteType[1].disabled = false;
		objForm.siteType[0].checked = true;
		setClass('intLabel', 'readOnly', '');
		setClass('extLabel', 'readOnly', '');
		objForm.siteUrl.readOnly = true;
		objForm.siteUrl.className = "readOnly";
		objForm.siteDb.readOnly = false;
		objForm.siteDb.className = "";
		objForm.btnAdd.disabled = false;
		objForm.btnEdit.disabled = true;
		objForm.btnDelete.disabled = true;
		document.formEdit.siteUrl.value = getMyServerUrl();
	}
	else {
		var tagid = "no" + siteNo;
		var element1 =  document.getElementById(tagid + "Db");
		var element2 =  document.getElementById(tagid + "ShortLabel");
		var element3 =  document.getElementById(tagid + "LongLabel");
		var element4 =  document.getElementById(tagid + "Url");
		if ( navigator.userAgent.indexOf("Firefox") == -1 ) {
			db = element1.innerText;
			sLabel = element2.innerText;
			lLabel = element3.innerText;
			val4 = element4.innerText;
		}
		else {
			db = element1.textContent;
			sLabel = element2.textContent;
			lLabel = element3.textContent;
			url = element4.textContent;
		}
		objForm.siteDb.value = db;
		objForm.siteShortLabel.value = sLabel;
		objForm.siteLongLabel.value = lLabel;
		objForm.siteUrl.value = url;

		if ( document.getElementById(tagid + "Type").value == "internal" ) {
			objForm.siteDb.readOnly = true;
			objForm.siteDb.className = "readOnly";
			objForm.siteType[0].disabled = false;
			objForm.siteType[1].disabled = true;
			objForm.siteType[0].checked = true;
			setClass('intLabel', 'readOnly', '');
			setClass('extLabel', '', 'readOnly');
			objForm.siteUrl.readOnly = true;
			objForm.siteUrl.className = "readOnly";
		}
		else {
			objForm.siteDb.readOnly = false;
			objForm.siteDb.className = "";
			objForm.siteType[0].disabled = true;
			objForm.siteType[1].disabled = false;
			objForm.siteType[1].checked = true;
			setClass('intLabel', '', 'readOnly');
			setClass('extLabel', 'readOnly', '');
			objForm.siteUrl.readOnly = false;
			objForm.siteUrl.className = "";
		}
		if ( siteNo == 0 ) {
			objForm.siteUrl.readOnly = false;
			objForm.siteUrl.className = "";
			objForm.btnAdd.disabled = true;
			objForm.btnEdit.disabled = false;
			objForm.btnDelete.disabled = true;
			showInternalUrlSelect();
		}
		else {
			objForm.btnAdd.disabled = true;
			objForm.btnEdit.disabled = false;
			objForm.btnDelete.disabled = false;
		}
	}
}

/**
 *
 */
function selectUrlType() {
	objForm = document.formEdit;
	var siteNo =  document.formEdit.siteNo.value;
	if ( siteNo == -1 ) {
		if ( objForm.siteType[1].checked ) {
			objForm.siteUrl.readOnly = false;
			objForm.siteUrl.className = "";
			objForm.siteUrl.value = "http://";
		}
		else {
			objForm.siteUrl.readOnly = true;
			objForm.siteUrl.className = "readOnly";
			document.formEdit.siteUrl.value = getMyServerUrl();
		}
	}
}

/**
 *
 */
function setClass(elementId, className1, className2) {
	if ( !op && !ie && !ns4 && !ns6 ) {
		alert("Your browser is not supported.");
		return;
	}
	if ( ns4 ) {//NS4
		element = document.layers[elementId];
	}
	else {		//OP,IE,NS6
		element = document.getElementById(elementId);
	}
	if ( element.className == className1 ) {
		element.className = className2;
	}
}

/**
 *
 */
function setBgColor() {
	var OFF_COLOR = "WhiteSmoke";
	var ON_COLOR = "LightSteelBlue";
	var objForm = document.formEdit;
	var siteNo = objForm.siteNo.value;
	var siteNoList = document.formEdit.siteNo.options;
	var num = siteNoList.length;
	if ( num == 1 ) {
		return;
	}
	for ( i = 1; i < num; i++ ) {
		var id = "row" + siteNoList[i].value;
		var val = OFF_COLOR;
		if ( siteNo != -1 && siteNoList[i].value == siteNo ) {
			val = ON_COLOR;
		}
		document.getElementById(id).style.background = val;
	}
}

/**
 *
 */
function getMyServerUrl() {
	var element = document.getElementById("no0Url");
	if ( navigator.userAgent.indexOf("Firefox") == -1 ) {
		val = element.innerText;
	} else {
		val = element.textContent;
	}
	return val;
}

/**
 *
 */
function checkInputValue(act) {
	var objForm = document.formEdit;
	var siteNo = objForm.siteNo.value;
	var siteUrl = objForm.siteUrl.value;
	var url = siteUrl.toLowerCase();
	var isInternal = isInternalUrl(url);
	if ( objForm.siteType[0].checked && !isInternal ) {
		alert("Please enter a URL of internal");
		return false;
	}
	else if ( objForm.siteType[1].checked && isInternal ) {
		alert("Please enter a URL of external");
		return false;
	}
	objForm.act.value = act;
	if ( act == "delete" ) {
		return confirm("Are you sure?");
	}
	return true;
}

/**
 *
 */
function isInternalUrl(url) {
	if (   url.indexOf("localhost") != -1
		|| url.indexOf("127.0.0.1") != -1 
		|| (this.hostName != "" && url.indexOf(hostName) != -1 )
		|| (this.ipAddress != "" && url.indexOf(ipAddress) != -1) ) {
		return true;
	}
	return false;
}

/**
 *
 */
function showInternalUrlSelect() {
	var html =  "<select name=\"siteUrl\" style=\"width:100%;\">\n";
	var names = [this.hostName, this.ipAddress, "localhost"];
	for ( var i = 0; i < names.length; i++) {
		var url = "http://" + names[i] + "/MassBank/";
		var selected = "";
		if ( url == getMyServerUrl() ) {
			selected = " selected";
		}
		html += "<option value=\"" + url + "\"" + selected + ">" + url + "</option>";
	}
	html +=  "</select>\n";
	var element = document.getElementById("inputUrl");
	element.innerHTML = html;
}

/**
 *
 */
function showInputUrl() {
	var element = document.getElementById("inputUrl");
	element.innerHTML = this.inputUrlHtml;
}

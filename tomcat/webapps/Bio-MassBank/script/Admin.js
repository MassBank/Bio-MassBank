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
 * Admin.js
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
var OFF_COLOR = "WhiteSmoke";
var ON_COLOR = "LightSteelBlue";
var prevIndex = -1;
var ev;

window.document.onkeydown = function(e){ ev = e; }

/**
 *
 */
function check(index) {
	objForm = document.form_regist;
	idLength = objForm.id.length;
	if ( idLength == null ) {
		obj = document.getElementById( String("row0") );
		if ( objForm.id.checked ) {
			obj.style.background = ON_COLOR;
		}
		else {
			obj.style.background = OFF_COLOR;
		}
		prevIndex = -1;
		return;
	}

	isShift = false;
	if ( navigator.appName.indexOf("Microsoft") != -1 ) {
		if ( window.event.shiftKey ) {
			isShift = true;
		}
	}
	else {
		if ( ev != null && ev.shiftKey ) {
			isShift = true;
			ev = null;
		}
	}
	
	if ( isShift && prevIndex != -1 ) {
		if ( index < prevIndex ) {
			start = index; end = prevIndex;
		}
		else {
			start = prevIndex; end = index;
		}
		isCheck = objForm.id[index].checked;
		if ( isCheck ) {
			bgcolor = ON_COLOR;
		}
		else {
			bgcolor = OFF_COLOR;
		}
		for ( i = start; i <= end; i++ ) {
			objForm.id[i].checked = isCheck;
			obj = document.getElementById( String("row" + i) );
			obj.style.background = bgcolor;
		}
	}
	else {
		obj = document.getElementById( String("row" + index) );
		if ( objForm.id[index].checked ) {
			obj.style.background = ON_COLOR;
		}
		else {
			obj.style.background = OFF_COLOR;
		}
	}
	prevIndex = index;
}

/**
 *
 */
function checkAll() {
	
	objForm = document.form_regist;
	isCheck = objForm.chkAll.checked;
	if ( isCheck ) {
		bgcolor = ON_COLOR;
	}
	else {
		bgcolor = OFF_COLOR;
	}
	
	idLength = objForm.id.length;
	if ( idLength != null ) {
		for ( i=0; i<idLength; i++ ) {
			if (objForm.id[i].disabled) {
				continue;
			}
			objForm.id[i].checked = isCheck;
			document.getElementById(String("row" + i)).style.background = bgcolor;
		}
	}
	else {
		objForm.id.checked = isCheck;
		document.getElementById(String("row0")).style.background = bgcolor;
	}
}

/**
 *
 */
function selectDb() {
	url = location.href;
	url = url.split("?")[0];
	url = url.split("#")[0];
	dbVal = document.form_regist.db.value;
	if ( dbVal != "" ) {
		url += "?db=" + dbVal;
	}
	location.href = url;
}

/**
 *
 */
function checkDb() {
	if ( document.form_regist.db.value == "" ) {
		alert("Please select a database.");
		return false;
	}
	return true;
}

/**
 * 
 */
function doRegister() {
	ret = checkDb();
	if ( ret ) {
		document.form_regist.act.value = "register";
	}
	return ret;
}

/**
 * 
 */
function doGet() {
	ret = checkDb();
	if ( ret ) {
		document.form_regist.act.value = "get";
	}
	return ret;
}

/**
 * 
 */
function doDelete() {
	ret = checkDb();
	if ( !ret ) {
		return false;
	}
	objForm = document.form_regist;
	chkFlag = false;
	idLength = objForm.id.length;
	if ( idLength != null ) {
		for ( i = 0; i < idLength; i++ ){
			if (objForm.id[i].disabled) {
				continue;
			}
			else if (objForm.id[i].checked) {
				chkFlag = true;
				break;
			}
		}
	}
	else {
		if (objForm.id.checked) {
			chkFlag = true;
		}
	}
	
	if ( !chkFlag ) {
		alert("Please select one or more checkbox.");
		return false;
	}
	
	if ( confirm("Are you sure?") ) {
		objForm.act.value = "delete";
		return true;
	}
	else {
		return false;
	}
}


/**
 * 
 */
function viewRecord(url) {
	win = window.open(url, "RecView", "width=1024, menubar=no, resizable=yes, status=no, toolbar=no, location=no, scrollbars=yes, directories=no" );
	win.focus();
}

/**
 *
 */
function popupMolView(url) {
	
	if ( ie ) {
		leftX = window.screenLeft + document.body.clientWidth - 250;
		topY =  window.screenTop;
	}
	else {
		leftX = window.screenX + document.body.clientWidth - 250;
		topY =  window.screenTop;
	}
	win = window.open(url, "MolView",
		'width=230,height=240,menubar=no,toolbar=no,scrollbars=no,status=no,left='
		 + leftX + ', top=' + topY + ',screenX=' + leftX + ',screenY=' + topY + '' );
	win.focus();
}

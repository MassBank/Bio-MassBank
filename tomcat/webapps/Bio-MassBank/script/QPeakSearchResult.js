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
 * SearchResult.js
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
var grid;
var is_checked = [];

window.onload = function() {
	initCorners();

	grid = $("#result_list");
	grid.jqGrid({
		datatype: 'local',
		height: 'auto',
		colNames: [ 'ID', 'Record Title', 'Structure', 'Formula', 'Exact Mass', 'Score' ],
		colModel: [
			{ name:'rid', index:'rid', width:130, align:'center' },
			{ name:'title', index:'title', width:380 },
			{ name:'struct', index:'struct', width:100, align:'center', sortable:false },
			{ name:'formula', index:'formula', width:100, align:'center' },
			{ name:'emass', index:'emass', width:100, align:'center', formatter:'number',formatoptions:{decimalPlaces: 5} },
			{ name:'score', index:'score', width:80, align:'center', formatter:'number',formatoptions:{decimalPlaces: 5} }
		],
//		pager: '#pager',
		pgbuttons: true,
		multiselect: true,
		viewrecords: true,
		subGrid: false,
		rowNum:25,
		rownumbers: false,
		loadComplete: function() {
			setRowHeiht(grid);
		},
		onSelectRow: function(row, checked) {
			is_checked[row] = checked;
		},
		onSelectAll: function(rows, checked) {
			var grid_ids = String(rows).split(',');
			for ( var i = 0; i < grid_ids.length; i++ ) {
				var row = grid_ids[i];
				is_checked[row] = checked;
			}
		},
		onSortCol: function(index, col, sortorder) {
		}
	});
	addCompoundInfo();
	grid.css("font-size", "14px");
	grid.trigger("reloadGrid");
}


/**
 * 
 */
function addCompoundInfo() {
	var index = 0;
	var id_list      = $("#id_list :input");
	var title_list   = $("#title_list :input");
	var formula_list = $("#formula_list :input");
	var emass_list   = $("#emass_list :input");
	var img_url_list = $("#img_url_list :input");
	var score_list   = $("#score_list :input");
	title_list.each(function () {
		var info = {};
		var id = id_list.val();
		info.rid = id;
		info.title = "<a href=\"./Record.jsp?id=" + id + "\" target=\"_blank\">" + $(this).val() + "</a>";
		info.emass = emass_list.val();
		info.formula = formula_list.val();
		info.struct = "<img src=" + img_url_list.val() + " height=100 alt=\"not&nbsp;available\">";
		info.score = score_list.val();
		grid.addRowData(++index, info);

		id_list = id_list.next();
		formula_list = formula_list.next();
		emass_list = emass_list.next();
		img_url_list = img_url_list.next();
		score_list = score_list.next();
	});
}

/**
 * 
 */
function setRowHeiht(grid) {
	var ids = grid.getDataIDs();
	for ( var i = 0; i < ids.length; i++ ) {
		grid.setRowData(ids[i], false, {height:100});
	}
}

/**
 * 
 */
function getSelectIDs() {
	var id_list = new Array();
	var index = 0;
	$("#id_list :input").each(function () {
		id_list[index++] = $(this).val();
	});
	var sel_acc = "";
	var ids = grid.getDataIDs();
	for ( var i = 0; i < ids.length; i++ ) {
		var row = ids[i];
		var checked = is_checked[row];
		if ( checked ) {
			sel_acc += id_list[i] + ",";
		}
	}
	sel_acc = sel_acc.substring(0, sel_acc.length-1);
	return sel_acc;
}


/**
 * 
 */
function showSpectra() {
	var url = "./MultipleDisplay.jsp?id=" + getSelectIDs();
	window.open(url);
}

/**
 * 
 */
function searchSpectrum() {
	var url = "./SpectrumSearch.jsp?qid=" + getSelectIDs();
	window.open(url);
}


/**
 * 
 */
function initCorners() {
	var settings = {
		tl: { radius: 10 },
		tr: { radius: 10 },
		bl: { radius: 10 },
		br: { radius: 10 },
		antiAlias: true
	}
	new curvyCorners(settings, "#search_parameter");
}

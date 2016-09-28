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
var is_open = false;
var is_checked = [];

window.onload = function() {
	grid = $("#result_list");
	grid.jqGrid({
		datatype: 'local',
		height: 'auto',
		colNames: [ 'Name', 'Structure', 'Formula', 'Exact Mass' ],
		colModel: [
			{ name:'cname', index:'cname', width:500 },
			{ name:'struct', index:'struct', width:100, align:'center', sortable:false },
			{ name:'formula', index:'formula', width:100, align:'center' },
			{ name:'emass', index:'emass', width:100, align:'center', formatter:'number',formatoptions:{decimalPlaces: 5} }
		],
//		pager: '#pager',
		pgbuttons: true,
		multiselect: true,
		viewrecords: true,
		subGrid: true,
		subGridRowExpanded: function(subgrid_id_name, row) {
			showRecordInfo(subgrid_id_name);
		},
		rowNum:25,
		rownumbers: false,
		loadComplete: function() {
			setRowHeiht(grid);
		},
		onSelectRow: function(row, checked) {
			selectAllGrid(row, checked);
			if ( checked ) {
				checkSelectAll();
			}
		},
		onSelectAll: function(rows, checked) {
			var grid_ids = String(rows).split(',');
			for ( var i = 0; i < grid_ids.length; i++ ) {
				var grid_id = grid_ids[i];
				selectAllGrid(grid_id, checked);
			}
		},
		onSortCol: function(index, col, sortorder) {
			initFlag();
			if ( is_open ) {
				$("#open_all").val("Open All Tree");
				is_open = !is_open;
			}
		}
	});
	addCompoundInfo();
	grid.css("font-size", "14px");
	grid.trigger("reloadGrid");

	initCorners();
}


/**
 * 
 */
function addCompoundInfo() {
	var index = 0;
	var name_list    = $("#compound_name_list :input");
	var formula_list = $("#formula_list :input");
	var emass_list   = $("#emass_list :input");
	var img_url_list = $("#img_url_list :input");
	name_list.each(function () {
		var info = {};
		info.cname = "<span class='compound_name'>" + $(this).val() + "</span>";
		info.emass = emass_list.val();
		info.formula = formula_list.val();
		info.struct = "<img src=" + img_url_list.val() + " height=100 alt=\"not&nbsp;available\">";
		grid.addRowData(++index, info);

		formula_list = formula_list.next();
		emass_list = emass_list.next();
		img_url_list = img_url_list.next();
	});
	initFlag();
}

/**
 * 
 */
function initFlag() {
	var ids = grid.getDataIDs();
	for ( var i = 1; i <= ids.length; i++ ) {
		is_checked[i] = new Array();
	}
}

/**
 * 
 */
function showRecordInfo(subgrid_id_name) {
	var subgrid_table_id = subgrid_id_name + "_sub";
	$("#" + subgrid_id_name).html( "<table id='" + subgrid_table_id + "' class='scroll'></table>" );
	var subgrid = $("#" + subgrid_table_id);
	var grid_id = subgrid_id_name.replace("result_list_","");
	subgrid.jqGrid({
		datatype: 'local',
		height: 'auto',
		colNames: [ 'Record Title', 'ID' ],
		colModel: [
			{ name:'title', index:'title', width:600 },
			{ name:'acc', index:'acc', width:150, align:'center' }
		],
		onSelectRow: function(row, checked) {
			is_checked[grid_id][row] = checked;
		},
		onSelectAll: function(rows, checked) {
			var subgrid_ids = String(rows).split(',');
			for ( var i = 0; i < subgrid_ids.length; i++ ) {
				is_checked[grid_id][subgrid_ids[i]] = checked;
			}
			if ( checked != is_checked[grid_id][0] ) {
				grid.setSelection(grid_id, true);
			}
			is_checked[grid_id][0] = checked;
		},
		pgtext: false,
		multiselect: true
	});


	var param = window.location.search.substring(1);
	var subgrid_id = 0;
	var record_list = $("#record_list_" + grid_id + " :input");
	record_list.each(function () {
		var info = {};
		var val = $(this).val();
		items = val.split("\t");
		var url = "./Record.jsp?id=" + items[1] + "&" + param;
		info.id = ++subgrid_id;
		info.title = "<a href='" + url + "' target='_blank'>" + items[0] + "</a>";
		info.acc = items[1];
		subgrid.addRowData(subgrid_id, info);
		if ( is_checked[grid_id][subgrid_id] ) {
			subgrid.setSelection(subgrid_id, false);
		}
	});
	if ( is_checked[grid_id][0] ) {
		var subgrid_all_chkbox = $("#cb_result_list_" + grid_id + "_sub");
		subgrid_all_chkbox.attr('checked','checked');
	}
}

/**
 * 
 */
function setRowHeiht(grid) {
	var ids = grid.getDataIDs();
	for ( var i = 0; i < ids.length; i++) {
		grid.setRowData(ids[i], false, {height:100});
	}
}

/**
 * 
 */
function checkSelectAll() {
	var vals = grid.getGridParam('selarrrow');
	var ids = grid.getDataIDs();
	var sel_ids = String(vals).split(',');
	for ( var i = 0; i < ids.length; i++ ) {
		var is_found = false;
		for ( var j = 0; j < ids.length; j++ ) {
			if ( ids[i] == sel_ids[j] ) {
				is_found = true;
				break;
			}
		}
		if ( !is_found ) {
			break;
		}
	}
	if ( is_found ) {
		$("#cb_result_list").attr('checked','checked');
	}
}

/**
 * 
 */
function selectAllGrid(grid_id, checked) {
	var subgrid = $("#result_list_" + grid_id + "_sub");
	var subgrid_ids = subgrid.getDataIDs();
	for ( var i = 0; i < subgrid_ids.length; i++ ) {
		var subgrid_id = subgrid_ids[i];
		if ( checked != is_checked[grid_id][subgrid_id] ) {
			subgrid.setSelection(subgrid_id, false);
		}
		is_checked[grid_id][subgrid_id] = checked;
	}
	is_checked[grid_id][0] = checked;
	var record_list = $("#record_list_" + grid_id + " :input");
	var index = 1;
	record_list.each(function () {
		var val = $(this).val();
		is_checked[grid_id][index++] = checked;
	});


	var subgrid_all_chkbox = $("#cb_result_list_" + grid_id + "_sub");
	if ( checked ) { 
		subgrid_all_chkbox.attr('checked','checked');
	}
	else {
		subgrid_all_chkbox.removeAttr('checked');
	}
}

/**
 * 
 */
function openAllTree() {
	var ids = grid.getDataIDs();
	var button = $("#open_all");
	for ( var i = 0; i < ids.length; i++) {
		if ( is_open ) {
			grid.collapseSubGridRow(ids[i]);
			button.val("Open All Tree");
		}
		else {
			grid.expandSubGridRow(ids[i]);
			button.val("Close All Tree");
		}
	}
	is_open = !is_open;
}

/**
 * 
 */
function getSelectIDs() {
	var sel_acc = "";
	var ids = grid.getDataIDs();
	for ( var i = 0; i < ids.length; i++ ) {
		var grid_id = ids[i];
		var record_list = $("#record_list_" + grid_id + " :input");
		var id_list = new Array();
		var index = 1;
		record_list.each(function () {
			var val = $(this).val();
			items = val.split("\t");
			id_list[index++] = items[1];
		});
		for ( var j = 1; j < is_checked[grid_id].length; j++ ) {
			var checked = is_checked[grid_id][j];
			if ( checked ) {
				sel_acc += id_list[j] + ",";
			}
		}
	}
	sel_acc = sel_acc.substring(0, sel_acc.length-1);
	return sel_acc;
}


/**
 * 
 */
function showSpectra() {
	var param = window.location.search.substring(1);
	var url = "./MultipleDisplay.jsp?id=" + getSelectIDs() + "&" + param;
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
	var obj = new curvyCorners(settings, "#search_parameter");
}

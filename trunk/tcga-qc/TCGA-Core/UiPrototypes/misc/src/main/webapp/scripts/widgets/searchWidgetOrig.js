Ext.namespace('marcs.widgets.search');

/*
 * Fields:
 * 	searchKeyName - the name to display to the user
 *  searchKey - the key used by the search engine for this field
 *  searchType - the type of field [text by default, number, bool, date, percent, list, rollupimagelist]
 *  operatorType - 0 = a simple =, 1 = a drop down of comparison operators
 *  searchList - a list of values to put in a combo
 *  searchValue - placeholder to stuff the value into
 *  advanced - boolean, false by default, flags whether this field only shows up for advanced searches
 */
marcs.widgets.search.data = {searchFields: 
	[{
		searchKeyName: 'Recall Event ID'
	}, {
		searchKeyName: 'Center',
		searchType: 'list',
		store: marcs.util.formFields.centerStore,
		displayField: 'name',
		valueField: 'id'
	}, {
		searchKeyName: 'District',
		searchType: 'list',
		store: marcs.util.formFields.districtStore,
		displayField: 'name',
		valueField: 'id',
		relatedTo: 'Center'
	}, {
		searchKeyName: 'Start Date',
		searchType: 'date'
	}, {
		searchKeyName: 'Recalling Firm FEI'
	}, {
		searchKeyName: 'Recall #',
		advanced: true
	}]
};

marcs.widgets.search.advanced = false;

marcs.widgets.search.toggleAdvancedSearchCriteriaVisibility = function(toggle) {
	var searchTermStore = Ext.StoreMgr.get('searchTermStore');
	for (var ndx=0;ndx < searchTermStore.getCount();ndx++) {
		var rec = searchTermStore.getAt(ndx);
		var field = Ext.getCmp(rec.get('searchKeyName'));
		if (rec.get('advanced')) {
			if (rec.get('advanced') == toggle) {
				field.show();
			}
			else {
				field.hide();
			}
		}
	}
}


marcs.widgets.search.viewLessSearchCriteria = function() {
	Ext.getCmp('viewLessSearchCriteria').hide();
	Ext.getCmp('viewMoreSearchCriteria').show();

	marcs.widgets.search.toggleAdvancedSearchCriteriaVisibility(false);
}

marcs.widgets.search.viewMoreSearchCriteria = function() {
	Ext.getCmp('viewMoreSearchCriteria').hide();
	Ext.getCmp('viewLessSearchCriteria').show();

	marcs.widgets.search.toggleAdvancedSearchCriteriaVisibility(true);
}

marcs.widgets.search.numericField = function(type) {
		if (type == 'date' ||
		type == 'number' ||
		type == 'percent') {
		return true;
	}
	else {
		return false;
	}
}

marcs.widgets.search.opResetNumeric = function(id) {
	var expander = Ext.get(id + '-expander');
	var op1 = Ext.getCmp('op-' + id);
	var op2 = Ext.getCmp('op-' + id + '-2');

	if (expander.dom.innerHTML != '+') {
		op1.setValue('>');
		op2.setValue('<');
	}
}

marcs.widgets.search.toggleNumericDisplay = function(id) {
	var expander = Ext.get(id + '-expander');
	var secondField = Ext.getCmp(id + '-2wrap');
	var op1 = Ext.getCmp('op-' + id);
	var op2 = Ext.getCmp('op-' + id + '-2');
	var field2 = Ext.getCmp(id + '-2');
	
	if (expander.dom.innerHTML == '+') {
		expander.dom.innerHTML = '&#151;';
		// Remove the less than and equal operators
		op1.store.loadData([['&gt;', 62], ['&ge;', 8805]]);
		secondField.show();

		// Set the appropriate values for the operators, they need to stay in synch as
		//	we can't mix > and <= and so on
		op1.setValue('>');
		op2.setValue('<');
		
		// Modify the qtip - no obvious way to get to it other than manually looking it up
		expander.dom.attributes[2].nodeValue = 'This is a tooltip';
	}
	else {
		expander.dom.innerHTML = '+';
		// Restore all of the operators
		op1.store.loadData([['&gt;', 62], ['&ge;', 8805], ['&lt;', 60], ['&le;', 8804], ['=', 61]]);
		
		// Get rid of the value in the 2nd field so it's not included in searches
		field2.setValue(null);
		
		secondField.hide();

		// Modify the qtip - no obvious way to get to it other than manually looking it up
		expander.dom.attributes[2].nodeValue = 'This is a tooltip';
	}
}

marcs.widgets.search.getAndFormatValues = function(searchRec) {
	var searchKey = searchRec.get('searchKey');

	// Figure out how we should represent the value in search terms, most we just use the string
	var searchType = searchRec.get('searchType');
	
	// Set up a var to hold the munged search value which is returned
	var searchValue = null;
	
	// Get the first value
	var field1 = Ext.getCmp(searchKey);
	if (!field1) {
		field1 = Ext.get(searchKey);
	}
	// Short term hack
	if (!field1) {
		return null;
	}
	var value1 = field1.getValue();
	if (value1 == null || value1 === '') {
		return null;
	}
	
	// If appropriate, get the second value
	var value2 = null;
	if (marcs.widgets.search.numericField(searchType)) {
		var field2 = Ext.getCmp(searchKey + '-2');
		if (!field2) {
			field2 = Ext.get(searchKey);
		}
		// Short term hack
		if (!field2 || field2.getValue() == '') {
			value2 = null;
		}
		else {
			value2 = field2.getValue();
		}
	}
	
	// For numeric searches we need to set the default min/max
	if (searchType == 'date') {
		var maxGe = 21001231;
		var maxGt = 21001231;
		var minLe = 19000101;
		var minLt = 19000101;
		value1 = value1.format('Ymd');
	}
	else {
		var maxGe = 100;
		var maxGt = 101;
		var minLe = 0;
		var minLt = '-0';
	}

	if (searchRec.get('operatorType') == 1) {
		var op1 = Ext.getCmp('op-' + searchKey);
		var op2 = Ext.getCmp('op-' + searchKey + '-2');
		
		if (op1.getValue() == '>') {
			searchValue = '{' + value1 + ' TO ';
			if (value2 == null) {
				searchValue += maxGt + '}';
			}
		}
		// Unicode for &ge;
		else if (op1.getValue() == String.fromCharCode(8805)) {
			searchValue = '[' + value1 + ' TO ';
			if (value2 == null) {
				searchValue += maxGe + ']';
			}
		}
		else if (op1.getValue() == '<') {
			searchValue = '{' + minLt + ' TO ' + value1 + '}';
		}
		// Unicode for &le;
		else if (op1.getValue() == String.fromCharCode(8804)) {
			searchValue = '[' + minLe + ' TO ' + value1 + ']';
		}
		else if (op1.getValue() == '=') {
			// No value munging to do since the value returned is just the value entered
			searchValue = value1;
		}
		
		if (value2 != null) {
			if (op2.getValue() == '<') {
				searchValue += value2 + '}';
			}
			// Unicode for &le;
			else if (op2.getValue() == String.fromCharCode(8804)) {
				searchValue += value2 + ']';
			}
		}
	}
	else {
		searchValue = value1;
	}

	return searchValue;
}

marcs.widgets.search.panel = Ext.extend(Ext.Panel, {
	refresh: function() {
		for (var ndx0 in this.searchTerms) {
			var searchLine = this.searchTerms[ndx0];
			for (var ndx1 in searchLine.items) {
				if (searchLine.items[ndx1].id) {
					var element = Ext.getCmp(searchLine.items[ndx1].id).getEl();
					if (element.parent().getWidth() == 0) {
						element.parent().setWidth(element.getWidth() + 20);
					}
				}
			}
		}
	},
	
	operatorRenderer: function(rec, idModifier, secondOp) {
		if (rec.get('operatorType') == 1) {
			var opData = [['&gt;', 62], ['&ge;', 8805], ['&lt;', 60], ['&le;', 8804], ['=', 61]];
			if (secondOp) {
				opData = [['&lt;', 60], ['&le;', 8804]]
			}
			
			return {
				border: false,
				width: 40,
				style: 'padding-right: 3px;',
				items: [{
					id: 'op-' + rec.get('searchKey') + (idModifier?'-' + idModifier:''),
					hiddenName: 'ophn-' + rec.get('searchKey') + (idModifier?'-' + idModifier:''),
					xtype: 'combo',
					mode: 'local',
					width: 20,
					listWidth: 30,
					store: new Ext.data.SimpleStore({
						storeId: 'opStore-' + rec.get('searchKey') + (idModifier?'-' + idModifier:''),
						fields: ['op', 'value'],
						data: opData
					}),
					displayField: 'op',
					valueField: 'op',
					value: '=',
					triggerAction: 'all',
					editable: false,
					listeners: {
						select: function(combo, rec) {
							combo.setValue(String.fromCharCode(rec.get('value')));
							if (combo.id.indexOf('-2') == -1) {
								var combo2 = Ext.getCmp(combo.id + '-2');
							
								if (rec.get('op') == '&gt;') {
									combo2.setValue('<');
								}
								else {
									combo2.setValue(String.fromCharCode(8804));
								}
							}
							else {
								var combo2 = Ext.getCmp(combo.id.substr(0, combo.id.length - 2));

								if (rec.get('op') == '&lt;') {
									combo2.setValue('>');
								}
								else {
									combo2.setValue(String.fromCharCode(8805));
								}
							}
						}
					}
				}]
			}
		}
			return {
				cls: 'x-form-item',
				bodyStyle: 'text-align: center;',
				border: false,
				style: 'padding-right: 3px;',
				width: 40,
				html: '='
			};
	},
	
	valueField: function(rec, idModifier) {
		selectionFunctions = {
			bool: function() {
				return {
					id: rec.get('searchKey') + 'Combo' + (idModifier?'-' + idModifier:''),
					hiddenName: rec.get('searchKey') + (idModifier?'-' + idModifier:''),
					xtype: 'combo',
					mode: 'local',
					width: 100,
					store: new Ext.data.SimpleStore({
						fields: ['bool'],
						data: [['True'], ['False']]
					}),
					displayField: 'bool',
					valueField: 'bool',
					triggerAction: 'all',
					editable: false
				}
			},
			
			list: function(rec) {
				var store = null;
				var displayField = 'itemData';
				var valueField = 'itemData';

				if (rec.get('store')) {
					store = rec.get('store');
					displayField = rec.get('displayField');
					valueField = rec.get('valueField');
					store.load();
				}
				else {
					var searchList = rec.get('searchList');
		
					var listHtml = '<select id="' + rec.get('searchKey') + '" name="' + rec.get('searchKey') + '">';
					listHtml += '<option></option>';
					for (var ndx = 0;ndx < searchList.length;ndx++) {
						listHtml += '<option>' + searchList[ndx] + '</option>';
					}
					listHtml += '</select>';
					
					var listData = [];
					for (var ndx = 0;ndx < searchList.length;ndx++) {
						listData.push([searchList[ndx]]);
					}
					
					store = new Ext.data.SimpleStore({
						fields: ['itemData'],
						data: listData
					});
				}
				
				return {
					id: rec.get('searchKey') + 'Combo' + (idModifier?'-' + idModifier:''),
					hiddenName: rec.get('searchKey'),
					xtype: 'combo',
					mode: 'local',
					width: 100,
					store: store,
					displayField: displayField,
					valueField: valueField,
					triggerAction: 'all',
					editable: false
				}
			},
		
			date: function(rec) {
				return {
					xtype: 'datefield',
					id: rec.get('searchKey') + (idModifier?'-' + idModifier:''),
					cls: 'x-form-item',
					readOnly: true,
                    format: 'Ymd',
					triggerClass: 'inventory-search-date-trigger x-form-date-trigger',
					width: 100,
					listeners: {
						resize: function() {
							this.getEl().parent().setWidth(118);
						}
					}
				}
			},

			number: function(rec) {
				return {
					xtype: 'numberfield',
					id: rec.get('searchKey') + (idModifier?'-' + idModifier:''),
					name: rec.get('searchKey') + (idModifier?'-' + idModifier:''),
					width: 118
				};
			},
			
			percent: function(rec) {
				return {
					xtype: 'numberfield',
					id: rec.get('searchKey') + (idModifier?'-' + idModifier:''),
					name: rec.get('searchKey') + (idModifier?'-' + idModifier:''),
					width: 118,
					allowNegative: false,
					maxValue: 100,
					minValue: 0
				};
			},
			
			rollupimagelist: function(rec) {
				var searchList = rec.get('searchList');
	
				var listHtml = '<select id="' + rec.get('searchKey') + '" name="' + rec.get('searchKey') + '">';
				listHtml += '<option></option>';
				for (var ndx = 0;ndx < searchList.length;ndx++) {
					listHtml += '<option class="rollup-state-image-' + searchList[ndx] + '" value="' + searchList[ndx] + '">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</option>';
				}
				listHtml += '</select>';
				
				return {
					cls: 'x-form-item',
					border: false,
					width: 125,
					html: listHtml
				}
			}
		};

		if (selectionFunctions[rec.get('searchType')] != null) {
			return selectionFunctions[rec.get('searchType')](rec);
		}
		else {
			return {
				xtype: 'textfield',
				id: rec.get('searchKey') + (idModifier?'-' + idModifier:''),
				name: rec.get('searchKey') + (idModifier?'-' + idModifier:''),
				width: 140
			};
		}
	},
	
	valueTypeRenderer: function(rec) {
		if (marcs.widgets.search.numericField(rec.get('searchType'))) {
			var numericFieldSet = [{
				border: false,
				width: 175,
				style: 'padding-bottom: 5px;',
				layout: 'column',
				items: [
					this.operatorRenderer(rec),
					this.valueField(rec),
					{
						border: false,
						width: 15,
						style: 'padding: 3px 0px 0px 3px;',
						html: marcs.util.display.displayLinkWithAction({
							id: rec.get('searchKey') + '-expander',
							qtip: 'This is a tooltip',
							text: '+',
							action: 'marcs.widgets.search.toggleNumericDisplay(\'' + rec.get('searchKey') + '\');'
						})
					}
				]
			}];
			
			numericFieldSet.push({
				id: rec.get('searchKey') + '-2wrap',
				hidden: true,
				border: false,
				width: 175,
				layout: 'column',
				items: [
					this.operatorRenderer(rec, 2, true),
					this.valueField(rec, 2)
				]
			});
			
			return {
				border: false,
				width: 175,
				items: numericFieldSet
			}
		}
		else {
			return this.valueField(rec);
		}
	},
	
	initComponent: function() {
		var searchTermStore = new Ext.data.JsonStore({
			storeId: 'searchTermStore',
			fields: [
				'searchKeyName',
				'searchKey',
				'searchType',
				'operatorType',
				'searchList',
				'searchValue',
				'advanced',
				'store',
				'displayField',
				'valueField',
				'relatedTo'
			],
			autoLoad: true,
			root: 'searchFields',
			data: marcs.widgets.search.data
		});
		
		var browserSize = marcs.util.display.getBrowserSize(true);
		var searchGridControls = {
			border: false,
			layout: 'column',
			items: [{
				id: 'search-button',
				style: 'padding: 5px',
				xtype: 'button',
				text: 'Search',
				handler: function() {
					// Submit form
					var recallStore = Ext.StoreMgr.get('recallStore');
					recallStore.load();
					
					Ext.getCmp('searchResultsPanel').show();
				}
			}, {
				id: 'search-reset-button',
				style: 'padding: 5px',
				xtype: 'button',
				text: 'Reset',
				handler: function() {
					var form = Ext.getCmp(id + '-search-portlet').getForm();
					
					form.reset();
					
					// Need to set the "=" back to ">" or "<" when the second
					// 	field is visible for numeric fields.
					for (var ndx = 0;ndx < searchTermStore.getCount();ndx++) {
						var rec = searchTermStore.getAt(ndx);
						
						if (marcs.widgets.search.numericField(rec.get('searchType'))) {
							marcs.widgets.search.opResetNumeric(rec.get('searchKey'))
						}
					}
				}
			}]
		};
		
		this.searchTerms = [];

		// Modify the layout/look and feel here		
		for (var ndx = 0;ndx < searchTermStore.getCount();ndx++) {
			var rec = searchTermStore.getAt(ndx);
			
			var searchFields = [{
				cls: 'stdLabel',
				border: false,
				html: rec.get('searchKeyName')
			}];

			searchFields.push(this.valueTypeRenderer(rec));
			
			this.searchTerms.push({
				id: rec.get('searchKeyName'),
				border: false,
				hidden: rec.get('advanced'),
				padding: '5px 0px 0px 5px',
				items: searchFields
			});
		}

		this.id = id + '-search-portal';
//		this.title = (this.title?this.title:'Search Table');
	    this.border = false;
		// Account for the height of the headers
/*		this.tools = [{
			id: 'close',
			handler: function(e, tool, panel) {
				panel.hide();
				
				Ext.getCmp('infrastructure-inventory-table').setWidth(document.documentElement.clientWidth - 40);
			}
		}];*/
	    this.items = [{
			columnWidth: 1,
			items: [
				searchGridControls,
			{
		        id: id + '-search-portlet',
				xtype: 'form',
				autoHeight: true,
				border: false,
				width: 150,
				items: this.searchTerms
			},
				searchGridControls,
			{
				id: 'viewMoreSearchCriteria',
				border: false,
				padding: '10px 0px 10px 5px',
				html: '<a class="stdOnClickLink" onclick="marcs.widgets.search.viewMoreSearchCriteria()">View more search criteria</a>'
			}, {
				id: 'viewLessSearchCriteria',
				border: false,
				padding: '10px 0px 10px 5px',
				hidden: true,
				html: '<a class="stdOnClickLink" onclick="marcs.widgets.search.viewLessSearchCriteria()">View less search criteria</a>'
			}]
	    }];

        marcs.widgets.search.panel.superclass.initComponent.call(this);
	}
});

Ext.reg('searchpanel', marcs.widgets.search.panel);
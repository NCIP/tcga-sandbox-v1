Ext.namespace("marcs.util.formFields");

marcs.util.formFields.stateList = function(config) {
	if (config.nameId == undefined || config.nameId == null) {
		config.nameId = Ext.id();
	}
	if (config.val == undefined) {
		config.val = null;
	}
	if (config.combo == undefined || config.combo == null) {
		config.combo = false;
	}
	
	if (config.combo) {
		var stateStore = new marcs.util.data.stateStore({});
	
		return new Ext.form.ComboBox({
			id: config.nameId,
			hideLabel: true,
			displayField: 'state',
			valueField: 'abbrev',
			mode: 'local',
			triggerAction: 'all',
			value: (config.val?config.val:'select a state'),
			editable: false,
			width: 150,
			store: stateStore
		});
	}
	else {
		var field = '<select id="' + config.nameId + '" name="' + config.nameId + '"tabIndex="' + marcs.util.tabIndex.getNextTabIndex() + '">';
	
		for (var ndx = 0;ndx < marcs.util.data.states.length;ndx++) {
			field += '<option value="' + marcs.util.data.states[ndx][1] + '" ' +
							'ext:qtip="' + marcs.util.data.states[ndx][0] + '"' + 
							(config.val == marcs.util.data.states[ndx][1]?'selected':'') +
							'>' +
							marcs.util.data.states[ndx][1] +
							'</option>';
		}
		
		field += '</select>';
	}

	return field;
}

marcs.util.formFields.dayList = function(nameId, val) {
	return new Ext.form.ComboBox({
		id: nameId,
		hiddenName: nameId,
		hideLabel: true,
		displayField: 'day',
		valueField: 'day',
		mode: 'local',
		triggerAction: 'all',
		value: (val?val:1),
		editable: false,
		width: 40,
		store: marcs.util.data.dayStore
	});
}

marcs.util.formFields.monthList = function(nameId, val) {
	return new Ext.form.ComboBox({
		id: nameId,
		hiddenName: nameId,
		hideLabel: true,
		displayField: 'month',
		valueField: 'month',
		mode: 'local',
		triggerAction: 'all',
		value: (val?val:1),
		editable: false,
		width: 40,
		store: marcs.util.data.monthStore
	});
}

marcs.util.formFields.yearList = function(nameId, base, val) {
	if (!base) {
		base = 2010;
	}
	
	var yearData = [];
	for (var ndx = 0;ndx < 5; ndx++) {
		yearData.push([base + ndx]);
	}
	
	var yearStore = new Ext.data.SimpleStore({
		fields: ['year'],
		autoLoad: true,
		data: [yearData]
	});

	return new Ext.form.ComboBox({
		id: nameId,
		hiddenName: nameId,
		hideLabel: true,
		displayField: 'year',
		valueField: 'year',
		mode: 'local',
		triggerAction: 'all',
		value: (val?val:base),
		editable: false,
		width: 60,
		store: yearStore
	});
}

marcs.util.formFields.operationTypeList = function(nameId, val) {
	var typeStore = new Ext.data.ArrayStore({
		fields: ['optDesc','optVal'],
		autoLoad: true,
		data: [
			['Recall Audit Checks','1'], ['Inspections','2']
		]
	});

	return new Ext.form.ComboBox({
		id: nameId,
		hideLabel: false,
		displayField: 'optDesc',
		valueField: 'optVal',
		mode: 'local',
		triggerAction: 'all',
		value: (val?val:'1'),
		editable: false,
		width: 150,
		store: typeStore
	});
}

marcs.util.formFields.priorityList = function(nameId, val) {
	var typeStore = new Ext.data.ArrayStore({
		fields: ['priorityDesc','priorityVal'],
		autoLoad: true,
		data: [
			['Top Priority','1'], ['Middle Priority','2'], ['Low Priority','3']
		]
	});

	return new Ext.form.ComboBox({
		id: nameId,
		hideLabel: false,
		displayField: 'priorityDesc',
		valueField: 'priorityVal',
		mode: 'local',
		triggerAction: 'all',
		value: (val?val:'3'),
		editable: false,
		width: 100,
		store: typeStore
	});
}

marcs.util.formFields.districtStore = new Ext.data.JsonStore({
	storeId: 'districtStore',
	url: 'json/fdaDistricts.sjson',
	root: 'districts',
	fields: [
		'id',
		'name',
		'acronym',
		'code'
	]
});

marcs.util.formFields.centerStore = new Ext.data.JsonStore({
	storeId: 'centerStore',
	url: 'json/fdaCenters.sjson',
	root: 'centers',
	fields: [
		'id',
		'name'
	]
});

marcs.util.formFields.percentageList = function(nameId, base, val) {
	if (!base) {
		base = 1;
	}
	
	var pctData = [];
	for (var ndx = 0;ndx < 100; ndx++) {
		pctData.push([base + ndx]);
	}
	
	var pctStore = new Ext.data.SimpleStore({
		fields: ['percentOfAC'],
		autoLoad: true,
		data: pctData
	});

	return new Ext.form.ComboBox({
		id: nameId,		
		hideLabel: true,
		displayField: 'percentOfAC',
		valueField: 'percentOfAC',
		mode: 'local',
		triggerAction: 'all',
		value: (val?val:base),
		editable: false,
		width: 50,
		store: pctStore
	});
}

// independent person list(no dependency on district or organization)
marcs.util.formFields.personList = function(nameId, val) {
	var myStore = new Ext.data.JsonStore({
		storeId: 'personStore',
		url: 'json/persons.sjson',
		root: 'persons',
		fields: [
			'id',
			'name'
		],
		autoLoad: true
	});

	return new Ext.form.ComboBox({
		id: nameId,
		store: myStore,
		width: 120,
		emptyText: 'Select a person',
		typeAhead: true,
		mode: 'local',
		triggerAction: 'all',
		forceSelection: true,
		displayField: 'name',
		valueField: 'id'		
	});
}

// load district list, when select a district, auto load person associate with it
marcs.util.formFields.districtList = function(nameId, val) {
	marcs.util.formFields.districtStore.load();

	return new Ext.form.ComboBox({
		id: nameId,
		//hiddenName: nameId,
		store: marcs.util.formFields.districtStore,
		width: 120,
		emptyText: 'District Name',
		typeAhead: true,
		mode: 'local',
		triggerAction: 'all',
		forceSelection: true,
		displayField: 'name',
		valueField: 'id',
		listeners: {
			render: function(combo) {
				//var field = Ext.getCmp('district');
				var field = Ext.getCmp(nameId);
				if (field.value == field.emptyValue) {
					return field.emptyValue;
				}
				
				var selNdx = myStore.find('name', field.value);
				if (selNdx == -1) {
					selNdx = 0;
				}
				var selRec = myStore.getAt(selNdx);
				combo.setValue(selRec.get('id'));
				combo.fireEvent('select', combo, selRec, selNdx);
			},
			// when select a district, load person list			
			select: function(cb, rec) {
				var pStore = Ext.StoreMgr.get('personStore');
				pStore.load({params: rec.get('id'), add: false});
			}
											
		}
	});
}

// person list is loaded when selecting a district
marcs.util.formFields.persons = function(nameId, val) {
	var myStore = new Ext.data.JsonStore({
		storeId: 'personStore',
		url: 'json/persons.sjson',
		root: 'persons',
		fields: [
			'id',
			'name'
		],
		autoLoad: false
	});

	return new Ext.form.ComboBox({
		id: nameId,
		store: myStore,
		width: 120,
		emptyText: 'Select a person',
		typeAhead: true,
		mode: 'local',
		triggerAction: 'all',
		forceSelection: true,
		displayField: 'name',
		valueField: 'id',
		listeners: {
			render: function(combo) {
				var field = Ext.getCmp(nameId);
				if (field.value == field.emptyValue) {
					return field.emptyValue;
				}
				
				var selNdx = districtStore.find('name', field.value);
				if (selNdx == -1) {
					selNdx = 0;
				}
				var selRec = districtStore.getAt(selNdx);
				combo.setValue(selRec.get('id'));
				combo.fireEvent('select', combo, selRec, selNdx);
			}
		}
	});
}

// common dropdown list: store fields: "id" and "name"
marcs.util.formFields.dropdownList = function(nameId, myWidth) {
	// empty store for now...
	// caller should pass in the store
	var dropdownStore = new Ext.data.JsonStore({
		fields: [
			'id',
			'name'
		]
	});
	
	return new Ext.form.ComboBox({
		id: nameId,
		store: dropdownStore,
		width: myWidth,
		emptyText: 'Select an item from list',
		typeAhead: true,
		mode: 'local',
		triggerAction: 'all',
		forceSelection: true,
		displayField: 'name',
		valueField: 'id'		
	});
}


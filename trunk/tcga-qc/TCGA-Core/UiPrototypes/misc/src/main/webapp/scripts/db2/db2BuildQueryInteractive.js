Ext.namespace('tcga.db2.build');

tcga.db2.build.updateDisease = function(query) {
	Ext.get('selectedDisease').update(query?query.get('disease').description + ' [' + query.get('disease').name + ']':null);
}

tcga.db2.build.createPatientClassSelectors = function(resultsDisplay) {
	Ext.get('addPatientClass').hide();
	Ext.get('editPatientClass').hide();
	Ext.get('patientClass').show();
	Ext.get('patientClassNamePanel').show();
	
	// Check to see if a patientClass has already been made part of the query, in which
	//		case we don't have to go back and create things again.
	if (action == 'edit') {
		return;
	}

	var queryClass = 'patientClass';

	var currQuery = tcga.db2.query.storage.getCurrQuery();
	var currValue = currQuery.patientClass.get('name');

	var processKeyEvents = function(tf) {
		currQuery.updateQueryClass(tf.queryClass, 'name', tf.getValue());

		tcga.db2.queryWriter.start();
	}

	new Ext.form.TextField({
		id: queryClass + 'NameTextField',
		renderTo: queryClass + 'Name',
		width: 250,
		emptyText: 'Enter a name',
		value: (currValue?currValue:null),
		queryClass: queryClass,
		enableKeyEvents: true,
		listeners: {
			keyup: processKeyEvents,
			keydown: processKeyEvents
		}
	});

	var patientClassParameterStore = new Ext.data.JsonStore({
//		url:'json/patientClassParameters.sjson',
		storeId: 'patientClassParameterStore',
		root:'patientClassParameters',
		idProperty:'id',
		fields: [
			'id',
			'name',
			'parameters'
		],
		autoLoad: true,
		listeners: {
			load: function(store, recs) {
				var firstSelector = tcga.db2.selectors.display(queryClass, queryClass, recs, resultsDisplay);
				
				// Now open up the first selector box
				tcga.db2.selectors.toggleSelectorBox(Ext.get('selectorBox' + firstSelector).dom, firstSelector);
			}
		},
		data: tcga.db2.localData.patientClassParameters
	});
}

tcga.db2.build.addPatientClass = function() {
	tcga.db2.selectors.queryType.add('patient');
	tcga.db2.build.createPatientClassSelectors('add');
}

tcga.db2.build.editPatientClass = function() {
	tcga.db2.build.createPatientClassSelectors('edit');
}

tcga.db2.build.destroyClass = function(queryClass) {
	var nameTextField = Ext.getCmp(queryClass + 'NameTextField');
	if (nameTextField) {
		nameTextField.destroy();
	}
	tcga.db2.selectors.destroy(queryClass);
}

tcga.db2.build.reset = function(){
	tcga.db2.build.updateDisease(null);

	tcga.db2.build.destroyClass('patientClass');
}

tcga.db2.build.init = function(){
	Ext.get('addPatientClass').setVisibilityMode(Ext.Element.DISPLAY);
	Ext.get('editPatientClass').setVisibilityMode(Ext.Element.DISPLAY);
	Ext.get('patientClassNamePanel').setVisibilityMode(Ext.Element.DISPLAY);
	Ext.get('patientClass').setVisibilityMode(Ext.Element.DISPLAY);
	
	// Update the disease in the buildQueryIdentification div
	tcga.db2.build.updateDisease(tcga.db2.query.storage.getCurrQuery());

	// Now start the queryWriter to put the nascent query into the right gutter box
	tcga.db2.queryWriter.start();

	if (!loadMask) {
		var loadMask = new Ext.LoadMask(Ext.getBody(), {msg:"Loading parameters..."});
	}
	loadMask.show();
	tcga.db2.build.createPatientClassSelectors(resultsDisplay);
	loadMask.hide();
}

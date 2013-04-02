Ext.namespace('tcga.db2.start');

tcga.db2.start.createPageComponents = function() {
	var diseaseStore = new Ext.data.JsonStore({
//		url:'json/diseases.sjson',
		storeId:'diseases',
		root:'diseases',
		idProperty:'tumorId',
		autoLoad: true,
		fields: [
			'tumorId',
			'tumorName',
			'tumorDescription'
		],
		data: tcga.db2.localData.diseases
	});
	
	var diseaseCombo = new tcga.extensions.comboPlus({
		id: 'diseaseCombo',
		renderTo: 'diseaseList',
		store: diseaseStore,
		mode: 'local',
		triggerAction: 'all',
		displayField:'tumorDescription',
		valueField : 'tumorName',
		emptyText: 'Select a disease',
		tplDisplay: '{tumorDescription} ({tumorName})',
		border: false,
		autoHeight: true,
		style: 'margin-bottom: 10px;',
		width: 270,
		listeners: {
			select: function(combo, rec) {
				var newQuery = new tcga.db2.query.query();
				newQuery.update('disease', {
					name: rec.get('tumorName'),
					description: rec.get('tumorDescription')
				});
				
				tcga.db2.query.storage.reset();
				tcga.db2.query.storage.addQuery(newQuery);
			}
		}
	});
	
	var geneTextField = new Ext.form.TextField({
		id: 'geneTextField',
		renderTo: 'geneSelection',
		emptyText: 'Enter a gene',
		width: 100,
		style: 'margin-left: 24px;'
	});
	
	var pathwayStore = new Ext.data.JsonStore({
//		url:'json/pathways.sjson',
		storeId:'pathways',
		root:'pathways',
		idProperty:'name',
		autoLoad: true,
		fields: [
			'name'
		],
		data: tcga.db2.localData.pathways
	});
	
	var pathwayCombo = new tcga.extensions.comboPlus({
		id: 'pathwayCombo',
		renderTo: 'pathwaySelection',
		store: pathwayStore,
		mode: 'local',
		hideTrigger: true,
		displayField:'name',
		valueField : 'name',
		emptyText: 'Enter a pathway',
		border: false,
		autoHeight: true,
		style: 'margin-bottom: 10px;',
		width: 400
	});

	var savedPatientListStore = new Ext.data.JsonStore({
//		url:'json/savedPatientLists.sjson',
		storeId:'savedPatientListStore',
		root:'lists',
		idProperty:'id',
		autoLoad: true,
		fields: [
			'id',
			'name'
		],
		data: tcga.db2.localData.savedPatientLists
	});
	
	var listComboPatients = new tcga.extensions.comboPlus({
		id: 'listComboPatients',
		renderTo: 'selectPatientList',
		store: savedPatientListStore,
		mode: 'local',
		triggerAction: 'all',
		displayField:'name',
		valueField : 'id',
		emptyText: 'Select a patient list',
		border: false,
		autoHeight: true,
		style: 'margin-bottom: 10px;',
		width: 270
	});

	new Ext.form.TextArea({
		id: 'enteredPatientsList',
		renderTo: 'enterPatientList',
		width: 500,
		height: 60,
		style: 'margin-bottom: 10px;',
		emptyText: 'Enter patient IDs in comma, space or line delimited format'
	});
}

tcga.db2.start.goToBuildQuery = function(){
	// Check that a disease has been selected
	var disease = Ext.getCmp('diseaseCombo').getValue();
	if (!disease || disease == '') {
		Ext.Msg.alert('Please select a disease', 'A disease must be selected to proceed with your query.')
		return;
	}

	// Add the build token to the history
	tcga.db2.history.addToken('build');
}

tcga.db2.start.goToGeneInfoPage = function(){
	var geneValue = tcga.db2.start.geneTextField.getValue();
	var pathwayValue = tcga.db2.start.pathwayCombo.getValue();
	if (geneValue == '' && pathway == '') {
		Ext.Msg.alert('Please enter a value', 'A gene or a pathway must be entered to proceed with your query.')
		return;
	}
	
	var gene = '';
	if (geneValue != '') {
		gene = 'gene=' + geneValue;
		if (pathway != '') {
			gene += '&';
		}
	}
	var pathway = '';
	if (pathway != '') {
		pathway = 'pathway=' + pathwayValue;
	}

	// Should we stay on this page?
	location.href='tcgaDb2GeneInfoPage.htm?' + gene + pathway;
}

tcga.db2.start.gotoPage = function() {
	// Add the build token to the history
	tcga.db2.history.addToken('start');
}

tcga.db2.start.reset = function() {

}

tcga.db2.start.init = function() {
	tcga.db2.queryWriter.reset();
	tcga.db2.selectors.queryType.reset();
	tcga.db2.results.reset();
	
	Ext.getCmp('diseaseCombo').setValue(null);
}

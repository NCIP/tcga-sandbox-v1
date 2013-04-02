/*
 * Initialize the watson app and fire up the start page
 */
Ext.onReady(function() {
	// Init the quicktips
	Ext.QuickTips.init();
	
	// Init the history - no field is created for this in the lib, so we have to create one ourselves
	var historyFieldHtml = '<div id="x-history-field"></div>';
	if (Ext.isIE) {
		// If we're in IE, then we also need an iframe
		historyFieldHtml += '<iframe id="x-history-frame"></iframe>';
	}
	Ext.DomHelper.insertHtml('afterBegin', Ext.getDom('historyField'), historyFieldHtml);
	Ext.History.init(tcga.db2.history.init, tcga.db2.history);
	Ext.History.addListener('change', tcga.db2.history.manageQueryPageChange, tcga.db2.history);

	var store = new Ext.data.JsonStore({
//		url: 'json/patientsResultsI.sjson',
		storeId: 'patientsInteractiveStore',
		root: 'patientsResults',
		idProperty: 'id',
		fields: [
		   'id',
			'gender',
			'ageAtDiagnosis',
			'race',
			'daysSurvival',
			'daysRecurrence',
			'tumorGrade',
			'tumorStage',
			'tumorSite',
			'tissueCollectionCenter'
		],
		data: tcga.db2.localData.patientsResultsI/*, Removed from here as using "data" generates an immediate load.  Need to hold the store load until later.
		listeners: {
			load: loadDataToSelectors
		}*/
	});

	// Create the components on the start page.  They can be created here since they are not
	//		disease dependent.
	tcga.db2.results.createPageComponents();
	
	// No fire up the watson start query page
	tcga.db2.history.addToken('interactivePatient');
	Ext.History.fireEvent('change', 'interactivePatient');
}, this);

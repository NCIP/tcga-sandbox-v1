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

	// Create the components on the start page.  They can be created here since they are not
	//		disease dependent.
	tcga.db2.start.createPageComponents();
	tcga.db2.results.createPageComponents();
	
	// No fire up the watson start query page
	tcga.db2.start.gotoPage();
	Ext.History.fireEvent('change', 'start');
}, this);

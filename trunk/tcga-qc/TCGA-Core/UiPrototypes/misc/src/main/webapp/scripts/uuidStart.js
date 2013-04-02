Ext.namespace('tcga.uuid');

tcga.uuid.titleAdjust = function(titleId) {
	if (titleId) {
		var uuidResultsTitleEl = Ext.get(titleId);
		// Analyte and Slide tables do not need to be adjusted for pixel perfection...
		if (uuidResultsTitleEl.getWidth() > 500) {
			uuidResultsTitleEl.setWidth(uuidResultsTitleEl.getWidth() - 1);
		}
		// ...except in IE7
		else if (Ext.isIE7) {
			uuidResultsTitleEl.setWidth(289);
		}
	}
}

tcga.uuid.browserStart = function(){
	Ext.QuickTips.init();
	
	// Init the history - no field is created for this in the lib, so we have to create one ourselves
	var historyFieldHtml = '<div id="x-history-field"></div>';
	if (Ext.isIE) {
		// If we're in IE, then we also need an iframe
		historyFieldHtml += '<iframe id="x-history-frame"></iframe>';
	}
	Ext.DomHelper.insertHtml('afterBegin', Ext.getDom('historyField'), historyFieldHtml);
	Ext.History.init();
	Ext.History.addListener('change', tcga.uuid.historyManager);
	
	browseStore = new Ext.data.JsonStore({
		storeId: 'uuidSearchResultsStore',
		url : 'json/uuidSearch3.sjson',
		remoteSort : true,
		root : 'uuid',
		totalProperty: 'totalCount',
		fields: [
			'uuid',
			'parent',
			'barcode',
			'uuidType',
			'disease',
			'center',
			'participant',
			'sampleType',
			'vial',
			'portion',
			'analyte',
			'aliquot',
			'slide'
		],
		listeners: {
			load: function(store, recs) {
				if (store.getCount() > 1) {
					Ext.History.add('Search');
				}
				else {
					tcga.uuid.browser.goTo(recs[0]);
				}
			}
		}
	});
	
	tcga.uuid.search.form(browseStore);
	tcga.uuid.search.results.init(browseStore);
	tcga.uuid.navigator.render();
	tcga.uuid.browser.render();
	
	tcga.uuid.search.results.hide();
	tcga.uuid.browser.hide();
}

Ext.onReady(tcga.uuid.browserStart, this);

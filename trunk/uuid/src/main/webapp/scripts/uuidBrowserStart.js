/*******************************************************************************
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 ******************************************************************************/

Ext.namespace('tcga.uuid');

tcga.uuid.titleAdjust = function(titleId) {
	if (titleId) {
		var uuidResultsTitleEl = Ext.get(titleId);
		// Analyte and Slide tables do not need to be adjusted for pixel perfection...
		if (uuidResultsTitleEl.getWidth() > 500) {
			uuidResultsTitleEl.setWidth(676);
		}
		// ...except in IE7
		else if (Ext.isIE7) {
			uuidResultsTitleEl.setWidth(289);
		}
	}
}

tcga.uuid.browserStart = function(){
	Ext.QuickTips.init();
	Ext.Ajax.timeout = 120000;
	var browseStore = new Ext.data.JsonStore({
		storeId: 'uuidSearchResultsStore',
		url : 'uuidBrowser.json',
		remoteSort : true,
        baseParams:{start:0, limit:50},
		root : 'uuidBrowserData',
		totalProperty: 'totalCount',
		fields: [
            'uuid',
            'parentUUID',
            'barcode',
            'uuidType',
            'disease',
            'participantId',
            'sampleType',
            'vialId',
            'plateId',
            'portionId',
            'analyteType',
            'aliquot',
            'slide',
            'tissueSourceSite',
            'receivingCenter',
            'updateDate',
            'createDate',
            'platform',
            'bcr',
            'batch'
        ]
	});

	tcga.uuid.search.form(browseStore);
	tcga.uuid.search.results.init(browseStore);
}

Ext.onReady(tcga.uuid.browserStart, this);

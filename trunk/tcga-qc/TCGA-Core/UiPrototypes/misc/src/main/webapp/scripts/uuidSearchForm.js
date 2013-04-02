Ext.namespace('tcga.uuid.search');

tcga.uuid.search.form = function(browseStore){
	var uuidSearchForm = new Ext.form.FormPanel({
		id: 'uuidSearchForm',
		border: false,
		layout: 'column',
		style: 'margin: 10px 0 0 180px;',
		width: 400,
		items: [{
			cls: 'stdLabel',
			border: false,
			width: 100,
			html: 'UUID:'
		}, {
			id: 'uuidField',
			xtype: 'textfield',
			width: 300,
			emptyText: 'Enter a UUID',
			regex: new RegExp('^([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})?$'),
			regexText: 'Error: Not a valid UUID'
		}, {
			cls: 'stdLabel',
			style: 'margin-top: 5px;',
			border: false,
			width: 100,
			html: 'Barcode:'
		}, {
			id: 'barcodeField',
			xtype: 'textfield',
			style: 'margin-top: 5px;',
			width: 300,
			emptyText: 'Enter a barcode',
			regex: new RegExp('^((TCGA)-([A-Z0-9]{2})-([A-Z0-9]{4})-((\d{2})([A-Z]{1}))-((\d{2})([A-Z]{1}))-([A-Z0-9]{4})-(\d{2}))?$'),
			regexText: 'Error: Not a valid barcode'
		}, {
			border: false,
			height: 5,
			width: 400,
			html: '&nbsp;'
		}, {
			cls: 'stdLabel',
			border: false,
			width: 100,
			html: 'File:'
		}, {
			xtype: 'fileuploadfield',
			layout: 'form',
			emptyText: 'Select a File to import',
			id: 'form-file',
			name:'file',
			width: 300,
			allowBlank : false,
			blankText : 'This field is required.',
			validateOnBlur: false
		}]
	});

	
	var searchPanel = new Ext.Panel({
		id: 'uuidSearchPanel',
		title: 'UUID/Barcode',
		style: 'margin-top: 5px;',
		bodyStyle: 'width: 848px;',
		border: true,
		layout: 'column',
		height: 105,
		items: [
			uuidSearchForm,
		{
			xtype: 'buttonplus',
			id: 'searchButton',
			width: 76,
			height: 76,
			style: 'margin: 10px 0 0 15px;',
			text: '<span class="stdLabel">Search!<span>',
			handler: function() {
				var uuidFieldVal = Ext.getCmp('uuidField').getValue();
				if (uuidFieldVal != null && uuidFieldVal == 1) {
					browseStore.proxy.setUrl('json/uuidSearch1.sjson');
				}
				browseStore.load();
			}	
		}]
	});
	
	new tcga.extensions.TabPanel({
		id: 'uuidBrowserTabPanel',
		renderTo: 'uuidSearch',
		border: false,
//		height: 340,
//		height: 'auto',
//		autoHeight: (Ext.isIE7?false:true),
		autoHeight: true,
		hideBorders: true,
		activeTab: 0,
//		layoutOnTabChange: true,
		items: [
            searchPanel,
            tcga.uuid.search.metadata()
		],
		listeners: {
			tabchange: function(tabPanel, newActiveTab) {
				if (newActiveTab != null) {
					if (newActiveTab.title == 'Metadata') {
						newActiveTab.setHeight(165);
						tabPanel.setHeight(210);
						Ext.get('uuidSearch').setHeight(210);
					}
					else {
						newActiveTab.setHeight(105);
						tabPanel.setHeight(150);
						Ext.get('uuidSearch').setHeight(150);
					}
				}
			}
		}
	});
}

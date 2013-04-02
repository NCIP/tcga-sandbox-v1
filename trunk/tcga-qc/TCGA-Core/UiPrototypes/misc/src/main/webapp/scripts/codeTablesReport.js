/*******************************************************************************
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 ******************************************************************************/

Ext.namespace('tcga.datareports.codetables');

//Create export url
tcga.datareports.codetables.exportUrl = function(grid,code,type){
	var dir,sort;
	var sortState = grid.store.getSortState();

	if(sortState) {
		dir = sortState.direction;
		sort = sortState.field;
	}
	ahref("codeTablesExport.htm?codeTablesReport="+code+"&exportType="+type+
		"&dir="+dir+"&sort="+sort);
};

//Create table selection Toolbar
tcga.datareports.codetables.tableSelection = function(){
	return {
		xtype: 'combo',
		id: 'selectReport',
		width: 150,
		store: new Ext.data.ArrayStore({
		   fields: ['id', 'fcn'],
		   data: [
				['BCR Batch', tcga.datareports.codetables.bcrBatchCodePanel],
				['Center', tcga.datareports.codetables.centerCodePanel],
				['Data Level', tcga.datareports.codetables.dataLevelCodePanel],
				['Data Type', tcga.datareports.codetables.dataTypeCodePanel],
				['Disease Study', tcga.datareports.codetables.diseaseStudyCodePanel],
				['Platform', tcga.datareports.codetables.platformCodePanel],
				['Portion Analyte', tcga.datareports.codetables.portionAnalyteCodePanel],
				['Sample Type', tcga.datareports.codetables.sampleTypeCodePanel],
				['Tissue', tcga.datareports.codetables.tissueCodePanel],
				['Tissue Source Site', tcga.datareports.codetables.tissueSourceSiteCodePanel]
			]
		}),
		mode: 'local',
		listWidth: 150,
		triggerAction: 'all',
		displayField: 'id',
		valueField: 'id',
		editable: false,
		forceSelection: true,
		listeners: {
			select: function(combo, rec, ndx) {
				var reportContainer = Ext.getCmp('codeReportTableContainer');
				reportContainer.removeAll();
				reportContainer.add(rec.get('fcn')());
				reportContainer.doLayout();
				Ext.getCmp('selectReport').setValue(rec.get('id'));
			}
		}
	};
};

//Create exports Toolbar
tcga.datareports.codetables.exports = function(grid,code){
	return {
		menu: [{
			text: 'Excel',
			iconCls: 'icon-xl',
			handler: function() {
				exportUrl(grid,code,"xl");
			}
		}, {
			text: 'CSV',
			iconCls: 'icon-txt',
			handler: function() {
				exportUrl(grid,code,"csv");
			}
		}, {
			text: 'Tab-delimited',
			iconCls: 'icon-txt',
			handler: function() {
				exportUrl(grid,code,"tab");
			}
		}],
		text: 'Export Data',
		iconCls: 'icon-grid'
	};
};

tcga.datareports.codetables.pagingToolbar = function(config) {
	//Create comboBox for page selection
	var combo = new Ext.form.ComboBox({
		name: 'perpage',
		width: 50,
		store: new Ext.data.ArrayStore({
		   fields: ['id'],
		   data: [['25'],['50'],['100']]
		}),
		mode: 'local',
		value: '25',
		listWidth: 40,
		triggerAction: 'all',
		displayField: 'id',
		valueField: 'id',
		editable: false,
		forceSelection: true
	});

	var paging = new Ext.PagingToolbar({
		pageSize: 25,
		store: config.store,
		displayInfo: true,
		displayMsg: config.displayMsg,
		emptyMsg: config.emptyMsg,
		items: ['-','Per Page ',combo]
	});

	combo.on('select', function(combo, record) {
		paging.pageSize = parseInt(record.get('id'), 10);
		paging.doLoad(paging.cursor);
	}, this);
	
	return paging;
};

tcga.datareports.codetables.codeReportPanel = function(config) {
	var codeReportStore = new Ext.data.JsonStore({
//     url: "codeTablesReport.json?codeTablesReport=" + config.code,
		url: 'json/' + config.code + '.sjson',
		remoteSort: true,
		autoLoad: {params:{start:0, limit:25}},
		root : config.storeRoot,
		totalProperty: 'totalCount',
		fields: config.storeFields
	});

	if (config.pagingToolbar != false) {
		var paging = tcga.datareports.codetables.pagingToolbar({
			store: codeReportStore,
			displayMsg: config.displayMsg + ' {0} - {1} of {2}',
			emptyMsg: config.emptyMsg,
		});
	}

   //Create the Grid
	var codeReportGrid = new Ext.grid.GridPanel({
		title: config.title,
		style: 'margin: auto;padding: 10px 0 10px 0;',
		store: codeReportStore,
		enableColumnHide: false,
		enableColumnMove: false,
		columns: config.columns,
		stripeRows: true,
		forceFit: true,
		height: 480,
		width: 730,
		frame: true,
		loadMask: true,
		bbar: (config.pagingToolbar != false)?paging:null,
		tbar: new Ext.Toolbar()
	});

	var topToolbar = codeReportGrid.getTopToolbar();
	topToolbar.add(tcga.datareports.codetables.tableSelection());
	topToolbar.doLayout();
	topToolbar.add(tcga.datareports.codetables.exports(codeReportGrid, config.code));
	topToolbar.doLayout();

	return codeReportGrid;
}
	 
tcga.datareports.codetables.bcrBatchCodePanel = function(config) {
	return tcga.datareports.codetables.codeReportPanel({
		title: 'BCR Batch',
		code: 'bcrBatchCode',
		storeRoot:'bcrBatchCodeData',
		storeFields: [
			'bcrBatch',
			'studyCode',
			'studyName',
			'bcr'
		],
		displayMsg: 'Displaying BCR Batch',
		emptyMsg: 'No BCR Batch to display',
		columns: [{
			header: 'BCR Batch',
			width: 100,
			sortable: true,
			dataIndex: 'bcrBatch',
			tooltip: 'BCR Batch'
		}, {
			header: 'Study Abbr.',
			width: 100,
			sortable: true,
			dataIndex: 'studyCode',
			tooltip: 'Study Abbreviation'
		}, {
			header: 'Study Name',
			width: 300,
			sortable: true,
			dataIndex: 'studyName',
			tooltip: 'Study Name'
		}, {
			header: 'BCR',
			width: 100,
			sortable: true,
			dataIndex: 'bcr',
			tooltip: 'BCR Short Name'
		}]
	});
};

tcga.datareports.codetables.centerCodePanel = function(config) {
	return tcga.datareports.codetables.codeReportPanel({
		title: 'Center',
		code: 'centerCode',
		storeRoot:'centerCodeData',
		storeFields: [
			'code',
			'centerName',
			'centerType',
			'centerDisplayName',
			'shortName'
	   ],
		displayMsg: 'Displaying centers',
		emptyMsg: 'No centers to display',
		columns: [
		{header: 'Code', width: 50, sortable: true, dataIndex: 'code', tooltip: 'Code'},
		{header: 'Center Name', width: 150, sortable: true, dataIndex: 'centerName', tooltip: 'Center Name'},
		{header: 'Center Type', width: 100, sortable: true, dataIndex: 'centerType', tooltip: 'Center Type'},
		{header: 'Display Name', width: 250, sortable: true, dataIndex: 'centerDisplayName',
			tooltip: 'Center Display Name'},
		{header: 'Short Name', width: 100, sortable: true, dataIndex: 'shortName', tooltip: 'Center Short Name'}
	   ]
	});
};

tcga.datareports.codetables.dataLevelCodePanel = function(config) {
	return tcga.datareports.codetables.codeReportPanel({
		title: 'Data Level',
		code: 'dataLevel',
		pagingToolbar: false,
		storeRoot:'dataLevelData',
		storeFields: [
			'code',
			'definition'
		],
		columns: [
		  {header: 'Level Number', width: 100, sortable: true, dataIndex: 'code', tooltip: 'Code'},
		  {header: 'Definition', width: 300, sortable: true, dataIndex: 'definition', tooltip: 'Definition'}
	   ]
	});
};

tcga.datareports.codetables.dataTypeCodePanel = function(config) {
	return tcga.datareports.codetables.codeReportPanel({
		title: 'Data Type',
		code: 'dataType',
		storeRoot:'dataTypeData',
		storeFields: [
			'centerType',
			'displayName',
			'ftpDisplay',    
			'available'
	   ],
		displayMsg: 'Displaying data types',
		emptyMsg: 'No data types to display',
		columns: [
			{header: 'Center Type', width: 100, sortable: true, dataIndex: 'centerType', tooltip: 'Center Type'},
			{header: 'Display Name', width: 250, sortable: true, dataIndex: 'displayName', tooltip: 'Display Name'},
			{header: 'FTP Display', width: 100, sortable: true, dataIndex: 'ftpDisplay', tooltip: 'FTP Display'},
			{header: 'Available', width: 100, sortable: true, dataIndex: 'available', tooltip: 'Available'}
	   ]
	});
};

tcga.datareports.codetables.diseaseStudyCodePanel = function(config) {
	return tcga.datareports.codetables.codeReportPanel({
		title: 'Disease Study',
		code: 'diseaseStudy',
		storeRoot:'diseaseStudyData',
		storeFields: [
			'tumorName',
			'tumorDescription'
	   ],
		displayMsg: 'Displaying studies',
		emptyMsg: 'No studies to display',
		columns: [
		{header: 'Study Abbreviation', width: 200, sortable: true, dataIndex: 'tumorName',
			tooltip: 'Study Abbreviation'},
		{header: 'Study Name', width: 400, sortable: true, dataIndex: 'tumorDescription',
			tooltip: 'Study Name'}
	   ]
	});
};

tcga.datareports.codetables.platformCodePanel = function(config) {
	return tcga.datareports.codetables.codeReportPanel({
		title: 'Platform',
		code: 'platformCode',
		storeRoot:'platformCodeData',
		storeFields: [
			'platformName',
			'platformAlias',
			'displayName',
			'available'
	   ],
		displayMsg: 'Displaying platforms',
		emptyMsg: 'No platforms to display',
		columns: [
		{header: 'Platform Code', width: 200, sortable: true, dataIndex: 'platformName',
			tooltip: 'Platform Code'},
		{header: 'Platform Alias', width: 130, sortable: true, dataIndex: 'platformAlias',
			tooltip: 'Platform Alias'},
		{header: 'Platform Name', width: 300, sortable: true, dataIndex: 'displayName',
			tooltip: 'Platform Name'},
		{header: 'Available', width: 60, sortable: true, dataIndex: 'available', tooltip: 'Available'}
	   ]
	});
};

tcga.datareports.codetables.portionAnalyteCodePanel = function(config) {
	return tcga.datareports.codetables.codeReportPanel({
		title: 'Portion Analyte',
		code: 'portionAnalyte',
		pagingToolbar: false,
		storeRoot:'portionAnalyteData',
		storeFields: [
			'code',
			'definition'
		],
		columns: [
		{header: 'Code', width: 100, sortable: true, dataIndex: 'code', tooltip: 'Code'},
		{header: 'Definition', width: 350, sortable: true, dataIndex: 'definition', tooltip: 'Definition'}
	   ]
	});
};

tcga.datareports.codetables.sampleTypeCodePanel = function(config) {
	return tcga.datareports.codetables.codeReportPanel({
		title: 'Sample Type',
		code: 'sampleType',
		pagingToolbar: false,
		storeRoot:'sampleTypeData',
		storeFields: [
			'code',
			'definition'
		],
		columns: [
		{header: 'Code', width: 100, sortable: true, dataIndex: 'code', tooltip: 'Code'},
		{header: 'Definition', width: 350, sortable: true, dataIndex: 'definition', tooltip: 'Definition'}
	   ]
	});
};

tcga.datareports.codetables.tissueCodePanel = function(config) {
	return tcga.datareports.codetables.codeReportPanel({
		title: 'Tissue',
		code: 'tissue',
		pagingToolbar: false,
		storeRoot:'tissueData',
		storeFields: [
			'tissue'
		],
		columns: [
	    {header: 'Tissue', width: 300, sortable: true, dataIndex: 'tissue', tooltip: 'Tissue'},
	   ]
	});
};

tcga.datareports.codetables.tissueSourceSiteCodePanel = function(config) {
	return tcga.datareports.codetables.codeReportPanel({
		title: 'Tissue Source Site',
		code: 'tissueSourceSite',
		storeRoot:'tissueSourceSiteData',
		storeFields: [
			'code',
			'definition',
			'tissue',
			'studyCode',
			'studyName'
	   ],
		displayMsg: 'Displaying TSS',
		emptyMsg: 'No TSS to display',
		columns: [
		{header: 'TSS Code', width: 60, sortable: true, dataIndex: 'code', tooltip: 'Tissue Source Site Code'},
		{header: 'Source Site', width: 280, sortable: true, dataIndex: 'definition', tooltip: 'Source Site'},
		{header: 'Tissue', width: 60, sortable: true, dataIndex: 'tissue', tooltip: 'Tissue'},
		{header: 'Study Abbr.', width: 65, sortable: true, dataIndex: 'studyCode', tooltip: 'Study Abbreviation'},
		{header: 'Study Name', width: 200, sortable: true, dataIndex: 'studyName', tooltip: 'Study Name'}
	   ]
	});
};

tcga.datareports.codetables.initH = function(activeReport) {
	Ext.QuickTips.init();
	
	new tcga.extensions.TabPanel({
		id: 'codeReportsTabPanel',
		renderTo: 'codeTablesDiv',
		border: false,
		autoHeight: true,
		hideBorders: true,
		tabCls: 'tcga-ext-tabCTH',
		activeTab: 0,
		items: [
			tcga.datareports.codetables.bcrBatchCodePanel(),
			tcga.datareports.codetables.centerCodePanel(),
			tcga.datareports.codetables.dataLevelCodePanel(),
			tcga.datareports.codetables.dataTypeCodePanel(),
			tcga.datareports.codetables.diseaseStudyCodePanel(),
			tcga.datareports.codetables.platformCodePanel(),
			tcga.datareports.codetables.portionAnalyteCodePanel(),
			tcga.datareports.codetables.sampleTypeCodePanel(),
			tcga.datareports.codetables.tissueCodePanel(),
			tcga.datareports.codetables.tissueSourceSiteCodePanel()
		]
	});
};

tcga.datareports.codetables.initV = function(activeReport) {
	Ext.QuickTips.init();
	
	tcga.datareports.codetables.codeReportPanelBorder = false;
	tcga.datareports.codetables.codeReportPanelWidth = 837;
	
	var activeTab = 0;
	
	var activateTab = function(panel, whichTab) {
		var selectorPanel = Ext.getCmp('selectorPanel');
		var tabPanel = Ext.getCmp('verticalTabPanel');
		var clickPanelEl = panel.getEl();
		
		selectorPanel.items.each(function(panel) {
			var spCpEl = panel.getEl();
			spCpEl.applyStyles('background-color: #ececec;');
			spCpEl.child('div.x-panel-body').applyStyles('background-color: #ececec;');
		});

		clickPanelEl.applyStyles('background-color: #ffffff;');
		clickPanelEl.child('div.x-panel-body').applyStyles('background-color: #ffffff;');
		tabPanel.setActiveTab(whichTab);
	};
	
	var createClickPanel = function(title, which, active) {
		var backgroundColor = '#ececec';
		if (active) {
			backgroundColor = '#ffffff';
		}
		
		return {
			xtype: 'clickpanel',
			handler: function() {activateTab(this, which)},
			border: false,
			style: 'cursor: pointer;padding: 5px;border-bottom: solid 1px blue;background-color: ' + backgroundColor + ';',
			bodyStyle: 'background-color: ' + backgroundColor + ';',
			html: title
		};
	};
	
	new Ext.Panel({
		renderTo: 'codeTablesDiv',
		height: 480,
		layout: 'column',
		items: [{
			id: 'selectorPanel',
			width: 100,
			border: false,
			style: 'border-right: solid 1px blue;height: 100%;',
			items: [
				createClickPanel('BCR Batch', 0, true),
				createClickPanel('Center', 1),
				createClickPanel('Data Level', 2),
				createClickPanel('Data Type', 3),
				createClickPanel('Disease Study', 4),
				createClickPanel('Platform', 5),
				createClickPanel('Portion Analyte', 6),
				createClickPanel('Sample Type', 7),
				createClickPanel('Tissue', 8),
				createClickPanel('Tissue Source Site', 9)
			]
		}, {
			id: 'verticalTabPanel',
			xtype: 'conflooktabpanel',
			width: 800,
			border: false,
			hideTabs: true,
			tabCls: 'tcga-ext-tabCTH',
			activeTab: 0,
			items: [
				tcga.datareports.codetables.bcrBatchCodePanel(),
				tcga.datareports.codetables.centerCodePanel(),
				tcga.datareports.codetables.dataLevelCodePanel(),
				tcga.datareports.codetables.dataTypeCodePanel(),
				tcga.datareports.codetables.diseaseStudyCodePanel(),
				tcga.datareports.codetables.platformCodePanel(),
				tcga.datareports.codetables.portionAnalyteCodePanel(),
				tcga.datareports.codetables.sampleTypeCodePanel(),
				tcga.datareports.codetables.tissueCodePanel(),
				tcga.datareports.codetables.tissueSourceSiteCodePanel()
			]
		}]
	});
};

tcga.datareports.codetables.initD = function(activeReport) {
	Ext.QuickTips.init();
	
	new Ext.Panel({
		id: 'codeReportTableContainer',
		renderTo: 'codeTablesDiv',
		border: false,
		width: 900, 
		autoHeight: true,
		items: [
			tcga.datareports.codetables.bcrBatchCodePanel()
		]
	});
	
	Ext.getCmp('selectReport').setValue('BCR Batch');
};


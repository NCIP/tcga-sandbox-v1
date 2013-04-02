Ext.namespace('tcga.db2.results');

tcga.db2.results.createCommonPageComponents = function(pageId, store) {
	return {
		exportToolbar: new Ext.Toolbar({
			id: pageId + 'exportToolbar',
			width: 800,
			items: [{
				menu: [{
					text: 'Excel',
					iconCls: 'icon-xl',
					handler: function(){
						exportUrl("xl", exportDataType);
					}
				}, {
					text: 'CSV',
					iconCls: 'icon-txt',
					handler: function(){
						exportUrl("csv", exportDataType);
					}
				}, {
					text: 'Tab-delimited',
					iconCls: 'icon-txt',
					handler: function(){
						exportUrl("tab", exportDataType);
					}
				}],
	             id: 'exportDataMenu',
				text: 'Export Data',
				iconCls: 'icon-grid'
			}]
		}),

		//Create export url
		exportUrl: function(type, dataToBeExported) {
			window.location = "uuidExport.htm?exportType="+type+"&dataToBeExported="+dataToBeExported;
		},
	
		// paging bar
		pagingToolbar: new Ext.PagingToolbar({
			id: 'resultsPagingTb',
			pageSize: 50,
			width: 800,
			store: store,
			displayInfo: true,
			displayMsg: 'Displaying gene {0} - {1} of {2}',
			emptyMsg: "No results to display",
			items: ['-','Per Page ',new Ext.form.ComboBox({
		       name: pageId + 'perpage',
		       width: 50,
		       store: new Ext.data.ArrayStore({
		       fields: ['id'],
		       data: tcga.db2.localData.paging}),
		       mode: 'local',
		       value: '50',
		       listWidth: 40,
		       triggerAction: 'all',
		       displayField: 'id',
		       valueField: 'id',
		       editable: false,
		       forceSelection: true
		   })],
			listeners: {
				// This is here purely for the tcga.db2.localData version, delete otherwise
				beforechange: function() {
					return false;
				}
			}
		}),
		
		cbSelModel: new Ext.grid.CheckboxSelectionModel({checkOnly: true})
	}
}

tcga.db2.results.createPatientsTab = function() {
	var patientsResultsStore = new Ext.data.JsonStore({
//		url: 'json/patientsResults.sjson',
		storeId: 'patientsResults',
		root: 'patientsResults',
		idProperty: 'id',
		totalProperty: 'totalCount',
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
			'tissueCollectionCenter',
		],
		data: tcga.db2.localData.patientsResults
	});
	
	var cpc = new tcga.db2.results.createCommonPageComponents('patients', patientsResultsStore);

   var patientsResultsGrid = new Ext.grid.GridPanel({
      id: 'patientsResultsGrid',
      loadMask: true,
      stripeRows: true,
      height:460,
      frame:true,
		sm: cpc.cbSelModel,
      bbar: cpc.pagingToolbar,
      tbar: cpc.exportToolbar,
      store: patientsResultsStore,
      enableColumnHide: false,
      columns: [
			cpc.cbSelModel,
		{
			id:'id',
			header: 'Patient Id',
			width: 90,
			sortable: true,
			dataIndex: 'id'
		}, {
			id: 'gender',
			header: 'Gender',
			width: 70,
			sortable: true,
			dataIndex: 'gender',
			align: 'center'
		}, {
			id: 'race',
			header: 'Race',
			width: 80,
			sortable: true,
			dataIndex: 'race',
			align: 'center'
		}, {
			id: 'ageAtDiagnosis',
			header: 'Age at Diagnosis',
			width: 80,
			sortable: true,
			dataIndex: 'ageAtDiagnosis',
			align: 'center'
		}, {
			id: 'daysSurvival',
			header: 'Days Survival since Diagnosis',
			width: 100,
			sortable: true,
			dataIndex: 'daysSurvival',
			align: 'center'
		}, {
			id: 'daysRecurrence',
			header: 'Days Recurrence',
			width: 70,
			sortable: true,
			dataIndex: 'daysRecurrence',
			align: 'center'
		}, {
			id: 'tumorGrade',
			header: 'Tumor Grade',
			width: 70,
			sortable: true,
			dataIndex: 'tumorGrade',
			align: 'center',
			hidden: true
		}, {
			id: 'tumorStage',
			header: 'Tumor Stage',
			width: 70,
			sortable: true,
			dataIndex: 'tumorStage',
			align: 'center',
			hidden: true
		}, {
			id: 'tumorSite',
			header: 'Tumor Site',
			width: 70,
			sortable: true,
			dataIndex: 'tumorSite',
			align: 'center',
			hidden: true
		}, {
			id: 'tissueCollectionCenter',
			header: 'Tissue Collection Center',
			width: 70,
			sortable: true,
			dataIndex: 'tissueCollectionCenter',
			align: 'center',
			hidden: true
		}]
   });
	
	var patientsGraphView = new Ext.Panel({
		id: 'patientsGraphView',
		border: false,
		cls: 'queryResultsImageView',
		height: 460,
		width: 820,
		hidden: true,
		html: '<img src="images/copyPatientsGraph.png">'
	});

	return new Ext.Panel({
		id: 'patientsTab',
      title: 'patients',
		border: false,
		items: [{
			id: 'patientsResultsControlPanel',
			border: false,
			layout: 'column',
			height: 30,
			cls: 'queryResultsViewControlPanel',
			items: [{
				border: false,
				style: 'margin-left: 10px;',
				html: '<div onclick="" class="button blueButtonFill">Save Selected</div>'
			}, {
				border: false,
				style: 'margin: 5px 10px 0 15px;',
				html: 'Show data:'
			}, {
				xtype: 'radiogroup',
				style: 'margin-top: 5px;',
				width: 200,
				columns: 2,
				defaultType: 'radio',
				items: [{
					width: 70,
					name: 'show',
					value: 'selected',
					boxLabel: 'selected',
					checked: true
				}, {
					width: 70,
					name: 'show',
					value: 'all',
					boxLabel: 'all'
				}],
				listeners: {
					change: function(cbgroup, checkedBox) {
						if (checkedBox.value == 'selected') {
							var cm = Ext.getCmp('patientsResultsGrid').getColumnModel();
							cm.setHidden(7, true);
							cm.setHidden(8, true);
							cm.setHidden(9, true);
							cm.setHidden(10, true);
						}
						else {
							var cm = Ext.getCmp('patientsResultsGrid').getColumnModel();
							cm.setHidden(7, false);
							cm.setHidden(8, false);
							cm.setHidden(9, false);
							cm.setHidden(10, false);
						}
					}
				}
			}, {
				border: false,
				style: 'margin: 5px 10px 0 15px;',
				html: 'View data in a(n):'
			}, {
				xtype: 'radiogroup',
				style: 'margin-top: 5px;',
				width: 200,
				columns: 2,
				defaultType: 'radio',
				items: [{
					width: 70,
					name: 'view',
					value: 'table',
					boxLabel: 'table',
					checked: true
				}, {
					width: 70,
					name: 'view',
					value: 'image',
					boxLabel: 'image'
				}],
				listeners: {
					change: function(cbgroup, checkedBox) {
						if (checkedBox.value == 'table') {
							Ext.getCmp('patientsGraphView').hide();
							Ext.getCmp('patientsResultsGrid').show();
						}
						else {
							Ext.getCmp('patientsGraphView').show();
							Ext.getCmp('patientsResultsGrid').hide();
						}
					}
				}
			}]
		},
			patientsResultsGrid,
			patientsGraphView
	]});
}

tcga.db2.results.createGeneTab = function() {
	var geneResultsStore = new Ext.data.JsonStore({
//		url: 'json/geneResults.sjson',
		storeId: 'geneResultsStore',
		root: 'geneResults',
		idProperty: 'gene',
		totalProperty: 'totalCount',
		fields: [
			'gene',
			'geneExpression',
			'copyNumber',
			'cnv',
			'chromosome',
			'location'
		],
		data: tcga.db2.localData.geneResults
	});
	
	var cpc = new tcga.db2.results.createCommonPageComponents('gene', geneResultsStore);

   var geneResultsGrid = new Ext.grid.GridPanel({
      id: 'geneResultsGrid',
      loadMask: true,
      stripeRows: true,
      height:460,
      frame:true,
		sm: cpc.cbSelModel,
      bbar: cpc.pagingToolbar,
      tbar: cpc.exportToolbar,
      store: geneResultsStore,
      enableColumnHide: false,
      columns: [
			cpc.cbSelModel,
		{
			id:'uuid',
			header: 'Gene',
			width: 120,
			sortable: true,
			dataIndex: 'gene'
		}, {
			header: 'Copy Number Results',
			width: 130,
			sortable: true,
			dataIndex: 'copyNumber',
			align: 'center'
		}, {
			header: 'Gene Expression',
			width: 130,
			sortable: true,
			dataIndex: 'geneExpression',
			align: 'center'
		}, {
			header: 'Copy Number Variation',
			width: 140,
			sortable: true,
			dataIndex: 'cnv',
			align: 'center',
			renderer: function(value) {
				return (value == 'true'?value:'&nbsp;');
			}
		}, {
			id: 'location',
			header: 'Gene Location',
			width: 240,
			sortable: true,
			dataIndex: 'chromosome',
			renderer: function(value, meta, rec) {
				return 'Chr ' + value + ': ' + rec.get('location').start + ' - ' + rec.get('location').end;
			}
		}]
   });
	
	var geneGraphView = new Ext.Panel({
		id: 'geneGraphView',
		border: false,
		cls: 'queryResultsImageView',
		height: 460,
		width: 820,
		hidden: true,
		html: '<img src="images/copyGeneGraph.png">'
	});

	return new Ext.Panel({
		id: 'geneTab',
      title: 'Genes',
		border: false,
		items: [{
			id: 'geneResultsControlPanel',
			border: false,
			layout: 'column',
			height: 30,
			cls: 'queryResultsViewControlPanel',
			items: [{
				border: false,
				style: 'margin-left: 10px;',
				html: '<div onclick="" class="button blueButtonFill">Save Selected</div>'
			}, {
				border: false,
				style: 'margin: 5px 10px 0 15px;',
				html: 'Show data as:'
			}, {
				xtype: 'radiogroup',
				style: 'margin-top: 5px;',
				width: 200,
				columns: 2,
				defaultType: 'radio',
				items: [{
					width: 100,
					name: 'dataDisplay',
					value: 'percentages',
					boxLabel: 'percentages',
					checked: true
				}, {
					width: 100,
					name: 'dataDisplay',
					value: 'ratios',
					boxLabel: 'ratios'
				}],
				listeners: {
					change: function(cbgroup, checkedBox) {
						
					}
				}
			}, {
				border: false,
				style: 'margin: 5px 10px 0 0;',
				html: 'View data in a(n):'
			}, {
				xtype: 'radiogroup',
				style: 'margin-top: 5px;',
				width: 200,
				columns: 2,
				defaultType: 'radio',
				items: [{
					width: 70,
					name: 'view',
					value: 'table',
					boxLabel: 'table',
					checked: true
				}, {
					width: 70,
					name: 'view',
					value: 'image',
					boxLabel: 'image'
				}],
				listeners: {
					change: function(cbgroup, checkedBox) {
						if (checkedBox.value == 'table') {
							Ext.getCmp('geneGraphView').hide();
							Ext.getCmp('geneResultsGrid').show();
						}
						else {
							Ext.getCmp('geneGraphView').show();
							Ext.getCmp('geneResultsGrid').hide();
						}
					}
				}
			}]
		},
			geneResultsGrid,
			geneGraphView
	]});
}

tcga.db2.results.createCorrelationsTab = function() {
	return new Ext.Panel({
		id: 'correlationsTab',
		title: 'Correlations',
		border: false,
		cls: 'queryResultsImageView',
		height: 460,
		width: 820,
		html: 'Results here'
	});
}

tcga.db2.results.createPathwaysTab = function() {
	return new Ext.Panel({
		id: 'pathwaysTab',
		title: 'Pathways',
		border: false,
		cls: 'queryResultsImageView',
		height: 460,
		width: 820,
		html: 'Results here'
	});
}

tcga.db2.results.createPageComponents = function() {
	var geneTab = tcga.db2.results.createGeneTab();
	
	var patientsTab = tcga.db2.results.createPatientsTab();

	var correlationsTab = tcga.db2.results.createCorrelationsTab();

	var pathwaysTab = tcga.db2.results.createPathwaysTab();

	new tcga.extensions.TabPanel({
		id: 'resultsTabPanel',
		renderTo: 'resultsData',
		border: false,
		style: 'margin: 10px;',
		autoHeight: true,
		hideBorders: true,
		activeTab: 0,
		listeners: {
			tabchange: function(tabPanel, tab) {
				if (tab) {
					if (tab.id == 'geneTab') {
						Ext.getCmp('geneResultsControlPanel').body.setHeight('auto');
					}
					else if (tab.id == 'patientsTab') {
						Ext.getCmp('patientsResultsControlPanel').body.setHeight('auto');
					}
				}
			}
		}
	});
	
	new Ext.Panel({
		renderTo: 'resultsHeader',
		border: false,
		html: 'Header panel goes here - show query name'
	});
}

tcga.db2.results.rightColumnDisplayToggle = function(flag) {
	var rightColumn = Ext.get('rightColumnQueryDisplay');
	rightColumn.setVisibilityMode(Ext.Element.DISPLAY);
	
	if (flag == 'show') {
		rightColumn.show();
	}
	else if (flag == 'hide') {
		rightColumn.hide();
	}
	else {
		rightColumn.toggle();
	}
}

tcga.db2.results.reset = function() {
	tcga.db2.results.rightColumnDisplayToggle('show');

	var resultsTabPanel = Ext.getCmp('resultsTabPanel');
	resultsTabPanel.removeAll(false);
}

tcga.db2.results.init = function() {
	tcga.db2.results.rightColumnDisplayToggle('hide');
	
	var resultsTabPanel = Ext.getCmp('resultsTabPanel');
	if (tcga.db2.selectors.queryType.find('patient')) {
		resultsTabPanel.add(Ext.getCmp('patientsTab')).show();
	}
	if (tcga.db2.selectors.queryType.find('molecular')) {
//		Unneeded when tcga.db2.localData is being used
//		Ext.StoreMgr.get('geneResults').load();
		resultsTabPanel.add(Ext.getCmp('geneTab')).show();
		resultsTabPanel.add(Ext.getCmp('correlationsTab')).show();
		resultsTabPanel.add(Ext.getCmp('pathwaysTab')).show();
		resultsTabPanel.setActiveTab(0);
	}
}

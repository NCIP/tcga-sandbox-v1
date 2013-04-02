Ext.namespace('tcga.db2.results');

tcga.db2.results.createCommonPageComponents = function(pageId, store) {
	return {
		exportToolbar: new Ext.Toolbar({
			id: pageId + 'exportToolbar',
			width: 'auto',
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
			width: 'auto',
			store: store,
			displayInfo: true,
			displayMsg: 'Displaying patient {0} - {1} of {2}',
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

tcga.db2.results.createPatientsDisplay = function() {
	var patientsResultsStore = Ext.StoreMgr.get('patientsInteractiveStore');
	
	var cpc = new tcga.db2.results.createCommonPageComponents('patients', patientsResultsStore);

   var patientsResultsGrid = new Ext.grid.GridPanel({
      id: 'patientsResultsGrid',
      loadMask: true,
      stripeRows: true,
      height:460,
      frame:true,
		style: 'margin-bottom: 10px;',
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
		renderTo: 'resultsData',
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

tcga.db2.results.createPageComponents = function() {
	tcga.db2.results.createPatientsDisplay();
}

tcga.db2.results.gotoPage = function(parameter) {
	tcga.db2.history.addToken('results');
	
	if (parameter) {
		var selector = Ext.getCmp('resultsDisplay').getSelector(parameter);

		var patientsResultsStore = Ext.StoreMgr.get('patientsInteractiveStore');
		patientsResultsStore.filterBy(selector.filter, selector);
	}
	
	// Because the grid is initially hidden, the toolbars do not show up, so we need to 
	//		adjust their widths
	var grid = Ext.getCmp('patientsResultsGrid');
	grid.getTopToolbar().getEl().setWidth('auto');
	grid.getTopToolbar().getEl().parent().setWidth('auto');
	grid.getBottomToolbar().getEl().setWidth('auto');
	grid.getBottomToolbar().getEl().parent().setWidth('auto');
}

tcga.db2.results.reset = function() {
}

tcga.db2.results.init = function(resultsParam) {
}

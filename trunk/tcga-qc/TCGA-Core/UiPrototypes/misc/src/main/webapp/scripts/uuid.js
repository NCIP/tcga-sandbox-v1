Ext.namespace('tcga.uuid');

tcga.uuid.start = function() {
	Ext.QuickTips.init({
		defaults: {
			anchorOffset: 50,
			defaultAlign: 'r-l?'
		}
	});
	
	/*
	 * Here be those merchants that bringeth forth for our consumptary pleasure the data from those rich
	 * stores of the database that provideth us with the sustenance of data 
	 */
	var centerStore = new Ext.data.JsonStore({
		storeId: 'centersStore',
		url: 'json/centers.sjson',
		root: 'centerData',
		fields: [
			'centerId',
			'centerDisplayName'
		],
		autoLoad: true
	});
	
	var diseaseStore = new Ext.data.JsonStore({
		url:'json/diseases.sjson',
		storeId:'diseases',
		root:'diseases',
		idProperty:'tumorId',
		fields: [
			'tumorId',
			'tumorName',
			'tumorDescription'
		],
		autoLoad: true
	});
	
	var generatedUuidsStore = new Ext.data.JsonStore({
		storeId: 'generatedUuidsStore',
		url: 'json/failure.sjson',
//		url: 'json/generatedUuids.sjson',
		root: 'listOfGeneratedUUIDs',
		fields: [
			'uuid',
			'center',
			'createdBy',
			'creationDate',
			'generationMethod'
		],
		listeners: {
			load: function() {
				var grid = Ext.getCmp('uuidReportResultsGrid');
				var cm = grid.getColumnModel();
				cm.setColumnHeader(0, 'Fred');
				var view = grid.getView();
			},
			exception: function(proxy, type, action) {
				alert('a failure has occurred: ' + 'aa');
			}
		}
	});

	/*
	 * Here liveth functions most grave that speaketh of exporting data 
	 */
	var getToolBar = function(dataToBeExported) {
		return new Ext.Toolbar({
			items: [{
				menu: [{
					text: 'Excel',
					iconCls: 'icon-xl',
					handler: function(){
						exportUrl("xl", dataToBeExported);
					}
				}, {
					text: 'CSV',
					iconCls: 'icon-txt',
					handler: function(){
						exportUrl("csv", dataToBeExported);
					}
				}, {
					text: 'Tab-delimited',
					iconCls: 'icon-txt',
					handler: function(){
						exportUrl("tab", dataToBeExported);
					}
				}],
				text: 'Export Data',
				iconCls: 'icon-grid'
			}]
		});
	};

	//Create export url
	var exportUrl = function(type, dataToBeExported) {
		window.location = "uuidExport.htm?exportType="+type+"&dataToBeExported="+dataToBeExported;
	};

	/*
	 * Here there be dragons imposing their will upon the rendering of text
	 */
	var detailLinkRenderer = function(value, metaData, record, rowIndex, colIndex) {
	    return "<a style='cursor:pointer;' " +
	                    "onClick=\"showUUIDDetails('"+value+"')\">"+value+"</a>";
	}

	var dateRenderer = function(value){
	    var dt = new Date();
	    dt = Date.parseDate(value, "Y-m-d");
	    if (dt){
	       return dt.format("m/d/Y");
	    } else {
	        return "";
	    }
	}

	var centerRenderer = function(value, metaData, record, rowIndex, colIndex) {
	    return value.centerName;
	}

	/*
	 * Here followeth the page interaction functionality
	 */
	var showCorrectUuidField = function(radio, checked) {
		var uuidCountField = Ext.getCmp('uuidCountField');
		var uuidFileUploadField = Ext.getCmp('uuidFileUploadField');

		if (radio == 'generate' && checked) {
			uuidFileUploadField.hide();
			uuidCountField.show();
		}
		else if (radio == 'upload' && checked) {
			uuidCountField.hide();
			uuidFileUploadField.show();
		}
	};

	var loadUuidViewStore = function(type, checked) {
		uuidReportResultsStore.load({params:{start: 0, limit: 25, reportType: 'submittedUUID'}});
	};
	
	var clearViewUuidSelections = function() {
		Ext.getCmp('newUuidRadio').setValue(false);
		Ext.getCmp('submittedUuidRadio').setValue(false);
		Ext.getCmp('missingUuidRadio').setValue(false);
		Ext.StoreMgr.get('uuidReportResultsStore').removeAll();
	};
	
	var clearSearchUuidSelections = function() {
		Ext.getCmp('searchUuidForm').items.each(function(cmp) {
			if (cmp.getXType() === 'panel') {
				cmp.items.each(function(cmp) {
					if (cmp.getId().indexOf('field') !== -1) {
						cmp.reset();
					}
				});
			}
		});
		Ext.StoreMgr.get('uuidReportResultsStore').removeAll();
	}

	var uuidGrid = new Ext.grid.GridPanel({
		style: 'margin: 10px 0 10px 0;border: solid 1px #8DB2E3;',
		height: 350,
		width: 250,
		store: generatedUuidsStore,
      tbar: getToolBar('exportData'),
		columns: [{
			header: 'UUIDs',
			dataIndex: 'uuid',
			width: 220,
			sortable: true
		}]			
	});
			
	var uuidCreatePanel = new Ext.Panel({
		id: 'uuidCreatePanel',
		title: 'Create UUIDs',
		autoHeight: true,
		border: true,
		style: 'margin-top: 5px;',
		hideBorders: true,
		layout: 'column',
		forceLayout: true,
		items: [{
			width: 460,
			layout: 'column',
			defaults: {
				border: false,
				width: 460,
				layout: 'column',
				style: 'padding: 10px 0 0 10px;',
			},
			items: [{
				items: [{
					cls: 'stdLabel',
					border: false,
					autoHeight: true,
					width: 150,
					html: 'Method:'
				}, {
					xtype: 'radio',
					name: 'uuidCreationType',
					width: 100,
					boxLabel: 'Generate',
					checked: true,
					listeners: {
						check: function(radio, checked) {
							showCorrectUuidField('generate', checked);
						}
					}
				}, {
					border: false,
					width: 20,
					html: '&nbsp;'
				}, {
					xtype: 'radio',
					name: 'uuidCreationType',
					width: 100,
					boxLabel: 'Upload',
					checked: false,
					listeners: {
						check: function(radio, checked) {
							showCorrectUuidField('upload', checked);
						}
					}
				}]
			}, {
				items: [{
					cls: 'stdLabel',
					border: false,
					autoHeight: true,
					width: 150,
					html: 'Center:'
				}, {
					xtype: 'combo',
					store: centerStore,
					triggerAction: 'all',
					valueField: 'centerId',
					displayField: 'centerDisplayName',
					border: false,
					autoHeight: true,
					width: 300
				}]
			}, {
				id: 'uuidCountField',
				items: [{
					name: 'numberOfUUIDs',
					cls: 'stdLabel',
					border: false,
					autoHeight: true,
					width: 150,
					html: 'Number of UUIDs:'
				}, {
					xtype: 'textfield',
					width: 50
				}]
			}, {
				id: 'uuidFileUploadField',
				hidden: true,
				forceLayout: true,
				items: [{
					cls: 'stdLabel',
					border: false,
					autoHeight: true,
					width: 150,
					html: 'Select a file:'
				}, {
					name:'centerId',
					xtype: 'textfield',
					inputType: 'file',
					width: 300,
					grow: true,
					growMax: 200
				}]
			}, {
				xtype: 'button',
				style: 'padding-top: 10px;float: right;',
				width: 105,
				minWidth: 105,
				text: '<b>Go</b>',
				handler: function(){
					generatedUuidsStore.load();
				}
			}]
		}, {
			border: false,
			width: 75,
			html: '&nbsp;'
		},
			uuidGrid
		]		
	});
	
   var combo = new Ext.form.ComboBox({
       name: 'perpage',
       width: 50,
       store: new Ext.data.ArrayStore({
       fields: ['id'],
       data: [['25'],['50'],['75']]}),
       mode: 'local',
       value: '25',
       listWidth: 40,
       triggerAction: 'all',
       displayField: 'id',
       valueField: 'id',
       editable: false,
       forceSelection: true
   });

   // paging bar
    var paging = new Ext.PagingToolbar({
        pageSize: 25,
        store: uuidReportResultsStore,
        displayInfo: true,
        displayMsg: 'Displaying uuid {0} - {1} of {2}',
        emptyMsg: "No uuids to display",
        items: ['-','Per Page ',combo]
    });

	var uuidReportResultsStore = new Ext.data.JsonStore({
		storeId: 'uuidReportResultsStore',
		url: 'json/generatedUuids.sjson',
		root: 'listOfGeneratedUUIDs',
		totalProperty: 'totalCount',
		fields: [
			'uuid',
			'center',
			'createdBy',
			'creationDate',
			'generationMethod'
		]
	});

   var uuidReportResultsGrid = new Ext.grid.GridPanel({
		id: 'uuidReportResultsGrid',
      autoExpandColumn: 'uuid',
      forceFit: true,
      stripeRows: true,
      height:444,
      width:855,
      frame:true,
      title: 'UUID Results',
      bbar: paging,
      tbar: getToolBar('exportData'),
      store: uuidReportResultsStore,
      enableColumnHide: false,
      columns: [{
			header: 'UUID',
			width: 250,
			sortable: true,
         renderer : detailLinkRenderer,
			dataIndex: 'uuid',
			align: 'center',
			id:'uuid'
		}, {
			header: 'Center',
			width: 240,
			sortable: true,
			dataIndex: 'center',
			renderer : centerRenderer,
			align: 'center'
		}, {
			header: 'Created By',
			width: 80,
			sortable: true,
			dataIndex: 'createdBy',
			align: 'center',
			id:'submittedBy'
		}, {
			header: 'Creation Date',
			width: 100,
			sortable: true,
			renderer : dateRenderer,
			dataIndex: 'creationDate',
			align: 'center'
		}, {
			header: 'Creation Method',
			width: 100,
			sortable: true,
			dataIndex: 'generationMethod',
			align: 'center'
		}]
   });

	var uuidFindPanel = new Ext.Panel({
		id: 'uuidFindPanel',
		title: 'Find UUIDs',
		autoHeight: true,
		border: false,
		hideBorders: true,
		layout: 'column',
		forceLayout: true,
		items: [{
			id: 'viewUuidForm',
			xtype: 'form',
			style: 'margin-top: 5px;',
			border: true,
			columnWidth: 1,
			autoHeight: true,
			layout: 'column',
			items: [{
				border: false,
				style: 'padding-left: 10px;',
				width: 155,
				autoHeight: true,
				items: [{
					cls: 'stdLabel',
					border: false,
					autoHeight: true,
					style: 'padding: 10px 0 10px 0;',
					width: 155,
					html: 'View UUIDs'
				}, {
					id: 'newUuidRadio',
					xtype: 'radio',
					name: 'uuidFindType',
					style: 'margin-left: 10px;',
					width: 100,
					boxLabel: 'New',
					checked: false,
					listeners: {
						check: function(radio, checked) {
							if (checked) {
								loadUuidViewStore('new', checked);
							}
						}
					}
				}, {
					id: 'submittedUuidRadio',
					xtype: 'radio',
					name: 'uuidFindType',
					style: 'margin-left: 10px;',
					width: 100,
					boxLabel: 'Submitted',
					checked: false,
					listeners: {
						check: function(radio, checked) {
							if (checked) {
								loadUuidViewStore('submitted', checked);
							}
						}
					}
				}, {
					id: 'missingUuidRadio',
					xtype: 'radio',
					name: 'uuidFindType',
					style: 'margin-left: 10px;',
					width: 100,
					boxLabel: 'Missing',
					checked: false,
					listeners: {
						check: function(radio, checked) {
							if (checked) {
								loadUuidViewStore('missing', checked);
							}
						}
					}
				}, {
					xtype: 'button',
					minWidth: 100,
					style: 'padding: 15px 5px 10px 0;',
					text: '<b>Clear</b>',
					handler: clearViewUuidSelections
				}]
			}, {
				cls: 'stdLabel',
				border: false,
				style: 'margin-top: 5px; height: 130px; border-left: 2px solid #00667C; width: 10px;',
				width: 15,
				html: '&nbsp;'
			}, {
				id: 'searchUuidForm',
				border: false,
				width: 665,
				autoHeight: true,
				layout: 'column',
				items: [{
					cls: 'stdLabel',
					border: false,
					autoHeight: true,
					style: 'padding: 10px 0 10px 0;',
					width: 665,
					html: 'Search for UUIDs'
				}, {
					border: false,
					width: 385,
					style: 'padding-left: 10px;',
					layout: 'column',
					items: [{
						border: false,
						width: 100,
						html: 'Disease:'
					}, {
						id: 'fieldDisease',
						xtype: 'combo',
						store: diseaseStore,
						triggerAction: 'all',
						displayField:'tumorDescription',
						valueField : 'tumorId',
						emptyText:'Select a disease',
						border: false,
						autoHeight: true,
						width: 270
					}]
				}, {
					border: false,
					width: 275,
					style: 'padding-left: 10px;',
					layout: 'column',
					items: [{
						border: false,
						width: 100,
						html: 'Barcode:'
					}, {
						id: 'fieldBarcode',
						xtype: 'textfield',
						width: 170
					}]
				}, {
					border: false,
					width: 385,
					style: 'padding: 5px 0 0 10px;',
					layout: 'column',
					items: [{
						border: false,
						width: 100,
						html: 'Center:'
					}, {
						id: 'fieldCenter',
						xtype: 'combo',
						store: centerStore,
						triggerAction: 'all',
						valueField: 'centerId',
						displayField: 'centerDisplayName',
						emptyText:'Select a center',
						border: false,
						autoHeight: true,
						width: 270
					}]
				}, {
					border: false,
					width: 275,
					style: 'padding: 5px 0 0 10px;',
					layout: 'column',
					items: [{
						border: false,
						width: 100,
						html: 'Created By:'
					}, {
						id: 'fieldCreatedBy',
						xtype: 'textfield',
						width: 170
					}]
				}, {
					border: false,
					width: 385,
					style: 'padding: 5px 0 0 10px;',
					layout: 'column',
					items: [{
						border: false,
						width: 100,
						html: 'UUID:'
					}, {
						id: 'fieldUuid',
						xtype: 'textfield',
						width: 258
					}]
				}, {
					border: false,
					width: 275,
					style: 'padding: 5px 0 0 10px;',
					layout: 'column',
					items: [{
						border: false,
						width: 100,
						html: 'Creation Date:'
					}, {
						id: 'fieldCreationDate',
						xtype: 'datefield',
						width: 100
					}]
				}, {
					width: 465,
					border: false,
					html: '&nbsp;'
				}, {
					xtype: 'button',
					minWidth: 100,
					style: 'padding: 5px 5px 10px 0;',
					text: '<b>Search</b>',
					handler: function() {
						clearViewUuidSelections();
						loadUuidViewStore('getSearchTerms', true);
					}
				}, {
					xtype: 'button',
					minWidth: 100,
					style: 'padding: 5px 5px 10px 0;',
					text: '<b>Clear</b>',
					handler: function() {
						clearSearchUuidSelections();
					}
				}]
			}]
		}, {
			id: 'resultsPanel',
			border: false,
			columnWidth: 1,
			autoHeight: true,
			style: 'margin-top: 5px;',
			items: [
				uuidReportResultsGrid
			]
		}]
	});

	new tcga.extensions.TabPanel({
		id: 'uuidTabPanel',
		renderTo: 'uuidManager',
		border: false,
		autoHeight: true,
		hideBorders: true,
		activeTab: 0,
		items: [
			uuidCreatePanel,
			uuidFindPanel
		]
	});
}

Ext.onReady(tcga.uuid.start, this);

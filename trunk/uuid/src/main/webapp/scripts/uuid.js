/*******************************************************************************
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 ******************************************************************************/

Ext.namespace('tcga.uuid');

var barcode;
var centerId;
var creationDate;
var disease;
var submittedBy;
var uuid;
var newSearch = false;
var exportDataType;

tcga.uuid.start = function() {
	Ext.QuickTips.init({
		defaults: {
			anchorOffset: 50,
			defaultAlign: 'r-l?'
		}
	});

    var maxAllowedUUIDs;
    function getMaximumAllowedUUID() {
        Ext.Ajax.request({
            url: "uuidConstant.json",
            method: 'GET',
            success : function(response){
                            var res = Ext.util.JSON.decode(response.responseText);	
                            maxAllowedUUIDs = res.uuidMaxAllowed;
                        }
        });
    }

	centerRecordTaskLocation = Ext.data.Record.create([
	    {name: "centerId", type: "string"},
	    {name: "centerDisplayText", type: "string"}
	]);
	
	var centerRecord = new centerRecordTaskLocation({
	    //combo sets empty string to displayText - so trick it by assigning space
	    //that way reset will pick displayText and not All
	    centerId: "  ",
	    centerDisplayText: "All"
	});
	    
	/*
	 * Here be those merchants that bringeth forth for our consumptary pleasure the data from those rich
	 * stores of the database that provideth us with the sustenance of data
	 */
	var centerStore = new Ext.data.JsonStore({
		storeId: 'centersStore',
		url: 'centers.json',
		root: 'centerData',
		fields: [
			'centerId',
			'centerDisplayText'
		],
		autoLoad: true,
	    listeners: {
	    	load: function (){ 
	    		centerStore.insert(0,centerRecord);
			}
	    }
	});

	diseaseRecordTaskLocation = Ext.data.Record.create([
	    {name: "tumorId", type: "string"},
	    {name: "tumorName", type: "string"},
	    {name: "tumorDescription", type: "string"},
	    {name: "tumorDisplayText", type: "string"}
	]);
	
	var diseaseRecord = new diseaseRecordTaskLocation({
	    //combo sets empty string to displayText - so trick it by assigning space
	    //that way reset will pick displayText and not All
	    tumorId: "  ",
	    tumorName: "",
	    tumorDescription: "",
	    tumorDisplayText: "All"
	});

	var diseaseStore = new Ext.data.JsonStore({
		url:'diseases.json',
		storeId:'diseases',
		root:'diseases',
		idProperty:'tumorId',
		fields: [
			'tumorId',
			'tumorName',
			'tumorDescription',
            'tumorDisplayText'
		],
		autoLoad: true,
	    listeners: {
	    	load: function (){ 
	    		diseaseStore.insert(0,diseaseRecord);
			}
	    }
	});

    var generatedUuidsProxy = new Ext.data.HttpProxy({
        url: 'generateUUIDs.json'
    });
	var generatedUuidsStore = new Ext.data.JsonStore({
		storeId: 'generatedUuidsStore',
		proxy: generatedUuidsProxy,
		root: 'listOfGeneratedUUIDs',
		fields: [
			'uuid',
			'centerId',
			'createdBy',
			'creationDate',
			'generationMethod'
		]
	});

    var uploadedUuidsStore = new Ext.data.JsonStore({
		storeId: 'generatedUuidsStore',
		url: 'uploadResults.json',
		root: 'uploadedUUIDs',
		fields: [
			'uuid',
			'centerId',
			'createdBy',
			'creationDate',
			'generationMethod'
		]
	});

    var uuidReportResultsStore = new Ext.data.JsonStore({
        storeId: 'uuidReportResultsStore',
        url : 'uuidReport.json',
        remoteSort: true,
        root: 'reportResults',
        totalProperty: 'totalCount',
        fields: [
            'uuid',
            'center',
            'createdBy',
            'creationDate',
            'generationMethod',
            'latestBarcode',
            'diseaseAbbrev'
        ]
    });

    var uuidSearchResultsStore = new Ext.data.JsonStore({
        storeId: 'uuidSearchResultsStore',
        url : 'paginatedResults.json',
        remoteSort : true,
        root : "searchResults",
        totalProperty: 'totalCount',
        fields: [
            'uuid',
            'creationDate',
            'generationMethod',
            'center',
            'createdBy',
            'latestBarcode',
            'diseaseAbbrev'
        ]
    });

	/*
	 * Here liveth functions most grave that speaketh of exporting data
	 */
	var getToolBar = function(id) {
		return new Ext.Toolbar({
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
                id: id,
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
	 * Here followeth the page interaction functionality
	 */
	var showCorrectUuidField = function(radio, checked) {
		var uuidCountField = Ext.getCmp('uuidCountField');
		var uuidFileUploadField = Ext.getCmp('uuidFileUploadField');

		if (radio == 'generate' && checked) {
            Ext.getCmp('uuidCount').setValue('');
			uuidFileUploadField.hide();
			uuidCountField.show();
		}
		else if (radio == 'upload' && checked) {
            Ext.getCmp('form-file').setValue('');
            uuidFileUploadField.show();
			uuidCountField.hide();
		}
	};

	var loadUuidViewStore = function(type, checked) {
        exportDataType = 'exportData';
        var resultsGrid = Ext.getCmp('uuidResultsGrid');
        var resultsPagingTb = Ext.getCmp('resultsPagingTb');
        resultsPagingTb.unbind(uuidSearchResultsStore);
        resultsPagingTb.bind(uuidReportResultsStore);
        resultsGrid.reconfigure(uuidReportResultsStore, resultsGrid.getColumnModel());
        uuidReportResultsStore.setBaseParam('reportType', type);
		uuidReportResultsStore.load({params:{start: 0, limit: paging.pageSize}});
	};

	var clearViewUuidSelections = function() {
		Ext.getCmp('newUuidRadio').setValue(false);
		Ext.getCmp('submittedUuidRadio').setValue(false);
		Ext.getCmp('missingUuidRadio').setValue(false);
	};

    var clearUuidSelections = function() {
        clearViewUuidSelections();

        Ext.getCmp('searchUuidForm').items.each(function(cmp) {
            if (cmp.getXType() === 'panel') {
                cmp.items.each(function(cmp) {
                    if (cmp.getId().indexOf('field') !== -1) {
                        cmp.reset();
                    }
                });
            }
        });
    };

	var uuidGrid = new Ext.grid.GridPanel({
		style: 'margin: 10px 0 10px 0;border: solid 1px #CCCCCC;overflow: hidden;',
		height: 350,
		width: 250,
        loadMask: true,
        tbar: getToolBar('uuidNewExport'),
		store: generatedUuidsStore,
		columns: [{
			header: 'UUIDs',
			dataIndex: 'uuid',
			width: 220,
			sortable: true
		}]
	});
			
    var loginPanel = new Ext.form.FormPanel({
        id: 'loginPanel',
        renderTo: 'loginUI',
        title: '',
        baseCls: 'stdLabel',
		hideBorders: true,
		layout: 'column',
		forceLayout: true,
        items: [
            tcga.uuid.security.getLoginButton(),
            tcga.uuid.security.getLogoutButton()
        ],
        listeners: {activate: tcga.uuid.security.showLoginPopup}
	});

    getMaximumAllowedUUID();
	var uuidCreatePanel = new Ext.form.FormPanel({
		id: 'uuidCreatePanel',
		title: 'Create UUIDs',
		autoHeight: true,
		border: true,
		style: 'margin-top: 5px;',
		hideBorders: true,
		layout: 'column',
		forceLayout: true,
        fileUpload: true,
		items: [{
			width: 460,
			layout: 'column',
			defaults: {
				border: false,
				width: 460,
				layout: 'column',
				style: 'padding: 10px 0 0 10px;'
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
                    id: 'uuidCreationTypeGenerate',
					name: 'uuidCreationType',
					width: 100,
					boxLabel: 'Generate',
                    value: 'generate',
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
                    id: 'uuidCreationTypeUpload',
					name: 'uuidCreationType',
					width: 100,
					boxLabel: 'Upload',
                    value: 'upload',
					checked: false,
					listeners: {
						check: function(radio, checked) {
							showCorrectUuidField('upload', checked);
						}
					}
				}]
			}, {
                    id: 'centerComboField',
				    items: [{
					cls: 'stdLabel',
					border: false,
					autoHeight: true,
					width: 150,
					html: 'Center:'
				}, {
                    id: 'centerCombo',
					xtype: 'combo',
					store: centerStore,
                    triggerAction: 'all',
                    emptyText: 'Select a center',
                    mode: 'local',
					valueField: 'centerId',
					displayField: 'centerDisplayText',
					border: false,
					autoHeight: true,
					width: 300,
                    forceSelection: true,
                    selectOnFocus:true,
                    allowBlank : false,
                    blankText : 'This field is required.',
                    validateOnBlur:false
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
                        id: 'uuidCount',
					    xtype: 'numberfield',
                        allowDecimals : false,
                        allowBlank: false,
                        allowNegative: false,
                        validateOnBlur: false,
					    width: 50
				    }]
        }, {
				id: 'uuidFileUploadField',
				items: [{
                    id: 'centerId',
                    name: 'centerId',
                    xtype: 'hidden'
                }, {
					cls: 'stdLabel',
					border: false,
					autoHeight: true,
					width: 150,
					html: 'Select a file:'
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
			}, {
				xtype: 'button',
				style: 'padding-top: 10px;float: right;',
				width: 105,
				minWidth: 105,
				text: '<b>Go</b>',
				handler: function(){
                    var uuidFormPanel = Ext.getCmp('uuidCreatePanel');
                    if (Ext.getCmp('uuidCreationTypeGenerate').getValue() == true) {
                        exportDataType = 'listOfGeneratedUUIDs';

                     Ext.getCmp('form-file').setValue('jim');
                     if (uuidFormPanel.getForm().isValid()) {
                        Ext.getCmp('form-file').setValue('');
                        if(Ext.getCmp('uuidCount').getValue() > maxAllowedUUIDs) {
                            Ext.Msg.show({
                               title:'Error',
                               msg: 'Only ' + maxAllowedUUIDs + ' UUIDs can be generated at a time.',
                               icon: Ext.Msg.ERROR,
                               buttons: Ext.Msg.OK
                            });
                        } else {
                            generatedUuidsProxy.setUrl('generateUUIDs.json?numberOfUUIDs=' +
                            Ext.getCmp('uuidCount').getValue() +'&centerId=' +
                            Ext.getCmp('centerCombo').getValue());
                            uuidGrid.reconfigure(generatedUuidsStore, uuidGrid.getColumnModel());
                            generatedUuidsStore.load();
                        }
                     } else {
                            Ext.Msg.alert('Error', 'All fields are required to generate UUIDs.');
                     }
                    } else {
                        exportDataType = 'uploadedUUIDs';

                                Ext.getCmp('uuidCount').setValue(0);
                                 if(uuidFormPanel.getForm().isValid()){
                                    Ext.getCmp('centerId').setValue(Ext.getCmp('centerCombo').getValue());
                                    uuidFormPanel.getForm().submit({
                                    waitMsg: 'Uploading file...',
                                    url: 'uuidUpload.htm',
                                    method: 'POST',
                                        success : function(form, action) {
                                            var res = Ext.util.JSON.decode(action.response.responseText);
                                            Ext.Msg.show({
                                                title: 'Succes',
                                                msg: 'File: '+ res.message +' processed successfuly!',
                                                minWidth: 200,
                                                modal: true,
                                                icon: Ext.Msg.INFO,
                                                buttons: Ext.Msg.OK
                                            });
                                        uuidGrid.reconfigure(uploadedUuidsStore, uuidGrid.getColumnModel());
                                        uploadedUuidsStore.load();
                                        Ext.getCmp('uuidCount').setValue('');
                                        },
                                        failure : function(form, action) {
                                            var res = Ext.util.JSON.decode(action.response.responseText);
                                            Ext.Msg.show({
                                                title: 'Error',
                                                msg: res.message,
                                                minWidth: 200,
                                                modal: true,
                                                buttons: Ext.Msg.OK,
                                                icon: Ext.Msg.ERROR
                                            });
                                            Ext.getCmp('uuidCount').setValue('');
                                        }
                                    });
                                 } else {
                                     Ext.Msg.alert('Error', 'All fields are required to generate UUIDs.');
                                 }
                    }
                }
            }]
		}, {
			border: false,
			width: 65,
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
       data: [['25'],['50'],['75'], ['100'], ['200'], ['500'], ['1000']]}),
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
        id: 'resultsPagingTb',
        pageSize: 25,
        store: uuidSearchResultsStore,
        displayInfo: true,
        displayMsg: 'Displaying uuid {0} - {1} of {2}',
        emptyMsg: "No uuids to display",
        items: ['-','Per Page ',combo]
    });

    combo.on('select', function(combo, record) {
        paging.pageSize = parseInt(record.get('id'), 10);
        paging.doLoad(paging.cursor);
    }, this);
	
   var uuidResultsGrid = new Ext.grid.GridPanel({
      id: 'uuidResultsGrid',
      autoExpandColumn: 'uuid',
      forceFit: true,
      loadMask: true,
      stripeRows: true,
      height:444,
      width:940,
      frame:true,
      title: 'UUID Results',
      bbar: paging,
      tbar: getToolBar('exportDataMenu'),
      store: uuidSearchResultsStore,
      enableColumnHide: false,
      columns: [{
			header: 'UUID',
			width: 170,
			sortable: true,
            renderer : detailLinkRenderer,
			dataIndex: 'uuid',
			hideable:false,
			id:'uuid'
		}, {
			header: 'Center',
			width: 140,
			sortable: true,
			dataIndex: 'center',
			renderer : centerRenderer
		}, {
			header: 'Created By',
			width: 70,
			sortable: true,
			dataIndex: 'createdBy',
			align: 'center',
			id:'submittedBy'
        }, {
              header: 'Disease',
              width: 55,
              sortable: true,
              dataIndex: 'diseaseAbbrev',
              align: 'center',
              id:'diseaseAbbrev'
        }, {
			header: 'Creation Date',
			width: 90,
			sortable: true,
			renderer : dateRenderer,
			dataIndex: 'creationDate',
			align: 'center'
		}, {
			header: 'Creation Method',
			width: 90,
			sortable: true,
			dataIndex: 'generationMethod',
			align: 'center'
		}, {
			header: 'Latest Barcode',
			width: 220,
			sortable: true,
			dataIndex: 'latestBarcode'
		}]
   });

	//Create a show/hide menu button on the toolbar
    var view = uuidResultsGrid.getView();
	view.colMenu = new Ext.menu.Menu({
		listeners: {
			beforeshow: view.beforeColMenuShow,
			itemclick: view.handleHdMenuClick,
			scope: view
		}
	});
    uuidResultsGrid.getTopToolbar().add('-',{
    	iconCls: 'x-cols-icon',
    	text: 'Show/Hide Columns', 
    	menu: view.colMenu
    },'-',
    {
    	text: 'Reset Table',
    	iconCls: 'icon-reset',
	    handler: function() {
	        window.location.href = "index.jsp";
	    } 
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
								loadUuidViewStore('newUUID', checked);
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
								loadUuidViewStore('submittedUUID', checked);
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
								loadUuidViewStore('missingUUID', checked);
							}
						}
					}
				}]
			}, {
				cls: 'stdLabel',
				border: false,
				style: 'margin-top: 5px; height: 141px; border-left: 2px solid #CCCCCC; width: 10px;',
				width: 15,
				html: '&nbsp;'
			}, {
				id: 'searchUuidForm',
                monitorValid:true,
				border: false,
				width: 760,
				autoHeight: true,
				layout: 'column',
				items: [{
					cls: 'stdLabel',
					border: false,
					autoHeight: true,
					style: 'padding: 10px 0 10px 0;',
					width: 760,
					html: 'Search for UUIDs'
				}, {
					border: false,
					width: 355,
					style: 'padding-left: 10px;',
					layout: 'column',
					items: [{
						border: false,
						width: 70,
						html: 'Disease:'
					}, {
						id: 'fieldDisease',
						xtype: 'combo',
                        triggerAction: 'all',
                        triggerConfig: {tag: "img", id: "diseaseTrigger", src: Ext.BLANK_IMAGE_URL, cls: "x-form-trigger " + this.triggerClass},
                        tpl: '<tpl for="."><div id="disease{[xindex]}" class="x-combo-list-item">{tumorDisplayText}</div></tpl>',
						store: diseaseStore,
                        mode: 'local',
						displayField:'tumorDisplayText',
						valueField : 'tumorId',
						emptyText:'Select a disease',
						border: false,
						autoHeight: true,
						width: 264
					}]
				}, {
					border: false,
					width: 400,
					style: 'padding-left: 10px;',
					layout: 'column',
					items: [{
						border: false,
						width: 100,
						html: 'Barcode:'
					}, {
						id: 'fieldBarcode',
						xtype: 'textfield',
                        regex: new RegExp('^((TCGA)-([A-Z0-9]{2})-([A-Z0-9]{4})(-(([0-9]{2})([A-Z]{1})))?(-(([0-9]{2})([A-Z]{1})))?(-([A-Z0-9]{4}))?(-([0-9]{2}))?)?$'),
                        regexText: 'Error: Not a valid barcode',
						width: 170
					}]
				}, {
					border: false,
					width: 355,
					style: 'padding: 5px 0 0 10px;',
					layout: 'column',
					items: [{
						border: false,
						width: 70,
						html: 'Center:'
					}, {
						id: 'fieldCenter',
						xtype: 'combo',
						store: centerStore,
                        triggerAction: 'all',
                        triggerConfig: {tag: "img", id: "centerTrigger", src: Ext.BLANK_IMAGE_URL, cls: "x-form-trigger " + this.triggerClass},
                        tpl: '<tpl for="."><div id="center{[xindex]}" class="x-combo-list-item">{centerDisplayText}</div></tpl>',
                        mode: 'local',
						valueField: 'centerId',
						displayField: 'centerDisplayText',
						emptyText:'Select a center',
						border: false,
						autoHeight: true,
						width: 264
					}]
				}, {
					border: false,
					width: 400,
					style: 'padding: 5px 0 0 10px;',
					layout: 'column',
					items: [{
						border: false,
						width: 100,
						html: 'Created By:'
					}, {
						id: 'fieldCreatedBy',
						xtype: 'textfield',
						width: 200
					}]
				}, {
					border: false,
					width: 355,
					style: 'padding: 5px 0 0 10px;',
					layout: 'column',
					items: [{
						border: false,
						width: 70,
						html: 'UUID:'
					}, {
						id: 'fieldUuid',
						xtype: 'textfield',
                        regex: new RegExp('^([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})?$'),
                        regexText: 'Error: Not a valid UUID',
						width: 270
					}]
				}, {
					border: false,
					width: 300,
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
					width: 500,
					border: false,
					html: '&nbsp;'
				}, {
                    id: 'searchButton',
					xtype: 'buttonplus',
                    formBind: true,
					minWidth: 100,
					style: 'padding: 5px 5px 10px 0;',
					text: '<b>Search</b>',
					handler: function() {
                        if (Ext.getCmp('viewUuidForm').getForm().isValid()){
						    clearViewUuidSelections();
                            var resultsGrid = Ext.getCmp('uuidResultsGrid');
                            var resultsPagingTb = Ext.getCmp('resultsPagingTb');
                            resultsPagingTb.unbind(uuidReportResultsStore);
                            resultsPagingTb.bind(uuidSearchResultsStore);
                            resultsGrid.reconfigure(uuidSearchResultsStore, resultsGrid.getColumnModel());
                            exportDataType = 'searchResults';
                            barcode = Ext.getCmp('fieldBarcode').getValue();
                            centerId = (Ext.getCmp('fieldCenter').getValue()?Ext.getCmp('fieldCenter').getValue():0);
                            creationDate = (Ext.getCmp('fieldCreationDate').getValue()?Ext.getCmp('fieldCreationDate').getValue().format('m/d/Y'):null);
                            disease = (Ext.getCmp('fieldDisease').getValue()?Ext.getCmp('fieldDisease').getValue():0);
                            submittedBy = Ext.getCmp('fieldCreatedBy').getValue();
                            uuid = Ext.getCmp('fieldUuid').getValue();
                            newSearch = true;
                            uuidSearchResultsStore.load({params: {
                                'barcode': barcode,
                                'centerId': centerId,
                                'creationDate': creationDate,
                                'disease': disease,
                                'submittedBy': submittedBy,
                                'uuid': uuid,
                                'newSearch': newSearch,
                                start:0,limit:paging.pageSize
                            }});
                        }
					}
				}, {
                    id: 'clearButton',
					xtype: 'buttonplus',
					minWidth: 100,
					style: 'padding: 5px 5px 10px 0;',
					text: '<b>Clear</b>',
					handler: function() {
						clearUuidSelections();
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
				uuidResultsGrid
			]
		}]
	});

    uuidSearchResultsStore.on('beforeload', function() {
        uuidSearchResultsStore.baseParams = {
            'barcode': barcode,
            'centerId': centerId,
             'creationDate': creationDate,
             'disease': disease,
             'submittedBy': submittedBy,
             'uuid': uuid,
             'newSearch': false
        };
    });
    
	var uuidTabPanel = new tcga.extensions.TabPanel({
		id: 'uuidTabPanel',
		renderTo: 'uuidManager',
		border: false,
		autoHeight: true,
		hideBorders: true,
		activeTab: 0,
		items: [
            uuidFindPanel,
			uuidCreatePanel
		]
	});

    tcga.uuid.security.hideLogoutButton();

    //Add the restrictions associated to 'Create UUID'
    tcga.uuid.security.addRestrictionsMixedCollectionToDomId(
            'uuidCreatePanel',
            ['ROLE_UUID_CREATOR'],
            null,
            null,
            null);

    //Redraw restricted UI
    tcga.uuid.security.redrawRestrictedUI(['uuidCreatePanel']);
    Ext.getCmp('uuidFileUploadField').hide();

    tcga.uuid.security.updateUIWithUserNameFromServer();
}

function detailLinkRenderer(value, metaData, record, rowIndex, colIndex) {
    return "<a id='uuidResult" + rowIndex + "' style='cursor:pointer;' " +
                    "onClick=\"displayDetailPanel('"+value+"')\">"+value+"</a>";
}

function dateRenderer(value){
    var dt = new Date();
    dt = Date.parseDate(value, "Y-m-d");
    if (dt){
       return dt.format("m/d/Y");
    } else {
        return "";
    }
}

function centerRenderer(value) {
    return value.centerDisplayText;

}

function displayDetailPanel(uuid) {
    var store = Ext.getCmp('uuidResultsGrid').getStore();
    var uuidNdx = store.findExact('uuid', uuid);
    if (uuidNdx == -1) {
        // Mistake, mistake!  We should find it.  If not, throw an error.
        Ext.Msg.alert('Error', 'The UUID was not found.');
        return;
    }
    var data = store.getAt(uuidNdx);

    var uuidDetailPanel = new Ext.Panel({
        layout:'table',
        layoutConfig: {columns: 2},
        autoHeight: true,
        autoScroll: true,
        bodyStyle:'padding-bottom:8px',
        border: true,
        defaults: {
            border: false,
            bodyStyle:'padding:5px'
        },
        items: [
            { html: 'UUID: ', cellCls: 'detailHeading'},
            { html: data.get('uuid'), cellCls:'detailValue'},
            { html: 'Center: ', cellCls: 'detailHeading'},
            { html: centerRenderer(data.get('center')), cellCls: 'detailValue'},
            { html: 'Created By:' , cellCls:'detailHeading'},
            { html: data.get('createdBy'), cellCls: 'detailValue'},
            { html: 'Disease:' , cellCls:'detailHeading'},
            { html: data.get('diseaseAbbrev'), cellCls: 'detailValue'},
            { html: 'Creation Date:' , cellCls:'detailHeading'},
            { html: dateRenderer(data.get('creationDate')), cellCls: 'detailValue'},
            { html: 'Creation Method:' , cellCls:'detailHeading'},
            { html: data.get('generationMethod'), cellCls: 'detailValue'}
        ]
    });

    if(data.get('latestBarcode')) {
        uuidDetailPanel.add({html: 'Latest Barcode:' , cellCls:'detailHeading'});
        uuidDetailPanel.add({html: data.get('latestBarcode'), cellCls: 'detailValue'});

    /* This data does not exist.  Is it supposed to?
        var historyTable = new Ext.grid.GridPanel({
            store: barcodeListStore,
            enableColumnHide: false,
            columns: [
                {header: 'Barcode', width: 130, sortable: true, dataIndex: 'barcode', align: 'center'},
                {header: 'Disease', width: 130, sortable: true, dataIndex: 'disease', align: 'center'},
                {header: 'Effective Date', width: 130, sortable: true, dataIndex: 'effectiveDate', align: 'center'}
            ],
            stripeRows: true,
            width:400,
            frame:false,
            autoHeight : true,
            border: true
        });

        if(barcodeListStore.getTotalCount()) {
            uuidDetailPanel.add({ html: 'Changes:' , cellCls:'detailHeading'});
            uuidDetailPanel.add(historyTable);
        }
    */
    }

    var win = new Ext.Window({
        title:'UUID Details',
        closable:true,
        width:600,
        autoHeight : true,
        collapsible: false,
        modal: true,
        layout: 'fit',
        buttons: [{text: 'Close',handler: function(){win.hide();}}]
   });
   win.add(uuidDetailPanel);
   win.show();

}

Ext.onReady(tcga.uuid.start, this);

/**
 * This will catch authentication exceptions from Spring and display a warning.
 */
Ext.util.Observable.observeClass(Ext.data.Connection); 
Ext.data.Connection.on('requestcomplete', function(dataConnection, response){
        var notAuthenticated = (response.responseText.indexOf('Please authenticate') != -1);
        if(notAuthenticated) {
            tcga.uuid.security.logout();
            tcga.uuid.security.showLoginPopup("Error: Your session timed out.<br/>Please authenticate before re-submitting data.");
        }
});

/**
 * Display error message
 * 
 * @param message the error message
 */
function displayError(message) {
    Ext.Msg.show({
        title: 'Error',
        msg: message,
        icon: Ext.Msg.ERROR,
        buttons: Ext.Msg.OK
     });
}

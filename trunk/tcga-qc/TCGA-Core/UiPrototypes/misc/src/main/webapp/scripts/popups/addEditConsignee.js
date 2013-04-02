Ext.namespace('marcs.popups');

marcs.popups.addEditConsignee = function() {
		
	// First, try to load racInstructions.  If they exist, then show the instructions for editing
	var dataStore = new Ext.data.JsonStore({
		storeId: 'consigneeStore',
		url: 'json/consignee.sjson',
		root: 'consignee',
		fields: [
			'id',
			'dateAdded',
			'directAccount',
			'lastName',
			'firstName',
			'feiNum',
			'address',
			'city',
			'state',
			'zipcode',
			'county',
			'district',
			'region',
			'country',
			'phone',
			'contact',
			'type',	// consignee type
			'comments'
		],
		listeners: {
			load: function(store, recs) {
				if (store.getCount() == 0) {
					Ext.getCmp('deleteFlag').hide();					
					Ext.getCmp('formTitle').update('Add Consignee');														
				}
				else {
					Ext.getCmp('deleteFlag').show();
					Ext.getCmp('formTitle').update('Edit Consignee');
					// set value
					Ext.getCmp('dateAdded').setValue(recs[0].get('dateAdded'));					
					Ext.getCmp('comments').setValue(recs[0].get('comments'));								
				}
			}
		}
	});
	//dataStore.load();
	
	var formWidth = 600;
	var col1Width = 185;
	var col2Width = 250;
	
	var dataPanel = new Ext.FormPanel({	
		id: 'myDataPanel',	
		border: false,
		hideBorders: true,
		padding: '0px 3px 0px 3px',
		width: formWidth,
		items: [{			
			xtype: 'panel',
			border: false,
			style: 'padding-top: 10px;',
			layout: 'column',
			width: formWidth - 10,
			items: [{
				id: 'formTitle',
				border: false,
				cls: 'stdTitle',
				width: 200,				
				html: 'Add Consignee'
			}, {
				xtype: 'checkbox',
				hidden: true,
				id: 'deleteFlag',
				name: 'deleteFlag',
				boxLabel: 'Delete consignee',
				width: 150
			}]
		}, {			
			xtype: 'panel',
			border: false,
			style: 'padding-top: 15px;',
			layout: 'column',
			width: formWidth - 10,
			items: [{
				border: false,
				cls: 'stdLabel right',
				width: col1Width,
				style: 'padding-right: 10px;',
				html: 'Date added'
			}, {
				id: 'dateAdded',
				xtype: 'datefield'				
			}]
		}, {
			xtype: 'panel',
			border: false,
			style: 'padding-top: 10px;',
			layout: 'column',
			width: formWidth - 10,
			items: [{				
				xtype: 'labelpanel',
				border: false,
				required: true,
				labelWidth: col1Width,
				labelText: 'Is this consignee part of a sub-account?'
			}, {
				xtype: 'panel',
				border: false,
				style: 'padding-top: 10px; padding-left: 10px',
				layout: 'column',
				width: 100,
				items: [{
					xtype: 'panel',
					border: false,
					layout: 'column',
					items: [{
						xtype: 'radio',
						id: 'isSub',
						name: 'isSub',
						boxLabel: 'Yes',
						inputValue: 'y',
						width: 100,
						listeners: {
							check: function(rb, rbChecked){
								if (rbChecked) {
									Ext.getCmp('directAccountPanel').show();																		
								}
							}
						}				
					}]					
				}, {
					xtype: 'panel',
					border: false,
					layout: 'column',						
					items: [{
						xtype: 'radio',
						id: 'isNotSub',
						name: 'isSub',
						boxLabel: 'No',
						inputValue: 'n',
						checked: true,
						width: 100,
						listeners: {
							check: function(rb, rbChecked){
								if (rbChecked) {
									Ext.getCmp('directAccountPanel').hide();																		
								}
							}
						}						
					}]					
				}]	// end of complex row item				
			}]
		}, {
			xtype: 'panel',
			id: 'directAccountPanel',
			hidden: true,
			border: false,
			style: 'padding-top: 10px;',
			layout: 'column',
			width: formWidth - 10,
			items: [{
				border: false,
				cls: 'stdLabel right',
				width: col1Width,
				style: 'padding-right: 10px;',
				html: 'Direct account'
			}, {
				border: false,
				width: col2Width,
				style: 'padding-left: 10px',				
				items: [marcs.util.formFields.personList('directAccount')]
			}]
		}, {
			xtype: 'panel',
			border: false,
			style: 'padding-top: 10px;',
			layout: 'column',
			width: formWidth - 10,
			items: [{				
				xtype: 'labelpanel',
				border: false,
				required: true,
				labelWidth: col1Width,
				labelText: 'Consignnee Name'
			}, {
				xtype: 'textfield',
				id: 'lastName',
				style: 'marginLeft: 10px',
				width: col2Width
			}]
		}, {
			xtype: 'panel',
			border: false,
			style: 'padding-top: 10px;',
			layout: 'column',
			width: formWidth - 10,
			items: [{
				xtype: 'labelpanel',
				border: false,
				required: true,
				labelWidth: col1Width,
				labelText: 'FEI #'
			}, {
				xtype: 'textfield',
				id: 'feiNum',
				style: 'marginLeft: 10px',
				width: col2Width
			}]
		}, {
			xtype: 'panel',
			border: false,
			style: 'padding-top: 10px;',
			layout: 'column',
			width: formWidth - 10,
			items: [{
				xtype: 'labelpanel',
				border: false,
				required: true,
				labelWidth: col1Width,
				labelText: 'Address'
			}, {
				xtype: 'textfield',
				id: 'address',
				style: 'marginLeft: 10px',
				width: col2Width
			}]	
		}, {
			xtype: 'panel',
			border: false,
			style: 'padding-top: 10px;',
			layout: 'column',
			width: formWidth - 10,
			items: [{
				xtype: 'labelpanel',
				border: false,
				required: true,
				labelWidth: col1Width,
				labelText: 'City'
			}, {
				xtype: 'textfield',
				id: 'city',
				style: 'marginLeft: 10px',
				width: col2Width
			}]
		}, {
			xtype: 'panel',
			border: false,
			style: 'padding-top: 10px;',
			layout: 'column',
			width: formWidth - 10,
			items: [{
				xtype: 'labelpanel',
				border: false,
				required: true,
				labelWidth: col1Width,
				labelText: 'State'
			}, {
				border: false,
				width: col2Width,
				style: 'padding-left: 10px',
				items: [marcs.util.formFields.stateList({nameId:'state', combo:true})]
			}]						
		}, {
			xtype: 'panel',
			border: false,
			style: 'padding-top: 10px;',
			layout: 'column',
			width: formWidth - 10,
			items: [{
				xtype: 'labelpanel',
				border: false,
				required: true,
				labelWidth: col1Width,
				labelText: 'Zip Code'
			}, {
				xtype: 'textfield',
				id: 'zipcode',
				style: 'marginLeft: 10px',
				width: col2Width
			}]		
		}, {
			xtype: 'panel',
			border: false,
			style: 'padding-top: 10px;',
			layout: 'column',
			width: formWidth - 10,
			items: [{
				border: false,
				cls: 'stdLabel right',
				width: col1Width,
				style: 'padding-right: 10px;',
				html: 'County'
			}, {
				border: false,
				width: col2Width,
				style: 'padding-left: 10px',
				items: [marcs.util.formFields.personList('county')]
			}]				
		}, {
			xtype: 'panel',
			border: false,
			style: 'padding-top: 10px;',
			layout: 'column',
			width: formWidth - 10,
			items: [{
				border: false,
				cls: 'stdLabel right',
				width: col1Width,
				style: 'padding-right: 10px;',
				html: 'Region'
			}, {
				border: false,
				width: col2Width,
				style: 'padding-left: 10px',
				items: [marcs.util.formFields.personList('region')]
			}]				
		}, {
			xtype: 'panel',
			border: false,
			style: 'padding-top: 10px;',
			layout: 'column',
			width: formWidth - 10,
			items: [{
				border: false,
				cls: 'stdLabel right',
				width: col1Width,
				style: 'padding-right: 10px;',
				html: 'Country'
			}, {
				xtype: 'textfield',
				id: 'country',
				style: 'marginLeft: 10px',
				width: col2Width
			}]
		}, {
			xtype: 'panel',
			border: false,
			style: 'padding-top: 10px;',
			layout: 'column',
			width: formWidth - 10,
			items: [{
				border: false,
				cls: 'stdLabel right',
				width: col1Width,
				style: 'padding-right: 10px;',
				html: 'Phone Number'
			}, {
				xtype: 'textfield',
				id: 'phone',
				style: 'marginLeft: 10px',
				width: col2Width
			}]				
		}, {
			xtype: 'panel',
			border: false,
			style: 'padding-top: 10px;',
			layout: 'column',
			width: formWidth - 10,
			items: [{
				border: false,
				cls: 'stdLabel right',
				width: col1Width,
				style: 'padding-right: 10px;',
				html: 'Point of Contact'
			}, {
				xtype: 'textfield',
				id: 'contact',
				style: 'marginLeft: 10px',
				width: col2Width
			}]
		}, {
			xtype: 'panel',
			border: false,
			style: 'padding-top: 10px;',
			layout: 'column',
			width: formWidth - 10,
			items: [{
				border: false,
				cls: 'stdLabel right',
				width: col1Width,
				style: 'padding-right: 10px;',
				html: 'Consignee Type'
			}, {
				border: false,
				width: col2Width,
				style: 'padding-left: 10px',
				items: [marcs.util.formFields.personList('consigneeType')]
			}]	
		
												
		}, {
			xtype: 'panel',
			border: false,
			style: 'padding-top: 10px;',
			layout: 'column',
			width: formWidth - 10,
			items: [{
				border: false,
				cls: 'stdLabel right',
				width: col1Width,
				style: 'padding-right: 10px;',
				html: 'Comments'
			}, {
				border: false,
				style: 'padding-left: 10px',
				items: [{
					xtype: 'textarea',
					id: 'comments',
					height: 60,
					width: 300
				}]
			}]
		}, {			
			buttons: [{
				style: 'padding-left: 5px;',
				minWidth: 80,
				text: '<b>Save</b>'		
			}, {
				xtype: 'clickpanel',
				style: 'padding-left: 5px; color: purple',
				cls: 'stdOnClickLink',
				minWidth: 80,
				html: 'Cancel',
				handler: function() {
					Ext.getCmp('consigneeWindow').close();
				}
			}],
			buttonAlign: 'center'		
		}]
	});

var consigneeWindow = new Ext.Window({
		id: 'consigneeWindow',
		width: formWidth + 20,
		border: true,
		closable: true,
		plain: true,
		items: dataPanel
	});
	
	consigneeWindow.show();
	
}

Ext.namespace('marcs.popups');

marcs.popups.inspectionalObservation = function() {
			
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
					Ext.getCmp('').hide();					
					Ext.getCmp('formTitle').update('New Observation');														
				}
				else {
					Ext.getCmp('deleteFlag').show();
					Ext.getCmp('formTitle').update('Update Observation');
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
	var col2Width = 400;
	
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
				html: 'Add Observation'
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
				html: 'Select Category of Citation'
			}, {
				border: false,
				width: col2Width,
				style: 'padding-left: 10px',
				items: [marcs.util.formFields.dropdownList('category',200)]
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
				html: 'Select Observation'
			}, {
				border: false,
				width: col2Width,
				style: 'padding-left: 10px',
				items: [marcs.util.formFields.dropdownList('observation',200)]
			}]								
		}, {
			xtype: 'panel',
			id: 'manufactureStagePanel',
			//hidden: true,
			border: false,
			style: 'padding-top: 10px;',
			layout: 'column',
			width: formWidth - 10,
			items: [{
				border: false,
				cls: 'stdLabel right',
				width: col1Width,
				style: 'padding-right: 10px;',
				html: 'Manufacturing Stages'
			}, {
				border: false,
				width: col2Width,
				style: 'padding-left: 10px',				
				items: [marcs.util.formFields.dropdownList('stages',200)]
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
					id: 'Detailed Observation',
					height: 60,
					width: 300
				}]
			}]
		} , {
			xtype: 'panel',
			border: false,
			style: 'padding-top: 10px; padding-left: 195px;',
			layout: 'column',
			width: formWidth - 10,
			items: [{				
				xtype: 'checkbox',
				id: 'includeFlag',
				name: 'includeFlag',
				boxLabel: 'Include in Inspectional Observation Report?'
			}]		
		}, {
			xtype: 'panel',
			id: 'annoRadio',
			border: false,
			style: 'padding-top: 10px;',
			layout: 'column',
			width: formWidth - 10,
			items: [{
				border: false,
				cls: 'stdLabel right',
				width: col1Width,
				style: 'padding-right: 10px;',
				html: 'Annotations'
			}, {				
				xtype: 'radio',
				name: 'annoRadio',
				boxLabel: 'Promise to correct',
				checked: true,
				inputValue: '1',
				width: col2Width
			}, {
				border: false,
				autoHeight: true,
				width: col1Width,
				style: 'padding-right: 10px;',
				html: '&nbsp;'						
			}, {					
				xtype: 'radio',
				name: 'annoRadio',
				boxLabel: 'Promise to correct between',
				inputValue: '2',
				width: col2Width - 238
			}, {
				id: 'correctSDate',
				xtype: 'datefield'
			}, {
				border: false,
				width: 30,
				html:'&nbsp;&nbsp;and'
			}, {
				id: 'correctEDate',
				xtype: 'datefield'
			}, {
				border: false,
				autoHeight: true,
				width: col1Width,
				style: 'padding-right: 10px;',
				html: '&nbsp;'					
			}, {					
				xtype: 'radio',
				name: 'annoRadio',
				boxLabel: 'Promise to correct by date',
				inputValue: '3',
				width: col2Width - 238
			}, {
				id: 'correctDate',
				xtype: 'datefield'
			}, {
				border: false,
				autoHeight: true,
				width: col1Width,
				style: 'padding-right: 10px;',
				html: '&nbsp;'					
			}, {				
				xtype: 'radio',
				name: 'annoRadio',
				boxLabel: 'Corrected and verified',
				inputValue: '4',
				width: col2Width													
			}]				
		}, {			
			buttons: [{
				style: 'padding-left: 5px;',
				minWidth: 80,
				text: '<b>Save</b>'		
			}, {
				xtype: 'clickpanel',
				border:false,
				style: 'padding-left: 5px; color: purple',
				cls: 'stdOnClickLink',
				minWidth: 80,
				html: 'Cancel',
				handler: function() {
					Ext.getCmp('inspObservWindow').close();
				}
			}],
			buttonAlign: 'center'		
		}]
	});

	var inspObservWindow = new Ext.Window({
		id: 'inspObservWindow',
		width: formWidth + 20,
		border: true,
		closable: true,
		plain: true,
		items: dataPanel
	});
	
	inspObservWindow.show();
	
}

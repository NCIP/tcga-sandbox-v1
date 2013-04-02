Ext.namespace('marcs.popups');

marcs.popups.addEditPAC = function() {
			
	var dataStore = new Ext.data.JsonStore({
		storeId: 'consigneeStore',
		url: 'json/consignee.sjson',
		root: 'consignee',
		fields: [
			'id',
			'pac',
			'subject',
			'establishType'
		],
		listeners: {
			load: function(store, recs) {
				if (store.getCount() == 0) {				
					Ext.getCmp('formTitle').update('Add Inspection Results: Detail');														
				}
				else {
					Ext.getCmp('formTitle').update('Edit Inspection Results: Detail');
					// set value
					Ext.getCmp('pac').setValue(recs[0].get('pac'));													
				}
			}
		}
	});
	//dataStore.load();
	
	var formWidth = 700;
	var col1Width = 185;
	var col2Width = 400;
	
	var dataPanel = new Ext.FormPanel({	
		id: 'myDataPanel',	
		border: false,
		hideBorders: true,
		padding: '0px 3px 0px 3px',
		width: formWidth,
		items: [{						
			id: 'formTitle',
			border: false,
			cls: 'stdTitle',
			width: formWidth,				
			html: 'Add Inspection Results: Detail'			
		}, {
			border: false,
			style: 'padding-top: 10px;',
			layout: 'column',
			width: formWidth - 10,
			items: [{
				border: false,
				cls: 'stdLabel right',
				width: col1Width + 10,	// + 10 to align with fieldset below
				style: 'padding-right: 10px;',
				html: 'PAC'
			}, {
				border: false,
				width: col2Width,
				style: 'padding-left: 10px',
				items: [marcs.util.formFields.dropdownList('pac',200)]
			}]	
		}, {
			border: false,
			style: 'padding-top: 10px;',
			layout: 'column',
			width: formWidth - 10,
			items: [{
				border: false,
				cls: 'stdLabel right',
				width: col1Width + 10,
				style: 'padding-right: 10px;',
				html: 'Select Product-related Subject'
			}, {
				border: false,
				width: col2Width,
				style: 'padding-left: 10px',
				items: [marcs.util.formFields.dropdownList('subject',200)]
			}]								
		}, {
			border: false,
			style: 'padding-top: 10px;',
			layout: 'column',
			width: formWidth - 10,
			items: [{
				border: false,
				cls: 'stdLabel right',
				width: col1Width + 10,
				style: 'padding-right: 10px;',
				html: 'Establishment Type'
			}, {
				border: false,
				width: col2Width,
				style: 'padding-left: 10px',				
				items: [marcs.util.formFields.dropdownList('establishType',200)]
			}]
		}, {
			border: false,
			style: 'padding-top: 10px;',
			layout: 'column',
			width: formWidth - 10,
			items: [{
				border: false,
				cls: 'stdLabel right',
				width: col1Width + 10,
				style: 'padding-right: 10px;',
				html: 'Inspection Findings'
			}, {
				border: false,
				width: col2Width,
				style: 'padding-left: 10px',				
				items: [marcs.util.formFields.dropdownList('inspectionFindings',200)]
			}]
		}, {											
            xtype:'fieldset',
			border:true,
			title: 'Proposed Inspection Results',
			width: formWidth - 20,
			style: 'padding-top: 30px;',	          
            items :[
			{				
				border: false,
				style: 'padding-top: 10px;',
				layout: 'column',
				width: formWidth - 20,
				items: [{
					border: false,
					cls: 'stdLabel right',
					width: col1Width,
					style: 'padding-right: 10px;',
					html: 'Inspection Conclusion'
				}, {					
					border: false,
					width: col2Width,
					style: 'padding-left: 10px;',					
					items: [marcs.util.formFields.dropdownList('conclusion', 300)]
				}]
			}, {
				border: false,
				style: 'padding-top: 10px;',
				layout: 'column',
				width: formWidth - 20,
				items: [{
					border: false,
					cls: 'stdLabel right',
					width: col1Width,
					style: 'padding-right: 10px;',
					html: 'Inspection Reschedule Date'
				}, {
					border: false,
					style: 'padding-left: 10px;',
					items: [{
						id: 'reschedDate',
						xtype: 'datefield'
					}]
				}]					 	
			} , {
				border: false,
				style: 'padding-top: 10px;',
				layout: 'column',
				width: formWidth - 20,
				items: [{
					border: false,
					cls: 'stdLabel right',
					width: col1Width,
					style: 'padding-right: 10px;',
					html: 'Inspection Reschedule Priority'
				}, {					
					border: false,
					width: col2Width,
					style: 'padding-left: 10px;',					
					items: [marcs.util.formFields.priorityList('priority')]
				}]
			} , {
				border: false,
				style: 'padding-top: 10px;',
				layout: 'column',
				width: formWidth - 20,
				items: [{
					border: false,
					cls: 'stdLabel right',
					width: col1Width,
					style: 'padding-right: 10px;',
					html: 'District Decision'
				}, {					
					border: false,
					width: col2Width,
					style: 'padding-left: 10px;',					
					items: [marcs.util.formFields.dropdownList('decision', 300)]
				}]
			} , {
				border: false,
				style: 'padding-top: 10px;',
				layout: 'column',
				width: formWidth - 20,
				items: [{
					border: false,
					cls: 'stdLabel right',
					width: col1Width,
					style: 'padding-right: 10px;',
					html: 'Resolution Text'
				}, {					
					border: false,
					style: 'padding-left: 10px',
					items: [{
						xtype: 'textarea',
						id: 'resolution',
						height: 60,
						width: 300
					}]
				}]
			}]			
		}, {			
			buttons: [{
				style: 'padding-left: 10px;',
				minWidth: 80,
				text: '<b>Save</b>'		
			}, {
				xtype: 'clickpanel',
				border: false,
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

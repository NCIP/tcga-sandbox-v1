Ext.namespace('marcs.popups');

marcs.popups.consigneeUpload = function() {
	var consigneeUploadForm = new Ext.form.FormPanel({
		id: 'consigneeUploadForm',
		fileUpload: true,
		width: 485,
		autoHeight: true,
		items: [{
			xtype: 'textfield',
			inputType: 'file',
			size: 50,
			fieldLabel: 'Consignee File',
			name: 'consigneeFilePath',
			labelStyle: 'padding-left: 5px;',
			permittedExtensions: ['csv', 'xls']
		}, {
			border: false,
			padding: '5px 0px 3px 5px',
			html: 'Upload .csv, .xls, language added here. File can be no larger than xxx MB.'
		}],
		buttons: [{
			xtype: 'button',
			text: 'Upload Now',
			handler: marcs.actions.handlers.uploadConsignees
		}, {
			xtype: 'button',
			text: 'Close',
			handler: function() {
				Ext.getCmp('consigneeUploadWindow').close();
			}
		}],
		buttonAlign: 'center'
	});

	var consigneeUploadWindow = new Ext.Window({
		id: 'consigneeUploadWindow',
		title: 'Consignee Upload',
		width: 500,
		border: true,
		closable: true,
		plain: true,
		items: consigneeUploadForm
	});
	
	consigneeUploadWindow.show();
}

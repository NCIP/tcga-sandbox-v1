/**
 * @author HuoW
 */
Ext.namespace('marcs.popups');

marcs.popups.documentList = function() {
		
	var documentListStore = new Ext.data.JsonStore({
		storeId: 'documentListStore',
		url: 'json/documentList.sjson',
		root: 'documents',
		fields: [
			'docName',
			'docType',
			'fileType',
			'fileSize'
		]
	});
	documentListStore.load();
	
	var documentSM = new Ext.grid.CheckboxSelectionModel();
	var documentListGrid = new Ext.grid.GridPanel({
		padding: '0px 3px 0px 3px',
		height: 200,
		width: 600,
		store: documentListStore,
		selModel: documentSM,
		columns: [
		documentSM,
		{
			id: 'docName',
			dataIndex: 'docName',
			header: 'Document Name',
			sortable: true,
			// width of the browser - checkbox - (width we're taking from the grid) - (grid border adj) - (the width of each column)
			width: 600 - 20 - 13 - 10 - 150 - 150,
			renderer: function(val, mD, rec, row, col) {
				return '<a class="stdOnClickLink">' + val + '</a>';
			}
		}, {
			id: 'docType',
			dataIndex: 'docType',
			header: 'Document Type',
			sortable: true,
			width: 150
		}, {
			id: 'fileType',
			dataIndex: 'fileType',
			header: 'File Type / Size',
			sortable: true,
			width: 150,
			renderer: function(val, mD, rec, row, col) {
				return val + ' ( '+rec.get("fileSize") + ' kb)</a>';				
			}		
		}]
	});
	documentListGrid.setAutoScroll(true);
	
	var documentListWindow = new Ext.Window({
		id: 'documentListWindow',
		width: 610,
		border: true,
		closable: true,
		plain: true,
		items: [{			
			xtype: 'panel',
			border: false,
			hideBorders: true,	
			layout: 'column',		
			dafaults: {
				cls: 'stdLabel'
			},
			items: [{				
				border: false,
				width: 600,
				style: 'padding-right: 0px;',
				cls: 'stdTitle',				
				html: 'Download to Work Offline'
			}, {
				border: false,
				width: 550,
				style: 'padding-left: 10px;',				
				html: 'You have selected to downloaded the following assigmnet to work offline:'
			}, {
				border: false,
				width: 550,
				style: 'padding-left: 10px;',
				cls: 'stdLabel',				
				html: 'Recall Audit Check(recall event ID#)'
			}, {			
				border: false,
				width: 550,
				style: 'padding-top: 30px; padding-left: 10px;',
				cls: 'stdLabel',				
				html: 'Select the supporting documents you would also like to download'
			}]		
		},		
			documentListGrid,
		{
			xtype: 'panel',
			border: false,
			hideBorders: true,	
			layout: 'column',
			items: [{	
				xtype: 'button',
					style: 'padding-left: 10px;',
					minWidth: 80,
					text: '<b>Download</b>'
				}, {
					xtype: 'button',
					style: 'padding-left: 10px;',
					minWidth: 80,
					text: '<b>Cancel</b>'
			}]				
		}
		]	
	});
	
	documentListWindow.show();
}

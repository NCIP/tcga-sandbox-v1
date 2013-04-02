/**
 * @author HuoW
 */
Ext.namespace('marcs.popups');

marcs.popups.racDocumentList = function() {
		
	var dataStore = new Ext.data.JsonStore({
		storeId: 'documentListStore',
		url: 'json/racDocumentList.sjson',
		root: 'documents',
		fields: [
			'docId',
			'name',
			'size',
			'description'
		],
		listeners: {
			load: function(store) {
				// remove the selected items from the list				
				var selectedList = Ext.StoreMgr.get('selectedDocStore').getRange();
				for (var i = 0; i < selectedList.length; i++) {
					var id = selectedList[i].get('docId');					
					var newList = store.getRange();
					for (var j = 0; j< newList.length; j++) {
						var newId = newList[j].get('docId');
						if (newId == id) {
							store.removeAt(j);
							break;
						}	
					}					
				}						
			}
		}
	});
	dataStore.load();
	
	var documentSM = new Ext.grid.CheckboxSelectionModel();
	var documentListGrid = new Ext.grid.GridPanel({
		id: 'documentListData',
		padding: '0px 3px 0px 3px',
		height: 200,
		width: 600,
		store: dataStore,
		selModel: documentSM,
		columns: [		
		documentSM,
		{
			id: 'name',
			dataIndex: 'name',
			header: 'File Name',
			sortable: true,
			// width of the browser - checkbox - (width we're taking from the grid) - (grid border adj) - (the width of each column)
			width: 600 - 20 - 13 - 10 - 100 - 300
		}, {
			id: 'size',
			dataIndex: 'size',
			header: 'File Size(KB)',
			sortable: true,
			width: 100
		}, {
			id: 'description',
			dataIndex: 'description',
			header: 'File Description',
			sortable: true,
			width: 300
		}, {
			id: 'docId',
			dataIndex: 'docId',
			hidden: true						
		}]
	});
	documentListGrid.setAutoScroll(true);
	
	var documentListWindow = new Ext.Window({
		id: 'documentListWindow',
		title: 'Select Documents for RAC Assignment',
		width: 610,
		border: true,
		closable: true,
		plain: true,
		items: [{
			layout: 'column',
			dafaults: {
				cls: 'stdLabel'
			},
			buttons: [{
				style: 'padding-left: 5px;',
				minWidth: 105,
				text: '<b>Select for RAC</b>',
				handler: marcs.control.workAssignment.setSelectedDocs	
			}, {
				style: 'padding-left: 5px;',
				minWidth: 105,
				text: '<b>Print</b>'
			}],
			buttonAlign: 'center'
		},		
			documentListGrid		
		]				
	});
	
	documentListWindow.show();
}

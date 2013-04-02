/**
 * @author HuoW
 */
Ext.namespace('marcs.popups');

marcs.popups.consigneeList = function() {
		
	var consigneeListStore = new Ext.data.JsonStore({
		storeId: 'consigneeListStore',
		url: 'json/consigneeList.sjson',
		root: 'consignees',
		fields: [
			'id',
			'lastName',
			'firstName',
			'address',
			'state',
			'zipCode',
			'district',
			'region',
			'phone',
			'dateAdded',
			'selectedForRAC',
			'comments'
		],
		listeners: {
			load: function(store) {
				// remove the selected items from the list				
				var selectedList = Ext.StoreMgr.get('selectedConsigneeStore').getRange();
				for (var i = 0; i < selectedList.length; i++) {
					var id = selectedList[i].get('id');					
					var newList = store.getRange();
					for (var j = 0; j< newList.length; j++) {
						var newId = newList[j].get('id');
						if (newId == id) {
							store.removeAt(j);
							break;
						}	
					}					
				}						
			}
		}
	});
	consigneeListStore.load();
	
	var consigneeSM = new Ext.grid.CheckboxSelectionModel();
	var consigneeListGrid = new Ext.grid.GridPanel({
		id: 'consigneeListData',
		padding: '0px 3px 0px 3px',
		height: 200,
		width: 1000,
		store: consigneeListStore,
		selModel: consigneeSM,
		columns: [		
		consigneeSM,
		{
			id: 'name',
			dataIndex: 'lastName',
			header: 'Consignee Name',
			sortable: true,
			// width of the browser - checkbox - (width we're taking from the grid) - (grid border adj) - (the width of each column)
			width: 1000 - 20 - 13 - 10 - 180 - 60 - 70 - 100 - 70 - 100 - 80 - 60 - 100,
			renderer: marcs.util.renderer.renderNameEditPossible
		}, {
			id: 'address',
			dataIndex: 'address',
			header: 'Address',
			sortable: true,
			width: 180
		}, {
			id: 'state',
			dataIndex: 'state',
			header: 'State / Province',
			sortable: true,
			width: 60
		}, {
			id: 'zipCode',
			dataIndex: 'zipCode',
			header: 'Zip Code',
			sortable: true,
			width: 70
		}, {
			id: 'district',
			dataIndex: 'district',
			header: 'District',
			sortable: true,
			width: 100
		}, {
			id: 'region',
			dataIndex: 'region',
			header: 'Region',
			sortable: true,
			width: 70
		}, {
			id: 'phone',
			dataIndex: 'phone',
			header: 'Phone Number',
			sortable: true,
			width: 100
		}, {
			id: 'dateAdded',
			dataIndex: 'dateAdded',
			header: 'Date Added',
			sortable: true,
			width: 80
		}, {
			id: 'selectedForRAC',
			dataIndex: 'selectedForRAC',
			header: 'Selected for RAC?',
			sortable: true,
			width: 60
		}, {
			id: 'comments',
			dataIndex: 'comments',
			header: 'Comments',
			sortable: false,
			width: 100
		}, {
			id: 'id',
			dataIndex: 'id',
			hidden: true		
		}]
	});
	consigneeListGrid.setAutoScroll(true);
	
	var consigneeListWindow = new Ext.Window({
		id: 'consigneeListWindow',
		title: 'Select Consignees for RAC Assignment',
		width: 1010,
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
				text: '<b>Select for Rac</b>',
				handler: marcs.control.workAssignment.setSelectedConsignees		
			}, {
				style: 'padding-left: 5px;',
				minWidth: 105,
				text: '<b>Print</b>'
			}],
			buttonAlign: 'center'
		},		
			consigneeListGrid		
		]				
	});
	
	consigneeListWindow.show();
}

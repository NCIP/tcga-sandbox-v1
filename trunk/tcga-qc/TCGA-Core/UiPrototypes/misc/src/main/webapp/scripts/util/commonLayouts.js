Ext.namespace("marcs.util.commonLayout");

marcs.util.commonLayout.docList = function(url) {
	
	var dataStore = new Ext.data.JsonStore({
		storeId: 'selectedDocStore',
		root: 'documents',
		url: url,
		fields: [
			'docId',
			'name',
			'size',
			'description'
		]
	});
	
	if(url != null)
		dataStore.load();
	
	var dataGrid = new Ext.grid.GridPanel({
		id: 'selectedDoc',
		padding: '0px 3px 0px 3px',
		border: false,		
		width: 525,
		//height: 100,
		autoHeight: true,
		store: dataStore,
		columns: [
		{
			id: 'name',
			dataIndex: 'name',
			header: '<b>File Name</b>',
			sortable: true,
			width: 200
		} , {
			id: 'size',
			dataIndex: 'size',
			header: '<b>File Size (kb)</b>',
			width: 100
		} , {
			id: 'description',
			dataIndex: 'description',
			header: '<b>Description</b>',
			width: 200
		}, {
			id: 'docId',
			dataIndex: 'docId',
			hidden: true		
		}]
	});
	
	return dataGrid;
}

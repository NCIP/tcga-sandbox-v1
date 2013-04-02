/**
 * @author HuoW
 */
Ext.namespace('marcs.popups');

marcs.popups.firmInformation = function() {
	
	var dataStore1 = new Ext.data.JsonStore({
		root: 'consumerComplaints',
		fields: [
			'docName',
			'docDate',
			'fileType',
			'fileSize'
		]
	});
	
	var dataStore2 = new Ext.data.JsonStore({
		root: 'prevEir',
		fields: [
			'docName',
			'docDate',
			'fileType',
			'fileSize'
		]
	});
	var dataStore3 = new Ext.data.JsonStore({
		root: 'registrations',
		fields: [
			'docName',
			'docDate',
			'fileType',
			'fileSize'
		]
	});
	
	var dataStore4 = new Ext.data.JsonStore({
		root: 'productCovered',
		fields: [
			'prodName',
			'prodDesc',
			'prodCode'
		]
	});
	
	var dataStore5 = new Ext.data.JsonStore({
		root: 'recalls',
		fields: [
			'eventId',
			'description'
		]
	});												
						
	var dataStore = new Ext.data.JsonStore({
		storeId: 'listStore',
		url: 'json/firmDetails.sjson',
		root: 'firmDetails',
		fields: [
			'firmName',
			'firmFei',
			'profileStatus',
			'consumerComplaints',
			'prevEir',
			'registrations',
			'historyData',
			'productCovered',
			'recalls'			
		], 
		listeners: {			
			load: function(store, recs){
				alert('In load');
				Ext.getCmp('firmName').update(recs[0].get('firmName'));
				Ext.getCmp('firmFei').update(recs[0].get('firmFei'));
				Ext.getCmp('profileStatus').update(recs[0].get('profileStatus'));
				var obj1 = recs[0].get('consumerComplaints');
				var obj2 = recs[0].get('prevEir');
				var obj3 = recs[0].get('registrations');
				var obj4 = recs[0].get('productCovered');
				var obj5 = recs[0].get('recalls');
				if (obj1 != null && obj1 !="") {
					var arrayObj1 = {
						"consumerComplaints": obj1
					};
					dataStore1.loadData(arrayObj1);
				}
				if (obj2 != null && obj2 != "") {
					var arrayObj2 = {
						"prevEir": obj2
					};
					dataStore2.loadData(arrayObj2);
				}
				if (obj3 != null && obj3 != "") {
					var arrayObj3 = {
						"registrations": obj3
					};
					dataStore3.loadData(arrayObj3);
				}
				if (obj4 != null && obj4 != "") {
					var arrayObj4 = {
						"productCovered": obj4
					};
					dataStore4.loadData(arrayObj4);
				}
				if (obj5 != null && obj5 != "") {
					var arrayObj5 = {
						"recalls": obj5
					};
					dataStore5.loadData(arrayObj5);
				}
			}
		}
	});
	alert('Before load');
	dataStore.load();
				
	var gridWidth = 600;
	var col1Width = 120;
	var col2Width = gridWidth - col1Width - 20;
	var dataGrid1 = new Ext.grid.GridPanel({
		id: 'listData2',
		padding: '0px 3px 0px 3px',
		autoHeight: true,
		width: gridWidth,
		store: dataStore1,
		columns: [
		{
			id: 'docName',
			dataIndex: 'docName',
			header: 'Document Name',
			sortable: true,
			width: 250
		}, {
			id: 'docDate',
			dataIndex: 'docDate',
			header: 'Date',
			sortable: true,
			width: 150
		}, {
			id: 'fileType',
			dataIndex: 'fileType',
			header: 'File Type / Size(KB)',
			sortable: true,
			width: 200,
			renderer: function(val, mD, rec, row, col) {
				return val + ' ( '+rec.get("fileSize") + ' kb)</a>';				
			}
		}, {
			id: 'docId',
			dataIndex: 'docId',
			hidden: true						
		}]
	});
	
	var dataGrid2 = new Ext.grid.GridPanel({
		id: 'listData2',
		padding: '0px 3px 0px 3px',
		autoHeight: true,
		width: gridWidth,
		store: dataStore2,
		columns: [
		{
			id: 'docName',
			dataIndex: 'docName',
			header: 'Document Name',
			sortable: true,
			width: 250
		}, {
			id: 'docType',
			dataIndex: 'docType',
			header: 'Document Type',
			sortable: true,
			width: 150
		}, {
			id: 'fileType',
			dataIndex: 'fileType',
			header: 'File Type / Size(KB)',
			sortable: true,
			width: 200,
			renderer: function(val, mD, rec, row, col) {
				return val + ' ( '+rec.get("fileSize") + ' kb)</a>';				
			}
		}, {
			id: 'docId',
			dataIndex: 'docId',
			hidden: true						
		}]
	});
	
	var dataGrid3 = new Ext.grid.GridPanel({
		id: 'listData3',
		padding: '0px 3px 0px 3px',
		autoHeight: true,
		width: gridWidth,
		store: dataStore3,
		columns: [
		{
			id: 'docName',
			dataIndex: 'docName',
			header: 'Document Name',
			sortable: true,
			width: 250
		}, {
			id: 'docType',
			dataIndex: 'docType',
			header: 'Document Type',
			sortable: true,
			width: 150
		}, {
			id: 'fileType',
			dataIndex: 'fileType',
			header: 'File Type / Size(KB)',
			sortable: true,
			width: 200,
			renderer: function(val, mD, rec, row, col) {
				return val + ' ( '+rec.get("fileSize") + ' kb)</a>';				
			}
		}, {
			id: 'docId',
			dataIndex: 'docId',
			hidden: true						
		}]
	});
	
	var dataGrid4 = new Ext.grid.GridPanel({
		id: 'listData4',
		padding: '0px 3px 0px 3px',
		autoHeight: true,
		width: gridWidth,
		store: dataStore4,
		columns: [
		{
			id: 'prodName',
			dataIndex: 'prodName',
			header: 'Product Name',
			sortable: true,
			width: 250
		}, {
			id: 'prodDesc',
			dataIndex: 'prodDesc',
			header: 'Description',
			sortable: true,
			width: 150
		}, {
			id: 'prodCode',
			dataIndex: 'prodCode',
			header: 'Product Code',
			sortable: true,
			width: 200						
		}]
	});
	
		var dataGrid5 = new Ext.grid.GridPanel({
		id: 'listData5',
		padding: '0px 3px 0px 3px',
		autoHeight: true,
		width: gridWidth,
		store: dataStore5,
		columns: [
		{
			id: 'eventId',
			dataIndex: 'eventId',
			header: 'Recall Event ID',
			sortable: true,
			width: 250		
		}, {
			id: 'description',
			dataIndex: 'description',
			header: 'Description',
			sortable: true,
			width: gridWidth - 250							
		}]
	});
	
	var firmInfoWindow = new Ext.Window({
		id: 'firmInfoWindow',
		width: gridWidth + 20,
		border: false,
		closable: true,
		//plain: true,
		layout: 'column',		
		items: [{
			border: false,
			width: gridWidth,
			style: 'padding: 10px; background-color: white; font: 14px arial;',
			html: 'Operation Background Information - Firm Deitails'
		} , {
			border: false,
			width: gridWidth,
			layout: 'column',
			style: 'padding-top: 10px; background-color: white',
			items: [{
				border: false,
				width: col1Width,
				cls: 'stdLabel right',
				html: 'Firm Name'
			} , {
				id: 'firmName',
				border: false,
				width: col2Width,
				style: 'padding-left: 10px;',
				html: ''
			}]			
		} , {
			border: false,
			width: gridWidth,
			style: 'padding-top: 10px; background-color: white',
			layout: 'column',
			items: [{
				border: false,
				width: col1Width,
				cls: 'stdLabel right',
				html: 'Firm FEI'
			} , {
				id: 'firmFei',
				border: false,
				width: col2Width,
				style: 'padding-left: 10px;',
				html: ''
			}]
		} , {
			border: false,
			width: gridWidth,
			style: 'padding-top: 10px; background-color: white',
			layout: 'column',
			items: [{
				border: false,
				width: col1Width,
				cls: 'stdLabel right',
				html: 'Profile Status'
			} , {
				id: 'profileStatus',
				border: false,
				width: col2Width,
				style: 'padding-left: 10px;',
				html: '&nbsp;'
			}]
		} , {
			border: false,
			width: gridWidth,
			style: 'padding-left: 10px; padding-top:30px; padding-bottom: 10px; background-color: white; font: 12px arial;',
			html: 'Consumer Complaints'
		} , {
			border: false,
			width: gridWidth,
			items: dataGrid1
		} , {
			border: false,
			width: gridWidth,
			style: 'padding-left: 10px; padding-top:30px; padding-bottom: 10px; background-color: white; font: 12px arial;',
			html: 'Previous Establishment Inspection Reports(EIR)'
		} , {
			border: false,
			width: gridWidth,
			items: dataGrid2
		} , {
			border: false,
			width: gridWidth,
			style: 'padding-left: 10px; padding-top:30px; padding-bottom: 10px; background-color: white; font: 12px arial;',
			html: 'Firm Registrations'
		} , {
			border: false,
			width: gridWidth,
			items: dataGrid3
		} , {
			xtype:'fieldset',
			border:true,
			title: 'Historical Firm Data',
			width: gridWidth + 10,
			style: 'padding-left: 10px; padding-top:30px; padding-bottom: 10px; background-color: white;'	          
        } , {
			border: false,
			width: gridWidth,
			style: 'padding-left: 10px; padding-top:30px; padding-bottom: 10px; background-color: white; font: 12px arial;',
			html: 'Products Covered'
		} , {
			border: false,
			width: gridWidth,
			items: dataGrid4 
		} , {
			border: false,
			width: gridWidth,
			style: 'padding-left: 10px; padding-top:30px; padding-bottom: 10px; background-color: white; font: 12px arial;',
			html: 'Recalls'
		} , {
			border: false,
			width: gridWidth,
			items: dataGrid5 
		}]						
	});
	
	firmInfoWindow.show();
}

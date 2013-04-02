Ext.namespace('marcs');

marcs.start = function() {
	var recallStore = new Ext.data.JsonStore({
		storeId: 'recallStore',
		url: 'json/recallSearchList.sjson',
		root: 'recalls',
		fields: [
			'eventId',			
			'district',
			'center',
			'districtAwareDate',
			'firmName',
			'product',
			'publicReason',
			'classification',
			'racStartDate'
		]
	});
	
	var browserSize = marcs.util.display.getBrowserSize(true);
	var recallSM = new marcs.extensions.RadioSelectionModel({});	
	var searchPanel = new Ext.Panel({
		renderTo: 'recallSearch',
		border: false,
		layout: 'column',
		items: [{
			id: 'searchFormPanel',
			xtype: 'searchpanel',
			border: false,
			columnWidth: 0.17,
			padding: '5px',
			// Account for the size of the headers
			height: browserSize.height - 76
		}, {
			id: 'searchResultsPanel',
			border: false,
			columnWidth: 0.83,
			hidden: true,
			forceLayout: true,
			style: 'padding-top: 5px;',
			layout: 'column',
			items: [{
				cls: 'stdTitle',
				border: false,
				padding: '0px 0px 0px 5px',
				width: (browserSize.width * 0.83) - 300,
				html: 'Recall Search Results'
			}, {
				xtype: 'button',
				minWidth: 80,
				text: '<b>Recall Audit Check</b>',
				handler: function() {
					// Radio, so only ever one result selected.
					var selected = Ext.getCmp('searchResultsGrid').getSelectionModel().getSelected();
					
					if (selected) {
						marcs.util.misc.redirectTo('recallDashboard.jsp?eventId=' + selected.get('eventId'));
					}
					else {
						alert('You must select a recall.');
					}
				}
			}, {
				xtype: 'button',
				minWidth: 80,
				style: 'padding-left: 5px;',
				text: '<b>Cancel</b>',
				handler: function() {
					recallStore.removeAll();
				}
			}, {
				id: 'searchResultsGrid',
				xtype: 'grid',
				border: false,
				padding: '5px 0px 0px 5px',
				height: 300,
				width: (browserSize.width * 0.83) - 33,
				store: recallStore,
				selModel: recallSM,
				columns: [
					recallSM,
				{
					id: 'eventId',
					dataIndex: 'eventId',
					header: 'Recall Event Id',
					sortable: true,
					width: 70,
					renderer: function(val) {
						//return '<a class="stdOnClickLink" href="recallDashboard.jsp?eventId=' + val + '">' + val + '</a>';
						return '<a class="stdOnClickLink" href="moreRecallInformation.html">' + val + '</a>';
					}				
				}, {
					id: 'district',
					dataIndex: 'district',
					header: 'District',
					sortable: true,
					width: 70
				}, {
					id: 'center',
					dataIndex: 'center',
					header: 'Center',
					sortable: true,
					width: 70
				}, {
					id: 'districtAwareDate',
					dataIndex: 'districtAwareDate',
					header: 'District Awareness Date',
					sortable: true,
					width: 100
				}, {
					id: 'firmName',
					dataIndex: 'firmName',
					header: 'Recalling Firm/ Manufacturer',
					sortable: true,
					width: 100
				}, {
					id: 'product',
					dataIndex: 'product',
					header: 'Product Description',
					sortable: true,
					width: 100
				}, {
					id: 'publicReason',
					dataIndex: 'publicReason',
					header: 'Public Reason for Recall',
					sortable: true,
					// width of the browser - checkbox - (width we're taking up with the grid) - (grid border adj) - (the width of each column)
					width: (browserSize.width * 0.83) - 20 - 40 - 70 - 70 - 70 - 100 - 100 - 100 - 75 - 100
				}, {
					id: 'classification',
					dataIndex: 'classification',
					header: 'Classification',
					sortable: true,
					width: 75
				}, {
					id: 'racStartDate',
					dataIndex: 'racStartDate',
					header: 'Date RAC started',
					sortable: true,
					width: 100
				}]
			}]
		}]
	});
}

Ext.onReady(marcs.start, this);

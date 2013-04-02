Ext.namespace('tcga.uuid.search');

tcga.uuid.search.results = function(browseStore){
    return {
		render: function() {
			searchResultsPanel.render('uuidSearchResults');
		},
		
		hide: function() {
			Ext.get('uuidSearchResults').setDisplayed('none');
		},
		
		show: function() {
			Ext.get('uuidSearchResults').show();
		},
		
		goTo: function(rec) {
			Ext.History.add('Search', true);
		},
		
		init: function(browseStore) {
		   var combo = new Ext.form.ComboBox({
		       name: 'perpage',
		       width: 50,
		       store: new Ext.data.ArrayStore({
		       fields: ['id'],
		       data: [['25'],['50'],['75'], ['100'], ['200'], ['500'], ['1000']]}),
		       mode: 'local',
		       value: '25',
		       listWidth: 40,
		       triggerAction: 'all',
		       displayField: 'id',
		       valueField: 'id',
		       editable: false,
		       forceSelection: true
		   });
		
		   //Create exports Toolbar
		   var exports = new Ext.Toolbar({
			  items: [{
			       menu: [
		              {text: 'Excel',iconCls: 'icon-xl',handler: this.exportUrlxl},
		              {text: 'CSV',iconCls: 'icon-txt',handler: this.exportUrlcsv},
		              {text: 'Tab-delimited',iconCls: 'icon-txt',handler: this.exportUrltab}
		           ],
		           text: 'Export Data',
		           iconCls: 'icon-grid'
			  }]
		   });
		
		   // paging bar
		    var paging = new Ext.PagingToolbar({
		        id: 'resultsPagingTb',
		        pageSize: 25,
		        store: browseStore,
		        displayInfo: true,
		        displayMsg: 'Displaying uuid {0} - {1} of {2}',
		        emptyMsg: "No uuids to display",
		        items: ['-','Per Page ',combo]
		    });
		
		    combo.on('select', function(combo, record) {
		        paging.pageSize = parseInt(record.get('id'), 10);
		        paging.doLoad(paging.cursor);
		    }, this);
		    
		    var uuidResultsGrid = new Ext.grid.GridPanel({
				id: 'uuidResultsGrid',
				renderTo: 'uuidSearchResults',
				loadMask: true,
				stripeRows: true,
				singleSelect: true,
				emptyText: 'Please search to display UUIDs',
				height: 344,
				width: 850,
				title: 'UUID Results',
		        tbar: exports,
				bbar: paging,
				store: browseStore,
				enableColumnHide: true,
				columns: [{
					header: 'UUID',
					width: 250,
					sortable: true,
					dataIndex: 'uuid',
					renderer: function(val, mD, rec, row) {
						return '<a class="hand" onclick="tcga.uuid.browser.goTo(Ext.getCmp(\'uuidResultsGrid\').getStore().getAt(' + row + '))">' + val + '</a>';
					}
				}, {
					id: 'barcode',
					header: 'Barcode',
					width: 115,
					sortable: true,
					dataIndex: 'barcode'
				}, {
					header: 'Element Type',
					width: 110,
					sortable: true,
					dataIndex: 'uuidType'
				}, {
					header: 'Disease',
					width: 100,
					sortable: true,
					dataIndex: 'disease'
				}, {
					id: 'tissueSourceSite',
					header: 'Tissue Source Site',
					width: 120,
					sortable: true,
					dataIndex: 'center'
				}, {
					id: 'participantNumber',
					header: 'Participant Number',
					width: 100,
					sortable: true,
					dataIndex: 'participant'
				}, {
					header: 'Sample Type',
					hidden: true,
					width: 80,
					sortable: true,
					dataIndex: 'sampleType'
				}, {
					header: 'Vial',
					hidden: true,
					width: 40,
					sortable: true,
					dataIndex: 'vial'
				}],
				listeners: {
					rowdblclick: function(grid, row) {
						tcga.uuid.browser.goTo(grid.getStore().getAt(row));
					}
				}
		   });
		    
		    uuidResultsGrid.getTopToolbar().add('-',
			   {iconCls: 'x-cols-icon',text: 'Show/Hide Columns', menu: uuidResultsGrid.getView().colMenu},'->',
			   {text: 'Help',iconCls: 'icon-help',handler: function(){
			       showHelp('xx','xx');
		   }});
		
		
		   var searchResultsPanel = new Ext.Panel({
		      id: 'uuidResultsPanel',
		      renderTo: 'uuidSearchResults',
				border: false,
				items: [
					uuidResultsGrid
				]
			});
		}
    }
}();

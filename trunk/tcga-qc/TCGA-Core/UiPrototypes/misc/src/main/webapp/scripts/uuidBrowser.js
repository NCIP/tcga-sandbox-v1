Ext.namespace('tcga.uuid');

tcga.uuid.browser = function(){
	var currentHighlight = [];

	var browseIds = {
		Participant: {
			title: 'participantTitle',
			grid: 'participantGrid',
			next: ['Sample']
		},
		Sample: {
			title: 'sampleTitle',
			grid: 'sampleGrid',
			next: ['Portion']
		},
		Portion: {
			title: 'portionTitle',
			grid: 'portionGrid',
			next: ['Analyte', 'Slide']
		},
		Analyte: {
			title: 'analyteTitle',
			grid: 'analyteGrid',
			next: ['Aliquot']
		},
		Aliquot: {
			title: 'aliquotTitle',
			grid: 'aliquotGrid',
			next: null
		},
		Slide: {
			title: 'slideTitle',
			grid: 'slideGrid',
			next: null
		}
	};

	var highlightGrid = function(titleId, gridBodyId) {
		if (currentHighlight.length != 0) {
			Ext.getCmp(currentHighlight[0]).body.removeClass('highlightGridTitleBackground');
			Ext.getCmp(currentHighlight[0]).body.addClass('lowlightGridTitleBackground');
		}
		
		currentHighlight = [titleId, gridBodyId];
		Ext.getCmp(titleId).body.removeClass('lowlightGridTitleBackground');
		Ext.getCmp(titleId).body.addClass('highlightGridTitleBackground');
	}

	var selectUuidRow = function(grid, uuid, uuidType) {
		uuidParticipantGrid.getSelectionModel().clearSelections();
		uuidSampleGrid.getSelectionModel().clearSelections();
		uuidPortionGrid.getSelectionModel().clearSelections();
		uuidAnalyteGrid.getSelectionModel().clearSelections();
		uuidAliquotGrid.getSelectionModel().clearSelections();
		uuidSlideGrid.getSelectionModel().clearSelections();

		var row = grid.getStore().find('uuid', uuid);
		
		if (row != null) {
			grid.getSelectionModel().selectRow(row);
		}
		
		// Now, if this is not a Participant, select the parents too.  This is completely fake right now.
		uuidParticipantGrid.getSelectionModel().selectRow(0);
		if (uuidType == 'Participant') {
			return;
		}
		uuidSampleGrid.getSelectionModel().selectRow(1);
		if (uuidType == 'Sample') {
			return;
		}
		uuidPortionGrid.getSelectionModel().selectRow(3);
		if (uuidType == 'Portion' || uuidType == 'Slide') {
			return;
		}
		uuidAnalyteGrid.getSelectionModel().selectRow(0);
	};
	
	var storeFields = [
    	'uuid', 'parent', 'barcode', 'uuidType', 'disease', 'center', 'participant',
    	'sampleType', 'vial', 'portion', 'analyte', 'aliquot', 'slide'
    ];

	
	var browseStore = new Ext.data.JsonStore({
		storeId: 'uuidBrowseResultsStore',
		url : 'json/uuidBrowse.sjson',
		root : 'uuid',
		fields: storeFields,
		listeners: {
			load: function(store, recs, opts) {
				participantStore.removeAll();
				sampleStore.removeAll();
				portionStore.removeAll();
				analyteStore.removeAll();
				aliquotStore.removeAll();
				slideStore.removeAll();
				participantStore.removeAll();

				store.each(function(rec) {
					switch(rec.get('uuidType')) {
						case 'Participant':
							participantStore.add(rec);
							break;
						case 'Sample':
							sampleStore.add(rec);
							break;
						case 'Portion':
							portionStore.add(rec);
							break;
						case 'Analyte':
							analyteStore.add(rec);
							break;
						case 'Slide':
							slideStore.add(rec);
							break;
						case 'Aliquot':
							aliquotStore.add(rec);
					}
				});

				Ext.get(browseIds.Portion.title).hide();
				uuidPortionGrid.hide();
				Ext.get(browseIds.Analyte.title).hide();
				uuidAnalyteGrid.hide();
				Ext.get(browseIds.Aliquot.title).hide();
				uuidAliquotGrid.hide();
				Ext.get(browseIds.Slide.title).hide();
				uuidSlideGrid.hide();

				switch(opts.uuidType) {
					case 'Participant':
						tcga.uuid.navigator.setActiveNode('Participant');
						selectUuidRow(uuidParticipantGrid, opts.uuid, 'Participant');

						highlightGrid(browseIds.Participant.title, browseIds.Participant.grid);
						break;
					case 'Sample':
						tcga.uuid.navigator.setActiveNode('Sample');
						selectUuidRow(uuidSampleGrid, opts.uuid, 'Sample');

						Ext.get(browseIds.Portion.title).show();
						uuidPortionGrid.show();

						highlightGrid(browseIds.Sample.title, browseIds.Sample.grid);
						break;
					case 'Portion':
						tcga.uuid.navigator.setActiveNode('Portion');
						selectUuidRow(uuidPortionGrid, opts.uuid, 'Portion');

						Ext.get(browseIds.Portion.title).show();
						uuidPortionGrid.show();
						Ext.get(browseIds.Analyte.title).show();
						uuidAnalyteGrid.show();
						Ext.get(browseIds.Slide.title).show();
						uuidSlideGrid.show();

						highlightGrid(browseIds.Portion.title, browseIds.Portion.grid);
						break;
					case 'Analyte':
						tcga.uuid.navigator.setActiveNode('Analyte');
						selectUuidRow(uuidAnalyteGrid, opts.uuid, 'Analyte');

						Ext.get(browseIds.Portion.title).show();
						uuidPortionGrid.show();
						Ext.get(browseIds.Analyte.title).show();
						uuidAnalyteGrid.show();

						highlightGrid(browseIds.Analyte.title, browseIds.Analyte.grid);
						break;
					case 'Slide':
						tcga.uuid.navigator.setActiveNode('Slide');
						selectUuidRow(uuidSlideGrid, opts.uuid, 'Slide');

						Ext.get(browseIds.Portion.title).show();
						uuidPortionGrid.show();
						Ext.get(browseIds.Slide.title).show();
						uuidSlideGrid.show();

						highlightGrid(browseIds.Slide.title, browseIds.Slide.grid);
						break;
					case 'Aliquot':
						tcga.uuid.navigator.setActiveNode('Aliquot');
						selectUuidRow(uuidAliquotGrid, opts.uuid, 'Aliquot');

						Ext.get(browseIds.Portion.title).show();
						uuidPortionGrid.show();
						Ext.get(browseIds.Analyte.title).show();
						uuidAnalyteGrid.show();
						Ext.get(browseIds.Aliquot.title).show();
						uuidAliquotGrid.show();
						Ext.get(browseIds.Slide.title).show();
						uuidSlideGrid.show();

						highlightGrid(browseIds.Aliquot.title, browseIds.Aliquot.grid);
				}
			}
		}
	});
	
	var storeConfigs = {
		root : 'uuid',
		fields: storeFields
	};
	
	var participantStore = new Ext.data.Store(storeConfigs);
	var sampleStore = new Ext.data.Store(storeConfigs);
	var portionStore = new Ext.data.Store(storeConfigs);
	var analyteStore = new Ext.data.Store(storeConfigs);
	var slideStore = new Ext.data.Store(storeConfigs);
	var aliquotStore = new Ext.data.Store(storeConfigs);

	var uuidParticipantGrid = new Ext.grid.GridPanel({
	   id: browseIds.Participant.grid,
	   forceFit: true,
	   loadMask: true,
	   stripeRows: true,
	   height:74,
	   width:585,
	   frame:false,
		border: true,
	   store: participantStore,
	   enableColumnHide: false,
	   columns: [{
			header: 'UUID',
			width: 220,
			sortable: true,
			dataIndex: 'uuid'
		}, {
			header: 'Barcode',
			width: 170,
			sortable: true,
			dataIndex: 'barcode'
		}, {
			id: 'participantNumber',
			header: 'Participant Number',
			width: 60,
			sortable: true,
			dataIndex: 'participant'
		}, {
			header: 'Disease',
			width: 60,
			sortable: true,
			dataIndex: 'disease'
		}, {
			id: 'tissueSourceSite',
			header: 'Tissue Source Site',
			width: 60,
			sortable: true,
			dataIndex: 'center'
		}]
	});

	var uuidSampleGrid = new Ext.grid.GridPanel({
	   id: browseIds.Sample.grid,
	   forceFit: true,
	   loadMask: true,
	   stripeRows: true,
	   height: 90,
	   width: 585,
		border: true,
	   store: sampleStore,
	   enableColumnHide: false,
	   columns: [{
			header: 'UUID',
			width: 225,
			sortable: true,
			dataIndex: 'uuid',
			renderer: function(val) {
				return '<a class="hand" onclick="tcga.uuid.browser.activateBrowseNode(\'Sample\', \'' + val + '\')">' + val + '</a>';
			}
		}, {
			header: 'Barcode',
			width: 175,
			sortable: true,
			dataIndex: 'barcode'
		}, {
			header: 'Sample Type',
			width: 80,
			sortable: true,
			dataIndex: 'sampleType'
		}, {
			header: 'Vial',
			width: 40,
			sortable: true,
			dataIndex: 'vial'
		}]
	});

	var uuidPortionGrid = new Ext.grid.GridPanel({
	   id: browseIds.Portion.grid,
	   forceFit: true,
	   loadMask: true,
	   stripeRows: true,
	   height:174,
	   width:585,
		border: true,
	   store: portionStore,
	   enableColumnHide: false,
	   columns: [{
			header: 'UUID',
			width: 225,
			sortable: true,
			dataIndex: 'uuid',
			renderer: function(val) {
				return '<a class="hand" onclick="tcga.uuid.browser.activateBrowseNode(\'Portion\', \'' + val + '\')">' + val + '</a>';
			}
		}, {
			header: 'Barcode',
			width: 175,
			sortable: true,
			dataIndex: 'barcode'
		}, {
			header: 'Portion Code',
			width: 80,
			sortable: true,
			dataIndex: 'portion'
		}]
	});

	var uuidAnalyteGrid = new Ext.grid.GridPanel({
	   id: browseIds.Analyte.grid,
	   forceFit: true,
	   loadMask: true,
	   stripeRows: true,
	   height: 120,
	   width: 289,
		border: true,
	   store: analyteStore,
	   enableColumnHide: false,
	   columns: [{
			id: 'uuid',
			header: 'UUID',
			width: 115,
			sortable: true,
			dataIndex: 'uuid',
			renderer: function(val) {
				return '<a class="hand" onclick="tcga.uuid.browser.activateBrowseNode(\'Analyte\', \'' + val + '\')">' + val + '</a>';
			}
		}, {
			id: 'barcode',
			header: 'Barcode',
			width: 115,
			sortable: true,
			dataIndex: 'barcode'
		}, {
			header: 'Analyte',
			width: 50,
			sortable: true,
			dataIndex: 'analyte'
		}]
	});

	var uuidAliquotGrid = new Ext.grid.GridPanel({
	   id: browseIds.Aliquot.grid,
	   forceFit: true,
	   loadMask: true,
	   stripeRows: true,
	   height: 274,
	   width: 585,
		border: true,
	   store: aliquotStore,
	   enableColumnHide: false,
	   columns: [{
			id: 'uuid',
			header: 'UUID',
			width: 135,
			sortable: true,
			dataIndex: 'uuid'
		}, {
			id: 'barcode',
			header: 'Barcode',
			width: 115,
			sortable: true,
			dataIndex: 'barcode'
		}, {
			header: 'Plate ID',
			width: 50,
			sortable: true,
			dataIndex: 'aliquot',
			renderer: function(val) {
				return val.plateId;
			}
		}, {
			id: 'recCenter',
			header: 'Receiving Center',
			width: 60,
			sortable: true,
			dataIndex: 'aliquot',
			renderer: function(val, mD, rec) {
				return '<span ext:qtip="' + val.rec_name + '">' + val.rec_code + '</span>';
			}
		}, {
			id: 'platform',
			header: 'Platform',
			width: 215,
			sortable: true,
			dataIndex: 'aliquot',
			renderer: function(val, mD, rec) {
				return '<span ext:qtip="' + val.platform_name + '">' + val.platform_code + '</span>';
			}
		}]
	});

	var uuidSlideGrid = new Ext.grid.GridPanel({
	   id: browseIds.Slide.grid,
	   forceFit: true,
	   loadMask: true,
	   stripeRows: true,
	   height: 120,
	   width: 289,
		border: true,
	   store: slideStore,
	   enableColumnHide: false,
	   columns: [{
		    id: 'uuid',
			header: 'UUID',
			width: 135,
			sortable: true,
			dataIndex: 'uuid'
		}, {
		    id: 'barcode',
			header: 'Barcode',
			width: 115,
			sortable: true,
			dataIndex: 'barcode'
		}]
	});

	var browserPanel = new Ext.Panel({
		id: 'browsePanel',
		border: false,
		hideBorders: true,
		items: [{
			id: browseIds.Participant.title,
			cls: 'stdLabel',
			html: 'Participant UUID'
		},
			uuidParticipantGrid,
		{
			id: browseIds.Sample.title,
			style: 'margin-top: 10px;',
			cls: 'stdLabel',
			html: 'Sample UUIDs'
		},
			uuidSampleGrid,
		{
			id: browseIds.Portion.title,
			style: 'margin-top: 10px;',
			cls: 'stdLabel',
			html: 'Portion UUIDs'
		},
			uuidPortionGrid,
		{
			border: false,
			layout: 'column',
			hideBorders: true,
			items: [{
				hideBorders: true,
				items: [{
					id: browseIds.Analyte.title,
					style: 'margin-top: 10px;',
					cls: 'stdLabel',
					html: 'Analyte UUIDs'
				},
					uuidAnalyteGrid
				]
			}, {
				hideBorders: true,
				style: 'margin-left: 5px;',
				items: [{
					id: browseIds.Slide.title,
					style: 'margin-top: 10px;',
					cls: 'stdLabel',
					html: 'Slide UUIDs'
				},
					uuidSlideGrid
				]
			}]
		}, {
			id: browseIds.Aliquot.title,
			style: 'margin-top: 10px;',
			cls: 'stdLabel',
			html: 'Aliquot UUIDs'
		},
			uuidAliquotGrid
		]
	});
	
	return {
		render: function() {
			browserPanel.render('uuidBrowseResults');
			for (var browseId in browseIds) {
				Ext.getCmp(browseIds[browseId].title).body.addClass('lowlightGridTitleBackground');
				tcga.uuid.titleAdjust(browseIds[browseId].title);
			}
		},
		
		hide: function() {
			Ext.get('uuidBrowse').setDisplayed('none');
		},
		
		show: function() {
			Ext.get('uuidBrowse').show();
		},
		
		goTo: function(rec) {
			Ext.History.add('Browse', true);

			browseStore.load({
				uuid: rec.get('uuid'),
				uuidType: rec.get('uuidType')
			});
		},
		
		activateBrowseNode: function(node, val) {
			tcga.uuid.navigator.setActiveNode(node);

			if (node == 'Participant') {
				Ext.get(browseIds.Portion.title).hide();
				uuidPortionGrid.getSelectionModel().clearSelections();
				uuidPortionGrid.hide();
				Ext.get(browseIds.Analyte.title).hide();
				uuidAnalyteGrid.getSelectionModel().clearSelections();
				uuidAnalyteGrid.hide();
				Ext.get(browseIds.Aliquot.title).hide();
				uuidAliquotGrid.getSelectionModel().clearSelections();
				uuidAliquotGrid.hide();
				Ext.get(browseIds.Slide.title).hide();
				uuidSlideGrid.getSelectionModel().clearSelections();
				uuidSlideGrid.hide();
			}
			else if (node == 'Sample') {
				uuidPortionGrid.getSelectionModel().clearSelections();
				Ext.get(browseIds.Analyte.title).hide();
				uuidAnalyteGrid.getSelectionModel().clearSelections();
				uuidAnalyteGrid.hide();
				Ext.get(browseIds.Aliquot.title).hide();
				uuidAliquotGrid.getSelectionModel().clearSelections();
				uuidAliquotGrid.hide();
				Ext.get(browseIds.Slide.title).hide();
				uuidSlideGrid.getSelectionModel().clearSelections();
				uuidSlideGrid.hide();
			}
			else if (node == 'Portion') {
				Ext.get(browseIds.Aliquot.title).hide();
				uuidAliquotGrid.getSelectionModel().clearSelections();
				uuidAliquotGrid.hide();

				uuidAnalyteGrid.getSelectionModel().clearSelections();
			}
			else if (node == 'Analyte') {
				uuidSlideGrid.getSelectionModel().clearSelections();
			}

			if (browseIds[node].next) {
				for (var ndx = 0;ndx < browseIds[node].next.length;ndx++) {
					Ext.get(browseIds[browseIds[node].next[ndx]].title).show();
					Ext.getCmp(browseIds[browseIds[node].next[ndx]].grid).show();
				}
			}
			highlightGrid(browseIds[node].title, browseIds[node].grid);
		}
	}
}();

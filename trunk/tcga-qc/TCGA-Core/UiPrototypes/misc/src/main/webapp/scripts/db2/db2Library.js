Ext.namespace('tcga.db2');

tcga.db2.library = function() {
	var libraryDataTypes = [{
		type: 'patientClass',
		title: 'Saved Patient Classes',
		select: false
	}, {
		type: 'molecularSet',
		title: 'Saved Molecular Sets',
		select: false
	}, {
		type: 'query',
		title: 'Saved Queries',
		select: true
	}, {
		type: 'tcgaQuery',
		title: 'TCGA Queries',
		select: true
	}];
	
	return {
		gotoPage: function() {
			// Add the build token to the history
			tcga.db2.history.addToken('library');
		},
		
		displaySavedQueries: function(libraryStore, libraryRecs) {
			var libraryBoxes = [];
			
			for (var ndx0 = 0;ndx0 < libraryDataTypes.length;ndx0++) {
				var libraryBoxDisplay = [];
				libraryStore.filter('type', libraryDataTypes[ndx0].type);
				var typeCount = libraryStore.getCount();
				
				libraryBoxDisplay.push({
					html: '<div class="selectorTitle">' + libraryDataTypes[ndx0].title + '</div><div class="openCloseSelectorToggle" onclick="tcga.db2.selectors.toggleSelectorBox(this, \'libraryData' + libraryDataTypes[ndx0].type + '\')">close</div>'
				});

				for (var ndx1 = 0;ndx1 < typeCount;ndx1++) {
					var libraryRec = libraryStore.getAt(ndx1);
					
					var libraryEntryHtml = '<div class="queryLibraryObjectName">' + libraryRec.get('name') + '</div>';

					var libraryPrivs = libraryRec.get('privs');
					var libraryPrivsHtml = '';
					if (libraryDataTypes[ndx0].select) {
						libraryPrivsHtml += '<a href="#">select</a>';
					}
					if (libraryPrivs.view == 'true') {
						libraryPrivsHtml += (libraryDataTypes[ndx0].select?' | ':'');
						libraryPrivsHtml += '<a href="#">view</a>';
					}
					if (libraryPrivs.edit == 'true') {
						libraryPrivsHtml += (libraryPrivs.view?' | ':'');
						libraryPrivsHtml += '<a href="#">edit</a>';
					}
					if (libraryPrivs.remove == 'true') {
						libraryPrivsHtml += (libraryPrivs.edit?' | ':'');
						libraryPrivsHtml += '<a href="#">delete</a>';
					}
					
					libraryBoxDisplay.push({
						html: libraryEntryHtml + libraryPrivsHtml
					});
				}
			
				libraryBoxes.push({
					id: 'libraryData' + libraryDataTypes[ndx0].type,
					xtype: 'panel',
					cls: 'selectorBox',
					border: false,
					defaults: {
						border: false,
					},
					items: libraryBoxDisplay
				});
				
				libraryStore.clearFilter();
			}

			new Ext.Panel({
				id: 'libraryData',
				renderTo: 'savedQueryLibraryData',
				border: false,
				items: libraryBoxes
			});
		},
		
		reset: function(values) {
			Ext.get('queryLibraryButton').show();
		},

		init: function(values) {
			var libraryStore = new Ext.data.JsonStore({
//				url: 'json/library.sjson',
				storeId: 'libraryStore',
				root: 'libraryObjects',
				idProperty: 'id',
				autoLoad: true,
				fields: [
					'id',
					'type',
					'name',
					'privs'
				],
				listeners: {
					load: this.displaySavedQueries
				},
				data: tcga.db2.localData.library
			});
			
			Ext.get('queryLibraryButton').hide();
		}
	}
}();

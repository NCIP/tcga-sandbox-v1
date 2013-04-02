Ext.namespace('tcga.db2');

tcga.db2.interactivePatient = function() {
	var updateDisease = function(query) {
		Ext.get('interactiveSelectedDisease').update(query?query.get('disease').name:null);
	};
	
	return {
		pageCreated: false,
		
		loadPatientList: function(store, disease, selectorRecs) {
			var loadDataToSelectors = function(store) {
				var patientCount = store.getCount();
				
				// Load the patient count into the patient numbers in the selectors
				for (var ndx0 = 0;ndx0 < selectorRecs.length;ndx0++) {
					var parameterRecs = selectorRecs[ndx0].get('parameters');
					for (var ndx1 = 0;ndx1 < parameterRecs.length;ndx1++) {
						Ext.get('results' + parameterRecs[ndx1].name).update(patientCount);
					}
				}
			}

			store.addListener('load', loadDataToSelectors);
		},

		createPatientFilter: function() {
			var queryClass = 'patientClass';

			var currQuery = tcga.db2.query.storage.getCurrQuery();
			var currValue = currQuery.patientClass.get('name');
		
			var processKeyEvents = function(tf) {
//				currQuery.updateQueryClass(tf.queryClass, 'name', tf.getValue());
		
				tcga.db2.queryWriter.start();
			}

			var patientClassParameterStore = new Ext.data.JsonStore({
		//		url:'json/patientClassParameters.sjson',
				storeId: 'patientClassParameterStore',
				root:'patientClassParameters',
				idProperty:'id',
				fields: [
					'id',
					'name',
					'parameters'
				],
				autoLoad: true,
				listeners: {
					load: {
						fn: function(store, recs) {
							var patientStore = Ext.StoreMgr.get('patientsInteractiveStore');

							// Now load the patient list - do this now in order to be able to put the count
							//		into the patient
							this.loadPatientList(patientStore, currQuery.get('disease'), recs);
							
							// Create the selectors
							var firstSelector = tcga.db2.selectors.display('patientClassInteractive', queryClass, recs, true, patientStore);

							// Now open up the first selector box
							tcga.db2.selectors.toggleSelectorBox(Ext.get('selectorBox' + firstSelector).dom, firstSelector);

							// Now load the patient store - normally - since we're using data, we need to just fire the load event
//							patientStore.load();
							patientStore.fireEvent('load', patientStore, patientStore.getRange(), {});
						},
						scope: this
					}
				},
				data: tcga.db2.localData.patientClassParameters
			});
		},

		gotoPage: function() {
			// Add the build token to the history
			tcga.db2.history.addToken('interactivePatient');
		},
		
		reset: function(values) {
		},

		init: function(values) {
			if (this.pageCreated) {
				return;
			}
			
			var newQuery = new tcga.db2.query.query();
			newQuery.update('disease', {
				name: 'GBM',
				description: 'Glioblastoma Multiforme'
			});
			
			tcga.db2.query.storage.reset();
			tcga.db2.query.storage.addQuery(newQuery);
			
			// Update the disease in the buildQueryIdentification div
			updateDisease(tcga.db2.query.storage.getCurrQuery());
		
			// Now start the queryWriter to put the nascent query into the right gutter box
			tcga.db2.queryWriter.start();
		
			if (!loadMask) {
				var loadMask = new Ext.LoadMask(Ext.getBody(), {msg:"Loading parameters..."});
			}
			loadMask.show();
			this.createPatientFilter();
			loadMask.hide();

			this.pageCreated = true;
		}
	}
}();

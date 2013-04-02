Ext.namespace('tcga.db2');

tcga.db2.preferences = function() {
	return {
		displayPreferences: function(store, recs) {
			var preferences = recs[0];

			if (!Ext.getCmp('preferencesDiseaseCombo')) {
				var diseaseStore = new Ext.data.JsonStore({
			//		url:'json/diseases.sjson',
					storeId:'diseasePreferenceStore',
					root:'diseases',
					idProperty:'tumorId',
					autoLoad: true,
					fields: [
						'tumorId',
						'tumorName',
						'tumorDescription'
					],
					data: tcga.db2.localData.diseases
				});
				
				new tcga.extensions.comboPlus({
					id: 'preferencesDiseaseCombo',
					renderTo: 'savedDiseasePreferenceData',
					store: diseaseStore,
					mode: 'local',
					triggerAction: 'all',
					displayField:'tumorDescription',
					valueField : 'tumorName',
					emptyText: 'Select a disease',
					tplDisplay: '{tumorDescription} ({tumorName})',
					border: false,
					autoHeight: true,
					style: 'margin-bottom: 10px;',
					width: 270,
					value: preferences.get('disease').name
				});
				
				new Ext.form.RadioGroup({
					id: 'preferencesScale',
					renderTo: 'savedScalePreferenceData',
					items: [{
						name: 'preferencesLog2',
						width: 70,
						value: 'log2',
						fieldLabel: 'Log2',
						checked: (preferences.get('scale') == 'log2'?true:false)
					}, {
						name: 'preferencesFold',
						width: 70,
						value: 'fold',
						fieldLabel: 'Fold',
						checked: (preferences.get('scale') == 'fold'?true:false)
					}],
					listeners: {
						change: function(cbGroup, checkedList) {
						}
					}
				});
				
				new Ext.form.RadioGroup({
					id: 'preferencesView',
					renderTo: 'savedViewPreferenceData',
					items: [{
						name: 'preferencesLog2',
						width: 70,
						value: 'table',
						fieldLabel: 'Table View',
						checked: (preferences.get('resultsView') == 'table'?true:false)
					}, {
						name: 'preferencesFold',
						width: 70,
						value: 'image',
						fieldLabel: 'Image View',
						checked: (preferences.get('resultsView') == 'image'?true:false)
					}],
					listeners: {
						change: function(cbGroup, checkedList) {
						}
					}
				});
				
				new Ext.form.ComboBox({
					id: 'preferencesResultsNum',
					renderTo: 'savedResultsPreferenceData',
					width: 70,
					store: new Ext.data.ArrayStore({
					fields: ['id'],
					data: tcga.db2.localData.paging}),
					mode: 'local',
					value: preferences.get('resultsNum'),
					listWidth: 70,
					triggerAction: 'all',
					displayField: 'id',
					valueField: 'id',
					editable: false,
					forceSelection: true
				});
			}
			else {
				// Just set the preference values
				Ext.getCmp('preferencesDiseaseCombo').setValue(preferences.get('disease').name);
				Ext.getCmp('preferencesScale').setValue(preferences.get('scale'), true);
				Ext.getCmp('preferencesView').setValue(preferences.get('resultsView'), true);
				Ext.getCmp('preferencesResultsNum').setValue(preferences.get('resultsNum'));
			}
			
		},

		save: function() {
			new tcga.extensions.window({
		      id: 'savePreferencesWindow',
		      modal: true,
				title: 'Preferences Saved',
		      width: 350,
		      items: [{
					border: false,
					width: 340,
			      layout:'column',
					items: [{
						border: false,
						style: 'margin: 5px 0 5px 10px;font-weight: bold;',
						width: 350,
						html: 'Your preferences have been saved.'
					}]
				}],
				buttons: [{
					text: 'OK',
					minWidth: 100,
					style: 'margin-left: 10px;margin-bottom: 5px;',
					handler: function() {
						Ext.getCmp('savePreferencesWindow').close();
					}
				}]
		  }).show();
		},
		
		gotoPage: function() {
			// Add the build token to the history
			tcga.db2.history.addToken('preferences');
		},
		
		reset: function(values) {
			Ext.get('queryPreferencesButton').show();
		},

		init: function(values) {
			var preferencesStore = new Ext.data.JsonStore({
//				url: 'json/preferences.sjson',
				storeId: 'preferencesStore',
				root: 'preferences',
				idProperty: 'id',
				autoLoad: true,
				fields: [
					'id',
					'disease',
					'scale',
					'resultsView',
					'resultsNum'
				],
				listeners: {
					load: this.displayPreferences
				},
				data: tcga.db2.localData.preferences
			});
			
			var preferencesButton = Ext.get('queryPreferencesButton');
			preferencesButton.setVisibilityMode(Ext.Element.DISPLAY);
			preferencesButton.hide();
		}
	}
}();

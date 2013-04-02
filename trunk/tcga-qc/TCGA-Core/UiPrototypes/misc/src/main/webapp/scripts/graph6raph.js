Ext.namespace('tcga.riphraph');

/*
 * An example of calling the graph drawing routine in a typical ExtJS javascript file.  In this
 * case, the javascript creates a filter for the graph that reloads the graph image based on the
 * selection from the dropdown.  I have not put in all of the selections, just a couple couple as 
 * examples.
 * 
 * Note that the filter dropdown uses the loadUrl function of the nodeDataStore to load a new
 * graph data file in place of the current one.  It also uses the nodeDataStore reset function
 * to go back to the originally loaded data file.
 * 
 * The start function creates the store which takes care of then drawing the graph.  You don't
 * explicitly call the graph drawing routine, you just create a store and give it the url of the
 * data file to load.  There are optional parameters, as with the id in this example, that may be
 * specified as well.
 */

tcga.riphraph.createFilter = function() {
	var nodeDataStore = Ext.StoreMgr.get('nodeDataStore');
	
	var diseaseStore = new Ext.data.JsonStore({
		url:'json/diseases.sjson',
		storeId:'diseases',
		root:'diseases',
		idProperty:'tumorId',
		fields: [
			'tumorId',
			'tumorName',
			'tumorDescription'
		],
		autoLoad: true,
		listeners: {
			load: function(store) {
				var diseaseRec = Ext.data.Record.create([
					'tumorId',
					'tumorName',
					'tumorDescription'
				]);
			   var allDiseases = new diseaseRec({
					'tumorId': -1,
					'tumorName': 'All',
					'tumorDescription': 'Show all diseases'
			   });
				store.insert(0, [allDiseases]);
			}
		}
	});

	new Ext.Panel({
		renderTo: 'filterPanel',
		border: false,
		layout: 'column',
		items: [{
			id: 'fieldDisease',
			xtype: 'combo',
			store: diseaseStore,
			mode: 'local',
			triggerAction: 'all',
			displayField:'tumorDescription',
			valueField : 'tumorId',
			emptyText:'Select a disease',
			border: false,
			autoHeight: true,
			style: 'margin-bottom: 10px;',
			width: 270,
			listeners: {
				select: function(combo, rec, ndx) {
					if (rec.get('tumorId') == -1) {
						nodeDataStore.reset();
					}
					else if (rec.get('tumorId') == 13) {
						nodeDataStore.loadUrl('json/pRepDataSubsetScaled.sjson');
					}
					else {
						nodeDataStore.loadUrl('json/pRepDataSubset.sjson');
					}
				}
			}
		}, {
			xtype: 'button',
			text: '<span style="font-family: tahoma;font-weight: bold;">Clear</span>',
			width: 100,
			style: 'margin-left: 10px;',
			handler: function() {
				var diseaseCombo = Ext.getCmp('fieldDisease');
				diseaseCombo.setValue('-1');
				diseaseCombo.fireEvent('select', diseaseCombo, diseaseCombo.getStore().getAt(0), 0);
			}
		}]
	});
}

tcga.riphraph.start = function(){
	var nodeDataStore = tcga.graph.draw.createStore({
		storeId: 'nodeDataStore',
//		url: 'json/pRepDataAlt3.sjson'
//		url: 'json/pRepDataAlt2.sjson'
		url: 'json/pRepDataAlt.sjson'
//		url: 'json/kirpPrep2.sjson'
//		url: 'json/luad.sjson'
//		url: 'json/blnp.sjson'
//		url: 'json/lgg.sjson'
	});
	
	tcga.riphraph.createFilter();
}

Ext.onReady(tcga.riphraph.start, this);

Ext.namespace('tcga.riphraph');

tcga.riphraph.start = function(){
	var nodeDataStore = tcga.graph.draw.createStore({
		storeId:'nodeDataStore',
		url:'json/pRepDataSub.sjson'
	});
}

Ext.onReady(tcga.riphraph.start, this);

Ext.namespace('tcga.dash');

tcga.dash.overDash = function(){
	tcga.dash.navigator.start('Tissue');
}

Ext.onReady(tcga.dash.overDash, this);

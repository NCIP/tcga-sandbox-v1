Ext.namespace('tcga.dash');

tcga.dash.overDash = function(){
	tcga.dash.navigator.start('DAWG');
}

Ext.onReady(tcga.dash.overDash, this);

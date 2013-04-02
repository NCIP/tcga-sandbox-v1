Ext.namespace('tcga.dash');

tcga.dash.overDash = function(){
	tcga.dash.navigator.start('OverDash');
	tcga.dash.projectTable.start();
}

Ext.onReady(tcga.dash.overDash, this);

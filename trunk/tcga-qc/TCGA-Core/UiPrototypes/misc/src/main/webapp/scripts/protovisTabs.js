Ext.namespace('tcga.protovis');

tcga.protovis.createTabPanels = function() {
	Ext.QuickTips.init();
	
	var protovisTabPanel = new tcga.extensions.TabPanel({
		id: 'protovisDemoTabPanel',
		renderTo: 'protovisDemos',
		autoHeight: true,
		tabCls: 'tcga-ext-tabCTH',
		activeTab: 2,
		items: [{
			title: 'Demo One',
			demo: tcga.protovis.createDemoOne,
			html: '<div id="protovis_one"></div>'
		}, {
			title: 'Demo Two',
			demo: tcga.protovis.createDemoTwo,
			html: '<div id="protovis_two"></div>'
		}, {
			title: 'Demo Three',
			demo: tcga.protovis.createDemoThree,
			html: '<div id="protovis_three"></div>'
		}],
		listeners: {
			tabchange: function(tabPanel, tab) {
				tab.demo();
			}
		}
	});
	
	return protovisTabPanel;
}

tcga.protovis.createDemoStart = function() {
	tcga.protovis.createDataSets();

	tcga.protovis.createTabPanels();
}

Ext.onReady(tcga.protovis.createDemoStart, this);

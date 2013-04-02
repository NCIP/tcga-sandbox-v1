Ext.namespace('tcga.tufte');

tcga.tufte.createDemoOne = function() {
	jQuery('#tufte_one').tufteBar({
		data: [
			[197, {label: 'AML'}],
			[432, {label: 'Colon'}],
			[242, {label: 'Kidney'}],
			[140, {label: 'Lung Ad'}],
			[364, {label: 'Lung Sq'}],
			[204, {label: 'Rectum'}]
		],
		barWidth: 0.8,
		barLabel:  function(index) { return this[0] },
		axisLabel: function(index) { return this[1].label },
		color:     function(index) { return ['#007ABB', '#ffd700'][index % 2] },
	});
}

tcga.tufte.createDemoTwo = function() {
	jQuery('#tufte_two').tufteBar({
		data: [
			[[197, 127, 222], {label: 'AML'}],
			[[432, 30, 50], {label: 'Colon'}],
			[[242, 99, 77], {label: 'Kidney'}],
			[[140, 111, 20], {label: 'Lung Ad'}],
			[[364, 10, 113], {label: 'Lung Sq'}],
			[[204, 32, 96], {label: 'Rectum'}]
		],
		barWidth: 0.8,
		barLabel:  function(index) { return $(this[0]).sum() },
		axisLabel: function(index) { return this[1].label },
		legend: {
			data: ['Tumor', 'Matched Normal', 'Unmatched Normal'],
			color: function(index, stackedIndex) { return ['#07093d', '#0c0f66', '#476fb2', '#7d7907', '#a6af0c', '#f2ff47'][index%2 + stackedIndex + (index%2?2:0)] }
		},
		color: function(index, stackedIndex) { return ['#07093d', '#0c0f66', '#476fb2', '#7d7907', '#a6af0c', '#f2ff47'][index%2 + stackedIndex + (index%2?2:0)] }
	});
}

tcga.tufte.createTabPanels = function() {
	Ext.QuickTips.init();
	
	var tufteTabPanel = new tcga.extensions.TabPanel({
		id: 'tufteDemoTabPanel',
		renderTo: 'tufteDemos',
		autoHeight: true,
		tabCls: 'tcga-ext-tabCTH',
		activeTab: 1,
		items: [{
			title: 'Demo One',
			demo: tcga.tufte.createDemoOne,
			height: 400,
			html: '<div id="tufte_one" class="tufteTabGraph"></div>'
		}, {
			title: 'Demo Two',
			demo: tcga.tufte.createDemoTwo,
			height: 400,
			html: '<div id="tufte_two" class="tufteTabGraph"></div>'
		}],
		listeners: {
			tabchange: function(tabPanel, tab) {
				tab.demo();
			}
		}
	});
	
	return tufteTabPanel;
}

tcga.tufte.createDemoStart = function() {
	tcga.tufte.createTabPanels();
}

Ext.onReady(tcga.tufte.createDemoStart, this);

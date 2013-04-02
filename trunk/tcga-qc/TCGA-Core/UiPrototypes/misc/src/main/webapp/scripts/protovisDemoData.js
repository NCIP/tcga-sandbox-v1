Ext.namespace('tcga.protovis');

tcga.protovis.createDataSets = function() {
	tcga.protovis.data = pv.range(100).map(function(x) {
		return {x: x, y: Math.random(), z: Math.pow(10, 2 * Math.random())};
	});

	tcga.protovis.data2alt = [
		{d: 202,l: 'LAML'},
		{d: 149, l:'COAD'},
		{d: 441, l: 'GBM'}, 
		{d: 79, l: 'LUAD'},
		{d: 66, l: 'LUSC'},
		{d: 533, l: 'OV'},
		{d: 50, l: 'READ'}
	];

	tcga.protovis.data2 = [202, 149, 441, 79, 66, 533, 50];
	tcga.protovis.data2labels = ['LAML', 'COAD', 'GBM', 'LUAD', 'LUSC', 'OV', 'READ'];
}

/*******************************************************************************
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 ******************************************************************************/

Ext.onReady(function() {
	var diseaseName = tcga.util.getUrlParam('diseaseName');
	var diseaseType = tcga.util.getUrlParam('diseaseType');
	
	var detailsPageTableStore = new Ext.data.JsonStore({
		storeId: 'detailsPageTableStore',
//      Test version
		url: 'json/detailData.sjson',
//      Production version
//		url: 'json/detailData.json',
		root: 'tumorSampleTypeCount',
		idProperty: 'sampleType',
		fields: [
			'sampleType',
			'total',
			'copyNumber',
			'methylation',
			'geneExpression',
			'miRnaExpression',
			'sequence'
		],
		autoLoad: true,
		listeners: {
			load: function(store, recs) {
				tcga.dataPortal.createChart(recs);
			}
		}
	});
	
	var detailsPageTableTemplate = new Ext.XTemplate(
		'<table id="detailsPageTableContent" class="gradientRoundedTable topAlign" cellspacing="0" cellpadding="3px">',
		'<tr>',
			'<th rowspan=2>' + diseaseName + ' [' + diseaseType + ']' + '</th>',
			'<th colspan=5>Number of Samples</th>',
		'</tr>',
		'<tr>',
			'<th>Total</th>',
			'<th>Copy Number</th>',
			'<th>Methylation</th>',
			'<th>Gene Expression</th>',
			'<th>miRNA Expression</th>',
/* Display later
			'<th>Sequence*</th>',
*/
		'</tr>',
	 	'<tpl for=".">',
			'<tr>',
				'<td id="{sampleType}DetailsRow">{sampleType}</td>',
				'<td class="center">',
				'<tpl if="this.isNotNull(\'total\', values)">',
					'<a href="' + tcgaHost + '/tcga/dataAccessMatrix.htm?mode=ApplyFilter&showMatrix=true&diseaseType=' + diseaseType + '&availability=A',
					'{[this.tissueType(values.sampleType)]}',
					'&platformType=1&platformType=2&platformType=3&platformType=4&platformType=5&platformType=6&platformType=7&platformType=12&platformType=13&platformType=14&platformType=15&platformType=16&platformType=17&platformType=18">{total}</a>',
				'</tpl>',
				'<tpl if="this.isNull(\'total\', values)">',
					'0',
				'</tpl>',
				'</td>',
				'<td class="center">',
				'<tpl if="this.isNotNull(\'copyNumber\', values)">',
					'<a href="' + tcgaHost + '/tcga/dataAccessMatrix.htm?mode=ApplyFilter&showMatrix=true&diseaseType=' + diseaseType + '&availability=A',
					'{[this.tissueType(values.sampleType)]}',
					'&platformType=1&platformType=4">{copyNumber}</a>',
				'</tpl>',
				'<tpl if="this.isNull(\'copyNumber\', values)">',
					'0',
				'</tpl>',
				'</td>',
				'<td class="center">',
				'<tpl if="this.isNotNull(\'methylation\', values)">',
					'<a href="' + tcgaHost + '/tcga/dataAccessMatrix.htm?mode=ApplyFilter&showMatrix=true&diseaseType=' + diseaseType + '&availability=A',
					'{[this.tissueType(values.sampleType)]}',
					'&platformType=2">{methylation}</a>',
				'</tpl>',
				'<tpl if="this.isNull(\'methylation\', values)">',
					'0',
				'</tpl>',
				'</td>',
				'<td class="center">',
				'<tpl if="this.isNotNull(\'geneExpression\', values)">',
					'<a href="' + tcgaHost + '/tcga/dataAccessMatrix.htm?mode=ApplyFilter&showMatrix=true&diseaseType=' + diseaseType + '&availability=A',
					'{[this.tissueType(values.sampleType)]}',
					'&platformType=3&platformType=5">{geneExpression}</a>',
				'</tpl>',
				'<tpl if="this.isNull(\'geneExpression\', values)">',
					'0',
				'</tpl>',
				'</td>',
				'<td class="center">',
				'<tpl if="this.isNotNull(\'miRnaExpression\', values)">',
					'<a href="' + tcgaHost + '/tcga/dataAccessMatrix.htm?mode=ApplyFilter&showMatrix=true&diseaseType=' + diseaseType + '&availability=A',
					'{[this.tissueType(values.sampleType)]}',
					'&platformType=6">{miRnaExpression}</a>',
				'</tpl>',
				'<tpl if="this.isNull(\'miRnaExpression\', values)">',
					'0',
				'</tpl>',
				'</td>',
/* Display later
				'<td class="center">',
				'<tpl if="this.isNotNull(\'sequence\', values)">',
					'{sequence}',
				'</tpl>',
				'<tpl if="this.isNull(\'sequence\', values)">',
					'0',
				'</tpl>',
				'</td>',
*/
			'</tr>',
		'</tpl>',
		'</table>',
		{
			isNotNull: function(valName, vals) {
				return (vals[valName] != null && vals[valName] != ''?true:false);
			},
			isNull: function(valName, vals) {
				return (vals[valName] != null && vals[valName] != ''?false:true);
			},
			tissueType: function(val) {
				if (val == 'Tumor') {
					return '&tumorNormal=TN&tumorNormal=T';
				}
				else if (val == 'Matched Normal') {
					return '&tumorNormal=NT';
				}
				else if (val == 'Unmatched Normal') {
					return '&tumorNormal=N';
				}
			}
		}
	);

	var detailsPageTablePanel = new Ext.Panel({
		renderTo: 'detailsPageTable',
		border: false,
		items: new Ext.DataView({
			store: detailsPageTableStore,
			itemSelector: 'div.gradientRoundedTable',
			tpl: detailsPageTableTemplate
		})
	});
});

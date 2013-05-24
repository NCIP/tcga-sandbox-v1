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
        url: 'damws/tumordetails/json?diseaseType=' + diseaseType,
		root: 'dataTypeCount',
		idProperty: 'countType',
		fields: [
			'countType',
			'total',
			'exome',
            'snp',
            'methylation',
			'mRna',
			'miRna',
			'clinical'
		],
		autoLoad: true
	});
	
	var detailsPageTableTemplate = new Ext.XTemplate(
		'<table id="detailsPageTableContent" class="gradientRoundedTable topAlign" cellspacing="0" cellpadding="3px">',
		'<thead><tr class="firstrow">',
			'<th scope="col">' + diseaseName + ' [' + diseaseType + ']' + '</th>',
			'<th scope="col">Total</th>',
			'<th scope="col">Exome<sup style="font-size: smaller">1</sup></th>',
			'<th scope="col">SNP</th>',
			'<th scope="col">Methylation</th>',
			'<th scope="col">mRNA</th>',
            '<th scope="col">miRNA</th>',
			'<th scope="col">Clinical</th>',
		'</tr></thead><tbody>',
	 	'<tpl for=".">',
			'<tr>',
				'<td scope="row" id="{countType}DetailsRow">{[this.displayNameForCountType(values.countType)]}</td>',
				'<td class="center">',
				'<tpl if="this.isNotNull(\'total\', values)">',
					'<a href="' + tcgaHost + '/tcga/dataAccessMatrix.htm?mode=ApplyFilter&showMatrix=true&diseaseType=' + diseaseType,
                    '{[this.tissueType(values.countType)]}',
                    '">{total}</a>',
				'</tpl>',
				'<tpl if="this.isNull(\'total\', values)">',
					'{[this.displayValueForNull(values)]}',
				'</tpl>',
				'</td>',
				'<td class="center">',
				'<tpl if="this.isNotNull(\'exome\', values)">',
					'<a href="' + tcgaHost + '/tcga/dataAccessMatrix.htm?mode=ApplyFilter&showMatrix=true&diseaseType=' + diseaseType,
                    '{[this.tissueType(values.countType)]}',
					'&platformType=12&platformType=7&platformType=41">{exome}</a>',
				'</tpl>',
				'<tpl if="this.isNull(\'exome\', values)">',
					'{[this.displayValueForNull(values)]}',
				'</tpl>',
				'</td>',
				'<td class="center">',
				'<tpl if="this.isNotNull(\'snp\', values)">',
					'<a href="' + tcgaHost + '/tcga/dataAccessMatrix.htm?mode=ApplyFilter&showMatrix=true&diseaseType=' + diseaseType,
					'{[this.tissueType(values.countType)]}',
					'&platformType=1&platformType=4&platformType=40">{snp}</a>',
				'</tpl>',
				'<tpl if="this.isNull(\'snp\', values)">',
					'{[this.displayValueForNull(values)]}',
				'</tpl>',
				'</td>',
				'<td class="center">',
				'<tpl if="this.isNotNull(\'methylation\', values)">',
					'<a href="' + tcgaHost + '/tcga/dataAccessMatrix.htm?mode=ApplyFilter&showMatrix=true&diseaseType=' + diseaseType,
					'{[this.tissueType(values.countType)]}',
					'&platformType=2">{methylation}</a>',
				'</tpl>',
				'<tpl if="this.isNull(\'methylation\', values)">',
					'{[this.displayValueForNull(values)]}',
				'</tpl>',
				'</td>',
				'<td class="center">',
				'<tpl if="this.isNotNull(\'mRna\', values)">',
					'<a href="' + tcgaHost + '/tcga/dataAccessMatrix.htm?mode=ApplyFilter&showMatrix=true&diseaseType=' + diseaseType,
					'{[this.tissueType(values.countType)]}',
					'&platformType=3&platformType=5&platformType=27&platformType=38">{mRna}</a>',
				'</tpl>',
				'<tpl if="this.isNull(\'mRna\', values)">',
					'{[this.displayValueForNull(values)]}',
				'</tpl>',
				'</td>',
	            '<td class="center">',
				'<tpl if="this.isNotNull(\'miRna\', values)">',
					'<a href="' + tcgaHost + '/tcga/dataAccessMatrix.htm?mode=ApplyFilter&showMatrix=true&diseaseType=' + diseaseType,
					'{[this.tissueType(values.countType)]}',
					'&platformType=6&platformType=28">{miRna}</a>',
				'</tpl>',
				'<tpl if="this.isNull(\'miRna\', values)">',
					'{[this.displayValueForNull(values)]}',
				'</tpl>',
				'</td>',
            	'<td class="center">',
				'<tpl if="this.isNotNull(\'clinical\', values)">',
					'<a href="' + tcgaHost + '/tcga/dataAccessMatrix.htm?mode=ApplyFilter&showMatrix=true&diseaseType=' + diseaseType,
					'{[this.tissueType(values.countType)]}',
					'&platformType=-999">{clinical}</a>',
				'</tpl>',
				'<tpl if="this.isNull(\'clinical\', values)">',
					'{[this.displayValueForNull(values)]}',
				'</tpl>',
				'</td>',
			'</tr>',
		'</tpl>',
		'</body></table>',
		{
			isNotNull: function(valName, vals) {
				return (vals[valName] != null && vals[valName] != '');
			},
			isNull: function(valName, vals) {
				return (!(vals[valName] != null && vals[valName] != ''));
			},
			tissueType: function(val) {
				if (val == 'Cases') {
					return '&tumorNormal=TN&tumorNormal=T&tumorNormal=NT';
				}
				else if (val == 'Organ-Specific Controls') {
					return '&tumorNormal=N';
				}
			},
            displayValueForNull: function(vals) {
                if(vals['countType'] == 'Organ-Specific Controls' && (this.isNull('total', vals) || vals['total'] == 0)) {
                    return 'N/A';
                } else {
                    return 0;
                }
            },
            displayNameForCountType: function(countType) {
                if (countType == 'Organ-Specific Controls') {
                    return countType + '<sup style="font-size: smaller">2</sup>';
                } else {
                    return countType;
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

/*******************************************************************************
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 ******************************************************************************/

Ext.onReady(function() {
	var homePageTableStore = new Ext.data.JsonStore({
		storeId: 'homePageTableStore',
		url: 'damws/tumormain/json',
		root: 'tumorMainCount',
		idProperty: 'tumorAbbreviation',
		fields: [
			'tumorName',
			'tumorAbbreviation',
			'casesShipped',
			'casesWithData',
			'lastUpdate'
		],
		autoLoad: true
	});
	
	var homePageTableTemplate = new Ext.XTemplate(
'<table class="gradientRoundedTable topAlign" style="empty-cells: show;" cellspacing="0" cellpadding="3px">',
		'<thead class="smallertext"><tr>',
           '<th scope="col">Available Cancer Types</th>',
           '<th scope="col"># Cases Shipped by BCR</th>',
           '<th scope="col"># Cases with Data</th>',
           '<th scope="col">Date Last Updated (mm/dd/yy)</th>',
		'</tr></thead><tbody>',
        '<tpl for=".">',
           '<tr>',
               '<td>',
               '<tpl if="this.isNotNull(\'casesWithData\', values)">',
                   '<a href="tcgaCancerDetails.jsp?diseaseType={tumorAbbreviation}&diseaseName={tumorName}">{tumorName}</a> [{tumorAbbreviation}]',
               '</tpl>',
               '<tpl if="this.isNull(\'casesWithData\', values)">',
                   '{tumorName} [{tumorAbbreviation}]',
               '</tpl>',
               '</td>',
               '<td class="center">',
               '<tpl if="this.isNotNull(\'casesShipped\', values)">',
                   '{casesShipped}',
               '</tpl>',
               '<tpl if="this.isNull(\'casesShipped\', values)">',
                   '0',
               '</tpl>',
               '</td>',
               '<td class="center">',
               '<tpl if="this.isNotNull(\'casesWithData\', values)">',
                   '<a href="' + tcgaHost + '/tcga/dataAccessMatrix.htm?mode=ApplyFilter&showMatrix=true',
                   '&diseaseType={tumorAbbreviation}&tumorNormal=TN&tumorNormal=T&tumorNormal=NT">{casesWithData}</a>',
               '</tpl>',
               '<tpl if="this.isNull(\'casesWithData\', values)">',
                   '0',
               '</tpl>',
               '</td>',
               '<td class="center">',
               '<tpl if="this.isNotNull(\'lastUpdate\', values)">',
                   '{lastUpdate}',
               '</tpl>',
               '<tpl if="this.isNull(\'lastUpdate\', values)">',
                   '&nbsp;',
               '</tpl>',
               '</td>',
           '</tr>',
       '</tpl>',
		'</tbody></table>',
		{
			isNotNull: function(valName, vals) {
				return (vals[valName] != null && vals[valName] != ''?true:false);
			},
			isNull: function(valName, vals) {
				return (vals[valName] != null && vals[valName] != ''?false:true);
			}
		}
	);

	var homePageTablePanel = new Ext.Panel({
		renderTo: 'homePageTable',
		border: false,
		items: new Ext.DataView({
			store: homePageTableStore,
			itemSelector: 'div.gradientRoundedTable',
			tpl: homePageTableTemplate
		})
	});
});

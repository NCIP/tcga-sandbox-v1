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
//      Test version
		url: 'json/homeData.sjson',
//      Production version
//		url: 'damws/tumormain/json',
		root: 'tumorMainCount',
		idProperty: 'tumorAbbreviation',
		fields: [
			'tumorName',
			'tumorAbbreviation',
			'patientSamples',
			'downloadableTumorSamples',
			'lastUpdate'
		],
		autoLoad: true
	});
	
	var homePageTableTemplate = new Ext.XTemplate(
		'<table class="gradientRoundedTable topAlign" cellspacing="0" cellpadding="3px">',
		'<tr>',
			'<th>Available Cancer Types</th>',
			'<th># Patients with Samples</th>',
			'<th># Downloadable Tumor Samples</th>',
			'<th>Date Last Updated (mm/dd/yy)</th>',
		'</tr>',
	 	'<tpl for=".">',
			'<tr>',
				'<td><a href="tcgaCancerDetails.jsp?diseaseType={tumorAbbreviation}&diseaseName={tumorName}">{tumorName}</a> [{tumorAbbreviation}]</td>',
				'<td class="center">',
				'<tpl if="this.isNotNull(\'patientSamples\', values)">',
					'{patientSamples}',
				'</tpl>',
				'<tpl if="this.isNull(\'patientSamples\', values)">',
					'0',
				'</tpl>',
				'</td>',
				'<td class="center">',
				'<tpl if="this.isNotNull(\'downloadableTumorSamples\', values)">',
					'<a href="' + tcgaHost + '/tcga/dataAccessMatrix.htm?mode=ApplyFilter&showMatrix=true&diseaseType={tumorAbbreviation}&availability=A&platformType=1&platformType=2&platformType=3&platformType=4&platformType=5&platformType=6&platformType=7&platformType=12&platformType=13&platformType=14&platformType=15&platformType=16&platformType=17&platformType=18">{downloadableTumorSamples}</a>',
				'</tpl>',
				'<tpl if="this.isNull(\'downloadableTumorSamples\', values)">',
					'0',
				'</tpl>',
				'</td>',
				'<td class="center">',
				'<tpl if="this.isNotNull(\'lastUpdate\', values)">',
					'{lastUpdate}',
				'</tpl>',
				'<tpl if="this.isNull(\'lastUpdate\', values)">',
					'',
				'</tpl>',
				'</td>',
			'</tr>',
		'</tpl>',
		'</table>',
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

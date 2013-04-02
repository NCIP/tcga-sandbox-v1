Ext.namespace('tcga.dash.projectTable');

tcga.dash.projectTable.start = function(){
	var createDashProjectTemplate = function(store, recs) {
		var gccCount = recs[0].get('gcc').length;
		var gscCount = recs[0].get('gsc').length;
		var totalCount = 1 /* Cancer Names */ + 5 /* Case Accrual */ + gccCount + gscCount + 1 /* At TCGA */;
		var totalWidth = totalCount * 80; /* 80 = 72 width + 3 cellpadding either side + 1 border either side */
		
		var dashProjectTableTemplate = new Ext.XTemplate(
			'<table style="table-layout: fixed;width: ' + totalWidth + 'px;" class="gradientRoundedTable topAlign" cellspacing="0" cellpadding="3px">',
			'<tr>',
				'<th rowspan=2>&nbsp</th>',
				'<th colspan=5>Case accrual through BCRs</th>',
				'<th colspan=' + gccCount + '>GCC (cases complete)</th>',
				'<th colspan=' + gscCount + '>GSC (cases complete)</th>',
				'<th style="width: 72px;">TCGA</th>',
			'</tr>',
		 	'<tpl for=".">',
				// Do the row of headers first, but only the first time through the loop
				'<tpl if="xindex == 1">',
					'<tr>',
						'<th style="width: 72px;">Projected</th>',
						'<th style="width: 72px;">Available</th>',
						'<th style="width: 72px;">Contracted</th>',
						'<th style="width: 72px;">Received</th>',
						'<th style="width: 72px;">Shipped</th>',
						'<tpl for="gcc">',
							'<th style="width: 72px;">{name}</th>',
						'</tpl>',
						'<tpl for="gsc">',
							'<th style="width: 72px;">{name}</th>',
						'</tpl>',
						'<th style="width: 72px;">Total (as % of Target)</th>',
					'</tr>',
				'</tpl>',
				'<tr>',
					'<td><b>{cancerType}</b></td>',
					'<tpl for="bcr">',
						'<td>{projected}</td>',
						'<td>{[this.percentage(values["available"],values["projected"])]}%<div class="progLine" style="width: 20px;" percent="{parent.cancerType}|Available|Projected|{available}|{projected}">&nbsp;</div></td>',
						'<td>{[this.percentage(values["contracted"],values["projected"])]}%<div class="progLine" style="width: 20px;" percent="{parent.cancerType}|Contracted|Projected|{contracted}|{projected}">&nbsp;</div></td>',
						'<td>{[this.percentage(values["received"],values["projected"])]}%<div class="progLine" style="width: 20px;" percent="{parent.cancerType}|Received|Projected|{received}|{projected}">&nbsp;</div></td>',
						'<td>{[this.percentage(values["shipped"],values["required"])]}%<div class="progLine" style="width: 20px;" percent="{parent.cancerType}|Shipped|Required|{shipped}|{required}">&nbsp;</div></td>',
					'</tpl>',
					'<tpl for="gcc">',
						'<td>{[this.percentage(values["delivered"],values["contracted"])]}%<div class="progLine" style="width: 20px;" percent="{parent.cancerType}|Delivered|Contracted|{delivered}|{contracted}">&nbsp;</div></td>',
					'</tpl>',
					'<tpl for="gsc">',
						'<td>{[this.percentage(values["delivered"],values["contracted"])]}%<div class="progLine" style="width: 20px;" percent="{parent.cancerType}|Delivered|Contracted|{delivered}|{contracted}">&nbsp;</div></td>',
					'</tpl>',
					'<tpl for="tcga">',
						'<td>{[this.percentage(values["delivered"],values["contracted"])]}%<div class="progLine" style="width: 20px;" percent="{parent.cancerType}|Delivered|Contracted|{delivered}|{contracted}">&nbsp;</div></td>',
					'</tpl>',
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
				percentage: function(num, denom) {
					if (denom != 0) {
						return Math.round(100 * num/denom);
					}
					else {
						return 0;
					}
				}
			}
		);
		
		return dashProjectTableTemplate;
	};

	var createDashProjectPanel = function(store, tpl) {
		var dashProjectTablePanel = new Ext.Panel({
			renderTo: 'dashProjectTable',
			border: false,
			items: new Ext.DataView({
				store: store,
				tpl: tpl
			})
		});
	};
	
	var dashProjectTableStore = new Ext.data.JsonStore({
		storeId: 'dashProjectTableStore',
		url: 'json/projectOverview.sjson',
		root: 'dashProjectData',
		idProperty: 'cancerType',
		fields: [
			'cancerType',
			'bcr',
			'gcc',
			'gsc',
			'tcga'
		],
		autoLoad: true,
		listeners: {
			load: function(store, recs) {
				var tpl = createDashProjectTemplate(store, recs);
				createDashProjectPanel(store, tpl);
				
				var progLines = Ext.select('div[class=progLine]');
				progLines.each(function(pL) {
					// percentStr = cancerType|numeratorName|denominatorName|numerator|denominator
					var percentStr = pL.getAttribute('percent');
					var percentVals = percentStr.split('|');
					var percent = (percentVals[4] != 0?Math.round(100 * percentVals[3]/percentVals[4]):0);
					percent = (percent > 100?100:percent);
					pL.setStyle('width', ((0.01 * percent) * (pL.findParent('td').clientWidth - 4 /* allow for cellPadding and border on right */)) + 'px');
					pL.setStyle('top', pL.getTop() - 20 + 'px');
					pL.setStyle('left', pL.getLeft() - 1 + 'px');

					new tcga.util.hoverWin({
						appearNear: pL.findParent('td'),
						top: -71, // Hover above the cell
						left: 0,
						text: '<b>' + percentVals[0] + '</b><br/>' + percentVals[1]+ ': ' + percentVals[3] + '<br/>' + percentVals[2]+ ': ' + percentVals[4]
					});
				});
			}
		}
	});
}

Ext.namespace('tcga.dai');

tcga.dai.createFilter = function() {
	// Use local data for now so that the same file can be used for local and app server versions
	var diseaseData = {"diseases" : [ 
			{ "tumorDescription" : "Acute Myeloid Leukemia",
	        "tumorId" : 13,
	        "tumorName" : "LAML"
	      },
	      { "tumorDescription" : "Breast invasive carcinoma",
	        "tumorId" : 5,
	        "tumorName" : "BRCA"
	      },
	      { "tumorDescription" : "Colon adenocarcinoma",
	        "tumorId" : 6,
	        "tumorName" : "COAD"
	      },
	      { "tumorDescription" : "Glioblastoma multiforme",
	        "tumorId" : 1,
	        "tumorName" : "GBM"
	      },
	      { "tumorDescription" : "Kidney renal papillary cell carcinoma",
	        "tumorId" : 8,
	        "tumorName" : "KIRP"
	      },
	      { "tumorDescription" : "Lung adenocarcinoma",
	        "tumorId" : 4,
	        "tumorName" : "LUAD"
	      },
	      { "tumorDescription" : "Lung squamous cell carcinoma",
	        "tumorId" : 2,
	        "tumorName" : "LUSC"
	      },
	      { "tumorDescription" : "Ovarian serous cystadenocarcinoma",
	        "tumorId" : 3,
	        "tumorName" : "OV"
	      },
	      { "tumorDescription" : "Rectum adenocarcinoma",
	        "tumorId" : 24,
	        "tumorName" : "READ"
	      },
	      { "tumorDescription" : "Uterine Corpus Endometrioid Carcinoma",
	        "tumorId" : 23,
	        "tumorName" : "UCEC"
	      }
	    ]
	 };
	
	var diseaseStore = new Ext.data.JsonStore({
//		url:'json/diseases.sjson',
		data: diseaseData,
		storeId:'diseases',
		root:'diseases',
		idProperty:'tumorId',
		fields: [
			'tumorId',
			'tumorName',
			'tumorDescription'
		],
		autoLoad: true,
		listeners: {
			load: function(store) {
				var diseaseRec = Ext.data.Record.create([
					'tumorId',
					'tumorName',
					'tumorDescription'
				]);
			   var allDiseases = new diseaseRec({
					'tumorId': -1,
					'tumorName': 'All',
					'tumorDescription': 'Show all diseases'
			   });
				store.insert(0, [allDiseases]);
			}
		}
	});

	new Ext.Panel({
		renderTo: 'daiFilter',
		border: false,
		layout: 'column',
		items: [{
			id: 'fieldDisease',
			xtype: 'combo',
			store: diseaseStore,
			mode: 'local',
			triggerAction: 'all',
			displayField:'tumorDescription',
			valueField : 'tumorId',
			emptyText:'Select a disease',
			border: false,
			autoHeight: true,
			style: 'margin-bottom: 10px;',
			width: 270,
			listeners: {
				select: function(combo, rec, ndx) {
					paper.clear();
					if (rec.get('tumorId') == -1) {
					}
					else if (rec.get('tumorId') == 13) {
					}
					else {
					}
				}
			}
		}, {
			xtype: 'button',
			text: '<span style="font-family: tahoma;font-weight: bold;">Clear</span>',
			width: 100,
			style: 'margin-left: 10px;',
			handler: function() {
				var diseaseCombo = Ext.getCmp('fieldDisease');
				diseaseCombo.setValue('-1');
				diseaseCombo.fireEvent('select', diseaseCombo, diseaseCombo.getStore().getAt(0), 0);
			}
		}]
	});
}

tcga.dai.createTreePanel = function() {
    var tree = new Ext.ux.tree.TreeGrid({
        title: 'TCGA Cancer Data',
        dataUrl: 'json/daiTree.sjson',
        width: 850,
        height: 400,
        renderTo: 'daiDisplay',
		  autoScroll: true,
        columns:[{
            header: 'Files',
            dataIndex: 'text',
            width: 600,
				tpl: new Ext.XTemplate(
					'{text}',
					'<tpl if="this.isNotNull(\'abbrev\',values)">',
						'({abbrev})',
					'</tpl>',
					{
						isNotNull: function(valName, vals) {
							return (vals[valName] != null?true:false);
						}
					}
				)
        }, {
            header: 'Date',
            width: 100,
            dataIndex: 'date'
        }, {
            header: 'Size',
            width: 100,
            dataIndex: 'size'
        }]
    });
}

tcga.dai.start = function(){
	tcga.dai.createFilter();
	tcga.dai.createTreePanel();
}

Ext.onReady(tcga.dai.start, this);

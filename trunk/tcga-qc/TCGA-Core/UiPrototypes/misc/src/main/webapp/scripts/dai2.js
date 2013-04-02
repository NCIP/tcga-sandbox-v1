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

tcga.dai.synch = false;
tcga.dai.currPanelNode = null;

tcga.dai.createTreePanel = function() {
	// Something happened in the panel, synch the tree to it
	var synchTreeToPanel = function() {
		if (tcga.dai.currPanelNode.attributes.id == 'root') {
			tree.collapseAll();
		}
		else {
			tree.expandPath(tcga.dai.currPanelNode.getPath());
			tcga.dai.currPanelNode.ensureVisible();
			tcga.dai.currPanelNode.suspendEvents(false);
			tcga.dai.currPanelNode.select();
			tcga.dai.currPanelNode.resumeEvents();
		}
	}

	var openFolderInPanel = function(selectedNode) {
		tcga.dai.currPanelNode = tree.getNodeById(selectedNode.id);

		treeData.fireEvent('load', treeData, selectedNode);

		Ext.get('dirNameFromTree').update(selectedNode.attributes.text);
	}

	var downloadFile = function(selectedNode) {
		Ext.Msg.show({
			title: 'Download Query',
			msg: 'Would you like to download ' + selectedNode.attributes.text + '?',
			buttons: Ext.Msg.OKCANCEL,
			icon: Ext.Msg.QUESTION
		});
	}

	var treeLevelStore = new Ext.data.ArrayStore({
		storeId: 'treeLevelStore',
		fields: [
			'id',
			'text',
			'abbrev',
			'date',
			'size',
			'leaf',
			'node'
		]
	});
	
	var treeData = new Ext.tree.TreeLoader({
		dataUrl: 'json/daiTree.sjson',
		listeners: {
			load: function(loader, node) {
				var treeLevelStore = Ext.StoreMgr.get('treeLevelStore');

				var treeNodes = [];
				node.eachChild(function(node) {
					treeNodes.push([
						node.attributes.id,
						node.attributes.text,
						node.attributes.abbrev,
						node.attributes.date,
						node.attributes.size,
						node.attributes.leaf,
						node
					]);
				});
								
				treeLevelStore.loadData(treeNodes);
			}
		}
	});

	var rootNode = {
		id: 'root',
		text: 'root'		
	};
	tcga.dai.currPanelNode = new Ext.tree.TreeNode(rootNode);;

    var tree = new Ext.tree.TreePanel({
		title: 'TCGA Cancer Data',
		loader: treeData,
		width: 250,
		height: 400,
		autoScroll: true,
		rootVisible: false,
		root: rootNode,
		listeners: {
			expandnode: function(selectedNode) {
				if (tcga.dai.synch) {
					openFolderInPanel(selectedNode);
				}
			},
			collapsenode: function(collapsedNode) {
				if (tcga.dai.synch) {
					openFolderInPanel(collapsedNode.parentNode);
				}
			}
		}
    });
	 
	 var folderTemplate = new Ext.XTemplate(
	 	'<tpl for=".">',
			'<div>',
				'<tpl if="this.isNotNull(\'leaf\', values)">',
					'<img src="images/default/tree/leaf.gif">',
				'</tpl>',
				'<tpl if="this.isNull(\'leaf\', values)">',
					'<img src="images/default/tree/folder-open.gif">',
				'</tpl>',
				'{text}',
			'</div>',
		'</tpl>',
		{
			isNotNull: function(valName, vals) {
				return (vals[valName] != null && vals[valName] != ''?true:false);
			},
			isNull: function(valName, vals) {
				return (vals[valName] != null && vals[valName] != ''?false:true);
			}
		}
	 );
	 
	 new Ext.Panel({
	 	renderTo: 'daiDisplay',
		width: 800,
		height: 400,
	 	border: false,
		layout: 'column',
		items: [
			tree,
		{
			width: 500,
			height: 400,
			border: true,
			style: 'padding-left: 5px;',
			items: [{
				width: 500,
			 	height: 30,
				border: true,
				style: 'line-height: 24px;',
				html: '<div id="dirNameFromTree">root</div>'
		   }, {
			 	width: 500,
			 	height: 30,
			 	layout: 'column',
			 	items: [{
			 		xtype: 'button',
			 		width: 25,
			 		enableToggle: true,
			 		icon: 'images/arrow_refresh.png',
			 		style: 'margin: 3px;',
			 		handler: function(btn, e){
			 			tcga.dai.synch = btn.pressed;
			 			if (btn.pressed) {
			 				synchTreeToPanel();
							Ext.get('synchPanelLabel').update('Click to unSynchronize Panels');
			 			}
						else {
							Ext.get('synchPanelLabel').update('Click to Synchronize Panels');
						}
			 		}
			 	}, {
					border: false,
					width: 250,
					style: 'padding-top: 5px;',
					html: '<div id="synchPanelLabel">Click to Synchronize Panels</div>'
				}]
			},
			new Ext.DataView({
				store: treeLevelStore,
				tpl: folderTemplate,
				listeners: {
					dblclick: function(dv, ndx, node) {
						var store = dv.getStore();
						
						var nodeNdx = store.findExact('text',node.textContent);
						var selectedNode = store.getAt(nodeNdx).get('node');
						treeData.load(selectedNode);
						
						// Only open up folder nodes
						if (selectedNode.attributes.leaf == null || selectedNode.attributes.leaf == false) {
							openFolderInPanel(selectedNode);
						}
						else {
							downloadFile(selectedNode);
						}
						
						if (tcga.dai.synch) {
							synchTreeToPanel();
						}
					}
				}
			})
			]
		}]
	 });
}

tcga.dai.start = function(){
	tcga.dai.createFilter();
	tcga.dai.createTreePanel();
}

Ext.onReady(tcga.dai.start, this);

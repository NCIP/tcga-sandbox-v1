Ext.namespace('tcga.uuid.search');

tcga.uuid.search.webServiceWin = function() {
	var introHtml = 'To request an archive of all data matching this filter from the UUID Browser web service, use the following URL:<br/>';
	
    var getBaseURL = function() {
    	var serverUrl = '';
        if (document.baseURI === undefined) {
            var baseTags = document.getElementsByTagName ("base");
            if (baseTags.length > 0) {
                serverUrl = baseTags[0].href;
            }
            else {
                serverUrl = window.location.href;
            }
        }
        else {
            serverUrl = document.baseURI;
        }
        
        var paramsStart = serverUrl.indexOf('#');
        if (paramsStart >= 0) {
        	serverUrl = serverUrl.substring(0, paramsStart);
        }
        
        return serverUrl;
    }
    var serverUrl = getBaseURL();
    
    // Collect the parameters
    var url = '/uuidBrowseWS?';
    url += 'param=parameters';
    
    var urlHtml = '<textarea style="margin: 10px;width: 360px;">' + serverUrl + url + '</textarea>';
    
    var win = new Ext.Window({
        title:'UUID Browser Web Service URL',
        closable:true,
        width:400,
        autoHeight : true,
        collapsible: false,
        modal: true,
        layout: 'fit',
        buttons: [{text: 'Close',handler: function(){win.hide();}}],
        html: introHtml + urlHtml
    });
    win.show();
};

tcga.uuid.search.metadata = function(){
	var formElementSep = 'margin-bottom: 5px;';
	
	var elementTypeCombo = {
		id: 'elementTypeCombo',
		xtype: 'lovpluscombo',
		store: {
			xtype: 'jsonstore',
			url:'json/elementTypes.sjson',
			storeId:'elementTypeStore',
			root:'elementTypes',
			idProperty:'type',
			autoLoad: true,
			fields: [
				'type'
			]
		},
		fieldLabel: 'Element',
		triggerAction: 'all',
		displayField:'type',
		valueField : 'type',
		emptyText:'Select types...',
		border: false,
		autoHeight: true,
		style: formElementSep,
		width: 130,
        editable: false,
        forceSelection: true
	};
	
    var platformCombo = {
		id: 'platformCombo',
		xtype: 'lovpluscombo',
		store: {
			xtype: 'jsonstore',
			url:'json/platformCode.sjson',
			storeId:'elementTypeStore',
			root:'platformCodeData',
			idProperty:'platformAlias',
			autoLoad: true,
			fields: [
				'platformName',
				'displayName'
			]
		},
		fieldLabel: 'Platform',
		triggerAction: 'all',
		displayTpl:'{platformName} - {displayName}',
		displayField:'platformName',
		valueField : 'platformName',
		emptyText:'Select platforms...',
		border: false,
		autoHeight: true,
		style: formElementSep,
		width: 130,
		listWidth: 370,
        editable: false,
        forceSelection: true
    };
    
    var participantTB = {
    	xtype: 'textfield',
    	fieldLabel: 'Participant',
		style: formElementSep,
    	width: 130
    };

    var batchTB = {
    	xtype: 'textfield',
    	fieldLabel: 'Batch',
		style: formElementSep,
    	width: 130
    };

	var diseaseCombo = {
		id: 'diseaseCombo',
		xtype: 'lovpluscombo',
		store: {
			xtype: 'jsonstore',
			url:'json/diseases.sjson',
			storeId:'diseasePreferenceStore',
			root:'diseases',
			idProperty:'tumorId',
			autoLoad: true,
			fields: [
				'tumorId',
				'tumorName',
				'tumorDescription'
			]
		},
		fieldLabel: 'Disease',
		triggerAction: 'all',
		displayField:'tumorName',
		valueField : 'tumorId',
		displayTpl: '{tumorName} - {tumorDescription}',
		emptyText:'Select types...',
		border: false,
		autoHeight: true,
		style: formElementSep,
		width: 150,
		listWidth: 270,
        editable: false,
        forceSelection: true
	};

	var sampleCombo = {
		id: 'sampleCombo',
		xtype: 'lovpluscombo',
		store: {
			xtype: 'jsonstore',
			url:'json/sampleTypes.sjson',
			storeId:'sampleTypeStore',
			root:'sampleTypeData',
			idProperty:'code',
			autoLoad: true,
			fields: [
				'code',
				'definition'
			]
		},
		fieldLabel: 'Sample Types',
		triggerAction: 'all',
		displayTpl:'{code} - {definition}',
		displayField:'code',
		valueField : 'code',
		emptyText:'Select sample type...',
		border: false,
		autoHeight: true,
		style: formElementSep,
		width: 150,
		listWidth: 270,
        editable: false,
        forceSelection: true
    };
    
	var vialTB = {
    	xtype: 'textfield',
    	fieldLabel: 'Vial',
		style: formElementSep,
    	width: 60
    };
	
	var portionTB = {
    	xtype: 'textfield',
    	fieldLabel: 'Portion',
		style: formElementSep,
    	width: 60
    };

	var analyteCombo = {
		id: 'analyteCombo',
		xtype: 'lovpluscombo',
		store: {
			xtype: 'jsonstore',
			url:'json/analyteTypes.sjson',
			storeId:'analyteTypeStore',
			root:'portionAnalyteData',
			idProperty:'code',
			autoLoad: true,
			fields: [
				'code',
				'definition'
			]
		},
		fieldLabel: 'Analyte Types',
		triggerAction: 'all',
		displayTpl:'{code} - {definition}',
		displayField:'code',
		valueField : 'code',
		emptyText:'Select analyte type...',
		border: false,
		autoHeight: true,
		style: formElementSep,
		width: 150,
		listWidth: 270,
        editable: false,
        forceSelection: true
    };
	
	var plateTB = {
    	xtype: 'textfield',
    	fieldLabel: 'Plate',
		style: formElementSep,
    	width: 150
    };
    
	var bcrCombo = {
		id: 'bcrCombo',
		xtype: 'lovpluscombo',
		store: {
			xtype: 'jsonstore',
			url:'json/bcrs.sjson',
			storeId:'bcrStore',
			root:'centerData',
			idProperty:'centerId',
			autoLoad: true,
			fields: [
			    'centerId',
				'shortName',
				'centerName'
			]
		},
		fieldLabel: 'BCR Source',
		triggerAction: 'all',
		displayTpl:'{shortName} - {centerName}',
		displayField:'shortName',
		valueField : 'centerId',
		emptyText:'Select centers...',
		border: false,
		autoHeight: true,
		style: formElementSep,
		width: 215,
		listWidth: 340,
        editable: false,
        forceSelection: true
    };
	
	var tssStore = new Ext.data.JsonStore({
		url:'json/tissueSourceSite.sjson',
		storeId:'tssStore',
		root:'tissueSourceSiteData',
		idProperty:'code',
		autoLoad: true,
		fields: [
		    'studyName',
			'bcr',
			'code',
			'definition'
		],
		listeners: {
			load: function(store) {
    			var recs = store.getRange();
        		var tssData = [];
    			for (var ndx = 0;ndx < recs.length;ndx++) {
    				tssData.push([recs[ndx].get('code'), recs[ndx].get('code') + '-' + recs[ndx].get('definition')]);
    			}

        		Ext.getCmp('tssCombo').getStore().loadData(tssData);
			}
		}
	});

	var tssTypeCombo = {
		id: 'tssTypeCombo',
		xtype: 'combo',
		store: {
			xtype: 'arraystore',
			storeId:'tssTypeStore',
			idIndex: 0,
			fields: ['type'],
			data: [['Name', 'TSS ID']]
		},
		mode: 'local',
		fieldLabel: 'Tissue Source Site',
		triggerAction: 'all',
		displayField:'type',
		valueField : 'type',
		value: 'TSS ID',
		border: false,
		autoHeight: true,
		style: formElementSep,
		width: 65,
		listWidth: 65,
        editable: false,
        forceSelection: true,
        listeners: {
        	select: function(combo, rec, ndx) {
        		var tssCombo = Ext.getCmp('tssCombo');
        		tssCombo.deselectAll();
        		var tssData = [];
        		
        		if (combo.getValue() == 'Name') {
        			var uniqueVals = tssStore.collect('definition');
        			for (var ndx = 0;ndx < uniqueVals.length;ndx++) {
        				var rec = tssStore.getAt(tssStore.find('definition', uniqueVals[ndx]));
        				tssData.push([rec.get('code'), rec.get('definition')]);
        			}
        		}
        		else {
        			tssStore.clearFilter();
        			var recs = tssStore.getRange();
        			for (var ndx = 0;ndx < recs.length;ndx++) {
        				tssData.push([recs[ndx].get('code'), recs[ndx].get('code') + '-' + recs[ndx].get('definition')]);
        			}
        		}

        		Ext.getCmp('tssCombo').getStore().loadData(tssData);
        	}
        }
    };

	var tssCombo = {
		id: 'tssCombo',
		xtype: 'lovpluscombo',
		store: {
			xtype: 'arraystore',
			idIndex: 0,
			fields: [
			    'code',
				'display'
			]
		},
		hideLabel: true,
		mode: 'local',
		displayField:'display',
		valueField : 'code',
		emptyText:'Select centers...',
		border: false,
		autoHeight: true,
		style: formElementSep,
		width: 140,
		listWidth: 240,
        editable: true,
        forceSelection: true
    };

	var recCtrTypeStore = new Ext.data.ArrayStore({
		storeId:'recCtrTypeStore',
		idIndex: 0,
		fields: ['type']
	});
	
	var recCtrStore = new Ext.data.JsonStore({
		url:'json/centerCode.sjson',
		storeId:'recCtrStore',
		root:'centerCodeData',
		idProperty:'centerCode',
		autoLoad: true,
		fields: [
		    'centerCode',
			'shortName',
			'centerName',
			'centerType',
			'centerDisplayName'
		],
		listeners: {
			load: function(store) {
				var typeArray = store.collect('centerType');
				var typeArrayArray = [];
				for (var ndx = 0;ndx < typeArray.length;ndx++) {
					typeArrayArray.push([typeArray[ndx]]);
				}
				
				recCtrTypeStore.loadData(typeArrayArray);
			}
		}
	});
	
	var recCtrTypeCombo = {
		id: 'recCtrTypeCombo',
		xtype: 'lovpluscombo',
		store: recCtrTypeStore,
		mode: 'local',
		fieldLabel: 'Receiving Center',
		triggerAction: 'all',
		displayField:'type',
		valueField : 'type',
		emptyText:'Type...',
		border: false,
		autoHeight: true,
		style: formElementSep,
		width: 65,
		listWidth: 340,
        editable: false,
        forceSelection: true
    };

	var recCtrCombo = {
		id: 'recCtrCombo',
		xtype: 'lovpluscombo',
		store: recCtrStore,
		hideLabel: true,
		mode: 'local',
		displayTpl:'{shortName} - {centerDisplayName}',
		displayField:'shortName',
		valueField : 'shortName',
		emptyText:'Select centers...',
		border: false,
		autoHeight: true,
		style: formElementSep,
		width: 140,
		listWidth: 340,
        editable: false,
        forceSelection: true,
        listeners: {
        	expand: function(combo, rec, ndx) {
        		var checkedValues = Ext.getCmp('recCtrTypeCombo').getCheckedValue('type').split(',');

        		Ext.getCmp('recCtrCombo').getStore().filterBy(function(rec) {
        			var type = rec.get('centerType');

        			// Nothing selected, so don't filter
        			if (this.length == 1 && this[0] == '') {return true;}
        			if (this.length == 1 && this[0] == type) {return true;}

        			for (var ndx = 0;ndx < this.length;ndx++) {
        				if (type == this[ndx]) {return true;}
        			}
        			return false;
        		}, checkedValues);
        	}
        }
    };

	var updatedAfter = {
    	xtype: 'datefield',
		style: formElementSep,
		width: 70,
    	fieldLabel: 'Updated After'
    };
	
	var updatedBefore = {
    	xtype: 'datefield',
		style: formElementSep,
		width: 70,
    	fieldLabel: 'Updated Before'
    };

	return new Ext.form.FormPanel({
		title: 'Metadata',
		style: 'margin-top: 5px;',
		height: 165,
		border: true,
		layout: 'column',
		hideBorders: true,
		defaults: {
			layout: 'form'
		},
		items: [{
			height: 155,
			width: 215,
			labelWidth: 70,
			style: 'margin: 10px 5px 0 8px;border-right: solid 1px #d0d0d0;',
			hideBorders: true,
			items: [
			    elementTypeCombo,
			    platformCombo,
			    participantTB,
			    batchTB
			]
		}, {
			height: 155,
			width: 265,
			style: 'margin: 10px 5px 0 0;border-right: solid 1px #d0d0d0;',
			hideBorders: true,
			items: [
			    diseaseCombo,
			    sampleCombo,
			    {
			    	width: 270,
			    	hideBorders: true,
					layout: 'column',
					labelWidth: 50,
			    	items: [
			    	    {layout: 'form', labelWidth: 50, style: 'margin-right: 25px;', items: vialTB}, 
			    	    {layout: 'form', labelWidth: 50, items: portionTB}
			    	]
			    },
			    analyteCombo,
			    plateTB
			]
		}, {
			width: 340,
			style: 'margin-top: 10px;',
			hideBorders: true,
			labelWidth: 120,
			items: [
			    bcrCombo,
			    {
			    	width: 340,
			    	hideBorders: true,
					layout: 'column',
			    	items: [
			    	    {layout: 'form', style: 'margin-right: 10px;', items: tssTypeCombo}, 
			    	    tssCombo
			    	]
			    },
			    {
			    	width: 340,
			    	hideBorders: true,
					layout: 'column',
			    	items: [
			    	    {layout: 'form', style: 'margin-right: 10px;', items: recCtrTypeCombo}, 
			    	    recCtrCombo
			    	]
			    },
			    {
			    	width: 340,
			    	hideBorders: true,
					layout: 'column',
			    	items: [
			    	    {layout: 'form', labelWidth: 95, style: 'margin-right: 10px;', items: updatedBefore}, 
			    	    {layout: 'form', labelWidth: 85, items: updatedAfter}
			    	]
			    },
			    {
			    	layout: 'column',
			    	hideBorders: true,
			    	items: [{
			    		width: (Ext.isIE?292:297),
			    		html: '<a class="hand" onclick="tcga.uuid.search.webServiceWin();">Get the web service URL for this filter</a>'
			    	}, {
			    		xtype: 'button',
			    		text: 'Search',
						handler: function() {
							browseStore.load();
						}	
			    	}]
			    }
			]
		}]
	});
}

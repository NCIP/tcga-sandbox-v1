/*******************************************************************************
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 ******************************************************************************/

Ext.namespace('tcga.extensions');

/**
 * @class tcga.extensions.selectorParameter
 * @extends Ext.Panel
 * <p></p>
 * @constructor
 * @param {Object} config The config object
 * @xtype selectorparameter
 */
tcga.extensions.selectorParameter = Ext.extend(Ext.Panel, {
	layout: 'column',
	border: false,
	hidden: true,
	interactive: false,
	interactiveLabel: 'Results',
	cls: 'selectorParameter',
	resultsDisplay: null,
	store: null,
	defaults: {
		border: false
	},
	
	// The most basic filter, I expect this to mostly be replaced in the child selectors
	filter: function(rec, id) {
		var recVal = rec.get(this.parameterDesc.dataParam);

		if (recVal == this.value) {
			return true;
		}
		return false;
	},

	interactiveUpdate: function() {
		this.store.filterBy(this.filter, this);
		
		var count = this.store.getCount();
		Ext.get('results' + this.parameterDesc.name).update((count>0?count:'0'));
		this.store.clearFilter(true);
		
		if (this.resultsDisplay != null) {
			this.resultsDisplay.update();
		}
	},
	
	initComponent : function(){
		var headerItems = [];
		if (this.interactive && this.interactive == true) {
			headerItems = [{
				width: 400,
				html: '<span class="selectorParameterTitle">' + this.parameterDesc.name + ':</span>'
			}, {
				width: 190,
				html: '<span class="selectorParameterTitle">' + this.interactiveLabel + ':</span> <a style="text-decoration: underline;cursor: pointer;" onclick="tcga.db2.results.gotoPage(\'' + this.parameterDesc.dataParam + '\');"><span id="results' + this.parameterDesc.name + '">0</span></a>'
			}];
		}
		else {
			// Temp until all selectors upgraded
			if (this.parameterDesc) {
			headerItems = [{
				width: 600,
				html: '<div class="selectorParameterTitle">' + this.parameterDesc.name + ':</div>'
			}];
				
			}
		}
		
		if (this.resultsDisplay != null) {
			this.resultsDisplay.register(this);
		}
		
	 	this.items = headerItems.concat(this.items);

		tcga.extensions.selectorParameter.superclass.initComponent.call(this);
	}
});

Ext.reg('selectorparameter', tcga.extensions.selectorParameter);

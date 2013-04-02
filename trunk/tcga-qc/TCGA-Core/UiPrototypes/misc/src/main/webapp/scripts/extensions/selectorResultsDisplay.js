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
 * @class tcga.extensions.selectorResultsDisplay
 * @extends Ext.Panel
 * <p></p>
 * @constructor
 * @param {Object} config The config object
 * @xtype selectorresultsdisplay
 */
tcga.extensions.selectorResultsDisplay = Ext.extend(Ext.Panel, {
	layout: 'column',
	border: false,
	hidden: true,
	cls: 'selectorParameter',
	defaults: {
		border: false
	},
	store: null,
	label: 'Total Results',
	initValue: 0,
	displayTemplate: '<div class="querySelection label" style="width: 170px;font-weight: normal;line-height: 32px;">{resultsLabel}</div>' +
		'<div id="{id}ResultsCount" class="querySelection" style="text-align: right;width: 120px;font-size: 64px;font-weight: normal;line-height: 64px;position: relative;top: -20px;text-decoration: underline;cursor: pointer;" onclick="tcga.db2.results.gotoPage();">{resultsInitValue}</div>',
	resultsDisplay: null,
	selectors: {},

	// Register selectors that will update the results display
	register: function(selector) {
		this.selectors[selector.parameterDesc.dataParam] = selector;
	},
	
	// Unregisters selectors that from updating the results display
	unregister: function(selector) {
		this.selectors[selector.parameterDesc.dataParam] = null;
	},

	// Unregisters selectors that from updating the results display
	getSelector: function(selectorDataParam) {
		return this.selectors[selectorDataParam];
	},

	update: function() {
		this.store.filterBy(function(rec, id) {
			var accept = false;
			
			for (var currSelectorNdx in this.selectors) {
				var currSelector = this.selectors[currSelectorNdx];
				var value = currSelector.getValue();
				if (value == undefined) {
					continue;
				}
				var recVal = rec.get(currSelector.parameterDesc.dataParam);
				if (recVal == undefined || recVal == null) {
					return false;
				}
				if (currSelector.selectorType == 'combo' || currSelector.selectorType == 'single') {
					if (recVal != value[0]) {
						return false;
					}
				}
				else if (currSelector.selectorType == 'lossGainRange') {
					return false;
				}
				else if (currSelector.selectorType == 'check' || currSelector.selectorType == 'radio') {
					if (value.length == 0) {
						return false;
					}
		
					var tempAccept = false;
					for (var ndx1 = 0;ndx1 < value.length;ndx1++) {
						if (recVal == value[ndx1]) {
							tempAccept = true;
						}
					}
					if (tempAccept == false) {
						return false;
					}
				}
				else if (currSelector.selectorType == 'range') {
					if (recVal < value.start || recVal > value.end) {
						return false;
					}
				}
			}
			
			return true;
		}, this);
		
		this.currCount = this.store.getCount();
		this.store.clearFilter(true);

		this.resultsDisplay.update(this.currCount>0?this.currCount:'0');
	},

	onUpdate: function() {
        this.update();
	},

	initComponent : function(){
		var displayTemplate = new Ext.Template(this.displayTemplate);
		var displayValues = {
			id: this.id,
			resultsLabel: this.label,
			resultsInitValue: new String(this.initValue)
		};
		this.html = displayTemplate.apply(displayValues);
		
		if (this.renderTo) {
			this.element = Ext.get(this.renderTo).update(this.html);
		}
		
		// No get the display component so we don't have to look it up each time we want to update it
		this.resultsDisplay = Ext.get(this.id + 'ResultsCount');
		
		tcga.extensions.selectorResultsDisplay.superclass.initComponent.call(this);
		
     this.addEvents(
         /**
          * @event click
          * Fires after the Panel has been clicked.
          * @param {Ext.Panel} p the Panel which has been resized.
          */
         'update'
	);
	}
});

Ext.reg('selectorresultsdisplay', tcga.extensions.selectorResultsDisplay);

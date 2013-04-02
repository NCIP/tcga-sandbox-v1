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
 * @class tcga.extensions.selectorCheck
 * @extends tcga.extensions.selectorParameter
 * <p></p>
 * @constructor
 * @param {Object} config The config object
 * 	closed
 * 	queryClass, closed, order, parameterDesc, interactive, store
 * @xtype selectorCheck
 */
tcga.extensions.selectorCheck = Ext.extend(tcga.extensions.selectorParameter, {
	selectorType: 'check',

	getValue: function() {
		return this.value;
	},
	
	checkForSavedValue: function(currValue, selectedValues) {
		// No selected values...get out of here
		if (!selectedValues || typeof(selectedValues) == "undefined") {
			return;
		}
		
		var checkedVal = false;
		if (typeof(selectedValues) == 'string' && currValue == selectedValues) {
			checkedVal = true;
		}
		else {
			for (var ndx = 0;ndx < selectedValues.length;ndx++) {
				if (currValue == selectedValues[ndx]) {
					var checkedVal = true;
				}
			}
		}
		
		return checkedVal;
	},
	
	setValue: function(value, updateElement) {
		this.group.setValue(value);

		var currQuery = tcga.db2.query.storage.getCurrQuery();
	
		var cbSetArray = [];
		for (var ndx = 0;ndx < parameterRec.values.length;ndx++) {
			this.checkForSavedValue(parameterRec.values[ndx].value, currQuery.patientClass.getValue(parameterRec.name));
		}
		
		var cbGrp = Ext.getCmp(parameterRec.name);
		cbGrp.suspendEvents(false);
		cbGrp.setValue(cbSetArray);
		cbGrp.resumeEvents();
	},
	
	getCheckBoxActiveCount: function(currQuery, queryClass, order, parameterDesc) {
		var activeCount = 0;
		var activeNdx = -1;
		for (var ndx = 0;ndx < parameterDesc.values.length;ndx++) {
			if (!(parameterDesc.values[ndx].disabled && parameterDesc.values[ndx].disabled == 'true')) {
				activeCount++;
				activeNdx = ndx;
			}
		}
		
		// If the active count is 1 and the type is single, then we just go ahead and put the value
		// 	into the state since that's the only choice that can be made anyway
		if (activeCount == 1) {
			currQuery.updateQueryClass(queryClass, parameterDesc.name, {
				value: [parameterDesc.values[activeNdx].value],
				type: 'checkbox',
				order: order
			});
		}
		
		return activeCount;
	},
	
	filter: function(rec, id) {
		var recVal = rec.get(this.parameterDesc.dataParam);

		// Either is a keyword for radio selectors - should abstract out of here in some fashion
		if (this.value.length == 0 || this.value == 'Either') {
			return true;
		}

		for (var ndx = 0;ndx < this.value.length;ndx++) {
			if (recVal == this.value[ndx]) {
				return true;
			}
		}
		return false;
	},

	initComponent : function(){
		var components = [];
		var param;
		
		var currQuery = tcga.db2.query.storage.getCurrQuery();
	
		var activeCount = this.getCheckBoxActiveCount(currQuery, this.queryClass, this.order, this.parameterDesc);
	
		var cbList = [];
		
		for (var ndx = 0;ndx < this.parameterDesc.values.length;ndx++) {
			var checkedVal = false;
			if (activeCount == 1 && this.parameterDesc.values[ndx].disabled && this.parameterDesc.values[ndx].disabled == 'false') {
				currQuery.updateQueryClass(this.queryClass, this.parameterDesc.name, {
					value: [this.parameterDesc.values[ndx].value],
					type: 'checkbox',
					order: order
				});
	
				tcga.db2.queryWriter.start();
				checkedVal = true;
			}
			else if (tcga.db2.selectors.checkForSavedValue(this.parameterDesc.values[ndx].value, currQuery.patientClass.getValue(this.parameterDesc.name))) {
				checkedVal = true;
			}
			
			cbList.push({
				name: (this.selectorType == 'check'?this.parameterDesc.values[ndx].value:this.parameterDesc.name),
				width: 140,
				value: this.parameterDesc.values[ndx].value,
				boxLabel: this.parameterDesc.values[ndx].value + (this.parameterDesc.values[ndx].comment?' (' + this.parameterDesc.values[ndx].comment + ')':''),
				disabled: ((this.parameterDesc.values[ndx].disabled && this.parameterDesc.values[ndx].disabled == 'true')?true:false),
				checked: checkedVal
			});
		}
	
		this.group = {
			id: this.parameterDesc.name,
			xtype: (this.selectorType == 'check'?'checkboxgroup':'radiogroup'),
			items: cbList,
			listeners: {
				change: {
					fn: function(cbGroup, checkedList) {
						this.value = [];
						if (cbGroup.xtype == 'radiogroup') {
							this.value[0] = checkedList.value;
						}
						else {
							for (var ndx = 0;ndx < checkedList.length;ndx++) {
								if (checkedList[ndx].getValue()) {
									this.value.push(checkedList[ndx].value);
								}
							}
						}
						
						var currQuery = tcga.db2.query.storage.getCurrQuery();
						currQuery.updateQueryClass(this.queryClass, this.parameterDesc.name, {
							value: [this.value],
							type: 'checkbox',
							order: this.order
						});
		
						tcga.db2.queryWriter.start();
							
						if (this.interactive == true) {
							this.interactiveUpdate();
						}
					},
					scope: this
				}
			}
		}
		
		this.items = [this.group];
		
		tcga.extensions.selectorCheck.superclass.initComponent.call(this);
	}
});

Ext.reg('selectorcheck', tcga.extensions.selectorCheck);

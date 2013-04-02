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
 * @class tcga.extensions.selectorCombo
 * @extends tcga.extensions.selectorParameter
 * <p></p>
 * @constructor
 * @param {Object} config The config object
 * 	closed
 * 	queryClass, closed, order, parameterRec, interactive, store
 * @xtype selectorcombo
 */
tcga.extensions.selectorCombo = Ext.extend(tcga.extensions.selectorParameter, {
	selectorType: 'combo',

	initValue: 0,
		
	getValue: function() {
		return this.value;
	},
	
	setValue: function(value) {
		this.combo.setValue(value);
	},
	
	filter: function(rec, id) {
		var recVal = rec.get(this.parameterDesc.dataParam);

		if (this.value.length == 0) {
			return true;
		}

		return (recVal == this.value[0]?true:false);
	},

	initComponent : function(){
		this.initValue = tcga.db2.query.storage.getCurrQuery().patientClass.getValue(this.parameterDesc.name);

		this.combo = {
			id: this.name,
			xtype: 'combo',
			mode: 'local',
			store: new Ext.data.JsonStore({
				fields: [
					'desc',
					'value'
				],
				data: this.parameterDesc.values
			}),
			triggerAction: 'all',
			displayField: 'desc',
			valueField : 'value',
			value: (this.initValue?this.initValue.value:undefined),
			emptyText:'Select a ' + this.parameterDesc.name,
			width: 270,
			data: this.parameterDesc.values,
			listeners: {
				select: {
					fn: function(combo, rec) {
						this.value = rec.get('value');
						
						tcga.db2.query.storage.getCurrQuery().updateQueryClass(this.queryClass, this.parameterDesc.name, {
							desc: rec.get('desc'),
							value: this.value,
							type: this.selectorType,
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
		};

		this.items = [{
			width: 600,
			items: [this.combo]
		}];
		
		tcga.extensions.selectorCombo.superclass.initComponent.call(this);
	}
});

Ext.reg('selectorcombo', tcga.extensions.selectorCombo);

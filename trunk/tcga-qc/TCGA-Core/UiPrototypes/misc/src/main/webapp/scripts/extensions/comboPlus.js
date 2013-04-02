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
 * @class tcga.extensions.comboPlus
 * @extends Ext.Panel
 * <p>Combo Plus adds an id to the trigger making up the standard combo to make it
 * easier to click the trigger with Selenium.  It also adds an id to each item in
 * the dropdown so that those are easier to select with Selenium.  And, it gives
 * a qtip option!  Note that the qtip option is not compatible with the tpl option.
 * AND, for a limited time it comes with TWO (2) ginsu knives and a Pocket Fisherman
 * if you use the NEW, NEW comboPlus with lazy rendering!  Just kidding, what you
 * actually get is the tpl being used in the display after it's selected.  Naturally
 * you can still override the tpl, but you can also override just the display part
 * of the template using the new param tplDisplay.  Setting tplDisplay will replace
 * just the part of the tpl that gets shown to the user.  Remember to surround store
 * values in tplDisplay with {}.  For example, to use the name and id fields from the
 * combo store, use "tplDisplay: '{name} ({id})' which would then display something
 * like "Fred (123) to the user.</p>
 * @constructor
 * @param {Object} config The config object
 * @xtype comboplus
 */
tcga.extensions.comboPlus = Ext.extend(Ext.form.ComboBox, {
	protoTriggerConfig: {tag: "img", src: Ext.BLANK_IMAGE_URL, cls: "x-form-trigger " + this.triggerClass},
	protoTpl: '<tpl for="."><div id="{id}{[xindex]}" {qtip} class="x-combo-list-item">{display}</div></tpl>',

	initComponent : function(){
		if (!this.triggerConfig) {
			this.triggerConfig = this.protoTriggerConfig;
			this.triggerConfig.id = this.id + 'Trigger';
		}
		
		if (!this.tplDisplay) {
			this.tplDisplay = '{' + this.displayField + '}';
		}
		
		if (!this.tpl) {
			var tpl = new Ext.Template(this.protoTpl);
			this.tpl = tpl.apply({
				id: this.id,
				qtip: this.qtip?'ext:qtip="' + this.qtip + '"':'',
				display: this.tplDisplay
			});
		}
		
		this.xtpl = new Ext.XTemplate(this.tplDisplay);
		this.xtpl.compile();
		
		tcga.extensions.comboPlus.superclass.initComponent.call(this);
	},
	
	setValue : function(v){
		var text = v;
		if(this.valueField){
			var r = this.findRecord(this.valueField, v);
			if(r){
				text = r.data[this.displayField];
			}
			else if(Ext.isDefined(this.valueNotFoundText)){
				text = this.valueNotFoundText;
			}
		}
		this.lastSelectionText = text;
		if(this.hiddenField){
			this.hiddenField.value = Ext.value(v, '');
		}
		Ext.form.ComboBox.superclass.setValue.call(this, r?this.xtpl.apply(r.data):text);
		this.value = v;
		return this;
	}
});

Ext.reg('comboplus', tcga.extensions.comboPlus);

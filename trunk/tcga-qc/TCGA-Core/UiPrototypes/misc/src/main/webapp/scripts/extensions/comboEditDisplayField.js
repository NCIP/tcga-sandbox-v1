Ext.namespace('marcs.extensions');

/**
 * @class marcs.extensions.ComboEditDisplayField
 * @extends Ext.Panel
 * <p>An edit display field with a date field instead of a text field</p>
 * @constructor
 * @param {Object} config The config object
 * @xtype comboeditdisplayfield
 */
marcs.extensions.ComboEditDisplayField = Ext.extend(marcs.extensions.EditDisplayField, {
	mode: 'local',
	data: null,
	
	getEditValue: function(valueDisplay) {
		var combo = Ext.getCmp(this.id + '-Combo');
		var store = combo.getStore();
		var selRec = store.getAt(store.find(this.valueField, combo.getValue()));
		if (selRec == undefined) {
			return this.emptyValue;
		}
		return selRec.get(this.valueField);
	},

    initComponent : function(){
		this.id = (this.id?this.id:Ext.id());
		
		if (this.data && this.data.length > 0) {
			var storeData = [];
			for (var ndx = 0;ndx < this.data.length;ndx++) {
				storeData.push([this.data[ndx]]);
			}
			
			this.store = new Ext.data.ArrayStore({
				fields: ['field'],
				autoLoad: true,
				data: storeData
			});
			this.displayField = 'field';
			this.valueField = 'field';
		}
		
		this.editConfig = {
			id: this.id + '-Combo',
			xtype: 'combo',
			store: this.store,
			width: 120,
			emptyText: this.emptyText,
			typeAhead: true,
			mode: this.mode,
			triggerAction: 'all',
			forceSelection: true,
			displayField: this.displayField,
			valueField: this.valueField,
			listeners: {
				render: function(combo) {
					if (this.value == this.emptyValue) {
						combo.setValue('');
						return null;
					}
					
					var selNdx = combo.getStore().find(combo.valueField, this.value);
					if (selNdx == -1) {
						combo.setValue('');
						return null;
					}
					var selRec = combo.getStore().getAt(selNdx);
					combo.setValue(selRec.get(combo.valueField));
				}
			}
		};

        marcs.extensions.RadioEditDisplayField.superclass.initComponent.call(this);
	}
});

Ext.reg('comboeditdisplayfield', marcs.extensions.ComboEditDisplayField);

Ext.namespace('marcs.extensions');

/**
 * @class marcs.extensions.RadioEditDisplayField
 * @extends Ext.EditDisplayField
 * <p>And edit display field with yes and no radios.</p>
 * @constructor
 * @param {Object} config The config object
 * @xtype radioeditdisplayfield
 */
marcs.extensions.RadioEditDisplayField = Ext.extend(marcs.extensions.EditDisplayField, {
	handler: null,
	radioSelections: ['yes', 'no'],
	
    initComponent : function(){
		var radioId = (this.id?this.id:Ext.id());
		
		var radioItems = [];
		for (var ndx = 0;ndx < this.radioSelections.length;ndx++) {
			radioItems.push({
				id: radioId + '-' + this.radioSelections[ndx],
				boxLabel: this.radioSelections[ndx],
				value: this.radioSelections[ndx]
			});
		}
		
		this.editConfig = {
			border: false,
			items: [{
				xtype: 'radiogroup',
				columns: 1,
				defaults: {
					name: Ext.id()
				},
				items: radioItems,
				listeners: {
					change: {
						fn: this.setValueOnChange,
						scope: this
					},
					render: {
						fn: function(rg) {
							rg.setValue(radioId + '-' + this.getValue(), true);
						},
						scope: this
					}
				}
			}]
		};

        marcs.extensions.RadioEditDisplayField.superclass.initComponent.call(this);

        this.addEvents(
            /**
             * @event click
             * Fires after the Panel has been clicked.
             * @param {Ext.Panel} p the Panel which has been resized.
             */
            'change'
		);
	},

	getEditValue: function(valueDisplay) {
		var selectedRadio = valueDisplay.get(0).getValue();
		return (selectedRadio?selectedRadio.value:this.emptyValue);
	},
	
	setValueOnChange: function(rg, radioChecked) {
		this.setValue(radioChecked.value);

        this.fireEvent('change', this, radioChecked.value);
	}
});

Ext.reg('radioeditdisplayfield', marcs.extensions.RadioEditDisplayField);

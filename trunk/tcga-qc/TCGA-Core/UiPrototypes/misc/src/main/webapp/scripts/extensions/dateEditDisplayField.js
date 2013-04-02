Ext.namespace('marcs.extensions');

/**
 * @class marcs.extensions.DateEditDisplayField
 * @extends Ext.Panel
 * <p>An edit display field with a date field instead of a text field</p>
 * @constructor
 * @param {Object} config The config object
 * @xtype dateeditdisplayfield
 */
marcs.extensions.DateEditDisplayField = Ext.extend(marcs.extensions.EditDisplayField, {
	handler: null,
	editWidth: 100,

	editConfig: {
		xtype: 'datefield',
		height: 25
	},
	
	getEditValue: function(valueDisplay) {
		var selectedDate = valueDisplay.getValue();
		return (selectedDate?selectedDate.format('m/d/Y'):this.emptyValue);
	},

    initComponent : function(){
        marcs.extensions.DateEditDisplayField.superclass.initComponent.call(this);

        this.addEvents(
            /**
             * @event click
             * Fires after the Panel has been clicked.
             * @param {Ext.Panel} p the Panel which has been resized.
             */
            'select'
		);
	}
});

Ext.reg('dateeditdisplayfield', marcs.extensions.DateEditDisplayField);

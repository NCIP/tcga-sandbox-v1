Ext.namespace('marcs.extensions');

/**
 * @class marcs.extensions.EditDisplayField
 * @extends Ext.Panel
 * <p>Clickable Panel is a panel that adds the click event.</p>
 * @constructor
 * @param {Object} config The config object
 * @xtype valuefield
 */
marcs.extensions.EditDisplayField = Ext.extend(Ext.Panel, {
	layout: 'column',
	columnWidth: 1,
	labelWidth: 200,
	displayWidth: 300,
	editWidth: 300,
	labelCls: 'stdLabel right',
	dataCls: 'stdData',
	noteCls: 'stdNote',
	noData: false,
	required: false,
	emptyValue: 'No value selected',
	// The value of the field
	value: null,
	// The mapping of the field to a store supplying a value
	mapping: null,
	style: 'padding-bottom: 5px;',
	displayConfig: {
		xtype: 'panel',
		border: false,
		cls: 'stdData',
		height: 25
	},
	editConfig: {
		xtype: 'textfield',
		height: 25
	},
	getEditValue: function(valueDisplay) {
		return valueDisplay.getValue();
	},

    initComponent : function(){
		if (this.autoWidth) {
			this.editWidth = this.displayWidth = (document.documentElement.clientWidth * this.columnWidth) - this.labelWidth;
		}
		
		if (this.dataWidth) {
			this.editWidth = this.displayWidth = this.dataWidth;
		}

		if (this.id == null) {
			if (this.mapping) {
				this.id = this.mapping;
			}
			else if (this.labelText) {
				this.id = this.labelText;
			}
		}
		
		if (this.value == null) {
			this.value = this.emptyValue;
		}
		
		if (this.required) {
			this.labelCls += ' required';
		}
		else {
			this.labelCls += ' notRequired';
		}
		
		this.items = [{
			xtype: 'labelpanel',
			border: false,
			required: this.required,
			labelWidth: this.labelWidth,
			labelText: this.labelText
		}];
		
		// Spacer panel between the label and the data
		// The spacer panel is used so that we don't have to try to put padding on the display/edit field
		//		since that is always changing and may be user defined, in which case we have no control of it.
		this.items.push({
			xtype: 'panel',
			border: false,
			width: 10,
			html: '&nbsp;'
		});
		
		if (!this.noData) {
			if (this.edit) {
				this.items.push(this.getEditConfig());
			}
			else {
				this.items.push(this.getDisplayConfig());
			}
		}
		
		if (this.note) {
			this.items.push({
				xtype: 'panel',
				id: 'notenote',
				columnWidth: 1,
				border: false,
				cls: this.noteCls,
				style: 'padding-left: ' + (this.labelWidth + 13) + 'px;',
				html: this.note
			});
		}

        marcs.extensions.EditDisplayField.superclass.initComponent.call(this);
	},
	
	onResize: function(w, h) {
		if (this.autoWidth) {
			this.editWidth = this.displayWidth = (w * this.columnWidth) - this.labelWidth;
		}
	},
	
	getId: function() {
		return this.id;
	},
	
	getValue: function() {
		if (this.edit) {
			return this.getEditValue(this.get(2));
		}
		return this.value;
	},
	
	setValue: function(newValue) {
		this.value = newValue;
	},
	
	refresh: function(toggleDisplay) {
		if (toggleDisplay == undefined) {
			toggleDisplay = false;
		}
		if (!this.edit && toggleDisplay) {
			this.showEditConfig();
		}
		else {
			this.showDisplayConfig(toggleDisplay);
		}

		this.doLayout();
	},

	// Todo: Set this up so it recognizes that there may have been an alternate config passed in
	getDisplayConfig: function() {
		return Ext.apply(this.displayConfig, {
			width: this.displayWidth,
			html: String(this.value)
		});
	},
	
	getEditConfig: function() {
		return Ext.apply(this.editConfig, {
			name: 'edit-' + this.labelText,
			width: this.editWidth,
			value: this.value
		});
	},
	
	showDisplayConfig: function(toggleDisplay) {
		var valueDisplay = this.get(2);

		// First grab the value in case it has been changed.
		// 	TODO: Make this sensitive to the fact that there are multiple types os fields we may see here
		// BUT!  If this is just a refresh, no need to grab the value
		if (toggleDisplay) {
			this.value = this.getEditValue(valueDisplay);
		}
		this.remove(valueDisplay);
		this.insert(2, this.getDisplayConfig());
	},
	
	showEditConfig: function() {
		var valueDisplay = this.get(2);

		this.remove(valueDisplay);
		this.insert(2, this.getEditConfig());
	},
	
	toggleEdit: function() {
		if (this.noData) {
			// Just a title to display, no data
			return;
		}

		this.refresh(true);
		
		this.edit = !this.edit;
	}
});

Ext.reg('editdisplayfield', marcs.extensions.EditDisplayField);

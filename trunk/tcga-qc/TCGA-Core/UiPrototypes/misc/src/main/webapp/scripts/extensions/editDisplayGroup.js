Ext.namespace('marcs.extensions');

/**
 * @class marcs.extensions.EditDisplayGroup
 * @extends Ext.Panel
 * <p>Clickable Panel is a panel that adds the click event.</p>
 * @constructor
 * @param {Object} config The config object
 * @xtype valuefield
 */
marcs.extensions.EditDisplayGroup = Ext.extend(Ext.Panel, {
	layout: 'column',
	columnWidth: 1,
	labelWidth: 200,
	titleCls: 'stdTitle',
	controlCls: 'stdOnClickLink',
	defaultEditControl: 'edit',
	defaultUpdateControl: 'update',
	defaultType: 'editdisplayfield',
	first: false,
	last: false,
	hideEdit: false,
	edit: false,
	groupTitle: null,
	wizardNavPrev: null,
	wizardNavNext: null,
	hideNav: true,
	defaults: {
		border: false,
		edit: false
	},

    initComponent : function(){
		this.items = [{
			id: Ext.id() + '-cmdPanel',
			xtype: 'panel',
			layout: 'column',
			columnWidth: 1,
			items: [{
				border: false,
				cls: this.titleCls,
				// Add 13 to move the edit link over to align with the data
				width: this.labelWidth + 13,
				hidden: (this.groupTitle?false:true),
				html: this.groupTitle
			}, {
				id: Ext.id() + '-edit',
				xtype: 'clickpanel',
				border: false,
				hidden: this.hideEdit,
				cls: this.controlCls,
				padding: '1px 0px 0px 23px;',
				html: this.defaultEditControl,
				listeners: {
					click: {
						fn: this.toggleEdit,
						scope: this
					}
				}
			}, {
				id: Ext.id() + '-update',
				xtype: 'clickpanel',
				border: false,
				hidden: true,
				cls: this.controlCls,
				padding: '1px 0px 0px 23px;',
				html: this.defaultUpdateControl,
				listeners: {
					click: {
						fn: this.toggleEdit,
						scope: this
					}
				}
			}, {
				hidden: this.hideNav,
				border: false,
				cls: 'racReportWizardControls',
				html: this.htmlForWizNav()
			}]},
			this.fields
		];

        marcs.extensions.EditDisplayGroup.superclass.initComponent.call(this);
	},
	
	htmlForWizNav: function() {
		var html = '';
		
		if (!this.first) {
			html += marcs.util.display.displayImage({
				imagePath: 'wizard/arrow_left_32.png',
				qtip: 'Prev page of form',
				alt: 'This is the alt text',
				action: this.wizardNavPrev
			});
		}
		if (!this.last) {
			html += marcs.util.display.displayImage({
				imagePath: 'wizard/arrow_right_32.png',
				qtip: 'Next page of form',
				action: this.wizardNavNext
			});
		}
		
		return html;
	},
	
	toggleEdit: function() {
		if (!this.hideEdit) {
			var cmdPanel = this.items.get(this.items.findIndex('id', /-cmdPanel$/));
			var edit = cmdPanel.items.get(cmdPanel.items.findIndex('id', /-edit$/));
			var update = cmdPanel.items.get(cmdPanel.items.findIndex('id', /-update$/));
			if (this.edit) {
				edit.show();
				update.hide();
			}
			else {
				edit.hide();
				update.show();
			}
		}
		
		for (var ndx = 1;ndx < this.items.length;ndx++) {
			this.get(ndx).toggleEdit();
		}

		this.edit = !this.edit;
	},
	
	getValue: function(flatten) {
		if (flatten == undefined) {
			flatten = false;
		}
		
		var values = {};
		for (var ndx0 = 1;ndx0 < this.items.length;ndx0++) {
			var value = this.get(ndx0).getValue();
			// Check to see if the value coming back is an array...this is what we would get
			// 	from another editDisplayGroup.  We flatten this out if flatten is true.
			//	We only have to do this to one level of depth since recursion will take care of the rest.
			if (value instanceof Array && flatten) {
				values = marcs.util.object.concat(values, value);
			}
			else {
				values[this.get(ndx0).getId()] = value;
			}
		}
		
		return values;
	}
});

Ext.reg('editdisplaygroup', marcs.extensions.EditDisplayGroup);

/**
 * @author HuoW
 */
Ext.namespace('marcs.popups');

marcs.popups.inBoxPreferenceData = [
	[0, 'View all assignments including ones for which outgoing work has been issued'],
	[1, 'View only assignments that have not been assigned to others'],
	[2, 'View cancelled work'],
	[3, 'View terminated work'],
	[4, 'View finished work']
];


marcs.popups.inBoxPreferences = function() {
		
	var winWidth = 450;
	var preferenceSM = new Ext.grid.CheckboxSelectionModel();
	var preferenceGrid = new Ext.grid.GridPanel({
		id: 'preferenceData',
		border: false,
		padding: '0px 3px 0px 3px',
		width: winWidth,
		autoHeight: true,				
		store: new Ext.data.SimpleStore({
			fields: [
				'preferenceNum',
				'preferenceDesc'
			],
			data: marcs.popups.inBoxPreferenceData,
			autoload: true
		}),
		selModel: preferenceSM,
		columns: [
		preferenceSM,
		{
			id: 'preferenceDesc',
			dataIndex: 'preferenceDesc',
			header: '<b>Description</b>',
			sortable: false,
			//width of the browser - checkbox - (width taking from the grid) - (grid border adj)
			width: winWidth - 20 - 10
		}],		
		buttons: [{			
			xtype: 'button',
			text: 'Save',
			handler: marcs.waam.handlers.assignment.savePreference
		}, {
			xtype: 'clickpanel',
			border: false,
			style: 'padding-left: 5px; color: purple',
			cls: 'stdOnClickLink',
			minWidth: 80,
			html: 'Cancel',
			handler: function() {
				Ext.getCmp('preferenceWindow').close();
			}
		}],
		buttonAlign: 'center'
	});

	var preferenceWindow = new Ext.Window({
		id: 'preferenceWindow',
		title: 'In Box Preferences',
		autoHeight: true,
		width: winWidth + 15,
		modal: true,
		closable: true,	
		items: [{
			border: false,
			cls: 'stdLabel',
			style: 'padding: 5px;border-bottom: 1px solid black;background-color: white;font: 14px arial;',
			html: 'Select what should be displayed in your inbox'
		},
			preferenceGrid
		]
	});

	preferenceWindow.show();
}

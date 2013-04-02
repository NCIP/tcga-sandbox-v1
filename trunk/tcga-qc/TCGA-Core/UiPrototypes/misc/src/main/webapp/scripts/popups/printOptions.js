/**
 * @author HuoW
 */
Ext.namespace('marcs.popups');

marcs.popups.printOptions = function() {		
	var winWidth = 450;
	var dataPanel = new Ext.FormPanel({	
		id: 'printOptionData',
		border: false,
		hideBorders: true,
		padding: '0px 3px 0px 3px',
		autoHeight: true,
		width: winWidth,
		items: [{
			border: false,
			cls: 'stdTitle',
			width: winWidth,				
			html: 'Print Options'			
		}, {
			border: false,
			width: winWidth,
			style: 'padding-top: 20px; padding-left: 20px;',				
			html: 'Select the observation that you would like to print'		
		} , {
			xtype: 'panel',
			id: 'printRadio',
			border: false,
			style: 'padding-top: 10px;',
			layout: 'column',
			width: winWidth,
			items: [{
				border: false,
				autoHeight: true,
				width: 50,
				html: '&nbsp;'	
			} , {							
				xtype: 'radio',
				name: 'printRadio',
				checked: true,
				boxLabel: 'All Observations',
				inputValue: '1',
				width: winWidth - 50
			} , {
				border: false,
				autoHeight: true,
				width: 50,
				html: '&nbsp;'	
			} , {			
				xtype: 'radio',
				name: 'printRadio',
				boxLabel: 'Only those observations that are not on the Form FDA 483',
				inputValue: '2',
				width: winWidth - 50
			} , {
				border: false,
				autoHeight: true,
				width: 50,
				html: '&nbsp;'	
			} , {			
				xtype: 'radio',
				name: 'printRadio',
				boxLabel: 'Only those observations that are on the Form FDA 483',
				inputValue: '3',
				width: winWidth - 50
			}]
		}, {			
			buttons: [{
				style: 'padding-left: 5px;',
				minWidth: 80,
				text: '<b>Print</b>'		
			}, {
				xtype: 'clickpanel',
				style: 'padding-left: 5px; color: purple',
				cls: 'stdOnClickLink',
				minWidth: 80,
				html: 'Cancel',
				handler: function() {
					Ext.getCmp('printOptionsWindow').close();
				}
			}],
			buttonAlign: 'center'		
		}]
	});

	var printOptionsWindow = new Ext.Window({
		id: 'printOptionsWindow',
		width: winWidth + 20,
		border: true,
		closable: true,
		plain: true,
		items: dataPanel
	});

	printOptionsWindow.show();
}

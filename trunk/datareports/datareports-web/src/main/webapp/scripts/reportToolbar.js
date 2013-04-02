/*******************************************************************************
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 ******************************************************************************/

/*
 * @class tcga.dataReports
 * @extends Ext.Toolbar
 * <p>The common report toolbar as an extension of the regular toolbar.</p>
 * @param (Object} config The config object
 * @cfg {String} exportUrl
 * <p>The function for exporting this report</p>
 * @cfg {String} filterFunction
 * <p>The filterUrl function for this particular report</p>
 * @constructor
 * @xtype reportstoolbar
 */

Ext.namespace('tcga.dataReports');

tcga.dataReports.toolbar = Ext.extend(Ext.Toolbar, {
    initComponent: function() {
        this.items = [{
            menu: [
               {text: 'Excel',iconCls: 'icon-xl',handler: this.exportUrlxl},
               {text: 'CSV',iconCls: 'icon-txt',handler: this.exportUrlcsv},
               {text: 'Tab-delimited',iconCls: 'icon-txt',handler: this.exportUrltab},
               {text: 'Filter Url',iconCls: 'icon-page_code',handler: this.filterFunction}
            ],
            text: 'Export Data',
            iconCls: 'icon-grid'
        }
         ,'-',{ xtype: 'buttonplus', text: 'Show Filters',enableToggle:true,id:'fToggle',iconCls: 'icon-filter',
                toggleHandler: function(btn, pressed){
                    if (pressed) {
                        btn.setText('Hide Filters');
                        Ext.getCmp('nord').expand(true);
                    }
                    else {
                        btn.setText('Show Filters');
                        Ext.getCmp('nord').collapse(true);
                    }
               }}
       ];

       tcga.dataReports.toolbar.superclass.initComponent.call(this);
     }
});

Ext.reg('reportstoolbar', tcga.dataReports);

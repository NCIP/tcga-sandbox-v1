/*******************************************************************************
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 ******************************************************************************/

// JavaScript Document
//Domi js Scripts

function ahref(val) {window.location = val;}

function toggleDisplay(id) {
  var e = document.getElementById(id);
    if(e.style.display == 'none') e.style.display = 'block';
    else e.style.display = 'none';
}

function showHelp(renderDiv, topic) {
    var win = new Ext.ux.ManagedIFrame.Window({
        title:"TCGA Data Reports Online Help",
        renderTo: renderDiv,
        width:920,
        height:550,
        plain: true,
        collapsible: false,
        closeAction: 'close',
        loadMask: {msg: 'Loading...'},
        defaultSrc: 'help/index.html?context=TCGA_Data_Portal_Data_Reports&topic='+topic,
        buttons: [
            {text: 'Close',handler: function() {
                win.hide();
            }}
        ]
    });
    win.show();
}

function showFilterUrl(renderDiv,url) {
    var win = new Ext.Window({
        renderTo: renderDiv,
        title:"Filters URL",
        closable:true,
        width:500,
        height:150,
        items: new Ext.form.FormPanel({
            baseCls: 'x-plain',
            layout: {type: 'vbox',align: 'stretch'},
            items:[
                {
                    xtype: 'textarea',name: 'filterUrl',flex: 1,
                    value: url
                }
            ]
        }),
        collapsible: true,
        animCollapse: true,
        layout: 'fit',
        buttons: [
            {text: 'Close',handler: function() {
                win.hide();
            }}
        ]
    });
    win.show();
}
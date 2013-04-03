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
	var landingUrl = "https://wiki.nci.nih.gov/x/uQI2Ag";
    window.open(landingUrl);
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
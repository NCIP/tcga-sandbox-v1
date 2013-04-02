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
	
	var landingUrl = "https://wiki.nci.nih.gov/x/tZghAg";
	
	if (topic == "aliquot_ID_breakdown_report") {
		landingUrl = "https://wiki.nci.nih.gov/x/JZohAg";
	} else if (topic == "exp_aliquot_report") {
		landingUrl = "https://wiki.nci.nih.gov/x/QpohAg";
	} else if (topic == "bam_telemetry_report") {
		landingUrl = "https://wiki.nci.nih.gov/x/3gxyAg";
	} else if (topic == "pipeline_report") {
		landingUrl = "https://wiki.nci.nih.gov/x/LZohAg";
	} else if (topic == "code_tables_report") {
		landingUrl = "https://wiki.nci.nih.gov/x/MpohAg";
	} else if (topic == "latest_archive_report") {
		landingUrl = "https://wiki.nci.nih.gov/x/L5ohAg";
	} else if (topic == "projectCaseDashboard") {
		landingUrl = "https://wiki.nci.nih.gov/x/lQ9yAg";
	} else if (topic == "count_summary_report") {
		landingUrl = "https://wiki.nci.nih.gov/x/I5ohAg";
	} else if (topic == "pendingUUIDReport") {
		landingUrl = "https://wiki.nci.nih.gov/x/xAuSB";
	}
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
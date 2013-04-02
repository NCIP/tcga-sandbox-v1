/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

/*******************************************************************************
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 ******************************************************************************/

Ext.onReady(function () {
    Ext.QuickTips.init();
    var formFilterValues;
    var serverUrl = Ext.get('serverUrl').getAttribute('value');
    var filterMap = {}; //order of member is the same as the form layout
    filterMap.aliquotId = Ext.get('aliquotId').getAttribute('value');
    filterMap.analyteId = Ext.get('analyteId').getAttribute('value');
    filterMap.sampleId = Ext.get('sampleId').getAttribute('value');
    filterMap.participantId = Ext.get('participantId').getAttribute('value');

//Create the data store
    var aliquotIdBreakdownStore = new Ext.data.JsonStore({
        url: "aliquotIdBreakdownReport.json",
        remoteSort: true,
        autoLoad: {params: {start: 0, limit: 50, "filterReq": Ext.util.JSON.encode(filterMap)}},
        root: 'aliquotIdBreakdownData',
        totalProperty: 'totalCount',
        fields: [
            {name: 'aliquotId'},
            {name: 'analyteId'},
            {name: 'sampleId'},
            {name: 'participantId'},
            {name: 'project'},
            {name: 'tissueSourceSite'},
            {name: 'participant'},
            {name: 'sampleType'},
            {name: 'vialId'},
            {name: 'portionId'},
            {name: 'portionAnalyte'},
            {name: 'plateId'},
            {name: 'centerId'},
            {name: 'valid'}
        ]
    });

//Create comboBox for page selection
    var combo = new Ext.form.ComboBox({
        name: 'perpage',
        width: 50,
        store: new Ext.data.ArrayStore({
            fields: ['id'],
            data: [
                ['50'],
                ['500'],
                ['1000']
            ]}),
        mode: 'local',
        value: '50',
        listWidth: 40,
        triggerAction: 'all',
        displayField: 'id',
        valueField: 'id',
        editable: false,
        forceSelection: true
    });

//Create paging Toolbar
    var paging = new Ext.PagingToolbar({
        pageSize: 50,
        store: aliquotIdBreakdownStore,
        displayInfo: true,
        displayMsg: 'Displaying Aliquot Id {0} - {1} of {2}',
        emptyMsg: "No Aliquot Id to display",
        items: ['-', 'Per Page ', combo]
    });

    combo.on('select', function (combo, record) {
        paging.pageSize = parseInt(record.get('id'), 10);
        paging.doLoad(paging.cursor);
    }, this);

    //Create exports Toolbar
    var exports = new tcga.dataReports.toolbar({
        exportUrlxl: function () {
            exportUrl("xl");
        },
        exportUrlcsv: function () {
            exportUrl("csv");
        },
        exportUrltab: function () {
            exportUrl("tab");
        },
        filterFunction: function () {
            showFilterUrl('aliquotIdBreakdownReportDiv', serverUrl +
                '/datareports/aliquotIdBreakdownReport.htm?' + Ext.urlEncode(formFilterValues));
        }
    });

//Create resize Panel
    var resizeGrid = new Ext.ux.PanelResizer({
        minHeight: 500
    });

//Create the Grid
    var aliquotIdBreakdownGrid = new Ext.grid.GridPanel({
        store: aliquotIdBreakdownStore,
        columns: [
            {header: 'Aliquot ID', width: 200, sortable: true, dataIndex: 'aliquotId', id: 'aliquotId',
                hideable: false, renderer: breakdownAliquot, tooltip: 'Aliquot Id'},
            {header: 'Analyte ID', width: 200, sortable: true, dataIndex: 'analyteId', renderer: breakdownAnalyte,
                tooltip: 'Analyte Id'},
            {header: 'Sample ID', width: 150, sortable: true, dataIndex: 'sampleId', renderer: breakdownSample,
                tooltip: 'Sample Id'},
            {header: 'Participant ID', width: 150, sortable: true, dataIndex: 'participantId',
                renderer: breakdownParticipant, tooltip: 'Participant Id'}
        ],
        stripeRows: true,
        forceFit: true,
        autoExpandColumn: 'aliquotId',
        height: 500,
        width: 750,
        frame: true,
        loadMask: true,
        tbar: exports,
        bbar: paging
    });

    //Create export url
    function exportUrl(type) {
        var dir, sort, cols;
        var sortState = aliquotIdBreakdownGrid.store.getSortState();
        if (sortState) {
            dir = sortState.direction;
            sort = sortState.field;
        }
        cols = aliquotIdBreakdownGrid.getColumnModel().getColumnsVisible(true, 'dataIndex');
        var form = Ext.DomHelper.append(document.body, {
            tag: 'form',
            method: 'POST',
            action: 'aliquotIdBreakdownExport.htm',
            children: [
                {tag: 'input', type: 'hidden', name: 'exportType', value: type},
                {tag: 'input', type: 'hidden', name: 'dir', value: dir},
                {tag: 'input', type: 'hidden', name: 'sort', value: sort},
                {tag: 'input', type: 'hidden', name: 'cols', value: "" + cols},
                {tag: 'input', type: 'hidden', name: 'filterReq', value: Ext.util.Format.htmlEncode(Ext.util.JSON.encode(filterMap))},
                {tag: 'input', type: 'hidden', name: 'formFilter', value: Ext.util.Format.htmlEncode(Ext.util.JSON.encode(formFilterValues))}
            ]
        });
        document.body.appendChild(form);
        form.submit();
        document.body.removeChild(form);
    }

    var filterBox = new Ext.form.FormPanel({
        frame: true,
        id: 'filterBox',
        labelWidth: 85,
        margins: '3 3 0 0',
        layout: 'column',
        items: [
            {width: 350, layout: 'form', items: [
                {id: 'aliquotIdTextField', fieldLabel: 'Aliquot Id', name: 'aliquotId', xtype: 'textfield', width: 200,
                    emptyText: "Search Aliquot Id...", value: filterMap.aliquotId},
                {id: 'analyteIdTextField', fieldLabel: 'Analyte Id', name: 'analyteId', xtype: 'textfield', width: 200,
                    emptyText: "Search Analyte Id...", value: filterMap.analyteId}
            ]},
            {width: 350, layout: 'form', items: [
                {id: 'sampleIdTextField', fieldLabel: 'Sample Id', name: 'sampleId', xtype: 'textfield', width: 200,
                    emptyText: "Search Sample Id...", value: filterMap.sampleId},
                {id: 'participantIdTextField', fieldLabel: 'Participant Id', name: 'participantId', xtype: 'textfield', width: 200,
                    emptyText: "Search Participant Id...", value: filterMap.participantId}
            ]}
        ],
        buttons: [
            {xtype: 'buttonplus', id: 'filterNow', text: 'Filter Now', handler: function () {
                formFilterValues = filterBox.getForm().getFieldValues();
                aliquotIdBreakdownStore.load({params: {start: 0, limit: paging.pageSize}});
            }},
            {xtype: 'buttonplus', id: 'clearFilters', text: 'Clear Filters', handler: function () {
                filterBox.getForm().clear();
                formFilterValues = filterBox.getForm().getFieldValues();
                aliquotIdBreakdownStore.load({params: {start: 0, limit: paging.pageSize}});
            }}
        ]
    });

    //Create resize Panel
    var resizePanel = new Ext.ux.PanelResizer({
        minHeight: 550
    });

    var panel = new Ext.Panel({
        title: 'Aliquot Id Breakdown Report',
        renderTo: 'aliquotIdBreakdownReportDiv',
        style: 'margin: auto;',
        width: 900,
        height: 650,
        iconCls: 'icon-report',
        layout: 'border',
        bodyStyle: 'z-index: 0;',
        frame: true,
        plugins: [resizePanel],
        items: [
            {
                title: 'Filters Extended',
                region: 'north',
                height: 125,
                minSize: 125,
                maxSize: 125,
                collapsible: true,
                collapsed: true,
                collapseMode: 'mini',
                listeners: {collapse: toggleOff, expand: toggleOn},
                floatable: false,
                cmargins: '0 0 2 0',
                id: 'nord',
                layout: 'fit',
                split: true,
                items: [filterBox]
            },
            {region: 'center', layout: 'fit', id: 'centre', items: [aliquotIdBreakdownGrid]}
        ]
    });

    function toggleOff() {
        Ext.getCmp('fToggle').toggle(false);
    }

    function toggleOn() {
        Ext.getCmp('fToggle').toggle(true);
    }

    //Create a show/hide menu button on the toolbar
    var view = aliquotIdBreakdownGrid.getView();
    aliquotIdBreakdownGrid.getTopToolbar().add('-',
        {iconCls: 'x-cols-icon', text: 'Show/Hide Columns', menu: view.colMenu}, '-',
        {text: 'Reset Table', iconCls: 'icon-reset', handler: function () {
            ahref('aliquotIdBreakdownReport.htm');
        }}, '->',
        {text: 'Help', iconCls: 'icon-help', handler: function () {
            showHelp('aliquotIdBreakdownReportDiv', 'aliquot_ID_breakdown_report');
        }});
    aliquotIdBreakdownGrid.getTopToolbar().doLayout();

    aliquotIdBreakdownStore.on('beforeload', function () {
        aliquotIdBreakdownStore.baseParams = {
            "formFilter": Ext.util.JSON.encode(formFilterValues)
        };
    });

    //breakdown aliquot renderer
    function breakdownAliquot(value, metaData, record, row) {
        var aliquotId = record.get('aliquotId');
        var project = record.get('project');
        var tissueSourceSite = record.get('tissueSourceSite');
        var participant = record.get('participant');
        var sampleType = record.get('sampleType');
        var vialId = record.get('vialId');
        var portionId = record.get('portionId');
        var portionAnalyte = record.get('portionAnalyte');
        var plateId = record.get('plateId');
        var centerId = record.get('centerId');
        return "<a id='aliquotId" + row + "' style='cursor:pointer;' " +
            "onClick=\"getAliquotGrid('Aliquot ID: " + aliquotId + "','" + project + "','" + tissueSourceSite + "','" + participant +
            "','" + sampleType + "','" + vialId + "','" + portionId + "','" + portionAnalyte + "','" + plateId + "','" + centerId + "')\">" +
            value + "</a>";
    }

    //breakdown analyte renderer
    function breakdownAnalyte(value, metaData, record) {
        var analyteId = record.get('analyteId');
        var project = record.get('project');
        var tissueSourceSite = record.get('tissueSourceSite');
        var participant = record.get('participant');
        var sampleType = record.get('sampleType');
        var vialId = record.get('vialId');
        var portionId = record.get('portionId');
        var portionAnalyte = record.get('portionAnalyte');
        return "<a style='cursor:pointer;' " +
            "onClick=\"getAnalyteGrid('Analyte ID: " + analyteId + "','" + project + "','" + tissueSourceSite + "','" + participant +
            "','" + sampleType + "','" + vialId + "','" + portionId + "','" + portionAnalyte + "')\">" +
            value + "</a>";
    }

    //breakdown sample renderer
    function breakdownSample(value, metaData, record) {
        var sampleId = record.get('sampleId');
        var project = record.get('project');
        var tissueSourceSite = record.get('tissueSourceSite');
        var participant = record.get('participant');
        var sampleType = record.get('sampleType');
        return "<a style='cursor:pointer;' " +
            "onClick=\"getSampleGrid('Sample ID: " + sampleId + "','" + project + "','" + tissueSourceSite + "','" + participant + "'," +
            "'" + sampleType + "')\">" +
            value + "</a>";
    }

    //breakdown participant renderer
    function breakdownParticipant(value, metaData, record) {
        var participantId = record.get('participantId');
        var project = record.get('project');
        var tissueSourceSite = record.get('tissueSourceSite');
        var participant = record.get('participant');
        return "<a style='cursor:pointer;' " +
            "onClick=\"getParticipantGrid('Participant ID: " + participantId + "','" + project + "','" + tissueSourceSite + "','" +
            participant + "')\">" + value + "</a>";
    }

}); //End of Ext.onReady

function getAliquotGrid(title, project, tss, participant, sampleType, vialId, portionId, portionAnalyte, plateId, centerId) {
    var breakdownGrid = new Ext.grid.PropertyGrid({
        renderTo: 'aliquotIdBreakdownReportDiv',
        source: {
            "Project": project,
            "Tissue Source Site": tss,
            "Participant": participant,
            "Sample Type": sampleType,
            "Vial ID": vialId,
            "Portion ID": portionId,
            "Portion Analyte": portionAnalyte,
            "Plate ID": plateId,
            "Center ID": centerId
        },
        autoHeight: true,
        stripeRows: true,
        width: 300
    });
    breakdownGrid.colModel.config[0].sortable = false;
    var win = new Ext.Window({
        title: "Breakdown for " + title,
        closable: true,
        width: 300,
        autoHeight: true,
        modal: true,
        layout: 'fit',
        items: [breakdownGrid],
        buttons: [
            {text: 'Close', handler: function () {
                win.hide();
            }}
        ]
    });
    win.show();
}

function getAnalyteGrid(title, project, tss, participant, sampleType, vialId, portionId, portionAnalyte) {
    var breakdownGrid = new Ext.grid.PropertyGrid({
        renderTo: 'aliquotIdBreakdownReportDiv',
        source: {
            "Project": project,
            "Tissue Source Site": tss,
            "Participant": participant,
            "Sample Type": sampleType,
            "Vial ID": vialId,
            "Portion ID": portionId,
            "Portion Analyte": portionAnalyte
        },
        autoHeight: true,
        stripeRows: true,
        width: 300
    });
    breakdownGrid.colModel.config[0].sortable = false;
    var win = new Ext.Window({
        title: "Breakdown for " + title,
        closable: true,
        width: 300,
        autoHeight: true,
        modal: true,
        layout: 'fit',
        items: [breakdownGrid],
        buttons: [
            {text: 'Close', handler: function () {
                win.hide();
            }}
        ]
    });
    win.show();
}

function getSampleGrid(title, project, tss, participant, sampleType) {
    var breakdownGrid = new Ext.grid.PropertyGrid({
        renderTo: 'aliquotIdBreakdownReportDiv',
        source: {
            "Project": project,
            "Tissue Source Site": tss,
            "Participant": participant,
            "Sample Type": sampleType
        },
        autoHeight: true,
        stripeRows: true,
        width: 300
    });
    breakdownGrid.colModel.config[0].sortable = false;
    var win = new Ext.Window({
        title: "Breakdown for " + title,
        closable: true,
        width: 300,
        autoHeight: true,
        modal: true,
        layout: 'fit',
        items: [breakdownGrid],
        buttons: [
            {text: 'Close', handler: function () {
                win.hide();
            }}
        ]
    });
    win.show();
}

function getParticipantGrid(title, project, tss, participant) {
    var breakdownGrid = new Ext.grid.PropertyGrid({
        renderTo: 'aliquotIdBreakdownReportDiv',
        source: {
            "Project": project,
            "Tissue Source Site": tss,
            "Participant": participant
        },
        autoHeight: true,
        stripeRows: true,
        width: 300
    });
    breakdownGrid.colModel.config[0].sortable = false;
    var win = new Ext.Window({
        title: "Breakdown for " + title,
        closable: true,
        width: 300,
        autoHeight: true,
        modal: true,

        layout: 'fit',
        items: [breakdownGrid],
        buttons: [
            {text: 'Close', handler: function () {
                win.hide();
            }}
        ]
    });
    win.show();
}

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
    filterMap.archiveType = Ext.get('archiveType').getAttribute('value');
    filterMap.dateFrom = Ext.get('dateFrom').getAttribute('value');
    filterMap.dateTo = Ext.get('dateTo').getAttribute('value');


//Create the data store
    var latestArchiveStore = new Ext.data.JsonStore({
        url: "latestArchiveReport.json",
        remoteSort: true,
        autoLoad: {params: {start: 0, limit: 50, "filterReq": Ext.util.JSON.encode(filterMap)}},
        root: 'latestArchiveData',
        totalProperty: 'totalCount',
        fields: [
            {name: 'archiveName'},
            {name: 'dateAdded'},
            {name: 'archiveUrl'},
            {name: 'archiveType'},
            {name: 'sdrfName'},
            {name: 'sdrfUrl'},
            {name: 'mafName'},
            {name: 'mafUrl'}
        ]
    });

//Create archiveType Json store.
    var archiveTypeStore = new Ext.data.JsonStore({
        url: "latestArchiveFilterData.json?filter=archiveType",
        root: 'archiveTypeData',
        autoLoad: 'true',
        listeners: {load: function () {
            Ext.getCmp('archiveTypeCombo').setValue(filterMap.archiveType);
        }},
        fields: ['id', 'text']
    });

//Create comboBox for page selection
    var combo = new Ext.form.ComboBox({
        name: 'perpage',
        width: 50,
        store: new Ext.data.ArrayStore({
            fields: ['id'],
            data: [
                ['50'],
                ['200'],
                ['500']
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
        store: latestArchiveStore,
        displayInfo: true,
        displayMsg: 'Displaying archive {0} - {1} of {2}',
        emptyMsg: "No archive to display",
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
            showFilterUrl('latestArchiveReportDiv', serverUrl +
                '/datareports/latestArchiveReport.htm?' + Ext.urlEncode(formFilterValues));
        }
    });

//Create resize Panel
    var resizeGrid = new Ext.ux.PanelResizer({
        minHeight: 500
    });

//Create the Grid
    var latestArchiveGrid = new Ext.grid.GridPanel({
        store: latestArchiveStore,
        columns: [
            {header: 'Archive', width: 190, sortable: true, dataIndex: 'archiveName', id: 'archive',
                renderer: makeArchiveUrl, hideable: false, tooltip: 'Archive Name'},
            {header: 'Date Added', width: 110, sortable: true, dataIndex: 'dateAdded', renderer: makeDate,
                tooltip: 'Date Added'},
            {header: 'Archive Type', width: 90, sortable: true, dataIndex: 'archiveType', tooltip: 'Archive Type', renderer: function (val, mD, rec, row) {
                return '<span id="archiveType' + row + '">' + val + '</span>';
            }},
            {header: 'Associated SDRF File', width: 190, sortable: true, dataIndex: 'sdrfName',
                renderer: makeSdrfUrl, tooltip: 'Associated SDRF File'},
            {header: 'Associated MAF File', width: 190, sortable: true, dataIndex: 'mafName', renderer: makeMafUrl,
                tooltip: 'Associated MAF File'}
        ],
        stripeRows: true,
        forceFit: true,
        autoExpandColumn: 'archive',
        height: 500,
        width: 940,
        frame: true,
        loadMask: true,
        tbar: exports,
        bbar: paging
    });

    //Create export url
    function exportUrl(type) {
        var dir, sort, cols;
        var sortState = latestArchiveGrid.store.getSortState();
        if (sortState) {
            dir = sortState.direction;
            sort = sortState.field;
        }
        cols = latestArchiveGrid.getColumnModel().getColumnsVisible(true, 'dataIndex');
        var form = Ext.DomHelper.append(document.body, {
            tag: 'form',
            method: 'POST',
            action: 'latestArchiveExport.htm',
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

    //url renderer: display N/A in red
    function redNArenderer(value, url) {
        if (value == 'N/A') {
            return '<span style="color:red;">' + value + '</span>';
        } else return "<a style='text-decoration:none;' href='" + url + "' target='_blank'>" + value + "</a>";
    }

    //archive renderer
    function makeArchiveUrl(value, metaData, record, rowIndex, colIndex) {
        return redNArenderer(value, record.get('archiveUrl'));
    }

    //sdrf renderer
    function makeSdrfUrl(value, metaData, record, rowIndex, colIndex) {
        return redNArenderer(value, record.get('sdrfUrl'));
    }

    //maf renderer
    function makeMafUrl(value, metaData, record, rowIndex, colIndex) {
        return redNArenderer(value, record.get('mafUrl'));
    }

    //date renderer
    function makeDate(value) {
        var dt = new Date();
        dt = Date.parseDate(value, "Y-m-d H:i:s.u");
        if (dt) {
            return dt.format("m/d/Y H:i");
        } else {
            return "";
        }
    }

    //Create comboBox for archiveType
    var archiveTypeCombo = new Ext.ux.form.LovCombo({
        id: 'archiveTypeCombo',
        fieldLabel: 'Archive Type',
        emptyText: 'Select an archive type...',
        name: 'archiveType',
        store: archiveTypeStore,
        mode: 'local',
        width: 200,
        triggerAction: 'all',
        triggerConfig: {tag: "img", id: "archiveTypeComboTrigger", src: Ext.BLANK_IMAGE_URL, cls: "x-form-trigger " + this.triggerClass},
        displayField: 'text',
        valueField: 'id',
        hiddenName: 'archiveType',
        editable: false,
        forceSelection: true
    });

    var filterBox = new Ext.form.FormPanel({
        frame: true,
        id: 'filterBox',
        margins: '3 3 0 0',
        layout: 'column',
        items: [
            {width: 400, layout: 'form', labelWidth: 90, items: [archiveTypeCombo]},
            {width: 250, layout: 'form', labelWidth: 65, items: [
                {id: 'dateFrom', fieldLabel: 'Date From', name: 'dateFrom',
                    xtype: 'datefield', emptyText: "Choose a Date...", value: filterMap.dateFrom, width: 150}
            ]},
            {width: 250, layout: 'form', labelWidth: 65, items: [
                {id: 'dateTo', fieldLabel: 'Date To', name: 'dateTo',
                    xtype: 'datefield', emptyText: "Choose a Date...", value: filterMap.dateTo, width: 150}
            ]}
        ],
        buttons: [
            {xtype: 'buttonplus', id: 'filterNow', text: 'Filter Now', handler: function () {
                formFilterValues = filterBox.getForm().getFieldValues();
                latestArchiveStore.load({params: {start: 0, limit: paging.pageSize}});
            }},
            {xtype: 'buttonplus', id: 'clearFilters', text: 'Clear Filters', handler: function () {
                filterBox.getForm().clear();
                formFilterValues = filterBox.getForm().getFieldValues();
                latestArchiveStore.load({params: {start: 0, limit: paging.pageSize}});
            }}
        ]
    });

    //Create resize Panel
    var resizePanel = new Ext.ux.PanelResizer({
        minHeight: 550
    });

    var panel = new Ext.Panel({
        title: 'Latest Archive Report',
        renderTo: 'latestArchiveReportDiv',
        width: 950,
        height: 550,
        iconCls: 'icon-report',
        layout: 'border',
        bodyStyle: 'z-index: 0;',
        frame: true,
        plugins: [resizePanel],
        items: [
            {
                title: 'Filters Extended',
                region: 'north',
                height: 100,
                minSize: 100,
                maxSize: 100,
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
            {region: 'center', layout: 'fit', id: 'centre', items: [latestArchiveGrid]}
        ]
    });

    function toggleOff() {
        Ext.getCmp('fToggle').toggle(false);
    }

    function toggleOn() {
        Ext.getCmp('fToggle').toggle(true);
    }

    //Create a show/hide menu button on the toolbar
    var view = latestArchiveGrid.getView();
    latestArchiveGrid.getTopToolbar().add('-',
        {iconCls: 'x-cols-icon', text: 'Show/Hide Columns', menu: view.colMenu}, '-',
        {text: 'Reset Table', iconCls: 'icon-reset', handler: function () {
            ahref('latestArchiveReport.htm');
        }}, '->',
        {text: 'Help', iconCls: 'icon-help', handler: function () {
            showHelp('latestArchiveReportDiv', 'latest_archive_report');
        }});
    latestArchiveGrid.getTopToolbar().doLayout();

    latestArchiveStore.on('beforeload', function () {
        latestArchiveStore.baseParams = {
            "formFilter": Ext.util.JSON.encode(formFilterValues)
        };
    });

}); //End of Ext.onReady

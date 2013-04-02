/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

Ext.onReady(function () {
    Ext.QuickTips.init();
    Ext.apply(Ext.QuickTips.getQuickTip(), {
        showDelay: 50,
        maxWidth: 150,
        trackMouse: true
    });

    var pcodWidth = 1465;

    var formFilterValues;
    var serverUrl = Ext.get('serverUrl').getAttribute('value');
    var filterMap = {}; //order of member is the same as the form layout
    filterMap.disease = Ext.get('disease').getAttribute('value');

    //Create All Disease,Center,Platform Json store.
    var diseaseStore = new Ext.data.JsonStore({
        url: "projectCaseDashboardFilterData.json?filter=disease",
        root: 'diseaseData',
        autoLoad: 'true',
        listeners: {load: function () {
            Ext.getCmp('diseaseCombo').setValue(filterMap.disease)
        }},
        fields: ['id', 'text']
    });

    //Create the data store
    var projectCaseStore = new Ext.data.JsonStore({
        url: "projectCaseDashboard.json",
        remoteSort: true,
        autoLoad: {params: {start: 0, limit: 100, "filterReq": Ext.util.JSON.encode(filterMap)}},
        root: 'projectCaseDashboardData',
        totalProperty: 'totalCount',
        fields: [
            {name: 'disease'},
            {name: 'diseaseName'},
            {name: 'overallProgress'},
            {name: 'methylationCGCC'},
            {name: 'microRNACGCC'},
            {name: 'expressionArrayCGCC'},
            {name: 'expressionRNASeqCGCC'},
            {name: 'copyNumberSNPCGCC'},
            {name: 'genomeGSC'},
            {name: 'exomeGSC'},
            {name: 'mutationGSC'},
            {name: 'expressionRNASeqGSC'},
            {name: 'microRNAGSC'},
            {name: 'projectedCaseBCR'},
            {name: 'currentCaseGapBCR'},
            {name: 'receivedBCR'},
            {name: 'shippedBCR'},
            {name: 'completeCases'},
            {name: 'incompleteCases'},
            {name: 'lowPassGCC'},
            {name: 'lowPassGSC'}
        ]
    });

    //Create comboBox for page selection
    var combo = new Ext.form.ComboBox({
        name: 'perpage',
        width: 50,
        store: new Ext.data.ArrayStore({
            fields: ['id'],
            data: [
                ['100'],
                ['150'],
                ['200']
            ]}),
        mode: 'local',
        value: '100',
        listWidth: 40,
        triggerAction: 'all',
        displayField: 'id',
        valueField: 'id',
        editable: false,
        forceSelection: true
    });

    //Create paging Toolbar
    var paging = new Ext.PagingToolbar({
        pageSize: 100,
        store: projectCaseStore,
        displayInfo: true,
        displayMsg: 'Displaying data {0} - {1} of {2}',
        emptyMsg: "No data to display",
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
            showFilterUrl('projectCaseDashboardDiv', serverUrl +
                '/datareports/projectCaseDashboard.htm?' + Ext.urlEncode(formFilterValues));
        }
    });

    var group = new Ext.ux.grid.ColumnHeaderGroup({
        rows: [
            [
                {header: '', colspan: 3},
                {header: 'BCRs (Tissue) Production - Tumor Cases', colspan: 3, align: "center"},
                {header: 'GSC Sequence Production', colspan: 6, align: "center", tooltip: "Relative to overall project goals"},
                {header: 'GCC Characterization production', colspan: 6, align: "center", tooltip: "Relative to overall project goals"},
                {header: 'Case Summary', colspan: 2, align: "center", tooltip: "Relative to overall project goals"}
            ]
        ]
    });

    var overallProgressPBar = new Ext.ux.grid.plugin.ProgressColumn({
        header: '% Complete', dataIndex: 'overallProgress',
        tooltip: 'Fraction of planned data acquired and available at DCC, averaged over all data types expressed as a number of cases for this tumor study',
        width: 75
    });
    var bcrReceivedPBar = new Ext.ux.grid.plugin.ProgressColumn({
        header: 'BCR Received', dataIndex: 'receivedBCR',
        tooltip: 'Total number of cases received from Tissue Source Sites by the BCRs for this tumor study',
        width: 85
    });
    var bcrShippedPBar = new Ext.ux.grid.plugin.ProgressColumn({
        header: 'BCR Shipped', dataIndex: 'shippedBCR',
        tooltip: 'Total number of cases passing QC and either shipped or pending shipment to data generating centers for this tumor study',
        width: 85
    });
    var genomePBar = new Ext.ux.grid.plugin.ProgressColumn({
        header: 'Genome', dataIndex: 'genomeGSC',
        tooltip: 'Number of cases for which whole genomes have been sequenced for this tumor study *',
        width: 65
    });
    var exomePBar = new Ext.ux.grid.plugin.ProgressColumn({
        header: 'Exome', dataIndex: 'exomeGSC',
        tooltip: 'Number of cases for which whole exomes have been sequenced for this tumor study',
        width: 65
    });
    var mutationPBar = new Ext.ux.grid.plugin.ProgressColumn({
        header: 'Mutation', dataIndex: 'mutationGSC',
        tooltip: 'Number of cases for which mutation calls (as MAF files) are available at the DCC for this tumor study',
        width: 65
    });
    var rnaSeqGSCPBar = new Ext.ux.grid.plugin.ProgressColumn({
        header: 'RNASeq', dataIndex: 'expressionRNASeqGSC',
        tooltip: 'Number of cases for which transcriptomes have been been sequenced for this tumor study, with sequence submitted to an approved TCGA repository',
        width: 65
    });
    var microRNAGSCPBar = new Ext.ux.grid.plugin.ProgressColumn({
        header: 'miRNASeq', dataIndex: 'microRNAGSC',
        tooltip: 'Number of cases for which the microRNA transciptome has been sequenced for this tumor study, with sequence submitted to an approved TCGA repository',
        width: 70
    });
    var methylationPBar = new Ext.ux.grid.plugin.ProgressColumn({
        header: 'Methylation', dataIndex: 'methylationCGCC',
        tooltip: 'Number of cases for which Level 3 methylation data are available at the DCC for this tumor study',
        width: 75
    });
    var microRNACGCCPBar = new Ext.ux.grid.plugin.ProgressColumn({
        header: 'microRNA', dataIndex: 'microRNACGCC',
        tooltip: 'Number of cases for which Level 3 miRNA array and sequence data are available at the DCC for this tumor study',
        width: 70
    });
    var expArrayPBar = new Ext.ux.grid.plugin.ProgressColumn({
        header: 'Exp Array', dataIndex: 'expressionArrayCGCC',
        tooltip: 'Number of cases for which Level 3 array-based gene expression data are available at the DCC for this tumor study',
        width: 65
    });
    var rnaSeqCGCCPBar = new Ext.ux.grid.plugin.ProgressColumn({
        header: 'RNASeq', dataIndex: 'expressionRNASeqCGCC',
        tooltip: 'Number of cases for which Level 3 RNASeq-based gene expression data are available at the DCC for this tumor study',
        width: 65
    });
    var cnSnpPBar = new Ext.ux.grid.plugin.ProgressColumn({
        header: 'SNP/CN', dataIndex: 'copyNumberSNPCGCC',
        tooltip: 'Number of cases for which Level 3 SNP-based copy number variation data are available at the DCC for this tumor study',
        width: 60
    });
    var lowPassGSCPBar = new Ext.ux.grid.plugin.ProgressColumn({
        header: 'Low Pass', dataIndex: 'lowPassGSC',
        tooltip: 'Number of cases for which GSC low pass sequencing data are available at the DCC for this tumor study',
        width: 60
    });
    var lowPassGCCPBar = new Ext.ux.grid.plugin.ProgressColumn({
        header: 'Low Pass', dataIndex: 'lowPassGCC',
        tooltip: 'Number of cases for which GCC low pass sequencing data are available at the DCC for this tumor study',
        width: 60
    });

    //Create the Grid
    var projectCaseGrid = new Ext.grid.GridPanel({
        store: projectCaseStore,
        colModel: new Ext.grid.ColumnModel({
            defaults: {sortable: true},
            columns: [
                {header: 'Disease', dataIndex: 'disease', tooltip: 'TCGA tumor study abbreviation', width: 55, renderer: getDiseaseNameTooltip},
                overallProgressPBar,
                {header: 'Cases Required', dataIndex: 'projectedCaseBCR',
                    tooltip: 'Estimated number of case acquisitions required to meet TCGA target number of qualified cases for this tumor study',
                    width: 90},
                {header: 'Case Gap', dataIndex: 'currentCaseGapBCR',
                    tooltip: 'Estimated number of further case acquisitions necessary to meet TCGA target number of qualified cases for this tumor study',
                    width: 65, renderer: bcrCss},
                bcrReceivedPBar,
                bcrShippedPBar,
                genomePBar,
                exomePBar,
                mutationPBar,
                rnaSeqGSCPBar,
                microRNAGSCPBar,
                lowPassGSCPBar,
                methylationPBar,
                microRNACGCCPBar,
                expArrayPBar,
                rnaSeqCGCCPBar,
                cnSnpPBar,
                lowPassGCCPBar,
                {header: 'Complete Cases', dataIndex: 'completeCases',
                    tooltip: 'Complete Cases',
                    width: 85},
                {header: 'Incomplete Cases', dataIndex: 'incompleteCases',
                    tooltip: 'Incomplete Cases',
                    width: 85}
            ]}),
        stripeRows: true,
        forceFit: true,
        height: 715,
        width: pcodWidth,
        frame: true,
        loadMask: true,
        plugins: [group, overallProgressPBar, bcrReceivedPBar, bcrShippedPBar, genomePBar, exomePBar,
            mutationPBar, rnaSeqGSCPBar, microRNAGSCPBar, methylationPBar, microRNACGCCPBar, expArrayPBar, rnaSeqCGCCPBar,
            cnSnpPBar],
        tbar: exports
    });

    function getDiseaseNameTooltip(value, metaData, record) {
        metaData.attr = 'ext:qtip="' + record.get('diseaseName') + '"';
        return value;
    }

    function bcrCss(value, metaData, record) {
        if ('LAML' == record.get('disease')) {
            metaData.css = "bcr-disabled";
        }
        return value;
    }

    //Create export url
    function exportUrl(type) {
        var dir, sort, cols;
        var sortState = projectCaseGrid.store.getSortState();
        if (sortState) {
            dir = sortState.direction;
            sort = sortState.field;
        }
        cols = projectCaseGrid.getColumnModel().getColumnsVisible(true, 'dataIndex');
        var form = Ext.DomHelper.append(document.body, {
            tag: 'form',
            method: 'POST',
            action: 'projectCaseDashboardExport.htm',
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

    //Create resize Panel
    var resizePanel = new Ext.ux.PanelResizer({
        minHeight: 715
    });

    //Create comboBox for disease
    var diseaseCombo = new Ext.ux.form.LovCombo({
        id: 'diseaseCombo',
        fieldLabel: 'Disease',
        emptyText: 'Select diseases...',
        name: 'disease',
        store: diseaseStore,
        mode: 'local',
        width: 200,
        triggerAction: 'all',
        displayField: 'text',
        valueField: 'id',
        hiddenName: 'disease',
        editable: false,
        forceSelection: true
    });

    var filterBox = new Ext.form.FormPanel({
        frame: true,
        id: 'filterBox',
        labelWidth: 65,
        margins: '3 3 0 0',
        layout: 'column',
        items: [
            {width: 350, layout: 'form', items: [diseaseCombo]}
        ],
        buttons: [
            {text: 'Filter Now', handler: function () {
                formFilterValues = filterBox.getForm().getFieldValues();
                projectCaseStore.load({params: {start: 0, limit: paging.pageSize}});
            }},
            {text: 'Clear Filters', handler: function () {
                filterBox.getForm().clear();
                formFilterValues = filterBox.getForm().getFieldValues();
                projectCaseStore.load({params: {start: 0, limit: paging.pageSize}});
            }}
        ]
    });

    var panel = new Ext.Panel({
        title: 'Project Case Overview Dashboard',
        renderTo: 'projectCaseDashboardDiv',
        width: pcodWidth,
        height: 715,
        iconCls: 'icon-report',
        layout: 'border',
        bodyStyle: 'z-index: 0',
        frame: true,
        plugins: [resizePanel],
        items: [
            {
                title: 'Filters Extended',
                region: 'north',
                height: 95,
                minSize: 95,
                maxSize: 95,
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
            {region: 'center', layout: 'fit', id: 'centre', items: [projectCaseGrid]}
        ]
    });

    function toggleOff() {
        Ext.getCmp('fToggle').toggle(false);
    }

    function toggleOn() {
        Ext.getCmp('fToggle').toggle(true);
    }

    var togglePercent = {
        text: 'Show Percentage',
        enableToggle: true,
        id: 'percentToggle',
        iconCls: 'icon-calc',
        toggleHandler: function (btn, pressed) {
            overallProgressPBar.showPercent = pressed;
            bcrReceivedPBar.showPercent = pressed;
            bcrShippedPBar.showPercent = pressed;
            genomePBar.showPercent = pressed;
            exomePBar.showPercent = pressed;
            mutationPBar.showPercent = pressed;
            rnaSeqGSCPBar.showPercent = pressed;
            microRNAGSCPBar.showPercent = pressed;
            methylationPBar.showPercent = pressed;
            microRNACGCCPBar.showPercent = pressed;
            expArrayPBar.showPercent = pressed;
            rnaSeqCGCCPBar.showPercent = pressed;
            cnSnpPBar.showPercent = pressed;
            lowPassGSCPBar.showPercent = pressed;
            lowPassGCCPBar.showPercent = pressed;
            if (pressed) {
                btn.setText('Show Ratio');
            }
            else {
                btn.setText('Show Percentage');
            }
            projectCaseGrid.getView().refresh();
        }
    };

    //Create a show/hide menu button on the toolbar
    var view = projectCaseGrid.getView();
    projectCaseGrid.getTopToolbar().add('-', togglePercent, '-',
        {iconCls: 'x-cols-icon', text: 'Show/Hide Columns', menu: view.colMenu}, '-',
        {text: 'Reset Table', iconCls: 'icon-reset', handler: function () {
            ahref('projectCaseDashboard.htm');
        }}
        , '->',
        {text: 'Help', iconCls: 'icon-help', handler: function () {
            showHelp('projectCaseDashboardDiv', 'projectCaseDashboard');
        }
        }
    );
    projectCaseGrid.getTopToolbar().doLayout();

    projectCaseStore.on('beforeload', function () {
        projectCaseStore.baseParams = {
            "formFilter": Ext.util.JSON.encode(formFilterValues)
        };
    });

});//End of Ext.onReady

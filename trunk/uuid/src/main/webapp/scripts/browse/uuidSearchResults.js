Ext.namespace('tcga.uuid.search');

tcga.uuid.search.results = function(browseStore) {
    var searchResultsPanel;

    return {
        render: function() {
            searchResultsPanel.render('uuidSearchResults');
        },

        hide: function() {
            Ext.get('uuidSearchResults').setDisplayed('none');
        },

        show: function() {
            Ext.get('uuidSearchResults').show();
        },

        goTo: function(rec) {
            Ext.History.add('Search', true);
        },

        init: function(browseStore) {
            var combo = new Ext.form.ComboBox({
                name: 'perpage',
                width: 50,
                store: new Ext.data.ArrayStore({
                    fields: ['id'],
                    data: [
                        ['50'],
                        ['100'],
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

            //Create exports Toolbar
            var exports = new Ext.Toolbar({
                items: [
                    {
                        menu: [
                            {text: 'Excel',iconCls: 'icon-xl',id: 'exportExcelButton',handler: function() {
                                exportUrl("xl");
                            }},
                            {text: 'CSV',iconCls: 'icon-txt',id: 'exportCSVButton',handler: function() {
                                exportUrl("csv");
                            }},
                            {text: 'Tab-delimited',iconCls: 'icon-txt',id: 'exportTabDelButton',handler: function() {
                                exportUrl("tab");
                            }}
                        ],
                        text: 'Export Data',
                        iconCls: 'icon-grid',
                        id: 'exportButton'
                    }
                ]
            });

            //Create export url
            function exportUrl(type) {
                var dir,sort,cols,totalCount;
                var sortState = uuidResultsGrid.store.getSortState();
                if (sortState) {
                    dir = sortState.direction;
                    sort = sortState.field;
                }
                totalCount = uuidResultsGrid.store.getTotalCount();
                cols = uuidResultsGrid.getColumnModel().getColumnsVisible(true, 'dataIndex');
                var form = Ext.DomHelper.append(document.body, {
                    tag : 'form',
                    method : 'POST',
                    action : 'uuidBrowserExport.htm',
                    children: [
                        {tag: 'input', type:'hidden', name: 'exportType', value: type},
                        {tag: 'input', type:'hidden', name: 'dir', value: dir},
                        {tag: 'input', type:'hidden', name: 'sort', value: sort},
                        {tag: 'input', type:'hidden', name: 'totalCount', value: totalCount},
                        {tag: 'input', type:'hidden', name: 'cols', value: "" + cols},
                        {tag: 'input', type:'hidden', name: 'searchParams', value: Ext.util.Format.htmlEncode(searchValues)}
                    ]
                });
                document.body.appendChild(form);
                form.submit();
                document.body.removeChild(form);
            }

            // paging bar
            var paging = new Ext.PagingToolbar({
                id: 'resultsPagingTb',
                pageSize: 50,
                store: browseStore,
                displayInfo: true,
                displayMsg: 'Displaying uuid {0} - {1} of {2}',
                emptyMsg: "No uuids to display",
                items: ['-','Per Page ',combo]
            });

            combo.on('select', function(combo, record) {
                paging.pageSize = parseInt(record.get('id'), 10);
                paging.doLoad(paging.cursor);
            }, this);

            var uuidResultsGrid = new Ext.grid.GridPanel({
                id: 'uuidResultsGrid',
                renderTo: 'uuidSearchResults',
                store: browseStore,
                loadMask: true,
                viewConfig: { forceFit: false },
                stripeRows: true,
                height: 344,
                width: 940,
                title: 'UUID Search Results',
                enableColumnHide: true,
                columns: [
                    {
                        header: 'UUID',
                        width: 222,
                        sortable: true,
                        dataIndex: 'uuid',
                        hideable:false,
                        renderer: function(val, mD, rec, row) {
                            var elementType = rec.get('uuidType');
                            if (elementType == 'Participant' ||
                                elementType == 'Sample' ||
                                elementType == 'Portion' ||
                                elementType == 'Analyte' ||
                                elementType == 'Slide' ||
                                elementType == 'Aliquot') {
                                return '<a class="hand" ' +
                                    'onclick="tcga.uuid.browser.goTo(Ext.getCmp(\'uuidResultsGrid\').getStore().getAt(' + row + '))">' + val + '</a>';
                            }
                            else {
                                return val;
                            }
                        }
                    },
                    {
                        id: 'barcode',
                        header: 'Barcode',
                        width: 187,
                        sortable: true,
                        dataIndex: 'barcode'
                    },
                    {
                        header: 'Element Type',
                        width: 77,
                        sortable: true,
                        dataIndex: 'uuidType'
                    },
                    {
                        header: 'Disease',
                        width: 57,
                        sortable: true,
                        dataIndex: 'disease'
                    },
                    {
                        id: 'tissueSourceSite',
                        header: '<NOBR>Tissue Source Site</NOBR>',
                        width: 120,
                        sortable: true,
                        dataIndex: 'tissueSourceSite'
                    },
                    {
                        id: 'participantNumber',
                        header: '<NOBR>Participant Number</NOBR>',
                        width: 120,
                        sortable: true,
                        dataIndex: 'participantId'
                    },
                    {
                        header: 'Sample Type',
                        width: 120,
                        sortable: true,
                        dataIndex: 'sampleType'
                    },
                    {
                        header: 'Batch',
                        hidden: true,
                        width: 37,
                        sortable: true,
                        dataIndex: 'batch'
                    },
                    {
                        header: 'Analyte Type',
                        width: 120,
                        hidden: true,
                        sortable: true,
                        dataIndex: 'analyteType'
                    },
                    {
                        header: 'BCR Source',
                        width: 120,
                        hidden: true,
                        sortable: true,
                        dataIndex: 'bcr'
                    },
                    {
                        header: 'Receiving Center',
                        width: 120,
                        hidden: true,
                        sortable: true,
                        dataIndex: 'receivingCenter'
                    },
                    {
                        header: 'Platform',
                        width: 120,
                        hidden: true,
                        sortable: true,
                        dataIndex: 'platform'
                    },
                    {
                        header: 'Vial',
                        hidden: true,
                        width: 32,
                        sortable: true,
                        dataIndex: 'vialId'
                    },
                    {
                        header: 'Portion',
                        hidden: true,
                        width: 45,
                        sortable: true,
                        dataIndex: 'portionId'
                    },
                    {
                        header: 'Plate',
                        hidden: true,
                        width: 32,
                        sortable: true,
                        dataIndex: 'plateId'
                    },
                    {
                        header: 'Slide',
                        hidden: true,
                        width: 32,
                        sortable: true,
                        dataIndex: 'slide'
                    },
                    {
                        header: 'Update Date',
                        hidden: true,
                        width: 77,
                        sortable: true,
                        renderer: makeDate,
                        dataIndex: 'updateDate'
                    },
                    {
                        header: 'Creation Date',
                        hidden: true,
                        width: 77,
                        sortable: true,
                        renderer: makeDate,
                        dataIndex: 'createDate'
                    }
                ],
                tbar: exports,
                bbar: paging
            });

            function makeDate(value) {
                var dt = new Date();
                dt = Date.parseDate(value, "Y-m-d");
                if (dt) {
                    return dt.format("m/d/Y");
                } else {
                    return "";
                }
            }

            var view = uuidResultsGrid.getView();
            view.colMenu = new Ext.menu.Menu({
                listeners: {
                    beforeshow: view.beforeColMenuShow,
                    itemclick: view.handleHdMenuClick,
                    scope: view
                }
            });
            uuidResultsGrid.getTopToolbar().add('-', {
                    iconCls: 'x-cols-icon',
                    text: 'Show/Hide Columns',
                    menu: view.colMenu,
                    id: 'showHideButton'
                }, '-',
                {
                    text: 'Reset Table',
                    iconCls: 'icon-reset',
                    id: 'resetResultsButton',
                    handler: function() {
                        browseStore.removeAll();
                        if (tcga.uuid.search.uuidSearchForm) {
                            tcga.uuid.search.uuidSearchForm.getForm().reset();
                            tcga.uuid.search.metadataForm.getForm().reset();
                            tcga.uuid.search.resetCombo();
                            tcga.uuid.search.resetChoice();
                        }
                    }
                }, '->', {text: 'Help',iconCls: 'icon-help',handler: function() {
                    window.open('https://wiki.nci.nih.gov/x/lxVyAg');
                }
                });

            browseStore.on('beforeload', function() {
                browseStore.baseParams = {"searchParams":searchValues};
            });

            searchResultsPanel = new Ext.Panel({
                id: 'uuidResultsPanel',
                renderTo: 'uuidSearchResults',
                border: false,
                forceFit: true,
                items: [uuidResultsGrid]
            });
        }
    }
}();

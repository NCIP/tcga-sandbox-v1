Ext.namespace('tcga.uuid.search');

tcga.uuid.search.metadata = function(browseStore, limit) {
    var formElementSep = 'margin-bottom: 5px;';

    function comboSelectAllToggle(combo, selectedRec, displayParam, forceUnselect) {
        if (selectedRec.get(displayParam) == "<b>Select All</b>" && !forceUnselect) {
            selectedRec.set(displayParam, '<b>Unselect All</b>');
            combo.selectAll();
        } else if (selectedRec.get(displayParam) == "<b>Unselect All</b>") {
            selectedRec.set(displayParam, '<b>Select All</b>');
            combo.deselectAll();
        }
    }

    ;

    tcga.uuid.search.resetCombo = function() {
        comboSelectAllToggle(uuidTypeCombo, uuidTypeCombo.getStore().getAt(0), 'text', true);
        comboSelectAllToggle(platformCombo, platformCombo.getStore().getAt(0), 'text', true);
        comboSelectAllToggle(tssCombo, tssCombo.getStore().getAt(0), 'text', true);
        comboSelectAllToggle(diseaseCombo, diseaseCombo.getStore().getAt(0), 'text', true);
        comboSelectAllToggle(sampleCombo, sampleCombo.getStore().getAt(0), 'text', true);
        comboSelectAllToggle(analyteCombo, analyteCombo.getStore().getAt(0), 'text', true);
        comboSelectAllToggle(bcrCombo, bcrCombo.getStore().getAt(0), 'text', true);
        comboSelectAllToggle(centerCombo, centerCombo.getStore().getAt(0), 'text', true);
    }

    tcga.uuid.search.resetChoice = function() {
        toggleTSSField('Name');
        Ext.getCmp('diseaseCombo').getStore().proxy.setUrl('uuidBrowserFilterData.json?filter=disease&column=Abbreviation');
        Ext.getCmp('sampleCombo').getStore().proxy.setUrl('uuidBrowserFilterData.json?filter=sampleType&column=Type');
        Ext.getCmp('analyteCombo').getStore().proxy.setUrl('uuidBrowserFilterData.json?filter=analyteType&column=Type');
        Ext.getCmp('bcrCombo').getStore().proxy.setUrl('uuidBrowserFilterData.json?filter=bcr&column=Name');
        Ext.getCmp('centerCombo').getStore().proxy.setUrl('uuidBrowserFilterData.json?filter=receivingCenter&column=Name');
        Ext.getCmp('diseaseCombo').getStore().load();
        Ext.getCmp('sampleCombo').getStore().load();
        Ext.getCmp('analyteCombo').getStore().load();
        Ext.getCmp('bcrCombo').getStore().load();
        Ext.getCmp('centerCombo').getStore().load();
    }

    function setTSS(field, value, checked) {
        if (checked) {
            if (field.getValue().length > 0) {
                var tmp = field.getValue();
                field.setValue(tmp + ',' + value);
            } else {
                field.setValue(value);
            }
        } else {
            var str = field.getValue().replace(',' + value, '');
            str = str.replace(value, '');
            var trim = str.replace(/(^\s*,)|(,\s*$)/g, '');
            field.setValue(trim);
        }
    }

    function toggleTSSField(column) {
        if (column == 'TSS ID') {
            Ext.getCmp('tssCombo').hide();
            Ext.getCmp('tssTB').show();
        } else {
            Ext.getCmp('tssCombo').show();
            Ext.getCmp('tssTB').hide();
        }
    }

    var allRecordTaskLocation = Ext.data.Record.create([
        {name: "id", type: "string", name: "text", type: "string"}
    ]);

    function comboStore(filter, column, sort) {
        return new Ext.data.JsonStore({
            url: 'uuidBrowserFilterData.json?filter=' + filter + '&column=' + column,
            root: filter + 'Data',
            autoLoad: true,
            sortInfo: sort ? {field: 'text',direction: 'ASC'} : null,
            listeners: {load : function() {
                this.insert(0, new allRecordTaskLocation({id: null,text: "<b>Select All</b>"}));
            }},
            fields: ['id','text']
        });
    }

    function comboChoice(choiceId, comboId, filter, store, label, defaultValue, tss, listWidth) {
        return new Ext.form.ComboBox({
            id: choiceId,
            store: store,
            mode: 'local',
            fieldLabel: label,
            triggerAction: 'all',
            displayField:'text',
            valueField : 'text',
            value: defaultValue,
            listeners: {
                select:function(combo, rec) {
                    if (tss) {
                        toggleTSSField(rec.get('text'));
                    } else {
                        Ext.getCmp(comboId).getStore().proxy.setUrl('uuidBrowserFilterData.json?filter='
                            + filter + '&column=' + rec.get('text'));
                        Ext.getCmp(comboId).getStore().load();
                    }
                }
            },
            border: false,
            autoHeight: true,
            style: formElementSep,
            width: 80,
            listWidth: listWidth,
            editable: false,
            forceSelection: true
        });
    }

    var uuidTypeCombo = new Ext.ux.form.LovCombo({
        id: 'uuidTypeCombo',
        store: comboStore('uuidType', null, false),
        listeners: {
            select:function(combo, rec, ndx) {
                (ndx == 0 ? comboSelectAllToggle(combo, rec, 'text', false) : null);
            }
        },
        mode: 'local',
        fieldLabel: 'Element',
        triggerAction: 'all',
        displayField:'text',
        valueField : 'id',
        emptyText:'Select types...',
        border: false,
        autoHeight: true,
        style: formElementSep,
        width: 100,
        listWidth: 120,
        editable: false,
        forceSelection: true
    });

    var platformCombo = new Ext.ux.form.LovCombo({
        id: 'platformCombo',
        store: comboStore('platform', null, true),
        listeners: {
            select:function(combo, rec, ndx) {
                (ndx == 0 ? comboSelectAllToggle(combo, rec, 'text', false) : null);
            }
        },
        mode: 'local',
        fieldLabel: 'Platform',
        triggerAction: 'all',
        displayField: 'text',
        valueField: 'id',
        emptyText:'Select platforms...',
        border: false,
        autoHeight: true,
        style: formElementSep,
        width: 100,
        listWidth: 180,
        editable: false,
        forceSelection: true
    });

    var participantTB = {
        id: 'participantTB',
        xtype: 'textfield',
        fieldLabel: 'Participant',
        name: 'participant',
        style: formElementSep,
        width: 100
    };

    var batchTB = {
        id: 'batchTB',
        xtype: 'textfield',
        fieldLabel: 'Batch',
        name: 'batch',
        style: formElementSep,
        width: 100
    };

    var diseaseCombo = new Ext.ux.form.LovCombo({
        id: 'diseaseCombo',
        store: comboStore('disease', 'Abbreviation', true),
        listeners: {
            select:function(combo, rec, ndx) {
                (ndx == 0 ? comboSelectAllToggle(combo, rec, 'text', false) : null);
            }
        },
        mode: 'local',
        hideLabel: true,
        triggerAction: 'all',
        displayField:'text',
        valueField : 'id',
        emptyText:'Select diseases...',
        border: false,
        autoHeight: true,
        style: formElementSep,
        width: 150,
        listWidth: 280,
        editable: false,
        forceSelection: true
    });

    var sampleCombo = new Ext.ux.form.LovCombo({
        id: 'sampleCombo',
        store: comboStore('sampleType', 'Type', true),
        listeners: {
            select:function(combo, rec, ndx) {
                (ndx == 0 ? comboSelectAllToggle(combo, rec, 'text', false) : null);
            }
        },
        mode: 'local',
        hideLabel: true,
        triggerAction: 'all',
        displayField:'text',
        valueField : 'id',
        emptyText:'Select types...',
        border: false,
        autoHeight: true,
        style: formElementSep,
        width: 150,
        listWidth: 270,
        editable: false,
        forceSelection: true
    });

    var vialTB = {
        id: 'vialTB',
        xtype: 'textfield',
        fieldLabel: 'Vial',
        name: 'vial',
        style: formElementSep,
        width: 80
    };

    var portionTB = {
        id: 'portionTB',
        xtype: 'textfield',
        fieldLabel: 'Portion',
        name: 'portion',
        style: formElementSep,
        width: 80
    };

    var analyteCombo = new Ext.ux.form.LovCombo({
        id: 'analyteCombo',
        store: comboStore('analyteType', 'Type', true),
        listeners: {
            select:function(combo, rec, ndx) {
                (ndx == 0 ? comboSelectAllToggle(combo, rec, 'text', false) : null);
            }
        },
        mode: 'local',
        hideLabel: true,
        triggerAction: 'all',
        displayField:'text',
        valueField : 'id',
        emptyText:'Select types...',
        border: false,
        autoHeight: true,
        style: formElementSep,
        width: 150,
        listWidth: 525,
        editable: false,
        forceSelection: true
    });

    var plateTB = {
        id: 'plateTB',
        xtype: 'textfield',
        fieldLabel: 'Plate',
        name: 'plate',
        style: formElementSep,
        width: 80
    };

    var bcrCombo = new Ext.ux.form.LovCombo({
        id: 'bcrCombo',
        store: comboStore('bcr', 'Name', true),
        listeners: {
            select:function(combo, rec, ndx) {
                (ndx == 0 ? comboSelectAllToggle(combo, rec, 'text', false) : null);
            }
        },
        mode: 'local',
        hideLabel: true,
        triggerAction: 'all',
        displayField:'text',
        valueField : 'id',
        emptyText:'Select centers...',
        border: false,
        autoHeight: true,
        style: formElementSep,
        width: 150,
        listWidth: 270,
        editable: false,
        forceSelection: true
    });

    var tssCombo = new Ext.ux.form.LovCombo({
        id: 'tssCombo',
        store: comboStore('tissueSourceSite', 'Name', true),
        listeners: {
            select:function(combo, rec, ndx) {
                if (ndx == 0) {
                    comboSelectAllToggle(combo, rec, 'text', false);
                    Ext.getCmp('tssTB').setValue(combo.getValue());
                } else {
                    setTSS(Ext.getCmp('tssTB'), rec.get('id'), rec.get(combo.checkField));
                }
            }
        },
        mode: 'local',
        hideLabel: true,
        triggerAction: 'all',
        displayField:'text',
        valueField : 'id',
        emptyText:'Select sites...',
        border: false,
        autoHeight: true,
        style: formElementSep,
        width: 150,
        listWidth: 340,
        editable: false,
        forceSelection: true
    });

    var tssTB = {
        id: 'tssTB',
        xtype: 'textfield',
        name: 'tissueSourceSite',
        hideLabel: true,
        hidden: true,
        style: formElementSep,
        width: 150
    };

    var centerTypeCombo = new Ext.ux.form.LovCombo({
        id: 'centerTypeCombo',
        store: new Ext.data.ArrayStore({fields: ['id', 'text'], data : [
            ['CGCC', 'GCC'],
            ['GSC', 'GSC']
        ]}),
        mode: 'local',
        hideLabel: true,
        triggerAction: 'all',
        displayField:'text',
        valueField : 'id',
        emptyText:'Select types...',
        border: false,
        autoHeight: true,
        style: formElementSep,
        width: 65,
        listWidth: 70,
        editable: false,
        forceSelection: true
    });

    var centerCombo = new Ext.ux.form.LovCombo({
        id: 'centerCombo',
        store: comboStore('receivingCenter', 'Name', true),
        listeners: {
            select:function(combo, rec, ndx) {
                (ndx == 0 ? comboSelectAllToggle(combo, rec, 'text', false) : null);
            }
        },
        mode: 'local',
        hideLabel: true,
        triggerAction: 'all',
        displayField:'text',
        valueField : 'id',
        emptyText:'Select centers...',
        border: false,
        autoHeight: true,
        style: formElementSep,
        width: 150,
        listWidth: 340,
        editable: false,
        forceSelection: true
    });

    var updatedAfter = {
        id: 'updatedAfter',
        xtype: 'datefield',
        name: 'updateAfter',
        style: formElementSep,
        width: 100,
        fieldLabel: 'Updated After'
    };

    var updatedBefore = {
        id: 'updatedBefore',
        xtype: 'datefield',
        name: 'updateBefore',
        style: formElementSep,
        width: 100,
        fieldLabel: 'Updated Before'
    };

    tcga.uuid.search.metadataForm = new Ext.form.FormPanel({
        id: 'metadataForm',
        title: 'Metadata',
        style: 'margin-top: 5px;',
        height: 165,
        border: true,
        layout: 'column',
        hideBorders: true,
        defaults: {
            layout: 'form'
        },
        items: [
            {
                height: 175,
                width: 175,
                labelWidth: 60,
                style: 'margin: 10px 5px 0 8px;border-right: solid 1px #d0d0d0;',
                hideBorders: true,
                items: [
                    uuidTypeCombo,
                    platformCombo,
                    participantTB,
                    batchTB
                ]
            },
            {
                height: 155,
                width: 305,
                style: 'margin: 10px 5px 0 0;border-right: solid 1px #d0d0d0;',
                hideBorders: true,
                labelWidth: 50,
                items: [
                    {
                        width: 305,
                        hideBorders: true,
                        layout: 'column',
                        items: [
                            {layout: 'form', style: 'margin-right: 10px;', items: comboChoice(
                                'diseaseChoiceCombo', 'diseaseCombo', 'disease',
                                new Ext.data.ArrayStore({fields: ['text'], data : [
                                    ['Abbreviation'],
                                    ['Study Name']
                                ]}),
                                'Disease', [
                                    ['Abbreviation']
                                ], false, 80)},
                            {layout: 'form', items: diseaseCombo}
                        ]
                    },
                    {
                        width: 305,
                        hideBorders: true,
                        layout: 'column',
                        items: [
                            {layout: 'form', style: 'margin-right: 10px;', items: comboChoice(
                                'sampleChoiceCombo', 'sampleCombo', 'sampleType',
                                new Ext.data.ArrayStore({fields: ['text'], data : [
                                    ['Type'],
                                    ['Code'],
                                    ['Short Letter Code']
                                ]}),
                                'Sample', [
                                    ['Type']
                                ], false, 115)},
                            {layout: 'form', items: sampleCombo}
                        ]
                    },
                    {
                        width: 305,
                        hideBorders: true,
                        layout: 'column',
                        items: [
                            {layout: 'form', labelWidth: 50, style: 'margin-right: 25px;', items: vialTB},
                            {layout: 'form', labelWidth: 50, items: portionTB}
                        ]
                    },
                    {
                        width: 305,
                        hideBorders: true,
                        layout: 'column',
                        items: [
                            {layout: 'form', style: 'margin-right: 10px;', items: comboChoice(
                                'analyteChoiceCombo', 'analyteCombo', 'analyteType',
                                new Ext.data.ArrayStore({fields: ['text'], data : [
                                    ['Type'],
                                    ['Code']
                                ]}),
                                'Analyte', [
                                    ['Type']
                                ], false, 80)},
                            {layout: 'form', items: analyteCombo}
                        ]
                    },
                    plateTB
                ]
            },
            {
                width: 440,
                style: 'margin-top: 10px;',
                hideBorders: true,
                labelWidth: 110,
                items: [
                    {
                        width: 440,
                        hideBorders: true,
                        layout: 'column',
                        items: [
                            {layout: 'form', style: 'margin-right: 10px;', items: comboChoice(
                                'bcrChoiceCombo', 'bcrCombo', 'bcr',
                                new Ext.data.ArrayStore({fields: ['text'], data : [
                                    ['Name'],
                                    ['Abbreviation'],
                                    ['Domain']
                                ]}),
                                'BCR Source', [
                                    ['Name']
                                ], false, 80)},
                            {layout: 'form', items: bcrCombo}
                        ]
                    },
                    {
                        width: 440,
                        hideBorders: true,
                        layout: 'column',
                        items: [
                            {layout: 'form', style: 'margin-right: 10px;', items: comboChoice(
                                'tssChoiceCombo', 'tssCombo', 'tissueSourceSite',
                                new Ext.data.ArrayStore({fields: ['text'], data : [
                                    ['Name'],
                                    ['TSS ID']
                                ]}),
                                'Tissue Source Site', [
                                    ['Name']
                                ], true, 80)},
                            {layout: 'form', items: tssCombo},
                            {layout: 'form', items: tssTB}
                        ]
                    },
                    {
                        width: 440,
                        hideBorders: true,
                        layout: 'column',
                        items: [
                            {layout: 'form', style: 'margin-right: 10px;', items: comboChoice(
                                'centerChoiceCombo', 'centerCombo', 'receivingCenter',
                                new Ext.data.ArrayStore({fields: ['text'], data : [
                                    ['Name'],
                                    ['Abbreviation'],
                                    ['Domain'],
                                    ['Code']
                                ]}), 'Receiving Center', [
                                    ['Name']
                                ], false, 80)},
                            {layout: 'form', style: 'margin-right: 10px;', items: centerCombo},
                            {layout: 'form', items: centerTypeCombo}
                        ]
                    },
                    {
                        width: 440,
                        hideBorders: true,
                        layout: 'column',
                        items: [
                            {layout: 'form', style: 'margin-right: 25px;', items: updatedBefore},
                            {layout: 'form', labelWidth: 85, items: updatedAfter}
                        ]
                    },
                    {
                        layout: 'column',
                        hideBorders: true,
                        items: [
                            {
                                width: (Ext.isGecko ? 266 : (Ext.isChrome ? 270 : 258)),
                                html: '<a class="hand" onclick="tcga.uuid.search.webServiceWin();">' +
                                    'Get the web service URL for this filter</a>',
                                style: 'padding-top: 5px;'
                            },
                            {
                                xtype: 'buttonplus',
                                id: 'resetButton',
                                width: 76,
                                height: 25,
                                text: '<span class="stdLabel">Reset<span>',
                                style: 'margin-right: 10px;',
                                handler: function() {
                                    tcga.uuid.search.metadataForm.getForm().reset();
                                    tcga.uuid.search.resetCombo();
                                    tcga.uuid.search.resetChoice();
                                }
                            },
                            {
                                xtype: 'button',
                                id: 'searchButton',
                                width: 76,
                                height: 25,
                                text: '<span class="stdLabel">Search<span>',
                                handler: function() {
                                    searchValues = Ext.util.JSON.encode(tcga.uuid.search.metadataForm.getForm().getFieldValues());
                                    browseStore.load({params:{start:0,limit:limit,searchParams:searchValues}});
                                }
                            }
                        ]
                    }
                ]
            }
        ]
    });

    return tcga.uuid.search.metadataForm;
};


tcga.uuid.search.webServiceWin = function() {

    function processResponse(el, success, response) {
        if (success) {
            var res = Ext.util.JSON.decode(response.responseText);
            el.update("<p><b>&nbsp;XML Format: </b><a target='_blank' href='" + res.urlXml + "'>" +
                Ext.util.Format.htmlEncode(res.urlXml) + "</a></p>" +
                "<p><b>&nbsp;JSON Format: </b><a target='_blank' href='" + res.urlJson + "'>" +
                Ext.util.Format.htmlEncode(res.urlJson) + "</a></p>");
        }
    }

    var win = new Ext.Window({
        title:"Biospecimen Metadata Browser Web Service URL",
        closable:true,
        width:525,
        height : 200,
        plain: true,
        autoScroll: true,
        autoLoad: {
            url: "buildWSUrl.json",
            params: {filter: Ext.util.JSON.encode(Ext.getCmp('metadataForm').getForm().getFieldValues())},
            callback: processResponse,
            discardUrl: true,
            nocache: true,
            text: "Loading...",
            timeout: 60,
            scripts: false
        },
        collapsible: false,
        modal: true,
        layout: 'fit',
        buttons: [
            {text: 'Close',handler: function() {
                win.hide();
            }}
        ]
    });
    win.show();
};

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

Ext.namespace('tcga.riphraph');

/*
 * An example of calling the graph drawing routine in a typical ExtJS javascript file.  In this
 * case, the javascript creates a filter for the graph that reloads the graph image based on the
 * selection from the dropdown.  I have not put in all of the selections, just a couple couple as
 * examples.
 *
 * Note that the filter dropdown uses the loadUrl function of the nodeDataStore to load a new
 * graph data file in place of the current one.  It also uses the nodeDataStore reset function
 * to go back to the originally loaded data file.
 *
 * The start function creates the store which takes care of then drawing the graph.  You don't
 * explicitly call the graph drawing routine, you just create a store and give it the url of the
 * data file to load.  There are optional parameters, as with the id in this example, that may be
 * specified as well.
 */

tcga.riphraph.createFilter = function () {
    var nodeDataStore = Ext.StoreMgr.get('nodeDataStore');

    var diseaseStore = new Ext.data.JsonStore({
        url: 'diseases.json',
        storeId: 'diseases',
        root: 'diseases',
        idProperty: 'tumorId',
        fields: [
            'tumorName',
            'tumorDisplayText'
        ],
        autoLoad: 'true',
        listeners: {
            load: function (store) {
                var diseaseRec = Ext.data.Record.create([
                    'tumorName',
                    'tumorDisplayText'
                ]);
                var allDiseases = new diseaseRec({
                    'tumorName': 'All',
                    'tumorDisplayText': 'Show diagram for all diseases'
                });
                store.insert(0, [allDiseases]);
            }
        }
    });

    var dateStore = new Ext.data.JsonStore({
        url: 'datesFromFile.json',
        storeId: 'dateStore',
        root: 'datesFromFile',
        autoLoad: 'true',
        fields: ['id', 'text']
    });

    var dateCombo = new Ext.form.ComboBox({
        name: 'dateCombo',
        id: 'dateCombo',
        width: 100,
        store: dateStore,
        mode: 'local',
        triggerAction: 'all',
        triggerConfig: {tag: "img", id: "dateComboTrigger", src: Ext.BLANK_IMAGE_URL, cls: "x-form-trigger " + this.triggerClass},
        displayField: 'text',
        valueField: 'id',
        emptyText: 'Select a date',
        tpl: '<tpl for="."><div id="date{[xindex]}" class="x-combo-list-item">{text}</div></tpl>',
        border: false,
        autoHeight: true,
        listeners: {
            select: function (combo, rec) {
                var diseaseCombo = Ext.getCmp('fieldDisease');
                nodeDataStore.loadUrl('pRepData.json?disease=' + diseaseCombo.getValue() +
                    '&date=' + rec.get('id'));
            }
        }
    });

    new Ext.Panel({
        renderTo: 'filterPanel',
        border: false,
        layout: 'column',
        items: [
            {
                border: false,
                width: 100,
                cls: 'stdLabel',
                style: 'margin-right: 10px;',
                html: 'Select Date'
            },
            {
                border: false,
                width: 800,
                cls: 'stdLabel',
                html: 'Select Disease'
            },
            dateCombo,
            {   // Spacer
                border: false,
                width: 10,
                html: '&nbsp;'
            },
            {
                id: 'fieldDisease',
                xtype: 'combo',
                store: diseaseStore,
                mode: 'local',
                triggerAction: 'all',
                triggerConfig: {tag: "img", id: "diseaseComboTrigger", src: Ext.BLANK_IMAGE_URL, cls: "x-form-trigger " + this.triggerClass},
                displayField: 'tumorDisplayText',
                valueField: 'tumorName',
                emptyText: 'select a disease',
                tpl: '<tpl for="."><div id="disease{[xindex]}" class="x-combo-list-item">{tumorDisplayText}</div></tpl>',
                border: false,
                autoHeight: true,
                style: 'margin-bottom: 10px;',
                width: 270,
                listeners: {
                    select: function (combo, rec) {
                        var dateCombo = Ext.getCmp('dateCombo');
                        nodeDataStore.loadUrl('pRepData.json?disease=' + rec.get('tumorName') +
                            '&date=' + dateCombo.getValue());
                    }
                }
            },
            {
                xtype: 'button',
                text: '<span style="font-family: tahoma;font-weight: bold;">Clear</span>',
                width: 100,
                style: 'margin-left: 10px;',
                handler: function () {
                    var dateCombo = Ext.getCmp('dateCombo');
                    var diseaseCombo = Ext.getCmp('fieldDisease');
                    dateCombo.setValue(dateCombo.getStore().getAt(0).get('id'));
                    diseaseCombo.setValue(diseaseCombo.getStore().getAt(0).get('tumorName'));
                    diseaseCombo.fireEvent('select', diseaseCombo, diseaseCombo.getStore().getAt(0), 0);
                }
            }
        ]
    });

    dateStore.on('load', function () {
        var dateCombo = Ext.getCmp('dateCombo');
        dateCombo.setValue(dateCombo.getStore().getAt(0).get('id'));
    });

    diseaseStore.on('load', function () {
        var diseaseCombo = Ext.getCmp('fieldDisease');
        diseaseCombo.setValue(diseaseCombo.getStore().getAt(0).get('tumorName'));
    });


}

tcga.riphraph.start = function () {

    var nodeDataStore = tcga.graph.draw.createStore({
        storeId: 'nodeDataStore',
        url: 'pRepData.json'
    });
    var mask = new Ext.LoadMask(Ext.getBody(), {msg: 'Loading diagram ...', store: nodeDataStore});
    tcga.riphraph.createFilter();
}

Ext.onReady(tcga.riphraph.start, this);
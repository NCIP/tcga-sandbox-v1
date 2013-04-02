/*******************************************************************************
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 ******************************************************************************/
var maxLengthForNotes = 4000; // based on the size of the db column

//temporary edit values used for editMode reset
var editMode = false;
var editItemBarcode;
var editDiseaseTumorId;
var editItemTypeId;
var editAnnotationId;
var editAnnotationCategoryId;
var editClassification;
var isRescindedText = "This Annotation has been rescinded.";
var redactionMsg = "Redacting this element means that all derived elements will be redacted as well and all corresponding data will no longer be accessible through DCC applications. Make sure you want to continue with this action.";
var rescindRedactionMsg = "You are requesting that a previous Redaction be rescinded. This will reinstate all derived elements, and will make accessible previously inaccessible data corresponding to these elements . Make sure you want to continue with this action.";
var rescindVerificationMessage = "You are about to permanently rescind this annotation";
	
var diseaseStore = new Ext.data.JsonStore({
    url:'diseases.json',
    storeId:'diseases',
    root:'diseases',
    idProperty:'tumorId',
    fields: ['tumorId', 'tumorName', 'tumorDescription'],
    autoLoad: true
});

diseaseRecordTaskLocation = Ext.data.Record.create([
    {name: "tumorId", type: "string"},
    {name: "tumorName", type: "string"},
    {name: "tumorDescription", type: "string"}
]);

var diseaseRecord = new diseaseRecordTaskLocation({
	//combo sets empty string to displayText - so trick it by assigning space
	//that way reset will pick displayText and not All
    tumorId: "  ",
    tumorName: "",
    tumorDescription: "All"
});

var diseaseStoreForSearch = new Ext.data.JsonStore({
    url:'diseases.json',
    storeId:'diseasesSearch',
    root:'diseases',
    idProperty:'tumorId',
    fields: ['tumorId', 'tumorName', 'tumorDescription'],
    autoLoad: true,
    listeners: {
    	load: function (){
    		diseaseStoreForSearch.insert(0,diseaseRecord);
			diseaseStoreForSearch.commitChanges();
		}
    }
});

// we seem to need a different data store for each combo box, otherwise
// they will conflict with each other and stop working.  Really annoying...

var itemTypeStoreForNew = new Ext.data.JsonStore({
    url: 'itemTypes.json',
    storeId: 'itemTypesNew',
    root: 'itemTypes',
    idProperty: 'itemTypeId',
    fields: ['itemTypeId', 'itemTypeName'],
    autoLoad: true
});

itemTypeRecordTaskLocation = Ext.data.Record.create([
    {name: "itemTypeId", type: "string"},
    {name: "itemTypeName", type: "string"}
]);

var itemTypeRecord = new itemTypeRecordTaskLocation({
	//combo sets empty string to displayText - so trick it by assigning space
	//that way reset will pick displayText and not All
    itemTypeId: "  ",
    itemTypeName: "All"
});

var itemTypeStoreForSearch = new Ext.data.JsonStore({
    url: 'itemTypes.json',
    storeId: 'itemTypesSearch',
    root: 'itemTypes',
    idProperty: 'itemTypeId',
    fields: ['itemTypeId', 'itemTypeName'],
    autoLoad: true,
    listeners: {
    	load: function (){
		    itemTypeStoreForSearch.insert(0,itemTypeRecord);
			itemTypeStoreForSearch.commitChanges();
		}
    }
});

var annotationCatStoreForNew = new Ext.data.JsonStore({
    url: 'annotationCategories.json',
    storeId: 'annotationCategoriesNew',
    root: 'annotationCategories',
    idProperty: 'categoryId',
    fields: ['categoryId', 'categoryName', 'itemTypes', 'annotationClassification'],
    autoLoad: true
});

annotationCatStoreForNew.on('load', function() {
    annotationCatStoreForNew.each(convertCategoryItemTypes);
});

annotationCatRecordTaskLocation = Ext.data.Record.create([
    {name: "categoryId", type: "string"},
    {name: "categoryName", type: "string"},
    {name: "itemTypes", type: "string"}
]);

var annotationCatRecord = new annotationCatRecordTaskLocation({
	//combo sets empty string to displayText - so trick it by assigning space
	//that way reset will pick displayText and not All
    categoryId: "  ",
    categoryName: "All",
    itemTypes: ""
});

var annotationCatStoreForSearch = new Ext.data.JsonStore({
    url: 'annotationCategories.json',
    storeId: 'annotationCategoriesSearch',
    root: 'annotationCategories',
    idProperty: 'categoryId',
    fields: ['categoryId', 'categoryName', 'itemTypes'],
    autoLoad: true,
    listeners: {
    	load: function (){
		    annotationCatStoreForSearch.insert(0,annotationCatRecord);
			annotationCatStoreForSearch.commitChanges();
		}
    }
});

annotationClassificationRecordTaskLocation = Ext.data.Record.create([
    {name: "annotationClassificationId", type: "string"},
    {name: "annotationClassificationName", type: "string"}
]);

var annotationClassificationRecord = new annotationClassificationRecordTaskLocation({
	//combo sets empty string to displayText - so trick it by assigning space
	//that way reset will pick displayText and not All
    annotationClassificationId: "  ",
    annotationClassificationName: "All"
});

var annotationClassificationStore = new Ext.data.JsonStore({
    url: 'annotationClassifications.json',
    storeId: 'annotationClassifications',
    root: 'annotationClassifications',
    idProperty: 'annotationClassificationId',
    fields: ['annotationClassificationId', 'annotationClassificationName'],
    autoLoad: true,
    listeners: {
        load: function() {
            annotationClassificationStore.insert(0, annotationClassificationRecord);
            annotationClassificationStore.commitChanges();
        }
    }
});

/*
 * The combo used to select the annotation type
 */
var annotationListsWidth = 250;
var newAnnotationCategoryCombo = new Ext.form.ComboBox({
	id: 'newAnnotationCategoryCombo',
    store: annotationCatStoreForNew,
    width:annotationListsWidth,
    resizable:true,
    displayField:'categoryName',
    valueField:'categoryId',
    editable: false,    
    mode: 'local',
    triggerAction: 'all',
    emptyText:'Select the annotation category...',
    selectOnFocus:true,
    fieldLabel: 'Annotation Category',
    hiddenId: 'newAnnotationCategoryId',
    hiddenName: 'annotationCategoryId',
    name: 'annotationCategory',
    allowBlank:false,
    validateOnBlur:false
});

/*
 * The combo used to select the item type.  Has a listener so that the annotation type combo options
 * are updated when the item type is changed.
 */
var newItemTypeCombo = new Ext.form.ComboBox({
    id: "newItemTypeCombo",
    store: itemTypeStoreForNew,
    displayField:'itemTypeName',
    valueField:'itemTypeId',
    editable: false,    
    mode: 'local',
    resizable:true,
    triggerAction: 'all',
    emptyText:'Select the type...',
    selectOnFocus:true,
    fieldLabel: 'Item to Annotate Is',
    name: 'itemType',
    hiddenId: 'newItemTypeId',
    hiddenName: 'itemTypeId',
    listeners: {
        select: {
            fn: function(combo, value) {
                filterAnnotationCategories(value.get('itemTypeId'),null);
            }
        }
    },
    allowBlank:false,
    validateOnBlur:false
});

var diseaseCombo = new Ext.form.ComboBox({
	id: 'diseaseComboBox',
    store: diseaseStore,    
    displayField:'tumorDescription',
    valueField:'tumorId',
    typeAhead: false,
    resizable:true,
    editable: false,
    mode: 'local',
    triggerAction: 'all',
    emptyText:'Select the disease...',
    selectOnFocus:true,
    fieldLabel: 'Disease',
    name: 'disease',
    hiddenId: 'diseaseId',
    hiddenName: 'diseaseId',
    allowBlank:false,
    validateOnBlur:false
});

/*
Annotation verification template
 */
var annotationVerifyTemplate = new Ext.Template(
    '<table border=0 class="x-form-item">',
        '<tr><td class="x-form-item-label annotationVerificationLabel">Disease:</td><td class="annotationVerificationValue">{disease}</td></tr>',
        '<tr><td class="x-form-item-label annotationVerificationLabel">Item to Annotate Is:</td><td class="annotationVerificationValue">{item}</td></tr>',
        '<tr><td class="x-form-item-label annotationVerificationLabel">Barcode of Item:</td><td class="annotationVerificationValue">{barcode}</td></tr>',
        '<tr><td class="x-form-item-label annotationVerificationLabel">Annotation Category:</td><td class="annotationVerificationValue">{category}</td></tr>',
        '<tr><td class="x-form-item-label annotationVerificationLabel">Annotation Note:</td><td class="annotationVerificationValue">{note}</td></tr>',
    '</table>',
    {
        compiled: true
    }
);

var annotationEditVerifyTemplate = new Ext.Template(
    '<div id="verificationMessage">{verificationMessage}</div><table border=0 class="x-form-item">',
        '<tr><td class="x-form-item-label annotationVerificationLabel">Disease:</td><td class="annotationVerificationValue">{disease}</td></tr>',
        '<tr><td class="x-form-item-label annotationVerificationLabel">Item to Annotate Is:</td><td class="annotationVerificationValue">{item}</td></tr>',
        '<tr><td class="x-form-item-label annotationVerificationLabel">Barcode of Item:</td><td class="annotationVerificationValue">{barcode}</td></tr>',
        '<tr><td class="x-form-item-label annotationVerificationLabel">Annotation Category:</td><td class="annotationVerificationValue">{category}</td></tr>',
        '<tr><td class="x-form-item-label annotationVerificationLabel">Annotation Status:</td><td class="annotationVerificationValue">{status}</td></tr>',
    '</table>',
    {
        compiled: true
    }
);

//possible status values for search
//all needs to be set to space and not empty string (to outdo extjs combo reset bug)
var statusData = [
        ['All', 	' ' ,	 'All'],
        ['Pending', 'Pending', 'All with items that have not been curated.'],
        ['Approved','Approved',  'All with items that have been curated.']
];

// create the status data store
var statusStore = new Ext.data.ArrayStore({
    fields: [
       {name: 'statusName'},
       {name: 'statusValue'},
       {name: 'statusDescription'}
    ]
});

//load status store for search
statusStore.loadData(statusData);

//possible status values for edit
var statusDataEdit = [
        ['Pending', 'Pending', 'All with items that have not been curated.'],
        ['Approved','Approved',  'All with items that have been curated.']
];

// create the status data store
var statusStoreEdit = new Ext.data.ArrayStore({
    fields: [
       {name: 'statusName'},
       {name: 'statusValue'},
       {name: 'statusDescription'}
    ]
});

//load status store for edit
statusStoreEdit.loadData(statusDataEdit);

var statusFieldSet = {
    id: 'statusSearch',
    xtype       : 'fieldset',
    flex        : 1,
    style: 'padding:0;margin:0',
    border      : false,
    items : [
    {
        id: 'statusSearchCombo',
        xtype: 'combo',
        resizable: true,
        width:annotationListsWidth,
        store: statusStore,
        displayField:'statusName',
        valueField:'statusValue',
		emptyText:'Select a state',
        editable: false,
        mode: 'local',
        triggerAction: 'all',
        triggerConfig: {tag: "img", id: "statusSearchTrigger", src: Ext.BLANK_IMAGE_URL, cls: "x-form-trigger " + this.triggerClass},
        tpl: '<tpl for="."><div id="statusSearch{[xindex]}" class="x-combo-list-item">{statusName}</div></tpl>',
        allowBlank: true,
        fieldLabel: 'Status',
        hiddenId: 'status',
        hiddenName: 'status',
        name: 'searchStatus',
        tabIndex:1
       }
      ]
    }
                            
var statusEditFieldSet = {
    id: 'statusEditFieldSet',
    xtype       : 'fieldset',
    flex        : 1,
    style: 'padding:0;margin:0',
    width:450,
    hidden:true,
    border      : false,
    items : [
    {
        id: 'statusEditCombo',
        xtype: 'combo',
        resizable: true,
        width:annotationListsWidth,
        store: statusStoreEdit,
        displayField:'statusName',
        valueField:'statusValue',
        editable: false,
        mode: 'local',
        triggerAction: 'all',
        triggerConfig: {tag: "img", id: "statusEditTrigger", src: Ext.BLANK_IMAGE_URL, cls: "x-form-trigger " + this.triggerClass},
        tpl: '<tpl for="."><div id="statusEdit{[xindex]}" class="x-combo-list-item">{statusName}</div></tpl>',
        allowBlank: true,
        fieldLabel: 'Status',
        hiddenId: 'editstatus',
        hiddenName: 'editstatus',
        name: 'editStatus',
        tabIndex:1
       }
      ]
    }

var rescindFieldSet = {
    id: 'rescindSearch',
    xtype       : 'fieldset',
    flex        : 1,
    style: 'padding:0;margin:0',
    border      : false,
    items : [{
        id: 'includeRescindedSearchCheckbox',
            xtype: 'checkbox',
            name: 'includeRescinded',
            boxLabel: 'Include Rescinded Annotations'
    }]
}
      
    //Add the restrictions associated to the 'Edit status' fieldset
    tcga.annotations.security.addRestrictionsMixedCollectionToDomId(
       'statusEditFieldSet',
        ['ROLE_ANNOTATIONS_ADMINISTRATOR'],
        null,
        null,
        null,
        true); 
        
	var itemValidationRadioFieldSet = {
	    id: 'itemValidationRadioFieldSet',
	    xtype       : 'fieldset',
	    flex        : 1,
	    style: 'padding:0;margin:0',
	    width: 450,
	    border      : false,
	    items : [
	    	{
	            id: "itemValidationRadio",
	            xtype          : 'radiogroup',
	            fieldLabel:'Item Validation',
	            items: [
	                {
	                    id: 'rb1',
	                    labelSeparator : ' ',
	                    inputValue     : 'true',
	                    boxLabel       : 'Strict',
	                    name: 'useStrictItemValidation',
	                    checked: true
	                },
	                {
	                    id: 'rb2',
	                    labelSeparator : ' ',
	                    inputValue     : 'false',
	                    boxLabel       : 'Relaxed',
	                    name: 'useStrictItemValidation',
	                    checked: false
	                }
	            ]
	    	}
	    ]
	}
 var verifyAnnotationFormSubmitButton = {
      text: 'Verify',
      id: 'verifyAnnotationFormSubmit',
      handler: function() {
          if (newAnnotationForm.getForm().isValid()) {
            newAnnotationVerificationForm.show();
            var submitWarningMsg = "";
			if(editMode == false){
	           Ext.get('verifyAnnotationEntry').dom.innerHTML = annotationVerifyTemplate.apply({
  	             disease: diseaseStoreForSearch.getAt(diseaseStoreForSearch.findExact('tumorId', diseaseCombo.getValue())).get('tumorDescription'),
	             item: itemTypeStoreForNew.getAt(itemTypeStoreForNew.findExact('itemTypeId', newItemTypeCombo.getValue())).get('itemTypeName'),
	             barcode: Ext.getCmp('newItemBarcode').getValue(),
	             category: annotationCatStoreForNew.getAt(annotationCatStoreForNew.findExact('categoryId', newAnnotationCategoryCombo.getValue())).get('categoryName'),
	             note: Ext.getCmp('newAnnotationNote').getValue()
	           });
	   		} else {	             
		        // redaction warnings
	            if (editClassification.toLowerCase() == "redaction" && Ext.getCmp('statusEditCombo').getValue().toLowerCase() == "approved") {	                        
	                if( Ext.getCmp('rescinded').getValue().toLowerCase() == "true" ) {
	                   	submitWarningMsg = rescindRedactionMsg;
	                } else {
	                   	submitWarningMsg = redactionMsg;
	                }   	
	                Ext.Msg.show({
	                    title: 'Warning',
	                    msg: submitWarningMsg,
	                    minWidth: 200,
	                    modal: true,
	                   	buttons: Ext.Msg.OK,
	                    icon: Ext.Msg.WARNING
	                });                     
	     		 }    
	     		         
	            var userVerificationMsg = "";
	            if( Ext.getCmp('rescinded').getValue().toLowerCase() == "true" ) {
	                 userVerificationMsg = rescindVerificationMessage;
	            } else {
	                 userVerificationMsg = "";
	            }
                    	
		        Ext.get('verifyAnnotationEntry').dom.innerHTML = annotationEditVerifyTemplate.apply({
		             verificationMessage: userVerificationMsg,
		             disease: diseaseStoreForSearch.getAt(diseaseStoreForSearch.findExact('tumorId', diseaseCombo.getValue())).get('tumorDescription'),
		 	         item: itemTypeStoreForNew.getAt(itemTypeStoreForNew.findExact('itemTypeId', newItemTypeCombo.getValue())).get('itemTypeName'),
		             barcode: Ext.getCmp('newItemBarcode').getValue(),
		             category: annotationCatStoreForNew.getAt(annotationCatStoreForNew.findExact('categoryId', newAnnotationCategoryCombo.getValue())).get('categoryName'),
		             status: Ext.getCmp('statusEditCombo').getValue()
		        });               
		     }
	         newAnnotationForm.hide();
	      } else {
	         showAddAnnotationFormErrors();
      	  }
       }
    }
    
/*
Form for entering information for new annotations
 */
var newAnnotationForm = new Ext.FormPanel({
	id:'newAnnotationForm',
    border: false,
    labelWidth: 150,
    defaults: {width: annotationListsWidth},
    url:'addAnnotation.json',
    method:'POST',
    waitMsg:'Saving new annotation to database...',
    defaultType: 'textfield',
    items: [
        diseaseCombo,
        newItemTypeCombo,
        {
            xtype: 'hidden',
            id: 'annotationId'
        },{
            xtype: 'hidden',
            id: 'rescinded',
            value: 'false'
        },{
            fieldLabel: 'Barcode of Item',
            name: 'item',
            id: 'newItemBarcode',
            vtype:'validateBarcode',
            allowBlank:false,
            validateOnBlur:true,
            stripCharsRe: /(^\s+|\s+$)/g
        },
        newAnnotationCategoryCombo,
        statusEditFieldSet,
        new Ext.form.TextArea({
            fieldLabel:'Annotation Note',
            name:'note',
            allowBlank:true,
            validateOnBlur:false,
            autoScroll: true,
            maxLength: maxLengthForNotes,
            id: 'newAnnotationNote'
        }),
        itemValidationRadioFieldSet
    ],
	buttons: [{
	  text: 'Reset',
	  id: 'resetFields',
	  handler: function() {
	      resetNewAnnotationForm();
	  }
	},{
	  text: 'Cancel',
	  id: 'cancelAnnotationFormSubmit',
	  handler: function() {
	     hideNewAnnotationWindow();
	  }
	},verifyAnnotationFormSubmitButton
	]
});

var newAnnotationVerificationForm = new Ext.FormPanel({
    labelWidth: 150,
    url:'addAnnotation.json',
    method:'POST',
    waitMsg:'Saving new annotation to database...',
    bodyStyle:'padding:5px 5px 0',
    width: 473,
    height:255,
    forceLayout: true,
    hidden: true,
    items: [{
            id: 'verifyAnnotationEntryContainer',
            xtype: 'panel',
            html: '<div id="verifyAnnotationEntry">&nbsp;</div>'
    }],
    buttons: [{
    	id: 'verifySubmitAnnotation',
        text: 'Submit',
        handler: function() {
                newAnnotationForm.getForm().submit({
                	waitTitle: 'Processing Annotation Submission',
                	waitMsg: 'Please wait...',
                    success: function(form, action) {
                    	addAnnotationSuccess(form, action);
                    },
                    failure: function(form, action) {
                    	failureFunction(form, action);
                    }
                });
                }
    },{
        text: 'Cancel',
    	id: 'verifyCancelAnnotation',
        handler: function() {
            newAnnotationForm.show();
            newAnnotationVerificationForm.hide();
            hideNewAnnotationWindow();
        }
    },{
    	id: 'verifyEditAnnotation',
        text: 'Edit',
        handler: function() {
            newAnnotationForm.show();
            newAnnotationVerificationForm.hide();
        }
    }]
});

/**
 * Add the note to the display if successful, show error message otherwise
 *
 * @param form
 * @param action
 */
function addAnnotationSuccess(form, action) {

    var response = Ext.util.JSON.decode(action.response.responseText);

    if (response.success == 'false') {
        failureFunction(form, action);
    } else {
        // hide the "new annotation" window and show the new annotation on the main panel
        newAnnotationForm.show();
        newAnnotationVerificationForm.hide();
        hideNewAnnotationWindow();
        
        if (editMode == false){
        	showAnnotation(response.annotation, 'New Annotation');
        } else {
        	showAnnotation(response.annotation, 'Annotation Details');
			editMode = false; //resetting editMode
			editClassification = ""; //resetting editClassification
		}
        refreshSearchResults();
    }
}

var newAnnotationWindow = new Ext.Window({
    id: 'newAnnotationWindow',
    layout:'fit',
    title: 'Annotation Entry',
    modal: true,
    onEsc:hideNewAnnotationWindow,
    width:500,
    height:310,
    closeAction:'hide',
    items: [{
        frame: true,
        layout: 'fit',
        items: [
            newAnnotationForm,
            newAnnotationVerificationForm
        ]
    }]
});

var newNoteForm = new Ext.FormPanel(makeNoteForm('addNote.json', addNoteSubmitHandler,
        function() {
            newNoteWindow.hide();
            newNoteForm.getForm().reset();
        }, 'add'));
var newNoteWindow;

var editNoteForm = new Ext.FormPanel(makeNoteForm('editNote.json', editNoteSubmitHandler,
        function() {
            editNoteWindow.hide();
            editNoteForm.getForm().reset();
        }, 'edit'));
var editNoteWindow = new Ext.Window({
    layout: 'fit',
    items: [editNoteForm],
    modal: true,
    title: 'Edit Note',
    width: 400,
    height: 250,
    closeAction: 'hide'
});

var defaultWelcomeMessage = 'Welcome to TCGA Annotations!'
                        + '<br/><br/>'
                        +'Please log in to get access to restricted operations such as adding and editing notes'
                        +' (You will only be able to edit your own notes).';

var annotationMainPanel = new Ext.Panel({
    region:'center',
    id:'annotationMain',
    border:false,
    layout: 'vbox',
    items:[
        {
            id: 'mainText',
            width:'100%',
            border: false,
            xtype: 'box',
            style:'padding:10px',
            autoEl: {
                html: defaultWelcomeMessage
            }
        }
    ]
});

//We need this wrapper around the panel declaration to be able to inject the value of the username
//(With a standard panel declaration, the username can not be injected, as its value is set after
//the panel is being instantiated.)
var annotationApplication;
function annotationApplicationRender() {
    var annotationsPanelTitle = 'TCGA Annotations';
    if(!annotationApplication) {

        annotationApplication = new Ext.Panel({
            id: 'annotationApplicationPanel',
            layout: 'border',
            height: 400, // will be changed dynamically if needed
            items: [
                {
                    region: 'north',
                    id: 'topframe',
                    title: annotationsPanelTitle,
                    layout: 'fit',
                    height: 53,
                    items:
                    {
                        xtype:'toolbar',
                        items: [
                            {
                                id: 'addNewAnnotationsButton',
                                xtype: 'buttonplus',
                                text: 'Add New Annotation',
                                cls:'x-btn-text-icon',
                                icon:'images/icons/add.png',
                                tooltip: 'Add New Annotation',
                                handler: function() {
                                	editMode = false;
                                	editClassification = "";
                                    resetNewAnnotationForm();
									newAnnotationWindow.removeClass('rescinded');  
                                    newAnnotationWindow.show(this);
            						Ext.get('newAnnotationWindow').child('.x-window-header-text').update("Add new annotation");
            						Ext.get('newAnnotationNote').dom.parentNode.parentNode.style.display = 'block';
                                    Ext.getCmp('statusEditFieldSet').setVisible(false);
                                    resetAnnotationsFormAccess();
                                    Ext.getCmp("annotationId").setValue(-1); //signals insert to the backend
                                },
                                disabled: true //disabled by default, will be enabled according to user's permissions
                            },
                            {
                                id: 'searchAnnotationsButton',
                                xtype: 'buttonplus',
                                text: 'Search Annotations',
                                cls: 'x-btn-text-icon',
                                icon: 'images/icons/find.png',
                                handler: function() {
                                    showSearch();
                                }
                            },
                            {
								id: 'manageAnnotationsTypesButton',
								xtype: 'buttonplus',
								text: 'Manage Annotation Types',
								cls: 'x-btn-text-icon',
								icon: 'images/icons/find.png',
								hidden: true,
								handler: function() {
									void(0);
								}
							},
							{
								id: 'manageCategoriesButton',
								xtype: 'buttonplus',
								text: 'Manage Categories',
								cls: 'x-btn-text-icon',
								icon: 'images/icons/find.png',
								hidden: true,
								handler: function() {
									void(0);
								}
							},
                            '->'
                            ,
                            tcga.annotations.security.getLogoutButton(),
                            tcga.annotations.security.getLoginButton(),
                            {
                                id: 'helpButton',
                                xtype: 'buttonplus',
                                text: 'Help',
                                iconCls: 'icon-help',
                                handler: function() {
                                    showHelp('main_page_help');
                                }
                            }
                        ]
                    }
                },
                annotationMainPanel

            ]
        });

        //Add the restrictions associated to buttons
        tcga.annotations.security.addRestrictionsMixedCollectionToDomId(
                'addNewAnnotationsButton',
                ['ROLE_ANNOTATION_CREATOR','ROLE_ANNOTATION_ITEM_CREATOR', 'ROLE_ANNOTATION_NOTE_CREATOR','ROLE_ANNOTATIONS_ADMINISTRATOR'],
                null,
                null,
                null,
                false);
        //NOT_YET to be removed once the buttons become active
        tcga.annotations.security.addRestrictionsMixedCollectionToDomId(
                'manageAnnotationsTypesButton',
                ['ROLE_ANNOTATIONS_ADMINISTRATOR_NOT_YET'],
                null,
                null,
                null,
                true);
                
        tcga.annotations.security.addRestrictionsMixedCollectionToDomId(
                'manageCategoriesButton',
                ['ROLE_ANNOTATIONS_ADMINISTRATOR_NOT_YET'],
                null,
                null,
                null,
                true);
        tcga.annotations.security.addRestrictionsMixedCollectionToDomId(
                'statusSearch',
                ['ROLE_ANNOTATIONS_ADMINISTRATOR'],
                null,
                null,
                null,
                true);
                                
        tcga.annotations.security.addRoleBasedTextUpdatesToDomId(
                'topframe',
                ['ROLE_ANNOTATIONS_ADMINISTRATOR'],
                'title',
                'TCGA Annotations - Administration',
                annotationsPanelTitle);
 
    	//Add the restrictions associated to the 'Edit Annotation' Button
    	tcga.annotations.security.addRestrictionsMixedCollectionToDomId(
            	'editAnnotationButton',
            	['ROLE_ANNOTATIONS_ADMINISTRATOR'],
            	null,
            	null,
            	null,
            	false);

		//Add the restrictions associated to the 'rescind Annotation' Button
        tcga.annotations.security.addRestrictionsMixedCollectionToDomId(
                'rescindAnnotationButton',
                ['ROLE_ANNOTATIONS_ADMINISTRATOR'],
                null,
                null,
                null,
                true);
                                           
        //Redraw restricted UI
        tcga.annotations.security.redrawRestrictedUI(['addNewAnnotationsButton']);
        tcga.annotations.security.redrawRestrictedUI(['manageAnnotationsTypesButton']);
        tcga.annotations.security.redrawRestrictedUI(['manageCategoriesButton']);
        tcga.annotations.security.redrawRoleBasedText();
    }

    //Time to render
    annotationApplication.render(Ext.get('main'));
}

var exportUrl = function(exportFormat) {
    if (exportFormat == null) {
        exportFormat = 'tab';
    }
    window.location = 'export.htm?exportFormat=' + exportFormat;
};

function getSearchParams() {
    var formParams = searchForm.getForm().getFieldValues();
    formParams.start = 0;
    formParams.limit = resultsGridPagingToolbar.pageSize;
    return formParams;
}
    
var searchFormItemPanel = new Ext.Panel({
    layout: 'hbox',
    fieldLabel: 'Barcode of Item',
    border: false,
    items: [
        {
            id: 'barcodeTextfield',
            xtype: 'textfield',
            name: 'item',
            allowBlank:true,
            tabIndex:2,
            width: annotationListsWidth
        },
        {
            xtype:'spacer',
            width:5
        },
        {
            id: 'exactMatchCheckbox',
            xtype: 'checkbox',
            name: 'exactItem',
            boxLabel: 'Exact Match',
            width: 100,
            tabIndex: 3
        }
    ]
});

var searchRadioGroup = new Ext.form.RadioGroup({

    id: 'searchRadioGroup',
    name: 'searchOption',
    xtype: 'radiogroup',
    fieldLabel: 'Search Option',
    items: [
        {
            id: 'searchByFilteringOptionRadio',
            name: 'searchOption',
            boxLabel: 'By Filtering',
            inputValue: 'searchByFiltering',
            checked: true,
            labelSeparator: ' '
        },
        {
            id: 'searchByIdOptionRadio',
            name: 'searchOption',
            boxLabel: 'By Id',
            inputValue: 'searchById',
            checked: false,
            labelSeparator : ' '
        }
    ],
    listeners: {
        change: function(radiogroup, radio) {

            searchForm.getForm().items.each(function(f) {
                if (f.id!='searchRadioGroup'){
                	f.reset();
                }
            });

            if (radio.inputValue == 'searchById') {
                searchWindow.setHeight(searchByIdWindowHeight);
                Ext.getCmp('searchFormCardPanel').layout.setActiveItem('searchByIdFieldSet');
                Ext.getCmp('annotationIdTextfield').focus();
            } else {
                searchWindow.setHeight(searchByFilterWindowHeight);
                Ext.getCmp('searchFormCardPanel').layout.setActiveItem('searchByFilterFieldSet');
            }
        }
    }
});

var searchForm = new Ext.FormPanel({

    id: 'searchForm',
    url: 'search.json',
    method: 'POST',
    frame: true,
    layout:'vbox',
    defaults: {
        border: false
    },
    width: 550,
    labelWidth: 150,
    items: [
        {
            id: 'searchFormRadioFieldSet',
            height: 40,
            width: 520,
            xtype: 'fieldset',
            items: searchRadioGroup
        },
        {
            id: 'searchFormCardPanel',
            layout: 'card',
            activeItem: 0,
            defaults: {
                border: false
            },
            items: [
                {
                    id: 'searchByFilterFieldSet',
                    xtype: 'fieldset',
                    height: 220,
                    items: [
                        {
                            id: 'diseaseCombo',
                            xtype: 'combo',
                            resizable: true,
                            width:annotationListsWidth,
                            store: diseaseStoreForSearch,
                            displayField:'tumorDescription',
                            valueField:'tumorId',
							emptyText:'Select a disease',
                            editable: false,
                            mode: 'local',
                            triggerAction: 'all',
                            triggerConfig: {tag: "img", id: "diseaseTrigger", src: Ext.BLANK_IMAGE_URL, cls: "x-form-trigger " + this.triggerClass},
                            tpl: '<tpl for="."><div id="itemType{[xindex]}" class="x-combo-list-item">{tumorDescription}</div></tpl>',
                            allowBlank: true,
                            fieldLabel: 'Disease',
                            hiddenId: 'searchDiseaseId',
                            hiddenName: 'diseaseId',
                            name: 'searchDisease',
                            tabIndex:1
                        },
                        searchFormItemPanel,
                        {
                            id: 'itemTypeCombo',
                            xtype: 'combo',
                            width:annotationListsWidth,
                            resizable:true,
                            store: itemTypeStoreForSearch,
                            displayField:'itemTypeName',
                            valueField:'itemTypeId',
							emptyText:'Select an item type',
                            editable: false,
                            mode: 'local',
                            triggerAction: 'all',
                            triggerConfig: {tag: "img", id: "itemTypeTrigger", src: Ext.BLANK_IMAGE_URL, cls: "x-form-trigger " + this.triggerClass},
                            tpl: '<tpl for="."><div id="itemType{[xindex]}" class="x-combo-list-item">{itemTypeName}</div></tpl>',
                            allowBlank: true,
                            fieldLabel: 'Item Type',
                            hiddenId:'searchItemTypeId',
                            hiddenName: 'itemTypeId',
                            name: 'searchItemType',
                            tabIndex:4
                        },
                        {
                            id: 'annotationClassificationCombo',
                            xtype: 'combo',
                            width: annotationListsWidth,
                            resizable: true,
                            store: annotationClassificationStore,
                            displayField: 'annotationClassificationName',
                            valueField: 'annotationClassificationId',
                            emptyText: 'Select an annotation classification',
                            editable: false,
                            mode: 'local',
                            triggerAction: 'all',
                            triggerConfig: {tag: "img", id: "annotationClassificationTrigger", src: Ext.BLANK_IMAGE_URL, cls: "x-form-trigger " + this.triggerClass},
                            tpl: '<tpl for="."><div id="annotationClassification{[xindex]}" class="x-combo-list-item">{annotationClassificationName}</div></tpl>',
                            allowBlank: true,
                            fieldLabel: 'Annotation Classification',
                            hiddenId: 'searchAnnotationClassificationId',
                            hiddenName: 'annotationClassificationId',
                            name: 'searchAnnotationClassification',
                            tabIndex:5
                        },
                        {
                            id: 'annotationCategoryCombo',
                            xtype: 'combo',
                            width:annotationListsWidth,
                            resizable:true,
                            store: annotationCatStoreForSearch,
                            displayField:'categoryName',
                            valueField:'categoryId',
							emptyText:'Select an annotation category',
                            editable: false,
                            mode: 'local',
                            triggerAction: 'all',
                            triggerConfig: {tag: "img", id: "annotationCategoryTrigger", src: Ext.BLANK_IMAGE_URL, cls: "x-form-trigger " + this.triggerClass},
                            tpl: '<tpl for="."><div id="annotationCategory{[xindex]}" class="x-combo-list-item">{categoryName}</div></tpl>',
                            allowBlank: true,
                            fieldLabel: 'Annotation Category',
                            hiddenId: 'searchAnnotationCategoryId',
                            hiddenName: 'annotationCategoryId',
                            name: 'searchAnnotationCategory',
                            tabIndex:6
                        },
                        statusFieldSet,
                        {
                            id: 'keywordTextfield',
                            xtype:'textfield',
                            width:annotationListsWidth,
                            fieldLabel:'Keyword',
                            name: 'keyword',
                            allowBlank: true,
                            tabIndex:7
                        },
                        {
                            id: 'annotatorTextfield',
                            xtype: 'textfield',
                            width: annotationListsWidth,
                            fieldLabel: 'Annotator',
                            name: 'annotatorUsername',
                            allowBlank: true,
                            tabIndex: 8
                        },
                        rescindFieldSet
                    ]
                },
                {
                    id: 'searchByIdFieldSet',
                    xtype: 'fieldset',
                    height: 90,
                    items: [
                        {
                            id: 'annotationIdTextfield',
                            xtype:'textarea',
                            width:annotationListsWidth,
                            fieldLabel:'Annotation Id(s)',
                            name: 'annotationId',
                            maskRe: /\d|,|;|\n|\r/i,
                            allowBlank: true,
                            tabIndex:1
                        }
                    ]
                }
            ]
        }
    ],
    buttons: [
        {
            id: 'searchButton',
            xtype: 'buttonplus',
            text: 'Search',
            handler: function() {
                searchAnnotations(getSearchParams());
            }
        },
        {
            id: 'resetButton',
            xtype: 'buttonplus',
            text: 'Reset',
            handler: function() {
                searchForm.getForm().reset();
            }
        },
        {
            id: 'cancelButton',
            xtype: 'buttonplus',
            text: 'Cancel',
            handler: function() {
                searchWindow.hide();
            }
        }
    ]

});

// FUNCTIONS (todo: move functions to their own JS file?)

function searchAnnotations(searchParams) {
    searchResultsStore.load({
        params:searchParams,
        callback: function(records, options, success) {
            if (searchWindow.isVisible()) {
                searchWindow.hide();
            }
            
            if (! success) {
                setMainText("Search failed");
            } else {
                setMainText(searchResultsStore.getTotalCount() + " annotations matched your search");
                searchResultsStore.lastParams = options;
                showSearchResults();
            }
        }
    });
}

var failureFunction = function(form, action) {

    var response = Ext.util.JSON.decode(action.response.responseText);
    var message = response.errorMessage;

    if (response.AccessDeniedException != null) {
        message = message + ': ' + response.AccessDeniedException;
    }

    //Converting UTF-8 new lines into HTML new lines
    message = message.replace(/\n/g, "<br/>");

    if(response.AuthenticationCredentialsNotFoundException != null) {
        tcga.annotations.security.logout();
        tcga.annotations.security.showLoginPopup("Error: Your session timed out.<br/>Please authenticate before re-submitting data.");
    } else {
        Ext.Msg.alert('Failure', message);
    }
};

var searchByFilterWindowHeight = 360;
var searchByIdWindowHeight = 200;

var searchWindow;
function showSearch() {
    if (!searchWindow) {
        searchWindow = new Ext.Window({
            layout:'fit',
            modal: true, //mask background
            onEsc: function() { searchWindow.hide(); },
            title: 'Search Annotations',
            width: 550,
            height: searchByFilterWindowHeight,
            closeAction: 'hide',
            items: searchForm
        });
    }
    //avail 'Search by Status' depending on role
    searchWindow.on('show', function() {
	    tcga.annotations.security.redrawRestrictedUI(['statusSearch']);
	}, this);
    searchWindow.show();
}

var searchResultsStore = new Ext.ux.data.PagingJsonStore({

    root: 'annotations',
    idProperty: 'id',
    url: 'search.json',
    autoSave: false,
    fields: [
        {
            name: 'id',
            type: 'int'
        },
        {
            name: 'annotationCategory',
            sortType: function(value) {
                return value.categoryName;
            }
        },
        'createdBy',
        {
            name: 'dateCreated',
            type: 'date',
            dateFormat: 'D M d G:i:s T Y'
        },
        'items',
        {
            name: 'disease',
            mapping: 'items[0].disease', // The first item's disease
            sortType: function(value) {
                return value.tumorName.toLowerCase();
            }
        },
        {
            name: 'itemType',
            mapping: 'items[0].itemType', // The first item's item type
            sortType: function(value) {
                return value.itemTypeName.toLowerCase();
            }
        },
        {
            name: 'item',
            mapping: 'items[0].item', // The first item's identifier
            sortType: function(value) {
                return value.toLowerCase();
            }
        },
        {
            name: 'notes',
            sortType: function(value) {
                if(value.length > 0){
                    return value[0].noteText.toLowerCase();
                }else{
                    return "";
                }
            }
        },
        'approved',
        'rescinded'
    ]
});

searchResultsStore.on("beforeload", function(store) {
	if (searchResultsStore.lastParams) {
		if (searchResultsStore.lastParams.params != undefined){
			searchResultsStore.lastParams.params.limit = resultsGridPagingToolbar.pageSize;
			Ext.apply(store.baseParams, searchResultsStore.lastParams.params);
		}
	}
	return true;
});


var combo = new Ext.form.ComboBox({
	name: 'perpage',
	width: 50,
	store: new Ext.data.ArrayStore({
	fields: ['id'],
	data: [['15'], ['50'], ['100'], ['500']]}),
	mode: 'local',
	value: '15',
	listWidth: 40,
	triggerAction: 'all',
	displayField: 'id',
	valueField: 'id',
	editable: false,
	forceSelection: true
});
		   
var resultsGridPagingToolbar = new Ext.PagingToolbar({
	id: 'resultsPagingTb',
    pageSize: 15,
    store: searchResultsStore,
    displayInfo: true,
    displayMsg: 'Displaying results {0} - {1} of {2}',
    emptyMsg: "No results to display",
	items: ['-','Per Page ',combo],
    doRefresh: refreshSearchResults
});
	
combo.on('select', function(combo, record) {
	resultsGridPagingToolbar.pageSize = parseInt(record.get('id'), 10);
	resultsGridPagingToolbar.doLoad(resultsGridPagingToolbar.cursor);
}, this);

var resultsGrid;
var searchGridView = new Ext.grid.GridView({ 
  forceFit: true, 
  getRowClass : function (row, index) {               
    var cls = ''; 
    var data = row.data; 
    switch (data.rescinded) { 
      case 'true' : 
        cls = 'grid-row-rescinded'; // highlight row red 
        break; 
    }//end switch                  
    return cls;
  } 
});  //end searchGridView

/**
 * The status column index (1-based)
 */
var statusColumnIndex = 9;

function showSearchResults() {

    if(resultsGrid == null) {
        var action = new Ext.ux.grid.RowActions({
            id: 'viewAction',
            header: '',
            hideable:false,
            actions: [
                {
                    iconCls:'detailAction',
                    tooltip:'View'
                }
            ],
            callbacks: {
                'detailAction': function(grid, record) {
                    showAnnotation(record.data, 'Annotation Detail');
                }
            }
        });

        var columns = [
            action,
            {
                header: 'ID',
                width:50,
                sortable: true,
                dataIndex: 'id',
                hideable:false
            },
            {
                header: 'Disease',
                width:50,
                sortable: true,
                dataIndex: 'disease',
                renderer: function(value, metaData, record, rowIndex, colIndex, store) {
                    return '<span id="disease' + rowIndex + '">' + value.tumorName + '</span>'; // Only the disease for the first item is shown for now
                }
            },
            {
                header: 'Item Type',
                width: 75,
                sortable: true,
                dataIndex: 'itemType',
                renderer: function(value, metaData, record, rowIndex, colIndex, store) {
                    return value.itemTypeName; // Only the item type for the first item is shown for now
                }
            },
            {
                header: 'Item Barcode',
                width: 150,
                sortable: true,
                dataIndex: 'item',
                renderer: function(value, metaData, record, rowIndex, colIndex, store) {
                    return value; // Only the item value for the first item is shown for now
                }
            },
            {
                header: 'Classification',
                sortable: true,
                width: 100,
                dataIndex: 'annotationCategory',
                renderer: function(value, metaData, record, rowIndex, colIndex, store) {
                    return value.annotationClassification.annotationClassificationName;
                }
            },
            {
                header: 'Category',
                sortable: true,
                width: 125,
                dataIndex: 'annotationCategory',
                renderer: function(value, metaData, record, rowIndex, colIndex, store) {
                    return value.categoryName;
                }
            },
            {
                id:'annotationColumn',
                header: 'Annotation',
                sortable: true,
                dataIndex: 'notes',
                renderer: function(value, metaData, record, rowIndex, colIndex, store) {
                    if(value.length > 0){
                        return  Ext.util.Format.ellipsis(value[0].noteText, 50, true);
                    }else{
                        return  Ext.util.Format.ellipsis("", 50, true);
                    }
                }
            },
            {
                header: 'Annotator',
                sortable: true,
                dataIndex: 'createdBy',
                renderer: function(value, metaData, record, rowIndex, colIndex, store) {
                    return value;
                }
            },
            {
                header: 'Date Created',
                width: 75,
                xtype: 'datecolumn',
                sortable: true,
                renderer: Ext.util.Format.dateRenderer('m/d/Y g:i A'),
                dataIndex: 'dateCreated'
            },
            {
                header: 'Status',
                sortable: true,
                dataIndex: 'approved',
                renderer: function(value, metaData, record, rowIndex, colIndex, store) {
                	//if approved and not rescinded show 'approved', else if approved and rescinded show 'rescinded', else show 'pending'
                    return value == 'true' ? (record.get('rescinded') == 'false' ? 'Approved' : 'Rescinded') : 'Pending';
                },
                hidden: tcga.annotations.security.getUsername() == null
            }
        ];

        resultsGrid = new Ext.grid.GridPanel({
            store: searchResultsStore,
            title: 'Search Results',
            enableColumnHide: false,
            columns: columns,
            plugins:[action],
            stripeRows: true,
            view: searchGridView,
            width: 934,
            height: 425,
            deferRowRender: false,
            forceFit: true,
            autoExpandColumn:'annotationColumn',
            autoExpandMax: 2000,            
            sm: new Ext.grid.RowSelectionModel({singleSelect:true}),
            bbar: resultsGridPagingToolbar,
            tbar:  {
                items: [{
                    menu:
                            [
                                {
                                    text: 'Excel',
                                    iconCls: 'icon-xl',
                                    handler: function(){
                                        exportUrl("xl");
                                    }
                                },
                                {
                                    text: 'CSV',
                                    iconCls: 'icon-txt',
                                    handler: function(){
                                        exportUrl("csv");
                                    }
                                },
                                {
                                    text: 'Tab-delimited',
                                    iconCls: 'icon-txt',
                                    handler: function(){
                                        exportUrl("tab");
                                    }
                                }
                            ],
                    id: 'exportDataMenu',
                    text: 'Export Data',
                    iconCls: 'icon-grid'
                }]}
        });

		//Create a show/hide menu button on the toolbar
		var view = resultsGrid.getView();
		view.colMenu = new Ext.menu.Menu({
			listeners: {
				beforeshow: view.beforeColMenuShow,
				itemclick: view.handleHdMenuClick,
				scope: view
			}
		});
		resultsGrid.getTopToolbar().add('-',{
		    iconCls: 'x-cols-icon',
		    text: 'Show/Hide Columns', 
		    menu: view.colMenu
		    },'-',
		    {
		    text: 'Reset Table',
		    iconCls: 'icon-reset',
			handler: function() {
			window.location.href = "index.jsp";
			} 
		});
		
        resultsGrid.on('rowdblclick', function(grid, rowIdx, e) {
            showAnnotation(searchResultsStore.getAt(rowIdx).data, 'Annotation Detail');
        });

        annotationMainPanel.add(resultsGrid);
        adjustHeight();
    }
}

function refreshSearchResults() {
    if (resultsGrid != null) {
        searchAnnotations(searchResultsStore.lastParams.params);
    }
}

function redrawSearchResults() {
    if (resultsGrid != null) {
        resultsGrid.getColumnModel().setHidden(statusColumnIndex, tcga.annotations.security.getUsername() == null);
        searchAnnotations(searchResultsStore.lastParams.params);
    } else if (tcga.annotations.security.getUsername() != null) {
        // no search yet, change the message on the screen
        setMainText("Welcome to TCGA Annotations, " + tcga.annotations.security.getUsername() + "!<br/><br/>Search results will include pending annotations that you created along with all approved annotations that match your criteria.");
    }
}

/**
 * Filters the annotation categories, showing only those that are valid with the given item type.
 *
 * @param itemTypeRecord the item type that was selected
 */
function filterAnnotationCategories(itemTypeId,categoryId) {
    newAnnotationCategoryCombo.clearValue();
    if (itemTypeRecord) {
        newAnnotationCategoryCombo.setReadOnly(false);
        newAnnotationCategoryCombo.store = annotationCatStoreForNew;
        // first clear the filter, restoring all options
        if (annotationCatStoreForNew.realSnapshot) {
            annotationCatStoreForNew.snapshot = annotationCatStoreForNew.realSnapshot;
        }
        annotationCatStoreForNew.clearFilter(true);
        // then apply the filter
        annotationCatStoreForNew.filter('itemTypes', new RegExp(',' + itemTypeId + ','), true, false);
        // then store the real snapshot and set the filtered data to be the snapshot
        // this is weird, but otherwise the filter wasn't working correctly -- it seems the combo box applies filters
        // on its own sometimes, which was clearing this filter
        annotationCatStoreForNew.realSnapshot = annotationCatStoreForNew.snapshot;
        annotationCatStoreForNew.snapshot = annotationCatStoreForNew.data;
        
        if (categoryId != null)
        	newAnnotationCategoryCombo.setValue(categoryId);
        
    } else {
        newAnnotationCategoryCombo.setReadOnly(true);
    }
}

/**
 * Reset the form when the window goes away. 
 * Also hide any verification windows left open during incomplete entries.
 */
function hideNewAnnotationWindow() { 
    Ext.getCmp('statusEditFieldSet').setVisible(false);
	newAnnotationWindow.removeClass('rescinded');
    newAnnotationWindow.hide();
    newAnnotationVerificationForm.hide();
    resetNewAnnotationForm();
}

/**
 * Resets the "new annotation form".  Also sets the annotation category selector to read-only, because no value can
 * be chosen for it until an item type is picked.
 */
function resetNewAnnotationForm() {

	if (editMode == true) { //editmode resets to original preedit values
		Ext.getCmp('statusEditCombo').setValue(editStatus);
        Ext.getCmp('newItemBarcode').setValue(editItemBarcode);
        Ext.getCmp('diseaseComboBox').setValue(editDiseaseTumorId);
        Ext.getCmp('newItemTypeCombo').setValue(editItemTypeId);
        filterAnnotationCategories(editItemTypeId,editAnnotationCategoryId);
    } else {        
	    newAnnotationForm.getForm().reset();
	    // clear filter in store
	    if (annotationCatStoreForNew.realSnapshot) {
	        annotationCatStoreForNew.snapshot = annotationCatStoreForNew.realSnapshot;
	    }
	    annotationCatStoreForNew.clearFilter(true);
	    newAnnotationCategoryCombo.setReadOnly(true);
	
	    //Reset the radio group (somehow getForm().reset() does not do it)
	    Ext.getCmp('itemValidationRadio').setValue("true");
    }
}

/*
 * Display an appropriate error if the Annotation addition form is invalid.  Abstracted from the submit function
 * so that it can be used for submit and for verify.
 */
function showAddAnnotationFormErrors() {
    var message = '';
    if (Ext.getCmp('diseaseComboBox').getValue() == '') {
        message = 'Disease is required to create a new annotation.';
    }else if (Ext.getCmp('newItemTypeCombo').getValue() == '') {
        message = 'Item is required to create a new annotation.';
    }else if (!Ext.getCmp('newItemBarcode').isValid()) {
         message = 'Invalid ' + newItemTypeCombo.getRawValue() + ' barcode format.  ';
    }else if (Ext.getCmp('newAnnotationCategoryCombo').getValue() == '') {
        message = 'Category field is required to create a new annotation.';
    }else if (!Ext.getCmp('newAnnotationNote').isValid()) {
        if (Ext.getCmp('newAnnotationNote').getValue() != '') {
            message += 'Max allowed note length is ' + maxLengthForNotes + '.';
        }
    }
    Ext.Msg.alert('Error', message);
}

/**
 * This takes the records in the annotationCategoryStore, and updates the itemTypes value to be a string of the values
 * appended together with commas, rather than an array of integers.  This is used by the form to filter the allowed
 * annotation categories when an item type is selected -- a regex is used which is why a string is needed!
 * @param annotationCategoryRecord the record to convert
 */
function convertCategoryItemTypes(annotationCategoryRecord) {
    var itemArray = annotationCategoryRecord.get('itemTypes');
    var itemString = '';
    // append together all the item types, with commas before and after each one (e.g. ",1,2,3,")
    for (var i=0; i<itemArray.length; i++) {
        itemString += ',' + itemArray[i].itemTypeId + ',';
    }
    // set the value in the record -- this will overwrite the initial value which is an array
    annotationCategoryRecord.set('itemTypes', itemString);
}

var currentDetailPanel, detailWindow, notePanel;

function showDetailInWindow(detailPanel, windowTitle) {
    // create new detail window if needed
    if (detailWindow == null) {
        detailWindow = new Ext.Window({
            layout: 'fit',
            title: windowTitle,
            width: 800,
            height: 500,
            closeAction: 'hide',
            buttons: [
                { text: 'Close',
                handler: function() {
                    detailWindow.hide();
                }}
            ]
        });
    } else if (currentDetailPanel) {
        // otherwise, remove old detail panel
        detailWindow.remove(currentDetailPanel);
    }

    detailWindow.setTitle(windowTitle);
    detailWindow.add(detailPanel);
    if (detailWindow.isVisible()) {
        detailWindow.doLayout();
    } else {
        detailWindow.show();
    }
    currentDetailPanel = detailPanel;
}

/**
 * Shows the annotation object given in a panel in the application.  If there is already an annotation shown, it will
 * be removed and replaced with this new one.
 * 
 * Note: Only the first item is shown
 *
 * @param annotation the annotation object (JSON object version of DccAnnotation bean)
 */
function showAnnotation(annotation, windowTitle) {

    // make new panel for notes
    notePanel = new Ext.Panel({
        layout:'table',
        layoutConfig: {columns: 1},
        bodyStyle:'padding-bottom:8px',
        autoHeight: true,
        defaults: {
            border: false            
        }
    });

    var annotationItems = [annotation.items.length];
    for (var i=0; i<annotation.items.length; i++) {
        var item = annotation.items[i];
        annotationItems[i] = {
            xtype: 'label',
            border: false,
            html: item.item,
            cls: 'clickable',
            height:25,
            width:400,
            qtipText: 'Click to search for all annotations for this ' + item.itemType.itemTypeName,
            listeners: {
                render: function(c){
                  c.getEl().on({
                    click: function(el){
                        getItemDetail(item.item, item.itemType.itemTypeId);
                    },
                    scope: c
                  });
                }
              }
        };
    }

    // now rescind button edit Note to this annotation
    var rescindAnnotationsButton = {
        id: 'rescindAnnotationButton',
        xtype:'button',
        text: 'Rescind Annotation',
        cls: 'x-btn-text-icon',
        tooltip: 'Rescind this annotation',
        icon: 'images/icons/action-stop-icon.png',
		hidden: true,
        handler: function() { 
            editMode = true;
            
            editClassification = annotation.annotationCategory.annotationClassification.annotationClassificationName;
	            	
            newAnnotationForm.show();
            newAnnotationWindow.show(this);
	        newAnnotationWindow.addClass('rescinded');
            Ext.get('newAnnotationNote').dom.parentNode.parentNode.style.display = 'none';
            Ext.get('newAnnotationWindow').child('.x-window-header-text').update("WARNING: Rescind annotation");
            
            editItemBarcode = annotation.items[0].item;
            editDiseaseTumorId = annotation.items[0].disease.tumorId;
            editItemTypeId = annotation.items[0].itemType.itemTypeId;
            editAnnotationId = annotation.id;
            editAnnotationCategoryId = annotation.annotationCategory.categoryId;
            editStatus = getAnnotationStatusText(annotation);
            
            Ext.getCmp('newItemBarcode').setValue(editItemBarcode);
            Ext.getCmp('diseaseComboBox').setValue(editDiseaseTumorId);
            Ext.getCmp('newItemTypeCombo').setValue(editItemTypeId);
            Ext.getCmp('annotationId').setValue(editAnnotationId);
            filterAnnotationCategories(editItemTypeId,editAnnotationCategoryId);
    		Ext.getCmp('statusEditCombo').setValue(editStatus);
			tcga.annotations.security.redrawRestrictedUI(['statusEditFieldSet']);

            Ext.getCmp('rescinded').setValue('true');
            newAnnotationForm.setHeight('auto');
			restrictAnnotationsFormAccess(null); 	
			verifyAnnotationFormSubmitButton.handler.call(verifyAnnotationFormSubmitButton.scope, verifyAnnotationFormSubmitButton, Ext.EventObject);
        }
    };
    
    // now edit button edit Note to this annotation
    var editAnnotationsButton = {
        id: 'editAnnotationButton',
        xtype:'button',
        text: 'Edit this annotation',
        cls: 'x-btn-text-icon',
        tooltip: 'Edit this annotation',
        icon: 'images/icons/note_edit.png',
        disabled: true,
        handler: function() { 
            editMode = true;
            editClassification = annotation.annotationCategory.annotationClassification.annotationClassificationName;
	            	
            newAnnotationForm.show();
            newAnnotationWindow.show(this);		            
			newAnnotationWindow.removeClass('rescinded');  
			
            Ext.get('newAnnotationNote').dom.parentNode.parentNode.style.display = 'none';
            Ext.get('newAnnotationWindow').child('.x-window-header-text').update("Edit annotation");
            
            editItemBarcode = annotation.items[0].item;
            editDiseaseTumorId = annotation.items[0].disease.tumorId;
            editItemTypeId = annotation.items[0].itemType.itemTypeId;
            editAnnotationId = annotation.id;
            editAnnotationCategoryId = annotation.annotationCategory.categoryId;
            editStatus = getAnnotationStatusText(annotation);
            
            Ext.getCmp('newItemBarcode').setValue(editItemBarcode);
            Ext.getCmp('diseaseComboBox').setValue(editDiseaseTumorId);
            Ext.getCmp('newItemTypeCombo').setValue(editItemTypeId);
            Ext.getCmp('annotationId').setValue(editAnnotationId);
            filterAnnotationCategories(editItemTypeId,editAnnotationCategoryId);
    		Ext.getCmp('statusEditCombo').setValue(editStatus);
			tcga.annotations.security.redrawRestrictedUI(['statusEditFieldSet']);
			
			Ext.getCmp('rescinded').setValue(annotation.rescinded);

			//if admin is not owner only allow access to the state
			if (annotation.createdBy != tcga.annotations.security.getUsername())
	            restrictAnnotationsFormAccess('statusEditCombo');      
	        else
	        	resetAnnotationsFormAccess();			
        }
    };
			
    // For now, the item's label, is the first item's item type
    var itemLabel = Ext.util.Format.capitalize(annotation.items[0].itemType.itemTypeName) + ': ';
    var items = [];
    var itemsIndex = 0;
    var editButtonIndex;
    var rescindButtonIndex;
    var detailMessageText;
    
    if( annotationIsRescinded(annotation) )
    	detailMessageText = isRescindedText;
    else
    	detailMessageText = "";    	
    
    items[itemsIndex++] = { html: '<div id="detailMessageText">' + detailMessageText + '</div>'};
    editButtonIndex = itemsIndex;  //this index keeps track of placement of edit button
    items[itemsIndex++] = editAnnotationsButton;
    items[itemsIndex++] = { html: itemLabel , cellCls: 'detailHeading' };
    items[itemsIndex++] = { xtype: 'panel', layout: 'vbox', items: annotationItems, cellCls:'detailValue', height: 30 * annotation.items.length};
    items[itemsIndex++] = { html: 'Disease: ', cellCls: 'detailHeading'};
    items[itemsIndex++] = { html: annotation.items[0].disease.tumorDescription, cellCls: 'detailValue'};
    items[itemsIndex++] = { html: 'Classification: ', cellCls: 'detailHeading'};
    items[itemsIndex++] = { html: annotation.annotationCategory.annotationClassification.annotationClassificationName, cellCls:'detailValue'};
    items[itemsIndex++] = { html: 'Category: ', cellCls: 'detailHeading'};
    items[itemsIndex++] = { html: annotation.annotationCategory.categoryName, cellCls:'detailValue'};
    items[itemsIndex++] = { html: 'Created On: ', cellCls: 'detailHeading'};
    items[itemsIndex++] = { html: Ext.util.Format.date(annotation.dateCreated, 'm/d/Y g:i A'), cellCls:'detailValue'};
    items[itemsIndex++] = { html: 'Created By: ', cellCls: 'detailHeading'};
    items[itemsIndex++] = { html: annotation.createdBy, cellCls:'detailValue'};
    if (tcga.annotations.security.getUsername() != null) {
        items[itemsIndex++] = { html: 'Status: ', cellCls: 'detailHeading' };
        
        items[itemsIndex++] = { html: annotationIsRescinded(annotation) ? 'Rescinded' : getAnnotationStatusText(annotation), cellCls:'detailValue'};
    	if (!annotationIsApproved(annotation) && !annotationIsRescinded(annotation)){
		    items[editButtonIndex].disabled = false;
		}
    }
    
    items[itemsIndex++] = { html: '  ' };
    rescindButtonIndex = itemsIndex;  //this index keeps track of placement of rescind button
    items[itemsIndex++] = rescindAnnotationsButton;
    items[itemsIndex++] = { html: 'Notes:', cellCls: 'detailHeading topAligned' };
    items[itemsIndex++] = notePanel;

    var detailPanel = new Ext.Panel({
        layout:'table',     
        id:'annotationDetailPanel',   
        layoutConfig: { columns: 2 },
        autoScroll: true,        
        bodyStyle:'padding-bottom:8px',        
        border: true,
        defaults: {
            border: false,
            bodyStyle:'padding:5px'
        },
        items: items
    });

    // now add button for adding new Note to this annotation
    detailPanel.add({ html:' '});
    detailPanel.add({
        id: 'addNoteButton',
        xtype:'button',
        text: 'Add Note',
        cls: 'x-btn-text-icon',
        tooltip: 'Add a new note to this annotation',
        icon: 'images/icons/note_add.png',
        disabled: true, //disabled by default, will be enabled according to user's permissions
        handler: function() {
            if (!newNoteWindow) {
                newNoteWindow = new Ext.Window({
                    layout: 'fit',
                    items: newNoteForm,
                    modal: true,
                    title: 'Enter New Note',
                    width: 400,
                    height: 250,
                    closeAction: 'hide'
                });
            }
            newNoteWindow.show();
            // can't get DOM object until the window is rendered!
            Ext.getCmp('addNoteFormIdField').setValue(annotation.id);
        }
    });

    //Add the restrictions associated to the 'Add Note' Button
    tcga.annotations.security.addRestrictionsMixedCollectionToDomId(
            'addNoteButton',
            ['ROLE_ANNOTATION_ITEM_CREATOR'],
            null,
            null,
            null,
            false);

    //Redraw restricted UI
    tcga.annotations.security.redrawRestrictedUI(['addNoteButton']);

    // Add rows to the display for each note
    if(annotation.notes.length > 0){
        Ext.each(annotation.notes, function(note, i) {
            addNote(note, i == 0);
        });

    }
    
    if (!annotationIsApproved(annotation)){
		tcga.annotations.security.redrawRestrictedUI(['editAnnotationButton']);
	} else if (annotationIsApproved(annotation) && !annotationIsRescinded(annotation) ){
        tcga.annotations.security.redrawRestrictedUI(['rescindAnnotationButton']);
	} else if (annotationIsRescinded(annotation) ){
		Ext.getCmp('annotationDetailPanel').addClass('rescindedDetail'); 
		windowTitle = windowTitle + ": " + isRescindedText;
	}
    showDetailInWindow(detailPanel, windowTitle);
}

function adjustHeight() {
    annotationMainPanel.doLayout();
    annotationApplication.doLayout();
    var height = Ext.get('mainText').getSize().height;    
    if (resultsGrid != null && resultsGrid.isVisible()) {        
        height += resultsGrid.getSize().height;        
    }

    annotationMainPanel.setHeight(height);
    annotationApplication.setHeight(annotationApplication.layout.north.getSize().height + annotationMainPanel.getSize().height);
}

//disables fields of annotation form
function restrictAnnotationsFormAccess(nonRestrictItem) {
	Ext.getCmp('newItemBarcode').setReadOnly(true);
	Ext.getCmp('newItemBarcode').addClass('x-noedit');
	Ext.getCmp('diseaseComboBox').setReadOnly(true);
	Ext.getCmp('diseaseComboBox').addClass('x-noedit');
	Ext.getCmp('newItemTypeCombo').setReadOnly(true);
	Ext.getCmp('newItemTypeCombo').addClass('x-noedit');
	Ext.getCmp('newAnnotationCategoryCombo').setReadOnly(true); 
	Ext.getCmp('newAnnotationCategoryCombo').addClass('x-noedit');
	Ext.getCmp('statusEditCombo').setReadOnly(true); 
	Ext.getCmp('statusEditCombo').addClass('x-noedit');
	
	if( nonRestrictItem != null ){
		Ext.getCmp(nonRestrictItem).setReadOnly(false); 
		Ext.getCmp(nonRestrictItem).removeClass('x-noedit');  
	}
		
}

//enables fields of annotation form
function resetAnnotationsFormAccess() {
	Ext.getCmp('newItemBarcode').setReadOnly(false);
	Ext.getCmp('newItemBarcode').removeClass('x-noedit');
	Ext.getCmp('diseaseComboBox').setReadOnly(false);
	Ext.getCmp('diseaseComboBox').removeClass('x-noedit');
	Ext.getCmp('newItemTypeCombo').setReadOnly(false);
	Ext.getCmp('newItemTypeCombo').removeClass('x-noedit');
	Ext.getCmp('newAnnotationCategoryCombo').setReadOnly(false); 
	Ext.getCmp('newAnnotationCategoryCombo').removeClass('x-noedit');   
	Ext.getCmp('statusEditCombo').setReadOnly(false); 
	Ext.getCmp('statusEditCombo').removeClass('x-noedit');  
}

function getItemDetail(itemName, itemTypeId) {
    var searchParams = {
        start: 0, limit: 15, item: itemName, itemTypeId: itemTypeId, exactItem: true
    };
    searchAnnotations(searchParams);
}

/**
 * Sets the text in the box at the top of the app.  Used to report statuses of requests, to show welcome message, etc.
 * @param text the text to display
 */
function setMainText(text) {
    Ext.get('mainText').update(text);
}

function parseDate(dateString) {
    if (Ext.isDate(dateString)) {
        return dateString;
    }
    var date = Date.parseDate(dateString, 'Y-m-d H:i:s.u');
    if (! date) {
        date = Date.parseDate(dateString, 'D M d G:i:s T Y');
    }
    return date;
}

/**
 * Adds a note to the display.  Will add to the current display panel -- in future maybe check that the note is
 * for the displayed annotation?  Would that ever be a problem?
 * @param note object that has noteText, addedBy, and dateAdded
 */
function addNote(note, isFirst) {
    note.dateAdded = parseDate(note.dateAdded);

    var noteText = Ext.util.Format.nl2br(Ext.util.Format.htmlEncode(note.noteText));

    var noteHeader = {
        layout: 'hbox',        
        items:
            [{
                xtype: 'box',
                html: 'Added by ' + note.addedBy + ' on ' + note.dateAdded.format('m/d/Y g:i A'),
                flex: 10,
                border: false
            }],
        width:'100%',
        height: 30,
        cellCls:'noteHeading',        
        bodyStyle:'padding:2px;margin-top:5px;background-color:#dddddd;' // bg-color in noteHeading not working for some reason?
    };

    noteHeader.items[1] = {
        id: 'editNoteButton' + note.noteId,
        disabled: true, //disabled by default, will be enabled according to user's permissions
        xtype:'button',
        flex: 0,
        icon:'images/icons/note_edit.png',
        tooltip: 'Edit note',
        handler: function() {
           editNote(note.noteId);
        }
    };

    //Add the restrictions associated to the 'Edit Note' Button
    tcga.annotations.security.addRestrictionsMixedCollectionToDomId(
            'editNoteButton' + note.noteId,
            ['ROLE_ANNOTATION_NOTE_EDITOR'],
            'ACL_ANNOTATION_NOTE_EDITOR',
            'DccAnnotationNote',
            note.noteId,
            false);

    notePanel.add(noteHeader);
    notePanel.add({
        xtype: 'box',
        html: noteText,
        cellCls:'detailValue noteText',
        border:true,
        bodyStyle:'padding:3px;',
        id: 'note-' + note.noteId
    });

    var noteMeta = '';
    if (note.editedBy) {
        note.dateEdited = parseDate(note.dateEdited);
        noteMeta = 'Edited by ' + note.editedBy + ', ' + note.dateEdited.format('m/d/Y g:i A');
    }
    notePanel.add({
        cellCls: 'noteMeta',
        html: noteMeta,
        id: 'note-meta-' + note.noteId
    });

    //Redraw restricted UI
    tcga.annotations.security.redrawRestrictedUI(['editNoteButton' + note.noteId]);
}

function editNote(noteId) {
    // open edit window
    editNoteWindow.show(false,function(){
        Ext.getCmp('editNoteFormNoteIdField').setValue(noteId);
        var noteText = Ext.getCmp('note-' + noteId).el.dom.innerHTML;
        noteText = Ext.util.Format.htmlDecode(noteText.replace(/<br\/?>/g, '\n'));
        Ext.getCmp('editNoteFormNoteField').setValue(noteText);  
    },this);
}

function addNoteSuccess(form, action) {
    // add the note to the display
    var response = Ext.util.JSON.decode(action.response.responseText);
    if (response.success == 'false') {
        failureFunction(form, action);
    } else {
        newNoteForm.getForm().reset();
        newNoteWindow.hide();
        addNote(response.note, false);
        currentDetailPanel.doLayout();
        refreshSearchResults();
    }
}

function addNoteSubmitHandler() {
    if(newNoteForm.getForm().isValid()) {
        newNoteForm.getForm().submit({
            success:addNoteSuccess,
            failure: failureFunction
        });
    } else {
        if (Ext.getCmp('addNoteFormNoteField').getValue() == '') {
            Ext.Msg.alert('Error', 'Note text is required');
        } else {
            Ext.Msg.alert('Error', 'Max allowed note length is ' + maxLengthForNotes);
        }
    }
}


function editNoteSubmitHandler() {
    if(editNoteForm.getForm().isValid()) {
        editNoteForm.getForm().submit({
            success: editNoteSuccess,
            failure: failureFunction
        });
    } else {
        if (Ext.getCmp('editNoteFormNoteField').getValue() == '') {
            Ext.Msg.alert('Error', 'Note text is required');
        } else {
            Ext.Msg.alert('Error', 'Max allowed note length is ' + maxLengthForNotes);
        }
    }
}

function editNoteSuccess(form, action) {
    var response = Ext.util.JSON.decode(action.response.responseText);
    if (response.success == 'false') {
        failureFunction(form, action);
    } else {
        editNoteForm.getForm().reset();
        editNoteWindow.hide();

        var noteField = Ext.getCmp('note-' + response.note.noteId);
        var noteText = Ext.util.Format.nl2br(Ext.util.Format.htmlEncode(response.note.noteText));
        noteField.update(noteText);
        
        var noteMetaText = 'Edited by ' + response.note.editedBy + ', ' + response.note.dateEdited;
        Ext.getCmp('note-meta-' + response.note.noteId).update(noteMetaText);
        refreshSearchResults();
    }
}

function makeNoteForm(url, submitFunction, cancelFunction, type) {
    return {
        autoLoad:true,
        xtype:'form',
        url:url,
        method:'POST',
        frame: true,
        bodyStyle:'padding 5px 5px 0',
        width: 350,
        labelWidth:50,
        forceLayout: true,
        layout: 'fit',
        items:[
            {
                xtype: 'textarea',
                fieldLabel:'Note',
                name:'note',
                hiddenName:'note',
                allowBlank:false,
                validateOnBlur:false,
                width: 300,
                height:150,
                autoScroll: true,
                id: type + 'NoteFormNoteField',
                maxLength: maxLengthForNotes
            },
            {
                xtype:'hidden',
                name:'annotationId',
                id: type + 'NoteFormIdField'
            },
            {
                xtype: 'hidden',
                name: 'noteId',
                id: type + 'NoteFormNoteIdField'
            }
        ],
        buttons:[{
            text: 'Submit',
            formBind: true,
            handler: submitFunction
        }, {
            text: 'Cancel',
            handler: cancelFunction           
        }]
    };
}

//helper function to get correct label from true/false values of annotation.approved
function getAnnotationStatusText(annotation) {
    if (annotation.approved == 'true'){
       	return 'Approved';
	} else {
       	return 'Pending';
	}
}

//helper function to see if annotation is rescinded
function annotationIsRescinded(annotation) {
    if (annotation.rescinded == 'true'){
       	return true;
	} else {
       	return false;
	}
}

//helper function to see if annotation is approved
function annotationIsApproved(annotation) {
    if (annotation.approved == 'true'){
       	return true;
	} else {
       	return false;
	}
}

var helpWindow;
function showHelp(topic) {
    if (topic == null) {
        topic = 'main_page_help;';
    }
    var url = 'https://wiki.nci.nih.gov/x/FaIhAg';
    window.open(url);
}

//Make sure all drop down values have RegExp otherwise javascript won't work
Ext.form.VTypes['ValidPatientBarcodeVal'] = /^((TCGA)-([A-Z0-9]{2})-([A-Z0-9]{4}))?$/i ;
Ext.form.VTypes['ValidSampleBarcodeVal'] = /^((TCGA)-([A-Z0-9]{2})-([A-Z0-9]{4})-(\d{2}[A-Z]))?$/i ;
Ext.form.VTypes['ValidAnalyteBarcodeVal'] = /^((TCGA)-([A-Z0-9]{2})-([A-Z0-9]{4})-((\d{2})([A-Z]{1}))-((\d{2})([A-Z]{1})))?$/i ;
Ext.form.VTypes['ValidAliquotBarcodeVal'] = /^((TCGA)-([A-Z0-9]{2})-([A-Z0-9]{4})-((\d{2})([A-Z]{1}))-((\d{2})([A-Z]{1}))-([A-Z0-9]{4})-(\d{2}))?$/i ;
Ext.form.VTypes['ValidPortionBarcodeVal'] = /^((TCGA)-([A-Z0-9]{2})-([A-Z0-9]{4})-((\d{2})([A-Z]{1}))-(\d{2}))?$/i ;
Ext.form.VTypes['ValidSlideBarcodeVal'] = /^((TCGA)-([A-Z0-9]{2})-([A-Z0-9]{4})-((\d{2})([A-Z]{1}))-(\d{2})-([T|M|B]S[A-Z0-9]))?$/i ;
Ext.form.VTypes['ValidShippedPortionBarcodeVal'] = /^((TCGA)-([A-Z0-9]{2})-([A-Z0-9]{4})-((\d{2})([A-Z]{1}))-(\d{2})-([A-Z0-9]{4})-(\d{2}))?$/i;

//UUID check for future not used now
Ext.form.VTypes['ValidUUIDVal'] = /^([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})?$/i;

Ext.form.VTypes['validateBarcodeText'] = 'Invalid Barcode';

Ext.form.VTypes['validateBarcode']=function(value)
  {
      var combo = Ext.getCmp("newItemTypeCombo");
      // remove any spaces from item type name
      var itemType = combo.getRawValue().split(' ').join('');
      var type = "Valid" + itemType + "BarcodeVal";
      //first time invocation default to Patient otherwise you will get javascript error
      if(combo.getRawValue()=='')
      {
          type = "ValidPatientBarcodeVal";
      }

      return Ext.form.VTypes[type].test(value);
  };
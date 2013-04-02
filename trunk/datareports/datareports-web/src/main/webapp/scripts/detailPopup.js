/*******************************************************************************
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 ******************************************************************************/

/*
 * @function tcga.detailPopup.show
 * <p>The detail popup is a standard formatting of popups to be used when showing extra details about a screen object.</p>
 * @param (Object} config The config object
 * @cfg {String} title
 * <p>[Optional] The title that will be displayed in the popup.</p>
 * @cfg {String} message
 * <p>The message to be displayed in the body of the popup.</p>
 */

Ext.namespace('tcga.detailPopup');

tcga.detailPopup.show = function(data) {
    var formatTemplate = new Ext.XTemplate(
        '<tpl if={title}>',
            '<div class="stdSecondaryTitle" style="padding-bottom: 10px;">{title}</div>',
        '</tpl>',
        '<div>{message}</div>'
    );

    var formattedMsg = formatTemplate.applyTemplate(data);

    Ext.Msg.show({
        width: 500,
        msg: formattedMsg,
        buttons: Ext.Msg.OK,
        icon: Ext.Msg.INFO
    });
};
/*******************************************************************************
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 ******************************************************************************/

Ext.BLANK_IMAGE_URL = Ext.isIE6 || Ext.isIE7 || Ext.isAir ?
                    'images/default/s.gif' :
                    'data:image/gif;base64,R0lGODlhAQABAID/AMDAwAAAACH5BAEAAAAALAAAAAABAAEAAAICRAEAOw==';

//this corrects Ext bug of mask having the correct body width so that there is no scrollbar
//in pop up windows
Ext.lib.Dom.getViewportWidth = function() {
	var doc = document;
	return !Ext.isStrict && !Ext.isOpera || Ext.isGecko3 ? doc.body.clientWidth :
	Ext.isIE ? doc.documentElement.clientWidth : self.innerWidth;
}

//overriding initTemplates for browser friendly means of allowing "copy-and-paste" in grid panels
Ext.override(Ext.grid.GridView, {
	initTemplates : function(){
        var ts = this.templates || {};
        
        if(!ts.master){
            ts.master = new Ext.Template(
                '<div class="x-grid3" hidefocus="true">',
                    '<div class="x-grid3-viewport">',
                        '<div class="x-grid3-header"><div class="x-grid3-header-inner"><div class="x-grid3-header-offset" style="{ostyle}">{header}</div></div><div class="x-clear"></div></div>',
                        '<div class="x-grid3-scroller"><div class="x-grid3-body" style="{bstyle}">{body}</div><a href="#" class="x-grid3-focus" tabIndex="-1"></a></div>',
                    '</div>',
                    '<div class="x-grid3-resize-marker">&#160;</div>',
                    '<div class="x-grid3-resize-proxy">&#160;</div>',
                '</div>'
            );
        }

        if(!ts.header){
            ts.header = new Ext.Template(
                '<table border="0" cellspacing="0" cellpadding="0" style="{tstyle}">',
                '<thead><tr class="x-grid3-hd-row">{cells}</tr></thead>',
                '</table>'
            );
        }

        if(!ts.hcell){
            ts.hcell = new Ext.Template(
                '<td class="x-grid3-hd x-grid3-cell x-grid3-td-{id} {css}" style="{style}"><div {tooltip} {attr} class="x-grid3-hd-inner x-grid3-hd-{id}" unselectable="on" style="{istyle}">', this.grid.enableHdMenu ? '<a class="x-grid3-hd-btn" href="#"></a>' : '',
                '{value}<img class="x-grid3-sort-icon" src="', Ext.BLANK_IMAGE_URL, '" />',
                '</div></td>'
            );
        }

        if(!ts.body){
            ts.body = new Ext.Template('{rows}');
        }

        if(!ts.row){
            ts.row = new Ext.Template(
                '<div class="x-grid3-row {alt}" style="{tstyle}"><table class="x-grid3-row-table" border="0" cellspacing="0" cellpadding="0" style="{tstyle}">',
                '<tbody><tr>{cells}</tr>',
                (this.enableRowBody ? '<tr class="x-grid3-row-body-tr" style="{bodyStyle}"><td colspan="{cols}" class="x-grid3-body-cell" tabIndex="0" hidefocus="on"><div class="x-grid3-row-body">{body}</div></td></tr>' : ''),
                '</tbody></table></div>'
            );
        }

        if(!ts.cell){
            ts.cell = new Ext.Template(
                    '<td class="x-grid3-col x-grid3-cell x-grid3-td-{id} {css}" style="{style}" tabIndex="0" {cellAttr}>',
                    '<div class="x-grid3-cell-inner x-grid3-col-{id}" {attr}>{value}</div>',
                    '</td>'
                    );
        }

        for(var k in ts){
            var t = ts[k];
            if(t && Ext.isFunction(t.compile) && !t.compiled){
                t.disableFormats = true;
                t.compile();
            }
        }

        this.templates = ts;
        this.colRe = new RegExp('x-grid3-td-([^\\s]+)', '');
    }
});

// note: code from http://www.extjs.com/forum/showthread.php?t=11537

// Override the Label's afterRender to add a QuickTip if there is a "qtipText" property on the label
Ext.override(Ext.form.Label, {
    afterRender : function() {
        if(this.qtipText){
            Ext.QuickTips.register({
                target:  this.getEl(),
                title: this.qtipTitle,
                text: this.qtipText,
                enabled: true
            });
        }
        Ext.form.Label.superclass.afterRender.call(this);
    }
});

// Override the Field's afterRender to add a QuickTip if there is a "qtipText" property on the field
Ext.override(Ext.form.Field, {
  afterRender : function() {
      var findLabel = function(field) {
          var wrapDiv = null;
          var label = null;
          //find form-element and label?
          wrapDiv = field.getEl().up('div.x-form-element');
          if(wrapDiv) {
              label = wrapDiv.child('label');
          }
          if(label) {
              return label;
          }

          //find form-item and label
          wrapDiv = field.getEl().up('div.x-form-item');
          if(wrapDiv) {
              label = wrapDiv.child('label');
          }
          if(label) {
              return label;
          }
      };

      if(this.qtipText){
          Ext.QuickTips.register({
              target:  this.getEl(),
              title: this.qtipTitle,
              text: this.qtipText,
              enabled: true
          });
          var label = findLabel(this);
          if(label) {
              Ext.QuickTips.register({
                  target:  label,
                  title: this.qtipTitle,
                  text: this.qtipText,
                  enabled: true
              });
          }
      }
      Ext.form.Field.superclass.afterRender.call(this);
      this.initEvents();
      this.initValue();
  }
});

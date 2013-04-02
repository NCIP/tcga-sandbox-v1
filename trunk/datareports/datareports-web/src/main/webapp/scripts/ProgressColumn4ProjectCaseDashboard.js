/**
 * @class Ext.ux.grid.plugin.ProgressColumn
 * @extends Ext.util.Observable
 * 
 * @author Benjamin Runnels
 * @date 2 September 2010
 * @version 1.2
 * 
 * @license Ext.ux.grid.plugin.ProgressColumn is licensed under the terms of the Open
 *          Source LGPL 3.0 license. Commercial use is permitted to the extent
 *          that the code/component(s) do NOT become part of another Open Source
 *          or Commercially licensed development library or toolkit without
 *          explicit permission.
 * 
 * <p>
 * License details: <a href="http://www.gnu.org/licenses/lgpl.html"
 * target="_blank">http://www.gnu.org/licenses/lgpl.html</a>
 * </p>
 */

//This plugin contains my modification to handle special behavior for the ProjectCaseDashboard
Ext.namespace('Ext.ux.grid.plugin');

Ext.ux.grid.plugin.ProgressColumn = function(config){
  Ext.apply(this, config);
  this.renderer = this.renderer.createDelegate(this);
  Ext.ux.grid.plugin.ProgressColumn.superclass.constructor.call(this);
};

Ext.extend(Ext.ux.grid.plugin.ProgressColumn, Ext.util.Observable, {
  /**
   * @cfg {String} Text to display above the progress bar (defaults to null)
   */ 
  topText: null,
  
  /**
   * @cfg {String} Text to display below the progress bar (defaults to null)
   */
  bottomText: null,
  
  /**
   * @cfg {Integer} upper limit for full progress indicator (defaults to 100)
   */
  ceiling : 100,
  
  /**
   * @cfg {String} symbol appended after the numeric value (defaults to %)
   */
  textPst : '%',
  
  /**
   * @cfg {Boolean} colored determines whether use special progression coloring
   *      or the standard Ext.ProgressBar coloring for the bar (defaults to
   *      true)
   */
  colored : true,
  
  /**
   * @cfg {Boolean} inverts the colors when colored is used.  Normally the progression
   *      is red, orange, green.  This switches it to green, orange, red. (defaults to false)
   */
  invertedColor : false,
  
  /**
   * @cfg {String} actionEvent Event to trigger actions, e.g. click, dblclick,
   *      mouseover (defaults to 'click')
   */
  actionEvent : 'click',

  showPercent : false,  

  init : function(grid)
  {
    this.grid = grid;
    this.view = grid.getView();
    
    // the actions column must have an id for Ext 3.x
    this.id = this.id || Ext.id();

    // for Ext 3.x compatibility
    var lookup = grid.getColumnModel().lookup;
    delete(lookup[undefined]);
    lookup[this.id] = this;

    if(this.editor && grid.isEditor){
      var cfg = {
        scope : this
      };
      cfg[this.actionEvent] = this.onClick;
      grid.afterRender = grid.afterRender.createSequence(function() {
        this.view.mainBody.on(cfg);
        grid.on('destroy', this.purgeListeners, this);
      }, this);
    }
  },

  onClick : function(e, target)
  {
    var rowIndex = e.getTarget('.x-grid3-row').rowIndex;
    var colIndex = this.view.findCellIndex(target.parentNode.parentNode);

    var t = e.getTarget('.x-progress-text');
    if(t){
      this.grid.startEditing(rowIndex, colIndex);
    }
  },

  getStyle: function(v, p, record)
  {
    var style = '';
    if (this.colored == true) {
      if(this.invertedColor == true) {
        if (v > (this.ceiling * 0.66)) style = '-red';
        if (v < (this.ceiling * 0.67) && v > (this.ceiling * 0.33)) style = '-orange';
        if (v < (this.ceiling * 0.34)) style = '-green';
      } else {
        if (v > (this.ceiling * 0.66)) style = '-green';
        if (v < (this.ceiling * 0.67) && v > (this.ceiling * 0.33)) style = '-orange';
        if (v < (this.ceiling * 0.34)) style = '-red';
      }
    }
    return style;
  },
  
  getTopText: function(v, p, record) {
    if(this.topText) {
      return String.format('<div class="x-progress-toptext">{0}</div>', this.topText);
    }
    return '';
  },
  
  getBottomText: function(v, p, record) {
    if(this.bottomText) {
      return String.format('<div class="x-progress-bottomtext">{0}</div>', this.bottomText);
    }
    return '';
  }, 
  
  // ugly hack to get IE looking the same as FF
  getText: function(v, p, record) {
    var textClass = (v < (this.ceiling / 1.818)) ? 'x-progress-text-back' : 'x-progress-text-front';
    var tooltip;
    if (v=='N/A'){
        tooltip = 'ext:qtitle="Percentage" ext:qtip="'+v+'"';
        textClass = 'x-progress-text-back';
    } else {
        tooltip = 'ext:qtitle="Percentage" ext:qtip="'+v+this.textPst+'"';
    }
    if (this.showPercent) {
        tooltip = 'ext:qtitle="Ratio" ext:qtip="{2}"';
    }
    var text = String.format('</div><div class="x-progress-text {0}" '+tooltip+'>{1}</div></div>',
      textClass, this.format(v,record),record.get(this.dataIndex)
    );       
    return (v < (this.ceiling / 1.031)) ? text.substring(0, text.length - 6) : text.substr(6);    
  },

  format : function (value,record) {
      if (this.showPercent && value!='N/A'){
          return value + this.textPst;
      } else {
          return record.get(this.dataIndex); 
      }
  },

  calcFunc : function (record) {
        var tab = record.get(this.dataIndex).split("/",2);
        return Math.round((tab[0] * 100)/tab[1]);
  },
    
  renderer: function(v, p, record) {
    var disease = record.get('disease');
    if (disease=='LAML'){
        if (this.dataIndex == 'caseContractedBCR' ||
                this.dataIndex == 'receivedBCR' ||
                this.dataIndex == 'shippedBCR'){
             p.css += "bcr-disabled";
        }
    }
    if (v=='N/A'){
    	p.css += "bcr-disabled";
        p.css += ' x-grid3-progresscol';
        return String.format(
                '{0}<div class="x-progress-wrap' + (Ext.isIE ? ' x-progress-wrap-ie">' : '">') +
                        '<!-- --><div class="x-progress-inner">' +
                        '<div class="x-progress-bar x-progress-bar{1}" style="width:{2}%;">{3}' +
                        '</div>' +
                        '</div>{4}',
                this.getTopText(0, p, record),
                this.getStyle(0, p, record),
                0,
                this.getText(v, p, record),
                this.getBottomText(0, p, record)
        );
    }
    p.css += ' x-grid3-progresscol';
    v = this.calcFunc.call(this, record);
    if(!v) v = 0;
    
    // the empty comment makes IE collapse empty divs
    return String.format(
      '{0}<div class="x-progress-wrap' + (Ext.isIE ? ' x-progress-wrap-ie">' : '">') +
        '<!-- --><div class="x-progress-inner">' +
          '<div class="x-progress-bar x-progress-bar{1}" style="width:{2}%;">{3}' +
        '</div>' +
      '</div>{4}',
      this.getTopText(v, p, record),
      this.getStyle(v, p, record), 
      ((v > this.ceiling?this.ceiling:v) / this.ceiling) * 100,
      this.getText(v, p, record),
      this.getBottomText(v, p, record)
    );
  }
});

Ext.preg('progresscolumn', Ext.ux.grid.plugin.ProgressColumn);
/*******************************************************************************
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 ******************************************************************************/

Ext.namespace('tcga.extensions');

/**
 * @class tcga.extensions.selectorRange
 * @extends tcga.extensions.selectorParameter
 * <p></p>
 * @constructor
 * @param {Object} config The config object
 * 	closed
 * 	queryClass, closed, order, parameterDesc, interactive, store
 * @xtype selectorRange
 */
tcga.extensions.selectorRange = Ext.extend(tcga.extensions.selectorParameter, {
	selectorType: 'range',

	getValue: function() {
		return this.value;
	},
	
	setValue: function(value) {
		this.slider.setValue(value);
	},
	
	filter: function(rec, id) {
		var recVal = rec.get(this.parameterDesc.dataParam);

		return (recVal >= this.value.start && recVal <= this.value.end?true: false);
	},

	initComponent : function(){
		var currValue = tcga.db2.query.storage.getCurrQuery().patientClass.getValue(this.parameterDesc.name);

		if (currValue) {
			this.value = currValue;
		}
		else {
			this.value = {
				start: this.parameterDesc.range.minValue,
				end: this.parameterDesc.range.maxValue
			}
		}
		
		var synchSliderToTf = function(tf, thumbNdx, parameterDesc) {
			var parent = tf.findParentByType('selectorrange');
			var slider = parent.items.get(2);
			var tfValue = new Number(tf.getValue());
			
			
			if (thumbNdx == 0 && tfValue < parameterDesc.range.minValue) {
				tf.setValue(parameterDesc.range.minValue);
				tfValue = parameterDesc.range.minValue;
			}
			else if (thumbNdx == 1 && tfValue > parameterDesc.range.maxValue) {
				tf.setValue(parameterDesc.range.maxValue);
				tfValue = parameterDesc.range.maxValue;
			}
			slider.setValue(thumbNdx, tfValue);
			
			this.value = {
				start: slider.thumbs[0].value,
				end: slider.thumbs[1].value,
			};
		};
		
		var saveValuesToQuery = function(slider, newVal, parameterDesc) {
			tcga.db2.query.storage.getCurrQuery().updateQueryClass(slider.selector.queryClass, parameterDesc.name, {
				value: {
					start: slider.thumbs[0].value,
					end: slider.thumbs[1].value,
				},
				type: 'range',
				order: this.order
			});
	
			tcga.db2.queryWriter.start();
		};
		
		this.slider = {
			id: this.parameterDesc.name,
			xtype: 'sliderplus',
			selector: this,
			width: 250,
			values: [(currValue?currValue.start:this.parameterDesc.range.minValue), (currValue?currValue.end:this.parameterDesc.range.maxValue)],
			minValue: this.parameterDesc.range.minValue,
			maxValue: this.parameterDesc.range.maxValue,
			plugins: new Ext.slider.Tip(),
			listeners: {
				change: {
					fn: function(slider, newVal, thumb) {
						var parent = slider.findParentByType('selectorrange');
						// The textfields are items 3 and 4 in the panel after the title(0), the interactive value(1), and the slider(2)
						var tf = parent.items.get(thumb.index + 3);
						tf.setValue(newVal);
						
						this.selector.value = {
							start: slider.thumbs[0].value,
							end: slider.thumbs[1].value,
						};

						if (this.selector.interactive == true) {
							this.selector.interactiveUpdate();
						}
					},
					scope: this.selector
				},
				changecomplete: {
					fn: function(slider, newVal) {
						saveValuesToQuery(slider, newVal, this.selector.parameterDesc);
					},
					scope: this.selector
				}
			}
		};
		
		this.items = [
			this.slider,
		{
			xtype: 'textfield',
			cls: 'sliderValue',
			width: 50,
			hideLabel: true,
			enableKeyEvents: true,
			value: (currValue?currValue.start:this.parameterDesc.range.minValue),
			listeners: {
				keyup: {
					fn: function(tf) {
						synchSliderToTf(tf, 0, this.parameterDesc);
					},
					scope: this
				},
				keydown: {
					fn: function(tf) {
						synchSliderToTf(tf, 0, this.parameterDesc);
					},
					scope: this
				}
			}
		}, {
			xtype: 'textfield',
			cls: 'sliderValue',
			width: 50,
			hideLabel: true,
			enableKeyEvents: true,
			value: (currValue?currValue.end:this.parameterDesc.range.maxValue),
			listeners: {
				keyup: {
					fn: function(tf) {
						synchSliderToTf(tf, 1, this.parameterDesc);
					},
					scope: this
				},
				keydown: {
					fn: function(tf) {
						synchSliderToTf(tf, 1, this.parameterDesc);
					},
					scope: this
				}
			}
		}];

		tcga.extensions.selectorRange.superclass.initComponent.call(this);
	}
});

Ext.reg('selectorrange', tcga.extensions.selectorRange);

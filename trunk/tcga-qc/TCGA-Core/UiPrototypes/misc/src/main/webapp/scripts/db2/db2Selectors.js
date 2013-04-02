Ext.namespace('tcga.db2.selectors');

tcga.db2.selectors.loadQuery = function(queryNum) {
	// Set current query to query num
	tcga.db2.query.storage.setCurrQuery(queryNum);
	
	// Get the data for the query
	var currQuery = tcga.db2.query.storage.getQuery(queryNum);
	
	tcga.db2.updateDisease(currQuery);

	// Get the selector store(s)
	var patientClassParameterStore = Ext.StoreMgr.get('patientClassParameterStore');	

	for (var ndx0 = 0;ndx0 < patientClassParameterStore.getCount();ndx0++) {
		var parameterRecs = patientClassParameterStore.getAt(ndx0).get('parameters');
		
		for (var ndx1 = 0;ndx1 < parameterRecs.length;ndx1++) {
			// Load the new data into the selectors
			tcga.db2.selectors.set[parameterRecs[ndx1].selectType](parameterRecs[ndx1]);
		}
	}
}

tcga.db2.selectors.closeAll = function(queryClass) {
	var selectors = Ext.get(queryClass).query('.selectorBox');
	
	for (var ndx0 = 0;ndx0 < selectors.length;ndx0++) {
		var selectorBox = new Ext.Element(selectors[ndx0]);
		var selectorToggle = selectorBox.child('.openCloseSelectorToggle').dom;
		if (selectorToggle.innerHTML == 'close') {
			var selectorParameters = selectorBox.query('.selectorParameter');
			for (var ndx1 = 0;ndx1 < selectorParameters.length;ndx1++) {
				selectorParameters[ndx1].style.display = 'none';
			}
			
			selectorToggle.innerHTML = 'open';
		}
	}
}

tcga.db2.selectors.toggleSelectorBox = function(toggleButton, selectorBoxId) {
	toggleButton.innerHTML = toggleButton.innerHTML.toggle('open', 'close');
	
	var selectorBox = Ext.getCmp(selectorBoxId);
	
	selectorBox.items.each(function(item, ndx) {
		// Don't change the visibility of the title
		if (ndx == 0) {return;}
		
		if (!item.isVisible()) {
			item.show();
			// This is a hack.  It fires the show event on the subcomponents, especially the sliders
			//		which, otherwise, will not adjust their thumbs.
			for (var ndx = 1;ndx < item.items.getCount();ndx++) {
				item.items.get(ndx).show();
			}
		}
		else {
			item.hide();
		}
	});
}

// Singleton to hold the queryType and make it available globally
tcga.db2.selectors.queryType = function() {
	return {
		value: [],
		get: function() {
			return this.value;
		},
		set: function(value) {
			this.value = [value];
		},
		reset: function() {
			this.value = [];
		},
		add: function(value) {
			this.value.push(value);
		},
		find: function(value) {
			for (var ndx = 0;ndx < this.value.length;ndx++) {
				if (value == this.value[ndx]) {
					return true;
				}
			}
			
			return false;
		}
	}
}();

tcga.db2.selectors.displayPatientClassBase = function(parameterRec, closed, components) {
	return {
		xtype: 'selectorparameter',
		hidden: closed,
		items: [{
			width: 600,
			html: '<div class="selectorParameterTitle">' + parameterRec.name + ':</div>'
		}].concat(components)
	};
}

tcga.db2.selectors.getCheckBoxActiveCount = function(currQuery, queryClass, order, parameterRec) {
	var activeCount = 0;
	var activeNdx = -1;
	for (var ndx = 0;ndx < parameterRec.values.length;ndx++) {
		if (!(parameterRec.values[ndx].disabled && parameterRec.values[ndx].disabled == 'true')) {
			activeCount++;
			activeNdx = ndx;
		}
	}
	
	// If the active count is 1 and the type is single, then we just go ahead and put the value
	// 	into the state since that's the only choice that can be made anyway
	if (activeCount == 1) {
		currQuery.updateQueryClass(queryClass, parameterRec.name, {
			value: [parameterRec.values[activeNdx].value],
			type: 'checkbox',
			order: order
		});
	}
	
	return activeCount;
};

tcga.db2.selectors.checkForSavedValue = function(currValue, selectedValues) {
	// No selected values...get out of here
	if (!selectedValues || typeof(selectedValues) == "undefined") {
		return;
	}
	
	var checkedVal = false;
	if (typeof(selectedValues) == 'string' && currValue == selectedValues) {
		checkedVal = true;
	}
	else {
		for (var ndx = 0;ndx < selectedValues.length;ndx++) {
			if (currValue == selectedValues[ndx]) {
				var checkedVal = true;
			}
		}
	}
	
	return checkedVal;
};

tcga.db2.selectors.displayCheckbox = function(queryClass, closed, order, parameterRec, type) {
	var components = [];
	var param;
	
	var currQuery = tcga.db2.query.storage.getCurrQuery();

	var activeCount = tcga.db2.selectors.getCheckBoxActiveCount(currQuery, queryClass, order, parameterRec);

	var cbList = [];
	
	for (var ndx = 0;ndx < parameterRec.values.length;ndx++) {
		var checkedVal = false;
		if (activeCount == 1 && parameterRec.values[ndx].disabled && parameterRec.values[ndx].disabled == 'false') {
			currQuery.updateQueryClass(queryClass, parameterRec.name, {
				value: [parameterRec.values[ndx].value],
				type: 'checkbox',
				order: order
			});

			tcga.db2.queryWriter.start();
			checkedVal = true;
		}
		else if (tcga.db2.selectors.checkForSavedValue(parameterRec.values[ndx].value, currQuery.patientClass.getValue(parameterRec.name))) {
			checkedVal = true;
		}
		
		cbList.push({
			name: (type == 'multi'?parameterRec.values[ndx].value:parameterRec.name),
			width: 140,
			value: parameterRec.values[ndx].value,
			boxLabel: parameterRec.values[ndx].value + (parameterRec.values[ndx].comment?' (' + parameterRec.values[ndx].comment + ')':''),
			disabled: ((parameterRec.values[ndx].disabled && parameterRec.values[ndx].disabled == 'true')?true:false),
			checked: checkedVal
		});
	}

	components = {
		id: parameterRec.name,
		xtype: (type == 'multi'?'checkboxgroup':'radiogroup'),
		items: cbList,
		listeners: {
			change: function(cbGroup, checkedList) {
				var value = [];
				if (cbGroup.xtype == 'radiogroup') {
					value[0] = checkedList.value;
				}
				else {
					for (var ndx = 0;ndx < checkedList.length;ndx++) {
						if (checkedList[ndx].getValue()) {
							value.push(checkedList[ndx].value);
						}
					}
				}
				
				currQuery.updateQueryClass(queryClass, parameterRec.name, {
					value: [value],
					type: 'checkbox',
					order: order
				});

				tcga.db2.queryWriter.start();
			}
		}
	}
	
	return components;
}

tcga.db2.selectors.displayCombo = function(queryClass, closed, order, parameterRec){
	var currQuery = tcga.db2.query.storage.getCurrQuery();
	var currValue = currQuery.patientClass.getValue(parameterRec.name);
	
	return tcga.db2.selectors.displayPatientClassBase(parameterRec, closed, [{
		width: 600,
		items: [{
			id: parameterRec.name,
			xtype: 'combo',
			mode: 'local',
			store: new Ext.data.JsonStore({
				fields: [
					'desc',
					'value'
				],
				data: parameterRec.values
			}),
			triggerAction: 'all',
			displayField: 'desc',
			valueField : 'value',
			value: (currValue?currValue.value:undefined),
			emptyText:'Select a ' + parameterRec.name,
			width: 270,
			data: parameterRec.values,
			listeners: {
				select: function(combo, rec) {
					tcga.db2.query.storage.getCurrQuery().updateQueryClass(queryClass, parameterRec.name, {
						desc: rec.get('desc'),
						value: rec.get('value'),
						type: 'combo',
						order: order
					});

					tcga.db2.queryWriter.start();
				}
			}
		}]
	}]);
}

tcga.db2.selectors.displayMulti = function(queryClass, closed, order, parameterRec){
	return tcga.db2.selectors.displayPatientClassBase(parameterRec, closed, tcga.db2.selectors.displayCheckbox(queryClass, closed, order, parameterRec, 'multi'));
}

tcga.db2.selectors.displayRange = function(queryClass, closed, order, parameterRec){
	var currQuery = tcga.db2.query.storage.getCurrQuery();
	var currValue = currQuery.patientClass.getValue(parameterRec.name);

	var synchSliderToTf = function(tf, thumbNdx, parameterRec) {
		var parent = tf.findParentByType('selectorparameter');
		var slider = parent.items.get(1);
		var tfValue = new Number(tf.getValue());
		
		
		if (thumbNdx == 0 && tfValue < parameterRec.range.minValue) {
			tf.setValue(parameterRec.range.minValue);
			tfValue = parameterRec.range.minValue;
		}
		else if (thumbNdx == 1 && tfValue > parameterRec.range.maxValue) {
			tf.setValue(parameterRec.range.maxValue);
			tfValue = parameterRec.range.maxValue;
		}
		slider.setValue(thumbNdx, tfValue);
	};
	
	var saveValuesToQuery = function(slider, newVal, parameterRec) {
		tcga.db2.query.storage.getCurrQuery().updateQueryClass(queryClass, parameterRec.name, {
			value: {
				start: slider.thumbs[0].value,
				end: slider.thumbs[1].value,
			},
			type: 'slider',
			order: order
		});

		tcga.db2.queryWriter.start();
	};
	
	return tcga.db2.selectors.displayPatientClassBase(parameterRec, closed, [{
		id: parameterRec.name,
		xtype: 'sliderplus',
		width: 250,
		values: [(currValue?currValue.start:parameterRec.range.minValue), (currValue?currValue.end:parameterRec.range.maxValue)],
		minValue: parameterRec.range.minValue,
		maxValue: parameterRec.range.maxValue,
		plugins: new Ext.slider.Tip(),
		listeners: {
			change: function(slider, newVal, thumb) {
				var parent = slider.findParentByType('selectorparameter');
				// The textfields are items 2 and 3 in the panel after the title(0) and the slider(1)
				var tf = parent.items.get(thumb.index + 2);
				tf.setValue(newVal);
			},
			changecomplete: function(slider, newVal) {
				saveValuesToQuery(slider, newVal, parameterRec);
			}
		}
	}, {
		xtype: 'textfield',
		cls: 'sliderValue',
		width: 50,
		hideLabel: true,
		enableKeyEvents: true,
		value: (currValue?currValue.start:parameterRec.range.minValue),
		listeners: {
			keyup: function(tf) {
				synchSliderToTf(tf, 0, parameterRec);
			},
			keydown: function(tf) {
				synchSliderToTf(tf, 0, parameterRec);
			}
		}
	}, {
		xtype: 'textfield',
		cls: 'sliderValue',
		width: 50,
		hideLabel: true,
		enableKeyEvents: true,
		value: (currValue?currValue.end:parameterRec.range.maxValue),
		listeners: {
			keyup: function(tf) {
				synchSliderToTf(tf, 1, parameterRec);
			},
			keydown: function(tf) {
				synchSliderToTf(tf, 1, parameterRec);
			}
		}
	}]);
}

tcga.db2.selectors.displayLossGainRange = function(queryClass, closed, order, parameterRec){
	var currQuery = tcga.db2.query.storage.getCurrQuery();
	var currValue = currQuery.get(queryClass).getValue(parameterRec.name);

	var saveValuesToQuery = function(queryClass, order, cmp, newVal, parameterRec) {
		var currQuery = tcga.db2.query.storage.getCurrQuery();
		var value = currQuery.get(queryClass).getValue(parameterRec.name);
		if (!value) {
			value = {
				type: 'lossGainSlider',
				order: order
			};
		}

		if (cmp.xtype == 'sliderplus') {
			if (cmp.gltype == 'loss') {
				if (value.value) {
					value.value.lossStart = cmp.minValue;
					value.value.lossEnd = cmp.thumbs[0].value;
				}
				else {
					value.value = {
						lossStart: cmp.minValue,
						lossEnd: cmp.thumbs[0].value
					};
				}
			}
			else {
				if (value.value) {
					value.value.gainStart = cmp.thumbs[0].value;
					value.value.gainEnd = cmp.maxValue;
				}
				else {
					value.value = {
						gainStart: cmp.thumbs[0].value,
						gainEnd: cmp.maxValue
					};
				}
			}
		}
		else {
			// The combo was updated
			if (value.value) {
				value.value.percent = newVal;
			}
			else {
				value.value = {
					percent: newVal
				};
			}
		}
		
		currQuery.updateQueryClass(queryClass, parameterRec.name, value);

		tcga.db2.queryWriter.start();
	};
	
	return {
		xtype: 'selectorparameter',
		hidden: closed,
		items: [{
		width: 580,
		cls: 'lossGainSliderGauge',
		html: '<span class="negative">-8 -6 -4 -2 0</span><span class="positive"> 2 4 6 8</span>'
	}, {
		id: parameterRec.name + 'LossSlider',
		xtype: 'sliderplus',
		gltype: 'loss',
		width: 150,
		style: 'margin-right: 10px;',
		values: [(currValue?currValue.start:parameterRec.range.minLoss)],
		minValue: parameterRec.range.minLoss,
		maxValue: parameterRec.range.maxLoss,
		highlight: 'left',
		plugins: new Ext.slider.Tip(),
		listeners: {
			change: function(slider, newVal, thumb) {
				var parent = slider.findParentByType('selectorparameter');
				var sliderDisplay = Ext.get(slider.id + 'Display');
				if (sliderDisplay) {
					if (newVal != slider.minValue) {
						sliderDisplay.setStyle('display', '');
						Ext.get(slider.id + 'Low').update(new String(newVal));
					}
					else {
						sliderDisplay.setStyle('display', 'none');
					}
				}
			},
			changecomplete: function(slider, newVal) {
				saveValuesToQuery(queryClass, order, slider, newVal, parameterRec);
			}
		}
	}, {
		id: parameterRec.name + 'GainSlider',
		xtype: 'sliderplus',
		gltype: 'gain',
		width: 150,
		style: 'margin-right: 10px;',
		values: [(currValue?currValue.end:parameterRec.range.maxGain)],
		minValue: parameterRec.range.minGain,
		maxValue: parameterRec.range.maxGain,
		highlight: 'right',
		plugins: new Ext.slider.Tip(),
		listeners: {
			change: function(slider, newVal, thumb) {
				var parent = slider.findParentByType('selectorparameter');
				var sliderDisplay = Ext.get(slider.id + 'Display');
				if (sliderDisplay) {
					if (newVal != slider.maxValue) {
						sliderDisplay.setStyle('display', '');
						Ext.get(slider.id + 'Low').update(new String(newVal));
					}
					else {
						sliderDisplay.setStyle('display', 'none');
					}
				}
			},
			changecomplete: function(slider, newVal) {
				saveValuesToQuery(queryClass, order, slider, newVal, parameterRec);
			}
		}
	}, {
		width: 265,
		html: '<span id="' + parameterRec.name + 'LossSliderDisplay" style="display:none">Loss Threshold: <span id="' + parameterRec.name + 'LossSliderLow"></span> fold to ' + parameterRec.range.minLoss + '<span id="' + parameterRec.name + 'LossSliderHigh"></span> fold or less<br/></span>' +
				'<span id="' + parameterRec.name + 'GainSliderDisplay" style="display:none">Gain Threshold: <span id="' + parameterRec.name + 'GainSliderLow"></span> fold to ' + parameterRec.range.maxGain + '<span id="' + parameterRec.name + 'GainSliderHigh"></span> fold or more</span>'
	}, {
		width: 360,
		style: 'margin-top: 10px;',
		html: '% of Patients Matching ' + parameterRec.name + ' Criteria:'
	}, {
		width: 150,
		style: 'margin-top: 10px;',
		items: [{
			id: parameterRec.name + 'Combo',
			xtype: 'combo',
			mode: 'local',
			store: new Ext.data.JsonStore({
				fields: [
					'value'
				],
				data: parameterRec.values
			}),
			triggerAction: 'all',
			displayField: 'value',
			valueField : 'value',
			value: (currValue?currValue.value:undefined),
			emptyText:'%',
			width: 50,
			data: parameterRec.values,
			listeners: {
				select: function(combo, rec, ndx) {
					saveValuesToQuery(queryClass, order, combo, rec.get('value'), parameterRec);
				}
			}
		}]
	}]};
}

tcga.db2.selectors.displaySingle = function(queryClass, closed, order, parameterRec, type){
	return tcga.db2.selectors.displayPatientClassBase(parameterRec, closed, tcga.db2.selectors.displayCheckbox(queryClass, closed, order, parameterRec, 'single'));
}

tcga.db2.selectors.setCheckbox = function(parameterRec) {
	var currQuery = tcga.db2.query.storage.getCurrQuery();

	var cbSetArray = [];
	for (var ndx = 0;ndx < parameterRec.values.length;ndx++) {
		cbSetArray.push(tcga.db2.selectors.checkForSavedValue(parameterRec.values[ndx].value, currQuery.patientClass.getValue(parameterRec.name)));
	}
	
	var cbGrp = Ext.getCmp(parameterRec.name);
	cbGrp.suspendEvents(false);
	cbGrp.setValue(cbSetArray);
	cbGrp.resumeEvents();
}

tcga.db2.selectors.setCombo = function(parameterRec) {
	var currQuery = tcga.db2.query.storage.getCurrQuery();
	var currValue = currQuery.patientClass.getValue(parameterRec.name);

	var combo = Ext.getCmp(parameterRec.name);
	combo.suspendEvents(false);
	combo.setValue(currValue.value?currValue.value:undefined);
	combo.resumeEvents();
}

tcga.db2.selectors.setRange = function(parameterRec) {
	var currQuery = tcga.db2.query.storage.getCurrQuery();
	var currValue = currQuery.patientClass.getValue(parameterRec.name);
	
	var slider = Ext.getCmp(parameterRec.name);
	
	slider.suspendEvents(false);
	// Left thumb
	slider.setValue(0, (currValue?currValue.start:slider.minValue));
	// Right thumb
	slider.setValue(1, (currValue?currValue.end:slider.maxValue));
	slider.resumeEvents();
}

tcga.db2.selectors.setLossGainRange = function(parameterRec) {
	var currQuery = tcga.db2.query.storage.getCurrQuery();
	var currValue = currQuery.patientClass.getValue(parameterRec.name);
/*	
	var slider = Ext.getCmp(parameterRec.name);
	
	slider.suspendEvents(false);
	// Left thumb
	slider.setValue(0, (currValue?currValue.start:slider.minValue));
	// Right thumb
	slider.setValue(1, (currValue?currValue.end:slider.maxValue));
	slider.resumeEvents();
*/
}

tcga.db2.selectors.factories = {
	'combo': tcga.db2.selectors.displayCombo,
	'check': tcga.db2.selectors.displayMulti,
	'multi': tcga.db2.selectors.displayMulti,
	'range': tcga.db2.selectors.displayRange,
	'radio': tcga.db2.selectors.displaySingle,
	'single': tcga.db2.selectors.displaySingle,
	'lossGainRange': tcga.db2.selectors.displayLossGainRange
}

tcga.db2.selectors.set = {
	'combo': tcga.db2.selectors.setCombo,
	'multi': tcga.db2.selectors.setCheckbox,
	'range': tcga.db2.selectors.setRange,
	'single': tcga.db2.selectors.setCheckbox,
	'lossGainRange': tcga.db2.selectors.setLossGainRange
}

tcga.db2.selectors.destroy = function(queryClass) {
	var selectorPanel = Ext.getCmp(queryClass + 'Selectors');
	if (selectorPanel) {
		selectorPanel.destroy();
	}
}

tcga.db2.selectors.display = function(queryClass, selectorRecs) {
	var parameterOrder = 0;
	
	var selectorBoxes = [];
	for (var ndx0 = 0;ndx0 < selectorRecs.length;ndx0++) {
		var parameterRecs = selectorRecs[ndx0].get('parameters');
		var parameterDisplay = [];
		
		var selectorName = selectorRecs[ndx0].get('name');

		parameterDisplay.push({
			html: '<div class="selectorTitle">Select ' + selectorName + '</div><div id="selectorBox' + selectorName + '" class="openCloseSelectorToggle" onclick="tcga.db2.selectors.toggleSelectorBox(this, \'' + selectorName + '\')">open</div>'
		});

		for (var ndx1 = 0;ndx1 < parameterRecs.length;ndx1++) {
			parameterDisplay.push(tcga.db2.selectors.factories[parameterRecs[ndx1].selectType](queryClass, true, parameterOrder++, parameterRecs[ndx1]));
		}

		selectorBoxes.push({
			id: selectorName,
			xtype: 'selectorbox',
			items: parameterDisplay
		});
	}
	
	new Ext.Panel({
		id: queryClass + 'Selectors',
		renderTo: queryClass,
		border: false,
		items: selectorBoxes
	});
	
	// Return the first selector so we can open it
	return selectorRecs[0].get('name');

}

Ext.namespace('tcga.db2.selectors');

// This is a hack.  Needs to be a mod to currQuery.
tcga.db2.selectors.currSelectors = {};

tcga.db2.selectors.updateInteractiveStore = function(store, selectorName, selectorType, paramName, value) {
	// Store the current selectors
	tcga.db2.selectors.currSelectors[selectorName] = {
		selectorName: selectorName,
		selectorType: selectorType,
		paramName: paramName,
		value: 1
	};

	store.filterBy(function(rec, id) {
		var accept = false;
		var recVal = rec.get(paramName);
		
//		for (var ndx = 0;ndx < tcga.db2.selectors.currSelectors.length;ndx++) {
		for (currSelector in tcga.db2.selectors.currSelectors) {
			var selectorName = currSelector.selectorName;
			var selectorType = currSelector.selectorType;
			var paramName = currSelector.paramName;

			if (selectorType == 'combo' || selectorType == 'single') {
				if (recVal == value[0]) {
					accept = true;
				}
				else {
					return false;
				}
			}
			else if (selectorType == 'lossGainRange') {
				accept = false;
			}
			else if (selectorType == 'check' || selectorType == 'radio') {
				if (value.length == 0) {
					accept = true;
				}
	
				for (var ndx = 0;ndx < value.length;ndx++) {
					if (recVal == value[ndx]) {
						accept = true;
					}
				}
				return false;
			}
			else if (selectorType == 'range') {
				if (recVal >= value.min && recVal <= value.max) {
					accept = true;
				}
				else {
					return false;
				}
			}
		}
		
		return accept;
	});
	
	var count = store.getCount();
	Ext.get('patientResultsCount').update((count>0?count:'0'));
	
	store.clearFilter(true);
}

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

tcga.db2.selectors.displayMolecularSetBase = function(parameterRec, closed, components) {
	return {
		xtype: 'panel',
		layout: 'column',
		border: false,
		hidden: closed,
		cls: 'selectorParameter',
		defaults: {
			border: false
		},
		items: components
	};
}

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

tcga.db2.selectors.set = {
	'combo': tcga.db2.selectors.setCombo,
	'check': tcga.db2.selectors.setCheckbox,
	'range': tcga.db2.selectors.setRange,
	'radio': tcga.db2.selectors.setCheckbox,
	'lossGainRange': tcga.db2.selectors.setLossGainRange
}
/*
tcga.db2.selectors.contructors = {
	'combo': tcga.extensions.selectorCombo,
	'check': tcga.extensions.selectorCheck,
	'range': tcga.extensions.selectorRange,
	'radio': tcga.extensions.selectorRadio,
	'lossGainRange': tcga.extensions.selectorLossGainRange
}
*/
tcga.db2.selectors.destroy = function(queryClass) {
	var selectorPanel = Ext.getCmp(queryClass + 'Selectors');
	if (selectorPanel) {
		selectorPanel.destroy();
	}
}

tcga.db2.selectors.display = function(renderLoc, queryClass, selectorRecs, interactive, store) {
	// Start up the results display
	var resultsDisplay = new tcga.extensions.selectorResultsDisplay({
		id: 'resultsDisplay',
		renderTo: 'patientResults',
		store: store,
		label: 'Total Patients Selected',
		initValue: store.getCount()
	});

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
			parameterDisplay.push({
				xtype: 'selector' + parameterRecs[ndx1].selectType,
				queryClass: queryClass,
				closed: true,
				order: parameterOrder++,
				parameterDesc: parameterRecs[ndx1],
				interactive: interactive,
				interactiveLabel: 'Patients',
				resultsDisplay: resultsDisplay,
				store: store
			});
		}

		selectorBoxes.push({
			id: selectorName,
			xtype: 'selectorbox',
			items: parameterDisplay
		});
	}
	
	new Ext.Panel({
		id: queryClass + 'Selectors',
		renderTo: renderLoc,
		border: false,
		items: selectorBoxes
	});
	
	// Return the first selector so we can open it
	return selectorRecs[0].get('name');

}

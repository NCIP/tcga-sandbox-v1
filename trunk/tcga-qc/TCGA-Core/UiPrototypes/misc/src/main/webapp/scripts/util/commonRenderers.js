Ext.namespace("marcs.util.renderer");

marcs.util.renderer.nameId = function(mDid, row, col) {
	return mDid + '.' + row + '.' + col;
}

marcs.util.renderer.renderNameEditPossible = function(val, mD, rec, row, col) {
	if (!rec.get('edit')) {
		return rec.get('lastName') + ', ' + rec.get('firstName');
	}
	
	var lastNameId = marcs.util.renderer.nameId('lastName', row, col);
	var firstNameId = marcs.util.renderer.nameId('firstName', row, col);
	
	// Otherwise, we're editing, so put in a text box with the value
	return 'Last: <input id="' + lastNameId + '" name="' + lastNameId + '" type="text" ' +
			'size=' + (this.width/50 * 2) + ' value="' + rec.get('lastName') + '"' +
			'tabIndex="' + marcs.util.tabIndex.getNextTabIndex() + '">' +
			' First: <input id="' + firstNameId + '" name="' + firstNameId + '" type="text" ' +
			'size=' + (this.width/50 * 2) + ' value="' + rec.get('firstName') + '"' +
			'tabIndex="' + marcs.util.tabIndex.getNextTabIndex() + '">';
}

marcs.util.renderer.renderStringEditPossible = function(val, mD, rec, row, col) {
	if (!rec.get('edit')) {
		return val;
	}
	
	var nameId = marcs.util.renderer.nameId(mD.id, row, col);
	
	// Otherwise, we're editing, so put in a text box with the value
	return '<input id="' + nameId + '" name="' + nameId + '" type="text" ' +
			'size=' + (this.width/50 * 5) + ' value="' + val + '"' +
			'tabIndex="' + marcs.util.tabIndex.getNextTabIndex() + '">';
}

marcs.util.renderer.renderStateEditPossible = function(val, mD, rec, row, col) {
	if (!rec.get('edit')) {
		return val;
	}
	
	var nameId = marcs.util.renderer.nameId(mD.id, row, col);
	
	return marcs.util.formFields.stateList({
		nameId: nameId,
		val: val
	});
}

marcs.util.renderer.renderZipEditPossible = function(val, mD, rec, row, col) {
	if (!rec.get('edit')) {
		return val;
	}
	
	var nameId = marcs.util.renderer.nameId(mD.id, row, col);
	
	// Otherwise, we're editing, so put in a text box with the value
	return '<input id="' + nameId + '" name="' + nameId + '" type="text" ' +
			'size=4 value="' + val + '" tabIndex="' + marcs.util.tabIndex.getNextTabIndex() + '">';
}

marcs.util.renderer.renderPhoneEditPossible = function(val, mD, rec, row, col) {
	if (!rec.get('edit')) {
		return val;
	}
	
	var nameId = marcs.util.renderer.nameId(mD.id, row, col);
	
	// Otherwise, we're editing, so put in a text box with the value
	return '<input id="' + nameId + '" name="' + nameId + '" type="text" ' +
			'size=10 value="' + val + '" tabIndex="' + marcs.util.tabIndex.getNextTabIndex() + '">';
}

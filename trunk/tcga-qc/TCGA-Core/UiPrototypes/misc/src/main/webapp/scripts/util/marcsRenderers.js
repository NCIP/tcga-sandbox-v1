Ext.namespace("marcs.util.renderer");

marcs.util.renderer.renderConsigneeTypeEditPossible = function(val, mD, rec, row, col) {
	if (!rec.get('edit')) {
		return val;
	}
	
	var nameId = marcs.util.renderer.nameId(mD.id, row, col);

	// Otherwise, we're editing, so put in a text box with the value
	return '<input id="' + nameId + '" name="' + nameId + '" type="text" ' +
			'size=6 value="' + val + '" tabIndex="' + marcs.util.tabIndex.getNextTabIndex() + '">';
}


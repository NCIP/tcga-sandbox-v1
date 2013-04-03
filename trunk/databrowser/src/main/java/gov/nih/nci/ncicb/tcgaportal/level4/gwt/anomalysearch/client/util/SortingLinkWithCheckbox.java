package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.util;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;

/**
 * Created by IntelliJ IDEA.
 * User: nassaud
 * Date: May 26, 2009
 * Time: 11:13:52 AM
 * To change this template use File | Settings | File Templates.
 */

/**
 * Sorting link that also includes a checkbox
 */
public class SortingLinkWithCheckbox extends SortingLink {
    CheckBox cb;

//    public SortingLinkWithCheckbox(String text, long columnId, String annotationName, boolean initialAscending) {
//        this(text, columnId, annotationName, initialAscending, null);
//    }

    public SortingLinkWithCheckbox(String text, long columnId, String annotationName, boolean initialAscending, String tooltipText) {
        super(text, columnId, annotationName, initialAscending, tooltipText);
        cb = new CheckBox();
        insert(cb, 0);
    }

    public boolean isChecked() {
        return cb.isChecked();
    }

    public void setChecked(boolean b) {
        cb.setChecked(b);
    }

    public void activateCheckbox(ClickListener listener) {
        cb.addClickListener(listener);
    }
}

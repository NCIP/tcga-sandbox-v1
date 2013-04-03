/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.util;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.*;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.UpperAndLowerLimits;

/**
 * @author Silpa Nanan
 *         Last updated by: $Author: whitmore $
 * @version $Rev: 9303 $
 */
public class WidgetHelper {

    public static void removeAndAddWidgetToPanel(Panel panel, Widget widget) {
        checkAndRemoveWidgetFromPanel(panel, widget);
        panel.add(widget);
    }

    public static void checkAndRemoveWidgetFromPanel(Panel panel, Widget widget) {
        if (!panel.remove(widget)) {
            panel.remove(widget);
        }
    }

    public static HTML setHtmlTextEnable(HTML html, String styleName, boolean b) {
        html.removeStyleName(styleName);
        styleName = b ? StyleConstants.BLACK_TEXT : StyleConstants.GREY_TEXT;
        html.addStyleName(styleName);
        return html;
    }

    //todo - need to take these three setHtml... methods and make them one.
    public static HTML setHtmlGreyTextEnable(HTML html, String styleName, boolean b) {
        html.removeStyleName(styleName);
        styleName = b ? StyleConstants.DARK_GREY_TEXT : StyleConstants.GREY_TEXT;
        html.addStyleName(styleName);
        return html;
    }

    public static HTML setHtmlTextEnable(HTML html, boolean b) {
        String thisStyle = (b ? StyleConstants.BLACK_TEXT : StyleConstants.GREY_TEXT);
        String otherStyle = (b ? StyleConstants.GREY_TEXT : StyleConstants.BLACK_TEXT);
        html.removeStyleName(otherStyle);
        html.addStyleName(thisStyle);
        return html;
    }

    public static void setDomId(Widget w, String id) {
        if (w instanceof CheckBox) {
            Element e_cb = w.getElement();
            Element e_input = DOM.getChild(e_cb, 0);
            DOM.setElementAttribute(e_input, "id", id);
//        final Element e_label = DOM.getChild(e_cb, 1);
//        DOM.setElementAttribute(e_label, "for", id);
        } else {
            DOM.setElementProperty(w.getElement(), "id", id);
        }
    }

    public static ListBox getLessThanOperatorListBox() {
        ListBox operatorListBox = new ListBox();
        operatorListBox.addItem(UpperAndLowerLimits.Operator.None.toString());
        operatorListBox.addItem(UpperAndLowerLimits.Operator.LTE.toString());
        operatorListBox.addItem(UpperAndLowerLimits.Operator.LT.toString());
        operatorListBox.setSelectedIndex(1); // select LTE by default

        return operatorListBox;
    }

    public static ListBox getGreaterThanOperatorListBox() {
        ListBox operatorListBox = new ListBox();
        operatorListBox.addItem(UpperAndLowerLimits.Operator.None.toString());
        operatorListBox.addItem(UpperAndLowerLimits.Operator.GTE.toString());
        operatorListBox.addItem(UpperAndLowerLimits.Operator.GT.toString());
        operatorListBox.setSelectedIndex(1); // select GTE by default

        return operatorListBox;
    }

    public static TextBox getTextBoxWithValidator() {
        TextBox textBox = new TextBox();
        textBox.addKeyboardListener(new KeyboardListenerAdapter() {
            public void onKeyPress(Widget sender, char keyCode, int modifiers) {
                if ((!Character.isDigit(keyCode)) && (keyCode != (char) KEY_TAB)
                        && (keyCode != (char) KEY_BACKSPACE) && (keyCode != '-')
                        && (keyCode != (char) KEY_DELETE) && (keyCode != (char) KEY_ENTER)
                        && (keyCode != (char) KEY_HOME) && (keyCode != (char) KEY_END)
                        && (keyCode != (char) KEY_LEFT) && (keyCode != (char) KEY_UP)
                        && (keyCode != (char) KEY_RIGHT) && (keyCode != (char) KEY_DOWN)) {
                    // TextBox.cancelKey() suppresses the current keyboard event.
                    ((TextBox) sender).cancelKey();
                }
            }
        });
        textBox.addStyleName(StyleConstants.TEXTBOX_WIDTH);

        return textBox;
    }

}

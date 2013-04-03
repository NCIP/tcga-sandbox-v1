package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.util;

/*
Tooltip component for GWT
Copyright (C) 2006 Alexei Sokolov http://gwt.components.googlepages.com/

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public                                      
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA

*/

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.MouseListenerAdapter;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class TooltipListener extends MouseListenerAdapter {

    private static class Tooltip {
        boolean mouseOnThisTooltip = false;
        PopupPanel mainTooltip;
        PopupPanel shadow;
        boolean visible;

        public Tooltip(int offsetX, int offsetY, final Widget contents, final String styleName) {
            mainTooltip = new PopupPanel(true);
            shadow = new PopupPanel(true);

            if (contents instanceof HTML) {
                HTML htmlContents = (HTML) contents;
                htmlContents.addMouseListener(new MouseListenerAdapter() {
                    public void onMouseEnter(Widget sender) {
                        mouseOnThisTooltip = true;
                    }

                    public void onMouseLeave(Widget sender) {
                        mouseOnThisTooltip = false;
                        mainTooltip.hide();
                        shadow.hide();
                    }

                });
                mainTooltip.add(htmlContents);
                shadow.add(new HTML(htmlContents.getText()));
            } else {  //for other types of widgets
                mainTooltip.add(contents);
            }

            mainTooltip.setPopupPosition(offsetX, offsetY);
            shadow.setPopupPosition(offsetX + 2, offsetY + 2);
            mainTooltip.setStyleName(styleName);
            shadow.setStyleName("shadow");
        }

        public void show() {
            visible = true;
            mainTooltip.show();
            shadow.show();
        }

        public void hide() {
            mainTooltip.hide();
            shadow.hide();
            //shouldn't need to do this but seems to be a bug in PopupPanel - PopupPanel.visible is always true
            visible = false;
        }

        public boolean isVisible() {
            return visible;
        }

    }

    private static final String DEFAULT_TOOLTIP_STYLE = "tooltipHTML";
    private static final int DEFAULT_OFFSET_X = 15;
    private static final int DEFAULT_OFFSET_Y = 25;
    private static final int DELAYBEFORESHOW = 1000;

    private Tooltip tooltip;
    private Widget text;
    private String styleName;
    private boolean mouseIsOnLink;

    public TooltipListener(Widget text) {
        this(text, DEFAULT_TOOLTIP_STYLE);
    }

    public TooltipListener(Widget text, String styleName) {
        this.text = text;
        this.styleName = styleName;
    }

    public void onMouseEnter(final Widget sender) {
        //if instance already visible, do nothing
        if (tooltip != null && tooltip.isVisible()) {
            return;
        }

        mouseIsOnLink = true;
        int delay = (DELAYBEFORESHOW < 0 ? 0 : DELAYBEFORESHOW);
        Timer t = new Timer() {
            public void run() {
                if (mouseIsOnLink && (tooltip == null || !tooltip.isVisible())) {  //in case onMouseEnter is called twice, will only create tooltip once
                    tooltip = new Tooltip(sender.getAbsoluteLeft() + DEFAULT_OFFSET_X, sender.getAbsoluteTop() + DEFAULT_OFFSET_Y, text, styleName);
                    tooltip.show();
                }
            }
        };
        t.schedule(delay);
    }

    public void onMouseLeave(Widget sender) {
        mouseIsOnLink = false;
        Timer t = new Timer() {
            public void run() {
                if (tooltip != null && !tooltip.mouseOnThisTooltip) {
                    tooltip.hide();
                }
            }
        };
        t.schedule(250);
    }

    public void hideTooltip() {
        mouseIsOnLink = false;
        tooltip.hide();
    }
}
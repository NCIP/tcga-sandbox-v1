/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.filter;

import com.google.gwt.user.client.ui.*;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.util.StyleConstants;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.util.TooltipListener;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.FilterSpecifier;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.ColumnType;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract superclass of the different anomaly filter display types.
 * A "display" in the UI is the area containing one or more "widgets" describing
 * a column selection in the results.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public abstract class AnomalyDisplay {
    protected FilterPanel filterPanel;
    protected List<ColumnType> colTypeResults;
    protected String headerTextStyle = "blackText";
    protected HTML headingHtml = new HTML();
    protected VerticalPanel displayPanel = new VerticalPanel();
    protected HTML addAnomalyButton = new HTML("Add");
    protected ListBox anomalyListBox = new ListBox();
    protected TooltipListener tooltipListener;
    protected boolean anomalyTypeExists;
    protected VerticalPanel selectedAnomalyPanel = new VerticalPanel();
    protected HTML widgetTooltipHTML;

    protected List<AnomalyWidget> selectedWidgets = new ArrayList<AnomalyWidget>();

    public AnomalyDisplay(FilterPanel filterPanel) {
        this.filterPanel = filterPanel;
        addAnomalyButton.addStyleName(StyleConstants.ADD_BUTTON);
        addAnomalyButton.addStyleName(StyleConstants.MARGIN_LEFT_5PX);
        addButtonListener();
    }

    public Panel getDisplayPanel() {
        return displayPanel;
    }

    public void setColType(List<ColumnType> colTypes) {
        this.colTypeResults = colTypes;
    }

    public void setHeadingHtml(String heading) {
        headingHtml = new HTML(heading);
    }

    public List<AnomalyWidget> getSelectedWidgets() {
        return selectedWidgets;
    }

    public void clearTooltipText() {
        setTooltipText(null, null);
    }

    public void setTooltipText(String tooltipText, String widgetTooltipText) {
        HTML tooltipHTML = new HTML(tooltipText);
        HTML widgetTooltipHTML = new HTML(widgetTooltipText);
        if (tooltipListener != null) {
            headingHtml.removeMouseListener(tooltipListener);
        }
        if (filterPanel.areTooltipsEnabled() && tooltipHTML.getHTML() != null) {
            tooltipListener = new TooltipListener(tooltipHTML);
            headingHtml.addMouseListener(tooltipListener);
            headingHtml.addStyleName("action");
        } else {
            headingHtml.removeStyleName("action");
        }

        //set tooltips on all the sub-widgets
        this.widgetTooltipHTML = widgetTooltipHTML;
        for (AnomalyWidget widget : this.getSelectedWidgets()) {
            widget.setTooltipText(widgetTooltipHTML);
        }
    }

    private void addButtonListener() {
        addAnomalyButton.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                String selectedItem = anomalyListBox.getValue(anomalyListBox.getSelectedIndex());
                if (selectedItem != null && selectedItem.trim().length() > 0) {
                    filterPanel.enableSearchButton();
                    addAnomalyWidget(selectedItem, filterPanel.getListBy() == FilterSpecifier.ListBy.Genes);
                    anomalyListBox.setSelectedIndex(0);
                }
            }
        });
    }

    public void createAnomalyPanel() {
        HorizontalPanel addAnomalyPanel = new HorizontalPanel();
        headingHtml.setStylePrimaryName("header");

        anomalyTypeExists = false;
        for (ColumnType ctype : colTypeResults) {
            if (shouldInclude(ctype)) {
                anomalyListBox.addItem(ctype.getDisplayName(), Long.toString(ctype.getId()));
                anomalyTypeExists = true;
            }
        }
        if (!anomalyTypeExists) {
            anomalyListBox.addItem("- No Data Available -", "");
            anomalyListBox.setEnabled(false);
        } else {
            anomalyListBox.insertItem("- Select to Add -", "", 0);
            anomalyListBox.setSelectedIndex(0);
            anomalyListBox.addChangeListener(new ChangeListener() {
                public void onChange(Widget sender) {
                    String selectedItem = anomalyListBox.getValue(anomalyListBox.getSelectedIndex());
                    if (selectedItem != null && selectedItem.trim().length() > 0) {
                        filterPanel.enableSearchButton();
                        addAnomalyWidget(selectedItem, filterPanel.getListBy() == FilterSpecifier.ListBy.Genes);
                        anomalyListBox.setSelectedIndex(0);
                    }
                }
            });
            anomalyListBox.setEnabled(true);
        }

        anomalyListBox.setVisibleItemCount(1);
        addAnomalyPanel.add(anomalyListBox);
        //addAnomalyPanel.add(addAnomalyButton);

        displayPanel.add(headingHtml);
        displayPanel.add(addAnomalyPanel);
        displayPanel.add(selectedAnomalyPanel);

        //anomalyListBox.setWidth("350px");

    }

    protected abstract boolean shouldInclude(ColumnType ctype);

    public void clearPanel() {
        selectedAnomalyPanel.clear();
        displayPanel.clear();
        anomalyListBox.clear();
        selectedWidgets = new ArrayList<AnomalyWidget>();
    }

    protected void addAnomalyWidget(String selectedItem, boolean isListByGene) {
        ColumnType selectedColumnType = null;
        for (ColumnType colType : colTypeResults) {
            if (Long.toString(colType.getId()).equals(selectedItem)) {
                selectedColumnType = colType;
            }
        }
        if (selectedColumnType == null) {
            return;
        }
        // first, see if this widget has already been added
        AnomalyWidget existingWidget = null;
        for (AnomalyWidget widget : selectedWidgets) {
            if (widget.getColType().getId() == selectedColumnType.getId()) {
                existingWidget = widget;
            }
        }
        // now make a new one if needed
        if (existingWidget == null) {
            AnomalyWidget newWidget = makeNewWidget(selectedColumnType, isListByGene);
            newWidget.addStyleName("anomalyWidget");
            newWidget.setPicked(true);
            selectedAnomalyPanel.add(newWidget);
            selectedWidgets.add(newWidget);
            if (widgetTooltipHTML != null) {
                newWidget.setTooltipText(widgetTooltipHTML);
            }
        } else {
            // or make sure existing one is visible and checked
            existingWidget.setVisible(true);
            existingWidget.setPicked(true);
            if (widgetTooltipHTML != null) {
                existingWidget.setTooltipText(widgetTooltipHTML);
            }
        }
    }

    protected abstract AnomalyWidget makeNewWidget(ColumnType selectedColumnType, boolean isListByGene);

    void copyFrom(AnomalyDisplay toCopy) {
        selectedWidgets.clear();
        selectedAnomalyPanel.clear();

        // for each selected widget, clone and add
        for (AnomalyWidget widget : toCopy.selectedWidgets) {
            AnomalyWidget widgetCopy = widget.cloneWidget();
            widgetCopy.addStyleName("anomalyWidget");
            widgetCopy.setPicked(widget.isPicked());
            if (widgetCopy.isPicked()) {
                selectedAnomalyPanel.add(widgetCopy);
                selectedWidgets.add(widgetCopy);
            }
        }
    }
}

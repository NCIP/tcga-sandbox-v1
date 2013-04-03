/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.filter;

import com.google.gwt.user.client.ui.*;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.util.CloseablePanel;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.util.StyleConstants;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.util.TooltipListener;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.util.WidgetHelper;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.ColumnType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.UpperAndLowerLimits;

/**
 * Creates a visual rectangle with controls used for specifying a results column.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public abstract class AnomalyWidget extends Composite {
    protected ColumnType cType;
    protected CloseablePanel mainPanel;
    protected VerticalPanel widgetPanel = new VerticalPanel();

    // stuff for frequency threshold
    protected TextBox frequencyTextBox;
    protected HTML frequencyHTML;
    protected HTML frequencyPercentHtml;
    protected HorizontalPanel frequencyPanel;

    // stuff for range
    protected ListBox lessThanOperatorListBox = new ListBox();
    protected TextBox lessThanOperatorTextBox = new TextBox();
    protected ListBox greaterThanOperatorListBox = new ListBox();
    protected TextBox greaterThanOperatorTextBox = new TextBox();
    protected HorizontalPanel operatorPanel = new HorizontalPanel();

    //todo  don't hardcode this - configure it specific to the type of anomaly
    protected final String DEFAULT_LOWER_LIMIT = "-0.5";
    protected final String DEFAULT_UPPER_LIMIT = "0.5";

    protected boolean selected = true;
    protected TooltipListener tooltipListener;

    AnomalyWidget(ColumnType cType) {
        this.cType = cType;

        mainPanel = new CloseablePanel(cType.getDisplayName(), "anomalyWidgetLabel", true,
                new CloseablePanel.PanelRemovedCallback() {
                    public void panelRemoved() {
                        selected = false;
                    }
                });

        if (hasRangeWidget()) {
            createRangePanel();
        }

        if (hasFrequencyWidget()) {
            createFrequencyPanel();
        }

        mainPanel.setContent(widgetPanel);

        // this tells the composite that the mainPanel is the widget
        initWidget(mainPanel);
        addStyleName(StyleConstants.MARGIN_TOP_5PX);
    }

    public void setTooltipText(HTML tooltipText) {
        if (tooltipListener != null) {
            mainPanel.getHeaderLabel().removeMouseListener(tooltipListener);
        }
        if (tooltipText != null && tooltipText.getHTML() != null && tooltipText.getHTML().length() > 0) {
            tooltipListener = new TooltipListener(tooltipText);
            mainPanel.getHeaderLabel().addMouseListener(tooltipListener);
            mainPanel.getHeaderLabel().addStyleName("action");
        }
    }

    protected abstract double getDefaultFrequency();

    protected abstract boolean hasFrequencyWidget();

    protected abstract boolean hasRangeWidget();

    protected abstract String getRangeText();

    protected abstract String getRangeUnits();

    /**
     * Default behavior parses the value into a decimal from a percent (e.g. 45 turns into 0.45)
     *
     * @return thre threshold value, or 0 if none
     */
    public float getFrequency() throws FilterPanel.FilterPanelException {
        float freq = 0;
        if (frequencyTextBox != null && frequencyTextBox.isEnabled()) {
            String s = frequencyTextBox.getText();
            if (s != null) {
                s = s.trim();
                if (s.length() > 0) {
                    try {
                        float percent = Float.parseFloat(s);
                        freq = percent / 100f;
                    } catch (NumberFormatException ne) {
                        throw new FilterPanel.FilterPanelException("Non-numeric value entered for frequency");
                    }
                }
            }
        }
        return freq;
    }

    //for unit testing
    void setFrequency(String freq) {
        if (frequencyTextBox != null) {
            frequencyTextBox.setText(freq);
        }
    }

    public boolean isPicked() {
        return selected;
    }

    void setPicked(boolean picked) {
        selected = picked;
    }

    public ColumnType getColType() {
        return cType;
    }

    public UpperAndLowerLimits.Operator getLowerOperator() {
        return getOperator(lessThanOperatorListBox.getValue(lessThanOperatorListBox.getSelectedIndex()), lessThanOperatorTextBox.getText());
    }

    public Double getLowerLimit() throws FilterPanel.FilterPanelException {
        Double ret = 0.;
        try {
            String s = lessThanOperatorTextBox.getText();
            if (s != null) {
                s = s.trim();
                if (s.length() > 0) {
                    ret = Double.parseDouble(s);
                }
            }
        } catch (NumberFormatException e) {
            throw new FilterPanel.FilterPanelException("Non-numeric value entered for lower limit");
        }
        return ret;
    }

    public void setLowerLimit(String lessThanTbText) {
        this.lessThanOperatorTextBox.setText(lessThanTbText);
    }

    public UpperAndLowerLimits.Operator getUpperOperator() {
        return getOperator(greaterThanOperatorListBox.getValue(greaterThanOperatorListBox.getSelectedIndex()), greaterThanOperatorTextBox.getText());
    }

    public Double getUpperLimit() throws FilterPanel.FilterPanelException {
        Double ret = 0.;
        try {
            String s = greaterThanOperatorTextBox.getText();
            if (s != null) {
                s = s.trim();
                if (s.length() > 0) {
                    ret = Double.parseDouble(s);
                }
            }
        } catch (NumberFormatException e) {
            throw new FilterPanel.FilterPanelException("Non-numeric value entered for upper limit");
        }
        return ret;
    }

    public void setUpperLimit(String lessThanTbText) {
        this.greaterThanOperatorTextBox.setText(lessThanTbText);
    }

    private UpperAndLowerLimits.Operator getOperator(String selectedOperatorText, String value) {
        if (value == null || value.trim().length() == 0) {
            return UpperAndLowerLimits.Operator.None;
        } else {
            return UpperAndLowerLimits.Operator.fromString(selectedOperatorText);
        }
    }

    protected void createRangePanel() {
        lessThanOperatorListBox = WidgetHelper.getLessThanOperatorListBox();
        lessThanOperatorTextBox = WidgetHelper.getTextBoxWithValidator();
        lessThanOperatorTextBox.setText(DEFAULT_LOWER_LIMIT);
        greaterThanOperatorListBox = WidgetHelper.getGreaterThanOperatorListBox();
        greaterThanOperatorTextBox = WidgetHelper.getTextBoxWithValidator();
        greaterThanOperatorTextBox.setText(DEFAULT_UPPER_LIMIT);
        operatorPanel.addStyleName(StyleConstants.MARGIN_LEFT_10PX);

        operatorPanel.add(lessThanOperatorListBox);
        operatorPanel.add(lessThanOperatorTextBox);
        operatorPanel.add(new HTML("or"));
        operatorPanel.add(greaterThanOperatorListBox);
        operatorPanel.add(greaterThanOperatorTextBox);
        operatorPanel.setSpacing(5);
        widgetPanel.add(operatorPanel);

        addRangeClickListeners();
        addChangeListeners();
    }

    protected void addChangeListeners() {
        lessThanOperatorTextBox.addChangeListener(new ChangeListener() {
            public void onChange(Widget sender) {
                // if value is blank, change lessThanOperator to None
                if (lessThanOperatorTextBox.getText().trim().equals("")) {
                    lessThanOperatorListBox.setSelectedIndex(0);
                } else if (lessThanOperatorListBox.getSelectedIndex() == 0) {
                    lessThanOperatorListBox.setSelectedIndex(1);
                }
            }
        });

        greaterThanOperatorTextBox.addChangeListener(new ChangeListener() {
            public void onChange(Widget sender) {
                if (greaterThanOperatorTextBox.getText().trim().equals("")) {
                    greaterThanOperatorListBox.setSelectedIndex(0);
                } else if (greaterThanOperatorListBox.getSelectedIndex() == 0) {
                    greaterThanOperatorListBox.setSelectedIndex(1);
                }
            }
        });
    }

    protected void addRangeClickListeners() {
        lessThanOperatorListBox.addChangeListener(new ChangeListener() {
            public void onChange(Widget sender) {
                if (sender instanceof ListBox) {
                    if (((ListBox) sender).getValue(((ListBox) sender).getSelectedIndex()).equals("")) {
                        // clear the text box and disable it
                        lessThanOperatorTextBox.setText("");
                        lessThanOperatorTextBox.setEnabled(false);
                    } else {
                        lessThanOperatorTextBox.setEnabled(true);
                        lessThanOperatorTextBox.setText(DEFAULT_LOWER_LIMIT);
                    }
                }
            }
        });

        greaterThanOperatorListBox.addChangeListener(new ChangeListener() {
            public void onChange(Widget sender) {
                if (sender instanceof ListBox) {
                    if (((ListBox) sender).getValue(((ListBox) sender).getSelectedIndex()).equals("")) {
                        // clear the text box and disable it
                        greaterThanOperatorTextBox.setText("");
                        greaterThanOperatorTextBox.setEnabled(false);
                    } else {
                        greaterThanOperatorTextBox.setEnabled(true);
                        greaterThanOperatorTextBox.setText(DEFAULT_UPPER_LIMIT);
                    }
                }
            }
        });


    }


    protected void createFrequencyPanel() {
        frequencyPanel = new HorizontalPanel();
        frequencyHTML = new HTML(getRangeText());
        frequencyPercentHtml = new HTML(getRangeUnits());
        frequencyTextBox = WidgetHelper.getTextBoxWithValidator();
        frequencyTextBox.setText(Double.toString(getDefaultFrequency()));
        frequencyTextBox.setStyleName(StyleConstants.TEXTBOX_WIDTH);
        frequencyTextBox.addStyleName(StyleConstants.MARGIN_LEFT_5PX);

        frequencyPanel.add(frequencyHTML);
        frequencyPanel.add(frequencyTextBox);
        frequencyPanel.add(frequencyPercentHtml);
        frequencyPanel.addStyleName(StyleConstants.MARGIN_LEFT_15PX);
        frequencyPanel.setCellVerticalAlignment(frequencyHTML, HasVerticalAlignment.ALIGN_MIDDLE);

        widgetPanel.add(frequencyPanel);
    }

    public AnomalyWidget cloneWidget() {
        AnomalyWidget widget = instanceForClone();
        widget.copyFrom(this);
        return widget;
    }


    protected abstract AnomalyWidget instanceForClone();

    protected void copyFrom(AnomalyWidget toCopy) {
        if (toCopy.hasRangeWidget()) {
            lessThanOperatorListBox.setSelectedIndex(toCopy.lessThanOperatorListBox.getSelectedIndex());
            lessThanOperatorTextBox.setText(toCopy.lessThanOperatorTextBox.getText());
            greaterThanOperatorListBox.setSelectedIndex(toCopy.greaterThanOperatorListBox.getSelectedIndex());
            greaterThanOperatorTextBox.setText(toCopy.greaterThanOperatorTextBox.getText());
        }
        if (toCopy.hasFrequencyWidget()) {
            frequencyTextBox.setText(toCopy.frequencyTextBox.getText());
        }
    }

}

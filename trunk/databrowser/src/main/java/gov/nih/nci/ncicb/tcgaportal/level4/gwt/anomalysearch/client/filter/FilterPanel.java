/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.filter;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.AnomalySearch;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.AnomalySearchFactory;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.AnomalySearchServiceAsync;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.results.ModeController;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.results.ModeControllerGene;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.results.ModeControllerPathway;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.results.ModeControllerPatient;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.util.*;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.*;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.*;

import java.util.*;

/**
 * Panel that allows the user to choose mode/filter/threshold settings that will be used to retrieve results.
 *
 * @author David Nassau
 *         Last updated by: $Author: kigonyapa $
 * @version $Rev: 15821 $
 */
public class FilterPanel extends SimplePanel implements FilterPanelI {
    private boolean isCollapsed = false;

    //we need a class to use in CloseablePanel which knows how to obtain a "collapsed" representation
    //of its contents. The collapsed version is based on executed search, not current filter criteria
    class DockPanelWithCollapsedRepresentation extends DockPanel implements HasCollapsedRepresentation {
        FilterPanel fp;
        Widget collapsed;

        public DockPanelWithCollapsedRepresentation(FilterPanel fp) {
            this.fp = fp;
        }

        //what to display as the collapsed - only updated upon a new search
        public void setCollapsedRepresentation(Widget collapsed) {
            this.collapsed = collapsed;
        }

        public Widget getCollapsedRepresentation() {
            return collapsed;
        }

        public String getExpansionLinkText() {
            return "Modify Search Criteria";
        }

        public void expand() {
            fp.expand();
        }

        public void collapse() {
            fp.collapse();
        }
    }

    public static class FilterPanelException extends Exception {
        public FilterPanelException(String msg) {
            super(msg);
        }
    }

    public static final String IMAGES_PATH = "AnomalySearch/images/";
    static final int DEFAULT_PATIENTTHRESHOLD = 5;

    CloseablePanel closeablePanel;

    private HTML filterError = new HTML();
    private Map<String, TooltipListener> tooltips = new HashMap<String, TooltipListener>();
    DockPanelWithCollapsedRepresentation filterPanel = new DockPanelWithCollapsedRepresentation(this);

    //diseaseTypePanel components
    private VerticalPanel diseasePanel = new VerticalPanel();
    private HTML diseaseTypeHtml = new HTML(AnomalySearch.dataBrowserConstants.diseaseType());
    private ListBox diseaseTypeLb = new ListBox();
    private String diseaseHeaderTextColorStyle = StyleConstants.BLACK_TEXT;
    private CheckBox keepInSyncCB = new CheckBox(AnomalySearch.dataBrowserConstants.keepInSynchRB());

    //gene limit components
    private VerticalPanel geneLimitPanel = new VerticalPanel();
    private HTML genesHtml = new HTML(AnomalySearch.dataBrowserConstants.genes());
    private RadioButton allGenesRb = new RadioButton("geneLimit", AnomalySearch.dataBrowserConstants.allGenesRB());
    private RadioButton geneListRb = new RadioButton("geneLimit", AnomalySearch.dataBrowserConstants.geneListRB());

    private VerticalPanel geneListPanel = new VerticalPanel();
    private TextArea geneListTa = new TextArea();
    private Button geneListClearButton = new Button("Clear");

    private VerticalPanel geneRegionVPanel = new VerticalPanel();
    private HorizontalPanel geneRegionHPanel = new HorizontalPanel();
    private RadioButton geneRegionRb = new RadioButton("geneLimit", AnomalySearch.dataBrowserConstants.chromosomeRegionRB());
    private HTML geneRegionAddButton = new HTML("Add");

    private String geneHeaderTextColorStyle = StyleConstants.BLACK_TEXT;

    //patients limit components
    private VerticalPanel patientLimitPanel = new VerticalPanel();
    private HTML patientsHtml = new HTML(AnomalySearch.dataBrowserConstants.patients());
    private RadioButton allPatientsRb = new RadioButton("patientLimit", AnomalySearch.dataBrowserConstants.allPatientsRB());
    private RadioButton patientListRb = new RadioButton("patientLimit", AnomalySearch.dataBrowserConstants.patientListRB());
    private TextArea patientListTa = new TextArea();
    private Button patientListClearButton = new Button("Clear");
    private String patientHeaderTextColorStyle = StyleConstants.BLACK_TEXT;

    //anomaly components
    protected CopyNumberDisplay geneCopyNumberDisplay = new CopyNumberDisplay(this, AnomalyType.GeneticElementType.Gene);
    protected CopyNumberDisplay mirnaCopyNumberDisplay = new CopyNumberDisplay(this, AnomalyType.GeneticElementType.miRNA);
    protected GeneExpressionDisplay geneExpressionDisplay = new GeneExpressionDisplay(this);
    protected miRNAExpressionDisplay mirnaExpressionDisplay = new miRNAExpressionDisplay(this);
    protected MethylationDisplay methylationDisplay = new MethylationDisplay(this);
    private MutationDisplay mutationDisplay = new MutationDisplay(this);
    private CorrelationDisplay correlationDisplay = new CorrelationDisplay(this);

    //searchPanel components
    private HorizontalPanel searchPanel = new HorizontalPanel();
    private Button searchButton = new Button("Search");
    private Button resetButton = new Button("Reset");
    private Image loadingImage = new Image(IMAGES_PATH + "loader-transparent.gif");

    private AnomalySearchFactory asFactory;

    // the mode controller for this filter
    private ModeController modeController;

    AnomalySearchServiceAsync searchService;
    List<ColumnType> colTypeResults;
    private Map<String, List<ColumnType>> columnTypes = new HashMap<String, List<ColumnType>>();

    public boolean areTooltipsEnabled() {
        return asFactory.areTooltipsEnabled();
    }

    public FilterPanel() {
    }

    public void init() {
        addStyles();
        setUpClickListeners();
        addCloseablePanel();
    }

    private void addCloseablePanel() {
        closeablePanel = new CloseablePanel("Search Criteria", modeController.getModeColorSchemeClass());
        closeablePanel.setContent(filterPanel);
        closeablePanel.setWidth("100%");
        this.setWidth("100%");
        this.add(closeablePanel);
    }

    public void setAnomalySearchFactory(AnomalySearchFactory asFactory) {
        this.asFactory = asFactory;
    }

    public void setAnomalySearchService(AnomalySearchServiceAsync searchService, final String selectedDisease) {
        this.searchService = searchService;
        getDiseaseTypes(selectedDisease);
        getTooltipTextFromServer();
    }

    private void getDiseaseTypes(final String selectedDisease) {
        AsyncCallback<List<Disease>> callback = new AsyncCallback<List<Disease>>() {
            public void onFailure(Throwable caught) {
                showError(caught.getMessage(), true);
            }

            public void onSuccess(List<Disease> result) {
                String selectedDiseaseName = addDiseaseTypes(result, selectedDisease);
                populateColTypes(selectedDiseaseName, new InternalProcessCallback() {
                    public void done() {
                        asFactory.filterPanelCallback();
                    }
                });
            }
        };
        searchService.getDiseases(callback);
    }

    interface InternalProcessCallback {
        public void done();
    }

    private void populateColTypes(final String diseaseType, final InternalProcessCallback callback) {
        if (columnTypes.get(diseaseType) != null) {
            colTypeResults = columnTypes.get(diseaseType);
            clearWidgets();
            createWidgets(colTypeResults);
            callback.done();

        } else {

            AsyncCallback<List<ColumnType>> asyncCallback = new AsyncCallback<List<ColumnType>>() {
                public void onFailure(Throwable caught) {
                    showError(caught.getMessage(), true);
                }

                public void onSuccess(List<ColumnType> result) {
                    colTypeResults = result;
                    columnTypes.put(diseaseType, colTypeResults);
                    clearWidgets();
                    createWidgets(colTypeResults);
                    if (callback != null) {
                        callback.done();
                    }
                }
            };
            searchService.getColumnTypes(diseaseType, asyncCallback);
        }
    }

    private void getTooltipTextFromServer() {
        AsyncCallback<TooltipTextMap> callback = new AsyncCallback<TooltipTextMap>() {
            public void onFailure(Throwable caught) {
                showError(caught.getMessage(), true);
            }

            public void onSuccess(TooltipTextMap tooltipTextMap) {
                //set static accessor
                TooltipTextMap.setInstance(tooltipTextMap);
                constructFilterPanel();
            }
        };
        searchService.getTooltipText(callback);
    }

    public void showError(String message, boolean errorPrefix) {
        message = handleWebServerError(message);
        StringBuilder sb = new StringBuilder();
        if (errorPrefix) {
            sb.append("Error: ");
        }
        sb.append(message);
        filterError.setHTML(sb.toString());
        filterError.setStyleName("error");
        filterError.removeFromParent();
        if (isCollapsed) {
            ((CellPanel) filterPanel.collapsed).add(filterError);
        } else {
            searchPanel.add(filterError);
        }
    }

    //occasionally the web server itself emits an error page, e.g. if apache cannot
    //connect to the application server. Substitute a regular text error message in
    //its place
    private String handleWebServerError(String message) {
        if (message != null && message.contains("<title>")) {
            message = "A problem occurred in the web server.";
        }
        return message;
    }

    private void constructFilterPanel() {
        constructDiseasePanel();
        constructGeneLimitPanel();
        constructPatientLimitPanel();
        constructSearchPanel();

        keepInSyncCB.setChecked(true);
        filterPanel.add(keepInSyncCB, DockPanel.NORTH);

        HorizontalPanel widgetPanel = new HorizontalPanel();
        VerticalPanel leftPanel = new VerticalPanel();
        leftPanel.addStyleName("marginRight10px");
        VerticalPanel middlePanel = new VerticalPanel();
        middlePanel.addStyleName("marginLeft10px");
        VerticalPanel rightPanel = new VerticalPanel();
        rightPanel.addStyleName("marginLeft10px");

        leftPanel.add(diseasePanel);
        leftPanel.add(geneLimitPanel);
        leftPanel.add(patientLimitPanel);

        if (modeController.allowsAnomalyType(geneCopyNumberDisplay)) {
            middlePanel.add(geneCopyNumberDisplay.getDisplayPanel());
            middlePanel.add(new HTML("<br><br>"));
        }
        if (modeController.allowsAnomalyType(mirnaCopyNumberDisplay)) {
            middlePanel.add(mirnaCopyNumberDisplay.getDisplayPanel());
            middlePanel.add(new HTML("<br><br>"));
        }
        if (modeController.allowsAnomalyType(geneExpressionDisplay)) {
            middlePanel.add(geneExpressionDisplay.getDisplayPanel());
            middlePanel.add(new HTML("<br><br>"));
        }
        if (modeController.allowsAnomalyType(mirnaExpressionDisplay)) {
            middlePanel.add(mirnaExpressionDisplay.getDisplayPanel());
            middlePanel.add(new HTML("<br><br>"));
        }
        if (modeController.allowsAnomalyType(methylationDisplay)) {
            rightPanel.add(methylationDisplay.getDisplayPanel());
            rightPanel.add(new HTML("<br><br>"));
        }
        if (modeController.allowsAnomalyType(mutationDisplay)) {
            rightPanel.add(mutationDisplay.getDisplayPanel());
            rightPanel.add(new HTML("<br><br>"));
        }
        if (modeController.allowsAnomalyType(correlationDisplay)) {
            rightPanel.add(correlationDisplay.getDisplayPanel());
        }

        widgetPanel.add(leftPanel);
        widgetPanel.add(middlePanel);
        widgetPanel.add(rightPanel);
        filterPanel.add(searchPanel, DockPanel.SOUTH);
        filterPanel.add(widgetPanel, DockPanel.CENTER);
    }

    public void enableTooltips(boolean enable) {
        if (enable) {
            TooltipListener diseaseTT = makeTooltip(AnomalySearchConstants.TOOLTIPKEY_FILTER_DISEASE);
            diseaseTypeHtml.addMouseListener(diseaseTT);
            diseaseTypeHtml.addStyleName("action");
            TooltipListener genesTT = makeTooltip(AnomalySearchConstants.TOOLTIPKEY_FILTER_GENES);
            genesHtml.addMouseListener(genesTT);
            genesHtml.addStyleName("action");
            TooltipListener patientsTT = makeTooltip(AnomalySearchConstants.TOOLTIPKEY_FILTER_PATIENTS);
            patientsHtml.addMouseListener(patientsTT);
            patientsHtml.addStyleName("action");
            setWidgetTooltips(modeController.getModeName());
        } else {
            geneCopyNumberDisplay.clearTooltipText();
            mirnaCopyNumberDisplay.clearTooltipText();
            geneExpressionDisplay.clearTooltipText();
            mirnaExpressionDisplay.clearTooltipText();
            methylationDisplay.clearTooltipText();
            mutationDisplay.clearTooltipText();
            correlationDisplay.clearTooltipText();

            if (tooltips.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_PATIENTS) != null) {
                patientsHtml.removeMouseListener(tooltips.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_PATIENTS));
                tooltips.remove(AnomalySearchConstants.TOOLTIPKEY_FILTER_PATIENTS);
                patientsHtml.removeStyleName("action");
            }
            if (tooltips.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_DISEASE) != null) {
                diseaseTypeHtml.removeMouseListener(tooltips.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_DISEASE));
                tooltips.remove(AnomalySearchConstants.TOOLTIPKEY_FILTER_DISEASE);
                diseaseTypeHtml.removeStyleName("action");
            }
            if (tooltips.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_GENES) != null) {
                genesHtml.removeMouseListener(tooltips.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_GENES));
                tooltips.remove(AnomalySearchConstants.TOOLTIPKEY_FILTER_GENES);
                genesHtml.removeStyleName("action");
            }
        }
    }

    public void setKeepInSync(boolean b) {
        keepInSyncCB.setChecked(b);
    }

    private void constructDiseasePanel() {
        //keepInSyncCB.setChecked(true);
        //diseasePanel.add(keepInSyncCB);
        diseasePanel.add(diseaseTypeHtml);
        diseasePanel.add(diseaseTypeLb);
    }

    private TooltipListener makeTooltip(String s) {
        TooltipTextMap tt = TooltipTextMap.getInstance();
        if (tt.get(s) != null) {
            HTML text = new HTML(tt.get(s));
            TooltipListener ttl = new TooltipListener(text);
            tooltips.put(s, ttl);
            return ttl;
        }
        return null;
    }

    private void constructGeneLimitPanel() {
        geneLimitPanel.add(genesHtml);
        geneLimitPanel.add(allGenesRb);
        constructGeneRegionPanel();
        geneLimitPanel.add(geneRegionVPanel);
        constructGeneListPanel();
        geneLimitPanel.add(geneListPanel);
        WidgetHelper.setDomId(geneListClearButton, SeleniumTags.CLEARGENELIST_BUTTON);
    }

    private void constructGeneRegionPanel() {
        geneRegionHPanel.add(geneRegionRb);
        geneRegionHPanel.add(geneRegionAddButton);
        geneRegionVPanel.add(geneRegionHPanel);
    }

    private void constructGeneListPanel() {
        geneListPanel.add(geneListRb);
        geneListPanel.add(geneListTa);
        geneListPanel.add(geneListClearButton);
        geneListPanel.setCellHorizontalAlignment(geneListClearButton, HasHorizontalAlignment.ALIGN_RIGHT);
        WidgetHelper.setDomId(geneListTa, SeleniumTags.GENELIST_TEXT);
    }

    private void constructPatientLimitPanel() {
        allPatientsRb.setChecked(true);
        allGenesRb.setChecked(true);

        patientLimitPanel.add(patientsHtml);
        patientLimitPanel.add(allPatientsRb);
        patientLimitPanel.add(patientListRb);
        patientLimitPanel.add(patientListTa);
        patientLimitPanel.add(patientListClearButton);
        patientLimitPanel.setCellHorizontalAlignment(patientListClearButton, HasHorizontalAlignment.ALIGN_RIGHT);
    }

    private void constructSearchPanel() {
        searchPanel.add(searchButton);
        searchPanel.add(resetButton);
        searchPanel.setHeight("32px");
        WidgetHelper.setDomId(searchButton, SeleniumTags.SEARCH_BUTTON);
    }

    private void addStyles() {
        this.setStylePrimaryName(StyleConstants.FILTER_PANEL);

        diseaseTypeHtml.addStyleName(StyleConstants.HEADER);
        diseaseTypeHtml.addStyleName(StyleConstants.MARGIN_RIGHT_10PX);
        patientsHtml.addStyleName(StyleConstants.HEADER);
        genesHtml.addStyleName(StyleConstants.HEADER);
        genesHtml.setWidth("0");

        geneRegionAddButton.addStyleName(StyleConstants.ADD_BUTTON);
        geneRegionAddButton.addStyleName(StyleConstants.MARGIN_LEFT_5PX);
        geneListClearButton.addStyleName(StyleConstants.CLEAR_BUTTON);
        patientListClearButton.addStyleName(StyleConstants.CLEAR_BUTTON);
        resetButton.addStyleName(StyleConstants.MARGIN_LEFT_5PX);
        loadingImage.addStyleName(StyleConstants.MARGIN_LEFT_10PX);

        filterPanel.setSpacing(10);
        patientListTa.setCharacterWidth(30);
        patientListTa.setVisibleLines(4);
        patientListTa.addStyleName(StyleConstants.MARGIN_LEFT_10PX);
        geneListTa.setCharacterWidth(30);
        geneListTa.setVisibleLines(4);
        geneListTa.addStyleName(StyleConstants.MARGIN_LEFT_10PX);
    }

    private void setRegionsEnabled(boolean b) {
        for (int i = 0; i < geneRegionVPanel.getWidgetCount(); i++) {
            Widget w = geneRegionVPanel.getWidget(i);
            if (w instanceof RegionWidget) {
                RegionWidget rw = (RegionWidget) w;
                rw.setRegionEnabled(b);
            }
        }
    }

    protected void setUpClickListeners() {

        allGenesRb.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                setRegionsEnabled(false);
                if (!geneListTa.getText().equals("")) {
                    geneListTa.setEnabled(false);
                }
            }
        });

        geneRegionRb.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                setRegionsEnabled(true);
                if (!geneListTa.getText().equals("")) {
                    geneListTa.setEnabled(false);
                }
            }
        });

        geneListRb.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                setRegionsEnabled(false);
                geneListTa.setEnabled(true);
            }
        });

        geneRegionAddButton.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                geneRegionRb.setChecked(true);
                setRegionsEnabled(true);
                if (!geneListTa.getText().equals("")) {
                    geneListTa.setEnabled(false);
                }
                geneRegionVPanel.add(new RegionWidget());
            }
        });

        patientListClearButton.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                patientListTa.setText("");
                allPatientsRb.setChecked(true);
                patientListTa.setEnabled(true);
            }
        });

        allPatientsRb.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                if (!patientListTa.getText().equals("")) {
                    patientListTa.setEnabled(false);
                }
            }
        });

        patientListRb.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                patientListTa.setEnabled(true);
            }
        });

        patientListTa.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                patientListRb.setChecked(true);
            }
        });

        geneListClearButton.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                geneListTa.setText("");
                allGenesRb.setChecked(true);
                geneListTa.setEnabled(true);
            }
        });

        geneListTa.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                geneListRb.setChecked(true);
                setRegionsEnabled(false);
            }
        });

        diseaseTypeLb.addChangeListener(new ChangeListener() {
            public void onChange(Widget widget) {
                populateColTypes(getSelectedDisease(), null);
            }
        });

        addResetButtonListener(new ClickListener() {
            public void onClick(Widget sender) {
                resetPanel();
                createWidgets(colTypeResults);
            }
        });

        ClickListener searchListener = new ClickListener() {
            public void onClick(Widget sender) {
                if (geneListTa.getText().trim().length() == 0 && geneListRb.isChecked()) {
                    //empty gene list, set to all genes
                    allGenesRb.setChecked(true);
                }
                if (patientListTa.getText().trim().length() == 0 && patientListRb.isChecked()) {
                    //empty patient list, set to all patients
                    allPatientsRb.setChecked(true);
                }
                modeController.search();
            }
        };
        searchButton.addClickListener(searchListener);
    }

    public String addDiseaseTypes(List<Disease> diseases, final String selectedDisease) {
        int index = 0;
        for (Disease disease : diseases) {
            diseaseTypeLb.addItem(disease.getId() + " - " + disease.getName(), disease.getId());
            if ((disease.getId()).equalsIgnoreCase(selectedDisease)) {
                diseaseTypeLb.setSelectedIndex(index);
            }
            index++;
        }
        return diseaseTypeLb.getValue(diseaseTypeLb.getSelectedIndex());
    }

    public String getSelectedDisease() {
        return diseaseTypeLb.getValue(diseaseTypeLb.getSelectedIndex());
    }

    private void addResetButtonListener(ClickListener listener) {
        resetButton.addClickListener(listener);
    }

    public void startSearchSpinner() {
        searchPanel.add(loadingImage);
    }

    private String mergeLists(String origList, String newList) {
        if (origList.length() > 0) {
            origList += ", ";
        }
        origList += newList;
        origList = origList.replaceAll("\\s", "");
        String[] items = origList.split("[,;]");

        Set<String> itemSet = new TreeSet<String>();
        Collections.addAll(itemSet, items);
        Iterator itemIt = itemSet.iterator();
        String finalList = "";
        while (itemIt.hasNext()) {
            finalList += itemIt.next();
            if (itemIt.hasNext()) {
                finalList += ", ";
            }
        }
        return finalList;
    }

    public void addToGeneList(String genes) {
        geneListRb.setChecked(true);
        allGenesRb.setChecked(false);
        geneRegionRb.setChecked(false);
        String list = geneListTa.getText();
        String finalList = mergeLists(list, genes);
        geneListTa.setText(finalList);
    }

    public void addToPatientList(String patients) {
        patientListRb.setChecked(true);
        allPatientsRb.setChecked(false);
        String list = patientListTa.getText();
        String finalList = mergeLists(list, patients);
        patientListTa.setText(finalList);
    }

    public void copyFrom(FilterPanelI master) {
        final FilterPanel toCopy = (FilterPanel) master;
        // 1. set disease type, if needed
        keepInSyncCB.setChecked(toCopy.keepInSyncCB.isChecked());
        if (diseaseTypeLb.getSelectedIndex() != toCopy.diseaseTypeLb.getSelectedIndex()) {
            diseaseTypeLb.setSelectedIndex(toCopy.diseaseTypeLb.getSelectedIndex());
            populateColTypes(getSelectedDisease(), new InternalProcessCallback() {
                public void done() {
                    copyFromAfterPopulate(toCopy);
                }
            });
        } else {
            copyFromAfterPopulate(toCopy);
        }
    }

    private void copyFromAfterPopulate(FilterPanel toCopy) {
        // 2. copy gene values
        geneListTa.setText(toCopy.geneListTa.getText());
        geneListTa.setEnabled(toCopy.geneListTa.isEnabled());

        // first remove all region widgets
        for (int i = 0; i < geneRegionVPanel.getWidgetCount(); i++) {
            Widget w = geneRegionVPanel.getWidget(i);
            if (w instanceof RegionWidget) {
                geneRegionVPanel.remove(w);
            }
        }
        for (int i = 0; i < toCopy.geneRegionVPanel.getWidgetCount(); i++) {
            Widget w = toCopy.geneRegionVPanel.getWidget(i);
            if (w instanceof RegionWidget) {
                RegionWidget rw = (RegionWidget) w;
                RegionWidget rwCopy = new RegionWidget();
                rwCopy.copyFrom(rw);
                geneRegionVPanel.add(rwCopy);
                rwCopy.setRegionEnabled(toCopy.geneRegionRb.isChecked());
            }
        }

        if (toCopy.geneListRb.isChecked()) {
            geneListRb.setChecked(true);
        } else if (toCopy.geneRegionRb.isChecked()) {
            geneRegionRb.setChecked(true);
        } else if (toCopy.allGenesRb.isChecked()) {
            // All genes is selected
            allGenesRb.setChecked(true);
        }

        // 3. copy patient values
        patientListTa.setText(toCopy.patientListTa.getText());
        if (toCopy.patientListRb.isChecked()) {
            patientListRb.setChecked(true);
        } else if (toCopy.allPatientsRb.isChecked()) {
            allPatientsRb.setChecked(true);
        }

        // 4. copy widgets shown in each anomaly display area
        geneCopyNumberDisplay.copyFrom(toCopy.geneCopyNumberDisplay);
        mirnaCopyNumberDisplay.copyFrom(toCopy.mirnaCopyNumberDisplay);
        geneExpressionDisplay.copyFrom(toCopy.geneExpressionDisplay);
        mirnaExpressionDisplay.copyFrom(toCopy.mirnaExpressionDisplay);
        methylationDisplay.copyFrom(toCopy.methylationDisplay);
        mutationDisplay.copyFrom(toCopy.mutationDisplay);
        correlationDisplay.copyFrom(toCopy.correlationDisplay);

        enableTooltips(true);
    }

    public boolean keepInSync() {
        return keepInSyncCB.isChecked();
    }

    public void stopSearchSpinner() {
        searchPanel.remove(loadingImage);
    }

    private void clearWidgets() {
        geneRegionVPanel.clear();
        constructGeneRegionPanel();
        geneCopyNumberDisplay.clearPanel();
        mirnaCopyNumberDisplay.clearPanel();
        geneExpressionDisplay.clearPanel();
        mirnaExpressionDisplay.clearPanel();
        methylationDisplay.clearPanel();
        mutationDisplay.clearPanel();
        correlationDisplay.clearPanel();
    }

    private void createWidgets(List<ColumnType> colTypeResults) {
        geneCopyNumberDisplay.setColType(colTypeResults);
        geneCopyNumberDisplay.setHeadingHtml("Copy Number - Genes");
        geneCopyNumberDisplay.createAnomalyPanel();

        mirnaCopyNumberDisplay.setColType(colTypeResults);
        mirnaCopyNumberDisplay.setHeadingHtml("Copy Number - miRNAs");
        mirnaCopyNumberDisplay.createAnomalyPanel();

        geneExpressionDisplay.setColType(colTypeResults);
        geneExpressionDisplay.setHeadingHtml("Gene Expression");
        geneExpressionDisplay.createAnomalyPanel();

        mirnaExpressionDisplay.setColType(colTypeResults);
        mirnaExpressionDisplay.setHeadingHtml("miRNA Expression");
        mirnaExpressionDisplay.createAnomalyPanel();

        methylationDisplay.setColType(colTypeResults);
        methylationDisplay.setHeadingHtml("DNA Methylation");
        methylationDisplay.createAnomalyPanel();

        mutationDisplay.setColType(colTypeResults);
        mutationDisplay.setHeadingHtml("Validated Somatic Mutations");
        mutationDisplay.createAnomalyPanel();

        correlationDisplay.setColType(colTypeResults);
        correlationDisplay.setHeadingHtml("Correlations");
        correlationDisplay.createAnomalyPanel();
    }

    private void setWidgetTooltips(String mode) {
        if (areTooltipsEnabled()) {
            TooltipTextMap ttmap = TooltipTextMap.getInstance();
            if (mode.equals(ModeControllerGene.MODE_NAME)) {
                geneCopyNumberDisplay.setTooltipText(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_GENECN),
                        ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_GENECN + "_widget"));
                mirnaCopyNumberDisplay.setTooltipText(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_GENECNMIRNA),
                        ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_GENECNMIRNA + "_widget"));
                geneExpressionDisplay.setTooltipText(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_GENEEXP),
                        ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_GENEEXP + "_widget"));
                mirnaExpressionDisplay.setTooltipText(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_GENEEXPMIRNA),
                        ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_GENEEXPMIRNA + "_widget"));
                methylationDisplay.setTooltipText(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_GENEMETH),
                        ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_GENEMETH + "_widget"));
                mutationDisplay.setTooltipText(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_GENEMUT),
                        ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_GENEMUT + "_widget"));
                correlationDisplay.setTooltipText(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_GENECORRELATIONS),
                        ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_GENECORRELATIONS + "_widget"));

            } else if (mode.equals(ModeControllerPatient.MODE_NAME)) {
                geneCopyNumberDisplay.setTooltipText(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_PATIENTCN),
                        ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_PATIENTCN + "_widget"));
                mirnaCopyNumberDisplay.setTooltipText(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_PATIENTCNMIRNA),
                        ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_PATIENTCNMIRNA + "_widget"));
                geneExpressionDisplay.setTooltipText(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_PATIENTEXP),
                        ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_PATIENTEXP + "_widget"));
                mirnaExpressionDisplay.setTooltipText(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_PATIENTEXPMIRNA),
                        ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_PATIENTEXPMIRNA + "_widget"));
                methylationDisplay.setTooltipText(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_PATIENTMETH),
                        ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_PATIENTMETH + "_widget"));
                mutationDisplay.setTooltipText(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_PATIENTMUT),
                        ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_PATIENTMUT + "_widget"));
                correlationDisplay.setTooltipText(null, null);

            } else if (mode.equals(ModeControllerPathway.MODE_NAME)) {
                geneCopyNumberDisplay.setTooltipText(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_PATHWAYCN),
                        ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_PATHWAYCN + "_widget"));
                mirnaCopyNumberDisplay.setTooltipText(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_PATHWAYCNMIRNA),
                        ttmap.get(AnomalySearchConstants.TOOLTIPKEY_PATHWAYCNMIRNA + "_widget"));
                geneExpressionDisplay.setTooltipText(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_PATHWAYEXP),
                        ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_PATHWAYEXP));
                mirnaExpressionDisplay.setTooltipText(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_PATHWAYEXPMIRNA),
                        ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_PATHWAYEXPMIRNA + "_widget"));
                methylationDisplay.setTooltipText(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_PATHWAYMETH),
                        ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_PATHWAYMETH + "_widget"));
                mutationDisplay.setTooltipText(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_PATHWAYMUT),
                        ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_PATHWAYMUT + "_widget"));
                correlationDisplay.setTooltipText(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_PATHWAYCORRELATIONS),
                        ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_PATHWAYCORRELATIONS + "_widget"));
            }
        }
    }

    public FilterSpecifier makeFilterSpecifier() throws FilterPanelException {
        FilterSpecifier filter = new FilterSpecifier();

        filter.setListBy(getListBy());

        if (geneListRb.isChecked()) {
            filter.setGeneListOptions(FilterSpecifier.GeneListOptions.List);
            filter.setGeneList(geneListTa.getText());
        } else if (geneRegionRb.isChecked()) {
            filter.setGeneListOptions(FilterSpecifier.GeneListOptions.Region);
            addRegionsToFilter(filter);
        } else { //allGenesRb.isChecked()
            filter.setGeneListOptions(FilterSpecifier.GeneListOptions.All);
            filter.setGeneList(null);
        }

        if (patientListRb.isChecked()) {
            filter.setPatientListOptions(FilterSpecifier.PatientListOptions.List);
            filter.setPatientList(patientListTa.getText());
        } else {
            filter.setPatientListOptions(FilterSpecifier.PatientListOptions.All);
            filter.setPatientList(null);
        }

        filter.setColumnTypes(getChosenColTypes());
        filter.setDisease(getSelectedDisease());

        return filter;
    }

    //but empty is OK - means beginning or end of chromosome
    private void addRegionsToFilter(FilterSpecifier ret) {
        for (int i = 0; i < geneRegionVPanel.getWidgetCount(); i++) {
            Widget w = geneRegionVPanel.getWidget(i);
            if (w instanceof RegionWidget) {
                RegionWidget rw = (RegionWidget) w;
                if (rw.includeInFilter()) {
                    try {
                        long start = rw.getStart();
                        long stop = rw.getStop();
                        if (start != -1 && stop != -1 && start >= stop) {
                            throw new IllegalArgumentException("Chromosome stop must be greater than start");
                        }
                        ret.addChromRegion(new FilterChromRegion(rw.getChromosome(), rw.getStart(), rw.getStop()));
                    } catch (NumberFormatException ex) {
                        throw new IllegalArgumentException("Chromosome start and stop must be valid numbers");
                    }
                }
            }
        }
    }

    private List<ColumnType> getChosenColTypes() throws FilterPanelException {
        List<ColumnType> colTypes = new ArrayList<ColumnType>();
        //todo  There should be a simpler way to do this without all these if statements
        if (modeController.allowsAnomalyType(geneCopyNumberDisplay)) {
            for (AnomalyWidget nonMutationAnomalyWidget : geneCopyNumberDisplay.getSelectedWidgets()) {
                if (nonMutationAnomalyWidget.isPicked()) {
                    CopyNumberWidget copyNumberWidget = (CopyNumberWidget) nonMutationAnomalyWidget;
                    CopyNumberType cnColType = (CopyNumberType) copyNumberWidget.getColType();
                    cnColType.setPicked(copyNumberWidget.isPicked());
                    cnColType.setCalculationType(copyNumberWidget.getCalculationType());
                    cnColType.setLowerOperator(copyNumberWidget.getLowerOperator());
                    cnColType.setLowerLimit(copyNumberWidget.getLowerLimit());
                    cnColType.setUpperOperator(copyNumberWidget.getUpperOperator());
                    cnColType.setUpperLimit(copyNumberWidget.getUpperLimit());
                    if (copyNumberWidget.isDoGistic()) {
                        cnColType.setCalculationType(CopyNumberType.CalculationType.GISTIC);
                    } else {
                        cnColType.setFrequency(copyNumberWidget.getFrequency());
                        cnColType.setCalculationType(CopyNumberType.CalculationType.Regular);
                    }
                    colTypes.add(cnColType);
                }
            }
        }
        if (modeController.allowsAnomalyType(mirnaCopyNumberDisplay)) {
            for (AnomalyWidget nonMutationAnomalyWidget : mirnaCopyNumberDisplay.getSelectedWidgets()) {
                if (nonMutationAnomalyWidget.isPicked()) {
                    CopyNumberWidget copyNumberWidget = (CopyNumberWidget) nonMutationAnomalyWidget;
                    CopyNumberType cnColType = (CopyNumberType) copyNumberWidget.getColType();
                    cnColType.setPicked(copyNumberWidget.isPicked());
                    cnColType.setCalculationType(copyNumberWidget.getCalculationType());
                    cnColType.setLowerOperator(copyNumberWidget.getLowerOperator());
                    cnColType.setLowerLimit(copyNumberWidget.getLowerLimit());
                    cnColType.setUpperOperator(copyNumberWidget.getUpperOperator());
                    cnColType.setUpperLimit(copyNumberWidget.getUpperLimit());
                    if (copyNumberWidget.isDoGistic()) {
                        cnColType.setCalculationType(CopyNumberType.CalculationType.GISTIC);
                    } else {
                        cnColType.setFrequency(copyNumberWidget.getFrequency());
                        cnColType.setCalculationType(CopyNumberType.CalculationType.Regular);
                    }
                    colTypes.add(cnColType);
                }
            }
        }
        if (modeController.allowsAnomalyType(geneExpressionDisplay)) {
            for (AnomalyWidget expressionWidget : geneExpressionDisplay.getSelectedWidgets()) {
                if (expressionWidget.isPicked()) {
                    ExpressionType expressionColType = (ExpressionType) expressionWidget.getColType();
                    expressionColType.setPicked(expressionWidget.isPicked());
                    expressionColType.setLowerOperator(expressionWidget.getLowerOperator());
                    expressionColType.setLowerLimit(expressionWidget.getLowerLimit());
                    expressionColType.setUpperOperator(expressionWidget.getUpperOperator());
                    expressionColType.setUpperLimit(expressionWidget.getUpperLimit());
                    expressionColType.setFrequency(expressionWidget.getFrequency());
                    colTypes.add(expressionColType);
                }
            }
        }
        if (modeController.allowsAnomalyType(mirnaExpressionDisplay)) {
            for (AnomalyWidget mirnaWidget : mirnaExpressionDisplay.getSelectedWidgets()) {
                if (mirnaWidget.isPicked()) {
                    ExpressionType expressionColType = (ExpressionType) mirnaWidget.getColType();
                    expressionColType.setPicked(mirnaWidget.isPicked());
                    expressionColType.setLowerOperator(mirnaWidget.getLowerOperator());
                    expressionColType.setLowerLimit(mirnaWidget.getLowerLimit());
                    expressionColType.setUpperOperator(mirnaWidget.getUpperOperator());
                    expressionColType.setUpperLimit(mirnaWidget.getUpperLimit());
                    expressionColType.setFrequency(mirnaWidget.getFrequency());
                    colTypes.add(expressionColType);
                }
            }
        }
        if (modeController.allowsAnomalyType(methylationDisplay)) {
            for (AnomalyWidget mirnaWidget : methylationDisplay.getSelectedWidgets()) {
                if (mirnaWidget.isPicked()) {
                    MethylationType methylationType = (MethylationType) mirnaWidget.getColType();
                    methylationType.setPicked(mirnaWidget.isPicked());
                    methylationType.setUpperOperator(mirnaWidget.getUpperOperator());
                    methylationType.setUpperLimit(mirnaWidget.getUpperLimit());
                    methylationType.setFrequency(mirnaWidget.getFrequency());
                    colTypes.add(methylationType);
                }
            }
        }
        if (modeController.allowsAnomalyType(mutationDisplay)) {
            for (AnomalyWidget mutationWidget : mutationDisplay.getSelectedWidgets()) {
                if (mutationWidget.isPicked()) {
                    MutationType mutationColType = (MutationType) mutationWidget.getColType();
                    mutationColType.setPicked(mutationWidget.isPicked());
                    mutationColType.setCategory(((MutationWidget) mutationWidget).getCategory());
                    mutationColType.setFrequency(mutationWidget.getFrequency());
                    colTypes.add(mutationColType);
                }
            }
        }
        if (modeController.allowsAnomalyType(correlationDisplay)) {
            for (AnomalyWidget correlationWidget : correlationDisplay.getSelectedWidgets()) {
                if (correlationWidget.isPicked()) {
                    CorrelationType correlationColType = (CorrelationType) correlationWidget.getColType();
                    correlationColType.setPicked(correlationWidget.isPicked());
                    correlationColType.setAnomalyType1(correlationColType.getAnomalyType1());
                    correlationColType.setAnomalyType2(correlationColType.getAnomalyType2());
                    correlationColType.setPvalueLimit(correlationWidget.getFrequency());
                    correlationColType.setLowerOperator(correlationWidget.getLowerOperator());
                    correlationColType.setLowerLimit(correlationWidget.getLowerLimit());
                    correlationColType.setUpperOperator(correlationWidget.getUpperOperator());
                    correlationColType.setUpperLimit(correlationWidget.getUpperLimit());

                    colTypes.add(correlationColType);
                }
            }
        }

        return colTypes;
    }

    public void enableSearchButton() {
        if (!searchButton.isEnabled()) {
            searchButton.setEnabled(true);
        }
    }

    private void resetPanel() {
        clearWidgets();
        allGenesRb.setChecked(true);
        allPatientsRb.setChecked(true);
        geneListTa.setText("");
        geneListTa.setEnabled(true);
        patientListTa.setText("");
        patientListTa.setEnabled(true);
        filterError.removeFromParent();
    }

    public FilterSpecifier.ListBy getListBy() {
        return modeController.getListBy();
    }

    public void clearError() {
        filterError.setHTML("");
        filterError.removeFromParent();
    }

    public void setDiseasesEnable(boolean b) {
        diseaseTypeHtml = WidgetHelper.setHtmlTextEnable(diseaseTypeHtml, diseaseHeaderTextColorStyle, b);
        diseaseTypeLb.setEnabled(b);
    }

    public void setGeneListEnable(boolean b) {
        genesHtml = WidgetHelper.setHtmlTextEnable(genesHtml, geneHeaderTextColorStyle, b);
        allGenesRb.setEnabled(b);
        geneListRb.setEnabled(b);
        geneListTa.setEnabled(b);
        geneListClearButton.setEnabled(b);
        geneRegionRb.setEnabled(b);
        //geneRegionAddButton.setEnabled(b);
    }

    public void setPatientListEnable(boolean b) {
        patientsHtml = WidgetHelper.setHtmlTextEnable(patientsHtml, patientHeaderTextColorStyle, b);
        allPatientsRb.setEnabled(b);
        patientListRb.setEnabled(b);
        patientListTa.setEnabled(b);
        patientListClearButton.setEnabled(b);
    }

    public void setSearchButtonText(String buttonText) {
        searchButton.setText(buttonText);
    }

    public void setModeController(ModeController modeController) {
        this.modeController = modeController;
        searchButton.setText("Search " + modeController.getModeName());
    }

    public void searchStarted(Widget collapsedRepresentation) {
        filterPanel.setCollapsedRepresentation(collapsedRepresentation);
        closeablePanel.collapse();
    }

    public void collapse() {
        isCollapsed = true;
        if (filterError.getParent() != null) {
            filterError.removeFromParent();
            ((CellPanel) filterPanel.collapsed).add(filterError);
        }
    }

    public void expand() {
        isCollapsed = false;
        if (filterError.getParent() != null) {
            filterError.removeFromParent();
            searchPanel.add(filterError);
        }
    }

}

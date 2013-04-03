/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.results;

import com.google.gwt.user.client.ui.*;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.util.StyleConstants;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.util.TooltipListener;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.FilterSpecifier;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.Results;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.SortSpecifier;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.TooltipTextMap;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.pathway.SinglePathwayResults;

/**
 * Panel added as a results tab for displaying information about a single pathway.
 *
 * @author David Nassau
 *         Last updated by: $Author$
 * @version $Rev$
 */

public class PathwayDiagramPanel extends VerticalPanel implements ResultsPanelCanCopyToFilter, SortController {
    private SimplePanel tablePanel = new SimplePanel();
    private SinglePathwayResults results;
    private HTML copyToFilterLink;
    private VerticalPanel diagramPanel = new VerticalPanel();
    private RadioButton matchingGeneRadioButton = new RadioButton("showGenes", "Show Genes Matching Search");
    private RadioButton allPathwayGenesRadioButton = new RadioButton("showGenes", "Show All Pathway Genes");
    private HorizontalPanel radioButtonHPanel = new HorizontalPanel();
    private ResultsTable embeddedGeneTable;
    private ModeControllerPathway modeController;
    private SortOrderKeepTracker sortOrder = new SortOrderKeepTracker();

    public PathwayDiagramPanel(SinglePathwayResults results, ModeControllerPathway modeController) {
        this.results = results;
        if (results != null) {
            drawPanel();
            addShowGenesClickListeners();
        }
        this.modeController = modeController;
    }

    public String getPathwayId() {
        return results.getId();
    }

    private void drawPanel() {
        HTML pathwayName = new HTML(results.getDisplayName());
        Image pathwayDiagram = new Image(results.getImagePath());
        add(pathwayName);
        diagramPanel.add(pathwayDiagram);
        add(diagramPanel);
        pathwayName.addStyleName(StyleConstants.BIG_TEXT);
        pathwayName.addStyleName(StyleConstants.MARGIN_BOTTOM_5PX);
        pathwayDiagram.addStyleName(StyleConstants.MARGIN_BOTTOM_5PX);

        radioButtonHPanel.add(matchingGeneRadioButton);
        radioButtonHPanel.add(allPathwayGenesRadioButton);
        matchingGeneRadioButton.setChecked(true);
        add(radioButtonHPanel);

        embeddedGeneTable = createResultsTable(results, true);
        tablePanel.add(embeddedGeneTable);
        add(tablePanel);

        copyToFilterLink = new HTML("Copy Genes to Criteria");
        copyToFilterLink.addStyleName("action");
        String ttText = TooltipTextMap.getInstance().get("geneCopyCheckedToSearch");
        TooltipListener ttListener = new TooltipListener(new HTML(ttText));
        copyToFilterLink.addMouseListener(ttListener);
        add(copyToFilterLink);

        setSpacing(7);
    }

    public String getTabText() {
        return makeTitle(results.getDisplayName());
    }

    /**
     * Method that returns an identifier to be used as tab Id
     * @return Tab ID  
     */
    public String getTabId() {
        // The tab Id need not be unique here, since we do not have to show pivot columns
        // in cae of pathways, hence title is used a tab Id    
        return makeTitle(results.getDisplayName());
    }

    public static String makeTitle(String name) {
        if (name.length() > 16) {
            return name.substring(0, 16) + "src/test/java";
        }
        return name;
    }

    public void addCopyToFilterClickListener(ClickListener listener) {
        if (copyToFilterLink != null) {
            copyToFilterLink.addClickListener(listener);
        }
    }

    protected String makeListFromCheckedRows() {
        return embeddedGeneTable.makeListFromCheckedRows();
    }

    ResultsTableGene createResultsTable(Results results, Boolean filterExists) {
        return new ResultsTableGene(results, null, this, filterExists);
    }

    private void addShowGenesClickListeners() {
        matchingGeneRadioButton.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                tablePanel.remove(embeddedGeneTable);
                embeddedGeneTable = createResultsTable(results, true);
                tablePanel.add(embeddedGeneTable);
                //replace diagram WITH highlighting
                retrieveReplacementDiagram(true);
            }
        });

        allPathwayGenesRadioButton.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                tablePanel.remove(embeddedGeneTable);
                embeddedGeneTable = createResultsTable(results, false);
                tablePanel.add(embeddedGeneTable);
                //replace diagram WITHOUT highlighting
                retrieveReplacementDiagram(false);
            }
        });
    }

    private void retrieveReplacementDiagram(boolean highlight) {
        modeController.retrievePathwayIntoExistingTab(results.getId(), this, highlight);
    }

    void replacePathwayDiagram(SinglePathwayResults results) {
        diagramPanel.remove(0);
        diagramPanel.add(new Image(results.getImagePath()));
    }

    //from SortController interface
    public void sort(long columnId, String annotation, boolean initialAscending) {
        boolean ascending = doColumnAscending(columnId, annotation, initialAscending);
        SortSpecifier sortspec = new SortSpecifier(columnId, annotation, ascending);
        results.sort(sortspec);
        tablePanel.remove(embeddedGeneTable);
        boolean filter = matchingGeneRadioButton.isChecked();
        embeddedGeneTable = createResultsTable(results, filter);
        tablePanel.add(embeddedGeneTable);
    }

    private boolean doColumnAscending(long columnId, String annotation, boolean initialAscending) {
        return sortOrder.doColumnAscending(columnId, annotation, initialAscending);
    }

    //from SortController interface
    public int getCurrentSortOrderForColumn(long columnId, String annotation) {
        return sortOrder.getCurrentSortOrderForColumn(columnId, annotation);
    }

    public ResultsTable getResultsTable() {
        return null;
    }

    //return Genes, not pathways, because this is used for copying gene symbols to filter
    public FilterSpecifier.ListBy getListBy() {
        return FilterSpecifier.ListBy.Genes;
    }
}

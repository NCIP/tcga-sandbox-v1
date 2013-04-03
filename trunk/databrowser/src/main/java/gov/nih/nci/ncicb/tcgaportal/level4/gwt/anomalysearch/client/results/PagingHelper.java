/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.results;

import com.google.gwt.user.client.ui.*;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.util.SeleniumTags;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.util.StyleConstants;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.util.WidgetHelper;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.Results;

/**
 * Creates a "Results X of Y-Z" panel as well as an actual paging panel in the two panels passed to the constructor.
 * Is no longer a widget of its own; its panels just get added to the given panels.  This is to allow the Page info
 * and Row info to be split into different places on the application.
 *
 * @author Silpa Nanan
 *         Last updated by: $Author: whitmore $
 * @version $Rev: 9303 $
 */
public class PagingHelper {

    protected String currentRowDisplay;
    protected Label totalRowDisplay = new Label();
    protected HTML totalPagesHtml = new HTML();
    private TextBox currentPageTb = new TextBox();
    protected Button firstPageButton = new Button("|&lt;");
    protected Button previousPageButton = new Button("&lt;");
    protected Button nextPageButton = new Button("&gt;");
    protected Button lastPageButton = new Button("&gt;|");
    private int totalPages;

    public PagingHelper(CellPanel forPageInfo, CellPanel forRowInfo) {
        HorizontalPanel pagingControls = new HorizontalPanel();
        HTML pageHtml = new HTML("Page ");
        currentPageTb = WidgetHelper.getTextBoxWithValidator();
        currentPageTb.addStyleName(StyleConstants.TEXTBOX_WIDTH);

        //add style elements
        firstPageButton.addStyleName(StyleConstants.PAGING_BUTTON);
        previousPageButton.addStyleName(StyleConstants.PAGING_BUTTON);
        nextPageButton.addStyleName(StyleConstants.PAGING_BUTTON);
        lastPageButton.addStyleName(StyleConstants.PAGING_BUTTON);
        WidgetHelper.setDomId(lastPageButton, SeleniumTags.LASTPAGE_BUTTON);
        WidgetHelper.setDomId(nextPageButton, SeleniumTags.NEXTPAGE_BUTTON);
        WidgetHelper.setDomId(previousPageButton, SeleniumTags.PREVPAGE_BUTTON);
        WidgetHelper.setDomId(firstPageButton, SeleniumTags.FIRSTPAGE_BUTTON);
        totalRowDisplay.addStyleName("resultsInfoLabel");

        //add the buttons to the panel
        pagingControls.add(firstPageButton);
        pagingControls.add(previousPageButton);
        pagingControls.add(pageHtml);
        pagingControls.add(currentPageTb);
        pagingControls.add(totalPagesHtml);
        pagingControls.add(nextPageButton);
        pagingControls.add(lastPageButton);
        pagingControls.setSpacing(5);
        pagingControls.setCellVerticalAlignment(pageHtml, HasVerticalAlignment.ALIGN_MIDDLE);
        pagingControls.setCellVerticalAlignment(totalPagesHtml, HasVerticalAlignment.ALIGN_MIDDLE);

        forPageInfo.add(pagingControls);
        forRowInfo.add(totalRowDisplay);
        forPageInfo.setCellHorizontalAlignment(pagingControls, HasHorizontalAlignment.ALIGN_RIGHT);
        forRowInfo.setCellHorizontalAlignment(totalRowDisplay, HasHorizontalAlignment.ALIGN_RIGHT);
    }


    public void addFirstPageListener(ClickListener listener) {
        firstPageButton.addClickListener(listener);
    }

    public void addPreviousPageListener(ClickListener listener) {
        previousPageButton.addClickListener(listener);
    }

    public void addPageChangeListener(ChangeListener listener) {
        currentPageTb.addChangeListener(listener);
    }

    public void addNextPageListener(ClickListener listener) {
        nextPageButton.addClickListener(listener);
    }

    public void addLastPageListener(ClickListener listener) {
        lastPageButton.addClickListener(listener);
    }


    public int getCurrentPage() {
        int ret = -1;
        String txt = currentPageTb.getText();
        if (txt != null && txt.trim().length() > 0) {
            ret = Integer.parseInt(txt);
        }
        return ret;
    }

    public int getLastPage() {
        return totalPages;
    }

    public void updateCounts(Results results) {
        updateCounts(results, true);
    }

    public void updateCounts(Results results, boolean pageChanged) {
        if (pageChanged) {
            String currentPage = Integer.toString(results.getCurrentPage());
            currentPageTb.setText(currentPage);
        }
        StringBuilder buf = new StringBuilder();
        buf.append(" of ");
        if (!results.isFinalRowCount()) {
            buf.append(results.getGatheredPages()).append("+");
        } else {
            totalPages = results.getTotalPages();
            buf.append(totalPages);
        }
        totalPagesHtml.setHTML(buf.toString());

        if (results.getCurrentPage() == 1) {
            previousPageButton.setEnabled(false);
            firstPageButton.setEnabled(false);
        } else {
            previousPageButton.setEnabled(true);
            firstPageButton.setEnabled(true);
        }
        // use gathered pages, so next button does not appear until the next page is actually loaded
        if (results.getCurrentPage() == results.getGatheredPages()) {
            nextPageButton.setEnabled(false);
            lastPageButton.setEnabled(false);
        } else {
            nextPageButton.setEnabled(true);
            lastPageButton.setEnabled(true);
        }

        if (results.isFinalRowCount() && results.getCurrentPage() != results.getTotalPages()) {
            lastPageButton.setEnabled(true);
        } else {
            lastPageButton.setEnabled(false);
        }

        int currentFirstRow = results.getGatheredRows() == 0 ? 0 : (results.getCurrentPage() - 1) * results.getRowsPerPage() + 1;
        int currentLastRow;
        if (results.getCurrentPage() == results.getTotalPages()) {
            currentLastRow = results.getTotalRowCount();
        } else {
            currentLastRow = currentFirstRow + results.getRowsPerPage() - 1;
        }


        buf.setLength(0);
        if (pageChanged) {
            buf.append("Results ").append(currentFirstRow).append(" - ").append(currentLastRow);
            currentRowDisplay = buf.toString();
        } else {
            buf.append(currentRowDisplay);
        }
        buf.append(" of ");
        if (!results.isFinalRowCount()) {
            buf.append(results.getGatheredRows()).append("+");
        } else {
            buf.append(results.getTotalRowCount());
        }
        totalRowDisplay.setText(buf.toString());
    }
}

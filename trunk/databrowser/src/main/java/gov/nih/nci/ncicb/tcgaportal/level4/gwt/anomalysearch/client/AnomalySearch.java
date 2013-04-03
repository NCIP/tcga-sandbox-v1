package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.filter.FilterPanel;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.results.ModeController;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.results.ModeControllerGene;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.results.ModeControllerPathway;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.results.ModeControllerPatient;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.util.HelpHelper;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.util.StyleConstants;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.util.constants.DataBrowserConstants;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.FilterSpecifier;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 *
 * @author Silpa Nanan
 *         Last updated by: $Author: nichollsmc $
 * @version $Rev: 16207 $
 */

public class AnomalySearch implements EntryPoint, AnomalySearchFactory {
    public static final String SERVLET_PATH = "anomalysearch.AnomalySearch/AnomalySearchService";
    public static final int KEEPALIVE_MILLIS = 60000;

    public static DataBrowserConstants dataBrowserConstants = GWT.create(DataBrowserConstants.class);

    /*
    windowPanel contains headerImage and mainPanel
    mainPanel contains helpPanel and tabPanel
    helpPanel contains tooltipCheckbox and helpImage/helpMenu
    tabPanel contains geneSearch, patientSearch, and pathwaySearch search tabs
    each search tab contains a filter panel and references a mode controller
     */
    private AnomalySearchServiceAsync searchService;
    private boolean callBackCalled = false;
    private VerticalPanel windowPanel = new VerticalPanel();
    private VerticalPanel mainPanel = new VerticalPanel();
    private TabPanel tabPanel = new TabPanel();
    private ModeController geneMode = new ModeControllerGene();
    private ModeController patientMode = new ModeControllerPatient();
    private ModeController pathwayMode = new ModeControllerPathway();
    private SearchTab geneSearch, patientSearch, pathwaySearch;
    private static final int NUM_TABS = 3;
    private SearchTab selectedTab = null;
    private int selectedTabIndex = -1;
    private HelpHelper helpHelper = new HelpHelper();
    private HorizontalPanel helpPanel = new HorizontalPanel();
    private Image helpImage = new Image(FilterPanel.IMAGES_PATH + "help_transparent.gif");
    private PopupPanel helpMenu = new PopupPanel(true);
    private int geneTabIndex;
    private int patientTabIndex;
    private int pathwayTabIndex;
    private String initialMode = "";
    private String initialDisease = "GBM";

    public void onModuleLoad() {

        // get the mode and disease
        if (RootPanel.get("mode") != null) {
            initialMode = RootPanel.get("mode").getElement().getPropertyString("value");
        }

        if (RootPanel.get("disease") != null) {
            initialDisease = RootPanel.get("disease").getElement().getPropertyString("value");
        }

        createApplication();
    }

    public void createApplication() {
        searchService = GWT.create(AnomalySearchService.class);
        ((ServiceDefTarget) searchService).setServiceEntryPoint(GWT.getModuleBaseURL() + SERVLET_PATH);

        startKeepAlive();

        tabPanel.addTabListener(new TabListener() {
            public boolean onBeforeTabSelected(SourcesTabEvents sender, int tabIndex) {
                // if the "keep in sync" checkbox is checked, and tab is not already current selected, need to copy criteria
                if (selectedTab != null && selectedTabIndex != tabIndex) {
                    if (selectedTab.getFilterPanel().keepInSync()) {
                        ((SearchTab) tabPanel.getWidget(tabIndex)).getFilterPanel().copyFrom(selectedTab.getFilterPanel());
                    } else {
                        // if the current tab does NOT have "keep in sync" checked, then uncheck on the new tab also
                        ((SearchTab) tabPanel.getWidget(tabIndex)).getFilterPanel().setKeepInSync(false);
                    }

                }
                return true;
            }

            public void onTabSelected(SourcesTabEvents sender, int tabIndex) {
                selectedTab = (SearchTab) tabPanel.getWidget(tabIndex);
                selectedTabIndex = tabIndex;
            }
        });


        int tabIndex = 0;
        geneSearch = new SearchTab(geneMode);
        geneSearch.setAnomalySearchService(searchService);
        geneSearch.setAnomalySearchFactory(this);
        geneSearch.init(initialDisease);
        SimplePanel geneTab = new SimplePanel();
        geneTab.add(new HTML(ModeControllerGene.MODE_NAME));
        geneTab.addStyleName("tab");
        geneSearch.addStyleName("geneTab");
        geneTab.addStyleName("greenTab");
        tabPanel.add(geneSearch, geneTab);
        geneMode.setMainPanel(geneSearch.getMainResultsPanel());
        geneTabIndex = tabIndex;
        tabIndex++;

        patientSearch = new SearchTab(patientMode);
        patientSearch.setAnomalySearchService(searchService);
        patientSearch.setAnomalySearchFactory(this);
        patientSearch.init(initialDisease);
        SimplePanel patientTab = new SimplePanel();
        patientTab.add(new HTML(ModeControllerPatient.MODE_NAME));
        patientTab.addStyleName("tab");
        patientTab.addStyleName("blueTab");
        patientSearch.addStyleName("patientTab");
        tabPanel.add(patientSearch, patientTab);
        patientMode.setMainPanel(patientSearch.getMainResultsPanel());
        patientTabIndex = tabIndex;
        tabIndex++;

        pathwaySearch = new SearchTab(pathwayMode);
        pathwaySearch.setAnomalySearchService(searchService);
        pathwaySearch.setAnomalySearchFactory(this);
        pathwaySearch.init(initialDisease);
        SimplePanel pathwayTab = new SimplePanel();
        pathwayTab.add(new HTML(ModeControllerPathway.MODE_NAME));
        pathwayTab.addStyleName("tab");
        pathwayTab.addStyleName("purpleTab");
        pathwaySearch.addStyleName("pathwayTab");
        tabPanel.add(pathwaySearch, pathwayTab);
        pathwayMode.setMainPanel(pathwaySearch.getMainResultsPanel());
        pathwayTabIndex = tabIndex;
        tabIndex++;

        //the rest of the work will be done in the callback method.
        //That way we make sure it happens after the filter panel has loaded data from the server
    }

    private int doneCount = 0;

    public void filterPanelCallback() {
        doneCount++;

        if (doneCount == NUM_TABS) {

            //addHelp();
            windowPanel.add(mainPanel);
            mainPanel.add(helpPanel);
            mainPanel.setCellHorizontalAlignment(helpPanel, HasHorizontalAlignment.ALIGN_RIGHT);
            mainPanel.add(tabPanel);
            mainPanel.add(new HTML("<br>")); //some vertical room for tooltips

            geneMode.setAnomalySearchFactory(this);
            patientMode.setAnomalySearchFactory(this);
            pathwayMode.setAnomalySearchFactory(this);

            geneMode.setSearchService(searchService);
            patientMode.setSearchService(searchService);
            pathwayMode.setSearchService(searchService);
            helpHelper.setSearchService(searchService);

            //mainPanel.addStyleName(StyleConstants.MARGIN_LEFT_15PX);
            windowPanel.setCellHorizontalAlignment(mainPanel, HasHorizontalAlignment.ALIGN_LEFT);
            windowPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
            windowPanel.setWidth("100%");

            tabPanel.selectTab(0);
            RootPanel.get("as").add(windowPanel);

            tabPanel.selectTab(geneTabIndex);

            if (initialMode.equalsIgnoreCase(FilterSpecifier.ListBy.Genes.getStringValue()))
                tabPanel.selectTab(geneTabIndex);
            else if (initialMode.equalsIgnoreCase(FilterSpecifier.ListBy.Patients.getStringValue()))
                tabPanel.selectTab(patientTabIndex);
            else if (initialMode.equalsIgnoreCase(FilterSpecifier.ListBy.Pathways.getStringValue()))
                tabPanel.selectTab(pathwayTabIndex);

        }
    }

    private void addHelp() {
//        helpPanel.add(tooltipCheckbox);
        helpPanel.add(helpImage);
        helpImage.addStyleName(StyleConstants.HELP_ICON);

        VerticalPanel panel = new VerticalPanel();
        Anchor htmlAnchor = new Anchor("Help - HTML Version");
        htmlAnchor.setHref("http://tcga-data.nci.nih.gov/docs/help/TCGA_data_browser/wwhelp/wwhimpl/js/html/wwhelp.htm");
        htmlAnchor.setTarget("_blank");
        panel.add(htmlAnchor);
        
        Anchor pdfAnchor = new Anchor("Help - PDF Version");
        pdfAnchor.setHref("/tcga-portal/files/tcga_DataBrowser_UserGuide.pdf");
        pdfAnchor.setTarget("_blank");
        panel.add(pdfAnchor);
        helpMenu.add(panel);

        htmlAnchor.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                helpMenu.hide();
            }
        });
        
        pdfAnchor.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                helpMenu.hide();
            }
        });
        
        helpImage.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                helpMenu.setPopupPosition(helpImage.getAbsoluteLeft() - 150, helpImage.getAbsoluteTop() + helpImage.getOffsetHeight());
                helpMenu.setWidth("180px");
                helpMenu.show();
            }
        });

        //let's just always enable the tooltips
        geneSearch.getFilterPanel().enableTooltips(true);
        patientSearch.getFilterPanel().enableTooltips(true);
        pathwaySearch.getFilterPanel().enableTooltips(true);

//        tooltipCheckbox.addClickListener(new ClickListener() {
//                public void onClick(Widget sender) {
//                    geneSearch.getFilterPanel().enableTooltips(tooltipCheckbox.isChecked());
//                    patientSearch.getFilterPanel().enableTooltips(tooltipCheckbox.isChecked());
//                    pathwaySearch.getFilterPanel().enableTooltips(tooltipCheckbox.isChecked());
//                }
//        });
    }

    //todo  delete useless method
    public boolean areTooltipsEnabled() {
//        return tooltipCheckbox.isChecked();
        return true;
    }

    //starts keepalive thread which will keep pinging the server to prevent from timing out
    void startKeepAlive() {
        Timer t = new Timer() {
            public void run() {
                searchService.keepAlive(new AsyncCallback() {
                    public void onFailure(Throwable caught) {
                    }

                    public void onSuccess(Object result) {
                    }
                });
            }
        };
        t.scheduleRepeating(KEEPALIVE_MILLIS);
    }

}

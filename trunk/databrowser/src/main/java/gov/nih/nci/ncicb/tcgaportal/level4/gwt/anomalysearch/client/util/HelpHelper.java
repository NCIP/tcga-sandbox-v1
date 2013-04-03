/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.util;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.AnomalySearchServiceAsync;

/**
 * Helps to display help.
 * 
 * @author David Nassau
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class HelpHelper {

    String userGuideUrl, onlineHelpUrl;
    AnomalySearchServiceAsync searchService;

    public void setSearchService(AnomalySearchServiceAsync searchService) {
        this.searchService = searchService;
    }

    public void showUserGuide() {
        if (userGuideUrl == null) {
            getUserGuideUrl();
        } else {
            openUserGuide();
        }
    }

    public void showOnlineHelp() {
        if (onlineHelpUrl == null) {
            getOnlineHelpUrl();
        } else {
            openOnlineHelp();
        }
    }

    private void openOnlineHelp() {
        if (onlineHelpUrl != null) {
            Window.open(onlineHelpUrl, "Data_Browser_Online_Help", "menubar=no,toolbar=no,location=no,directories=no,personalbar=no,status=no,resizable=yes,scrollbars=yes,dependent=yes");
        }
    }

    private void getOnlineHelpUrl() {
        AsyncCallback<String> callback = new AsyncCallback<String>() {
            public void onFailure(Throwable caught) {
                caught.printStackTrace();
                //do nothing, link just won't work
            }

            public void onSuccess(String result) {
                onlineHelpUrl = result;
                openOnlineHelp();
            }
        };
        searchService.getOnlineHelpLocation(callback);

    }

    private void openUserGuide() {
        if (userGuideUrl != null) {
            Window.open(userGuideUrl, "Data_Browser_User_Guide", "menubar=no,toolbar=no,location=no,directories=no,personalbar=no,status=no,resizable=yes,scrollbars=yes,dependent=yes");
        }
    }

    private void getUserGuideUrl() {
        AsyncCallback<String> callback = new AsyncCallback<String>() {
            public void onFailure(Throwable caught) {
                caught.printStackTrace();
                //do nothing, link just won't work
            }

            public void onSuccess(String result) {
                userGuideUrl = result;
                openUserGuide();
            }
        };
        searchService.getUserGuideLocation(callback);
    }

}

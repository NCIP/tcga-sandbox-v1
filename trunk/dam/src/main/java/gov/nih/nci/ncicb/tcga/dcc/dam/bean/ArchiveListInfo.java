package gov.nih.nci.ncicb.tcga.dcc.dam.bean;

import java.util.List;

/**
 * Bean representing information needed for browsing archives.  Contains a list of ArchiveListLink beans
 * which hold the URLs and names of the links to drill down to the next level, plus a list of ArchiveListLinks
 * representing the hierarchy down to this page, and a ArchiveListLink representing the URL and name of the current display.
 *
 * @author chenjw
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ArchiveListInfo {
    private List<ArchiveListLink> pageLinks;
    private ArchiveListLink currentPage;
    private ArchiveListLink parentPage;

    public List<ArchiveListLink> getPageLinks() {
        return pageLinks;
    }

    public void setPageLinks(final List<ArchiveListLink> pageLinks) {
        this.pageLinks = pageLinks;
    }

    public void setCurrentPage(final ArchiveListLink currentPage) {
        this.currentPage = currentPage;
    }

    public ArchiveListLink getCurrentPage() {
        return currentPage;
    }

    public ArchiveListLink getParentPage() {
        return parentPage;
    }

    public void setParentPage(final ArchiveListLink parentPage) {
        this.parentPage = parentPage;
    }
}

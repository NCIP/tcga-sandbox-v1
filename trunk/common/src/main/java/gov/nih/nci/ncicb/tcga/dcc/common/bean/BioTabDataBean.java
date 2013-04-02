package gov.nih.nci.ncicb.tcga.dcc.common.bean;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Bean which stores bio tab data
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class BioTabDataBean {
    public Integer STRING_BUFFER_LENGTH = 8192;
    private BioTabData bioTabData;

    public BioTabDataBean() {
        bioTabData = new BioTabData();
    }

    public BioTabData getBioTabData() {
        return bioTabData;
    }

    public class BioTabData {
        final private List<String> columnHeaders;
        final private StringBuffer data;
        final private Map<String, String> bioTabFileNameByCenter;
        private String bioTabFileName;

        BioTabData() {
            columnHeaders = new ArrayList<String>();
            data = new StringBuffer();
            bioTabFileNameByCenter = new LinkedHashMap<String, String>();
        }

        public List<String> getColumnHeaders() {
            return columnHeaders;
        }


        public String getData() {
            return data.toString();
        }


        public void clearData() {
            data.delete(0, data.length());
        }

        public String getColumnHeadersAsString() {
            final StringBuilder columnHeaderStr = new StringBuilder();
            for (final String header : columnHeaders) {
                columnHeaderStr.append(header)
                        .append("\t");
            }
            columnHeaderStr.deleteCharAt(columnHeaderStr.length() - 1);
            return columnHeaderStr.toString();
        }

        public void addColumnHeaders(final List<String> additionalColumnHeaders) {
            if (additionalColumnHeaders.size() > 0) {
                columnHeaders.addAll(additionalColumnHeaders);
            }

        }

        public void addTabDelimitedData(final String bioTabData) {
            data.append(bioTabData)
                    .append("\t");
        }

        public void setEndOfLine() {
            if (data.toString().endsWith("\t")) {
                data.deleteCharAt(data.length() - 1);
            }
            data.append("\n");
        }

        public String getBioTabFileName(final String centerName) {
            return bioTabFileNameByCenter.get(centerName);
        }

        public void setBioTabFileName(final String centerName, final String fileName) {
            bioTabFileNameByCenter.put(centerName, fileName);
        }

        public List<String> getCenterNameList() {
            return new ArrayList(bioTabFileNameByCenter.keySet());
        }

        public boolean isBufferSpaceFull() {
            return (data.length() > STRING_BUFFER_LENGTH);
        }

        public String getBioTabFileName() {
            return bioTabFileName;
        }

        public void setBioTabFileName(String bioTabFileName) {
            this.bioTabFileName = bioTabFileName;
        }
    }

}

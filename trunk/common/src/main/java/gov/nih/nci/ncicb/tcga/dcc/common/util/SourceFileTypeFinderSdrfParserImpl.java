package gov.nih.nci.ncicb.tcga.dcc.common.util;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.FileInfo;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.FileInfoQueries;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Implementation of SourceFileTypeFinder interface that, given a file's id from the database, finds the
 * "source file type" for that file, if any by parsing the latest SDRF for that experiment to find the protocol
 * type used to generate the file.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class SourceFileTypeFinderSdrfParserImpl implements SourceFileTypeFinder {
    private FileInfoQueries fileInfoQueries;

    /**
     * Looks in the latest SDRF for the mage-tab archive corresponding to the latest archive containing this file.
     * Will return null if there is no latest SDRF, or of the file isn't part of any latest archives.
     *
     * @param fileId the ID of the file whose type you want
     * @return the source file type for this file, or null if none found
     * @throws java.io.IOException if there was an error reading the SDRF to get the source file type
     */
    @Override
    public String findSourceFileType(final long fileId) throws IOException {
        final FileInfo file = fileInfoQueries.getFileForFileId(fileId);

        if (file != null) {
            final String filename = file.getFileName();
            final Archive latestArchive = fileInfoQueries.getLatestArchiveContainingFile(file);
            if (latestArchive != null) {
                final String sdrfLocation = fileInfoQueries.getSdrfFilePathForExperiment(
                        latestArchive.getDomainName(),
                        latestArchive.getPlatform(),
                        latestArchive.getTumorType());

                if (sdrfLocation != null) {
                    // read the SDRF,
                    BufferedReader in = null;
                    try {
                        in = new BufferedReader(new FileReader(sdrfLocation));

                        final String headerLine = in.readLine();
                        final String[] headers = headerLine.split("\\t");
                        String line;
                        while ((line = in.readLine()) != null) {
                            if (line.contains(filename)) {
                                final String[] lineData = line.split("\\t");
                                for (int i = 0; i < lineData.length; i++) {
                                    if (lineData[i].equals(filename)) {
                                        for (int j = i - 1; j >= 0; j--) {
                                            if (headers[j].equals("Protocol REF")) {
                                                // found the right protocol so return the type as the source file type
                                                final ProtocolNameParser protocolNameParser = new ProtocolNameParser(lineData[j]);
                                                return protocolNameParser.getProtocolType();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } finally {
                        if (in != null) {
                            try {
                                in.close();
                            } catch (IOException e) {
                                // ignore
                            }
                        }
                    }
                }
            }
        }

        return null;

    }

    public void setFileInfoQueries(final FileInfoQueries fileInfoQueries) {
        this.fileInfoQueries = fileInfoQueries;
    }
}

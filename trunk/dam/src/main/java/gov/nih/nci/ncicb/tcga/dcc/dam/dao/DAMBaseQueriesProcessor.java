/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.BaseQueriesProcessor;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Allows Spring injection to determine whether to use threaded buffered writer, and buffer size
 * as well as fetch size
 *
 * @author David Nassau
 *         Last updated by: $Author: sfeirr $
 */
public class DAMBaseQueriesProcessor extends BaseQueriesProcessor {

    //default buffer size
    private static final int BUFFEREDWRITER_DEFAULTBUFFERSIZE = 8192;
    //true if using the ThreadedBufferedWriter instead of standard BufferedWriter
    //need to turn this on and off for testing
    protected boolean useThreadedBufferedWriter = true;
    //buffer size in buffered writer
    protected int bufferedWriterBufferSize = BUFFEREDWRITER_DEFAULTBUFFERSIZE;
    static final int DEFAULT_FETCHSIZE = 1000;

    public void setUseThreadedBufferedWriter( final boolean useThreadedBufferedWriter ) {
        this.useThreadedBufferedWriter = useThreadedBufferedWriter;
    }

    public void setBufferedWriterBufferSize( final int bufferedWriterBufferSize ) {
        this.bufferedWriterBufferSize = bufferedWriterBufferSize;
    }

    public enum Control {
        TRUE(1), FALSE(0);
        private final int value;
        Control(int value) {
            this.value = value;
        }

        public int value() {
            return value;
        }
    }

    private static final String SAMPLE_BATCH_MAP_SQL = "select distinct sample, batch_id " +
             "from shipped_biospecimen_breakdown";

    /**
     * Creates a map of sample barcode to batch number.
     *
     * @return a map where the key is the sample and the value is the batch number
     */
    protected Map<String, Integer> getBarcodeBatches() {
        final Map<String, Integer> barcodeToBatch = new HashMap<String, Integer>();
        getJdbcTemplate().query(SAMPLE_BATCH_MAP_SQL, new RowCallbackHandler() {
            public void processRow(final ResultSet resultSet) throws SQLException {
                final String sample = resultSet.getString(1);
                Integer batch = resultSet.getInt(2);
                if (resultSet.wasNull()) {
                    batch = null;
                }

                // if we already have a batch for this sample but it is null, replace it. or if we don't have it yet, set it.
                if ((barcodeToBatch.containsKey(sample) && barcodeToBatch.get(sample) == null && batch != null) ||
                        !barcodeToBatch.containsKey(sample)) {

                    barcodeToBatch.put(sample, batch);
                }
            }
        });
        return barcodeToBatch;
    }
}

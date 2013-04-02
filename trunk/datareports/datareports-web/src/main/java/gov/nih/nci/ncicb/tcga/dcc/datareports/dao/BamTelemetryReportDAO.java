package gov.nih.nci.ncicb.tcga.dcc.datareports.dao;

import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.BamTelemetry;

import javax.sql.DataSource;
import java.util.List;

/**
 * Bam telemetry report dao layer
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface BamTelemetryReportDAO {

    public void setDataSource(DataSource dataSource);

    /**
     * A query to get all of the rows in the BamTelemetry table
     *
     * @return a series of rows to populate the table
     */
    public List<BamTelemetry> getBamTelemetryRows();

}
